package kendzi.josm.kendzi3d.jogl.model.roof.mk.model;

import javax.vecmath.Vector2d;

public class RoofFrontDirection {

    private Vector2d direction;

    private boolean soft;

    public RoofFrontDirection() {
        this(new Vector2d(), false);
    }

    public RoofFrontDirection(Vector2d direction, boolean soft) {
        super();
        this.direction = direction;
        this.soft = soft;
    }

    /**
     * @return the direction
     */
    public Vector2d getDirection() {
        return this.direction;
    }
    /**
     * @param direction the direction to set
     */
    public void setDirection(Vector2d direction) {
        this.direction = direction;
    }
    /**
     * @return the soft
     */
    public boolean isSoft() {
        return this.soft;
    }
    /**
     * @param soft the soft to set
     */
    public void setSoft(boolean soft) {
        this.soft = soft;
    }


}
