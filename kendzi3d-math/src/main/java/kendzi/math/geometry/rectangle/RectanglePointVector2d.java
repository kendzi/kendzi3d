/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.rectangle;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class RectanglePointVector2d {
    double width;
    double height;
    Point2d point;
    Vector2d vector;

    public RectanglePointVector2d(double width, double height, Point2d point, Vector2d vector) {
        this(width, height, point, vector, false);
    }

    public RectanglePointVector2d(double width, double height, Point2d point, Vector2d vector, boolean normalized) {
        super();
        this.width = width;
        this.height = height;
        this.point = new Point2d(point);
        this.vector = new Vector2d(vector);

        if (!normalized) {
            this.vector.normalize();
        }
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the point
     */
    public Point2d getPoint() {
        return point;
    }

    /**
     * @param point the point to set
     */
    public void setPoint(Point2d point) {
        this.point = point;
    }

    /**
     * @return the vector
     */
    public Vector2d getVector() {
        return vector;
    }

    /**
     * @param vector the vector to set
     */
    public void setVector(Vector2d vector) {
        this.vector = vector;
    }

//    RectanglePoint2d toRectanglePoint2d() {
//        RectanglePoint2d ret = new RectanglePoint2d();
////        ret.
//        return null;
//    }

//
//    //TODO
//    Point2d p1;
//    Point2d p2;
//    Point2d p3;
//    Point2d p4;

}
