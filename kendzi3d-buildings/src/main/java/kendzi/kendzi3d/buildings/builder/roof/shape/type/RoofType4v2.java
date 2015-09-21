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

/**
 * Roof type 4.2.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class RoofType4v2 extends RectangleRoofTypeBuilder {

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
     * 
     * 
     * 
     * @param buildingPolygon
     * @param recHeight
     * @param recWidth
     * @param rectangleContur
     * @param h1
     * @param h2
     * @param l2
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double recHeight, double recWidth,
            Point2d[] rectangleContur, double h1, double h2, double l2, RoofMaterials roofTextureData) {

        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        double halfRecHeight = 0.5d * recHeight;

        Point2d rightTop = new Point2d(recWidth, recHeight);
        Point2d rightMiddleTopPoint = new Point2d(recWidth - l2, recHeight - l2);
        Point2d rightCenterPoint = new Point2d(recWidth - halfRecHeight, halfRecHeight);
        Point2d rightMiddleBottomPoint = new Point2d(recWidth - l2, l2);
        Point2d rightBottom = new Point2d(recWidth, 0);

        Point2d leftTop = new Point2d(0, recHeight);
        Point2d leftMiddleTopPoint = new Point2d(l2, recHeight - l2);
        Point2d leftCenterPoint = new Point2d(halfRecHeight, halfRecHeight);
        Point2d leftMiddleBottomPoint = new Point2d(l2, l2);
        Point2d leftBottom = new Point2d(0, 0);

        final LinePoints2d tLine = new LinePoints2d(leftMiddleTopPoint, rightMiddleTopPoint);
        final LinePoints2d mLine = new LinePoints2d(leftCenterPoint, rightCenterPoint);
        final LinePoints2d bLine = new LinePoints2d(leftMiddleBottomPoint, rightMiddleBottomPoint);

        final LinePoints2d lLine = new LinePoints2d(leftMiddleBottomPoint, leftMiddleTopPoint);
        final LinePoints2d rLine = new LinePoints2d(rightMiddleTopPoint, rightMiddleBottomPoint);

        final LinePoints2d lbLine = new LinePoints2d(leftBottom, leftCenterPoint);
        final LinePoints2d ltLine = new LinePoints2d(leftCenterPoint, leftTop);

        final LinePoints2d rtLine = new LinePoints2d(rightTop, rightCenterPoint);
        final LinePoints2d rbLine = new LinePoints2d(rightCenterPoint, rightBottom);

        Vector3d nt = new Vector3d(0, l2, -h2);
        nt.normalize();
        Vector3d nmt = new Vector3d(0, recHeight * 0.5d - l2, -(h1 - h2));
        nmt.normalize();

        Vector3d nmb = new Vector3d(0, recHeight * 0.5d - l2, h1 - h2);
        nmb.normalize();
        Vector3d nb = new Vector3d(0, l2, h2);
        nb.normalize();

        Vector3d nr = new Vector3d(h2, l2, 0);
        nr.normalize();
        Vector3d nmr = new Vector3d(h1 - h2, recHeight * 0.5d - l2, 0);
        nmr.normalize();
        Vector3d nl = new Vector3d(-h2, l2, 0);
        nl.normalize();
        Vector3d nml = new Vector3d(-(h1 - h2), recHeight * 0.5d - l2, 0);
        nml.normalize();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);
        MultiPolygonList2d borderMultiPolygon = new MultiPolygonList2d(borderPolygon);

        MultiPolygonList2d topMP = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, leftTop,
                leftMiddleTopPoint, rightMiddleTopPoint, rightTop, leftTop);
        MultiPolygonList2d topMiddleMP = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, leftMiddleTopPoint,
                leftCenterPoint, rightCenterPoint, rightMiddleTopPoint, leftMiddleTopPoint);
        MultiPolygonList2d bottomMiddleMP = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon,
                leftMiddleBottomPoint, rightMiddleBottomPoint, rightCenterPoint, leftCenterPoint, leftMiddleBottomPoint);
        MultiPolygonList2d bottomMP = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, leftBottom, rightBottom,
                rightMiddleBottomPoint, leftMiddleBottomPoint, leftBottom);

        MultiPolygonList2d leftMP = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, leftBottom,
                leftMiddleBottomPoint, leftMiddleTopPoint, leftTop, leftBottom);
        MultiPolygonList2d leftMiddleMP = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon,
                leftMiddleBottomPoint, leftCenterPoint, leftMiddleTopPoint, leftMiddleBottomPoint);

        MultiPolygonList2d rightMP = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, rightBottom, rightTop,
                rightMiddleTopPoint, rightMiddleBottomPoint, rightBottom);
        MultiPolygonList2d rightMiddleMP = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon,
                rightMiddleBottomPoint, rightMiddleTopPoint, rightCenterPoint, rightMiddleBottomPoint);

        Point3d planeLeftBottomPoint = new Point3d(leftMiddleBottomPoint.x, h2, -leftMiddleBottomPoint.y);

        Point3d planeRightTopPoint = new Point3d(rightMiddleTopPoint.x, h2, -rightMiddleTopPoint.y);

        final Plane3d planeBottom = new Plane3d(planeLeftBottomPoint, nb);

        final Plane3d planeMiddleBottom = new Plane3d(planeLeftBottomPoint, nmb);

        final Plane3d planeTop = new Plane3d(planeRightTopPoint, nt);

        final Plane3d planeMiddleTop = new Plane3d(planeRightTopPoint, nmt);

        final Plane3d planeLeft = new Plane3d(planeLeftBottomPoint, nl);
        final Plane3d planeMiddleLeft = new Plane3d(planeLeftBottomPoint, nml);

        final Plane3d planeRight = new Plane3d(planeRightTopPoint, nr);
        final Plane3d planeMiddleRight = new Plane3d(planeRightTopPoint, nmr);

        Vector3d roofBottomLineVector = new Vector3d(recWidth, 0, 0);

        Vector3d roofTopLineVector = new Vector3d(-recWidth, 0, 0);

        Vector3d roofLeftLineVector = new Vector3d(0, 0, recHeight);

        Vector3d roofRightLineVector = new Vector3d(0, 0, -recHeight);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMiddleMP, planeMiddleTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMiddleMP, planeMiddleBottom, roofBottomLineVector,
                roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, leftMP, planeLeft, roofLeftLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, leftMiddleMP, planeMiddleLeft, roofLeftLineVector, roofTexture);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, rightMP, planeRight, roofRightLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, rightMiddleMP, planeMiddleRight, roofRightLineVector,
                roofTexture);

        HeightCalculator hc = new MultiSplitHeightCalculator() {
            @Override
            public double calcHeight(Point2d point) {
                return RoofType4v2.calcHeight(point, mLine, bLine, tLine, lLine, rLine, lbLine, ltLine, rtLine, rbLine,
                        planeTop, planeMiddleTop, planeMiddleBottom, planeBottom, planeLeft, planeMiddleLeft,
                        planeRight, planeMiddleRight);
            }

            @Override
            public List<LinePoints2d> getSplittingLines() {
                return Arrays.asList(mLine, bLine, tLine, lLine, rLine, lbLine, ltLine, rtLine, rbLine);
            }
        };

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(h1, h2));
        rto.setHeightCalculator(hc);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs = buildRectRoofHooksSpace(rectangleContur,
                new PolygonPlane(bottomMP, planeBottom), null, new PolygonPlane(topMP, planeTop), null);

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    public static MultiPolygonList2d intersectionOfLeftSideOfMultipleCuts(MultiPolygonList2d polygons, Point2d... lines) {
        return PolygonSplitHelper.intersectionOfLeftSideOfMultipleCuts(polygons,
                PolygonSplitHelper.polygonalChaniToLineArray(lines));
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
            LinePoints2d lLine, LinePoints2d rLine, LinePoints2d lbLine, LinePoints2d ltLine, LinePoints2d rtLine,
            LinePoints2d rbLine, Plane3d planeTop, Plane3d planeMiddleTop, Plane3d planeMiddleBottom,
            Plane3d planeBottom, Plane3d planeLeft, Plane3d planeMiddleLeft, Plane3d planeRight,
            Plane3d planeMiddleRight) {

        double x = point.x;
        double z = -point.y;

        if (lbLine.inFront(point) && ltLine.inFront(point)) {
            if (lLine.inFront(point)) {
                return planeLeft.calcYOfPlane(x, z);
            }
            return planeMiddleLeft.calcYOfPlane(x, z);
        } else if (rtLine.inFront(point) && rbLine.inFront(point)) {
            if (rLine.inFront(point)) {
                return planeRight.calcYOfPlane(x, z);
            }
            return planeMiddleRight.calcYOfPlane(x, z);

        } else if (mLine.inFront(point)) {

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
