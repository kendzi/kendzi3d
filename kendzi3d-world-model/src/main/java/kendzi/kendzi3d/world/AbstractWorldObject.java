package kendzi.kendzi3d.world;

import kendzi.jogl.model.geometry.Model;
import org.joml.Vector3dc;

/**
 * Represents object in world.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public abstract class AbstractWorldObject implements WorldObject, StaticModelWorldObject {

    /**
     * Object location inside world.
     */
    private Vector3dc point;

    /**
     * Gets static model for world object.
     * 
     * @return static model
     */
    @Override
    public abstract Model getModel();

    /**
     * @return the point
     */
    @Override
    public Vector3dc getPoint() {
        return point;
    }

    /**
     * @param point
     *            the point to set
     */
    @Override
    public void setPoint(Vector3dc point) {
        this.point = point;
    }

}
