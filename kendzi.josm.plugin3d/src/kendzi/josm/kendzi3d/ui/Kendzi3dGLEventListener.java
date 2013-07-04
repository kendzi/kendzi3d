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
import javax.swing.JOptionPane;
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
import kendzi.josm.kendzi3d.jogl.selection.JosmEditorListener;
import kendzi.josm.kendzi3d.jogl.selection.ObjectSelectionManager;
import kendzi.josm.kendzi3d.jogl.selection.Selection;
import kendzi.josm.kendzi3d.jogl.selection.draw.SelectionDrawUtil;
import kendzi.josm.kendzi3d.jogl.skybox.SkyBox;
import kendzi.josm.kendzi3d.service.TextureCacheService;
import kendzi.josm.kendzi3d.service.TextureLibraryService;
import kendzi.josm.kendzi3d.ui.debug.AxisLabels;
import kendzi.josm.kendzi3d.ui.fps.FpsChangeEvent;
import kendzi.josm.kendzi3d.ui.fps.FpsListener;
import kendzi.math.geometry.point.PointUtil;
import kendzi.math.geometry.ray.Ray3d;

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



    Viewport viewport = new Viewport(1, 1);

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



    private ObjectSelectionManager objectSelectionListener;

    /**
     * Axis labels.
     */
    private AxisLabels axisLabels;

    private SelectionDrawUtil selectionDrawUtil;

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



    private Ray3d lastSelectRay = null;







//    private Point3d closestPointOnBaseRay;



    private CloseEvent closeEvent;



    private boolean error;





    /**
     * Default constructor.
     */
    public Kendzi3dGLEventListener() {

        setGroundType(false);

        this.axisLabels = new AxisLabels();

        this.simpleMoveAnimator = new SimpleMoveAnimator();

        this.cameraMoveListener = new CameraMoveListener(this.simpleMoveAnimator);

        this.skyBox = new SkyBox();

        this.selectionDrawUtil = new SelectionDrawUtil();

        initObjectSelectionListener();
    }


    private void initObjectSelectionListener() {

        this.objectSelectionListener = new ObjectSelectionManager() {

            @Override
            public Ray3d viewportPicking(int x, int y) {
                return Kendzi3dGLEventListener.this.viewport.picking(x, y);
            }

            @Override
            public Selection select(Ray3d selectRay) {
                return Kendzi3dGLEventListener.this.renderJosm.select(selectRay);
            }
        };

//        this.selectEditorListeners.add(this.objectSelectionListener);
//        this.objectSelectionListener.addEditorChangeListener(this);

        JosmEditorListener jel = new JosmEditorListener();
        this.objectSelectionListener.addEditorChangeListener(jel);
    }


    @Override
    public void display(GLAutoDrawable pDrawable) {

        if (this.error) {
            return;
        }

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

        // clear color and depth buffers
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        //      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();


        setCamera(glu, this.simpleMoveAnimator);


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



//        if (this.closestPointOnBaseRay != null) {
//            drawPoint(gl, this.closestPointOnBaseRay);
//        }

        selectionDrawUtil.draw(gl, this.objectSelectionListener);


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

        checkRequiredExtensions(gl);


        // Enable VSync
        gl.setSwapInterval(1);



        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        gl.glClearDepth(1.0);
        // sky blue color
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
        this.selectionDrawUtil.init(gl);




    }

    /** Check if all required openGl extensions are available.
     * @param gl
     * @return
     */
    private boolean checkRequiredExtensions(GL2 gl) {
        this.error = false;
        // String extensions = gl.glGetString(GL2.GL_EXTENSIONS);
        if (!gl.isExtensionAvailable("GL_ARB_multitexture")) {

            // JOptionPane.showMessageDialog(null,
            // "GL_ARB_vertex_buffer_object extension not available",
            // "Unavailable extension", JOptionPane.ERROR_MESSAGE);

            // Check if the extension ARB_multitexture is supported by the Graphic card
            JOptionPane.showMessageDialog(
                            null,
                            "GL_ARB_multitexture OpenGL extension is not supported. Install correct graphic drivers!",
                            "Extension not supported", JOptionPane.ERROR_MESSAGE);
            this.error = true;

            if (this.closeEvent != null) {
                this.closeEvent.closeAction();
            }
        }

        return this.error;
    }




    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!
            height = 1;
        }

        //this.viewportAspectRatio = (double) width / (double) height;

        this.viewport = new Viewport(width, height);


        gl.glViewport(0, 0, width, height); // size of drawing area

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(
                Viewport.PERSP_VIEW_ANGLE,
                this.viewport.viewportAspectRatio(),
                Viewport.PERSP_NEAR_CLIPPING_PLANE_DISTANCE, 1500.0); // 5

    }


    /** Register listener for camera move.
     * @param pCanvas canvas for listener
     */
    public void registerMoveListener(Canvas pCanvas) {
        pCanvas.addKeyListener(this.cameraMoveListener);

        pCanvas.addMouseMotionListener(this.cameraMoveListener);

        pCanvas.addMouseListener(this.cameraMoveListener);
    }

    /** Register listener for mouse selection.
     * @param pCanvas canvas for listener
     */
    public void registerMouseSelectionListener(Canvas pCanvas) {

        pCanvas.addMouseListener(this.objectSelectionListener);
        pCanvas.addMouseMotionListener(this.objectSelectionListener);
    }

    /**
     * Set up a point source with ambient, diffuse, and specular color.
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
     * @param pCamera
     */
    private void setCamera(GLU pGlu, Camera pCamera) {


        Point3d position = pCamera.getPoint();

        this.viewport.updateViewport(pCamera);

        Vector3d lookAt = this.viewport.getLookAt();
        Vector3d lookUp = this.viewport.getLookUp();

//        Vector3d lookAt = new Vector3d(100, 0, 0);
//        Vector3d lookUp = new Vector3d(0, 1, 0);
//
//        Vector3d rotate = pCamera.getAngle();
//
//        lookAt = PointUtil.rotateZ3d(lookAt, rotate.z);
//        lookAt = PointUtil.rotateY3d(lookAt, rotate.y);
////        posLookAt = PointUtil.rotateX3d(posLookAt, rotate.x);
//
//        lookUp = PointUtil.rotateZ3d(lookUp, rotate.z);
//        lookUp = PointUtil.rotateY3d(lookUp, rotate.y);
//
//        lookAt.add(position);


        pGlu.gluLookAt(position.x, position.y, position.z,
                lookAt.x, lookAt.y, lookAt.z,
                lookUp.x, lookUp.y, lookUp.z);



//        Vector3d view = new Vector3d();
//        Vector3d screenHoritzontally = new Vector3d();
//        Vector3d screenVertically = new Vector3d();
//
//        // look direction
//        view.sub(lookAt, position);
//        view.normalize();
//
//
//        // screenX
//        screenHoritzontally.cross(view, lookUp);
//        screenHoritzontally.normalize();
//
//        // screenY
//        screenVertically.cross(screenHoritzontally, view);
//        screenVertically.normalize();
//
//        final float radians = (float) (PERSP_VIEW_ANGLE * Math.PI / 180f);
//        float halfHeight = (float) (Math.tan(radians/2) * PERSP_NEAR_CLIPPING_PLANE_DISTANCE);
//        float halfScaledAspectRatio = (float) (halfHeight * this.viewportAspectRatio);
//
//        screenVertically.scale(halfHeight);
//        screenHoritzontally.scale(halfScaledAspectRatio);
    }







    private void onEditorChanged() {

    }





    static class Viewport {

        /**
         * Viev angle of camera (fovy).
         */
        public static final double PERSP_VIEW_ANGLE = 45;

        /**
         * The distance from the viewer to the near clipping plane. (zNear).
         */
        public static final double PERSP_NEAR_CLIPPING_PLANE_DISTANCE = 1d;

        private int width;
        private int height;

        private Point3d position;
        private Vector3d lookAt;
        private Vector3d lookUp;
        private Vector3d view;
        private Vector3d screenHoritzontally = new Vector3d();
        private Vector3d screenVertically = new Vector3d();



        public Viewport(int width, int height) {
            super();
            this.width = width;
            this.height = height;
        }


        public double viewportAspectRatio() {
            return (double) this.width / (double) this.height;
//
        }

        void updateViewport(Camera pCamera) {

            Point3d position = pCamera.getPoint();
            Vector3d lookAt = new Vector3d(100, 0, 0);
            Vector3d lookUp = new Vector3d(0, 1, 0);

            Vector3d rotate = pCamera.getAngle();

            lookAt = PointUtil.rotateZ3d(lookAt, rotate.z);
            lookAt = PointUtil.rotateY3d(lookAt, rotate.y);
//            posLookAt = PointUtil.rotateX3d(posLookAt, rotate.x);

            lookUp = PointUtil.rotateZ3d(lookUp, rotate.z);
            lookUp = PointUtil.rotateY3d(lookUp, rotate.y);

            lookAt.add(position);


            Vector3d view = new Vector3d();
            Vector3d screenHoritzontally = new Vector3d();
            Vector3d screenVertically = new Vector3d();

            // look direction
            view.sub(lookAt, position);
            view.normalize();


            // screenX
            screenHoritzontally.cross(view, lookUp);
            screenHoritzontally.normalize();

            // screenY
            screenVertically.cross(screenHoritzontally, view);
            screenVertically.normalize();

            final float radians = (float) (PERSP_VIEW_ANGLE * Math.PI / 180f);
            float halfHeight = (float) (Math.tan(radians/2) * PERSP_NEAR_CLIPPING_PLANE_DISTANCE);
            float halfScaledAspectRatio = (float) (halfHeight * this.viewportAspectRatio());

            screenVertically.scale(halfHeight);
            screenHoritzontally.scale(halfScaledAspectRatio);


            this.lookAt = lookAt;
            this.lookUp = lookUp;
            this.view = view;
            this.screenHoritzontally = screenHoritzontally;
            this.screenVertically = screenVertically;
            this.position = position;
        }

        public Ray3d picking(float screenX, float screenY) {

            Ray3d pickingRay = new Ray3d();

            double viewportWidth = this.width;
            double viewportHeight = this.height;

//            Camera camera = this.simpleMoveAnimator;


            Point3d point = new Point3d(this.position);
            point.add(this.view);

            pickingRay.getPoint().set(this.position);
            pickingRay.getPoint().add(this.view);

            screenX -= (float)viewportWidth/2f;
            screenY = (float)viewportHeight/2f - screenY;

            // normalize to 1
            screenX /= ((float)viewportWidth/2f);
            screenY /= ((float)viewportHeight/2f);

            pickingRay.getPoint().x += this.screenHoritzontally.x*screenX + this.screenVertically.x*screenY;
            pickingRay.getPoint().y += this.screenHoritzontally.y*screenX + this.screenVertically.y*screenY;
            pickingRay.getPoint().z += this.screenHoritzontally.z*screenX + this.screenVertically.z*screenY;

            pickingRay.getVector().set(pickingRay.getPoint());
            pickingRay.getVector().sub(this.position);

            return pickingRay;
        }


        /**
         * @return the width
         */
        public int getWidth() {
            return this.width;
        }


        /**
         * @param width the width to set
         */
        public void setWidth(int width) {
            this.width = width;
        }


        /**
         * @return the height
         */
        public int getHeight() {
            return this.height;
        }


        /**
         * @param height the height to set
         */
        public void setHeight(int height) {
            this.height = height;
        }


        /**
         * @return the lookAt
         */
        public Vector3d getLookAt() {
            return this.lookAt;
        }


        /**
         * @param lookAt the lookAt to set
         */
        public void setLookAt(Vector3d lookAt) {
            this.lookAt = lookAt;
        }


        /**
         * @return the lookUp
         */
        public Vector3d getLookUp() {
            return this.lookUp;
        }


        /**
         * @param lookUp the lookUp to set
         */
        public void setLookUp(Vector3d lookUp) {
            this.lookUp = lookUp;
        }

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




    public void closeEvent(CloseEvent closeEvent) {
        this.closeEvent = closeEvent;
    }


//    @Override
//    public void onEditorChange(EditorChangeEvent args) {
//        if (args instanceof ArrowEditorChangeEvent) {
//            this.closestPointOnBaseRay = ((ArrowEditorChangeEvent)args).getClosestPointOnBaseRay();
//        }
//    }
}
