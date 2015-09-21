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
 * Roof type 4.0.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType4v0 extends RectangleRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);
        Double h2 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_2, h1 * 2d / 3d);

        Double l2 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_2, conf.getRecHeight() / 2,
                conf.getRecHeight() / 2d * 1d / 3d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1,
                h2, l2, conf.getRoofTextureData());
    }

    /**
     * @param buildingPolygon
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param h2
     * @param l2
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double pRecHeight, double pRecWidth,
            Point2d[] pRectangleContur, double h1, double h2, double l2, RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        Point2d rightTopPoint = new Point2d(pRecWidth, pRecHeight - l2);
        Point2d rightMiddlePoint = new Point2d(pRecWidth, 0.5d * pRecHeight);
        Point2d rightBottomPoint = new Point2d(pRecWidth, l2);

        Point2d leftTopPoint = new Point2d(0, pRecHeight - l2);
        Point2d leftMiddlePoint = new Point2d(0, 0.5d * pRecHeight);
        Point2d leftBottomPoint = new Point2d(0, l2);

        final LinePoints2d topLine = new LinePoints2d(leftTopPoint, rightTopPoint);
        final LinePoints2d middleLine = new LinePoints2d(leftMiddlePoint, rightMiddlePoint);
        final LinePoints2d bottomLine = new LinePoints2d(leftBottomPoint, rightBottomPoint);

        Vector3d nt = new Vector3d(0, l2, -h2);
        nt.normalize();
        Vector3d nmt = new Vector3d(0, pRecHeight * 0.5d - l2, -(h1 - h2));
        nmt.normalize();

        Vector3d nmb = new Vector3d(0, pRecHeight * 0.5d - l2, h1 - h2);
        nmb.normalize();
        Vector3d nb = new Vector3d(0, l2, h2);
        nb.normalize();

        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        MultiPolygonSplitResult middleSplit = PolygonSplitHelper.splitMultiPolygon(
                new MultiPolygonList2d(borderPolygon), middleLine);

        MultiPolygonList2d topMP = middleSplit.getLeftMultiPolygon();
        MultiPolygonList2d bottomMP = middleSplit.getRightMultiPolygon();

        MultiPolygonSplitResult topSplit = PolygonSplitHelper.splitMultiPolygon(topMP, topLine);

        topMP = topSplit.getLeftMultiPolygon();
        MultiPolygonList2d topMiddleMP = topSplit.getRightMultiPolygon();

        MultiPolygonSplitResult bottomSplit = PolygonSplitHelper.splitMultiPolygon(bottomMP, bottomLine);

        bottomMP = bottomSplit.getRightMultiPolygon();
        MultiPolygonList2d bottomMiddleMP = bottomSplit.getLeftMultiPolygon();

        Point3d planeLeftBottomPoint = new Point3d(leftBottomPoint.x, h2, -leftBottomPoint.y);

        Point3d planeRightTopPoint = new Point3d(rightTopPoint.x, h2, -rightTopPoint.y);

        final Plane3d planeBottom = new Plane3d(planeLeftBottomPoint, nb);

        final Plane3d planeMiddleBottom = new Plane3d(planeLeftBottomPoint, nmb);

        final Plane3d planeTop = new Plane3d(planeRightTopPoint, nt);

        final Plane3d planeMiddleTop = new Plane3d(planeRightTopPoint, nmt);

        Vector3d roofBottomLineVector = new Vector3d(pRecWidth, 0, 0);

        Vector3d roofTopLineVector = new Vector3d(-pRecWidth, 0, 0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMiddleMP, planeMiddleTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMiddleMP, planeMiddleBottom, roofBottomLineVector,
                roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);

        HeightCalculator hc = new MultiSplitHeightCalculator() {
            @Override
            public double calcHeight(Point2d point) {
                return RoofType4v0.calcHeight(point, middleLine, bottomLine, topLine, planeTop, planeMiddleTop,
                        planeMiddleBottom, planeBottom);
            }

            @Override
            public List<LinePoints2d> getSplittingLines() {
                return Arrays.asList(middleLine, bottomLine, topLine);
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
     * Calculates height of point in wall.
     * 
     * @param point
     *            the point
     * @param mLine
     *            the middle line
     * @param bLine
     *            the bottom line
     * @param tLine
     *            the top line
     * @param planeTop
     *            the top plane
     * @param planeMiddleTop
     *            the middle top plane
     * @param planeMiddleBottom
     *            the middle bottom plane
     * @param planeBottom
     *            the bottom plane
     * @return the height
     */
    private static double calcHeight(Point2d point, LinePoints2d mLine, LinePoints2d bLine, LinePoints2d tLine,
            Plane3d planeTop, Plane3d planeMiddleTop, Plane3d planeMiddleBottom, Plane3d planeBottom) {

        double x = point.x;
        double z = -point.y;

        if (mLine.inFront(point)) {

            if (tLine.inFront(point)) {
                return planeTop.calcYOfPlane(x, z);
            }
            return planeMiddleTop.calcYOfPlane(x, z);

        } else {

            if (bLine.inFront(point)) {
                return planeMiddleBottom.calcYOfPlane(x, z);
            }
            return planeBottom.calcYOfPlane(x, z);
        }
    }

}
