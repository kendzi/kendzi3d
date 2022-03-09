package kendzi.kendzi3d.world;

import kendzi.kendzi3d.editor.EditableObject;
import org.joml.Vector3dc;

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
    Vector3dc getPoint();

    /**
     * Sets object location in world.
     *
     * @param point
     *            the point to set
     */
    void setPoint(Vector3dc point);
}
