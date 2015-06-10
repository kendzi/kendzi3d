/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.josm.kendzi3d.jogl.model.roof.mk.wall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.SegmentHeight;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.split.EnrichPolygonalChainUtil;

import org.apache.log4j.Logger;

/**
 * Calculate heights on line segments. Height is stored in planes. Planes are
 * limited by lines. It is chosen plane[n] laying between line[n] and line[n+1].
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class BetweenLinesHeightCalculator implements HeightCalculator {

    /** Log. */
    private static final Logger log = Logger.getLogger(BetweenLinesHeightCalculator.class);

    /**
     * Lines splitting 2d surface on smaller parts.
     */
    private final LinePoints2d[] lines;

    /**
     * Planes assigned for smaller parts.
     */
    private final Plane3d[] planes;

    /**
     * @param lines
     *            2d lines splitting surface
     * @param planes
     *            3d planes assigned for divided surface
     */
    public BetweenLinesHeightCalculator(LinePoints2d[] lines, Plane3d[] planes) {
        super();
        this.lines = lines;
        this.planes = planes;
    }

    @Override
    public List<SegmentHeight> height(Point2d p1, Point2d p2) {

        List<Point2d> splitPolygon = Arrays.asList(p1, p2);

        for (LinePoints2d line : lines) {
            splitPolygon = EnrichPolygonalChainUtil.enrichOpenPolygonalChainByLineCrossing(splitPolygon, line);
        }

        List<SegmentHeight> ret = new ArrayList<SegmentHeight>(splitPolygon.size() - 1);

        for (int i = 0; i < splitPolygon.size() - 1; i++) {
            int j = i + 1;

            Point2d begin = splitPolygon.get(i);
            Point2d end = splitPolygon.get(j);

            double beginHeight = calcHeight(begin, lines, planes);
            double endHeight = calcHeight(end, lines, planes);

            SegmentHeight sh = new SegmentHeight(begin, beginHeight, end, endHeight);

            ret.add(sh);
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
