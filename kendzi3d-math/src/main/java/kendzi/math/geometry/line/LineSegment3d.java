package kendzi.math.geometry.line;

import javax.vecmath.Point3d;

public class LineSegment3d {

    Point3d begin;
    Point3d end;

    public LineSegment3d(Point3d begin, Point3d end) {
        super();
        this.begin = begin;
        this.end = end;
    }

    /**
     * @return the begin
     */
    public Point3d getBegin() {
        return begin;
    }

    /**
     * @param begin the begin to set
     */
    public void setBegin(Point3d begin) {
        this.begin = begin;
    }

    /**
     * @return the end
     */
    public Point3d getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Point3d end) {
        this.end = end;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LineSegment2d " + "(" + begin + ", " + end + ")";
    }
}

