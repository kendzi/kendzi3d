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
 * Roof type 2.5.
 *
 * @author Tomasz Kêdziora (Kendzi)
 *
 */
public class RoofType2_5 extends RectangleRoofType{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2_5.class);

    @Override
    public String getPrefixKey() {
        return "2.5";
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
            List<Double> heights,
            List<Double> sizeB,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData
            ) {

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 2.5d);

        Double b1 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_1, pRecWidth, pRecWidth / 2d);
        Double b2 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_2, pRecHeight, pRecHeight / 2d);



        return build(border, scaleA, scaleB, pRecHeight, pRecWidth, rectangleContur, h1, b1, b2, pRoofTextureData);

    }

    @Override
    protected boolean normalizeAB() {
        return false;
    }

    /**
     *
     * <img src="doc-files/RoofType2_5.png">
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
            double b1,
            double b2,
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

        Point2d middlePoint = new Point2d(b1, b2);
        Point2d plb = new Point2d(0, 0);
        Point2d plt = new Point2d(0, pRecHeight);
        Point2d prb = new Point2d(pRecWidth, 0);
        Point2d prt = new Point2d(pRecWidth, pRecHeight);







        LinePoints2d ltLine = new LinePoints2d(plt, middlePoint);
        LinePoints2d lbLine = new LinePoints2d(plb, middlePoint);
        LinePoints2d rtLine = new LinePoints2d(middlePoint, prt);
        LinePoints2d rbLine = new LinePoints2d(middlePoint,  prb);


        Vector3d nl = new Vector3d(-h1 , b1 , 0);
        nl.normalize();
        Vector3d nr = new Vector3d(h1, pRecWidth - b1 , 0);
        nr.normalize();

        Vector3d nt = new Vector3d(0,  pRecHeight - b2  , -h1);
        nt.normalize();
        Vector3d nb = new Vector3d(0, b2  , h1);
        nb.normalize();


        Point3d planePoint =  new Point3d(
                middlePoint.x ,
                h1,
                -middlePoint.y);



        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        MultiPolygonList2d mpb = borderPolygon.intersection(
                new PolygonList2d(Arrays.asList(plb,  prb, middlePoint)));
        MultiPolygonList2d mpt = borderPolygon.intersection(
                new PolygonList2d(Arrays.asList(plt, middlePoint, prt)));
        MultiPolygonList2d mpl = borderPolygon.intersection(
                new PolygonList2d(Arrays.asList(plb, middlePoint, plt)));
        MultiPolygonList2d mpr = borderPolygon.intersection(
                new PolygonList2d(Arrays.asList(middlePoint, prb, prt)));





        Plane3d planeLeft = new Plane3d(
                planePoint,
                nl);

        Plane3d planeRight = new Plane3d(
                planePoint,
                nr);

        Plane3d planeTop = new Plane3d(
                planePoint,
                nt);

        Plane3d planeBottom = new Plane3d(
                planePoint,
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



        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, mpl, planeLeft, roofLeftLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, mpr, planeRight, roofRightLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, mpt, planeTop, roofTopLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, mpb, planeBottom, roofButtomLineVector, roofTexture);


        List<Point2d> borderSplit = RoofTypeUtil.splitBorder(borderPolygon, ltLine, lbLine, rtLine, rbLine);

        List<Double> borderHeights = calcHeightList(
                borderSplit, ltLine, lbLine, rtLine, rbLine,
                planeLeft, planeRight, planeTop, planeBottom);



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

        RoofHooksSpace [] rhs =
            buildRectRoofHooksSpace(
                    pRectangleContur,
                    new PolygonPlane(mpb, planeBottom),
                    new PolygonPlane(mpr, planeRight),
                    new PolygonPlane(mpt, planeTop),
                    new PolygonPlane(mpl, planeLeft)
                  );

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }



    private List<Double> calcHeightList(List<Point2d> pSplitBorder, LinePoints2d lt, LinePoints2d lb, LinePoints2d rt, LinePoints2d rb, Plane3d planeLeft, Plane3d planeRight, Plane3d planeTop, Plane3d planeButtom) {

        List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
        for (Point2d point : pSplitBorder) {

           double height = calcHeight(point, lt, lb, rt, rb, planeLeft, planeRight, planeTop, planeButtom);

           borderHeights.add(height);

        }

        return borderHeights;
    }

    /** Calc height of point in border.
     * @param point point
     * @param lt
     * @param lb
     * @param rt
     * @param rb
     * @param planeLeft
     * @param planeRight
     * @param planeTop
     * @param planeButtom
     * @return height of point
     */
    private double calcHeight(Point2d point,
            LinePoints2d lt, LinePoints2d lb, LinePoints2d rt, LinePoints2d rb,
            Plane3d planeLeft, Plane3d planeRight, Plane3d planeTop, Plane3d planeButtom) {

        double x = point.x;
        double z = -point.y;

        if (lt.inFront(point)) {
            if (rt.inFront(point)) {
                return planeTop.calcYOfPlane(x, z);
            }

        } else {
            if (lb.inFront(point)) {
                return planeLeft.calcYOfPlane(x, z);
            }
        }

        if (rb.inFront(point)) {
            if (!rt.inFront(point)) {
                return planeRight.calcYOfPlane(x, z);
            }
        } else {
            if (!lb.inFront(point)) {
                return planeButtom.calcYOfPlane(x, z);
            }
        }

        return 0;
    }






}
