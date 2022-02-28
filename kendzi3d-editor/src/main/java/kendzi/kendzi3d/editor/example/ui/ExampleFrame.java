package kendzi.kendzi3d.editor.example.ui;

import com.jogamp.opengl.GLEventListener;

import javax.inject.Inject;
import kendzi.kendzi3d.editor.ui.BaseEditorFrame;

public class ExampleFrame extends BaseEditorFrame {

    /**
     * Listener for GL related draw events.
     */
    @Inject
    private ExampleEditorGLEventListener listener;

    /**
     * Constructor.
     */
    public ExampleFrame() {
        super("Example 3d editor");
    }

    @Override
    public GLEventListener getGlEventListener() {
        return listener;
    }
}
