package kendzi.kendzi3d.buildings.model.roof.lines;

import java.util.List;
import java.util.Map;

import kendzi.kendzi3d.buildings.model.roof.RoofModel;
import kendzi.math.geometry.line.LineSegment2d;
import org.joml.Vector2dc;

public class RoofLinesModel implements RoofModel {

    private Map<Vector2dc, Double> heights;

    private List<LineSegment2d> innerSegments;

    private double roofHeight;

    public RoofLinesModel(Map<Vector2dc, Double> heights, List<LineSegment2d> innerSegments, double roofHeight) {
        super();
        this.heights = heights;
        this.innerSegments = innerSegments;
        this.roofHeight = roofHeight;
    }

    /**
     * @return the heights
     */
    public Map<Vector2dc, Double> getHeights() {
        return this.heights;
    }

    /**
     * @param heights
     *            the heights to set
     */
    public void setHeights(Map<Vector2dc, Double> heights) {
        this.heights = heights;
    }

    /**
     * @return the innerSegments
     */
    public List<LineSegment2d> getInnerSegments() {
        return this.innerSegments;
    }

    /**
     * @param innerSegments
     *            the innerSegments to set
     */
    public void setInnerSegments(List<LineSegment2d> innerSegments) {
        this.innerSegments = innerSegments;
    }

    /**
     * @return the roofHeight
     */
    @Override
    public double getRoofHeight() {
        return roofHeight;
    }

    /**
     * @param roofHeight
     *            the roofHeight to set
     */
    public void setRoofHeight(double roofHeight) {
        this.roofHeight = roofHeight;
    }
}
