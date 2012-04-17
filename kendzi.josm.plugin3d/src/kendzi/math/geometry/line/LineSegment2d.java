package kendzi.math.geometry.line;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class LineSegment2d {

    Point2d begin;
    Point2d end;

    /**
     * XXX is need ?
     */
    boolean openBegin;
    /**
     * XXX is need ?
     */
    boolean openEnd;

    public LineSegment2d(Point2d begin, Point2d end) {
        super();
        this.begin = begin;
        this.end = end;
    }

    /** Collision point of line and line segment.
     * @param x1 line segment begin point x
     * @param y1 line segment begin point y
     * @param x2 line segment end point x
     * @param y2 line segment end point y
     *
     * @param A line in linear form parameter A
     * @param B line in linear form parameter B
     * @param C line in linear form parameter C
     * @return collision point
     */
    public static Point2d collide(double x1, double y1, double x2, double y2, double A, double B, double C
            ) {

        // XXX TODO FIXME when end of line segment is laying on line

        if (det(x1, y1, A, B, C) * det(x2, y2, A, B, C) < 0) {

            double A2 = y1 - y2;
            double B2 = x2 - x1;
            double C2 = x1 * y2 - x2 * y1;

            return LineLinear2d.collide(A, B, C, A2, B2, C2);
        }
        return null;
    }

    private static double det(double x, double y, double A, double B, double C) {
        return A * x + B * y + C;
    }

    /**
     * @return the begin
     */
    public Point2d getBegin() {
        return begin;
    }

    /**
     * @param begin the begin to set
     */
    public void setBegin(Point2d begin) {
        this.begin = begin;
    }

    /**
     * @return the end
     */
    public Point2d getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Point2d end) {
        this.end = end;
    }

    /**
     * @return the openBegin
     */
    public boolean isOpenBegin() {
        return openBegin;
    }

    /**
     * @param openBegin the openBegin to set
     */
    public void setOpenBegin(boolean openBegin) {
        this.openBegin = openBegin;
    }

    /**
     * @return the openEnd
     */
    public boolean isOpenEnd() {
        return openEnd;
    }

    /**
     * @param openEnd the openEnd to set
     */
    public void setOpenEnd(boolean openEnd) {
        this.openEnd = openEnd;
    }


 // dist_Point_to_Segment(): get the distance of a point to a segment.
//  Input:  a Point P and a Segment S (in any dimension)
//  Return: the shortest distance from P to S

    //http://softsurfer.com/Archive/algorithm_0102/algorithm_0102.htm#References
    public static double distancePointToSegment(Point2d P, LineSegment2d S) {
//        Vector v = S.P1 - S.P0;
//        Vector w = P - S.P0;

        Vector2d v = new Vector2d(S.getEnd());
        v.sub(S.getBegin());

        Vector2d w = new Vector2d(P);
        w.sub(S.getBegin());


        double c1 = w.dot(v);//dot(w,v);
        if ( c1 <= 0 ) {
            return d(P, S.getBegin());
        }

        double c2 = v.dot(v);//dot(v,v);
        if ( c2 <= c1 ) {
            return d(P, S.getEnd());
        }

        double b = c1 / c2;

        //Pb = S.P0 + b * v;
        Point2d Pb = new Point2d(v);
        Pb.scale(b);
        Pb.add(S.getBegin());

        return d(P, Pb);
    }

    private static double d(Point2d u, Point2d v) {
        double dx = u.x - v.x;
        double dy = u.y - v.y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LineSegment2d "
                + (openBegin ? "(" : "<")
                + begin
                + ", "
                + end
                + (openEnd ? ")" : ">")
               ;
    }


}

