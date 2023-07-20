/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.height.BetweenLinesHeightCalculator;
import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType5v2.CrossSectionElement;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

/**
 * Roof type 5.0.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType5v0 extends RectangleRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, conf.getRecHeight() / 2d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1,
                conf.getRoofTextureData());
    }

    /**
     * @see {http://en.wikipedia.org/wiki/Circular_segment}
     * @param h
     * @param c
     * @return
     */
    double calcR(double h, double c) {
        return h / 2d + c * c / (8d * h);
    }

    double calcTeta(double c, double r) {
        return 2d * Math.asin(c / (2d * r));
    }

    List<Double> calcSplitPoint(boolean half, int segments) {

        if (half) {
            List<Double> ret = new ArrayList();
            double step = Math.toRadians(90d / segments);
            ret.add(0d);
            for (int i = 0; i < segments; i++) {
                ret.add(1 - Math.sin((i + 1) * step));

            }
            return ret;
        }

        List<Double> ret = new ArrayList();
        double step = Math.toRadians(180d / segments);
        ret.add(0d);
        for (int i = 0; i < segments; i++) {
            ret.add(1 - Math.sin((i + 1) * step));

        }
        return ret;

    }

    /**
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param height
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double pRecHeight, double pRecWidth,
            Point2d[] pRectangleContur, double height, RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();
        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        int segments = 16;

        if (height > pRecHeight / 2d) {
            height = pRecHeight / 2d;
        }

        List<CrossSectionElement> crossSection = createCrossSection(height, pRecHeight, segments);

        final LinePoints2d[] lines = RoofType5v2.createLines(crossSection);

        MultiPolygonList2d[] mps = RoofType5v2.createMP(borderPolygon, lines);
        // Segment[] createSegments = createSegments(crossSection);

        final Plane3d[] planes = RoofType5v2.createPlanes(crossSection);

        double[] offsets = RoofType5v2.createTextureOffsets(crossSection);

        Vector3d roofLineVector = new Vector3d(pRecWidth, 0, 0);

        for (int i = 0; i < mps.length; i++) {

            MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mps[i], planes[i], roofLineVector, roofTexture, 0,
                    offsets[i]);
        }

        HeightCalculator hc = new BetweenLinesHeightCalculator(lines, planes);

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(height);
        rto.setHeightCalculator(hc);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        return rto;
    }

    List<Point2d> createRound(double recHeight, double r, double h, double maxTeta, int segments) {

        double cx = recHeight / 2d;
        double cy = h - r;

        List<Point2d> ret = new ArrayList<Point2d>();
        ret.add(new Point2d());

        if (segments <= 0) {
            ret.add(new Point2d(recHeight, 0));
            return ret;
        }

        double dTeta = maxTeta / segments;

        double startTeta = Math.PI / 2d - maxTeta / 2d;

        for (int i = 1; i < segments; i++) {
            double teta = startTeta + i * dTeta;
            double px = -Math.cos(teta) * r + cx;
            double py = Math.sin(teta) * r + cy;

            ret.add(new Point2d(px, py));
        }
        ret.add(new Point2d(recHeight, 0));

        return ret;
    }

    private List<RoofType5v2.CrossSectionElement> createCrossSection(double height, double pRecHeight, int segments) {

        double c = pRecHeight;
        double h = height;

        double r = calcR(h, c);
        double teta = calcTeta(c, r);

        List<Point2d> round = createRound(pRecHeight, r, height, teta, segments);

        List<RoofType5v2.CrossSectionElement> split = new ArrayList<RoofType5v2.CrossSectionElement>();

        for (Point2d p : round) {
            double x = p.x;
            double y = p.y;

            split.add(new RoofType5v2.CrossSectionElement(new Point2d(x, y), new Vector2d(p.x - 1d, p.y)));
        }

        if (height < pRecHeight) {
            split.add(new RoofType5v2.CrossSectionElement(new Point2d(pRecHeight, height), new Vector2d(0, 1)));
        }

        return split;
    }

}
