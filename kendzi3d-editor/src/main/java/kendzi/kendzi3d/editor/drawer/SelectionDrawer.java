package kendzi.kendzi3d.editor.drawer;

import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import kendzi.jogl.camera.Viewport;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.editor.selection.editor.ArrowEditor;
import kendzi.kendzi3d.editor.selection.editor.Editor;

public class SelectionDrawer {

    /* Storage For Our Quadratic Objects. */
    private GLUquadric quadratic;

    private final GLU glu = new GLU();

    private final ActiveSpotDrawer activeSpotDrawer = new ActiveSpotDrawer();
    private final ArrowEditorDrawer arrowEditorDrawer = new ArrowEditorDrawer();

    public void init(GL2 gl) {
        // Quadric for geometry
        quadratic = glu.gluNewQuadric();
        // Create smooth normals quadric
        glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH);

        activeSpotDrawer.init(gl);
        arrowEditorDrawer.init(gl);
    }

    private void drawSelectedObject(Selection lastSelection, GL2 gl) {

        // TODO Auto-generated method stub

    }

    public void drawEditors(GL2 gl, List<Editor> editors, Editor activeEditor, Editor highlightedEditor, Viewport viewport) {

        if (editors == null) {
            return;
        }

        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_DEPTH_TEST);

        for (Editor editor : editors) {
            boolean isActiveEditor = editor.equals(activeEditor);
            boolean isHighlightedEditor = editor.equals(highlightedEditor);

            if (editor instanceof ArrowEditor) {
                ArrowEditor ae = (ArrowEditor) editor;
                arrowEditorDrawer.draw(gl, ae, isActiveEditor, isHighlightedEditor, viewport);
            }
        }
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    public void draw(GL2 gl, Selection lastSelection, Editor lastActiveEditor, Editor lastHighlightedEditor, Viewport viewport) {

        drawSelectedObject(lastSelection, gl);

        drawEditors(gl, lastSelection.getEditors(), lastActiveEditor, lastHighlightedEditor, viewport);

    }
}
