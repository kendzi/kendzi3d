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
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kendzi.josm.kendzi3d.jogl.photos.PhotoParmPanel;
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
public class Kendzi3dGLFrame extends Frame implements WindowListener, FpsListener {

    /** Log. */
    private static final Logger log = Logger.getLogger(Kendzi3dGLFrame.class);

    /**
     *
     */
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

    private PhotoParmPanel photoParmPanel;

    @Inject
    Kendzi3dGLEventListener canvasListener;
    // = new Kendzi3dGLEventListener() {
    //
    // @Override
    // void displayStats(long pTime, int pFps) {
    // setTimeSpent(pTime);
    // setFps(pFps);
    // }
    // };

    /**
     * 3d view animator.
     */
    private AnimatorBase animator;

    /**
     * Constructor.
     */
    public Kendzi3dGLFrame() {
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

        this.jTFFps = new JTextField("Fps: unknown");
        this.jTFFps.setEditable(false);
        ctrls.add(this.jTFFps);

        this.jTFTime = new JTextField("Time Spent: 0 secs");
        this.jTFTime.setEditable(false);
        ctrls.add(this.jTFTime);

        c.add(ctrls, BorderLayout.SOUTH);

        addWindowListener(this);

        // setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        if (PhotoParmPanel.showPhotoPanel) {
            initPhotoFrame();
        }

        pack();
        // setVisible(true);
    }


    private void initPhotoFrame() {
        JFrame photoFrame = new JFrame();
        this.photoParmPanel = new PhotoParmPanel();
        photoFrame.getContentPane().add(this.photoParmPanel);
        photoFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        photoFrame.pack();
        photoFrame.setVisible(true);

        this.photoParmPanel.addCameraChangeListener(this.canvasListener);
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
        // renderPane.add("Center", this.canvas);

        renderPane.add(this.canvas);
        renderPane.setVisible(true);

        renderPane.getComponentListeners();
        this.getWindowListeners();

        this.canvas.setFocusable(true);
        this.canvas.requestFocus(); // the canvas now has focus, so receives key
        // events

        // // detect window resizes, and reshape the canvas accordingly
        // renderPane.addComponentListener(new ComponentAdapter() {
        // @Override
        // public void componentResized(ComponentEvent evt) {
        // Dimension d = evt.getComponent().getSize();
        // // log.info("New size: " + d);
        // View3dGLFrame.this.canvas.reshape(d.width, d.height);
        // } // end of componentResized()
        // });

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

        this.canvasListener.closeEvent(new CloseEvent() {
            @Override
            public void closeAction() {
                windowClosing(null);
            }
        });

        // selection/edition listener first!
        this.canvasListener.registerMouseSelectionListener(canvas);
        this.canvasListener.registerMoveListener(canvas);

        this.canvasListener.addFpsChangeListener(this);

        // Center frame
        // render.setLocationRelativeTo(null);

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

        String sampleBuffers = System.getProperty("kendzi3d.opengl.sampleBuffers");
        log.info("user sampleBuffers: " + sampleBuffers);

        if (sampleBuffers == null) {
            capabilities.setSampleBuffers(true);
        } else if ("true".equals(sampleBuffers)) {
            capabilities.setSampleBuffers(true);
        } else if ("false".equals(sampleBuffers)) {
            capabilities.setSampleBuffers(false);
        }

        String sampleBuffersNum = System.getProperty("kendzi3d.opengl.sampleBuffersNum");
        log.info("user sampleBuffersNum: " + sampleBuffersNum);

        if (sampleBuffersNum == null) {
            capabilities.setNumSamples(2);
        } else if (!"default".equals(sampleBuffersNum)) {
            capabilities.setNumSamples(Integer.parseInt(sampleBuffersNum));
        }

        log.info("GLCapabilities: " + capabilities);
    }

    /**
     * Display time spent.
     *
     * @param pTime
     *            time
     */
    public void setTimeSpent(long pTime) {
        this.jTFTime.setText("Time Spent: " + pTime + " secs");
    }

    /**
     * Display fps.
     *
     * @param pFps
     *            fps
     */
    public void setFps(int pFps) {
        this.jTFFps.setText("Fps: " + pFps);
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

        if (Kendzi3dGLFrame.this.animator.isStarted()) {
            Kendzi3dGLFrame.this.animator.stop();
        }

        this.canvasListener.removeFpsChangeListener(this);

        new Thread(new Runnable() {

            @Override
            public void run() {

                Kendzi3dGLFrame.this.setVisible(false);
                Kendzi3dGLFrame.this.dispose();
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
    public Kendzi3dGLEventListener getCanvasListener() {
        return this.canvasListener;
    }

    /**
     * @param canvasListener
     *            the canvasListener to set
     */
    public void setCanvasListener(Kendzi3dGLEventListener canvasListener) {
        this.canvasListener = canvasListener;
    }

    @Override
    public void dispatchFpsChange(FpsChangeEvent fpsChangeEvent) {

        if (fpsChangeEvent != null) {
            setFps(fpsChangeEvent.getFps());
            setTimeSpent(fpsChangeEvent.getTime());
        }
    }

}
