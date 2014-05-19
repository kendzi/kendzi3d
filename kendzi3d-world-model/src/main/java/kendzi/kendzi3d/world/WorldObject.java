package kendzi.kendzi3d.world;

import javax.vecmath.Point3d;

/**
 * Represents object in world.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public interface WorldObject {

    /**
     * Gets object location in world.
     * 
     * @return the point
     */
    Point3d getPoint();

    /**
     * Sets object location in world.
     * 
     * @param point
     *            the point to set
     */
    void setPoint(Point3d point);
}
