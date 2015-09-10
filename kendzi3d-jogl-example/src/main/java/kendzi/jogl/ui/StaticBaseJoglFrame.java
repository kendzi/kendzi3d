/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.ui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

import kendzi.jogl.drawer.AxisLabels;
import kendzi.jogl.drawer.TilesSurface;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.awt.TextRenderer;

/**
 */
public class StaticBaseJoglFrame implements GLEventListener {

    /**
     * Position of sun. XXX
     */
    private float[] lightPos = new float[] { 0.0f, 1.0f, 1.0f, 0f };

    /**
     * XXX Font for axis.
     */
    private Font font = new Font("SansSerif", Font.BOLD, 24);

    /**
     * XXX For axis labels.
     */
    private TextRenderer axisLabelRenderer = new TextRenderer(this.font);

    /**
     * Drawer for axis labels.
     */
    private AxisLabels axisLabels = new AxisLabels();

    /**
     * Drawer for tiles floor.
     */
    private TilesSurface floor = new TilesSurface();

    /**
     * XXX For the axis labels.
     */
    private final static float SCALE_FACTOR = 0.01f;

    /**
     * XXX
     */
    private final static int FLOOR_LEN = 50;

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
        gl.glClearColor(0.17f, 0.65f, 0.92f, 0.0f); // sky blue color

        gl.glEnable(GL.GL_DEPTH_TEST);
        int[] depth_bits = new int[1];
        gl.glGetIntegerv(GL.GL_DEPTH_BITS, depth_bits, 0);

        gl.glShadeModel(GLLightingFunc.GL_SMOOTH); // try setting this to
        // GL_FLAT and see what
        // happens.

        addLight(gl);

        axisLabels.init();

        float[] grayCol = { 0.8f, 0.8f, 0.8f, 1.0f };

        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE, grayCol, 0);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!

            height = 1;
        }

        gl.glViewport(0, 0, width, height); // size of drawing area

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (float) width / (float) height, 1.0, 1500.0); // 5
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        GL2 gl = drawable.getGL().getGL2();
        // System.err.println("INIT GL IS: " + gl.getClass().getName());

        GLU glu = new GLU();

        // _direction_
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, this.lightPos, 0);

        // clear color and depth buffers
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        setCamera(glu);

        gl.glEnable(GL.GL_MULTISAMPLE);

        // String versionStr = gl.glGetString( GL2.GL_VERSION );
        // log.info( "GL version:"+versionStr );

        floor.draw(gl);

        axisLabels.draw(gl);

        // drawTextInfo(gl, this.simpleMoveAnimator.info());

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    /**
     * Sets camera position and rotation.
     *
     * @param pGlu
     *            GLU
     */
    private void setCamera(GLU pGlu) {

        pGlu.gluLookAt(10, 3, 0, 0, 0, 0, 0, 1, 0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    /**
     * Set up a point source with ambient, diffuse, and specular color.
     * components
     */
    private void addLight(GL2 gl) {
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        // enable a single light source
        gl.glEnable(GLLightingFunc.GL_LIGHTING);
        gl.glEnable(GLLightingFunc.GL_LIGHT0);

        float gray = 0.5f;
        float[] grayLight = { gray, gray, gray, 1.0f }; // weak gray ambient
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, grayLight, 0);

        float[] whiteLight = { 1.0f, 1.0f, 1.0f, 1.0f }; // bright white diffuse
        // & specular
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, whiteLight, 0);
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, whiteLight, 0);

        float[] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        // _direction_
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightPos, 0);

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
