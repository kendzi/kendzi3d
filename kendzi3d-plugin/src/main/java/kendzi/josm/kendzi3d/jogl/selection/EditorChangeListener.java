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
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

public class EditorChangeListener {



    private final List<EditorChangeFinishListener> editorChangeFinishListeners = new LinkedList<EditorChangeFinishListener>();

    public final void addEditorChangeFinishListener(EditorChangeFinishListener listener) {
        this.editorChangeFinishListeners.add(listener);
    }

    public final void removeEditorChangeFinishListener(EditorChangeFinishListener listener) {
        this.editorChangeFinishListeners.remove(listener);
    }

    private void raiseEditorChangeFinish(EditorChangeFinishArgs args) {
        for (EditorChangeFinishListener listener : this.editorChangeFinishListeners)
            listener.onEditorChangeFinish(args);
    }

    public interface EditorChangeFinishListener extends EventListener {
        public void onEditorChangeFinish(EditorChangeFinishArgs args);
    }

    public class EditorChangeFinishArgs extends EventObject {
        public EditorChangeFinishArgs(Object source) {
            super(source);
        }
    }

    private final List<EditorChangeInProgresListener> editorChangeInProgresListeners = new LinkedList<EditorChangeInProgresListener>();

    public final void addEditorChangeInProgresListener(EditorChangeInProgresListener listener) {
        this.editorChangeInProgresListeners.add(listener);
    }

    public final void removeEditorChangeInProgresListener(EditorChangeInProgresListener listener) {
        this.editorChangeInProgresListeners.remove(listener);
    }

    private void raiseEditorChangeInProgres(EditorChangeInProgresArgs args) {
        for (EditorChangeInProgresListener listener : this.editorChangeInProgresListeners)
            listener.onEditorChangeInProgres(args);
    }

    public interface EditorChangeInProgresListener extends EventListener {
        public void onEditorChangeInProgres(EditorChangeInProgresArgs args);
    }

    public class EditorChangeInProgresArgs extends EventObject {
        public EditorChangeInProgresArgs(Object source) {
            super(source);
        }
    }

}
