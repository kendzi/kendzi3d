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
 * Roof type 2.0.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType2v0 extends RectangleRoofTypeBuilder{

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofType2v0.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE2_0;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double l1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecHeight(),
                conf.getRecHeight() / 2d);

        Double h1 = getHeightDegreesMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 0, l1, 30);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, l1,
                conf.getRoofTextureData());
    }

    /**
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
     * @param recHeight
     * @param recWidth
     * @param rectangleContur
     * @param h1
     * @param l1
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(
            PolygonWithHolesList2d buildingPolygon,
            double recHeight,
            double recWidth,
            Point2d[] rectangleContur,
            double h1,
            double l1,
            RoofMaterials roofTextureData) {


        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        Point2d rightMiddlePoint = new Point2d(recWidth, l1);

        Point2d leftMiddlePoint = new Point2d(0, l1);

        LinePoints2d mLine = new LinePoints2d(leftMiddlePoint, rightMiddlePoint);

        Vector3d nt = new Vector3d(0, l1, -h1);
        nt.normalize();

        Vector3d nb = new Vector3d(0, l1, h1);
        nb.normalize();

        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        SplitPolygons middleSplit = PolygonSplitUtil.split(borderPolygon, mLine);

        MultiPolygonList2d topMP = middleSplit.getTopMultiPolygons();
        MultiPolygonList2d bottomMP = middleSplit.getBottomMultiPolygons();


        Point3d planeLeftPoint =  new Point3d(
                leftMiddlePoint.x ,
                h1,
                -leftMiddlePoint.y);

        Point3d planeRightPoint =  new Point3d(
                rightMiddlePoint.x ,
                h1,
                -rightMiddlePoint.y);

        Plane3d planeTop = new Plane3d(planeRightPoint, nt);
        Plane3d planeBottom = new Plane3d(planeLeftPoint, nb);


        Vector3d roofBottomLineVector = new Vector3d(
                recWidth,
                0,
                0);

        Vector3d roofTopLineVector = new Vector3d(
                -recWidth,
                0,
                0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);


        List<Point2d> borderSplit = RoofTypeUtil.splitBorder(borderPolygon, mLine);

        List<Double> borderHeights = calcHeightList(
                borderSplit, mLine,
                planeTop, planeBottom);



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
                        rectangleContur,
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
            LinePoints2d mLine,
            Plane3d planeTop, Plane3d planeBottom) {



        List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
        for (Point2d point : pSplitBorder) {

            double height = calcHeight(point, mLine,
                    planeTop, planeBottom);

            borderHeights.add(height);

        }

        return borderHeights;
    }

    /** Calc height of point in border.
     * @param point
     * @param mLine
     * @param planeTop
     * @param planeBottom
     * @return
     */
    private double calcHeight(Point2d point,
            LinePoints2d mLine,
            Plane3d planeTop, Plane3d planeBottom) {

        double x = point.x;
        double z = -point.y;

        if (mLine.inFront(point)) {

            return planeTop.calcYOfPlane(x, z);
        } else {

            return planeBottom.calcYOfPlane(x, z);
        }
    }

}
