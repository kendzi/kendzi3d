/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RectangleRoofHooksSpaces;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitUtil;
import kendzi.math.geometry.polygon.split.SplitPolygons;

/**
 * Roof type 2.7.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType2v7 extends RectangleRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        boolean left = isLeft();

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);

        Double l1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecWidth(),
                conf.getRecWidth() / 2d);
        Double l2 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_2, conf.getRecHeight(),
                conf.getRecHeight() / 2d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, l1, l2,
                conf.getRoofTextureData(), left);
    }

    public boolean isLeft() {
        return true;
    }

    /**
     * 
     * 
     * 
     * @param pBorderList
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param h2
     * @param h3
     * @param roofTextureData
     * @param isLeft
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double pRecHeight, double pRecWidth,
            Point2d[] pRectangleContur, double h1, double l1, double l2, RoofMaterials roofTextureData, boolean isLeft) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        Point2d rightTopPoint = new Point2d(pRecWidth - l1, pRecHeight);
        Point2d rightBottomPoint = new Point2d(pRecWidth, 0);

        Point2d leftTopPoint = new Point2d(l2, pRecHeight);
        Point2d leftBottomPoint = new Point2d(0, 0);

        if (!isLeft) {
            leftTopPoint = new Point2d(-1, pRecHeight);
            leftBottomPoint = new Point2d(-1, 0);
        }

        LinePoints2d lLine = new LinePoints2d(leftBottomPoint, leftTopPoint);
        LinePoints2d rLine = new LinePoints2d(rightTopPoint, rightBottomPoint);

        Vector3d nl = new Vector3d(-h1, l2, 0);
        nl.normalize();
        Vector3d nr = new Vector3d(h1, l1, 0);
        nr.normalize();
        Vector3d nb = new Vector3d(0, pRecHeight, h1);
        nb.normalize();

        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        SplitPolygons leftSplit = PolygonSplitUtil.split(borderPolygon, lLine);

        MultiPolygonList2d leftMP = leftSplit.getTopMultiPolygons();
        MultiPolygonList2d middleMP = leftSplit.getBottomMultiPolygons();

        SplitPolygons rightSplit = PolygonSplitUtil.split(middleMP, rLine);

        MultiPolygonList2d rightMP = rightSplit.getTopMultiPolygons();
        middleMP = rightSplit.getBottomMultiPolygons();

        Point3d planeLeftPoint = new Point3d(leftTopPoint.x, h1, -leftTopPoint.y);

        Point3d planeRightPoint = new Point3d(rightTopPoint.x, h1, -rightTopPoint.y);

        Plane3d planeLeft = new Plane3d(planeLeftPoint, nl);

        Plane3d planeRight = new Plane3d(planeRightPoint, nr);

        Plane3d planeBottom = new Plane3d(planeRightPoint, nb);

        // List<Point2d> border = new ArrayList<Point2d>();
        //
        // for (Point2d ppp : pBorderList) {
        // border.add(new Point2d(ppp.x, ppp.y));
        // }
        // if (border.get(border.size() - 1).equals(border.get(0))) {
        // border.remove(border.size() - 1);
        // }

        Vector3d roofLeftLineVector = new Vector3d(0, 0, pRecHeight);

        Vector3d roofRightLineVector = new Vector3d(0, 0, -pRecHeight);

        Vector3d roofTopLineVector = new Vector3d(-pRecWidth, 0, 0);
        Vector3d roofButtomLineVector = new Vector3d(pRecWidth, 0, 0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, leftMP, planeLeft, roofLeftLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, rightMP, planeRight, roofRightLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, middleMP, planeBottom, roofButtomLineVector, roofTexture);

        List<Point2d> borderSplit = RoofTypeUtil.splitBorder(borderPolygon, lLine, rLine);

        List<Double> borderHeights = calcHeightList(borderSplit, lLine, rLine, planeLeft, planeRight, planeBottom);

        // //******************

        RoofTypeUtil.makeRoofBorderMesh(

        borderSplit, borderHeights,

        meshBorder, facadeTexture);

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs = buildRectRoofHooksSpace(pRectangleContur, new PolygonPlane(middleMP, planeBottom),
                new PolygonPlane(rightMP, planeRight), null, isLeft ? new PolygonPlane(leftMP, planeLeft) : null);

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    private List<Double> calcHeightList(List<Point2d> pSplitBorder, LinePoints2d lLine, LinePoints2d rLine, Plane3d planeLeft,
            Plane3d planeRight, Plane3d planeButtom) {

        List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
        for (Point2d point : pSplitBorder) {

            double height = calcHeight(point, rLine, lLine, planeLeft, planeRight, planeButtom);

            borderHeights.add(height);

        }

        return borderHeights;
    }

    /**
     * Calc height of point in border.
     * 
     * @param point
     * @param rLine
     * @param lLine
     * @param planeLeft
     * @param planeRight
     * @param planeButtom
     * @return
     */
    private double calcHeight(Point2d point, LinePoints2d rLine, LinePoints2d lLine, Plane3d planeLeft, Plane3d planeRight,
            Plane3d planeButtom) {

        double x = point.x;
        double z = -point.y;

        if (rLine.inFront(point)) {
            return planeRight.calcYOfPlane(x, z);

        } else if (lLine.inFront(point)) {

            return planeLeft.calcYOfPlane(x, z);

        } else {
            return planeButtom.calcYOfPlane(x, z);
        }

    }

}
