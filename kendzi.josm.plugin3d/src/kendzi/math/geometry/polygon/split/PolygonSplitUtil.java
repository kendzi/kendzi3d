package kendzi.math.geometry.polygon.split;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;

import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;

public class PolygonSplitUtil {

//    /**
//     * @param pPolygons
//     * @param pSplitingLine
//     * @return
//     *  use kendzi.math.geometry.polygon.PolygonSplitUtil.splitMultiPolygon(MultiPolygonList2d, LinePoints2d)
//     */
//    @Deprecated
//    public static MultiPolygonList2d splitMultiPolygon(List<List<Point2d>> pPolygons, LinePoints2d pSplitingLine) {
//        PolygonSplit.splitPolygonByLine(
//    }

    public static SplitPolygons split(PolygonList2d pPolygon, LinePoints2d pSplitingLine) {
        return split(new MultiPolygonList2d(pPolygon), pSplitingLine);
    }

    public static SplitPolygons split(MultiPolygonList2d pPolygons, LinePoints2d pSplitingLine) {
        return PolygonSplit.splitMultiPolygon(pPolygons, pSplitingLine);
    }

    public static MultiPolygonList2d splitFrontPart(MultiPolygonList2d pPolygons, LinePoints2d pSplitingLine) {
        return PolygonSplit.splitMultiPolygon(pPolygons, pSplitingLine).getTopMultiPolygons();
    }

    public static MultiPolygonList2d splitBackPart(MultiPolygonList2d pPolygons, LinePoints2d pSplitingLine) {
        return PolygonSplit.splitMultiPolygon(pPolygons, pSplitingLine).getBottomMultiPolygons();
    }


    public static MultiPolygonList2d intersectionOfFrontPart(MultiPolygonList2d pPolygons, LinePoints2d ... lines) {
        // czesc wspolna

        List<List<Point2d>> multiPoints1 = new ArrayList<List<Point2d>>();
        for (PolygonList2d poly : pPolygons.getPolygons()) {
            multiPoints1.add(poly.getPoints());
        }

        for (LinePoints2d l : lines) {

            SplitPolygons splitPolygons = PolygonSplit.splitMultiPolygon(multiPoints1, l);

            multiPoints1 = splitPolygons.getRightPolygons();
        }

        MultiPolygonList2d mp = new MultiPolygonList2d();
        Set<PolygonList2d> polygons = mp.getPolygons();

        for (List<Point2d> m : multiPoints1) {
            PolygonList2d polygon = new PolygonList2d(m);
            polygons.add(polygon);
        }

        return mp;
    }

    public static MultiPolygonList2d intersectionOfFrontPart(MultiPolygonList2d pPolygons,
            Point2d ... lines) {

        // czesc wspolna
        LinePoints2d [] linesArray = new LinePoints2d [lines.length - 1];

        for (int i = 0; i < lines.length - 1; i++) {

            Point2d p1 = lines[i];
            Point2d p2 = lines[i + 1];

            linesArray[i] = new LinePoints2d(p1, p2);
        }

        return intersectionOfFrontPart(pPolygons, linesArray);
    }

    public static MultiPolygonList2d unionOfFrontPart(MultiPolygonList2d pPolygons, LinePoints2d ... lines) {
        // suma

        List<List<Point2d>> multiPoints1 = new ArrayList<List<Point2d>>();
        for (PolygonList2d poly : pPolygons.getPolygons()) {
            multiPoints1.add(poly.getPoints());
        }

        List<List<Point2d>> ret = new ArrayList<List<Point2d>>();

        for (LinePoints2d l : lines) {

            SplitPolygons splitPolygons = PolygonSplit.splitMultiPolygon(multiPoints1, l);

            ret.addAll(splitPolygons.getRightPolygons());

            multiPoints1 = splitPolygons.getLeftPolygons();
        }

        MultiPolygonList2d mp = new MultiPolygonList2d();
        Set<PolygonList2d> polygons = mp.getPolygons();

        for (List<Point2d> m : ret) {
            PolygonList2d polygon = new PolygonList2d(m);
            polygons.add(polygon);
        }

        return mp;
    }

    public static MultiPolygonList2d unionOfFrontPart(MultiPolygonList2d pPolygons, List<Point2d> lines) {
        throw new RuntimeException();
    }
}
