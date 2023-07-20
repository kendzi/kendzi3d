/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.height.BetweenLinesHeightCalculator;
import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper.MultiPolygonSplitResult;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * Roof type 5.2.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType5v2 extends RectangleRoofTypeBuilder {

    private static final double EPSILON = 1e-10;

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, conf.getRecHeight());

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1,
                conf.getRoofTextureData());
    }

    List<Double> calcSplitPoint(boolean half, int segments) {

        if (half) {
            List<Double> ret = new ArrayList<>();
            double step = Math.toRadians(90d / segments);
            ret.add(0d);
            for (int i = 0; i < segments; i++) {
                ret.add(1 - Math.sin((i + 1) * step));

            }
            return ret;
        }

        List<Double> ret = new ArrayList<>();
        double step = Math.toRadians(180d / segments);
        ret.add(0d);
        for (int i = 0; i < segments; i++) {
            ret.add(1 - Math.sin((i + 1) * step));
        }
        return ret;

    }

    /**
     * @param buildingPolygon
     * @param recHeight
     * @param recWidth
     * @param rectangleContur
     * @param height
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double recHeight, double recWidth,
            Vector2dc[] rectangleContur, double height, RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        List<Vector2dc> pBorderList = buildingPolygon.getOuter().getPoints();
        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        int segments = 6;
        List<Double> splitPoint = calcSplitPoint(true, segments);
        // List<LinePoints2d> lines = createLines(splitPoint, pRecHeight);

        List<CrossSectionElement> crossSection = createCrossSection(height, recHeight + EPSILON);
        final LinePoints2d[] lines = createLines(crossSection);

        MultiPolygonList2d[] mps = createMP(borderPolygon, lines);
        // Segment[] createSegments = createSegments(crossSection);

        final Plane3d[] planes = createPlanes(crossSection);

        double[] offsets = createTextureOffsets(crossSection);

        Vector3dc roofLineVector = new Vector3d(recWidth, 0, 0);

        for (int i = 0; i < mps.length; i++) {

            MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mps[i], planes[i], roofLineVector, roofTexture, 0, offsets[i]);
        }

        HeightCalculator hc = new BetweenLinesHeightCalculator(lines, planes);

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(height);
        rto.setHeightCalculator(hc);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        //
        // RectangleRoofHooksSpaces rhs =
        // buildRectRoofHooksSpace(
        // pRectangleContur,
        // new PolygonPlane(bottomMP, planeBottom),
        // null,
        // new PolygonPlane(topMP, planeTop),
        // null
        // );
        //
        // rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    public static double[] createTextureOffsets(List<CrossSectionElement> crossSection) {
        double[] ret = new double[crossSection.size()];
        ret[0] = 0;
        for (int i = 1; i < crossSection.size(); i++) {
            Vector2dc p1 = crossSection.get(i - 1).getP();
            Vector2dc p2 = crossSection.get(i).getP();

            ret[i] = ret[i - 1] + p1.distance(p2);
        }
        return ret;
    }

    public static MultiPolygonList2d[] createMP(PolygonList2d borderPolygon, LinePoints2d[] lines) {

        MultiPolygonList2d topMP = new MultiPolygonList2d(borderPolygon);

        MultiPolygonList2d[] mps = new MultiPolygonList2d[lines.length - 1];

        for (int i = 1; i < lines.length; i++) {

            MultiPolygonSplitResult middleSplit = PolygonSplitHelper.splitMultiPolygon(topMP, lines[i]);

            topMP = middleSplit.getLeftMultiPolygon();
            mps[i - 1] = middleSplit.getRightMultiPolygon();
        }

        return mps;
    }

    List<Vector2dc> createRound(int segments) {

        List<Vector2dc> ret = new ArrayList<>();
        double step = Math.toRadians(90d / segments);
        ret.add(new Vector2d());
        for (int i = 0; i < segments; i++) {
            ret.add(new Vector2d(1 - Math.cos((i + 1) * step), Math.sin((i + 1) * step)));
        }
        return ret;

    }

    static class CrossSectionElement {
        Vector2dc p;
        Vector2dc v;

        /**
         * @return the p
         */
        public Vector2dc getP() {
            return p;
        }

        /**
         * @param p
         *            the p to set
         */
        public void setP(Vector2dc p) {
            this.p = p;
        }

        /**
         * @return the v
         */
        public Vector2dc getV() {
            return v;
        }

        /**
         * @param v
         *            the v to set
         */
        public void setV(Vector2dc v) {
            this.v = v;
        }

        public CrossSectionElement(Vector2dc p, Vector2dc v) {
            super();
            this.p = p;
            this.v = v;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "CSE (" + p + ") > (" + v + ">";
        }

    }

    private List<CrossSectionElement> createCrossSection(double height, double recHeight) {
        List<Vector2dc> round = createRound(6);

        List<CrossSectionElement> split = new ArrayList<>();

        for (Vector2dc p : round) {
            double x = p.x() * height;
            double y = p.y() * height;

            split.add(new CrossSectionElement(new Vector2d(x, y), new Vector2d(p.x() - 1d, p.y())));
        }

        if (height < recHeight) {
            split.add(new CrossSectionElement(new Vector2d(recHeight, height), new Vector2d(0, 1)));
        }

        return split;

    }

    public static Plane3d[] createPlanes(List<CrossSectionElement> crossSection) {
        Plane3d[] seg = new Plane3d[crossSection.size() - 1];

        for (int i = 1; i < crossSection.size(); i++) {
            CrossSectionElement first = crossSection.get(i - 1);
            CrossSectionElement second = crossSection.get(i);

            Vector3dc p = new Vector3d(0, first.p.y(), -first.p.x());

            Vector3dc v = new Vector3d(0, second.p.x() - first.p.x(), second.p.y() - first.p.y()).normalize();
            Plane3d plane3d = new Plane3d(p, v);

            seg[i - 1] = plane3d;
        }
        return seg;
    }

    /**
     * @param crossSection
     * @return
     */
    public static LinePoints2d[] createLines(List<CrossSectionElement> crossSection) {
        LinePoints2d[] lines = new LinePoints2d[crossSection.size()];
        for (int i = 0; i < crossSection.size(); i++) {
            Vector2dc p = crossSection.get(i).p;

            LinePoints2d l = new LinePoints2d(new Vector2d(0, p.x()), new Vector2d(1, p.x()));
            lines[i] = l;
        }
        return lines;
    }

}
