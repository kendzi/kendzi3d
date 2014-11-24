package kendzi.kendzi3d.world;

import javax.vecmath.Point3d;

import kendzi.kendzi3d.editor.EditableObject;

/**
 * Represents object in world.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public interface WorldObject extends EditableObject {

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
