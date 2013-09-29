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

import org.apache.log4j.Logger;

/**
 * Roof type 2.5.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType2v5 extends RectangleRoofTypeBuilder{

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofType2v5.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE2_5;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);

        Double b1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecWidth(),
                conf.getRecWidth() / 2d);
        Double b2 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_2, conf.getRecHeight(),
                conf.getRecHeight() / 2d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, b1, b2,
                conf.getRoofTextureData());
    }

    /**
     *
     * <img src="doc-files/RoofType2_5.png">
     *
     * @param pBorderList
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param h2
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
            double b1,
            double b2,
            RoofMaterials roofTextureData) {


        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

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


        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);
        MultiPolygonList2d borderMultiPolygon = new MultiPolygonList2d(borderPolygon);

        MultiPolygonList2d mpb =
                PolygonSplitUtil.intersectionOfFrontPart(borderMultiPolygon, plb,  prb, middlePoint, plb);
        MultiPolygonList2d mpt =
                PolygonSplitUtil.intersectionOfFrontPart(borderMultiPolygon, plt, middlePoint, prt, plt);
        MultiPolygonList2d mpl =
                PolygonSplitUtil.intersectionOfFrontPart(borderMultiPolygon, plb, middlePoint, plt, plb);
        MultiPolygonList2d mpr =
                PolygonSplitUtil.intersectionOfFrontPart(borderMultiPolygon, middlePoint, prb, prt, middlePoint);





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



        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpl, planeLeft, roofLeftLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpr, planeRight, roofRightLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpt, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpb, planeBottom, roofButtomLineVector, roofTexture);


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

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs =
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
