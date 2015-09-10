package kendzi.kendzi3d.editor.ui;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.inject.Inject;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import kendzi.jogl.camera.CameraMoveListener;
import kendzi.kendzi3d.editor.selection.ObjectSelectionManager;
import kendzi.kendzi3d.editor.selection.listener.ObjectSelectionListener;
import kendzi.kendzi3d.editor.ui.event.CloseWindowListener;

import com.jogamp.opengl.util.Animator;

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
    private ObjectSelectionManager objectSelectionManager;

    /**
     * Constructor.
     */
    public BaseEditorFrame() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param name
     *            frame name
     */
    public BaseEditorFrame(String name) {
        super(name);
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

        // Creates canvas.
        GLCanvas canvas = createCanvas();
        // Adds canvas drawer.
        canvas.addGLEventListener(listener);

        // Adds canvas to frame.
        frame.add(canvas);
        frame.setSize(640, 480);

        // Setup animator for canvas.
        final Animator animator = new Animator(canvas);

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
        addSelectionListener(canvas, objectSelectionManager);
        // Adds listeners for mouse and keyboard to support camera move.
        addCameraMoveListener(canvas, cameraMoveListener);

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

    private void closeWindowRequest(final Frame frame, final Animator animator) {
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
        capabilities.setDepthBits(16);

        // for anti-aliasing
        // FIXME enabling sample buffers on dual screen ubuntu cause problems...
        // capabilities.setSampleBuffers(true);
        // capabilities.setNumSamples(2);

        // initialize a GLDrawable of your choice
        return new GLCanvas(capabilities);
    }

    private static void addCameraMoveListener(GLCanvas canvas, final CameraMoveListener cameraMoveListener) {

        canvas.addKeyListener(cameraMoveListener);
        canvas.addMouseMotionListener(cameraMoveListener);
        canvas.addMouseListener(cameraMoveListener);
    }

    /**
     * Register listener for mouse selection.
     * 
     * @param pCanvas
     *            canvas for listener
     */
    private void addSelectionListener(Canvas canvas, ObjectSelectionListener objectSelectionListener) {

        canvas.addMouseListener(objectSelectionListener);
        canvas.addMouseMotionListener(objectSelectionListener);
    }
}
