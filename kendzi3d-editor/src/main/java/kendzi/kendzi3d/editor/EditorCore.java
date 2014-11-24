package kendzi.kendzi3d.editor;

import kendzi.jogl.camera.Camera;

public interface EditorCore extends EditableObjectProvider {

    /**
     * Gets viewport position for editor.
     * 
     * @return viewport position
     */
    Camera getCamera();

}
