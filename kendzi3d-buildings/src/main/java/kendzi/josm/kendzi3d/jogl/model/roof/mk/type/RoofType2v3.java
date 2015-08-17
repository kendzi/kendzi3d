/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RectangleRoofHooksSpaces;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.wall.HeightCalculator;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.wall.MultiSplitHeightCalculator;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper;

/**
 * Roof type 2.3.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class RoofType2v3 extends RectangleRoofTypeBuilder {

    /**
     * Error epsilon.
     */
    private static final double EPSILON = 0.001;

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);

        Double h2 = getHeight2(conf.getMeasurements());

        Double l1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecHeight(),
                conf.getRecHeight() / 2d);
        Double l2 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_2, conf.getRecWidth(),
                conf.getRecWidth() / 4d);
        Double l3 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_3, conf.getRecHeight(),
                conf.getRecHeight() / 2d);

        if (h2 > h1) {
            throw new RuntimeException("bad parameters: h1 is bigger then h2");
        }

        boolean skipLeft = getSkipLeft();

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1,
                h2, l1, l2, l3, skipLeft, conf.getRoofTextureData());

    }

    protected double getHeight2(Map<MeasurementKey, Measurement> pMeasurements) {
        return getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_2, 1.5d);
    }

    protected boolean getSkipLeft() {
        return false;
    }

    /**
     * 
     * <img src="doc-files/RoofType2_3.png">
     * 
     * @param pBorderList
     * @param recHeight
     * @param recWidth
     * @param rectangleContur
     * @param h1
     * @param h2
     * @param l1
     * @param l2
     * @param b3
     * @param skipLeft
     * @param h3
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double recHeight, double recWidth,
            Point2d[] rectangleContur, double h1, double h2, double l1, double l2, double b3, boolean skipLeft,
            RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        // double l1 = l1;
        // double l2 = l2;
        // double sizeB3 = b3;

        // for numerical errors
        if (l1 < EPSILON) {
            l1 -= 1;
        }

        if (recHeight - l1 < EPSILON) {
            l1 += 1;
        }

        double sizeB1Oposit = recHeight - l1;

        final LinePoints2d middleLine = new LinePoints2d(new Point2d(0, l1), new Point2d(recWidth, l1));

        Vector3d normalTop = new Vector3d(0, sizeB1Oposit, -h1);
        normalTop.normalize();

        Vector3d normalBottom = new Vector3d(0, l1, h1);
        normalBottom.normalize();

        Point3d planePoint = new Point3d(middleLine.getP1().x, h1, -middleLine.getP1().y);

        final Plane3d planeTop = new Plane3d(planePoint, normalTop);
        final Plane3d planeBottom = new Plane3d(planePoint, normalBottom);

        double leftTopY = -planeTop.calcZOfPlane(0, h2);
        double leftBottomY = -planeBottom.calcZOfPlane(0, h2);

        Point2d leftTop = new Point2d(0, leftTopY);
        Point2d leftCenter = new Point2d(l2, l1);
        Point2d leftBottom = new Point2d(0, leftBottomY);

        if (skipLeft) {
            // draw only on right site

            leftTop = new Point2d(-0.01, recHeight);
            leftCenter = new Point2d(-0.01, l1);
            leftBottom = new Point2d(-0.01, 0);
        }

        Point2d rightTop = new Point2d(recWidth, leftTopY);
        Point2d rightCenter = new Point2d(recWidth - l2, l1);
        Point2d rightBottom = new Point2d(recWidth, leftBottomY);

        final LinePoints2d leftBottomLine = new LinePoints2d(leftBottom, leftCenter);
        final LinePoints2d leftTopLine = new LinePoints2d(leftCenter, leftTop);

        final LinePoints2d rightBottomLine = new LinePoints2d(rightBottom, rightCenter);
        final LinePoints2d rightTopLine = new LinePoints2d(rightCenter, rightTop);

        double leftHeight = planeBottom.calcYOfPlane(leftBottom.x, -leftBottom.y);
        if (l1 < 0.5 * recHeight) {
            leftHeight = planeTop.calcYOfPlane(leftTop.x, -leftTop.y);
            // only for numerical errors for lenght1 == 0 or == pRecHeight
        }
        double rightHeight = planeBottom.calcYOfPlane(rightBottom.x, -rightBottom.y);
        if (l1 < 0.5 * recHeight) {
            // only for numerical errors for lenght1 == 0 or == pRecHeight
            rightHeight = planeTop.calcYOfPlane(rightTop.x, -rightTop.y);
        }

        final Plane3d planeLeft = new Plane3d(new Point3d(leftCenter.x, h1, -leftCenter.y), new Vector3d(
                -(h1 - leftHeight), l2, 0));

        final Plane3d planeRight = new Plane3d(new Point3d(rightCenter.x, h1, -rightCenter.y), new Vector3d(
                -(rightHeight - h1), l2, 0));

        // //******************
        List<Point2d> border = new ArrayList<Point2d>();

        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        for (Point2d ppp : pBorderList) {
            border.add(new Point2d(ppp.x, ppp.y));
        }
        if (border.get(border.size() - 1).equals(border.get(0))) {
            border.remove(border.size() - 1);
        }

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);
        MultiPolygonList2d borderMultiPolygon = new MultiPolygonList2d(borderPolygon);

        MultiPolygonList2d mpb = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, rightBottom, rightCenter,
                leftCenter, leftBottom);
        MultiPolygonList2d mpt = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, leftTop, leftCenter,
                rightCenter, rightTop);
        MultiPolygonList2d mpl = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, leftBottom, leftCenter,
                leftTop);
        MultiPolygonList2d mpr = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, rightTop, rightCenter,
                rightBottom);

        Vector3d roofLeftLineVector = new Vector3d(0, 0, recWidth);

        Vector3d roofRightLineVector = new Vector3d(0, 0, -recWidth);

        Vector3d roofTopLineVector = new Vector3d(-recHeight, 0, 0);
        Vector3d roofButtomLineVector = new Vector3d(recHeight, 0, 0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpl, planeLeft, roofLeftLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpr, planeRight, roofRightLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpt, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpb, planeBottom, roofButtomLineVector, roofTexture);

        HeightCalculator hc = new MultiSplitHeightCalculator() {
            @Override
            public double calcHeight(Point2d point) {
                return RoofType2v3.calcHeight(point, middleLine, rightTopLine, rightBottomLine, leftTopLine,
                        leftBottomLine, planeLeft, planeRight, planeTop, planeBottom);
            }

            @Override
            public List<LinePoints2d> getSplittingLines() {
                return Arrays.asList(middleLine, rightTopLine, rightBottomLine, leftTopLine, leftBottomLine);
            }
        };

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);
        rto.setHeightCalculator(hc);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs = buildRectRoofHooksSpace(rectangleContur, new PolygonPlane(mpb, planeBottom),
                new PolygonPlane(mpr, planeRight), new PolygonPlane(mpt, planeTop), new PolygonPlane(mpl, planeLeft));

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    public static MultiPolygonList2d intersectionOfLeftSideOfMultipleCuts(MultiPolygonList2d polygons, Point2d... lines) {
        return PolygonSplitHelper.intersectionOfLeftSideOfMultipleCuts(polygons,
                PolygonSplitHelper.polygonalChaniToLineArray(lines));
    }

    /**
     * Calculate height of point.
     * 
     * @param point
     *            point to find height
     * @param middleLine
     *            middle line
     * @param rightTopLine
     *            right top line
     * @param rightBottomLine
     *            right bottom line
     * @param leftTopLine
     *            left top line
     * @param leftBottomLine
     *            left bottom line
     * @param planeLeft
     *            plane left
     * @param planeRight
     *            plane right
     * @param planeTop
     *            plane top
     * @param planeBottom
     *            plane bottom
     * @return height of point
     */
    private static double calcHeight(Point2d point, LinePoints2d middleLine, LinePoints2d rightTopLine,
            LinePoints2d rightBottomLine, LinePoints2d leftTopLine, LinePoints2d leftBottomLine, Plane3d planeLeft,
            Plane3d planeRight, Plane3d planeTop, Plane3d planeBottom) {

        double x = point.x;
        double z = -point.y;

        if (middleLine.inFront(point)) {
            if (leftTopLine.inFront(point)) {
                return planeLeft.calcYOfPlane(x, z);
            } else if (!rightTopLine.inFront(point)) {
                return planeRight.calcYOfPlane(x, z);
            } else {
                return planeTop.calcYOfPlane(x, z);
            }

        } else {

            if (leftBottomLine.inFront(point)) {
                return planeLeft.calcYOfPlane(x, z);
            } else if (!rightBottomLine.inFront(point)) {
                return planeRight.calcYOfPlane(x, z);
            } else {
                return planeBottom.calcYOfPlane(x, z);
            }
        }
    }
}
