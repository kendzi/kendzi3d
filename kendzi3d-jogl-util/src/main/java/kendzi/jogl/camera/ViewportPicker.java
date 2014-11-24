package kendzi.jogl.camera;

import kendzi.math.geometry.ray.Ray3d;

/**
 * Interface for translation 2d mouse coordinates in window to 3d ray inside 3d
 * screen.
 */
public interface ViewportPicker {

    /**
     * Translates 2d mouse coordinates in window space into 3d ray inside 3d
     * screen. Ray position and direction depends on mouse location and current
     * viewport settings.
     *
     * @param x
     *            mouse x location in window space
     * @param y
     *            mouse y location in window space
     * @return ray in 3d space of current viewport
     */
    Ray3d picking(float x, float y);
}
