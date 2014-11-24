/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import javax.media.opengl.GL2;

import kendzi.jogl.camera.Camera;
import kendzi.kendzi3d.world.BuildableWorldObject;

/**
 * Model of OSM objects. Base interface for all objects. Support building and
 * drawing them.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 *         FIXME
 * @deprecated this interface need to be redesigned!
 * 
 */
@Deprecated
public interface DrawableModel extends BuildableWorldObject {

    /**
     * Object gravity center.
     * 
     * @return x middle of object
     */
    @Deprecated
    double getX();

    /**
     * Object gravity center.
     * 
     * @return y middle of object
     */
    @Deprecated
    double getY();

    /**
     * Size of object.
     * 
     * @return max distance from all vertex to middle point of object
     */
    double getRadius();

    /**
     * Draw using openGl.
     * 
     * @param gl
     *            openGl
     * @param camera
     *            camera
     */
    void draw(GL2 gl, Camera camera);

    /**
     * Draw using openGl. When object can be selectable.
     * 
     * @param gl
     *            openGl
     * @param camera
     *            camera
     * @param selected
     *            if object is selected
     */
    void draw(GL2 gl, Camera camera, boolean selected);

    /**
     * When error occurred in time of model building/rendering.
     * 
     * @return is error
     */
    boolean isError();

    /**
     * Set if error occurred.
     * 
     * @param error
     *            error
     */
    void setError(boolean error);

}
