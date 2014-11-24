package kendzi.kendzi3d.editor.selection.editor;

import kendzi.kendzi3d.editor.selection.listener.ObjectSelectionListener.EditorChangeListener;

public interface ChangeAwareEditor {

    public void addChangeListener(EditorChangeListener listener);

}
