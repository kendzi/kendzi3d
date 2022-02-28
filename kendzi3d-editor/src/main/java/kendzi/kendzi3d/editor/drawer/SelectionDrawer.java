package kendzi.kendzi3d.editor.drawer;

import java.util.List;

import kendzi.jogl.camera.Viewport;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.editor.selection.editor.ArrowEditor;
import kendzi.kendzi3d.editor.selection.editor.Editor;
import org.lwjgl.opengl.GL11;

public class SelectionDrawer {

    /* Storage For Our Quadratic Objects. */

    private final ActiveSpotDrawer activeSpotDrawer = new ActiveSpotDrawer();
    private final ArrowEditorDrawer arrowEditorDrawer = new ArrowEditorDrawer();

    public void init() {
        activeSpotDrawer.init();
        arrowEditorDrawer.init();
    }

    private void drawSelectedObject(Selection lastSelection) {

        // TODO Auto-generated method stub

    }

    public void drawEditors(List<Editor> editors, Editor activeEditor, Editor highlightedEditor, Viewport viewport) {

        if (editors == null) {
            return;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        for (Editor editor : editors) {
            boolean isActiveEditor = editor.equals(activeEditor);
            boolean isHighlightedEditor = editor.equals(highlightedEditor);

            if (editor instanceof ArrowEditor) {
                ArrowEditor ae = (ArrowEditor) editor;
                arrowEditorDrawer.draw(ae, isActiveEditor, isHighlightedEditor, viewport);
            }
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void draw(Selection lastSelection, Editor lastActiveEditor, Editor lastHighlightedEditor, Viewport viewport) {

        drawSelectedObject(lastSelection);

        drawEditors(lastSelection.getEditors(), lastActiveEditor, lastHighlightedEditor, viewport);

    }
}
