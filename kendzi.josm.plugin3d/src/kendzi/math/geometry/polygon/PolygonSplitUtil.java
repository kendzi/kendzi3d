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

import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.line.LineUtil;
import kendzi.math.geometry.polygon.split.SplitPolygon;
import kendzi.math.geometry.polygon.split.SplitPolygons;

import org.apache.log4j.Logger;

public class PolygonSplitUtil {

    /** Log. */
    private static final Logger log = Logger.getLogger(PolygonSplitUtil.class);

    private static final double EPSILON = 1E-10;
    private static final double EPSILON_SQRT = EPSILON * EPSILON;

    /**
     * @param pSplitingLine
     * @param pPolygonPoints
     * @param pPolygonPointsExtanded
     * @param polygonsLeft
     * @param polygonsRight
     *
     * @deprecated required clean up and change for PolygonList2d
     */
    @Deprecated
    public static void splitPolygonByLine(LinePoints2d pSplitingLine, List<Point2d> pPolygonPoints,  List<Point2d> pPolygonPointsExtanded, List<List<Integer>> polygonsLeft,  List<List<Integer>> polygonsRight) {

        pPolygonPointsExtanded.clear();
        polygonsLeft.clear();
        polygonsRight.clear();

        boolean isCounterClockwise = false;
        if (0.0f < Triangulate.area(pPolygonPoints)) {
            isCounterClockwise = true;
        }
        pPolygonPointsExtanded.addAll(splitLineSegmentsOnLine(pSplitingLine, pPolygonPoints));


//        //      List<List<Integer>> polygons = new ArrayList<List<Integer>>();
//        List<List<Integer>> polygonsLeft = new ArrayList<List<Integer>>();
//        List<List<Integer>> polygonsRight = new ArrayList<List<Integer>>();

        // now we looking for polygons laying on "left" and "right" site of roof top line

        int firstChange = findFirstChangePoint(pSplitingLine, pPolygonPointsExtanded);

        splitPolygonsPartsByLine(pSplitingLine,
                pPolygonPointsExtanded, firstChange, polygonsLeft, polygonsRight);

        closePolygon(pPolygonPointsExtanded, polygonsLeft, isCounterClockwise);
        closePolygon(pPolygonPointsExtanded, polygonsRight, isCounterClockwise);
    }


    /** Splits line segments of polygon on point where line is crossing them.
     * @param pLine line which divide polygon
     * @param pPolygon polygon
     * @return polygon with extra points where line is crossing line segments of polygon
     */
    public static List<Point2d> splitLineSegmentsOnLine(LinePoints2d pLine,
            List<Point2d> pPolygon) {

        List<Point2d> borderExtanded = new ArrayList<Point2d>();

        List<java.lang.Double> detList = new ArrayList<java.lang.Double>();
        int size = pPolygon.size();
        for (int i = 0; i < size; i++) {

            detList.add(matrix_det(pLine.getP1(), pLine.getP2(), pPolygon.get(i)));
        }


        // we need add new vertex on line segments grossing top of roof
        for (int i = 0; i < pPolygon.size(); i++) {
            // searching for first line segment crossing top of roof
            Point2d pp1 = pPolygon.get(i);
            Point2d pp2 = pPolygon.get((i + 1) % size);

            double dp1 = detList.get(i);
            double dp2 = detList.get((i + 1) % size);

            borderExtanded.add(pp1);

            if (equalZero(dp1) || equalZero(dp2)) {
                // point laying on splitting line, we don't need add new point
                continue;

            } else if (dp1 * dp2 < 0) {
                // line segment crossing top of roof
                // we need add new vertex on this line
                Point2d p3 = lineCrosLineSegment(pLine.getP1(), pLine.getP2(), pp1, pp2);

                if (!equalZero(matrix_det(pLine.getP1(), pLine.getP2(), p3))) {
                    // for test only
                    log.error("added point is not on splitting line !!!. Probably epsilon is too small !!!");
                }

                borderExtanded.add(p3);
            }
        }

        return borderExtanded;
    }

    /** Splits polygon on polygon parts laying on left and right site of line.
     * @param pRoofLine top roof line
     * @param pBorderExtanded polygon
     * @param pFirstChange index of first point laying on line
     * @param pPolygonsLeft polygons parts laying of left site of line
     * @param pPolygonsRight polygons parts laying on right site of line
     *
     */
    private static void splitPolygonsPartsByLine(LinePoints2d pRoofLine,
            List<Point2d> pBorderExtanded, int pFirstChange,
            List<List<Integer>> pPolygonsLeft,
            List<List<Integer>> pPolygonsRight) {

        int eSize = pBorderExtanded.size();

        Boolean lastSite = null;
        List<Integer> polygon = null;


        if (pFirstChange == -1) {
            // non of points laying on spliting line! so we have only one polygon.
            // we need deside if it is left or right

            Point2d point = pBorderExtanded.get(0);

            double det = matrix_det(pRoofLine.getP1(), pRoofLine.getP2(), point);
            polygon = new ArrayList<Integer>();

            // add polygon to left or right site list
            if (equalZero(det) || det > 0) {
                pPolygonsRight.add(polygon);
                lastSite = true;
            } else {
                pPolygonsLeft.add(polygon);
                lastSite = false;
            }

            // there is no spliting line croosing so we can start from any point
            pFirstChange = 0;
        }


        // loop over all vertex from first which cross top roof line
        for (int i = pFirstChange; i < eSize + pFirstChange; i++) {
            //          Point2d pp1 = borderExtanded.get((i + eSize-1)% eSize);
            Point2d pp2 = pBorderExtanded.get((i) % eSize);
            Point2d p3 = pBorderExtanded.get((i + 1) % eSize);

            //          double d1 = matrix_det(roofLine.getP1(), roofLine.getP2(), pp1);
            double d2 = matrix_det(pRoofLine.getP1(), pRoofLine.getP2(), pp2);
            double d3 = matrix_det(pRoofLine.getP1(), pRoofLine.getP2(), p3);
            //log.warn("d2 = " + d2);

            if (equalZero(d2)) {
                if (equalZero(d3)) {
                    // we don't know if it is left or right polygon continue.
                    //                  throw new RuntimeException("something wrong, both det are 0 !");
                    if (polygon != null) {
                        // XXX !!!
                        polygon.add((i) % eSize);
                    }
// FIXME !!!
                    log.error("something wrong, both det are 0 !");
                    continue;
                }

                // so point laying on upper edge of the roof
                if (lastSite == null) {
                    // first new polygon
                    polygon = new ArrayList<Integer>();
                    polygon.add((i) % eSize);


                    if (equalZero(d3) || d3 > 0) {
                        pPolygonsRight.add(polygon);
                        lastSite = true;
                    } else {
                        pPolygonsLeft.add(polygon);
                        lastSite = false;
                    }
                    continue;
                } else {
                    //                  if ((d3 > 0) && (!lastSite)) {
                    //                      log.error("somthing wrong, last point was on other site !!");
                    //                  }
                    // if it is not first point so it have be last point of polygon
                    // add this point and create new polygon
                    polygon.add((i) % eSize);

                    polygon = new ArrayList<Integer>();
                    polygon.add((i) % eSize);

                    // add polygon to left or right site list
                    if (equalZero(d3) || d3 > 0) {
                        pPolygonsRight.add(polygon);
                        lastSite = true;
                    } else {
                        pPolygonsLeft.add(polygon);
                        lastSite = false;
                    }
                    //lastSite = null;
                    continue;

                }

            } else {
                polygon.add((i) % eSize);
                lastSite = true;
            }
            continue;


        }

        if (polygon == null) {
            return;
        }

        Point2d firsPoint = pBorderExtanded.get((pFirstChange) % eSize);
        double detFirsPoint = matrix_det(pRoofLine.getP1(), pRoofLine.getP2(), firsPoint);
        if (equalZero(detFirsPoint)) {
            if (polygon.contains((pFirstChange) % eSize)) {
                // XXX it is point in the polygon !!!
                // this is a bug, it require to fix it
                log.error(" it is point in the polygon !!!");
            } else {
                // first point id laying on upper edge of the roof so we have to add it to last polygon
                polygon.add((pFirstChange) % eSize);
            }
        } else {
            log.error("blad dla ostatniego punktu");
        }
    }

    /** Find for first point laying on top roof line.
     * @param pRoofLine top roof line
     * @param pBorderExtanded polygon
     * @return index of first point on the line
     */
    private static int findFirstChangePoint(LinePoints2d pRoofLine,
            List<Point2d> pBorderExtanded) {

        int firstChange = -1;
        int eSize = pBorderExtanded.size();

        for (int i = 0; i < eSize; i++) {
            // searching for first line segment crossing top of roof
            //          Point2d pp1 = borderExtanded.get((i + eSize -1)% eSize); // XXX
            Point2d pp2 = pBorderExtanded.get((i) % eSize);
            Point2d p3 = pBorderExtanded.get((i + 1) % eSize);

            //          double d1 = matrix_det(roofLine.getP1(), roofLine.getP2(), pp1);
            double d2 = matrix_det(pRoofLine.getP1(), pRoofLine.getP2(), pp2);
            double d3 = matrix_det(pRoofLine.getP1(), pRoofLine.getP2(), p3);

            if (equalZero(d2)) {
                firstChange = i;
                break;
            } else if (equalZero(d3)) {
                firstChange = (i + 1) % eSize;
                break;
            } else if (d2 * d3 < 0) {
                // this case never should happen!!!
                // always should be point in top roof line
                log.error(
                        "1. something bad happened !!! " +
                        "it is posible that EPSILON is too smal!!! d2: " + d2 + " d3: " + d3 );

                firstChange = (i + 1) % eSize;
                break;
            }
        }
        return firstChange;
    }

    /** Close polygons.
     * @param pPolygonsPoints polygons points
     * @param pPolygons parts of polygons. Defined by index
     * @param pIsCounterClockwise is polygon counter clockwise
     */
    private static void closePolygon(List<Point2d> pPolygonsPoints,
            List<List<Integer>> pPolygons, boolean pIsCounterClockwise) {


        List<List<Integer>> borderPolygons = new ArrayList<List<Integer>>();
        List<List<Integer>> internalPolygons = new ArrayList<List<Integer>>();

        for (List<Integer> polyIndex : pPolygons) {


            List<Point2d> poly = makeListFromIndex(pPolygonsPoints, polyIndex);

            if (pIsCounterClockwise == (0.0f < Triangulate.area2(poly))) {
                borderPolygons.add(polyIndex);
            } else {
                internalPolygons.add(polyIndex);
            }
        }

        // now we only need to merge this mess
        for (List<Integer> polyIndex : borderPolygons) {
            Point2d p1 = pPolygonsPoints.get(polyIndex.get(0));
            Point2d p2 = pPolygonsPoints.get(polyIndex.get(polyIndex.size() - 1));

            // now we need answer questions
            // 1. filter only internal polygon if they are inside border polygon
            // 2. find polygon starting closest to p2
            // 3. merge border polygon with internal
            // 4. replace p2 with i2
            List<List<Integer>> inSectionPolygons = new ArrayList<List<Integer>>();

            do {
                //1
                for (List<Integer> internalIndex : internalPolygons) {
                    Point2d i1 = pPolygonsPoints.get(internalIndex.get(0));
                    Point2d i2 = pPolygonsPoints.get(internalIndex.get(internalIndex.size() - 1));

                    if (isInSideLineSection(p1, p2, i1, i2)) {
                        inSectionPolygons.add(internalIndex);
                    }
                }
                //2.
                double minDistance = java.lang.Double.MAX_VALUE;
                int closestPolygonIndex = -1;
                for (int i = 0; i < inSectionPolygons.size(); i++) {
                    List<Integer> internalIndex = inSectionPolygons.get(i);

                    Point2d i1 = pPolygonsPoints.get(internalIndex.get(0));
                    Point2d i2 = pPolygonsPoints.get(internalIndex
                            .get(internalIndex.size() - 1));

                    double distance = calcMinDistance(p2, i1, i2);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestPolygonIndex = i;
                    }
                }
                //3.
                if (closestPolygonIndex != -1) {
                    List<Integer> closestPolygon = inSectionPolygons.get(closestPolygonIndex);
                    polyIndex.addAll(closestPolygon);
                    inSectionPolygons.remove(closestPolygon);
                    internalPolygons.remove(closestPolygon);

                    Point2d i2 = pPolygonsPoints.get(
                            closestPolygon.get(closestPolygon.size() - 1));
                    pPolygons.remove(closestPolygon);
                    p2 = i2;
                } else {
                    break;
                }

            } while (inSectionPolygons.size() > 0);


        }


    }

    private static Double matrix_det(Point2d l1, Point2d l2, Point2d point2d) {
        return LineUtil.matrixDet(l1, l2, point2d);
    }

    private static boolean equalZero(double d2) {
        if (d2 * d2 < EPSILON_SQRT) {
            return true;
        }
        return false;
    }


    private static Point2d lineCrosLineSegment(Point2d l1, Point2d l2, Point2d p1, Point2d p2) {
        return LineUtil.crossLineWithLineSegment(l1, l2, p1, p2);
    }

    /** Test if Line section |i1,i2| is in side Line Section |p1,p2|
     *  there is asumption that boat Line Section lay on the same line!
     *
     * @param p1
     * @param p2
     * @param i1
     * @param i2
     * @return
     */
    private static boolean isInSideLineSection(Point2d p1, Point2d p2, Point2d i1,
            Point2d i2) {

        if (isPointInLineSection(p1, p2, i1)
                && isPointInLineSection(p1, p2, i2)) {

            return true;
        }
        return false;

    }


    private static boolean isPointInLineSection(Point2d A, Point2d B, Point2d Z) {
        // XXX add epsilon !
        if ((Math.min(A.x, B.x) - EPSILON <= Z.x) && (Z.x <= Math.max(A.x, B.x) + EPSILON)
                && (Math.min(A.y, B.y) - EPSILON <= Z.y) && (Z.y <= Math.max(A.y, B.y) + EPSILON)) {
            return true;
        }
        return false;
    }

    /** Calculate minimal distance between point to two other points.
     * @param pPoint point
     * @param pFirstPoint first point to each distance is calculated
     * @param pSecondPoint second point to each distance is calculated
     * @return minimal distance from point to two other points
     */
    private static double calcMinDistance(Point2d pPoint, Point2d pFirstPoint, Point2d pSecondPoint) {
        return Math.min(pPoint.distance(pFirstPoint), pPoint.distance(pSecondPoint));
    }


    private static List<Point2d> makeListFromIndex(List<Point2d> borderExtanded,
            List<Integer> polyIndex) {

        List<Point2d> ret = new ArrayList<Point2d>(polyIndex.size());
        for (Integer i : polyIndex) {
            ret.add(borderExtanded.get(i));
        }
        return ret;
    }





    public static SplitPolygon splitPolygon(PolygonList2d pPolygon, LinePoints2d pSplitingLine) {
        SplitPolygon splitPolygon = new SplitPolygon();

        PolygonSplitUtil.splitPolygonByLine(pSplitingLine, pPolygon.getPoints(), splitPolygon.getPolygonExtanded(), splitPolygon.getPolygonsLeft(), splitPolygon.getPolygonsRight());

        return splitPolygon;
    }

    /**
     * @param pPolygon
     * @param pSplitingLine
     * @return
     * @deprecated use kendzi.math.geometry.polygon.PolygonSplitUtil.splitPolygon(PolygonList2d, LinePoints2d)
     */
    @Deprecated
    public static SplitPolygon splitPolygon(List<Point2d> pPolygon, LinePoints2d pSplitingLine) {
        SplitPolygon splitPolygon = new SplitPolygon();

        PolygonSplitUtil.splitPolygonByLine(pSplitingLine, pPolygon, splitPolygon.getPolygonExtanded(), splitPolygon.getPolygonsLeft(), splitPolygon.getPolygonsRight());

        return splitPolygon;
    }

    /**
     * @param pPolygons
     * @param pSplitingLine
     * @return
     * @deprecated use kendzi.math.geometry.polygon.PolygonSplitUtil.splitMultiPolygon(MultiPolygonList2d, LinePoints2d)
     */
    @Deprecated
    public static SplitPolygons splitMultiPolygon(List<List<Point2d>> pPolygons, LinePoints2d pSplitingLine) {

        SplitPolygons splitPolygons = new SplitPolygons();

        for (List<Point2d> p : pPolygons) {
            SplitPolygon partPolygon = new SplitPolygon();

            PolygonSplitUtil.splitPolygonByLine(
                    pSplitingLine,
                    p,
                    partPolygon.getPolygonExtanded(),
                    partPolygon.getPolygonsLeft(),
                    partPolygon.getPolygonsRight());

            splitPolygons.add(partPolygon);
        }

        return splitPolygons;
    }


    public static SplitPolygons splitMultiPolygon(MultiPolygonList2d pPolygons, LinePoints2d pSplitingLine) {

        SplitPolygons splitPolygons = new SplitPolygons();

        for (PolygonList2d p : pPolygons.getPolygons()) {
            SplitPolygon partPolygon = new SplitPolygon();

            PolygonSplitUtil.splitPolygonByLine(
                    pSplitingLine,
                    p.getPoints(),
                    partPolygon.getPolygonExtanded(),
                    partPolygon.getPolygonsLeft(),
                    partPolygon.getPolygonsRight());

            splitPolygons.add(partPolygon);
        }

        return splitPolygons;
    }

}
