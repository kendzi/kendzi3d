package kendzi.kendzi3d.editor.ui;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import kendzi.jogl.camera.SimpleMoveAnimator;
import kendzi.jogl.camera.Viewport;
import kendzi.jogl.camera.ViewportUtil;
import kendzi.jogl.util.ColorUtil;
import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.EditorCore;
import kendzi.kendzi3d.editor.drawer.SelectionDrawer;
import kendzi.kendzi3d.editor.drawer.SquareIcon;
import kendzi.kendzi3d.editor.example.objects.Box;
import kendzi.kendzi3d.editor.selection.ObjectSelectionManager;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.editor.selection.ViewportProvider;
import kendzi.kendzi3d.editor.ui.event.CloseWindowListener;
import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class BaseEditorGLEventListener implements GLEventListener, ViewportProvider, CloseWindowEventSource {

    private static final Logger LOG = Logger.getLogger(Box.class);

    @Inject
    private Viewport viewport;

    @Inject
    private SimpleMoveAnimator camera;

    @Inject
    private ObjectSelectionManager objectSelectionListener;

    @Inject
    private EditorCore core;

    @Inject
    private SquareIcon squareIcon;

    private final SelectionDrawer selectionDrawer = new SelectionDrawer();

    private boolean windowClosed;

    @Override
    public void init(GLAutoDrawable drawable) {
        org.lwjgl.opengl.GL.createCapabilities();

        GL2 gl = drawable.getGL().getGL2();

        if (!checkRequiredExtensions(gl)) {
            LOG.error("can't find required openGl extensions closing window");
            windowClosed = true;
            fireCloseWindow();
        }

        // Enable VSync.
        gl.setSwapInterval(1);

        // Clear z-buffer.
        GL11.glClearDepth(1.0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // Enable z-buffer.
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        // Background color.
        GL11.glClearColor(0.17f, 0.65f, 0.92f, 0.0f);

        // Smooth shade model.
        GL11.glShadeModel(GL11.GL_SMOOTH);

        // Enable Antialiasing for lines.
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        // Set Line Antialiasing.
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        // Enable Blending.
        GL11.glEnable(GL11.GL_BLEND);

        // Type Of Blending.
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Adds light for screen.
        addLight();

        selectionDrawer.init(gl);

    }

    /**
     * Set up a point source with ambient, diffuse, and specular color. components.
     *
     */
    private void addLight() {

        // Put light in model view.
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Enable lighting.
        GL11.glEnable(GL11.GL_LIGHTING);

        // Enable a single light source.
        GL11.glEnable(GL11.GL_LIGHT0);

        // Weak gray ambient.
        float[] grayLight = { 0.5f, 0.5f, 0.5f, 1.0f };
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, grayLight);

        // Bright white diffuse & specular.
        float[] whiteLight = { 1.0f, 1.0f, 1.0f, 1.0f };
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, whiteLight);
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, whiteLight);

        // Position of light source on top right front.
        float[] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPos);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        //
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        if (windowClosed) {
            /*
             * Window should be closed. E.g. required extensions are not available. Skip
             * rendering because it will fail.
             */
            return;
        }

        // Animate position of camera depending on mouse or keyboard input.
        camera.updateState();

        /*
         * Update viewport using current camera position. View port will store
         * information required to setup OpenGl model view matrix. Calculates parameters
         * of viewport required to back trace click of mouse from 2d space into 3d
         * space.
         */
        viewport.updateViewport(camera);

        // Gl 2 context.
        GL2 gl = drawable.getGL().getGL2();

        // Clear color and depth buffers.
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // Draw before camera is set.
        drawBeforeSetCamera(gl, viewport);

        // Sets new view matrix.
        ViewportUtil.lookAt(viewport);

        drawBeforeEditorObjects(gl, viewport);

        drawEditorObjects(gl, viewport);

        drawAfterEditorObjects(gl, viewport);

        { // XXX remove!

            GL11.glColor3fv(ColorUtil.colorToArray(new Color(0.0f, 0.5f, 0.1f)));

        }
        drawSelection(gl);

        GL11.glFlush();
    }

    protected void drawBeforeSetCamera(GL2 gl, Viewport viewport) {
        //
    }

    protected void drawBeforeEditorObjects(GL2 gl, Viewport viewport) {
        //
    }

    protected void drawAfterEditorObjects(GL2 gl, Viewport viewport) {
        //
    }

    private void drawSelection(GL2 gl) {

        Selection lastSelection = objectSelectionListener.getLastSelection();
        if (lastSelection != null) {
            squareIcon.addIcon("/textures/pd/selection.png");

            Object editableObject = lastSelection.getSource();

            drawHighlightEditorObject(gl, editableObject);

            selectionDrawer.draw(lastSelection, objectSelectionListener.getLastActiveEditor(),
                    objectSelectionListener.getLastHighlightedEditor(), viewport);

            // selectionDrawer.drawEditors(gl, lastSelection.getEditors(),
            // objectSelectionListener.getLastActiveEditor(),
            // objectSelectionListener.getLastHighlightedEditor(), viewport);
        } else {
            squareIcon.addIcon(null);
        }

        squareIcon.draw(gl);
    }

    protected void drawHighlightEditorObject(GL2 gl, Object editableObject) {
        throw new IllegalStateException("unsupported editor object: " + editableObject);
    }

    protected void drawEditorObjects(GL2 gl, Viewport viewport) {

        try {
            List<EditableObject> editableObjects = core.getEditableObjects();

            for (EditableObject editableObject : editableObjects) {
                drawEditorObject(gl, editableObject, viewport);
            }
        } catch (Exception e) {
            LOG.error("can't draw editor objects", e);
        }
    }

    protected void drawEditorObject(GL2 gl, EditableObject editableObject, Viewport viewport) {
        throw new IllegalStateException("unsupported editor object: " + editableObject);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        GL2 gl = drawable.getGL().getGL2();

        // Avoid a divide by zero error!
        if (height <= 0) {
            height = 1;
        }

        // Re-setup viewport.
        viewport.reshape(width, height, camera);

        // Re-setup opengl perspective.
        ViewportUtil.reshapePerspective(viewport);
    }

    @Override
    public Viewport getViewport() {
        return viewport;
    }

    /**
     * When close window event is requested.
     */
    private final List<CloseWindowListener> closeWindowListeners = new ArrayList<>();

    @Override
    public void addCloseWindowListener(CloseWindowListener listener) {
        closeWindowListeners.add(listener);
    }

    @Override
    public void removeCloseWindowListener(CloseWindowListener listener) {
        closeWindowListeners.remove(listener);
    }

    protected void fireCloseWindow() {
        for (CloseWindowListener closeWindowListener : closeWindowListeners) {
            closeWindowListener.closeWindow();
        }
    }

    protected boolean checkRequiredExtensions(GL2 gl) {
        return true;
    }
}
