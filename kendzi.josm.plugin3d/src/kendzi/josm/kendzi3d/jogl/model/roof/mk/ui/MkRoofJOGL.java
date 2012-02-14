/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.ui;

//import com.sun.opengl.util.Animator;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.DrawUtil;
import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.model.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.math.geometry.Normal;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.point.PointUtil;

import org.apache.log4j.Logger;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 */
public class MkRoofJOGL implements GLEventListener {

    /** Log. */
    private static final Logger log = Logger.getLogger(MkRoofJOGL.class);


    /**
     * Position of sun. XXX
     */
    private float [] lightPos = new float[] { 0.0f, 1.0f, 1.0f, 0f };

    /**
     * Renderer of model.
     */
    private ModelRender modelRender;


    /** XXX
     * Font for axis.
     */
    private Font font = new Font("SansSerif", Font.BOLD, 24);

    /** XXX
     * For axis labels.
     */
    private TextRenderer axisLabelRenderer = new TextRenderer(this.font);

    /** XXX
     * For the axis labels.
     */
    private final static float SCALE_FACTOR = 0.01f;

    /**
     * XXX
     */
    private final static int FLOOR_LEN = 50;

    /**
     * Model of roof.
     */
    private Model model;

    private SimpleMoveAnimator simpleMoveAnimator = new SimpleMoveAnimator();

    CameraMoveListener cameraMoveListener = new CameraMoveListener(this.simpleMoveAnimator);


    private Model modelWall;

    public static void main(String[] args) {
        Frame frame = new Frame("Simple JOGL Application");



      //create a profile, in this case OpenGL 2 or later
        GLProfile profile = GLProfile.get(GLProfile.GL2);

        //configure context
        GLCapabilities capabilities = new GLCapabilities(profile);

        // setup z-buffer
        capabilities.setDepthBits(16);

        // for anti-aliasing
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(2);

        //initialize a GLDrawable of your choice
        GLCanvas canvas = new GLCanvas(capabilities);

//        GLCanvas canvas = new GLCanvas();



//        TextureCacheService.initTextureCache(".");
//        FileUrlReciverService.initFileReciver(".");

        MkRoofJOGL sj = new MkRoofJOGL();
        canvas.addGLEventListener(sj);
        frame.add(canvas);
        frame.setSize(640, 480);
        final Animator animator = new Animator(canvas);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });

        addSimpleMover(canvas, sj);

        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
        canvas.setFocusable(true);
        canvas.requestFocus();
    }

    private static void addSimpleMover(GLCanvas canvas, final MkRoofJOGL sj) {
        canvas.addKeyListener(sj.cameraMoveListener);

        canvas.addMouseMotionListener(sj.cameraMoveListener);

        canvas.addMouseListener(sj.cameraMoveListener);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL2 gl = drawable.getGL().getGL2();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        gl.glClearDepth(1.0);
        gl.glClearColor(0.17f, 0.65f, 0.92f, 0.0f); // sky blue colour

        gl.glEnable(GL2.GL_DEPTH_TEST);
        int[] depth_bits = new int[1];
        gl.glGetIntegerv(GL2.GL_DEPTH_BITS, depth_bits, 0);

        gl.glShadeModel(GL2.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.

        addLight(gl);

        float[] grayCol = { 0.8f, 0.8f, 0.8f, 1.0f };
        // float[] blueCol = {0.0f, 0.0f, 0.8f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, grayCol, 0);

        List<Point2d> border = new ArrayList<Point2d>();
//        border.add(new Point2d(0, 3));
//        border.add(new Point2d(0, -3));
//        border.add(new Point2d(3, 0));
//        border.add(new Point2d(5, 2));
        border.add(new Point2d(0, 3));
        border.add(new Point2d(0, -3));
        border.add(new Point2d(4, -1));
        border.add(new Point2d(5, 2));
//        border.add(new Point2d(0, 3));
//        border.add(new Point2d(0, -3));
//        border.add(new Point2d(3, 0));
//        border.add(new Point2d(5, 2));

        border.add(border.get(0));

        Point2d pStartPoint = border.get(0);
        String pKey = "2.3.aaa.aa";
        String dormer = "aa.aaa";
        List<Double> heights = new ArrayList<Double>();
        heights.add(2d);
        heights.add(1d);
        List<Double> sizeB = new ArrayList<Double>();
        double height = 5;

        RoofTextureData rtd = new RoofTextureData();
        rtd.setFacadeTextrure(new TextureData("/textures/building_facade_plaster.png", 4, 2));
        rtd.setRoofTexture(new TextureData("/textures/building_roof_material_roofTiles.png", 3, 3));

      //FIXME
        this.modelRender = null;//ModelRender.getInstance();

        //FIXME
//        RoofOutput output = DormerRoofBuilder.build(pStartPoint, border, pKey, dormer, height, null, rtd);
        //FIXME
//        this.model = output.getModel();
//        this.model.useLight = true;
////FIXME
//        this.modelWall = buildWalls(border, 0, height - output.getHeight());

    }

    private Model buildWalls( List<Point2d> border, double minHeight, double height) {
        double facadeTextureLenght = 4;
        double facadeTextureHeight = 2;


        boolean isCounterClockwise = false;
        if (0.0f < Triangulate.area(border)) {
              isCounterClockwise = true;
        }

        Material facadeMaterial =  MaterialFactory.createTextureMaterial("");

        ModelFactory modelBuilder = ModelFactory.modelBuilder();

//        int mat = modelBuilder.addMaterial(facadeMaterial);


        MeshFactory meshWalls = modelBuilder.addMesh("walls");

//        meshWalls.materialID = mat;
        meshWalls.hasTexture = false;


        if (border.size() > 0) {

            double vEnd = (int) (height / facadeTextureHeight);

            Point2d beginPoint = border.get(0);

            for (int i = 1; i < border.size(); i++) {

                Point2d endPoint = border.get(i);

                Vector3d norm = Normal.calcNormalNorm2(
                        beginPoint.getX(), 0.0f, beginPoint.getY(),
                        endPoint.getX(), 0.0f, endPoint.getY(),
                        beginPoint.getX(), 1.0, beginPoint.getY());

                if (isCounterClockwise) {
                    norm.negate();
                }

                int n = meshWalls.addNormal(norm);

                double distance = beginPoint.distance(endPoint);
                double uEnd = (int) (distance / facadeTextureLenght);

                int tc1 = meshWalls.addTextCoord(new TextCoord(0, 0));
                int tc2 = meshWalls.addTextCoord(new TextCoord(0, vEnd));
                int tc3 = meshWalls.addTextCoord(new TextCoord(uEnd, vEnd));
                int tc4 = meshWalls.addTextCoord(new TextCoord(uEnd, 0));

                int w1 = meshWalls.addVertex(new Point3d(beginPoint.getX(),  minHeight, -beginPoint.getY()));
                int w2 = meshWalls.addVertex(new Point3d(beginPoint.getX(), height, -beginPoint.getY()));
                int w3 = meshWalls.addVertex(new Point3d(endPoint.getX(), height, -endPoint.getY()));
                int w4 = meshWalls.addVertex(new Point3d(endPoint.getX(), minHeight, -endPoint.getY()));


                FaceFactory face = meshWalls.addFace(FaceType.QUADS);
                face.addVert(w1, tc1, n);
                face.addVert(w2, tc2, n);
                face.addVert(w3, tc3, n);
                face.addVert(w4, tc4, n);

                beginPoint = endPoint;
            }
        }

        Model model = modelBuilder.toModel();
        model.setUseLight(true);
        model.setUseTexture(true);

        return model;
    }



    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!

            height = 1;
        }
        final float h = (float) width / (float) height;
//        gl.glViewport(0, 0, width, height);
//        gl.glMatrixMode(GL2.GL_PROJECTION);
//        gl.glLoadIdentity();
//        glu.gluPerspective(45.0f, h, 1.0, 20.0);
//        gl.glMatrixMode(GL2.GL_MODELVIEW);
//        gl.glLoadIdentity();



        gl.glViewport(0, 0, width, height); // size of drawing area

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (float)width / (float) height, 1.0, 1500.0); // 5
    }

    @Override
    public void display(GLAutoDrawable drawable) {


        this.simpleMoveAnimator.updateState();


        GL2 gl = drawable.getGL().getGL2();
       // System.err.println("INIT GL IS: " + gl.getClass().getName());

        GLU glu = new GLU();

     // _direction_
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, this.lightPos, 0);


//        // Clear the drawing area
//        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
//
//
//
//
//        // Reset the current matrix to the "identity"
//        gl.glLoadIdentity();
//
//        gl.glMatrixMode(GL2.GL_MODELVIEW);
//        gl.glLoadIdentity();

        // clear colour and depth buffers
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        //      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();


        setCamera(glu);


        gl.glEnable(GL2.GL_MULTISAMPLE);

        if (this.model != null) {
            this.modelRender.render(gl, this.model);
        }

        if (this.modelWall != null) {
            this.modelRender.render(gl, this.modelWall);
        }

//        String versionStr = gl.glGetString( GL2.GL_VERSION );
//        log.info( "GL version:"+versionStr );

        drawFloor(gl);

//        drawTextInfo(gl, this.simpleMoveAnimator.info());

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    private void drawFloor(GL2 gl) {
       // gl.glDisable(GL2.GL_LIGHTING);

        //blue
        gl.glColor3f(0.0f, 0.1f, 0.4f);
        DrawUtil.drawTiles(gl, 50, true);
        // green
        gl.glColor3f(0.0f, 0.5f, 0.1f);
        DrawUtil.drawTiles(gl, 50, false);


        labelAxes(gl);

       // gl.glEnable(GL2.GL_LIGHTING);
    }
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

    @Override
    public void dispose(GLAutoDrawable drawable){}

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    /**
     * Set up a point source with ambient, diffuse, and specular colour.
     * components
     */
    private void addLight(GL2 gl) {
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        // enable a single light source
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);


        float gray = 0.5f;
        float[] grayLight = {gray, gray, gray, 1.0f }; // weak gray ambient
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, grayLight, 0);

        float[] whiteLight = { 1.0f, 1.0f, 1.0f, 1.0f }; // bright white diffuse
        // & specular
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, whiteLight, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, whiteLight, 0);

        //      float lightPos[] = { 1.0f, 1.0f, 1.0f, 0.0f }; // top right front
        float [] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        // _direction_
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);


//        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);

//        float [] lmodel_ambient = { 1f, 1f, 1f, 1.0f };
//        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
    }





    /**
     * The game-over message is an overlay that always stays at the front,
     * 'stuck' to the screen. The message consists of three elements: a robot
     * image with transparent elements, a red rectangle, and a text message in
     * the rectangle which shows the player's final score.
     *
     * I borrowed this 2D overlay technique from ozak in his message at
     * http://www.javagaming.org/forums/index.php?topic=8110.0
     */
    private void drawTextInfo(GL2 gl, String msg) {
        GLUT glut = new GLUT();

        gl.glDisable(GL2.GL_LIGHTING);

        int msgWidth = glut.glutBitmapLength(GLUT.BITMAP_TIMES_ROMAN_10, msg);
        // use a bitmap font (since no scaling required)
        // get (x,y) for centering the text on screen
        int x = 100;
        int y = 100;

        begin2D(gl); // switch to 2D viewing


        // write the message in the center of the screen
        gl.glColor3f(1.0f, 1.0f, 1.0f); // white text
        gl.glRasterPos2i(x, y);


        String [] split = msg.split("\\n");

        int i = 0;
        for (String msgLine : split) {
            gl.glRasterPos2i(x, y + (i * 20));
            glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, msgLine);

            i++;
        }

        end2D(gl); // switch back to 3D viewing

        gl.glEnable(GL2.GL_LIGHTING);
    }

    /**
     * Switch to 2D viewing (an orthographic projection).
     * @param gl
     */
    private void begin2D(GL2 gl) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix(); // save projection settings
        gl.glLoadIdentity();
        double panelWidth = 800;
        double panelHeight = 800;
        gl.glOrtho(0.0f, panelWidth, panelHeight, 0.0f, -1.0f, 1.0f);
        // left, right, bottom, top, near, far

        /*
         * In an orthographic projection, the y-axis runs from the bottom-left,
         * upwards. This is reversed back to the more familiar top-left,
         * downwards, by switching the the top and bottom values in glOrtho().
         */
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix(); // save model view settings
        gl.glLoadIdentity();
        gl.glDisable(GL2.GL_DEPTH_TEST);
    }

    /**
     * switch back to 3D viewing.
     * @param gl
     */
    private void end2D(GL2 gl) {
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix(); // restore previous projection settings
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix(); // restore previous model view settings
    }

    /**
     * Place numbers along the x- and z-axes at the integer positions.
     *
     * @param gl
     */
    private void labelAxes(GL2 gl) {
        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            drawAxisText(gl, "x: " + i, i, 0.0f, 0.0f); // along x-axis
        }

        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            drawAxisText(gl, "z: " + i, 0.0f, 0.0f, i); // along z-axis
        }

        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            drawAxisText(gl, "y: " + i, 0.0f, i, 0.0f); // along y-axis
        }
    }

    /**
     * Draw txt at (x,y,z), with the text centered in the x-direction, facing
     * along the +z axis.
     *
     * @param gl
     * @param txt
     * @param x
     * @param y
     * @param z
     */
    private void drawAxisText(GL2 gl, String txt, float x, float y, float z) {

        Rectangle2D dim = this.axisLabelRenderer.getBounds(txt);
        float width = (float) dim.getWidth() * SCALE_FACTOR;

        this.axisLabelRenderer.begin3DRendering();
        this.axisLabelRenderer.draw3D(txt, x - width / 2, y, z, SCALE_FACTOR);
        this.axisLabelRenderer.end3DRendering();
    }
}