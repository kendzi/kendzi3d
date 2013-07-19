/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.polygon;

import java.util.ArrayList;

import javax.vecmath.Point2d;

/**
 * Polygon util.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class PolygonUtil {

    /**
     * Test if point is inside polygon
     * @see "http://en.wikipedia.org/wiki/Point_in_polygon"
     * @see "http://en.wikipedia.org/wiki/Even-odd_rule"
     * @see "http://paulbourke.net/geometry/insidepoly/"
     * @param point point to test
     * @param polygon polygon
     * @return is point inside polygon
     */
    // XXX change name to contains or pointInsidePolygon?
    public static boolean isInside(Point2d point, PolygonList2d polygon) {
        // PointType & point, PolygonType & polygon
        int numpoints = polygon.getPoints().size();

        if (numpoints < 3) {
            return false;
        }

        ArrayList<Point2d> points = (ArrayList<Point2d>) polygon.getPoints();

        // PointListType const_iterator
        int it = 0;
        // ListIterator<Point2d> it = points.listIterator();// begin();
        // ListIterator<Point2d> itend = points.listIterator();

        // itend--;

        Point2d first = points.get(it);
        // Point2d last = (itend).GetPosition();

        /// XXX
        // // If last point same as first, don't bother with it.
        // if( polygon.isClosed() )
        // {
        // numpoints--;
        // }

        boolean oddNodes = false;

        Point2d node1;
        Point2d node2;

        for (int i = 0; i < numpoints; i++) {
            node1 = points.get(it);
            it++;
            if (i == numpoints - 1) {
                node2 = first;
            } else {
                node2 = points.get(it);
            }

            double x = point.x;
            double y = point.y;

            if ((node1.y < y && node2.y >= y) || (node2.y < y && node1.y >= y)) {
                if (node1.x + (y - node1.y) / (node2.y - node1.y) * (node2.x - node1.x) < x) {
                    oddNodes = !oddNodes;
                }
            }
        }

        return oddNodes;
    }

    /**
     *  Minimal values in polygon. Minimal coordinates of bounding box.
     *
     * @param pPolygon polygon
     * @return minimal values
     */
    public static Point2d minBound(PolygonList2d pPolygon) {

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (Point2d p : pPolygon.getPoints()) {
            if (p.x < minX) {
                minX = p.x;
            }
            if (p.y < minY) {
                minY = p.y;
            }
        }

        return new Point2d(minX, minY);
    }

    /**
     *  Maximal values in polygon. Maximal coordinates of bounding box.
     *
     * @param pPolygon polygon
     * @return maximal values
     */
    public static Point2d maxBound(PolygonList2d pPolygon) {

        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (Point2d p : pPolygon.getPoints()) {
            if (p.x > maxX) {
                maxX = p.x;
            }
            if (p.y > maxY) {
                maxY = p.y;
            }
        }

        return new Point2d(maxX, maxY);
    }

}
