/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry;

import javax.vecmath.Point2d;

public class Quadrilateral2d {
    Point2d p1;
    Point2d p2;
    Point2d p3;
    Point2d p4;

    /**
     * @return the p1
     */
    public Point2d getP1() {
        return this.p1;
    }
    /**
     * @param p1 the p1 to set
     */
    public void setP1(Point2d p1) {
        this.p1 = p1;
    }
    /**
     * @return the p2
     */
    public Point2d getP2() {
        return this.p2;
    }
    /**
     * @param p2 the p2 to set
     */
    public void setP2(Point2d p2) {
        this.p2 = p2;
    }
    /**
     * @return the p3
     */
    public Point2d getP3() {
        return this.p3;
    }
    /**
     * @param p3 the p3 to set
     */
    public void setP3(Point2d p3) {
        this.p3 = p3;
    }
    /**
     * @return the p4
     */
    public Point2d getP4() {
        return this.p4;
    }
    /**
     * @param p4 the p4 to set
     */
    public void setP4(Point2d p4) {
        this.p4 = p4;
    }
}
