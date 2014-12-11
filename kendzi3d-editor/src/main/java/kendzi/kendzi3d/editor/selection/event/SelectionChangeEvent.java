/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.editor.selection.event;

import kendzi.kendzi3d.editor.selection.Selection;

public class SelectionChangeEvent {
    Selection selection;

    public SelectionChangeEvent(Selection selection) {
        this.selection = selection;
    }

    public Selection getSelection() {
        return this.selection;
    }
}
