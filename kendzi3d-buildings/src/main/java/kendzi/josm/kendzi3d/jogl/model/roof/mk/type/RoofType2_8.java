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
 * Roof type 2.8.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType2_8 extends RectangleRoofTypeBuilder{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2_8.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE2_8;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {



        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);
        Double h2 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_2, h1);

        //        Double l1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecWidth(), conf.getRecWidth() / 2d);
        //        Double l2 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_2, conf.getRecHeight(), conf.getRecHeight() / 2d);


        double middleLineHeight = getMiddleLineHeight(h1, h2);

        h1 = getHeight1(h1);
        h2 = getHeight2(h2);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, h2, middleLineHeight, conf.getRoofTextureData());

    }

    protected double getMiddleLineHeight(Double h1, Double h2) {
        return 0;
    }

    protected Double getHeight1(Double h1) {
        return h1;
    }

    protected Double getHeight2(Double h2) {
        return h2;
    }

    /**
     * @param pBorderList
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param h2
     * @param middleLineHeight
     * @param h3
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
            double middleLineHeight,
            RoofMaterials roofTextureData
            ) {



        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        Point2d rightTopPoint = new Point2d(pRecWidth, pRecHeight);
        Point2d rightBottomPoint = new Point2d(pRecWidth, 0);

        Point2d leftTopPoint = new Point2d(0, pRecHeight);
        Point2d leftBottomPoint = new Point2d(0, 0);


        LinePoints2d lLine = new LinePoints2d(rightBottomPoint, leftTopPoint);
        //  LinePoints2d rLine = new LinePoints2d(rightTopPoint, rightBottomPoint);


        //        Vector3d nl = new Vector3d(-h1 , l2 , 0);
        //        nl.normalize();
        //        Vector3d nr = new Vector3d(h1, l1 , 0);
        //        nr.normalize();
        //        Vector3d nb = new Vector3d(pRecWidth, pRecHeight  , h1);
        //        nb.normalize();
        //        Vector3d nt = new Vector3d(pRecWidth, pRecHeight  , h2);
        //        nt.normalize();

        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        SplitPolygons leftSplit = PolygonSplitUtil.split(borderPolygon, lLine);

        MultiPolygonList2d bottomMP = leftSplit.getTopMultiPolygons();
        MultiPolygonList2d topMP = leftSplit.getBottomMultiPolygons();



        Vector3d roofBottomLineVector = new Vector3d(
                rightBottomPoint.x - leftTopPoint.x,
                0,
                -(rightBottomPoint.y -leftTopPoint.y));

        Vector3d roofBottomPointVector = new Vector3d(
                leftBottomPoint.x - rightBottomPoint.x ,
                h1,
                -(leftBottomPoint.y - rightBottomPoint.y));


        Vector3d roofTopLineVector = new Vector3d(
                leftTopPoint.x - rightBottomPoint.x ,
                0,
                -(leftTopPoint.y - rightBottomPoint.y));

        Vector3d roofTopPointVector = new Vector3d(
                rightTopPoint.x - leftTopPoint.x ,
                h2,
                -(rightTopPoint.y - leftTopPoint.y));




        Vector3d nb = new Vector3d();
        nb.cross(roofBottomPointVector, roofBottomLineVector );
        nb.normalize();

        Vector3d nt = new Vector3d();
        nt.cross(roofTopPointVector, roofTopLineVector );
        nt.normalize();




        Point3d planeRightTopPoint =  new Point3d(
                rightTopPoint.x ,
                middleLineHeight + h2,
                -rightTopPoint.y);

        Point3d planeLeftBottomPoint =  new Point3d(
                leftBottomPoint.x ,
                middleLineHeight + h1,
                -leftBottomPoint.y);


        Plane3d planeTop = new Plane3d(
                planeRightTopPoint,
                nt);


        Plane3d planeBottom = new Plane3d(
                planeLeftBottomPoint,
                nb);




        //        Vector3d roofLeftLineVector = new Vector3d(
        //                0,
        //                0,
        //                pRecHeight);
        //
        //        Vector3d roofRightLineVector = new Vector3d(
        //                0,
        //                0,
        //                -pRecHeight);

        //        Vector3d roofTopLineVector = new Vector3d(
        //                -pRecWidth,
        //                0,
        //                0);
        //        Vector3d roofButtomLineVector = new Vector3d(
        //                pRecWidth,
        //                0,
        //                0);

        if (middleLineHeight <= 0) {
            // for texturing
            // textures change direction when h1 and h1 are below zero.
            roofBottomLineVector.negate();
            roofTopLineVector.negate();
        }

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);



        List<Point2d> borderSplit = RoofTypeUtil.splitBorder(borderPolygon, lLine);

        List<Double> borderHeights = calcHeightList(
                borderSplit, lLine,
                planeBottom, planeTop);

        ////******************

        RoofTypeUtil.makeRoofBorderMesh(

                borderSplit,
                borderHeights,

                meshBorder,
                facadeTexture
                );



        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(Math.abs(h1), Math.abs(h2)));

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs =
                buildRectRoofHooksSpace(
                        pRectangleContur,
                        new PolygonPlane(bottomMP, planeBottom),
                        new PolygonPlane(topMP, planeTop),
                        new PolygonPlane(topMP, planeTop),
                        new PolygonPlane(bottomMP, planeBottom)
                        );

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }



    private List<Double> calcHeightList(
            List<Point2d> pSplitBorder,
            LinePoints2d lLine,
            Plane3d planeButtom, Plane3d planeTop) {


        List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
        for (Point2d point : pSplitBorder) {

            double height = calcHeight(point, lLine, planeButtom, planeTop);

            borderHeights.add(height);

        }

        return borderHeights;
    }

    /** Calc height of point in border.
     * @param point
     * @param rLine
     * @param lLine
     * @param planeLeft
     * @param planeRight
     * @param planeButtom
     * @param planeTop
     * @return
     */
    private double calcHeight(Point2d point,
            LinePoints2d lLine,
            Plane3d planeButtom, Plane3d planeTop) {

        double x = point.x;
        double z = -point.y;

        if (lLine.inFront(point)) {
            return planeButtom.calcYOfPlane(x, z);

        } else {
            return planeTop.calcYOfPlane(x, z);
        }

    }






}
