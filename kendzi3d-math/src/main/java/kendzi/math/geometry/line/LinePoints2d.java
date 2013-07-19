/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.line;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

/**
 *
 * Geometry line in two point form. Form:
 *
 * x = x_A + t(x_B - x_A) y = y_A + t(y_B - y_A)
 *
 * Similar to Parametric form.
 *
 * TODO
 *
 * @see "http://pl.wikipedia.org/wiki/Prosta"
 * @see "http://en.wikipedia.org/wiki/Linear_equation"
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class LinePoints2d {
    /**
     * Start point A. XXX rename to A ?
     */
    Point2d p1;
    /**
     * End point B. XXX rename to B ?
     */
    Point2d p2;

    public LinePoints2d(Point2d p1, Point2d p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point2d getP1() {
        return this.p1;
    }

    public void setP1(Point2d p1) {
        this.p1 = p1;
    }

    public Point2d getP2() {
        return this.p2;
    }

    public void setP2(Point2d p2) {
        this.p2 = p2;
    }

    /**
     * Starting Point A from parametric description.
     *
     * @return starting point A.
     */
    Point2d getPointA() {
        return this.p1;
    }

    /**
     * Direction vector U from parametric description.
     *
     * @return direction vector U.
     */
    Vector2d getVectorU() {
        Vector2d u = new Vector2d(this.p2);
        u.sub(this.p1);
        return u;
    }

    public LineParametric2d getLineParametric2d() {
        return new LineParametric2d(getPointA(), getVectorU());
    }


    /** Determinate if point is over line or on line.
     * @param pPoint point
     * @return point is over line or on line
     * TODO RENAME TO POINT_IN_FRONT
     */
    public boolean inFront(Tuple2d pPoint) {
        return LineUtil.matrixDet(this.p1, this.p2, pPoint) >= 0;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "L (" + this.p1 + ") -> (" + this.p2 + ")";
    }




}
