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

    private final List<EditorChangeListener> editorChangeListeners = new LinkedList<>();
    private final List<SelectEditorListener> selectEditorListeners = new LinkedList<>();
    private final List<SelectionChangeListener> selectionChangeListeners = new LinkedList<>();

    public final void addEditorChangeListener(EditorChangeListener listener) {
        this.editorChangeListeners.add(listener);
    }

    public final void removeEditorChangeListener(EditorChangeListener listener) {
        this.editorChangeListeners.remove(listener);
    }

    protected void raiseEditorChange(EditorChangeEvent event) {
        for (EditorChangeListener listener : this.editorChangeListeners) {
            listener.onEditorChange(event);
        }
    }

    public interface EditorChangeListener extends EventListener {
        void onEditorChange(EditorChangeEvent event);
    }

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

    public final void addSelectionChangeListener(SelectionChangeListener listener) {
        this.selectionChangeListeners.add(listener);
    }

    public final void removeSelectionChangeListener(SelectionChangeListener listener) {
        this.selectionChangeListeners.remove(listener);
    }

    protected void raiseSelectionChange(SelectionChangeEvent event) {
        for (SelectionChangeListener listener : this.selectionChangeListeners) {
            listener.onSelectionChange(event);
        }
    }

    public interface SelectEditorListener extends EventListener {
        void onSelectEditor(SelectEditorEvent args);
    }

    public interface SelectionChangeListener extends EventListener {
        void onSelectionChange(SelectionChangeEvent args);
    }

}
