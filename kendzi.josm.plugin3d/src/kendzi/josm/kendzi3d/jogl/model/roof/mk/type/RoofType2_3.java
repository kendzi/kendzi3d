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

import org.apache.log4j.Logger;

/**
 * Roof type 2.3.
 *
 * @author Tomasz Kêdziora (Kendzi)
 *
 */
public class RoofType2_3 extends RectangleRoofType{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2_3.class);

    @Override
    public String getPrefixKey() {
        return "2.3";
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
            double pSizeA,
            double pSizeB,
            Integer prefixParameter,
            List<Double> heights,
            List<Double> sizeB,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData
            ) {

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 2.5d);

        Double h2 = getHeight2(pMeasurements);

//        Double h2 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_2, 1.5d);

        Double b1 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_1, pSizeA, pSizeA /2d);
        Double b2 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_2, pSizeB, pSizeB /4d);
        Double b3 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_3, pSizeA, pSizeA /2d);

        if (h2 > h1) {
            throw new RuntimeException("bad parameters: h1 is biger then h2");
        }

        boolean skipLeft = getSkipLeft();

        return build(border, scaleA, scaleB, pSizeA, pSizeB, rectangleContur,
                h1, h2, b1, b2, b3, skipLeft, pRoofTextureData);

    }

    protected double getHeight2(Map<MeasurementKey, Measurement> pMeasurements) {
        return getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_2, 1.5d);
    }

    protected boolean getSkipLeft() {
        return false;
    }



    @Override
    protected boolean normalizeAB() {
        return false;
    }

    /**
     *
     * <img src="doc-files/RoofType2_3.png">
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
            double b1,
            double b2,
            double b3,
            boolean skipLeft,
            RoofTextureData pRoofTextureData) {


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

        double sizeB1 = b1;
        double sizeB2 = b2;
        double sizeB3 = b3;

        double sizeB1Oposit = pRecHeight - sizeB1;

        LinePoints2d middleLine = new LinePoints2d(
                new Point2d(0, sizeB1), new Point2d(pRecWidth, sizeB1));



        Vector3d normalTop = new Vector3d(0, sizeB1Oposit,  -h1);
        normalTop.normalize();

        Vector3d normalBottom = new Vector3d(0, sizeB1, h1);
        normalBottom.normalize();

        Point3d planePoint =  new Point3d(
                (middleLine.getP1().x) ,
                h1,
                -(middleLine.getP1().y));

        Plane3d planeTop = new Plane3d(planePoint, normalTop);
        Plane3d planeBottom = new Plane3d(planePoint, normalBottom);


        double leftTopY = -planeTop.calcZOfPlane(0, h2);
        double leftBottomY = -planeBottom.calcZOfPlane(0, h2);

        Point2d leftTop = new Point2d(0, leftTopY);
        Point2d leftCenter = new Point2d(sizeB2, sizeB1);
        Point2d leftBottom = new Point2d(0, leftBottomY);

        if (skipLeft) {
            // draw only on right site

            leftTop = new Point2d(-0.01, pRecHeight);
            leftCenter = new Point2d(-0.01, sizeB1);
            leftBottom = new Point2d(-0.01, 0);
        }

        Point2d rightTop = new Point2d(pRecWidth, leftTopY);
        Point2d rightCenter = new Point2d(pRecWidth - sizeB2, sizeB1);
        Point2d rightBottom = new Point2d(pRecWidth, leftBottomY);



        LinePoints2d leftBottomLine = new LinePoints2d(
                leftBottom, leftCenter);
        LinePoints2d leftTopLine = new LinePoints2d(
                leftCenter, leftTop);

        LinePoints2d rightBottomLine = new LinePoints2d(
                rightBottom, rightCenter);
        LinePoints2d rightTopLine = new LinePoints2d(
                rightCenter, rightTop);


        double leftHeight = planeBottom.calcYOfPlane(leftBottom.x, -leftBottom.y);
        double rightHeight = planeBottom.calcYOfPlane(rightBottom.x, -rightBottom.y);

        Plane3d planeLeft = new Plane3d(
                new Point3d(leftCenter.x, h1, -leftCenter.y),
                new Vector3d(-(h1 - leftHeight),  sizeB2, 0));

        Plane3d planeRight = new Plane3d(
                new Point3d(rightCenter.x, h1, -rightCenter.y),
                new Vector3d(-(rightHeight - h1), sizeB2, 0));

        ////******************
        List<Point2d> border = new ArrayList<Point2d>();

        for (Point2d ppp : pBorderList) {
            border.add(new Point2d(ppp.x, ppp.y));
        }
        if (border.get(border.size() - 1).equals(border.get(0))) {
            border.remove(border.size() - 1);
        }


        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        MultiPolygonList2d mpb = borderPolygon.intersectionOpen(
                new PolygonList2d(Arrays.asList(rightBottom,  rightCenter, leftCenter, leftBottom)));
        MultiPolygonList2d mpt = borderPolygon.intersectionOpen(
                new PolygonList2d(Arrays.asList(leftTop, leftCenter, rightCenter, rightTop)));
        MultiPolygonList2d mpl = borderPolygon.intersection(
                new PolygonList2d(Arrays.asList(leftBottom, leftCenter, leftTop)));
        MultiPolygonList2d mpr = borderPolygon.intersection(
                new PolygonList2d(Arrays.asList(rightTop, rightCenter, rightBottom)));



        Vector3d roofLeftLineVector = new Vector3d(
                0,
                0,
                pRecWidth);

        Vector3d roofRightLineVector = new Vector3d(
                0,
                0,
                -pRecWidth);

        Vector3d roofTopLineVector = new Vector3d(
                -pRecHeight,
                0,
                0);
        Vector3d roofButtomLineVector = new Vector3d(
                pRecHeight,
                0,
                0);

        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, mpl, planeLeft, roofLeftLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, mpr, planeRight, roofRightLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, mpt, planeTop, roofTopLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, mpb, planeBottom, roofButtomLineVector, roofTexture);


        ////******************

        List<Point2d> borderSplit = RoofTypeUtil.splitBorder(
                borderPolygon, middleLine, rightTopLine, rightBottomLine, leftTopLine, leftBottomLine);

        List<Double> borderHeights = calcHeightList(
                borderSplit,  middleLine, rightTopLine, rightBottomLine, leftTopLine, leftBottomLine,
                planeLeft, planeRight, planeTop, planeBottom);


        RoofTypeUtil.makeRoofBorderMesh(
                borderSplit,
                borderHeights,

                meshBorder,
                facadeTexture
                );

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);

        rto.setModel(model);

        RoofHooksSpace [] rhs =
            buildRectRoofHooksSpace(
                    pRectangleContur,
                    new PolygonPlane(mpb, planeBottom),
                    null,
                    new PolygonPlane(mpt, planeTop),
                    null
                  );

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    /** Heights of point on border.
     * @param pSplitBorder border after split
     * @param pMiddleLine middle line
     * @param pRightTopLine right top line
     * @param pRightBottomLine right bottom line
     * @param pLeftTopLine left top line
     * @param pLeftBottomLine left bottom line
     * @param pPlaneLeft plane left
     * @param pPlaneRight plane right
     * @param pPlaneTop plane top
     * @param pPlaneBottom plane bottom
     * @return heights heights
     */
    private List<Double> calcHeightList(
            List<Point2d> pSplitBorder,

            LinePoints2d pMiddleLine,
            LinePoints2d pRightTopLine,
            LinePoints2d pRightBottomLine,
            LinePoints2d pLeftTopLine,
            LinePoints2d pLeftBottomLine,

            Plane3d pPlaneLeft,
            Plane3d pPlaneRight,
            Plane3d pPlaneTop,
            Plane3d pPlaneBottom) {

        List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
        for (Point2d point : pSplitBorder) {

            double height = calcHeight(point,
                    pMiddleLine, pRightTopLine, pRightBottomLine, pLeftTopLine, pLeftBottomLine,
                    pPlaneLeft, pPlaneRight, pPlaneTop, pPlaneBottom);

            borderHeights.add(height);
        }

        return borderHeights;
    }

    /** Calculate height of point.
     *
     * @param pPoint point to find height     *
     * @param pMiddleLine middle line
     * @param pRightTopLine right top line
     * @param pRightBottomLine right bottom line
     * @param pLeftTopLine left top line
     * @param pLeftBottomLine left bottom line
     * @param pPlaneLeft plane left
     * @param pPlaneRight plane right
     * @param pPlaneTop plane top
     * @param pPlaneBottom plane bottom
     * @return height of point
     */
    private double calcHeight(Point2d pPoint,
        LinePoints2d pMiddleLine,
        LinePoints2d pRightTopLine,
        LinePoints2d pRightBottomLine,
        LinePoints2d pLeftTopLine,
        LinePoints2d pLeftBottomLine,

        Plane3d pPlaneLeft,
        Plane3d pPlaneRight,
        Plane3d pPlaneTop,
        Plane3d pPlaneBottom) {

        double x = pPoint.x;
        double z = -pPoint.y;

        if (pMiddleLine.inFront(pPoint)) {
            if (pLeftTopLine.inFront(pPoint)) {
                return pPlaneLeft.calcYOfPlane(x, z);
            } else if (!pRightTopLine.inFront(pPoint)) {
                return pPlaneRight.calcYOfPlane(x, z);
            } else {
                return pPlaneTop.calcYOfPlane(x, z);
            }

        } else {

            if (pLeftBottomLine.inFront(pPoint)) {
                return pPlaneLeft.calcYOfPlane(x, z);
            } else if (!pRightBottomLine.inFront(pPoint)) {
                return pPlaneRight.calcYOfPlane(x, z);
            } else {
                return pPlaneBottom.calcYOfPlane(x, z);
            }
        }
    }
}
