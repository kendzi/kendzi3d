package kendzi.kendzi3d.buildings.builder.height;

import javax.vecmath.Point2d;

import kendzi.math.geometry.line.LineSegment2d;

public class SegmentHeight extends LineSegment2d {

    private double beginHeight;
    private double endHeight;

    public SegmentHeight(Point2d begin, double beginHeight, Point2d end, double endHeight) {
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
     * @param beginHeight the beginHeight to set
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
     * @param endHeight the endHeight to set
     */
    public void setEndHeight(double endHeight) {
        this.endHeight = endHeight;
    }



}

