/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.ui;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.CameraMoveListener;
import kendzi.jogl.camera.SimpleMoveAnimator;
import kendzi.jogl.drawer.AxisLabels;
import kendzi.jogl.drawer.TilesSurface;
import kendzi.jogl.glu.GLU;
import kendzi.math.geometry.point.PointUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * Base for test jogl applications.
 */
public class BaseJoglFrame implements GLEventListener {

    /**
     * Position of sun. XXX
     */
    private final float[] lightPos = new float[] { 0.0f, 1.0f, 1.0f, 0f };

    /**
     * Drawer for axis labels.
     */
    private final AxisLabels axisLabels = new AxisLabels();

    /**
     * Drawer for tiles floor.
     */
    private final TilesSurface floor = new TilesSurface();

    private final SimpleMoveAnimator simpleMoveAnimator = new SimpleMoveAnimator();

    CameraMoveListener cameraMoveListener = new CameraMoveListener(this.simpleMoveAnimator);

    public static void main(String[] args) {

        BaseJoglFrame sj = new BaseJoglFrame();

        sj.initUi();
    }

    /**
     * @param frame
     * @param sj
     */
    public void initUi() {
        Frame frame = new Frame("Simple JOGL Application");

        GLCanvas canvas = createCanvas();

        canvas.addGLEventListener(this);
        frame.add(canvas);
        frame.setSize(640, 480);
        final AnimatorBase animator = new FPSAnimator(canvas, 60);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                /*
                 * Run this on another thread than the AWT event queue to make sure the call to
                 * Animator.stop() completes before exiting.
                 */
                new Thread(() -> {
                    animator.stop();
                    System.exit(0);
                }).start();
            }
        });

        addSimpleMover(canvas, this);

        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
        canvas.setFocusable(true);
        canvas.requestFocus();
    }

    /**
     * @return
     */
    public static GLCanvas createCanvas() {
        // create a profile, in this case OpenGL 2 or later
        GLProfile profile = GLProfile.get(GLProfile.GL2);

        // configure context
        GLCapabilities capabilities = new GLCapabilities(profile);

        // setup z-buffer
        capabilities.setDepthBits(16);

        // for anti-aliasing
        // FIXME enabling sample buffers on dual screen ubuntu cause problems...
        // capabilities.setSampleBuffers(true);
        // capabilities.setNumSamples(2);

        // initialize a GLDrawable of your choice
        GLCanvas canvas = new GLCanvas(capabilities);
        return canvas;
    }

    private static void addSimpleMover(GLCanvas canvas, final BaseJoglFrame sj) {
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
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GL11.glClearDepth(1.0);
        GL11.glClearColor(0.17f, 0.65f, 0.92f, 0.0f); // sky blue color

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        int[] depth_bits = new int[1];
        GL11.glGetIntegerv(GL11.GL_DEPTH_BITS, depth_bits);

        GL11.glShadeModel(GL11.GL_SMOOTH); // try setting this to
        // GL_FLAT and see what
        // happens.

        addLight();

        float[] grayCol = { 0.8f, 0.8f, 0.8f, 1.0f };
        // float[] blueCol = {0.0f, 0.0f, 0.8f, 1.0f};
        GL11.glMaterialfv(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, grayCol);

        axisLabels.init();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        if (height <= 0) { // avoid a divide by zero error!

            height = 1;
        }

        GL11.glViewport(0, 0, width, height); // size of drawing area

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(45.0f, (float) width / (float) height, 1.0f, 1500.0f); // 5
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        this.simpleMoveAnimator.updateState();

        GL2 gl = drawable.getGL().getGL2();
        // System.err.println("INIT GL IS: " + gl.getClass().getName());

        // _direction_
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, this.lightPos);

        // clear color and depth buffers
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        // GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        setCamera();

        GL11.glEnable(GL13.GL_MULTISAMPLE);

        // String versionStr = GL11.glGetString( GL11.GL_VERSION );
        // log.info( "GL version:"+versionStr );

        floor.draw();

        axisLabels.draw();

        // drawTextInfo(gl, this.simpleMoveAnimator.info());

        // Flush all drawing operations to the graphics card
        GL11.glFlush();
    }

    /**
     * Sets camera position and rotation.
     *
     */
    private void setCamera() {

        Point3d pos = this.simpleMoveAnimator.getPoint();
        Vector3d posLookAt = new Vector3d(100, 0, 0);
        Vector3d rotate = this.simpleMoveAnimator.getAngle();

        posLookAt = PointUtil.rotateZ3d(posLookAt, rotate.z);
        posLookAt = PointUtil.rotateY3d(posLookAt, rotate.y);

        posLookAt.add(pos);

        GLU.gluLookAt((float) pos.x, (float) pos.y, (float) pos.z, (float) posLookAt.x, (float) posLookAt.y, (float) posLookAt.z,
                0, 1, 0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    /**
     * Set up a point source with ambient, diffuse, and specular color. components
     */
    private void addLight() {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        // enable a single light source
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);

        float gray = 0.5f;
        float[] grayLight = { gray, gray, gray, 1.0f }; // weak gray ambient
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, grayLight);

        float[] whiteLight = { 1.0f, 1.0f, 1.0f, 1.0f }; // bright white diffuse
        // & specular
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, whiteLight);
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, whiteLight);

        // float lightPos[] = { 1.0f, 1.0f, 1.0f, 0.0f }; // top right front
        float[] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        // _direction_
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPos);

        // GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);

        // float [] lmodel_ambient = { 1f, 1f, 1f, 1.0f };
        // GL11.glLightModelfv(GL11.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
    }
}
