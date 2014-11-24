package kendzi.josm.kendzi3d.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import kendzi.josm.kendzi3d.ui.fps.FpsChangeEvent;
import kendzi.josm.kendzi3d.ui.fps.FpsListener;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.jogamp.common.GlueGenVersion;
import com.jogamp.opengl.JoglVersion;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Main application window. Display 3d view, panel with menu and layers.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class Kendzi3dGLFrameOld extends Frame implements WindowListener, FpsListener {

    /** Log. */
    private static final Logger log = Logger.getLogger(Kendzi3dGLFrameOld.class);

    private static final long serialVersionUID = 1L;

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

    /**
     * Display fps.
     */
    private JTextField jTFFps;

    /**
     * Display 3d run time.
     */
    private JTextField jTFTime;

    @Inject
    Kendzi3dGLEventListenerOld canvasListener;

    /**
     * 3d view animator.
     */
    private AnimatorBase animator;

    /**
     * Constructor.
     */
    public Kendzi3dGLFrameOld() {
        super("Kendzi 3D");
    }

    /**
     * Init ui after bean is created.
     */
    public void initUI() {

        Container c = this;
        c.setLayout(new BorderLayout());
        c.add(makeRenderPanel(), BorderLayout.CENTER);

        // a row of text fields
        JPanel ctrls = new JPanel();
        ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.X_AXIS));

        jTFFps = new JTextField("Fps: unknown");
        jTFFps.setEditable(false);
        ctrls.add(jTFFps);

        jTFTime = new JTextField("Time Spent: 0 secs");
        jTFTime.setEditable(false);
        ctrls.add(jTFTime);

        c.add(ctrls, BorderLayout.SOUTH);

        addWindowListener(this);

        pack();
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

        canvas = makeCanvas(renderPane);

        renderPane.add(canvas);
        renderPane.setVisible(true);

        canvas.setFocusable(true);
        canvas.requestFocus(); // the canvas now has focus, so receives key

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

        canvas.addGLEventListener(canvasListener);

        canvasListener.closeEvent(new CloseEvent() {
            @Override
            public void closeAction() {
                windowClosing(null);
            }
        });

        // selection/edition listener first!
        canvasListener.registerMouseSelectionListener(canvas);
        canvasListener.registerMoveListener(canvas);

        canvasListener.addFpsChangeListener(this);

        // Center frame
        // render.setLocationRelativeTo(null);

        animator = new FPSAnimator(canvas, 50);// Animator(canvas);
        animator.start();

        canvas.setFocusable(true);
        canvas.requestFocus();

        return canvas;
    }

    /**
     * Log Jogl manifest information.
     */
    private void logJoglManifest() {
        log.info("is set debug for GraphicsConfiguration: " + jogamp.opengl.Debug.debug("GraphicsConfiguration"));

        StringBuilder sb = new StringBuilder();
        sb.append("JoglVersion: \n");
        JoglVersion.getInstance().getFullManifestInfo(sb);

        sb.append("\nGlueGenVersion: \n");
        GlueGenVersion.getInstance().getFullManifestInfo(sb);

        log.info(sb.toString());
    }

    /**
     * Set up openGL capabilities.
     *
     * @param capabilities
     *            openGL capabilities
     */
    private void setUpCapabilities(GLCapabilities capabilities) {
        String zbuffer = System.getProperty("kendzi3d.opengl.zbuffer");
        log.info("user zbuffer: " + zbuffer);

        // setup z-buffer
        if (zbuffer == null) {
            // Default use 16
            capabilities.setDepthBits(16);
        } else if (!"default".equals(zbuffer)) {
            capabilities.setDepthBits(Integer.parseInt(zbuffer));
        }

        log.info("GLCapabilities: " + capabilities);
    }

    public void setTimeAndFps(final long time, final int fps) {
        // in some configuration it is called from outside event queue
        // and with out generated EDT violation
        final JTextField textField = jTFFps;
        final JTextField timeField = jTFTime;

        // GuiHelper.runInEDT(new Runnable() {
        SwingUtilities.invokeLater(new Runnable() {
            // always in by query, with out it it could create dead lock in AWT
            @Override
            public void run() {
                textField.setText("Fps: " + fps);
                timeField.setText("Time Spent: " + time + " secs");
            }
        });
    }

    // ----------------- window listener methods -------------

    @Override
    public void windowActivated(WindowEvent e) {
        // this.canvas.resumeGame();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // this.canvas.pauseGame();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // this.canvas.resumeGame();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        // this.canvas.pauseGame();
    }

    @Override
    public void windowClosing(WindowEvent e) {

        if (Kendzi3dGLFrameOld.this.animator.isStarted()) {
            Kendzi3dGLFrameOld.this.animator.stop();
        }

        canvasListener.removeFpsChangeListener(this);

        new Thread(new Runnable() {

            @Override
            public void run() {

                Kendzi3dGLFrameOld.this.setVisible(false);
                Kendzi3dGLFrameOld.this.dispose();
            }
        }).start();

    }

    @Override
    public void windowClosed(WindowEvent e) {
        //
    }

    @Override
    public void windowOpened(WindowEvent e) {
        //
    }

    /**
     * @return the canvasListener
     */
    public Kendzi3dGLEventListenerOld getCanvasListener() {
        return canvasListener;
    }

    /**
     * @param canvasListener
     *            the canvasListener to set
     */
    public void setCanvasListener(Kendzi3dGLEventListenerOld canvasListener) {
        this.canvasListener = canvasListener;
    }

    @Override
    public void dispatchFpsChange(FpsChangeEvent fpsChangeEvent) {

        if (fpsChangeEvent != null) {
            setTimeAndFps(fpsChangeEvent.getTime(), fpsChangeEvent.getFps());
        }
    }
}
