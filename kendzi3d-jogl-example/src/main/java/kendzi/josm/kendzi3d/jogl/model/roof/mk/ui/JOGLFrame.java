package kendzi.josm.kendzi3d.jogl.model.roof.mk.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.jogamp.common.GlueGenVersion;
import com.jogamp.opengl.JoglVersion;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

public class JOGLFrame extends Frame  {

    /**
     * Panel default width.
     */
    private static final int DEF_WIDTH = 512;

    /**
     * Panel default height.
     */
    private static final int DEF_HEIGHT = 512;

    /**
     * Canvas.
     */
    private Canvas canvas;


    JoglCanvasListener canvasListener = new JoglCanvasListener();

    /**
     * 3d view animator.
     */
    private AnimatorBase animator;

    /**
     * Constructor.
     */
    public JOGLFrame() {
        super("Kendzi 3D");
    }

    /**
     * Init ui after bean is created.
     */
    public void initUI() {

        // Container c = getContentPane();
        Container c = this;
        c.setLayout(new BorderLayout());
        c.add(makeRenderPanel(), BorderLayout.CENTER);

        JPanel ctrls = new JPanel(); // a row of text fields
        ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.X_AXIS));

        //        this.jTFFps = new JTextField("Fps: unknown");
        //        this.jTFFps.setEditable(false);
        //        ctrls.add(this.jTFFps);
        //
        //        this.jTFTime = new JTextField("Time Spent: 0 secs");
        //        this.jTFTime.setEditable(false);
        //        ctrls.add(this.jTFTime);

        c.add(ctrls, BorderLayout.SOUTH);



        pack();
        // setVisible(true);
    }



    /**
     * Make canvas panel.
     *
     * @return panel with canvas
     */
    private JPanel makeRenderPanel() {
        JPanel renderPane = new JPanel();
        renderPane.setLayout(new BorderLayout());
        renderPane.setOpaque(false);
        renderPane.setPreferredSize(new Dimension(DEF_WIDTH, DEF_HEIGHT));
        // XXX
        renderPane.setSize(640, 480);

        this.canvas = makeCanvas(renderPane);

        renderPane.add(this.canvas);
        renderPane.setVisible(true);

        renderPane.getComponentListeners();
        this.getWindowListeners();

        this.canvas.setFocusable(true);
        this.canvas.requestFocus(); // the canvas now has focus, so receives key


        return renderPane;
    }

    private Canvas makeCanvas(JPanel render) {

        logJoglManifest();

        // create a profile, in this case OpenGL 2 or later
        GLProfile profile = GLProfile.get(GLProfile.GL2);

        // configure context
        GLCapabilities capabilities = new GLCapabilities(profile);

        setUpCapabilities(capabilities);

        // initialize a GLDrawable of your choice
        GLCanvas canvas = new GLCanvas(capabilities);

        canvas.addGLEventListener(this.canvasListener);

        this.animator = new FPSAnimator(canvas, 50);// Animator(canvas);
        this.animator.start();

        canvas.setFocusable(true);
        canvas.requestFocus();

        return canvas;
    }

    /**
     * Log Jogl manifest information.
     */
    private void logJoglManifest() {
        StringBuilder sb = new StringBuilder();
        sb.append("JoglVersion: \n");
        JoglVersion.getInstance().getFullManifestInfo(sb);

        sb.append("\nGlueGenVersion: \n");
        GlueGenVersion.getInstance().getFullManifestInfo(sb);

    }

    /**
     * Set up openGL capabilities.
     *
     * @param capabilities
     *            openGL capabilities
     */
    private void setUpCapabilities(GLCapabilities capabilities) {
        String zbuffer = System.getProperty("kendzi3d.opengl.zbuffer");

        // setup z-buffer
        if (zbuffer == null) {
            // Default use 16
            capabilities.setDepthBits(16);
        } else if (!"default".equals(zbuffer)) {
            capabilities.setDepthBits(Integer.parseInt(zbuffer));
        }

        String sampleBuffers = System.getProperty("kendzi3d.opengl.sampleBuffers");

        if (sampleBuffers == null) {
            capabilities.setSampleBuffers(true);
        } else if ("true".equals(sampleBuffers)) {
            capabilities.setSampleBuffers(true);
        } else if ("false".equals(sampleBuffers)) {
            capabilities.setSampleBuffers(false);
        }

        String sampleBuffersNum = System.getProperty("kendzi3d.opengl.sampleBuffersNum");

        if (sampleBuffersNum == null) {
            capabilities.setNumSamples(2);
        } else if (!"default".equals(sampleBuffersNum)) {
            capabilities.setNumSamples(Integer.parseInt(sampleBuffersNum));
        }

    }


}
