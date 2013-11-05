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
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.apache.log4j.Logger;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 */
public class JoglCanvasListener implements GLEventListener {

    /** Log. */
    private static final Logger log = Logger.getLogger(JoglCanvasListener.class);


    /**
     * Position of sun. XXX
     */
    private float [] lightPos = new float[] { 0.0f, 1.0f, 1.0f, 0f };

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

        gl.glShadeModel(GLLightingFunc.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.

        addLight(gl);

        float[] grayCol = { 0.8f, 0.8f, 0.8f, 1.0f };
        // float[] blueCol = {0.0f, 0.0f, 0.8f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE, grayCol, 0);


    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!

            height = 1;
        }
        final float h = (float) width / (float) height;

        gl.glViewport(0, 0, width, height); // size of drawing area

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (float)width / (float) height, 1.0, 1500.0); // 5
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
        //      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();


        setCamera(glu);


        gl.glEnable(GL.GL_MULTISAMPLE);


        //        String versionStr = gl.glGetString( GL2.GL_VERSION );
        //        log.info( "GL version:"+versionStr );

        drawFloor(gl);

        //        drawTextInfo(gl, this.simpleMoveAnimator.info());

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    private void drawFloor(GL2 gl) {
        gl.glDisable(GLLightingFunc.GL_LIGHTING);

        //blue
        gl.glColor3f(0.0f, 0.1f, 0.4f);
        DrawUtil.drawTiles(gl, 50, true);
        // green
        gl.glColor3f(0.0f, 0.5f, 0.1f);
        DrawUtil.drawTiles(gl, 50, false);


        labelAxes(gl);

        gl.glEnable(GLLightingFunc.GL_LIGHTING);
    }



    /**
     * Sets camera position and rotation.
     * @param pGlu GLU
     */
    private void setCamera(GLU pGlu) {

        pGlu.gluLookAt(10, 3, 0,
                0, 0, 0,
                0, 1, 0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable){}

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
        float[] grayLight = {gray, gray, gray, 1.0f }; // weak gray ambient
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, grayLight, 0);

        float[] whiteLight = { 1.0f, 1.0f, 1.0f, 1.0f }; // bright white diffuse
        // & specular
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, whiteLight, 0);
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, whiteLight, 0);

        //      float lightPos[] = { 1.0f, 1.0f, 1.0f, 0.0f }; // top right front
        float [] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        // _direction_
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightPos, 0);


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

        gl.glDisable(GLLightingFunc.GL_LIGHTING);

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
            gl.glRasterPos2i(x, y + i * 20);
            glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, msgLine);

            i++;
        }

        end2D(gl); // switch back to 3D viewing

        gl.glEnable(GLLightingFunc.GL_LIGHTING);
    }

    /**
     * Switch to 2D viewing (an orthographic projection).
     * @param gl
     */
    private void begin2D(GL2 gl) {
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
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
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glPushMatrix(); // save model view settings
        gl.glLoadIdentity();
        gl.glDisable(GL.GL_DEPTH_TEST);
    }

    /**
     * switch back to 3D viewing.
     * @param gl
     */
    private void end2D(GL2 gl) {
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glPopMatrix(); // restore previous projection settings
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
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
