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

public class EditorChangeEvent {
    private boolean end;
    private Editor editor;

    public EditorChangeEvent(boolean end, Editor editor) {
        this.end = end;
        this.editor = editor;
    }

    /**
     * @return the end
     */
    public boolean isEnd() {
        return end;
    }

    /**
     * @param end
     *            the end to set
     */
    public void setEnd(boolean end) {
        this.end = end;
    }

    /**
     * @return the editor
     */
    public Editor getEditor() {
        return editor;
    }

    /**
     * @param editor
     *            the editor to set
     */
    public void setEditor(Editor editor) {
        this.editor = editor;
    }

}
