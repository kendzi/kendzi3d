/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.ui;

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.DrawUtil;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.jogl.model.ground.Ground;
import kendzi.josm.kendzi3d.jogl.model.ground.StyledTitleGround;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.ui.CameraMoveListener;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.ui.SimpleMoveAnimator;
import kendzi.josm.kendzi3d.jogl.photos.CameraChangeEvent;
import kendzi.josm.kendzi3d.jogl.photos.CameraChangeListener;
import kendzi.josm.kendzi3d.jogl.photos.PhotoChangeEvent;
import kendzi.josm.kendzi3d.jogl.photos.PhotoRenderer;
import kendzi.josm.kendzi3d.jogl.skybox.SkyBox;
import kendzi.josm.kendzi3d.service.TextureCacheService;
import kendzi.josm.kendzi3d.service.TextureLibraryService;
import kendzi.josm.kendzi3d.ui.debug.AxisLabels;
import kendzi.josm.kendzi3d.ui.fps.FpsChangeEvent;
import kendzi.josm.kendzi3d.ui.fps.FpsListener;
import kendzi.math.geometry.point.PointUtil;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Draws 3d.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class Kendzi3dGLEventListener implements GLEventListener, CameraChangeListener {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Kendzi3dGLEventListener.class);

    /**
     * Model renderer.
     */
    @Inject
    private ModelRender modelRender;

    /**
     * Renderer of josm opengl object.
     */
    @Inject
    private RenderJOSM renderJosm;

    /**
     * Texture cache service.
     */
    @Inject
    private TextureCacheService textureCacheService;

    /**
     * Texture library service.
     */
    @Inject
    private TextureLibraryService textureLibraryService;

    /**
     * Position of sun. XXX
     */
    private float [] lightPos = new float[] { 0.0f, 1.0f, 1.0f, 0f };


    /**
     * Animator of camera movement.
     */
    private SimpleMoveAnimator simpleMoveAnimator;

    /**
     * Key and mouse listener for camera movement.
     */
    private CameraMoveListener cameraMoveListener;

    /**
     * Axis labels.
     */
    private AxisLabels axisLabels;



    /**
     * Ground.
     */
    private Ground ground;

    /**
     * Fps counter.
     */
    private int fpsCount = 0;

    /**
     * Fps last counter reset time.
     */
    private long fpsTimeStamp = 0;

    /**
     * Number of fps.
     */
    private int fps = 0;

    /**
     * Start time.
     */
    private long startTimeStamp = System.currentTimeMillis();


    /**
     * Time spend in render loop.
     */
    private long timeSpend;



    /**
     * Photos as layer in 3d.
     */
    @Inject
    private PhotoRenderer photoRenderer;


    private List<FpsListener> fpsChangeListenerList = new ArrayList<FpsListener>();

    @Inject
    SkyBox skyBox;


    /**
     * Default constructor.
     */
    public Kendzi3dGLEventListener() {

        setGroundType(false);

        this.axisLabels = new AxisLabels();

        this.simpleMoveAnimator = new SimpleMoveAnimator();

        this.cameraMoveListener = new CameraMoveListener(this.simpleMoveAnimator);

        this.skyBox = new SkyBox();
    }




    @Override
    public void display(GLAutoDrawable pDrawable) {

        countFps();

        this.simpleMoveAnimator.updateState();


        GL2 gl = pDrawable.getGL().getGL2();
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


        this.skyBox.draw(gl, this.simpleMoveAnimator, null);


        this.ground.draw(gl, this.simpleMoveAnimator, this.renderJosm.getPerspective());

        if (this.modelRender.isDebugging()) {

            this.axisLabels.draw(gl);

            drawFloor(gl);

            // drawTextInfo(gl, this.simpleMoveAnimator.info());
        }

        this.renderJosm.draw(gl, this.simpleMoveAnimator);

        if (this.photoRenderer.isEnabled()) {
              //  photo != null) {
            //FIXME

           this.photoRenderer.update(this.simpleMoveAnimator, this.renderJosm.getPerspective());




            this.photoRenderer.draw(gl, this.simpleMoveAnimator, this.renderJosm.getPerspective());


        }




        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }




    /**
     * Counts fps. Save last result to variable fps.
     */
    protected void countFps() {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - this.fpsTimeStamp > 1000) {
            this.fpsTimeStamp = timeMillis;
            this.fps = this.fpsCount;
            this.fpsCount = 0;

            this.timeSpend = (timeMillis - this.startTimeStamp) / 1000l;

            dispatchFpsChange(this.timeSpend, this.fps);
        }

        this.fpsCount++;
    }

    protected void dispatchFpsChange(long pTime, int pFps) {

        FpsChangeEvent fpsChangeEvent = new FpsChangeEvent(pFps, pTime);

        dispatchFpsChange(fpsChangeEvent);
    }

    @Override
    public void dispose(GLAutoDrawable pDrawable) {
       //
    }

    @Override
    public void init(GLAutoDrawable pDrawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL2 gl = pDrawable.getGL().getGL2();
        //System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);



        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        gl.glClearDepth(1.0);
        // sky blue colour
        gl.glClearColor(0.17f, 0.65f, 0.92f, 0.0f);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        int[] depth_bits = new int[1];
        gl.glGetIntegerv(GL2.GL_DEPTH_BITS, depth_bits, 0);

        gl.glShadeModel(GL2.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.

        addLight(gl);

        float[] grayCol = { 0.8f, 0.8f, 0.8f, 1.0f };
        // float[] blueCol = {0.0f, 0.0f, 0.8f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, grayCol, 0);


//        this.modelRender = ModelRender.getInstance();

        this.axisLabels.init();
        this.renderJosm.init(gl);



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


    /** Register listener for camera move.
     * @param pCanvas canvas for listener
     */
    public void registerMoveListener(Canvas pCanvas) {
        pCanvas.addKeyListener(this.cameraMoveListener);

        pCanvas.addMouseMotionListener(this.cameraMoveListener);

        pCanvas.addMouseListener(this.cameraMoveListener);
    }

    /**
     * Set up a point source with ambient, diffuse, and specular colour.
     * components
     * @param pGl gl
     */
    private void addLight(GL2 pGl) {

        pGl.glMatrixMode(GL2.GL_MODELVIEW);
        // enable a single light source
        pGl.glEnable(GL2.GL_LIGHTING);
        pGl.glEnable(GL2.GL_LIGHT0);


        float gray = 0.5f;
        float[] grayLight = {gray, gray, gray, 1.0f }; // weak gray ambient
        pGl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, grayLight, 0);

        float[] whiteLight = { 1.0f, 1.0f, 1.0f, 1.0f }; // bright white diffuse
        // & specular
        pGl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, whiteLight, 0);
        pGl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, whiteLight, 0);

        //      float lightPos[] = { 1.0f, 1.0f, 1.0f, 0.0f }; // top right front
        float [] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        // _direction_
        pGl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
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

        pGlu.gluLookAt(pos.x, pos.y, pos.z,
                posLookAt.x, posLookAt.y, posLookAt.z,
                0, 1, 0);
    }


    /**
     * @return the renderJosm
     */
    public RenderJOSM getRenderJosm() {
        return this.renderJosm;
    }

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
    public void setCamPos(double pCamPosX, double pCamPosY, double pCamPosZ) {

        this.simpleMoveAnimator.setPoint(pCamPosX, pCamPosY, pCamPosZ);
    }

    /**
     * @return the fps
     */
    public int getFps() {
        return this.fps;
    }

    private void drawFloor(GL2 gl) {
        gl.glDisable(GL2.GL_LIGHTING);

        //blue
        gl.glColor3f(0.0f, 0.1f, 0.4f);
        DrawUtil.drawTiles(gl, 50, true);
        // green
        gl.glColor3f(0.0f, 0.5f, 0.1f);
        DrawUtil.drawTiles(gl, 50, false);

        gl.glEnable(GL2.GL_LIGHTING);
    }

    /**
     * @param ground type of the ground to set
     */
    public void setGroundType(boolean pType) {
        if (pType) {
            this.ground = new StyledTitleGround(this.textureCacheService);
        } else {
            this.ground = new Ground(this.textureCacheService, this.textureLibraryService);
        }
    }


    @Override
    public void dispatchCameraChange(CameraChangeEvent pEvent) {


        if (pEvent instanceof PhotoChangeEvent) {

            PhotoChangeEvent pce = (PhotoChangeEvent) pEvent;
//            photo = pce.getPhoto();

            this.photoRenderer.setPhoto(pce.getPhoto());


            //XXX

        } else if (pEvent instanceof CameraChangeEvent) {
            //XXX
        }
    }




    /**
     * @param renderJosm the renderJosm to set
     */
    public void setRenderJosm(RenderJOSM renderJosm) {
        this.renderJosm = renderJosm;
    }




    /**
     * @return the modelRender
     */
    public ModelRender getModelRender() {
        return this.modelRender;
    }




    /**
     * @param modelRender the modelRender to set
     */
    public void setModelRender(ModelRender modelRender) {
        this.modelRender = modelRender;
    }




    /**
     * @return the photoRenderer
     */
    public PhotoRenderer getPhotoRenderer() {
        return this.photoRenderer;
    }




    /**
     * @param photoRenderer the photoRenderer to set
     */
    public void setPhotoRenderer(PhotoRenderer photoRenderer) {
        this.photoRenderer = photoRenderer;
    }



    public void dispatchFpsChange(FpsChangeEvent fpsChangeEvent) {
        for (FpsListener fl : this.fpsChangeListenerList) {
            fl.dispatchFpsChange(fpsChangeEvent);
        }
    }

    public void addFpsChangeListener(FpsListener pFpsListener) {
        this.fpsChangeListenerList.add(pFpsListener);
    }

    public void removeFpsChangeListener(FpsListener pFpsListener) {
        this.fpsChangeListenerList.remove(pFpsListener);
    }
}
