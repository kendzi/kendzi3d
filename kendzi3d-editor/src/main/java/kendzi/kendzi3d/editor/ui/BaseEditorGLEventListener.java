package kendzi.kendzi3d.editor.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

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

    private boolean windowClosed = false;

    @Override
    public void init(GLAutoDrawable drawable) {

        GL2 gl = drawable.getGL().getGL2();

        if (!checkRequiredExtensions(gl)) {
            LOG.error("can't find required openGl extensions closing window");
            windowClosed = true;
            fireCloseWindow();
        }

        // Enable VSync.
        gl.setSwapInterval(1);

        // Clear z-buffer.
        gl.glClearDepth(1.0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Enable z-buffer.
        gl.glEnable(GL.GL_DEPTH_TEST);

        // Background color.
        gl.glClearColor(0.17f, 0.65f, 0.92f, 0.0f);

        // Smooth shade model.
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);

        // Enable Antialiasing for lines.
        gl.glEnable(GL.GL_LINE_SMOOTH);

        // Set Line Antialiasing.
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);

        // Enable Blending.
        gl.glEnable(GL.GL_BLEND);

        // Type Of Blending.
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        // Adds light for screen.
        addLight(gl);

        selectionDrawer.init(gl);

    }

    /**
     * Set up a point source with ambient, diffuse, and specular color.
     * components.
     *
     * @param gl
     *            gl
     */
    private void addLight(GL2 gl) {

        // Put light in model view.
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

        // Enable lighting.
        gl.glEnable(GLLightingFunc.GL_LIGHTING);

        // Enable a single light source.
        gl.glEnable(GLLightingFunc.GL_LIGHT0);

        // Weak gray ambient.
        float[] grayLight = { 0.5f, 0.5f, 0.5f, 1.0f };
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, grayLight, 0);

        // Bright white diffuse & specular.
        float[] whiteLight = { 1.0f, 1.0f, 1.0f, 1.0f };
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, whiteLight, 0);
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, whiteLight, 0);

        // Position of light source on top right front.
        float[] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightPos, 0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        //
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        if (windowClosed) {
            /*
             * Window should be closed. E.g. required extensions are not
             * available. Skip rendering because it will fail.
             */
            return;
        }

        // Animate position of camera depending on mouse or keyboard input.
        camera.updateState();

        /*
         * Update viewport using current camera position. View port will store
         * information required to setup OpenGl model view matrix. Calculates
         * parameters of viewport required to back trace click of mouse from 2d
         * space into 3d space.
         */
        viewport.updateViewport(camera);

        // Gl 2 context.
        GL2 gl = drawable.getGL().getGL2();

        // Clear color and depth buffers.
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Draw before camera is set.
        drawBeforeSetCamera(gl, viewport);

        // Sets new view matrix.
        ViewportUtil.lookAt(gl, viewport);

        drawBeforeEditorObjects(gl, viewport);

        drawEditorObjects(gl, viewport);

        drawAfterEditorObjects(gl, viewport);

        { // XXX remove!

            gl.glColor3fv(ColorUtil.colorToArray(new Color(0.0f, 0.5f, 0.1f)), 0);

        }
        drawSelection(gl);

        gl.glFlush();
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

            selectionDrawer.draw(gl, lastSelection, objectSelectionListener.getLastActiveEditor(),
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
        ViewportUtil.reshapePerspective(viewport, gl);
    }

    @Override
    public Viewport getViewport() {
        return viewport;
    }

    /**
     * When close window event is requested.
     */
    private final List<CloseWindowListener> closeWindowListeners = new ArrayList<CloseWindowListener>();

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
