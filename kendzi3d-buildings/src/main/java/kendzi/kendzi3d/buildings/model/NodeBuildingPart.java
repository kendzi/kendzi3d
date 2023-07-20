package kendzi.kendzi3d.buildings.model;

import java.awt.Color;

import org.joml.Vector2dc;

public interface NodeBuildingPart {

    /**
     * @return the point
     */
    Vector2dc getPoint();

    /**
     * @return the height
     */
    double getHeight();

    String getFacadeMaterialType();

    Color getFacadeColor();

}
