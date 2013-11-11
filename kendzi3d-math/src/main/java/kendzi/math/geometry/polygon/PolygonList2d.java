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
import java.util.List;

import javax.vecmath.Point2d;

/**
 * Polygon described by list of points.
 *
 * Polygon is [XXX anty cloak wise]!
 *
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class PolygonList2d {

    /**
     * Points of polygon.
     */
    private List<Point2d> points;

    /**
     * Create polygon from list of points.
     *
     * @param pPoints list of points
     */
    public PolygonList2d(List<Point2d> pPoints) {
        this.points = pPoints;
    }

    /**
     * Create polygon from  points.
     *
     * @param pPoints points
     */
    public PolygonList2d(Point2d ... pPoints) {
        List<Point2d> ret = new ArrayList<Point2d>(pPoints.length);
        for(Point2d p : pPoints) {
            ret.add(p);
        }

        this.points = ret;
    }

    /**
     * Create empty polygon.
     */
    public PolygonList2d() {
        this(new ArrayList<Point2d>());
    }

    /**
     * @return the points
     */
    public List<Point2d> getPoints() {
        return this.points;
    }

    /**
     * @param pPoints the points to set
     */
    public void setPoints(List<Point2d> pPoints) {
        this.points = pPoints;
    }




    public void union(PolygonList2d pPolygon) {
        // TODO !!!
        //        suma
        throw new RuntimeException("TODO");
    }
    public void difference(PolygonList2d pPolygon) {
        // TODO !!!
        //        roznica
        throw new RuntimeException("TODO");
    }

    public boolean inside(Point2d pPoint) {
        // TODO !!!
        throw new RuntimeException("TODO");

    }

    public boolean inside(Point2d pPoint, double epsilon) {
        // TODO !!!
        throw new RuntimeException("TODO");
    }

    /**
     * Reverse point order in list
     * 
     * @param polygon
     * @return
     */
    public static List<Point2d> reverse(List<Point2d> polygon) {
        return PolygonUtil.reverse(polygon);
    }
}
