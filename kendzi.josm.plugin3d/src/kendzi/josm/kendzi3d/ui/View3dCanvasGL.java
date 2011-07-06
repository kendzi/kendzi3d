package kendzi.josm.kendzi3d.ui;

// based on TourCanvasGL.java by Andrew Davison

/* A single thread is spawned which initialises the rendering
 and then loops, carrying out update, render, sleep with
 a fixed period.

 The active rendering framework comes from chapter 2
 of "Killing Game Programming in Java" (KGPJ). There's a
 version online at http://fivedots.coe.psu.ac.th/~ad/jg/ch1/.

 The statistics code is lifted from chapter 3 of KGPJ (p.54-56), which
 is online at http://fivedots.coe.psu.ac.th/~ad/jg/ch02/.
 The time calculations in this version use System.nanoTime() rather
 than J3DTimer.getValue(), so require J2SE 5.0.

 The canvas displays a 3D world consisting of:

 * a green and blue checkerboard floor with a red square at its center
 and numbers along its z- and z- axes (as in the Java 3D
 Checkers3D example in chapter 15 of KGPJ;
 online at http://fivedots.coe.psu.ac.th/~ad/jg/ch8/).

 * a skybox of stars

 * a billboard showing a tree, which rotates around the y-axis
 to always face the camera

 * user navigation using keys to move forward, backwards, left,
 right, up, down, and turn left and right. The user cannot move
 off the checkboard or beyond the skybox

 * the user can quit the game by pressing 'q', ctrl-c, the 'esc' key,
 or by clicking the close box

 * several images are placed at random on the ground. The 'game'
 (such as it is) is to navigate over these shapes to make
 them disappear. Then the game ends. The shapes are managed
 by a GroundShapes object

 * a game-over image and message is placed as a 2D overlay in front
 of the game at the end

 * the application uses OpenGL bitmap fonts and Java fonts

 I borrowed the 2D overlay technique from ozak in his message at
 http://www.javagaming.org/forums/index.php?topic=8110.0

 A "Loading. Please wait..." message is displayed at start-up time.

 ------
 Changes (Feb 2007)
 - drawScreen() uses Texture.getImageTexCoords() values

 - drawSphere() rotation change to orientate sphere

 - drawStars() uses Texture.getImageTexCoords() values

 - recoding of drawAxisText() to use TextRenderer, and
 changes to labelAxes()

 - separated ground shapes code into a GroundShapes class
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.media.nativewindow.NativeWindow;
import javax.media.nativewindow.NativeWindowFactory;
import javax.media.nativewindow.awt.AWTGraphicsConfiguration;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.ground.Ground;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.ui.CameraMoveListener;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.ui.SimpleMoveAnimator;
import kendzi.josm.kendzi3d.service.FileUrlReciverService;
import kendzi.math.geometry.point.PointUtil;
import net.java.joglutils.model.ModelFactory;
import net.java.joglutils.model.ModelLoadException;
import net.java.joglutils.model.examples.DisplayListRenderer;
import net.java.joglutils.model.geometry.Model;

import org.apache.log4j.Logger;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

public class View3dCanvasGL extends Canvas implements Runnable {

    /** Log. */
    private static final Logger log = Logger.getLogger(View3dCanvasGL.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // for the floor
    private final static int FLOOR_LEN = 50; // should be even
    public final static int BLUE_TILE = 0; // floor tile colour types
    public final static int GREEN_TILE = 1;
    private final static float SCALE_FACTOR = 0.01f; // for the axis labels



    // statistics constants
    private static long MAX_STATS_INTERVAL = 1000000000L;
    // private static long MAX_STATS_INTERVAL = 1000L;
    // record stats every 1 second (roughly)

    private static final int NO_DELAYS_PER_YIELD = 16;
    /*
     * Number of renders with a sleep delay of 0 ms before the animation thread
     * yields to other running threads.
     */

    private static int MAX_RENDER_SKIPS = 5; // was 2;
    // no. of renders that can be skipped in any one animation loop
    // i.e the games state is updated but not rendered

    private static int NUM_FPS = 10;
    // number of FPS values stored to get an average

    // used for gathering statistics
    private long statsInterval = 0L; // in ns
    private long prevStatsTime;
    private long totalElapsedTime = 0L;
    private long gameStartTime;
    private int timeSpentInGame = 0; // in seconds

    private long frameCount = 0;
    private double fpsStore[];
    private long statsCount = 0;
    private double averageFPS = 0.0;

    private long rendersSkipped = 0L;
    private long totalRendersSkipped = 0L;
    private double upsStore[];
    private double averageUPS = 0.0;

    private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
    private DecimalFormat timedf = new DecimalFormat("0.####"); // 4 dp



    // used by the loading message (and axis labels and game-over msg)
    private Font font;
    private FontMetrics metrics;
    private BufferedImage waitIm;

    private View3dGLFrame tourTop; // reference back to top-level JFrame
    private long period; // period between drawing in _nanosecs_

    private Thread animator; // the thread that performs the animation
    private volatile boolean isRunning = false; // used to stop the animation
    // thread
    private volatile boolean isPaused = false;

    // OpenGL
    private GLDrawable drawable; // the rendering 'surface'
    private GLContext context; // the rendering context (holds rendering state
    // info)
    private GL2 gl;
    private GLU glu;
    private GLUT glut;

    private int starsDList; // display lists for stars
    private GLUquadric quadric; // for the sphere
    private Texture earthTex, starsTex, treeTex;

    private TextRenderer axisLabelRenderer; // for floor axis labels

    // sphere movement
    private float orbitAngle = 0.0f;
    private float spinAngle = 0.0f;

    // window sizing
    private boolean isResized = false;
    private int panelWidth, panelHeight;

    private DisplayListRenderer modelRenderer;
    private Model model2;

    private RenderJOSM renderJosm;

    private SimpleMoveAnimator simpleMoveAnimator = new SimpleMoveAnimator();

    CameraMoveListener cameraMoveListener = new CameraMoveListener(this.simpleMoveAnimator);

    private ModelRender modelRender;

    private Ground ground = new Ground();

    public View3dCanvasGL() {

    }

    public View3dCanvasGL(View3dGLFrame top, long period, int width, int height,
            AWTGraphicsConfiguration awtConfig, GLCapabilities caps) {
        // super(awtConfig);
        super(unwrap(awtConfig));

        this.tourTop = top;
        this.period = period;
        this.panelWidth = width;
        this.panelHeight = height;

        setBackground(Color.white);

        //		GLProfile profile = GLProfile.get(GLProfile.GL2);

        NativeWindow win = NativeWindowFactory.getNativeWindow(this,
                awtConfig);

        GLCapabilities glCaps = (GLCapabilities) win.getGraphicsConfiguration()
        .getNativeGraphicsConfiguration().getChosenCapabilities();


        //		drawable = GLDrawableFactory.getFactory(glCaps.getGLProfile()).createGLDrawable(win);
        this.drawable = GLDrawableFactory.getFactory(caps.getGLProfile()).createGLDrawable(win);

        // get a rendering surface and a context for this canvas
        // drawable = GLDrawableFactory.getFactory().getGLDrawable(this, caps,
        // null);
        this.context = this.drawable.createContext(null);

      // System.err.println(this.context.getGL().getGL2().glGetString( GL2.GL_VERSION ));

        initViewerPosn();

        addKeyListener(this.cameraMoveListener);

        addMouseMotionListener(this.cameraMoveListener);

        addMouseListener(this.cameraMoveListener);



        // create 'loading' message font (and axis labels and game-over msg)
        this.font = new Font("SansSerif", Font.BOLD, 24);
        this.metrics = this.getFontMetrics(this.font);
        this.waitIm = loadImage("hourglass.jpg");

        // axes labels renderer
        this.axisLabelRenderer = new TextRenderer(this.font);

        // statistics initialization
        this.fpsStore = new double[NUM_FPS];
        this.upsStore = new double[NUM_FPS];
        for (int i = 0; i < NUM_FPS; i++) {
            this.fpsStore[i] = 0.0;
            this.upsStore[i] = 0.0;
        }

        this.renderJosm = new RenderJOSM();
    } // end of TourCanvasGL()



    private static GraphicsConfiguration unwrap(AWTGraphicsConfiguration config) {
        if (config == null) {
            return null;
        }
        return config.getGraphicsConfiguration();
    }

    /**
     * Specify the camera (player) position, the x- and z- step distance, and
     * the position being looked at.
     */
    private void initViewerPosn() {
//        this.camera.init();
    } // end of initViewerPosn()

    /** Load image from directory "/images/".
     * @param pImageName file name
     * @return image
     */
    private BufferedImage loadImage(String pImageName) {
        BufferedImage im = null;
        try {
//            String fileName = "images/" + fnm;
//            ClassLoader source = ClassLoader.getSystemClassLoader();
//            URL res = source.getResource(fileName);
//            log.info(res);

//            log.info(org.openstreetmap.josm.gui.MainApplication.class.getClassLoader().getResource("images/wmsmenu.png"));

//            log.info(Kendzi3DPlugin.getPluginResource("/images/tree.gif"));



//            URL pluginResource = Kendzi3DPlugin.getPluginResource("/images/" + fnm);


            URL pluginResource = FileUrlReciverService.getResourceUrl("/images/" + pImageName);
            log.info(pluginResource);



            //			getPluginResource("/images/tree.gif");
            //				tex = TextureIO.newTexture(new File(fileName), false);
            //FIXME
            //			URL url = new URL();
            //			 MirroredInputStream is = new MirroredInputStream(fnm, new File(Main.pref.getPreferencesDir(),
            //             "images").toString());
            //			 im = Toolkit.getDefaultToolkit().createImage(is.getFile().toURI().toURL());
//            log.info(new File(fileName).toURI());
//            log.info(new File(fileName).toURI().toURL());

            //			 try {
            im = ImageIO.read(pluginResource);
            //			} catch (URISyntaxException e) {
            //				// TODO Auto-generated catch block
            //				e.printStackTrace();
            //			}
            //			im = ImageIO.read(getClass().getResource("images/" + fnm));
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Could not load image from images/" + pImageName);
        }
        return im;
    } // end of loadImage()

    @Override
    public void addNotify()
    // wait for the canvas to be added to the JPanel before starting
    {
        super.addNotify(); // creates the peer
        this.drawable.setRealized(true); // the canvas can now be rendering into

        // initialise and start the animation thread
        if (this.animator == null || !this.isRunning) {
            this.animator = new Thread(this);
            this.animator.start();
        }
    } // end of addNotify()

    // ------------- game life cycle methods ------------
    // called by the JFrame's window listener methods

    public void resumeGame()
    // called when the JFrame is activated / deiconified
    {
        this.isPaused = false;
    }

    public void pauseGame()
    // called when the JFrame is deactivated / iconified
    {
        this.isPaused = true;
    }

    public void stopGame()
    // called when the JFrame is closing
    {
        this.isRunning = false;
    }

    // ----------------------------------------------

    public void reshape(int w, int h)
    /*
     * called by the JFrame's ComponentListener when the window is resized
     * (similar to the reshape() callback in GLEventListener)
     */
    {
        this.isResized = true;
        if (h == 0)
        {
            h = 1; // to avoid division by 0 in aspect ratio in resizeView()
        }
        this.panelWidth = w;
        this.panelHeight = h;
    } // end of reshape()

    @Override
    public void update(Graphics g) {
    }

    @Override
    public void paint(Graphics g)
    // display a loading message while the canvas is being initialized
    {
        if (!this.isRunning) {
            String msg = "Loading. Please wait...";
            int x = (this.panelWidth - this.metrics.stringWidth(msg)) / 2;
            int y = (this.panelHeight - this.metrics.getHeight()) / 3;
            g.setColor(Color.blue);
            g.setFont(this.font);
            g.drawString(msg, x, y);

            // draw image under text
            int xIm = (this.panelWidth - this.waitIm.getWidth()) / 2;
            int yIm = y + 20;
            g.drawImage(this.waitIm, xIm, yIm, this);
        }
    } // end of paint()





    /** Set up camera position.
     * Warning this is 2d version!!!
     * it convert z to -z!!!
     * @deprecated
     *
     * @param pCamPosX x coordinate
     * @param pCamPosY y coordinate
     */
    @Deprecated
    public void setCamPos(double pCamPosX, double pCamPosY) {
        setCamPos(pCamPosX, Camera.CAM_HEIGHT, -pCamPosY);
    }

    /** Set up camera position.
     * @param pCamPosX x coordinate
     * @param pCamPosY y coordinate
     * @param pCamPosZ z coordinate
     */
    private void setCamPos(double pCamPosX, double pCamPosY, double pCamPosZ) {

        this.simpleMoveAnimator.setPoint(pCamPosX, pCamPosY, pCamPosZ);
    }





    @Override
    public void run()
    // initialize rendering and start frame generation
    {
        // makeContentCurrent();

        initRender();
        renderLoop();

        // discard the rendering context and exit
        // context.release();
        this.context.destroy();
        System.exit(0);
    } // end of run()

    private void makeContentCurrent()
    // make the rendering context current for this thread
    {
        try {
            while (this.context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
                log.info("Context not yet current...");
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } // end of makeContentCurrent()

    private void initRender()
    /*
     * rendering initialization (similar to the init() callback in
     * GLEventListener)
     */
    {
        makeContentCurrent();

        this.gl = this.context.getGL().getGL2();
        this.glu = new GLU();
        this.glut = new GLUT();



        log.error("INIT GL IS: " + this.gl.getClass().getName());

        log.error("Chosen GLCapabilities: " + this.drawable.getChosenGLCapabilities());


        //		gl.glEnable(GL2.GL_DEPTH_TEST);
        //		glut.glutInitDisplayMode (/*GL2.gl_GLUT_DEPTH */);
        //		gl.glutInitDisplayMode (/*GL2.gl_GLUT_DEPTH */);
        resizeView();

        //		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // black background
        this.gl.glClearDepth(1.0);
        this.gl.glClearColor(0.17f, 0.65f, 0.92f, 0.0f); // sky blue colour
        // background

        // z- (depth) buffer initialization for hidden surface removal
        //		gl.glEnable(GL2.GL_CULL_FACE);//**
        //		gl.glDepthFunc(GL2.GL_LESS);
        this.gl.glEnable(GL2.GL_DEPTH_TEST);
        //		gl.glDepthMask(true);//**

        //		IntBuffer depth = new IntBuffer();
        //
        //		gl.glGetIntegerv(GL2.GL_DEPTH_BITS, depth);

        int[] depth_bits = new int[1];
        this.gl.glGetIntegerv(GL2.GL_DEPTH_BITS, depth_bits, 0);

        log.error(depth_bits[0]);


        this.gl.glShadeModel(GL2.GL_SMOOTH); // use smooth shading

        // create a textured quadric
        this.quadric = this.glu.gluNewQuadric();
        this.glu.gluQuadricTexture(this.quadric, true); // creates texture coords


        addLight();


        //		// create a display list for drawing the stars
        //		starsDList = gl.glGenLists(1);
        //		gl.glNewList(starsDList, GL2.GL_COMPILE);
        //		drawStars();
        //		gl.glEndList();

        /*
         * release the context, otherwise the AWT lock on X11 will not be
         * released
         */
        this.context.release();

        String modelFilename = "models/teapot.obj";


        //		    if (modelFilename != null) {
        //		        try {
        //		          InputStream in = getClass().getClassLoader().getResourceAsStream(modelFilename);
        //		          if (in == null) {
        //		            throw new IOException("Unable to open model file " + modelFilename);
        //		          }
        //		          model = new ObjReader(in);
        //		          if (model.getVerticesPerFace() != 3) {
        //		            throw new IOException("Sorry, only triangle-based WaveFront OBJ files supported");
        //		          }
        //		          model.rescale(1.2f / model.getRadius());
        ////		          ++numModels;
        ////		          modelno = 5;
        //		        } catch (IOException e) {
        //		          e.printStackTrace();
        //		          System.exit(1);
        //		        }
        //		      }

        try
        {
            // Get an instance of the display list renderer a renderer
            this.modelRenderer = DisplayListRenderer.getInstance();

            // Turn on debugging
            this.modelRenderer.debug(true);

            // Call the factory for a model from a local file
            //model = ModelFactory.createModel("C:\\models\\apollo.3ds");

            // Call the factory for a model from a jar file
            //                model = ModelFactory.createModel("net/java/joglutils/model/examples/models/max3ds/apollo.3ds");
            this.model2 = ModelFactory.createModel("models/obj/penguin.obj");

            // When loading the model, adjust the center to the boundary center
            this.model2.centerModelOnPosition(true);

            this.model2.setUseTexture(true);

            // Render the bounding box of the entire model
            this.model2.setRenderModelBounds(false);

            // Render the bounding boxes for all of the objects of the model
            this.model2.setRenderObjectBounds(false);

            // Make the model unit size
            this.model2.setUnitizeSize(true);

            // Get the radius of the model to use for lighting and view presetting
            float radius = this.model2.getBounds().getRadius();

        }
        catch (ModelLoadException ex)
        {
            ex.printStackTrace();
        }


        this.modelRender = ModelRender.getInstance();


        this.renderJosm.init(this.gl);

    } // end of initRender()

    private void resizeView() {
        this.gl.glViewport(0, 0, this.panelWidth, this.panelHeight); // size of drawing area

        this.gl.glMatrixMode(GL2.GL_PROJECTION);
        this.gl.glLoadIdentity();
        this.glu.gluPerspective(45.0, (float) this.panelWidth / (float) this.panelHeight, 1.0, 1500.0); // 5
        // ,
        // 100
        // )
        // ;
        // fov, aspect ratio, near & far clipping planes
    } // end of resizeView()



    /**
     * Set up a point source with ambient, diffuse, and specular colour.
     * components
     */
    private void addLight() {
        this.gl.glMatrixMode(GL2.GL_MODELVIEW);
        // enable a single light source
        this.gl.glEnable(GL2.GL_LIGHTING);
        this.gl.glEnable(GL2.GL_LIGHT0);

        float[] grayLight = {0.5f, 0.5f, 0.5f, 1.0f }; // weak gray ambient
        this.gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, grayLight, 0);

        float[] whiteLight = { 1.0f, 1.0f, 1.0f, 1.0f }; // bright white diffuse
        // & specular
        this.gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, whiteLight, 0);
        this.gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, whiteLight, 0);

        //		float lightPos[] = { 1.0f, 1.0f, 1.0f, 0.0f }; // top right front
        float [] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        // _direction_
        this.gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);


//        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);

//        float [] lmodel_ambient = { 1f, 1f, 1f, 1.0f };
//        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
    } // end of addLight()

    // ---------------- frame-based rendering -----------------------

    /**
     * Repeatedly update, render, and sleep, keeping to a fixed period as
     * closely as possible. gather and report statistics.
     */
    private void renderLoop() {
        // timing-related variables
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;

        this.gameStartTime = System.nanoTime();
        this.prevStatsTime = this.gameStartTime;
        beforeTime = this.gameStartTime;

        this.isRunning = true;

        while (this.isRunning) {
            makeContentCurrent();
            gameUpdate();

            renderScene(); // rendering
            this.drawable.swapBuffers(); // put the scene onto the canvas
            // swap front and back buffers, making the new rendering visible

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (this.period - timeDiff) - overSleepTime;

            if (sleepTime > 0) { // some time left in this cycle
                try {
                    Thread.sleep(sleepTime / 1000000L); // nano -> ms
                } catch (InterruptedException ex) {
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            } else { // sleepTime <= 0; this cycle took longer than the period
                excess -= sleepTime; // store excess time value
                overSleepTime = 0L;

                if (++noDelays >= NO_DELAYS_PER_YIELD) {
                    Thread.yield(); // give another thread a chance to run
                    noDelays = 0;
                }
            }

            beforeTime = System.nanoTime(); // J3DTimer.getValue();

            /*
             * If the rendering is taking too long, update the game state
             * without rendering it, to get the updates/sec nearer to the
             * required FPS.
             */
            int skips = 0;
            while ((excess > this.period) && (skips < MAX_RENDER_SKIPS)) {
                excess -= this.period;
                gameUpdate(); // update state but don't render
                skips++;
            }
            this.rendersSkipped += skips;

            /*
             * release the context, otherwise the AWT lock on X11 will not be
             * released
             */
            this.context.release();

            storeStats();
        }

        printStats();

        this.glu.gluDeleteQuadric(this.quadric);
    } // end of renderLoop()

    private void gameUpdate() {
        if (!this.isPaused) {
            // update the earth's orbit and the R's
            this.orbitAngle = (this.orbitAngle + 2.0f) % 360.0f;
            this.spinAngle = (this.spinAngle + 1.0f) % 360.0f;

        }
    } // end of gameUpdate()



    // ------------------ rendering methods -----------------------------

    private void renderScene() {
        if (GLContext.getCurrent() == null) {
            log.error("Current context is null");
            System.exit(0);
        }

        if (this.isResized) {
            resizeView();
            this.isResized = false;
        }

        this.simpleMoveAnimator.updateState();
        String versionStr = gl.glGetString( GL2.GL_VERSION );
      System.out.println( "GL version:"+versionStr );
      //XXXX

        // clear colour and depth buffers
        this.gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        //		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        this.gl.glMatrixMode(GL2.GL_MODELVIEW);
        this.gl.glLoadIdentity();

        setCamera(this.glu);


        this.gl.glEnable(GL2.GL_MULTISAMPLE);

        drawGround( this.gl, this.simpleMoveAnimator, this.renderJosm.getPerspective());
        /*
         * log.info("Posn: (" + df.format(xCamPos) + ", " +
         * df.format(yCamPos) + ", " + df.format(zCamPos) + ")");
         */
        //        drawTree();

        //        drawSphere();



        if (this.modelRender.isDebugging()) {
            drawFloor(this.gl);
        }

        // execute display lists for drawing the stars
        this.gl.glCallList(this.starsDList);
        // drawStars();



        //		   gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        //		    gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        //		    gl.glVertexPointer(3, GL2.GL_FLOAT, 0, model.getVertices());
        //		    gl.glNormalPointer(GL2.GL_FLOAT, 0, model.getVertexNormals());
        //		    int[] indices = model.getFaceIndices();
        //		    gl.glDrawElements(GL2.GL_TRIANGLES, indices.length, GL2.GL_UNSIGNED_INT, IntBuffer.wrap(indices));
        //		    gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        //		    gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);



        // Render the model
        //        modelRenderer.render(gl, model2);


        this.renderJosm.draw(this.gl, this.simpleMoveAnimator);


        this.gl.glDisable(GL2.GL_MULTISAMPLE);

    } // end of renderScene()

    /**
     * Sets camera position and rotation.
     * @param pGlu GLU
     */
    private void setCamera(GLU pGlu) {

        Point3d pos = this.simpleMoveAnimator.getPoint();
        Vector3d posLookAt = new Vector3d(100, 0, 0);
        Vector3d rotate = this.simpleMoveAnimator.getAngle();

        posLookAt = PointUtil.rotateZ3d(posLookAt, rotate.z);
        posLookAt = PointUtil.rotateY3d(posLookAt, rotate.y);
//        posLookAt = PointUtil.rotateX3d(posLookAt, rotate.x);

        posLookAt.add(pos);

        pGlu.gluLookAt(pos.getX(), pos.getY(), pos.getZ(),
                posLookAt.getX(), posLookAt.getY(), posLookAt.getZ(),
                0, 1, 0);
    }

    public   void drawGround(GL2 gl , Camera camera, Perspective3D perspective3d ) {

        ground.draw(gl, camera, perspective3d);
    }

    private void drawTree()
    /*
     * the tree is a 'billboard': a screen that is always rotated around the
     * y-axis to be facing the camera.
     */
    {
        float[] verts = { 0, 0, 0, 2, 0, 0, 2, 2, 0, 0, 2, 0 }; // posn of tree
        this.gl.glPushMatrix();
        this.gl.glRotatef(-1 * ((float) this.simpleMoveAnimator.getAngle().y + 90.0f), 0, 1, 0);
        // rotate in the opposite direction to the camera
        drawScreen(verts, this.treeTex);
        this.gl.glPopMatrix();
    } // end of drawTree()

    private void drawScreen(float[] verts, Texture tex)
    /*
     * A screen is a transparent quadrilateral which only shows the
     * non-transparent parts of the texture. Lighting is disabled. The screen is
     * positioned according to the vertices in verts[].
     */
    {
        boolean enableLightsAtEnd = false;
        if (this.gl.glIsEnabled(GL2.GL_LIGHTING)) { // switch lights off if currently
            // on
            this.gl.glDisable(GL2.GL_LIGHTING);
            enableLightsAtEnd = true;
        }

        // do not draw the transparent parts of the texture
        this.gl.glEnable(GL2.GL_BLEND);
        this.gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        // don't show source alpha parts in the destination

        // determine which areas of the polygon are to be rendered
        this.gl.glEnable(GL2.GL_ALPHA_TEST);
        this.gl.glAlphaFunc(GL2.GL_GREATER, 0); // only render if alpha > 0


        /***/
        tex.setTexParameteri(GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        tex.setTexParameteri(GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);


        /***/

        // enable texturing
        this.gl.glEnable(GL2.GL_TEXTURE_2D);
        tex.bind();

        // replace the quad colours with the texture
        this.gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);

        TextureCoords tc = tex.getImageTexCoords();

        this.gl.glBegin(GL2.GL_QUADS);
        this.gl.glTexCoord2f(tc.left(), tc.bottom());
        this.gl.glVertex3f(verts[0], verts[1], verts[2]);

        this.gl.glTexCoord2f(tc.right(), tc.bottom());
        this.gl.glVertex3f(verts[3], verts[4], verts[5]);

        this.gl.glTexCoord2f(tc.right(), tc.top());
        this.gl.glVertex3f(verts[6], verts[7], verts[8]);

        this.gl.glTexCoord2f(tc.left(), tc.top());
        this.gl.glVertex3f(verts[9], verts[10], verts[11]);
        this.gl.glEnd();

        this.gl.glDisable(GL2.GL_TEXTURE_2D);

        // switch back to modulation of quad colours and texture
        this.gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        this.gl.glDisable(GL2.GL_ALPHA); // switch off transparency
        this.gl.glDisable(GL2.GL_BLEND);

        if (enableLightsAtEnd) {
            this.gl.glEnable(GL2.GL_LIGHTING);
        }
    } // end of drawScreen()

    private void drawSphere()
    // draw the earth orbiting a point, and rotating around its y-axis
    {
        // enable texturing and choose the 'earth' texture
        this.gl.glEnable(GL2.GL_TEXTURE_2D);
        this.earthTex.bind();

        // set how the sphere's surface responds to the light
        this.gl.glPushMatrix();
        float[] grayCol = { 0.8f, 0.8f, 0.8f, 1.0f };
        // float[] blueCol = {0.0f, 0.0f, 0.8f, 1.0f};
        this.gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, grayCol, 0);

        float[] whiteCol = { 1.0f, 1.0f, 1.0f, 1.0f };
        this.gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, whiteCol, 0);
        this.gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, 100);

        this.gl.glTranslatef(0.0f, 2.0f, -5.0f); // position the sphere
        this.gl.glRotatef(this.orbitAngle, 0.0f, 1.0f, 0.0f); // make it orbit around the
        // y-axis
        this.gl.glTranslatef(2.0f, 0.0f, 0.0f); // make the orbit x-axis radius 2
        // units

        this.gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        // rotate sphere upwards around x-axis so texture is correctly
        // orientated
        this.gl.glRotatef(this.spinAngle, 0.0f, 0.0f, 1.0f); // spin around z-axis (which
        // looks like y-axis)

        this.glu.gluSphere(this.quadric, 1.0f, 32, 32); // generate the textured sphere
        // radius, slices, stacks
        this.gl.glPopMatrix();

        this.gl.glDisable(GL2.GL_TEXTURE_2D);
    } // end of drawSphere()

    private void drawStars()
    /*
     * Draws a sky box using a stars image.
     *
     * Each 'wall' of the box extends the width of the floor (FLOOR_LEN) and is
     * FLOOR_LEN/2 high. The stars image is textured over the each wall twice,
     * each texture occupying FLOOR_LEN/2FLOOR_LEN/2 area.
     *
     * The ceiling is the same size as the floor, FLOOR_LENFLOOR_LEN, and is
     * covered by four copies of the stars image. Each texture occupies a
     * FLOOR_LEN/2FLOOR_LEN/2 area.
     */
    {
        this.gl.glDisable(GL2.GL_LIGHTING);

        // enable texturing and choose the 'stars' texture
        this.gl.glEnable(GL2.GL_TEXTURE_2D);
        this.starsTex.bind();
        // rTex.bind();

        TextureCoords tc = this.starsTex.getImageTexCoords();
        float left = tc.left();
        float right = tc.right();
        float bottom = tc.bottom();
        float top = tc.top();

        // replace the quad colours with the texture
        this.gl.glTexEnvf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);

        this.gl.glBegin(GL2.GL_QUADS);
        // back wall
        int edge = FLOOR_LEN / 2;
        this.gl.glTexCoord2f(left, bottom);
        this.gl.glVertex3i(-edge, 0, -edge);
        this.gl.glTexCoord2f(2 * right, bottom);
        this.gl.glVertex3i(edge, 0, -edge);
        this.gl.glTexCoord2f(2 * right, top);
        this.gl.glVertex3i(edge, edge, -edge);
        this.gl.glTexCoord2f(left, top);
        this.gl.glVertex3i(-edge, edge, -edge);

        // right wall
        this.gl.glTexCoord2f(left, bottom);
        this.gl.glVertex3i(edge, 0, -edge);
        this.gl.glTexCoord2f(2 * right, bottom);
        this.gl.glVertex3i(edge, 0, edge);
        this.gl.glTexCoord2f(2 * right, top);
        this.gl.glVertex3i(edge, edge, edge);
        this.gl.glTexCoord2f(left, top);
        this.gl.glVertex3i(edge, edge, -edge);

        // front wall
        this.gl.glTexCoord2f(left, bottom);
        this.gl.glVertex3i(edge, 0, edge);
        this.gl.glTexCoord2f(2 * right, bottom);
        this.gl.glVertex3i(-edge, 0, edge);
        this.gl.glTexCoord2f(2 * right, top);
        this.gl.glVertex3i(-edge, edge, edge);
        this.gl.glTexCoord2f(left, top);
        this.gl.glVertex3i(edge, edge, edge);

        // left wall
        this.gl.glTexCoord2f(left, bottom);
        this.gl.glVertex3i(-edge, 0, edge);
        this.gl.glTexCoord2f(2 * right, bottom);
        this.gl.glVertex3i(-edge, 0, -edge);
        this.gl.glTexCoord2f(2 * right, top);
        this.gl.glVertex3i(-edge, edge, -edge);
        this.gl.glTexCoord2f(left, top);
        this.gl.glVertex3i(-edge, edge, edge);

        // ceiling
        this.gl.glTexCoord2f(left, bottom);
        this.gl.glVertex3i(edge, edge, edge);
        this.gl.glTexCoord2f(2 * right, bottom);
        this.gl.glVertex3i(-edge, edge, edge);
        this.gl.glTexCoord2f(2 * right, 2 * top);
        this.gl.glVertex3i(-edge, edge, -edge);
        this.gl.glTexCoord2f(left, 2 * top);
        this.gl.glVertex3i(edge, edge, -edge);
        this.gl.glEnd();

        // switch back to modulation of quad colours and texture
        this.gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

        this.gl.glDisable(GL2.GL_TEXTURE_2D);
        this.gl.glEnable(GL2.GL_LIGHTING);
    } // end drawStars()

    // ---------------- finishing the game ------------------------


    private void begin2D()
    // switch to 2D viewing (an orthographic projection)
    {
        this.gl.glMatrixMode(GL2.GL_PROJECTION);
        this.gl.glPushMatrix(); // save projection settings
        this.gl.glLoadIdentity();
        this.gl.glOrtho(0.0f, this.panelWidth, this.panelHeight, 0.0f, -1.0f, 1.0f);
        // left, right, bottom, top, near, far

        /*
         * In an orthographic projection, the y-axis runs from the bottom-left,
         * upwards. This is reversed back to the more familiar top-left,
         * downwards, by switching the the top and bottom values in glOrtho().
         */
        this.gl.glMatrixMode(GL2.GL_MODELVIEW);
        this.gl.glPushMatrix(); // save model view settings
        this.gl.glLoadIdentity();
        this.gl.glDisable(GL2.GL_DEPTH_TEST);
    } // end of begin2D()

    private void end2D()
    // switch back to 3D viewing
    {
        this.gl.glEnable(GL2.GL_DEPTH_TEST);
        this.gl.glMatrixMode(GL2.GL_PROJECTION);
        this.gl.glPopMatrix(); // restore previous projection settings
        this.gl.glMatrixMode(GL2.GL_MODELVIEW);
        this.gl.glPopMatrix(); // restore previous model view settings
    } // end of end2D()



    // ------------------ the checkboard floor --------------------
    /*
     * This code is almost a direct translation of the CheckerFloor class in the
     * Java 3D Checkers3D example in chapter 15 of KGPJ; online at
     * http://fivedots.coe.psu.ac.th/~ad/jg/ch8/).
     */

    /*
     * Create tiles, the origin marker, then the axes labels. The tiles are in a
     * checkboard pattern, alternating between green and blue.
     */
    private void drawFloor(GL2 gl) {
        gl.glDisable(GL2.GL_LIGHTING);

        drawTiles(gl, BLUE_TILE); // blue tiles
        drawTiles(gl, GREEN_TILE); // green
        addOriginMarker();
        labelAxes(gl);

        gl.glEnable(GL2.GL_LIGHTING);
    } // end of CheckerFloor()

    @Deprecated
    public static void drawTiles(GL2 gl, int drawType)
    /*
     * Create a series of quads, all with the same colour. They are spaced out
     * over a FLOOR_LENFLOOR_LEN area, with the area centered at (0,0) on the XZ
     * plane, and y==0.
     */
    {
        if (drawType == BLUE_TILE) {
            gl.glColor3f(0.0f, 0.1f, 0.4f);
        } else {
            // green
            gl.glColor3f(0.0f, 0.5f, 0.1f);
        }

        gl.glBegin(GL2.GL_QUADS);
        boolean aBlueTile;
        for (int z = -FLOOR_LEN / 2; z <= (FLOOR_LEN / 2) - 1; z++) {
            aBlueTile = (z % 2 == 0) ? true : false; // set colour type for new
            // row
            for (int x = -FLOOR_LEN / 2; x <= (FLOOR_LEN / 2) - 1; x++) {
                if (aBlueTile && (drawType == BLUE_TILE)) {
                    // drawing blue
                    drawTile(gl, x, z);
                } else if (!aBlueTile && (drawType == GREEN_TILE)) {
                    drawTile(gl, x, z);
                }
                aBlueTile = !aBlueTile;
            }
        }
        gl.glEnd();
    } // end of drawTiles()

    public static  void drawTile(GL2 gl, int x, int z)
    /*
     * Coords for a single blue or green square; its top left hand corner at
     * (x,0,z).
     */
    {
        // points created in counter-clockwise order
        gl.glVertex3f(x, 0.0f, z + 1.0f); // bottom left point
        gl.glVertex3f(x + 1.0f, 0.0f, z + 1.0f);
        gl.glVertex3f(x + 1.0f, 0.0f, z);
        gl.glVertex3f(x, 0.0f, z);
    } // end of drawTile()

    private void addOriginMarker()
    /*
     * A red square centered at (0,0.01,0), of length 0.5, lieing flat on the XZ
     * plane.
     */
    {
        this.gl.glColor3f(0.8f, 0.4f, 0.3f); // medium red
        this.gl.glBegin(GL2.GL_QUADS);

        // points created counter-clockwise, a bit above the floor
        this.gl.glVertex3f(-0.25f, 0.01f, 0.25f); // bottom left point
        this.gl.glVertex3f(0.25f, 0.01f, 0.25f);
        this.gl.glVertex3f(0.25f, 0.01f, -0.25f);
        this.gl.glVertex3f(-0.25f, 0.01f, -0.25f);

        this.gl.glEnd();
    } // end of addOriginMarker();

    private void labelAxes(GL2 gl)
    // Place numbers along the x- and z-axes at the integer positions
    {
        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++)
        {
            drawAxisText(gl, "x: " + i, i, 0.0f, 0.0f); // along x-axis
        }

        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++)
        {
            drawAxisText(gl, "z: " + i, 0.0f, 0.0f, i); // along z-axis
        }

        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++)
        {
            drawAxisText(gl, "y: " + i, 0.0f, i, 0.0f ); // along y-axis
        }
    } // end of labelAxes()

    private void drawAxisText(GL2 gl, String txt, float x, float y, float z)
    /*
     * Draw txt at (x,y,z), with the text centered in the x-direction, facing
     * along the +z axis.
     */
    {
        Rectangle2D dim = this.axisLabelRenderer.getBounds(txt);
        float width = (float) dim.getWidth() * SCALE_FACTOR;

        this.axisLabelRenderer.begin3DRendering();
        this.axisLabelRenderer.draw3D(txt, x - width / 2, y, z, SCALE_FACTOR);
        this.axisLabelRenderer.end3DRendering();
    } // end of drawAxisText()

    // ----------------- statistics methods ------------------------

    /**
     * The statistics: - the summed periods for all the iterations in this
     * interval (period is the amount of time a single frame iteration should
     * take), the actual elapsed time in this interval, the error between these
     * two numbers;
     *
     * - the total frame count, which is the total number of calls to run();
     *
     * - the frames skipped in this interval, the total number of frames
     * skipped. A frame skip is a game update without a corresponding render;
     *
     * - the FPS (frames/sec) and UPS (updates/sec) for this interval, the
     * average FPS & UPS over the last NUM_FPSs intervals.
     *
     * The data is collected every MAX_STATS_INTERVAL (1 sec).
     */
    private void storeStats() {
        this.frameCount++;
        this.statsInterval += this.period;

        if (this.statsInterval >= MAX_STATS_INTERVAL) { // record stats every
            // MAX_STATS_INTERVAL
            long timeNow = System.nanoTime(); // J3DTimer.getValue();
            this.timeSpentInGame = (int) ((timeNow - this.gameStartTime) / 1000000000L); // ns
            // --
            // >
            // secs
            this.tourTop.setTimeSpent(this.timeSpentInGame);

            long realElapsedTime = timeNow - this.prevStatsTime; // time since last
            // stats collection
            this.totalElapsedTime += realElapsedTime;

            double timingError = ((double) (realElapsedTime - this.statsInterval) / this.statsInterval) * 100.0;

            this.totalRendersSkipped += this.rendersSkipped;

            double actualFPS = 0; // calculate the latest FPS and UPS
            double actualUPS = 0;
            if (this.totalElapsedTime > 0) {
                actualFPS = (((double) this.frameCount / this.totalElapsedTime) * 1000000000L);
                actualUPS = (((double) (this.frameCount + this.totalRendersSkipped) / this.totalElapsedTime) * 1000000000L);
            }

            // store the latest FPS and UPS
            this.fpsStore[(int) this.statsCount % NUM_FPS] = actualFPS;
            this.upsStore[(int) this.statsCount % NUM_FPS] = actualUPS;
            this.statsCount = this.statsCount + 1;

            double totalFPS = 0.0; // total the stored FPSs and UPSs
            double totalUPS = 0.0;
            for (int i = 0; i < NUM_FPS; i++) {
                totalFPS += this.fpsStore[i];
                totalUPS += this.upsStore[i];
            }

            if (this.statsCount < NUM_FPS) { // obtain the average FPS and UPS
                this.averageFPS = totalFPS / this.statsCount;
                this.averageUPS = totalUPS / this.statsCount;
            } else {
                this.averageFPS = totalFPS / NUM_FPS;
                this.averageUPS = totalUPS / NUM_FPS;
            }
            /*
             * log.info(timedf.format( (double)
             * statsInterval/1000000000L) + " " + timedf.format((double)
             * realElapsedTime/1000000000L) + "s " + df.format(timingError) +
             * "% " + frameCount + "c " + rendersSkipped + "/" +
             * totalRendersSkipped + " skip; " + df.format(actualFPS) + " " +
             * df.format(averageFPS) + " afps; " + df.format(actualUPS) + " " +
             * df.format(averageUPS) + " aups" );
             */
            this.rendersSkipped = 0;
            this.prevStatsTime = timeNow;
            this.statsInterval = 0L; // reset
        }
    } // end of storeStats()

    private void printStats() {
        // log.info("Frame Count/Loss: " + frameCount + " / " +
        // totalRendersSkipped);
        log.info("Average FPS: " + this.df.format(this.averageFPS));
        log.info("Average UPS: " + this.df.format(this.averageUPS));
        log.info("Time Spent: " + this.timeSpentInGame + " secs");
    } // end of printStats()


    public RenderJOSM getRenderJosm() {
        return this.renderJosm;
    }


}
