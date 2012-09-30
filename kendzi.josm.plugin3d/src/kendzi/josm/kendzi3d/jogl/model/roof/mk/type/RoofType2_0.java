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
 * Roof type 2.0.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType2_0 extends RectangleRoofTypeBuilder{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2_0.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE2_0;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(
            List<Point2d> pBorder,
            Point2d[] pRectangleContur,
            double pScaleA,
            double pScaleB,
            double pRecHeight,
            double pRecWidth,
            Integer pPrefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofMaterials pRoofTextureData
            ) {


        Double l1 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_1, pRecHeight, pRecHeight / 2d);

        Double h1 = getHeightDegreesMeters(pMeasurements, MeasurementKey.HEIGHT_1, 0, l1, 30);


        return build(pBorder, pScaleA, pScaleB, pRecHeight, pRecWidth, pRectangleContur, h1, l1, pRoofTextureData);

    }

    @Override
    protected boolean normalizeAB() {
        return false;
    }

    /**
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param l1
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
            double l1,
            RoofMaterials pRoofTextureData) {


        MeshFactory meshBorder = createFacadeMesh(pRoofTextureData);
        MeshFactory meshRoof = createRoofMesh(pRoofTextureData);

        TextureData facadeTexture = pRoofTextureData.getFacade().getTextureData();
        TextureData roofTexture = pRoofTextureData.getRoof().getTextureData();

        Point2d rightMiddlePoint = new Point2d(pRecWidth, l1);

        Point2d leftMiddlePoint = new Point2d(0, l1);

        LinePoints2d mLine = new LinePoints2d(leftMiddlePoint, rightMiddlePoint);

        Vector3d nt = new Vector3d(0, l1, -h1);
        nt.normalize();

        Vector3d nb = new Vector3d(0, l1, h1);
        nb.normalize();

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
                pRecWidth,
                0,
                0);

        Vector3d roofTopLineVector = new Vector3d(
                -pRecWidth,
                0,
                0);

        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);
        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);


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
