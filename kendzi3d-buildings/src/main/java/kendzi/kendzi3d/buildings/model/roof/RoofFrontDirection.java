package kendzi.kendzi3d.buildings.model.roof;

import org.joml.Vector2d;
import org.joml.Vector2dc;

public class RoofFrontDirection {

    private Vector2dc direction;

    private boolean soft;

    public RoofFrontDirection() {
        this(new Vector2d(), false);
    }

    public RoofFrontDirection(Vector2dc direction, boolean soft) {
        super();
        this.direction = direction;
        this.soft = soft;
    }

    /**
     * @return the direction
     */
    public Vector2dc getDirection() {
        return this.direction;
    }

    /**
     * @param direction
     *            the direction to set
     */
    public void setDirection(Vector2dc direction) {
        this.direction = direction;
    }

    /**
     * @return the soft
     */
    public boolean isSoft() {
        return this.soft;
    }

    /**
     * @param soft
     *            the soft to set
     */
    public void setSoft(boolean soft) {
        this.soft = soft;
    }

}
