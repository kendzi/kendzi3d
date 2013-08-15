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
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.math.geometry.rectangle.RectanglePointVector2d;

import org.apache.log4j.Logger;


public class RectangleUtil {

    /** Log. */
    private static final Logger log = Logger.getLogger(RectangleUtil.class);

    /** Change order of points. Longer side of rectangle well by always first
     * @param points points
     * @return changed points if needed
     */
    public static Point2d[] longerSideFirst(Point2d[] points) {
        if (points == null || points.length < 4) {
            throw new IllegalArgumentException("only for Rectangle");
        }
        if (points[0].distanceSquared(points[1]) < points[1].distanceSquared(points[2])) {
            //			Double p = points[0];

            //			points[0] = points[1];
            //			points[1] = points[2];
            //			points[2] = points[3];
            //			points[3] = p;
            points = swapOnePoint(points);

        }
        return points;
    }

    /** Swap one point.
     * @param points list of points
     * @return swap by one list of points
     */
    public static Point2d[] swapOnePoint(Point2d[] points) {

        if (points.length < 2) {
            return points;
        }

        Point2d p = points[0];


        for (int i = 0; i < points.length - 1; i++) {
            points[i] = points[i + 1];

        }
        points[points.length - 1] = p;

        return points;
    }




    /**
     *  For given direction vector, calculate smallest rectangle with all points in side.
     * @param pPolygon
     * @param pDirection
     * @return
     */
    public static RectanglePointVector2d findRectangleContur(List<Point2d> points, Vector2d pDirection) {

        Vector2d vector = new Vector2d(pDirection);
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
     * Computes the dot product of the this vector and vector v1.
     * @param v vector
     * @param v1 the other vector (or if point vector starting ad origin and end in point)
     * @return dot product
     */
    public final static double dot(Vector2d v, Tuple2d v1) {
        return (v.x*v1.x + v.y*v1.y);
    }


    /** Finds minimal area rectangle containing set of points.
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
        //        log.info("new area: " + smalestRectangle.getHeight() * smalestRectangle.getWidth());
        return smalestRectangle;
    }

    /** Finds minimal area rectangle containing set of points.
     *
     * @param plist set of points
     * @return vertex of rectangle or null if less then 3 points
     */
    @Deprecated
    public static Point2d[] findRectangleConturOld(List<Point2d> plist) {

        // TODO clenup, but it work for now

        Point2d border[] = new Point2d[4];

        double minArea = java.lang.Double.MAX_VALUE;

        Point2d p1 = plist.get(plist.size() - 1);

        for (int i = 0; i < plist.size(); i++) {
            Point2d p2 = plist.get(i);

            double maxHeight = -1;

            Point2d[] ppp1 = new Point2d[plist.size()];

            double minLenght = java.lang.Double.MAX_VALUE;
            double maxLenght = -java.lang.Double.MAX_VALUE;
            int minLenghtIndex = -1;
            int maxLenghtIndex = -1;

            double kat = Math.atan2(p2.y - p1.y, p2.x - p1.x);
            //			log.info(Math.toDegrees(kat));
            int maxHeightIndex = -1;

            for (int j = 0; j < plist.size(); j++) {
                Point2d p3 = plist.get(j);

                Point2d pointOnLine = Algebra.projectPointPerpendicularToLine(
                        p1, p2, p3);

                // if ((j != i) && (j !=i+1)) {

                double height = p3.distance(pointOnLine);

                if (maxHeight < height) {
                    maxHeight = height;
                    maxHeightIndex = j;
                }
                // }
                ppp1[j] = pointOnLine;

                double lenght = (pointOnLine.x - p1.x) * Math.cos(kat)
                        + (pointOnLine.y - p1.y) * Math.sin(kat);

                if (lenght < minLenght) {
                    minLenght = lenght;
                    minLenghtIndex = j;
                }
                if (lenght > maxLenght) {
                    maxLenght = lenght;
                    maxLenghtIndex = j;
                }
            }

            double area = maxHeight * (maxLenght - minLenght);

            if (area < minArea) {
                log.debug("znaleziono mniejsze pole i: " + i + " area: " + area);
                minArea = area;

                double dist2 = ppp1[minLenghtIndex]
                        .distance(ppp1[maxLenghtIndex]);
                if (Math.abs((dist2 - (maxLenght - minLenght))) > 5) {
                    log.error("pole sie nie zgadza: " + dist2 + ", "
                            + (maxLenght - minLenght));
                }

                border[0] = ppp1[minLenghtIndex];
                border[1] = ppp1[maxLenghtIndex];

                double heightVecX = plist.get(maxHeightIndex).x
                        - ppp1[maxHeightIndex].x;
                double heightVecY = plist.get(maxHeightIndex).y
                        - ppp1[maxHeightIndex].y;

                border[2] = new Point2d(ppp1[maxLenghtIndex].x + heightVecX,
                        ppp1[maxLenghtIndex].y + heightVecY);
                border[3] = new Point2d(ppp1[minLenghtIndex].x + heightVecX,
                        ppp1[minLenghtIndex].y + heightVecY);
            }
            p1 = p2;
        }

        return border;
    }


    /**
     * @see kendzi.math.geometry.Plane3d#calcYOfPlane(double, double)
     *
     * @param x
     * @param z
     * @param plainPoint
     * @param plainNormal
     * @return
     */
    @Deprecated
    public static double calcYOfPlane(double x, double z, Point3d plainPoint, Vector3d plainNormal) {
        double a = plainNormal.x;
        double b = plainNormal.y;
        double c = plainNormal.z;
        double d = -a * plainPoint.x - b * plainPoint.y - c * plainPoint.z;

        return (-a * x - c * z - d) / b;
    }

}
