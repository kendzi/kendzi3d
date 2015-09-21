/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.height.MultiSplitHeightCalculator;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space.RectangleRoofHooksSpaces;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper.MultiPolygonSplitResult;

/**
 * Roof type 3.0.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType3v0 extends RectangleRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double l1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecHeight(),
                conf.getRecHeight() * 0.2);

        Double h1 = getHeightDegreesMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 0, l1, 60);

        Double h2 = getHeightDegreesMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_2, 0,
                conf.getRecHeight() - l1, 10);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1,
                h2, l1, conf.getRoofTextureData());

    }

    /**
     * @param buildingPolygon
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param h2
     * @param l1
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double pRecHeight, double pRecWidth,
            Point2d[] pRectangleContur, double h1, double h2, double l1, RoofMaterials roofTextureData) {

        double height = Math.max(h1, h2);

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        Point2d rightMiddlePoint = new Point2d(pRecWidth, l1);

        Point2d leftMiddlePoint = new Point2d(0, l1);

        final LinePoints2d mLine = new LinePoints2d(leftMiddlePoint, rightMiddlePoint);

        Vector3d nt = new Vector3d(0, pRecHeight - l1, -h2);
        nt.normalize();

        Vector3d nb = new Vector3d(0, l1, h1);
        nb.normalize();

        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        MultiPolygonSplitResult middleSplit = PolygonSplitHelper.splitMultiPolygon(
                new MultiPolygonList2d(borderPolygon), mLine);

        MultiPolygonList2d topMP = middleSplit.getLeftMultiPolygon();
        MultiPolygonList2d bottomMP = middleSplit.getRightMultiPolygon();

        Point3d planeLeftPoint = new Point3d(leftMiddlePoint.x, height, -leftMiddlePoint.y);

        Point3d planeRightPoint = new Point3d(rightMiddlePoint.x, height, -rightMiddlePoint.y);

        final Plane3d planeTop = new Plane3d(planeRightPoint, nt);
        final Plane3d planeBottom = new Plane3d(planeLeftPoint, nb);

        Vector3d roofBottomLineVector = new Vector3d(pRecWidth, 0, 0);

        Vector3d roofTopLineVector = new Vector3d(-pRecWidth, 0, 0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);

        HeightCalculator hc = new MultiSplitHeightCalculator() {
            @Override
            public double calcHeight(Point2d point) {
                return RoofType3v0.calcHeight(point, mLine, planeTop, planeBottom);
            }

            @Override
            public List<LinePoints2d> getSplittingLines() {
                return Arrays.asList(mLine);
            }
        };

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(h1, h2));
        rto.setHeightCalculator(hc);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs = buildRectRoofHooksSpace(pRectangleContur,
                new PolygonPlane(bottomMP, planeBottom), null, new PolygonPlane(topMP, planeTop), null);

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    /**
     * Calculate height of point in wall.
     * 
     * @param point
     *            the point
     * @param mLine
     *            the middle line
     * @param planeTop
     *            the top plane
     * @param planeBottom
     *            the bottom plane
     * @return
     */
    private static double calcHeight(Point2d point, LinePoints2d mLine, Plane3d planeTop, Plane3d planeBottom) {

        double x = point.x;
        double z = -point.y;

        if (mLine.inFront(point)) {

            return planeTop.calcYOfPlane(x, z);
        } else {

            return planeBottom.calcYOfPlane(x, z);
        }
    }
}
