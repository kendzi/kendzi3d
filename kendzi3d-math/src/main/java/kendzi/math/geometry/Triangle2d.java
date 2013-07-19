package kendzi.math.geometry;

import javax.vecmath.Point2d;

public class Triangle2d {

    private Point2d p1;

    private Point2d p2;

    private Point2d p3;

    public Triangle2d(Point2d p1, Point2d p2, Point2d p3) {
        super();
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Point2d center() {
        return new Point2d((this.p1.x + this.p2.x + this.p3.x) / 3d, (this.p1.y + this.p2.y + this.p3.y) / 3d);
    }

    /**
     * @return the p1
     */
    public Point2d getP1() {
        return this.p1;
    }

    /**
     * @return the p2
     */
    public Point2d getP2() {
        return this.p2;
    }

    /**
     * @return the p3
     */
    public Point2d getP3() {
        return this.p3;
    }


}
