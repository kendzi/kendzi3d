/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import javax.media.opengl.GL2;

import kendzi.jogl.camera.Camera;

/**
 *
 * Additional debug information for world object which should be display.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public interface WorldObjectDebugDrawable {

    /**
     * Draw debug information using openGl.
     *
     * @param gl
     *            openGl
     * @param camera
     *            camera
     */
    void drawDebug(GL2 gl, Camera camera);

}
