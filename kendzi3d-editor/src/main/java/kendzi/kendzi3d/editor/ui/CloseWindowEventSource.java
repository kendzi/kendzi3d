package kendzi.kendzi3d.editor.ui;

import kendzi.kendzi3d.editor.ui.event.CloseWindowListener;

public interface CloseWindowEventSource {

    public abstract void addCloseWindowListener(CloseWindowListener listener);

    public abstract void removeCloseWindowListener(CloseWindowListener listener);

}