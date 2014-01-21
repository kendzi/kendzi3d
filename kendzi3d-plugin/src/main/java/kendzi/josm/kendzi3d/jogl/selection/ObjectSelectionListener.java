/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.selection;

import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

import kendzi.josm.kendzi3d.jogl.selection.event.EditorChangeEvent;
import kendzi.josm.kendzi3d.jogl.selection.event.SelectEditorEvent;

public abstract class ObjectSelectionListener extends MouseSelectionListener {
    private final List<EditorChangeListener> editorChangeListeners = new LinkedList<EditorChangeListener>();

    public final void addEditorChangeListener(EditorChangeListener listener) {
        this.editorChangeListeners.add(listener);
    }

    public final void removeEditorChangeListener(EditorChangeListener listener) {
        this.editorChangeListeners.remove(listener);
    }

    protected void raiseEditorChange(EditorChangeEvent args) {
        for (EditorChangeListener listener : this.editorChangeListeners) {
            listener.onEditorChange(args);
        }
    }

    public interface EditorChangeListener extends EventListener {
        public void onEditorChange(EditorChangeEvent args);
    }

    private final List<SelectEditorListener> selectEditorListeners = new LinkedList<SelectEditorListener>();

    public final void addSelectEditorListener(SelectEditorListener listener) {
        this.selectEditorListeners.add(listener);
    }

    public final void removeSelectEditorListener(SelectEditorListener listener) {
        this.selectEditorListeners.remove(listener);
    }

    protected void raiseSelectEditor(SelectEditorEvent args) {
        for (SelectEditorListener listener : this.selectEditorListeners) {
            listener.onSelectEditor(args);
        }
    }

    public interface SelectEditorListener extends EventListener {
        public void onSelectEditor(SelectEditorEvent args);
    }

}
