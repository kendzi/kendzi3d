package kendzi.kendzi3d.buildings.model;

import java.util.List;

import kendzi.kendzi3d.buildings.model.element.BuildingNodeElement;
import org.joml.Vector2dc;

public class WallNode {

    public WallNode() {
        //
    }

    public WallNode(Vector2dc point, List<BuildingNodeElement> buildingNodeElements) {
        super();
        this.point = point;
        this.buildingNodeElements = buildingNodeElements;
    }

    Vector2dc point;

    // powtazanie do gory
    List<BuildingNodeElement> buildingNodeElements;

    /**
     * @return the point
     */
    public Vector2dc getPoint() {
        return this.point;
    }

    /**
     * @param point
     *            the point to set
     */
    public void setPoint(Vector2dc point) {
        this.point = point;
    }

    /**
     * @return the buildingElements
     */
    public List<BuildingNodeElement> getBuildingNodeElements() {
        return this.buildingNodeElements;
    }

    /**
     * @param buildingElements
     *            the buildingElements to set
     */
    public void setBuildingNodeElements(List<BuildingNodeElement> buildingElements) {
        this.buildingNodeElements = buildingElements;
    }

}
