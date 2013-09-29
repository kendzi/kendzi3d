/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
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
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitUtil;
import kendzi.math.geometry.polygon.split.SplitPolygons;

import org.apache.log4j.Logger;

/**
 * Roof type 4.0.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType4v0 extends RectangleRoofTypeBuilder{

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofType4v0.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE4_0;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);
        Double h2 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_2, h1 * 2d / 3d);

        Double l2 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_2, conf.getRecHeight() / 2,
                conf.getRecHeight() / 2d * 1d / 3d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, h2, l2,
                conf.getRoofTextureData());
    }

    /**
     * @param pBorderList
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param h2
     * @param l2
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(
            PolygonWithHolesList2d buildingPolygon,
            double pRecHeight,
            double pRecWidth,
            Point2d[] pRectangleContur,
            double h1,
            double h2,
            double l2,
            RoofMaterials roofTextureData) {


        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        Point2d rightTopPoint = new Point2d(pRecWidth, pRecHeight - l2);
        Point2d rightMiddlePoint = new Point2d(pRecWidth, 0.5d * pRecHeight);
        Point2d rightBottomPoint = new Point2d(pRecWidth, l2);

        Point2d leftTopPoint = new Point2d(0, pRecHeight - l2);
        Point2d leftMiddlePoint = new Point2d(0, 0.5d * pRecHeight);
        Point2d leftBottomPoint = new Point2d(0, l2);




        LinePoints2d tLine = new LinePoints2d(leftTopPoint, rightTopPoint);
        LinePoints2d mLine = new LinePoints2d(leftMiddlePoint, rightMiddlePoint);
        LinePoints2d bLine = new LinePoints2d(leftBottomPoint, rightBottomPoint);


        Vector3d nt = new Vector3d(0, l2  , -h2);
        nt.normalize();
        Vector3d nmt = new Vector3d(0, pRecHeight * 0.5d - l2  , -(h1 - h2));
        nmt.normalize();

        Vector3d nmb = new Vector3d(0, pRecHeight * 0.5d - l2  , h1 - h2);
        nmb.normalize();
        Vector3d nb = new Vector3d(0, l2  , h2);
        nb.normalize();

        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        SplitPolygons middleSplit = PolygonSplitUtil.split(borderPolygon, mLine);

        MultiPolygonList2d topMP = middleSplit.getTopMultiPolygons();
        MultiPolygonList2d bottomMP = middleSplit.getBottomMultiPolygons();


        SplitPolygons topSplit = PolygonSplitUtil.split(topMP, tLine);

        topMP = topSplit.getTopMultiPolygons();
        MultiPolygonList2d topMiddleMP = topSplit.getBottomMultiPolygons();


        SplitPolygons bottomSplit = PolygonSplitUtil.split(bottomMP, bLine);

        bottomMP = bottomSplit.getBottomMultiPolygons();
        MultiPolygonList2d bottomMiddleMP = bottomSplit.getTopMultiPolygons();


        Point3d planeLeftBottomPoint =  new Point3d(
                leftBottomPoint.x ,
                h2,
                -leftBottomPoint.y);

        Point3d planeRightTopPoint =  new Point3d(
                rightTopPoint.x ,
                h2,
                -rightTopPoint.y);



        Plane3d planeBottom = new Plane3d(
                planeLeftBottomPoint,
                nb);

        Plane3d planeMiddleBottom = new Plane3d(
                planeLeftBottomPoint,
                nmb);

        Plane3d planeTop = new Plane3d(
                planeRightTopPoint,
                nt);


        Plane3d planeMiddleTop = new Plane3d(
                planeRightTopPoint,
                nmt);



        Vector3d roofBottomLineVector = new Vector3d(
                pRecWidth,
                0,
                0);

        Vector3d roofTopLineVector = new Vector3d(
                -pRecWidth,
                0,
                0);




        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMiddleMP, planeMiddleTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMiddleMP, planeMiddleBottom, roofBottomLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);



        List<Point2d> borderSplit = RoofTypeUtil.splitBorder(borderPolygon, mLine, bLine, tLine);

        List<Double> borderHeights = calcHeightList(
                borderSplit, mLine, bLine, tLine,
                planeTop, planeMiddleTop, planeMiddleBottom, planeBottom);



        ////******************

        RoofTypeUtil.makeRoofBorderMesh(

                borderSplit,
                borderHeights,

                meshBorder,
                facadeTexture
                );



        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(h1, h2));

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs =
                buildRectRoofHooksSpace(
                        pRectangleContur,
                        new PolygonPlane(bottomMP, planeBottom),
                        null,
                        new PolygonPlane(topMP, planeTop),
                        null
                        );

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }



    private List<Double> calcHeightList(
            List<Point2d> pSplitBorder,
            LinePoints2d mLine, LinePoints2d bLine,  LinePoints2d tLine,
            Plane3d planeTop, Plane3d planeMiddleTop, Plane3d planeMiddleBottom, Plane3d planeBottom) {



        List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
        for (Point2d point : pSplitBorder) {

            double height = calcHeight(point, mLine, bLine, tLine,
                    planeTop, planeMiddleTop, planeMiddleBottom, planeBottom);

            borderHeights.add(height);

        }

        return borderHeights;
    }

    /** Calc height of point in border.
     * @param point
     * @param mLine
     * @param bLine
     * @param tLine
     * @param planeTop
     * @param planeMiddleTop
     * @param planeMiddleBottom
     * @param planeBottom
     * @return
     */
    private double calcHeight(Point2d point,
            LinePoints2d mLine, LinePoints2d bLine,  LinePoints2d tLine,
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
