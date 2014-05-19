package kendzi.kendzi3d.world;

import kendzi.jogl.model.geometry.Model;

/**
 * Object in world represents by single model.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public interface StaticModelWorldObject extends WorldObject {

    /**
     * Gets static model for world object.
     * 
     * @return static model
     */
    Model getModel();
}
