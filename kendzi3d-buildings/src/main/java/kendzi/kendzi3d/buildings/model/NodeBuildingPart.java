package kendzi.kendzi3d.buildings.model;

import java.awt.Color;

import javax.vecmath.Point2d;

public interface NodeBuildingPart {

    /**
     * @return the point
     */
    public abstract Point2d getPoint();

    /**
     * @return the height
     */
    public abstract double getHeight();

    public abstract String getFacadeMaterialType();

    public abstract Color getFacadeColor();

}
