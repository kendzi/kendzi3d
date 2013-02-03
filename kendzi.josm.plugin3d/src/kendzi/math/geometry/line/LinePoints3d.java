/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.line;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

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
public class LinePoints3d {
    /**
     * Start point A. XXX rename to A ?
     */
    Point3d p1;
    /**
     * End point B. XXX rename to B ?
     */
    Point3d p2;

    public LinePoints3d(Point3d p1, Point3d p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point3d getP1() {
        return this.p1;
    }

    public void setP1(Point3d p1) {
        this.p1 = p1;
    }

    public Point3d getP2() {
        return this.p2;
    }

    public void setP2(Point3d p2) {
        this.p2 = p2;
    }

    /**
     * Starting Point A from parametric description.
     *
     * @return starting point A.
     */
    Point3d getPointA() {
        return this.p1;
    }

    /**
     * Direction vector U from parametric description.
     *
     * @return direction vector U.
     */
    Vector3d getVectorU() {
        Vector3d u = new Vector3d(this.p2);
        u.sub(this.p1);
        return u;
    }




}
