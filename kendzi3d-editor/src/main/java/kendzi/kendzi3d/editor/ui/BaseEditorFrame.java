package kendzi.kendzi3d.editor.ui;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.inject.Inject;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

import kendzi.jogl.camera.CameraMoveListener;
import kendzi.kendzi3d.editor.selection.ObjectSelectionManager;
import kendzi.kendzi3d.editor.ui.event.CloseWindowListener;

/**
 * Example frame with 3d editor.
 *
 * @author tkedziora
 *
 */
public abstract class BaseEditorFrame extends Frame {

    private static final long serialVersionUID = 1L;

    /**
     * Camera move listener, for move animation after keyboard or mouse events.
     */
    @Inject
    private CameraMoveListener cameraMoveListener;

    /**
     * Selection listener manager, to handle mouse and keyboard events for
     * selection.
     */
    @Inject
    private ObjectSelectionManager objectSelectionListener;

    /**
     * The GLCanvas to draw on.
     */
    protected final GLCanvas canvas;

    /**
     * Constructor.
     */
    public BaseEditorFrame() {
        super();
        canvas = createCanvas();
    }

    /**
     * Constructor.
     *
     * @param name
     *            frame name
     */
    public BaseEditorFrame(String name) {
        super(name);
        canvas = createCanvas();
    }

    /**
     * Listener for GL related draw events.
     *
     * @return gl event listener
     */
    public abstract GLEventListener getGlEventListener();

    /**
     * Initiate frame with 3d canvas.
     */
    public void initUi() {

        GLEventListener listener = getGlEventListener();

        final Frame frame = this;

        // Adds canvas drawer.
        canvas.addGLEventListener(listener);

        // Adds canvas to frame.
        frame.add(canvas);
        frame.setSize(640, 480);

        // Setup animator for canvas.
        final FPSAnimator animator = new FPSAnimator(canvas, 30);

        if (listener instanceof CloseWindowEventSource) {
            // if listener could be source of window close event
            ((CloseWindowEventSource) listener).addCloseWindowListener(new CloseWindowListener() {

                @Override
                public void closeWindow() {
                    closeWindowRequest(frame, animator);
                }
            });
        }

        // Listener to close correctly application and stop animator.
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                closeWindowRequest(frame, animator);
            }

        });

        // Adds listener for mouse and keyboard to support object selection.
        addSelectionListener();
        // Adds listeners for mouse and keyboard to support camera move.
        addCameraMoveListener();

        // Center frame.
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Start animate.
        animator.start();

        // Request focus to enable keyboard input.
        canvas.setFocusable(true);
        canvas.requestFocus();

        onOpenWindow();
    }

    protected void onOpenWindow() {
        //
    }

    protected void onCloseWindow() {
        //
    }

    private void closeWindowRequest(final Frame frame, final AnimatorBase animator) {
        /*
         * Run this on another thread than the AWT event queue to make sure the
         * call to Animator.stop() completes before exiting.
         */
        new Thread(new Runnable() {

            @Override
            public void run() {

                // If need stop animator before dispose frame.
                if (animator.isStarted()) {
                    animator.stop();
                }

                // Dispose frame.
                frame.setVisible(false);
                frame.dispose();

                onCloseWindow();
            }

        }).start();
    }

    /**
     * @return canvas with GL2 profile.
     */
    private static GLCanvas createCanvas() {
        // create a profile, in this case OpenGL 2 or later
        GLProfile profile = GLProfile.get(GLProfile.GL2);

        // configure context
        GLCapabilities capabilities = new GLCapabilities(profile);

        // setup z-buffer
        capabilities.setDepthBits(32);

        // for anti-aliasing
        // FIXME enabling sample buffers on dual screen ubuntu cause problems...
        // capabilities.setSampleBuffers(true);
        // capabilities.setNumSamples(2);

        // initialize a GLDrawable of your choice
        return new GLCanvas(capabilities);
    }

    private void addCameraMoveListener() {

        canvas.addKeyListener(cameraMoveListener);
        canvas.addMouseListener(cameraMoveListener);
        canvas.addMouseMotionListener(cameraMoveListener);
        canvas.addMouseWheelListener(cameraMoveListener);
        canvas.addComponentListener(cameraMoveListener);
    }

    /**
     * Register listener for mouse selection.
     *
     * @param pCanvas
     *            canvas for listener
     */
    private void addSelectionListener() {

        canvas.addMouseListener(objectSelectionListener);
        canvas.addMouseMotionListener(objectSelectionListener);
    }
}
