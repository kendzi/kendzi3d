package kendzi.kendzi3d.buildings.builder.height;

import kendzi.math.geometry.line.LineSegment2d;
import org.joml.Vector2dc;

public class SegmentHeight extends LineSegment2d {

    private double beginHeight;
    private double endHeight;

    public SegmentHeight(Vector2dc begin, double beginHeight, Vector2dc end, double endHeight) {
        super(begin, end);

        this.beginHeight = beginHeight;
        this.endHeight = endHeight;
    }

    /**
     * @return the beginHeight
     */
    public double getBeginHeight() {
        return beginHeight;
    }

    /**
     * @param beginHeight
     *            the beginHeight to set
     */
    public void setBeginHeight(double beginHeight) {
        this.beginHeight = beginHeight;
    }

    /**
     * @return the endHeight
     */
    public double getEndHeight() {
        return endHeight;
    }

    /**
     * @param endHeight
     *            the endHeight to set
     */
    public void setEndHeight(double endHeight) {
        this.endHeight = endHeight;
    }

}
