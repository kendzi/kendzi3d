package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.ArrayList;
import java.util.List;

import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.height.SegmentHeight;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import org.ejml.simple.SimpleMatrix;
import org.joml.Vector2dc;

public class RectangleRoofTransformationUtil {
    public static PolygonWithHolesList2d transformationPolygonWithHoles(PolygonWithHolesList2d polygonWithHoles,
            SimpleMatrix transformLocal) {

        PolygonList2d outer = transformPolygon(polygonWithHoles.getOuter(), transformLocal);

        List<PolygonList2d> inner = new ArrayList<>();

        if (polygonWithHoles.getInner() != null) {
            for (PolygonList2d pi : polygonWithHoles.getInner()) {
                inner.add(transformPolygon(pi, transformLocal));
            }
        }
        return new PolygonWithHolesList2d(outer, inner);
    }

    private static PolygonList2d transformPolygon(PolygonList2d polygon, SimpleMatrix transformLocal) {
        return new PolygonList2d(TransformationMatrix2d.transformList(polygon.getPoints(), transformLocal));
    }

    /**
     * Prepare transformation matrix from local rectangle roof coordinates to global
     * building coordinates.
     * 
     * @param startPoint
     *            rectangle roof starting point
     * @param height
     *            roof minimal height
     * @param alpha
     *            roof angle
     * @return
     */
    public static SimpleMatrix prepareTransformToGlobalMatrix3d(Vector2dc startPoint, double height, double alpha) {

        SimpleMatrix transf = TransformationMatrix3d.tranA(startPoint.x(), height, -startPoint.y());
        SimpleMatrix rot = TransformationMatrix3d.rotYA(alpha);
        // XXX test me
        return transf.mult(rot);
    }

    /**
     * Prepare transformation matrix from local rectangle roof coordinates to global
     * building coordinates.
     * 
     * @param startPoint
     *            rectangle roof starting point
     * 
     * @param alpha
     *            roof angle
     * @return
     */
    public static SimpleMatrix prepareTransformMatrixToGlobal2d(Vector2dc startPoint, double alpha) {

        SimpleMatrix transf = TransformationMatrix2d.tranA(startPoint.x(), startPoint.y());
        SimpleMatrix rot = TransformationMatrix2d.rotZA(alpha);
        // XXX test me
        return transf.mult(rot);
    }

    /**
     * @param x
     * @param y
     * @param alpha
     * @param sizeA
     * @param sizeB
     * @return
     */
    public static SimpleMatrix trandformToLocalMatrix2d(double x, double y, double alpha) {

        SimpleMatrix transfLocal = TransformationMatrix2d.tranA(-x, -y);
        SimpleMatrix rotLocal = TransformationMatrix2d.rotZA(-alpha);
        // XXX test me
        return rotLocal.mult(transfLocal);
    }

    public static class TransformedHeightCalculator implements HeightCalculator {

        private final transient HeightCalculator heightCalculator;
        private final transient SimpleMatrix transformationToGlobal;
        private final transient double transformToGlobalHeightOffset;
        private final transient SimpleMatrix transformationToLocal;

        public TransformedHeightCalculator(HeightCalculator heightCalculator, SimpleMatrix transformationToGlobal,
                double transformToGlobalHeightOffset, SimpleMatrix transformationToLocal) {
            this.heightCalculator = heightCalculator;
            this.transformationToGlobal = transformationToGlobal;
            this.transformationToLocal = transformationToLocal;
            this.transformToGlobalHeightOffset = transformToGlobalHeightOffset;
        }

        @Override
        public List<SegmentHeight> height(Vector2dc p1, Vector2dc p2) {

            List<SegmentHeight> heights = heightCalculator.height(transformToLocal(p1), transformToLocal(p2));
            List<SegmentHeight> ret = new ArrayList<>();
            for (SegmentHeight segmentHeight : heights) {
                ret.add(transformToGlobal(segmentHeight));
            }
            return ret;
        }

        private SegmentHeight transformToGlobal(SegmentHeight segmentHeight) {

            Vector2dc begin = segmentHeight.getBegin();
            Vector2dc end = segmentHeight.getEnd();
            double beginHeight = segmentHeight.getBeginHeight();
            double endHeight = segmentHeight.getEndHeight();

            /* Do transformation to global frame. */
            return new SegmentHeight( //
                    TransformationMatrix2d.transform(begin, transformationToGlobal, true), //
                    beginHeight + transformToGlobalHeightOffset, //
                    TransformationMatrix2d.transform(end, transformationToGlobal, true), //
                    endHeight + transformToGlobalHeightOffset);
        }

        private Vector2dc transformToLocal(Vector2dc point) {
            return TransformationMatrix2d.transform(point, transformationToLocal, true);
        }
    }
}
