/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.ui;

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.camera.CameraMoveListener;
import kendzi.jogl.camera.SimpleMoveAnimator;
import kendzi.jogl.camera.Viewport;
import kendzi.jogl.camera.ViewportUtil;
import kendzi.jogl.drawer.AxisLabels;
import kendzi.jogl.drawer.TilesSurface;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.jogl.compas.CompassDrawer;
import kendzi.josm.kendzi3d.jogl.model.ground.GroundDrawer;
import kendzi.josm.kendzi3d.jogl.model.ground.StyledTitleGroundDrawer;
import kendzi.josm.kendzi3d.jogl.skybox.SkyBoxDrawer;
import kendzi.josm.kendzi3d.ui.fps.FpsChangeEvent;
import kendzi.josm.kendzi3d.ui.fps.FpsListener;
import kendzi.kendzi3d.editor.selection.ObjectSelectionManager;
import kendzi3d.light.render.LightRender;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Draws 3d.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class Kendzi3dGLEventListenerOld implements GLEventListener {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Kendzi3dGLEventListenerOld.class);

    Viewport viewport = new Viewport(1, 1);

    /**
     * Model renderer.
     */
    private ModelRender modelRender;

    /**
     * Renderer of josm opengl object.
     */
    private RenderJOSM renderJosm;

    /**
     * Texture cache service.
     */
    private TextureCacheService textureCacheService;

    /**
     * Texture library service.
     */
    private TextureLibraryStorageService textureLibraryStorageService;

    /**
     * Light render.
     */
    private final LightRender lightRender;

    /**
     * Animator of camera movement.
     */
    private final SimpleMoveAnimator simpleMoveAnimator;

    /**
     * Key and mouse listener for camera movement.
     */
    private final CameraMoveListener cameraMoveListener;

    private ObjectSelectionManager objectSelectionListener;

    /**
     * Axis labels.
     */
    private final AxisLabels axisLabels;

    /**
     * Drawer for tiles floor.
     */
    private final TilesSurface floor = new TilesSurface();

    // private final SelectionDrawUtil selectionDrawUtil;

    /**
     * Ground.
     */
    private GroundDrawer ground;

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
    private final long startTimeStamp = System.currentTimeMillis();

    /**
     * Time spend in render loop.
     */
    private long timeSpend;

    private final List<FpsListener> fpsChangeListenerList = new ArrayList<FpsListener>();

    SkyBoxDrawer skyBox;

    private CloseEvent closeEvent;

    private boolean error;

    private final CompassDrawer compass;

    @Inject
    public Kendzi3dGLEventListenerOld(ModelRender modelRender, RenderJOSM renderJosm,
            TextureCacheService textureCacheService, TextureLibraryStorageService textureLibraryStorageService,
            SkyBoxDrawer skyBox, LightRender lightRender) {
        super();
        this.modelRender = modelRender;
        this.renderJosm = renderJosm;
        this.textureCacheService = textureCacheService;
        this.textureLibraryStorageService = textureLibraryStorageService;
        this.skyBox = skyBox;

        setGroundType(false);

        axisLabels = new AxisLabels();

        simpleMoveAnimator = new SimpleMoveAnimator();

        cameraMoveListener = new CameraMoveListener(simpleMoveAnimator);

        // selectionDrawUtil = new SelectionDrawUtil();

        compass = new CompassDrawer();

        this.lightRender = lightRender;

        initObjectSelectionListener();
    }

    private void initObjectSelectionListener() {

        // objectSelectionListener = new ObjectSelectionManager() {
        //
        // @Override
        // public Ray3d viewportPicking(int x, int y) {
        // return viewport.picking(x, y);
        // }
        //
        // @Override
        // public Selection select(Ray3d selectRay) {
        // return renderJosm.select(selectRay);
        // }
        // };

        // selectEditorListeners.add(objectSelectionListener);
        // objectSelectionListener.addEditorChangeListener(this);

        // JosmEditorListener jel = new JosmEditorListener();
        // objectSelectionListener.addEditorChangeListener(jel);
    }

    @Override
    public void display(GLAutoDrawable pDrawable) {

        if (error) {
            return;
        }

        countFps();

        simpleMoveAnimator.updateState();

        GL2 gl = pDrawable.getGL().getGL2();
        // System.err.println("INIT GL IS: " + gl.getClass().getName());

        GLU glu = new GLU();

        lightRender.draw(gl);

        // _direction_
        // gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
        // this.lightPos, 0);

        // // Clear the drawing area
        // gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        //
        //
        //
        //
        // // Reset the current matrix to the "identity"
        // gl.glLoadIdentity();
        //
        // gl.glMatrixMode(GL2.GL_MODELVIEW);
        // gl.glLoadIdentity();

        // clear color and depth buffers
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        setCamera(gl, glu, simpleMoveAnimator);

        gl.glEnable(GL.GL_MULTISAMPLE);

        skyBox.draw(gl, simpleMoveAnimator.getPoint());

        ground.draw(gl, simpleMoveAnimator.getPoint());

        if (modelRender.isDebugging()) {

            axisLabels.draw(gl);

            floor.draw(gl);

            // drawTextInfo(gl, simpleMoveAnimator.info());
        }

        renderJosm.draw(gl, simpleMoveAnimator);

        // selectionDrawUtil.draw(gl, objectSelectionListener,
        // simpleMoveAnimator);

        drawCompass(gl);

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    private void drawCompass(GL2 gl) {

        compass.drawAtLeftBottom(gl, viewport);
    }

    /**
     * Counts fps. Save last result to variable fps.
     */
    protected void countFps() {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - fpsTimeStamp > 1000) {
            fpsTimeStamp = timeMillis;
            fps = fpsCount;
            fpsCount = 0;

            timeSpend = (timeMillis - startTimeStamp) / 1000l;

            dispatchFpsChange(timeSpend, fps);
        }

        fpsCount++;
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
        // System.err.println("INIT GL IS: " + gl.getClass().getName());

        checkRequiredExtensions(gl);

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        gl.glClearDepth(1.0);
        // sky blue color
        gl.glClearColor(0.17f, 0.65f, 0.92f, 0.0f);

        gl.glEnable(GL.GL_DEPTH_TEST);
        // int[] depth_bits = new int[1];
        // gl.glGetIntegerv(GL.GL_DEPTH_BITS, depth_bits, 0);

        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);

        float[] grayCol = { 0.8f, 0.8f, 0.8f, 1.0f };
        // float[] blueCol = {0.0f, 0.0f, 0.8f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE, grayCol, 0);

        // this.modelRender = ModelRender.getInstance();

        axisLabels.init();
        renderJosm.init(gl);
        // selectionDrawUtil.init(gl);
        compass.init(gl);

        lightRender.init(gl);

    }

    /**
     * Check if all required openGl extensions are available.
     * 
     * @param gl
     * @return
     */
    private boolean checkRequiredExtensions(GL2 gl) {
        error = false;
        // String extensions = gl.glGetString(GL2.GL_EXTENSIONS);
        if (!gl.isExtensionAvailable("GL_ARB_multitexture")) {

            // JOptionPane.showMessageDialog(null,
            // "GL_ARB_vertex_buffer_object extension not available",
            // "Unavailable extension", JOptionPane.ERROR_MESSAGE);

            /*
             * Check if the extension ARB_multitexture is supported by the
             * Graphic card
             */
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    JOptionPane.showMessageDialog(null,
                            "GL_ARB_multitexture OpenGL extension is not supported. Install correct graphic drivers!",
                            "Extension not supported", JOptionPane.ERROR_MESSAGE);
                }
            });

            error = true;

            if (closeEvent != null) {
                closeEvent.closeAction();
            }
        }

        return error;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        // GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!
            height = 1;
        }

        viewport = new Viewport(width, height);

        // gl.glViewport(0, 0, width, height); // size of drawing area
        //
        // gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        // gl.glLoadIdentity();
        // glu.gluPerspective(Viewport.PERSP_VIEW_ANGLE,
        // viewport.viewportAspectRatio(),
        // Viewport.PERSP_NEAR_CLIPPING_PLANE_DISTANCE, 1500.0);

        ViewportUtil.reshapePerspective(viewport, gl);

    }

    /**
     * Register listener for camera move.
     * 
     * @param pCanvas
     *            canvas for listener
     */
    public void registerMoveListener(Canvas pCanvas) {
        pCanvas.addKeyListener(cameraMoveListener);

        pCanvas.addMouseMotionListener(cameraMoveListener);

        pCanvas.addMouseListener(cameraMoveListener);
    }

    /**
     * Register listener for mouse selection.
     * 
     * @param pCanvas
     *            canvas for listener
     */
    public void registerMouseSelectionListener(Canvas pCanvas) {

        pCanvas.addMouseListener(objectSelectionListener);
        pCanvas.addMouseMotionListener(objectSelectionListener);
    }

    /**
     * Sets camera position and rotation.
     * 
     * @param glu
     *            GLU
     * @param pCamera
     */
    private void setCamera(GL2 gl, GLU glu, Camera pCamera) {

        viewport.updateViewport(pCamera);

        ViewportUtil.lookAt(gl, viewport);

    }

    /**
     * @return the renderJosm
     */
    public RenderJOSM getRenderJosm() {
        return renderJosm;
    }

    /**
     * Set up camera position. Warning this is 2d version!!! it convert z to
     * -z!!!
     * 
     * @deprecated
     * 
     * @param pCamPosX
     *            x coordinate
     * @param pCamPosY
     *            y coordinate
     */
    @Deprecated
    public void setCamPos(double pCamPosX, double pCamPosY) {
        setCamPos(pCamPosX, Camera.CAM_HEIGHT, -pCamPosY);
    }

    /**
     * Set up camera position.
     * 
     * @param pCamPosX
     *            x coordinate
     * @param pCamPosY
     *            y coordinate
     * @param pCamPosZ
     *            z coordinate
     */
    public void setCamPos(double pCamPosX, double pCamPosY, double pCamPosZ) {

        simpleMoveAnimator.setPoint(pCamPosX, pCamPosY, pCamPosZ);
    }

    public Camera getCamera() {
        return simpleMoveAnimator;
    }

    /**
     * @return the fps
     */
    public int getFps() {
        return fps;
    }

    /**
     * @param pType
     *            type of the ground to set
     */
    public void setGroundType(boolean pType) {
        if (pType) {
            ground = new StyledTitleGroundDrawer(textureCacheService, null);
        } else {
            ground = new GroundDrawer(textureCacheService, textureLibraryStorageService);
        }
    }

    /**
     * @param renderJosm
     *            the renderJosm to set
     */
    public void setRenderJosm(RenderJOSM renderJosm) {
        this.renderJosm = renderJosm;
    }

    /**
     * @return the modelRender
     */
    public ModelRender getModelRender() {
        return modelRender;
    }

    /**
     * @param modelRender
     *            the modelRender to set
     */
    public void setModelRender(ModelRender modelRender) {
        this.modelRender = modelRender;
    }

    public void dispatchFpsChange(FpsChangeEvent fpsChangeEvent) {
        for (FpsListener fl : fpsChangeListenerList) {
            fl.dispatchFpsChange(fpsChangeEvent);
        }
    }

    public void addFpsChangeListener(FpsListener pFpsListener) {
        fpsChangeListenerList.add(pFpsListener);
    }

    public void removeFpsChangeListener(FpsListener pFpsListener) {
        fpsChangeListenerList.remove(pFpsListener);
    }

    public void closeEvent(CloseEvent closeEvent) {
        this.closeEvent = closeEvent;
    }

    /**
     * @return the textureCacheService
     */
    public TextureCacheService getTextureCacheService() {
        return textureCacheService;
    }

    /**
     * @param textureCacheService
     *            the textureCacheService to set
     */
    public void setTextureCacheService(TextureCacheService textureCacheService) {
        this.textureCacheService = textureCacheService;
    }

    /**
     * @return the textureLibraryStorageService
     */
    public TextureLibraryStorageService getTextureLibraryStorageService() {
        return textureLibraryStorageService;
    }

    /**
     * @param textureLibraryStorageService
     *            the textureLibraryStorageService to set
     */
    public void setTextureLibraryStorageService(TextureLibraryStorageService textureLibraryStorageService) {
        this.textureLibraryStorageService = textureLibraryStorageService;
    }

    // @Override
    // public void onEditorChange(EditorChangeEvent args) {
    // if (args instanceof ArrowEditorChangeEvent) {
    // this.closestPointOnBaseRay =
    // ((ArrowEditorChangeEvent)args).getClosestPointOnBaseRay();
    // }
    // }
}
