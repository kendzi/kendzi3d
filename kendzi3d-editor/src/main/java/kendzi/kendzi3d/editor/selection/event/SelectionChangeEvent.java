/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.editor.selection.event;

import kendzi.kendzi3d.editor.selection.Selection;

/**
 * Selection change inside 3d view.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class SelectionChangeEvent {

    /**
     * New selection.
     */
    private Selection selection;

    /**
     * Source of selection change.
     */
    private SelectionEventSource selectionSource;

    /**
     * Selection change event.
     *
     * @param selection
     *            selection
     * @param selectionSource
     *            source of selection change
     */
    public SelectionChangeEvent(Selection selection, SelectionEventSource selectionSource) {
        this.selection = selection;
        this.selectionSource = selectionSource;
    }

    /**
     * @return the selection
     */
    public Selection getSelection() {
        return selection;
    }

    /**
     * @return the selectionSource
     */
    public SelectionEventSource getSelectionSource() {
        return selectionSource;
    }
}
