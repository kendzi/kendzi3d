package kendzi.math.geometry.polygon.split;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.split.PlygonSplitUtil.SplitResult;

public class PolygonSplitHelper {

    public static PlygonSplitUtil.SplitResult split(PolygonList2d polygon, LinePoints2d splitingLine) {
        return split(new MultiPolygonList2d(polygon), splitingLine);
    }

    public static MultiPolygonSplitResult splitMultiPolygon(MultiPolygonList2d multiPolygon, LinePoints2d splittingLine) {
        SplitResult splitResult = split(multiPolygon, splittingLine);

        MultiPolygonList2d leftMultiPolygons = toMultiPolygon(splitResult.getLeftPolygons());
        MultiPolygonList2d rightMultiPolygons = toMultiPolygon(splitResult.getRightPolygons());
        return new MultiPolygonSplitResult(leftMultiPolygons, rightMultiPolygons);

    }

    private static MultiPolygonList2d toMultiPolygon(List<List<Point2d>> leftPolygons) {
        List<PolygonList2d> polygons = new ArrayList<PolygonList2d>();

        for (List<Point2d> polygon : leftPolygons) {
            polygons.add(new PolygonList2d(polygon));
        }
        return new MultiPolygonList2d(polygons);
    }

    public static class MultiPolygonSplitResult {
        private final MultiPolygonList2d leftMultiPolygon;
        private final MultiPolygonList2d rightMultiPolygon;

        public MultiPolygonSplitResult(MultiPolygonList2d leftMultiPolygon, MultiPolygonList2d rightMultiPolygon) {
            super();
            this.leftMultiPolygon = leftMultiPolygon;
            this.rightMultiPolygon = rightMultiPolygon;
        }

        /**
         * @return the leftMultiPolygon
         */
        public MultiPolygonList2d getLeftMultiPolygon() {
            return leftMultiPolygon;
        }

        /**
         * @return the rightMultiPolygon
         */
        public MultiPolygonList2d getRightMultiPolygon() {
            return rightMultiPolygon;
        }

    }

    public static PlygonSplitUtil.SplitResult split(MultiPolygonList2d multiPolygon, LinePoints2d splittingLine) {
        final List<List<Point2d>> leftPolygons = new ArrayList<List<Point2d>>();
        final List<List<Point2d>> rightPolygons = new ArrayList<List<Point2d>>();

        for (PolygonList2d polygon : multiPolygon.getPolygons()) {

            // FIXME XXX holes are missing!
            SplitResult split = PlygonSplitUtil.split(polygon.getPoints(), splittingLine);

            leftPolygons.addAll(split.getLeftPolygons());
            rightPolygons.addAll(split.getRightPolygons());
        }

        return new PlygonSplitUtil.SplitResult(leftPolygons, rightPolygons);
    }

    /**
     * Calculate intersection of left site of polygon cut multiple times by
     * different splitting lines. The result is on left site of each of
     * splitting lines.
     * 
     * @param multiPolygon
     *            the polygon to cut
     * @param lines
     *            splitting lines
     * @return the polygon which is on left site of each of splitting lines
     */
    public static MultiPolygonList2d intersectionOfLeftSideOfMultipleCuts(MultiPolygonList2d multiPolygon,
            LinePoints2d... lines) {

        MultiPolygonList2d leftMultiPolygon = multiPolygon;

        for (LinePoints2d line : lines) {

            MultiPolygonSplitResult splitResult = splitMultiPolygon(leftMultiPolygon, line);
            leftMultiPolygon = splitResult.getLeftMultiPolygon();
        }

        return leftMultiPolygon;
    }

    public static LinePoints2d[] polygonalChaniToLineArray(Point2d... lines) {
        LinePoints2d[] linesArray = new LinePoints2d[lines.length - 1];

        for (int i = 0; i < lines.length - 1; i++) {

            Point2d p1 = lines[i];
            Point2d p2 = lines[i + 1];

            linesArray[i] = new LinePoints2d(p1, p2);
        }

        return linesArray;
    }

    /**
     * Calculate union of left site of polygon cut multiple times by different
     * splitting lines. The result is on left site of at least of one splitting
     * lines.
     * 
     * @param multiPolygon
     *            the polygon to cut
     * @param lines
     *            splitting lines
     * @return the polygon which is on left site of each of splitting lines
     */
    public static MultiPolygonList2d unionOfLeftSideOfMultipleCuts(MultiPolygonList2d multiPolygon,
            LinePoints2d... lines) {

        MultiPolygonList2d leftMultiPolygon = new MultiPolygonList2d();

        MultiPolygonList2d rightMultiPolygon = multiPolygon;

        for (LinePoints2d line : lines) {

            MultiPolygonSplitResult splitResult = splitMultiPolygon(rightMultiPolygon, line);
            leftMultiPolygon.getPolygons().addAll(splitResult.getLeftMultiPolygon().getPolygons());
            rightMultiPolygon = splitResult.getRightMultiPolygon();

        }

        return leftMultiPolygon;
    }

}
