package kendzi.josm.kendzi3d.jogl.model.roof.mk.wall;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.SegmentHeight;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.split.PolygonSplit;

import org.apache.log4j.Logger;

/**
 * Calculate heights on line segments. Height is stored in planes. Planes are limited by lines. It is chosen plane[n]
 * laying between line[n] and line[n+1].
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class BetweenLinesHeightCalculator implements HeightCalculator {

    /** Log. */
    private static final Logger log = Logger.getLogger(BetweenLinesHeightCalculator.class);

    LinePoints2d[] lines;
    Plane3d[] planes;

    /**
     * @param lines
     * @param planes
     */
    public BetweenLinesHeightCalculator(LinePoints2d[] lines, Plane3d[] planes) {
        super();
        this.lines = lines;
        this.planes = planes;
    }

    @Override
    public SegmentHeight[] height(Point2d p1, Point2d p2) {

        List<Point2d> splitPolygon = Arrays.asList(p1, p2);

        for (LinePoints2d line : this.lines) {
            splitPolygon = PolygonSplit.splitLineSegmentsOnLineBBB(line, splitPolygon);
        }

        SegmentHeight[] ret = new SegmentHeight[splitPolygon.size() - 1];

        for (int i = 0; i < splitPolygon.size() - 1; i++) {
            int j = i + 1;

            Point2d begin = splitPolygon.get(i);
            Point2d end = splitPolygon.get(j);

            double beginHeight = calcHeight(begin, this.lines, this.planes);
            double endHeight = calcHeight(end, this.lines, this.planes);

            SegmentHeight sh = new SegmentHeight(begin, beginHeight, end, endHeight);
            ret[i] = sh;
        }

        return ret;
    }

    /**
     * Calc height of point in border.
     *
     * @param point
     * @param lines
     * @param planes
     * @return
     */
    private double calcHeight(Point2d point, LinePoints2d[] lines, Plane3d[] planes) {

        double x = point.x;
        double z = -point.y;

        for (int i = 0; i < lines.length; i++) {
            LinePoints2d line_mi = lines[i];
            LinePoints2d line = lines[(i + 1) % lines.length];

            if (line_mi.inFront(point) && !line.inFront(point)) {
                return planes[i].calcYOfPlane(x, z);
            }
        }

        log.warn("this should not happen");
        return 0; // planes[planes.length-1].calcYOfPlane(x, z);
    }

}
