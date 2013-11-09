/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.math.geometry.point;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

/**
 * Vector 2d util.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public class Vector2dUtil {

    private static double EPSILON = 0.00000001;

    public static Vector2d orthogonal(Vector2d v) {
        // XXX rename to orthogonalLeft
        return new Vector2d(-v.y, v.x);
    }

    public static Vector2d ortagonalRight(Vector2d v) {
        // / XXX rename to orthogonalRight
        return new Vector2d(v.y, -v.x);
    }

    public static Vector2d bisector(Point2d p1, Point2d p2, Point2d p3) { // left
        // XXX rename to bisectorLeft
        return Vector2dUtil.bisector(Vector2dUtil.fromTo(p1, p2), Vector2dUtil.fromTo(p2, p3));
    }

    public static Vector2d bisector(Vector2d v1, Vector2d v2) {
        // XXX rename to bisectorLeft
        Vector2d norm1 = new Vector2d(v1);
        norm1.normalize();

        Vector2d norm2 = new Vector2d(v2);
        norm2.normalize();

        return bisectorNormalized(norm1, norm2);
    }

    public static Vector2d bisectorNormalized(Vector2d norm1, Vector2d norm2) {
        Vector2d e1v = orthogonal(norm1);
        Vector2d e2v = orthogonal(norm2);

        // 90 - 180 || 180 - 270
        // if (norm1.dot(e2v) <= 0 && ) { //XXX >= !!
        if (norm1.dot(norm2) > 0) {

            e1v.add(e2v);
            return e1v;

        }

        // 0 - 180
        Vector2d ret = new Vector2d(norm1);
        ret.negate();
        ret.add(norm2);

        if (e1v.dot(norm2) < 0) {
            // 270 - 360
            ret.negate();
        }
        return ret;
    }

    private static boolean equalsEpsilon(double pNumber) {
        if ((pNumber < 0 ? -pNumber : pNumber) > EPSILON) {
            return false;
        }
        return false;

    }

    /**
     * Cross product for 2d is same as doc
     * 
     * @param u
     * @param v
     * @return
     * @see {http://mathworld.wolfram.com/CrossProduct.html}
     */
    public static double cross(Tuple2d u, Tuple2d v) {
        return u.x * v.y - u.y * v.x;
    }

    public static Vector2d fromTo(Point2d begin, Point2d end) {
        return new Vector2d(end.x - begin.x, end.y - begin.y);
    }

    public static Vector2d negate(Vector2d vector) {
        return new Vector2d(-vector.x, -vector.y);
    }

}
