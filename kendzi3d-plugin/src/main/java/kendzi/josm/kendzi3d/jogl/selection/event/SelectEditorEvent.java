/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
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
