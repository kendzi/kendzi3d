package kendzi.kendzi3d.buildings.builder.height;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.split.EnrichPolygonalChainUtil;

/**
 * Calculates segments height of walls for roof splitted multiple times by
 * lines.
 *
 */
public abstract class MultiSplitHeightCalculator implements HeightCalculator {

    /**
     * Sets height calculator for surface splitted multiple times.
     */
    public MultiSplitHeightCalculator() {

    }

    public abstract double calcHeight(Point2d point);

    public abstract List<LinePoints2d> getSplittingLines();

    @Override
    public List<SegmentHeight> height(Point2d p1, Point2d p2) {

        List<Point2d> chain = new ArrayList<Point2d>();
        chain.add(p1);
        chain.add(p2);

        List<Point2d> enrichedChain = chain;

        List<LinePoints2d> splittingLines = getSplittingLines();
        for (LinePoints2d splittingLine : splittingLines) {
            enrichedChain = EnrichPolygonalChainUtil.enrichOpenPolygonalChainByLineCrossing(enrichedChain,
                    splittingLine);
        }

        List<SegmentHeight> ret = new ArrayList<SegmentHeight>();

        for (int i = 0; i < enrichedChain.size() - 1; i++) {
            Point2d begin = enrichedChain.get(i);
            Point2d end = enrichedChain.get(i + 1);

            ret.add(new SegmentHeight( //
                    begin, calcHeight(begin), //
                    end, calcHeight(end)));
        }

        return ret;
    }
}
