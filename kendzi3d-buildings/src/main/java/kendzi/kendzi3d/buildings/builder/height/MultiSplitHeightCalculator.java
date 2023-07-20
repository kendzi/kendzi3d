package kendzi.kendzi3d.buildings.builder.height;

import java.util.ArrayList;
import java.util.List;

import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.split.EnrichPolygonalChainUtil;
import org.joml.Vector2dc;

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

    public abstract double calcHeight(Vector2dc point);

    public abstract List<LinePoints2d> getSplittingLines();

    @Override
    public List<SegmentHeight> height(Vector2dc p1, Vector2dc p2) {

        List<Vector2dc> chain = new ArrayList<>();
        chain.add(p1);
        chain.add(p2);

        List<Vector2dc> enrichedChain = chain;

        List<LinePoints2d> splittingLines = getSplittingLines();
        for (LinePoints2d splittingLine : splittingLines) {
            enrichedChain = EnrichPolygonalChainUtil.enrichOpenPolygonalChainByLineCrossing(enrichedChain, splittingLine);
        }

        List<SegmentHeight> ret = new ArrayList<>();

        for (int i = 0; i < enrichedChain.size() - 1; i++) {
            Vector2dc begin = enrichedChain.get(i);
            Vector2dc end = enrichedChain.get(i + 1);

            ret.add(new SegmentHeight( //
                    begin, calcHeight(begin), //
                    end, calcHeight(end)));
        }

        return ret;
    }
}
