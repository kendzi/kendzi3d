package kendzi.kendzi3d.editor.ui;

import kendzi.kendzi3d.editor.ui.event.CloseWindowListener;

public interface CloseWindowEventSource {

    void addCloseWindowListener(CloseWindowListener listener);

    void removeCloseWindowListener(CloseWindowListener listener);

}