package kendzi.kendzi3d.editor.selection;

import kendzi.jogl.camera.Viewport;

/**
 * Interface for providing viewport.
 */
public interface ViewportProvider {

    /**
     * Provide current viewport information like size of window.
     *
     * @return viewport
     */
    Viewport getViewport();
}
