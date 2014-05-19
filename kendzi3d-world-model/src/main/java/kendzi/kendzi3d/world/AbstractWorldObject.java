package kendzi.kendzi3d.world;

import javax.vecmath.Point3d;

import kendzi.jogl.model.geometry.Model;

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
    private Point3d point;

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
    public Point3d getPoint() {
        return point;
    }

    /**
     * @param point
     *            the point to set
     */
    @Override
    public void setPoint(Point3d point) {
        this.point = point;
    }

}
