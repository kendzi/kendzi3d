package kendzi.josm.kendzi3d.jogl.selection.event;

import kendzi.josm.kendzi3d.jogl.selection.editor.Editor;

public class SelectEditorEvent {
    Editor editor;

    public SelectEditorEvent(Editor editor) {
        this.editor = editor;
    }

    public Editor getEditor() {
        return this.editor;
    }
}
