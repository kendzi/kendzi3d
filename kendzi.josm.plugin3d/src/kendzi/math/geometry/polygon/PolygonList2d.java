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
import java.util.Set;

import javax.vecmath.Point2d;

import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.split.SplitPolygon;
import kendzi.math.geometry.polygon.split.SplitPolygons;

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

//    public static MultiPolygonList2d split(PolygonList2d pPolygon1, LinePoints2d pSplitingLine) {
//
//        List<Point2d> points1 = pPolygon1.getPoints();
//
//        SplitPolygon splitPolygon = new SplitPolygon();
//
//        PolygonSplitUtil.splitPolygonByLine(pSplitingLine, points1, splitPolygon.getPolygonExtanded(), splitPolygon.getPolygonsLeft(), splitPolygon.getPolygonsRight());
//
//        splitPolygon.getPolygonsLeft();
//
//
//    }
//
//    public static SplitPolygon splitPolygon(List<Point2d> pPolygon, LinePoints2d pSplitingLine) {
//        SplitPolygon splitPolygon = new SplitPolygon();
//
//        PolygonSplitUtil.splitPolygonByLine(pSplitingLine, pPolygon, splitPolygon.getPolygonExtanded(), splitPolygon.getPolygonsLeft(), splitPolygon.getPolygonsRight());
//
//        return splitPolygon;
//    }


    public static MultiPolygonList2d intersection(PolygonList2d pPolygon1, PolygonList2d pPolygon2) {
        return intersection(pPolygon1, pPolygon2, false);
    }
    public static MultiPolygonList2d intersection(PolygonList2d pPolygon1, PolygonList2d pPolygon2, boolean pOpen) {

        // XXX for parameter pOpen==true require better name, may by intersection with line ?

        List<Point2d> points1 = pPolygon1.getPoints();

        List<List<Point2d>> multiPoints1 = new ArrayList<List<Point2d>>();
        multiPoints1.add(points1);

        List<Point2d> points2 = pPolygon2.getPoints();

        int size = points2.size();
        int loopSize = size;

        if (pOpen) {
            loopSize--;
        }

        for (int i = 0; i < loopSize; i++) {
            Point2d p1 = points2.get(i);
            Point2d p2 = points2.get((i + 1) % size);



            SplitPolygons splitPolygons = PolygonSplitUtil.splitMultiPolygon(multiPoints1, new LinePoints2d(p1, p2));

            // XXX
            multiPoints1 = splitPolygons.getRightPolygons();



//                    PolygonSplitUtil.splitPolygonByLine(
//                            new LinePoints2d(p1, p2),
//                            points1, pPolygonPointsExtanded, polygonsLeft, polygonsRight)
        }

//        PolygonSplitUtil.splitPolygonByLine(roofLine, border, borderExtanded, polygonsLeft, polygonsRight)
        MultiPolygonList2d mp = new MultiPolygonList2d();
        Set<PolygonList2d> polygons = mp.getPolygons();

        for (List<Point2d> m : multiPoints1) {
            PolygonList2d polygon = new PolygonList2d(m);
            polygons.add(polygon);
        }

        return mp;
    }


    public static SplitPolygon splitPolygon(List<Point2d> pPolygon, LinePoints2d roofLine) {
        SplitPolygon splitPolygon = new SplitPolygon();

        PolygonSplitUtil.splitPolygonByLine(roofLine, pPolygon, splitPolygon.getPolygonExtanded(), splitPolygon.getPolygonsLeft(), splitPolygon.getPolygonsRight());

        return splitPolygon;
    }

    public MultiPolygonList2d intersectionOpen(PolygonList2d pPolygon) {
        // XXX require better name, may by intersection with line ?
        //
        // czesc wspolna
        //counterclockwise
        return intersection(this, pPolygon, true);
    }


    public MultiPolygonList2d intersection(PolygonList2d pPolygon) {

        // czesc wspolna
        //counterclockwise
        return intersection(this, pPolygon);

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

}
