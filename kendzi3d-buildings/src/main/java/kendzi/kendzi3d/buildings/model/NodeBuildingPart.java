package kendzi.kendzi3d.buildings.model;

import java.awt.Color;

import javax.vecmath.Point2d;

public interface NodeBuildingPart {

    /**
     * @return the point
     */
    Point2d getPoint();

    /**
     * @return the height
     */
    double getHeight();

    String getFacadeMaterialType();

    Color getFacadeColor();

}
