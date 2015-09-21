package kendzi.kendzi3d.buildings.model;

import java.util.List;

import javax.vecmath.Point2d;

import kendzi.kendzi3d.buildings.model.element.BuildingNodeElement;

public class WallNode {

    public WallNode() {
        //
    }

    public WallNode(Point2d point, List<BuildingNodeElement> buildingNodeElements) {
        super();
        this.point = point;
        this.buildingNodeElements = buildingNodeElements;
    }

    Point2d point;

    // powtazanie do gory
    List<BuildingNodeElement> buildingNodeElements;

    /**
     * @return the point
     */
    public Point2d getPoint() {
        return this.point;
    }

    /**
     * @param point the point to set
     */
    public void setPoint(Point2d point) {
        this.point = point;
    }

    /**
     * @return the buildingElements
     */
    public List<BuildingNodeElement> getBuildingNodeElements() {
        return this.buildingNodeElements;
    }

    /**
     * @param buildingElements the buildingElements to set
     */
    public void setBuildingNodeElements(List<BuildingNodeElement> buildingElements) {
        this.buildingNodeElements = buildingElements;
    }


}
