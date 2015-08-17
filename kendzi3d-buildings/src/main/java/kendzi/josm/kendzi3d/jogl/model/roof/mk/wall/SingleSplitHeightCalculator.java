package kendzi.josm.kendzi3d.jogl.model.roof.mk.wall;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.SegmentHeight;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.line.LineUtil;
import kendzi.math.geometry.polygon.split.EnrichPolygonalChainUtil;

/**
 * Calculates segments height of walls for roof with only single splitting line
 * e.g. gabled roof.
 *
 */
public class SingleSplitHeightCalculator implements HeightCalculator {

    /**
     * The error for numerical equals.
     */
    private static final double EPSILON = 1e-10;

    /**
     * The plane on bottom of splitting line.
     */
    private final Plane3d bottomPlane;

    /**
     * The plane on top of splitting line.
     */
    private final Plane3d topPlane;

    /**
     * The splitting line of two planes.
     */
    private final LinePoints2d splittingLine;

    /**
     * Sets single height calculator.
     * 
     * @param splittingLine
     *            the splitting line of two planes
     * @param bottomPlane
     *            the plane on bottom of splitting line
     * @param topPlane
     *            the plane on top of splitting line
     */
    public SingleSplitHeightCalculator(LinePoints2d splittingLine, Plane3d bottomPlane, Plane3d topPlane) {
        this.splittingLine = splittingLine;
        this.bottomPlane = bottomPlane;
        this.topPlane = topPlane;
    }

    @Override
    public List<SegmentHeight> height(Point2d p1, Point2d p2) {

        List<Point2d> chain = new ArrayList<Point2d>();
        chain.add(p1);
        chain.add(p2);

        List<Point2d> enrichedChain = EnrichPolygonalChainUtil.enrichOpenPolygonalChainByLineCrossing(chain,
                splittingLine);

        List<SegmentHeight> ret = new ArrayList<SegmentHeight>();

        for (int i = 0; i < enrichedChain.size() - 1; i++) {
            Point2d begin = enrichedChain.get(i);
            Point2d end = enrichedChain.get(i + 1);

            Plane3d plane = bottomPlane;
            if (isSegmentInFrontOfLine(begin, end, splittingLine)) {
                plane = topPlane;
            }
            ret.add(new SegmentHeight( //
                    begin, calcHeight(begin, plane), //
                    end, calcHeight(end, plane)));
        }

        return ret;
    }

    /**
     * Calculates height of point where it intersect with the plane.
     *
     * @param point
     *            the 2d point
     * @param plane
     *            the surface plane
     * @return the height of point where it intersect with the plane
     */
    private double calcHeight(Point2d point, Plane3d plane) {

        double x = point.x;
        double z = -point.y;

        return plane.calcYOfPlane(x, z);

    }

    private boolean isSegmentInFrontOfLine(Point2d begin, Point2d end, LinePoints2d line) {

        double beginDet = LineUtil.matrixDet(line.getP1(), line.getP2(), begin);
        double endDet = LineUtil.matrixDet(line.getP1(), line.getP2(), end);

        if (equalZero(beginDet, EPSILON)) {
            beginDet = 0;
        }

        if (equalZero(endDet, EPSILON)) {
            endDet = 0;
        }

        if (beginDet > 0 && (endDet >= 0)) {
            return true;
        }
        if (endDet > 0 && (beginDet >= 0)) {
            return true;
        }
        return false;
    }

    private static boolean equalZero(double number, double epsilon) {
        return number * number < epsilon;
    }
}
