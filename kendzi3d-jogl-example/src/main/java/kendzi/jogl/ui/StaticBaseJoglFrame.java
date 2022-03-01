/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.ui;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import kendzi.jogl.drawer.AxisLabels;
import kendzi.jogl.drawer.TilesSurface;
import kendzi.jogl.glu.GLU;
import kendzi.jogl.util.GLEventListener;
import kendzi.jogl.util.texture.awt.TextRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 */
public class StaticBaseJoglFrame implements GLEventListener {

    /**
     * Position of sun. XXX
     */
    private final float[] lightPos = new float[] { 0.0f, 1.0f, 1.0f, 0f };

    /**
     * XXX Font for axis.
     */
    private final Font font = new Font("SansSerif", Font.BOLD, 24);

    /**
     * XXX For axis labels.
     */
    private final TextRenderer axisLabelRenderer = new TextRenderer(this.font);

    /**
     * Drawer for axis labels.
     */
    private final AxisLabels axisLabels = new AxisLabels();

    /**
     * Drawer for tiles floor.
     */
    private final TilesSurface floor = new TilesSurface();

    /**
     * XXX For the axis labels.
     */
    private static final float SCALE_FACTOR = 0.01f;

    /**
     * XXX
     */
    private static final int FLOOR_LEN = 50;

    public static void main(String[] args) {

        StaticBaseJoglFrame sj = new StaticBaseJoglFrame();

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
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(() -> {
                    animator.stop();
                    System.exit(0);
                }).start();
            }
        });

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
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(2);

        // initialize a GLDrawable of your choice
        GLCanvas canvas = new GLCanvas(capabilities);
        return canvas;
    }

    @Override
    public void init() {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        // FIXME Enable VSync
        // gl.setSwapInterval(1);

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

        axisLabels.init();

        float[] grayCol = { 0.8f, 0.8f, 0.8f, 1.0f };

        GL11.glMaterialfv(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, grayCol);

    }

    @Override
    public void reshape(int x, int y, int width, int height) {
        if (height <= 0) { // avoid a divide by zero error!

            height = 1;
        }

        GL11.glViewport(0, 0, width, height); // size of drawing area

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(45.0f, (float) width / (float) height, 1.0f, 1500.0f); // 5
    }

    @Override
    public void display() {
        // System.err.println("INIT GL IS: " + GL11.getClass().getName());

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

        GLU.gluLookAt(10, 3, 0, 0, 0, 0, 0, 1, 0);
    }

    @Override
    public void dispose() {
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

        float[] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        // _direction_
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPos);

    }

    /**
     * Place numbers along the x- and z-axes at the integer positions.
     *
     */
    private void labelAxes() {
        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            drawAxisText("x: " + i, i, 0.0f, 0.0f); // along x-axis
        }

        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            drawAxisText("z: " + i, 0.0f, 0.0f, i); // along z-axis
        }

        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            drawAxisText("y: " + i, 0.0f, i, 0.0f); // along y-axis
        }
    }

    /**
     * Draw txt at (x,y,z), with the text centered in the x-direction, facing along
     * the +z axis.
     *
     * @param txt
     * @param x
     * @param y
     * @param z
     */
    private void drawAxisText(String txt, float x, float y, float z) {

        Rectangle2D dim = this.axisLabelRenderer.getBounds(txt);
        float width = (float) dim.getWidth() * SCALE_FACTOR;

        this.axisLabelRenderer.begin3DRendering();
        this.axisLabelRenderer.draw3D(txt, x - width / 2, y, z, SCALE_FACTOR);
        this.axisLabelRenderer.end3DRendering();
    }
}
