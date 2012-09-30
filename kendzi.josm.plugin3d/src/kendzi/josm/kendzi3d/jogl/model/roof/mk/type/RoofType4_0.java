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
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RectangleRoofHooksSpaces;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitUtil;
import kendzi.math.geometry.polygon.split.SplitPolygons;

import org.apache.log4j.Logger;

/**
 * Roof type 4.0.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType4_0 extends RectangleRoofTypeBuilder{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType4_0.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE4_0;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(
            List<Point2d> border,
            Point2d[] rectangleContur,
            double pScaleA,
            double pScaleB,
            double pRecHeight,
            double pRecWidth,
            Integer prefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofMaterials pRoofTextureData
            ) {

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 2.5d);
        Double h2 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_2, h1 * 2d/3d);


//        Double l1 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_1, pRecWidth, pRecWidth / 2d);
        Double l2 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_2, pRecHeight /2, (pRecHeight / 2d) * 1d/3d);
//        Double l2 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_2, pRecHeight, pRecHeight / 2d);



        return build(border, pScaleA, pScaleB, pRecHeight, pRecWidth, rectangleContur, h1, h2, l2, pRoofTextureData);

    }

    @Override
    protected boolean normalizeAB() {
        return false;
    }

    /**
     *
     *
     *
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param h2
     * @param l2
     * @param pRoofTextureData
     * @return
     */
    protected RoofTypeOutput build(
            List<Point2d> pBorderList,
            double pScaleA,
            double pScaleB,
            double pRecHeight,
            double pRecWidth,
            Point2d[] pRectangleContur,
            double h1,
            double h2,
            double l2,
            RoofMaterials pRoofTextureData) {


        MeshFactory meshBorder = createFacadeMesh(pRoofTextureData);
        MeshFactory meshRoof = createRoofMesh(pRoofTextureData);

        TextureData facadeTexture = pRoofTextureData.getFacade().getTextureData();
        TextureData roofTexture = pRoofTextureData.getRoof().getTextureData();

        Point2d rightTopPoint = new Point2d(pRecWidth, pRecHeight - l2);
        Point2d rightMiddlePoint = new Point2d(pRecWidth, 0.5d * pRecHeight);
        Point2d rightBottomPoint = new Point2d(pRecWidth, l2);

        Point2d leftTopPoint = new Point2d(0, pRecHeight - l2);
        Point2d leftMiddlePoint = new Point2d(0, 0.5d * pRecHeight);
        Point2d leftBottomPoint = new Point2d(0, l2);




        LinePoints2d tLine = new LinePoints2d(leftTopPoint, rightTopPoint);
        LinePoints2d mLine = new LinePoints2d(leftMiddlePoint, rightMiddlePoint);
        LinePoints2d bLine = new LinePoints2d(leftBottomPoint, rightBottomPoint);


        Vector3d nt = new Vector3d(0, l2  , -(h2));
        nt.normalize();
        Vector3d nmt = new Vector3d(0, (pRecHeight * 0.5d) - l2  , -(h1 - h2));
        nmt.normalize();

        Vector3d nmb = new Vector3d(0, (pRecHeight * 0.5d) - l2  , (h1 - h2));
        nmb.normalize();
        Vector3d nb = new Vector3d(0, l2  , (h2));
        nb.normalize();


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




        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, topMiddleMP, planeMiddleTop, roofTopLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, bottomMiddleMP, planeMiddleBottom, roofBottomLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);



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
