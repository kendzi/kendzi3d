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
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RectangleRoofHooksSpaces;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonSplitUtil;
import kendzi.math.geometry.polygon.split.SplitPolygon;
import kendzi.math.geometry.polygon.split.SplitPolygons;

import org.apache.log4j.Logger;

/**
 * Roof type 2.7.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType2_7 extends RectangleRoofType{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2_7.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE2_7;
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

        boolean left = isLeft();

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 2.5d);

        Double l1 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_1, pRecWidth, pRecWidth / 2d);
        Double l2 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_2, pRecHeight, pRecHeight / 2d);



        return build(border, scaleA, scaleB, pRecHeight, pRecWidth, rectangleContur, h1, l1, l2, pRoofTextureData, left);

    }

    public boolean isLeft() {
        return true;
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
     * @param h3
     * @param pRoofTextureData
     * @param isLeft
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
            double l1,
            double l2,
            RoofTextureData pRoofTextureData,
            boolean isLeft) {


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


        Vector3d nl = new Vector3d(-h1 , l2 , 0);
        nl.normalize();
        Vector3d nr = new Vector3d(h1, l1 , 0);
        nr.normalize();
        Vector3d nb = new Vector3d(0, pRecHeight  , h1);
        nb.normalize();


        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        SplitPolygon leftSplit = PolygonSplitUtil.splitPolygon(borderPolygon, lLine);

        MultiPolygonList2d leftMP = leftSplit.getTopMultiPolygons();
        MultiPolygonList2d middleMP = leftSplit.getBottomMultiPolygons();


        SplitPolygons rightSplit = PolygonSplitUtil.splitMultiPolygon(middleMP, rLine);

        MultiPolygonList2d rightMP = rightSplit.getTopMultiPolygons();
        middleMP = rightSplit.getBottomMultiPolygons();


        Point3d planeLeftPoint =  new Point3d(
                leftTopPoint.x ,
                h1,
                -leftTopPoint.y);

        Point3d planeRightPoint =  new Point3d(
                rightTopPoint.x ,
                h1,
                -rightTopPoint.y);



        Plane3d planeLeft = new Plane3d(
                planeLeftPoint,
                nl);

        Plane3d planeRight = new Plane3d(
                planeRightPoint,
                nr);


        Plane3d planeBottom = new Plane3d(
                planeRightPoint,
                nb);

//        List<Point2d> border = new ArrayList<Point2d>();
//
//        for (Point2d ppp : pBorderList) {
//            border.add(new Point2d(ppp.x, ppp.y));
//        }
//        if (border.get(border.size() - 1).equals(border.get(0))) {
//            border.remove(border.size() - 1);
//        }

















        Vector3d roofLeftLineVector = new Vector3d(
                0,
                0,
                pRecHeight);

        Vector3d roofRightLineVector = new Vector3d(
                0,
                0,
                -pRecHeight);

        Vector3d roofTopLineVector = new Vector3d(
                -pRecWidth,
                0,
                0);
        Vector3d roofButtomLineVector = new Vector3d(
                pRecWidth,
                0,
                0);



        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, leftMP, planeLeft, roofLeftLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, rightMP, planeRight, roofRightLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, middleMP, planeBottom, roofButtomLineVector, roofTexture);



        List<Point2d> borderSplit = RoofTypeUtil.splitBorder(borderPolygon, lLine, rLine);

        List<Double> borderHeights = calcHeightList(
                borderSplit, lLine, rLine,
                planeLeft, planeRight, planeBottom);



        ////******************

        RoofTypeUtil.makeRoofBorderMesh(

               borderSplit,
               borderHeights,

               meshBorder,
               facadeTexture
               );



        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);

        rto.setModel(model);

        RectangleRoofHooksSpaces rhs =
            buildRectRoofHooksSpace(
                    pRectangleContur,
                    new PolygonPlane(middleMP, planeBottom),
                    new PolygonPlane(rightMP, planeRight),
                   null,
                    (isLeft ? new PolygonPlane(leftMP, planeLeft) : null)
                  );

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }



    private List<Double> calcHeightList(
            List<Point2d> pSplitBorder,
            LinePoints2d lLine, LinePoints2d rLine,
            Plane3d planeLeft, Plane3d planeRight, Plane3d planeButtom) {


        List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
        for (Point2d point : pSplitBorder) {

           double height = calcHeight(point, rLine, lLine, planeLeft, planeRight, planeButtom);

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
     * @return
     */
    private double calcHeight(Point2d point,
            LinePoints2d rLine, LinePoints2d lLine,
            Plane3d planeLeft, Plane3d planeRight, Plane3d planeButtom) {

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
