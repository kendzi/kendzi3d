/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry;

import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

import kendzi.math.geometry.rectangle.RectanglePointVector2d;

import org.apache.log4j.Logger;


/**
 * Basic function on rectangles.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class RectangleUtil {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RectangleUtil.class);


    /**
     * For given direction vector, calculate smallest rectangle with all points
     * in side.
     * 
     * @param points set of points
     * @param direction direction vector
     * @return smallest rectangle for given direction
     */
    public static RectanglePointVector2d findRectangleContur(List<Point2d> points, Vector2d direction) {

        Vector2d vector = new Vector2d(direction);
        vector.normalize();

        Vector2d orthogonal = new Vector2d(-vector.y, vector.x);

        double minVector = Double.MAX_VALUE;
        double maxVector = -Double.MAX_VALUE;
        double minOrtagonal = Double.MAX_VALUE;
        double maxOrtagonal = -Double.MAX_VALUE;

        for (Point2d point : points) {

            double dot = dot(vector, point);
            if (dot < minVector) {
                minVector = dot;
            }
            if (dot > maxVector) {
                maxVector = dot;
            }

            dot = dot(orthogonal, point);
            if (dot < minOrtagonal) {
                minOrtagonal = dot;
            }
            if (dot > maxOrtagonal) {
                maxOrtagonal = dot;
            }
        }

        double height = maxOrtagonal - minOrtagonal;
        double width = maxVector - minVector;

        Point2d point = new Point2d(
                vector.x * minVector + orthogonal.x * minOrtagonal,
                vector.y * minVector + orthogonal.y * minOrtagonal
                );

        return new RectanglePointVector2d(width, height, point, vector, true);
    }

    /**
     * Computes the dot product of the v1 vector and vector v2. Parameter can be
     * point then vector will start from origin and to point.
     * 
     * @param v1 first vector
     * @param v2 second vector
     * @return dot product
     * XXX
     */
    private final static double dot(Tuple2d v1, Tuple2d v2) {
        return v1.x*v2.x + v1.y*v2.y;
    }

    /**
     * Finds minimal area rectangle containing set of points.
     * 
     * @param points set of points
     * @return vertex of rectangle or null if less then 3 points
     */
    public static RectanglePointVector2d findRectangleContur(List<Point2d> points) {

        List<Point2d> graham = Graham.grahamScan(points);

        double smalestArea = Double.MAX_VALUE;
        RectanglePointVector2d smalestRectangle = null;

        Point2d begin = graham.get(graham.size() - 1);
        for (Point2d end : graham) {

            Vector2d direction = new Vector2d(end);
            direction.sub(begin);

            RectanglePointVector2d rectangleContur = findRectangleContur(graham, direction);

            double area = rectangleContur.getHeight() * rectangleContur.getWidth();
            if (area < smalestArea) {
                smalestArea = area;
                smalestRectangle = rectangleContur;
            }

            begin = end;
        }

        return smalestRectangle;
    }
}
