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
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Material;
import kendzi.josm.kendzi3d.jogl.model.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonSplitUtil;
import kendzi.math.geometry.polygon.split.SplitPolygon;

import org.apache.log4j.Logger;

/**
 * Roof type 2.8.
 *
 * @author Tomasz Kêdziora (Kendzi)
 *
 */
public class RoofType2_8 extends RectangleRoofType{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2_8.class);

    @Override
    public String getPrefixKey() {
        return "2.8";
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(
            List<Point2d> border,
            Point2d[] rectangleContur,
            double scaleA,
            double scaleB,
            double pRecHeight,
            double pRecWidth,
            Integer prefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData
            ) {



        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 2.5d);
        Double h2 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_2, h1);

//        Double l1 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_1, pRecWidth, pRecWidth / 2d);
//        Double l2 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_2, pRecHeight, pRecHeight / 2d);


        double middleLineHeight = getMiddleLineHeight(h1, h2);

        h1 = getHeight1(h1);
        h2 = getHeight2(h2);

        return build(border, scaleA, scaleB, pRecHeight, pRecWidth, rectangleContur, h1, h2, middleLineHeight, pRoofTextureData);

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
     * @param middleLineHeight
     * @param h3
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
            double middleLineHeight,

            RoofTextureData pRoofTextureData
           ) {



        ModelFactory model = ModelFactory.modelBuilder();
        MeshFactory meshBorder = model.addMesh("roof_border");
        MeshFactory meshRoof = model.addMesh("roof_top");

        //XXX move it
        TextureData facadeTexture = pRoofTextureData.getFacadeTextrure();
        TextureData roofTexture = pRoofTextureData.getRoofTexture();
        Material facadeMaterial = MaterialFactory.createTextureMaterial(facadeTexture.getFile());
        Material roofMaterial = MaterialFactory.createTextureMaterial(roofTexture.getFile());
        // XXX move material
        int facadeMaterialIndex = model.addMaterial(facadeMaterial);
        int roofMaterialIndex = model.addMaterial(roofMaterial);

        meshBorder.materialID = facadeMaterialIndex;
        meshBorder.hasTexture = true;

        meshRoof.materialID = roofMaterialIndex;
        meshRoof.hasTexture = true;

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


        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        SplitPolygon leftSplit = PolygonSplitUtil.splitPolygon(borderPolygon, lLine);

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

        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);



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

        rto.setModel(model);

        RoofHooksSpace [] rhs =
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
