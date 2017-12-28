/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.editor.selection.listener;

import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

import kendzi.kendzi3d.editor.selection.event.EditorChangeEvent;
import kendzi.kendzi3d.editor.selection.event.SelectEditorEvent;
import kendzi.kendzi3d.editor.selection.event.SelectionChangeEvent;

public abstract class ObjectSelectionListener extends MouseSelectionListener {

    private final List<EditorChangeListener> editorChangeListeners = new LinkedList<EditorChangeListener>();
    private final List<SelectEditorListener> selectEditorListeners = new LinkedList<SelectEditorListener>();
    private final List<SelectionChangeListener> selectionChangeListeners = new LinkedList<SelectionChangeListener>();

    public final void addEditorChangeListener(EditorChangeListener listener) {
        editorChangeListeners.add(listener);
    }

    public final void removeEditorChangeListener(EditorChangeListener listener) {
        editorChangeListeners.remove(listener);
    }

    protected boolean raiseEditorChange(EditorChangeEvent event) {
        for (EditorChangeListener listener : editorChangeListeners) {
            listener.onEditorChange(event);
        }

        return !editorChangeListeners.isEmpty();
    }

    public interface EditorChangeListener extends EventListener {
        public void onEditorChange(EditorChangeEvent event);
    }

    public final void addSelectEditorListener(SelectEditorListener listener) {
        selectEditorListeners.add(listener);
    }

    public final void removeSelectEditorListener(SelectEditorListener listener) {
        selectEditorListeners.remove(listener);
    }

    protected boolean raiseSelectEditor(SelectEditorEvent args) {
        for (SelectEditorListener listener : selectEditorListeners) {
            listener.onSelectEditor(args);
        }

        return !selectEditorListeners.isEmpty();
    }

    public final void addSelectionChangeListener(SelectionChangeListener listener) {
        selectionChangeListeners.add(listener);
    }

    public final void removeSelectionChangeListener(SelectionChangeListener listener) {
        selectionChangeListeners.remove(listener);
    }

    protected boolean raiseSelectionChange(SelectionChangeEvent event) {
        for (SelectionChangeListener listener : selectionChangeListeners) {
            listener.onSelectionChange(event);
        }

        return !selectionChangeListeners.isEmpty();
    }

    public interface SelectEditorListener extends EventListener {
        public void onSelectEditor(SelectEditorEvent args);
    }

    public interface SelectionChangeListener extends EventListener {
        public void onSelectionChange(SelectionChangeEvent args);
    }

}
