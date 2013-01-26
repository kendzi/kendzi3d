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
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

import org.apache.log4j.Logger;

/**
 * Roof type 1.1.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType1_1 extends RectangleRoofTypeBuilder{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType1_1.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE1_1;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(
            PolygonWithHolesList2d buildingPolygon,
            Point2d[] rectangleContur,
            double pScaleA,
            double pScaleB,
            double pRecHeight,
            double pRecWidth,
            Integer prefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofMaterials pRoofTextureData
            ) {

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 0d);
        Double h2 = getHeightDegreesMeters(pMeasurements, MeasurementKey.HEIGHT_2, 0, pRecHeight, 30d);
        Double h3 = getHeightDegreesMeters(pMeasurements, MeasurementKey.HEIGHT_3, 0, pRecWidth, 30d);
//        Double h3 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_3, h2);


        return build(buildingPolygon, pScaleA, pScaleB, pRecHeight, pRecWidth, rectangleContur, h1, h2, h3, pRoofTextureData);

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
     * @param h2
     * @param h3
     * @param pRoofTextureData
     * @return
     */
    protected RoofTypeOutput build(
            PolygonWithHolesList2d buildingPolygon,
            double pScaleA,
            double pScaleB,
            double pRecHeight,
            double pRecWidth,
            Point2d[] pRectangleContur,
            double h1,
            double h2,
            double h3,
//            ModelFactory model,
            RoofMaterials pRoofTextureData) {




        MeshFactory meshBorder = createFacadeMesh(pRoofTextureData);
        MeshFactory meshRoof = createRoofMesh(pRoofTextureData);

        TextureData facadeTexture = pRoofTextureData.getFacade().getTextureData();
        TextureData roofTexture = pRoofTextureData.getRoof().getTextureData();


        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();


        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);
        MultiPolygonList2d topMP = new MultiPolygonList2d(borderPolygon);


        Vector3d roofTopLineVector = new Vector3d(
                pRecWidth,
                0,
                0);


        Plane3d planeTop = createRoofPlane(h1, h2, h3, pRecWidth, pRecHeight);

        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);

        List<Point2d> borderSplit = RoofTypeUtil.splitBorder(borderPolygon);

        List<Double> borderHeights = calcHeightList(
                borderSplit,
                planeTop);



        ////******************

        RoofTypeUtil.makeRoofBorderMesh(

               borderSplit,
               borderHeights,

               meshBorder,
               facadeTexture
               );



        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(Math.max(h1, h2), h3));

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs =
            buildRectRoofHooksSpace(
                    pRectangleContur,
                    new PolygonPlane(topMP, planeTop),
                    new PolygonPlane(topMP, planeTop),
                    new PolygonPlane(topMP, planeTop),
                    new PolygonPlane(topMP, planeTop)
                  );

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    /**
     * Create roof plane.
     *
     * @param h1 height 1
     * @param h2 height 2
     * @param h3 height 3
     * @param pRecHeight
     * @param pRecWidth
     * @return roof plane
     */
    private Plane3d createRoofPlane(double h1, double h2, double h3, double pRecWidth, double pRecHeight) {

        Point3d p1 = new Point3d(0, h1, 0);

        Vector3d v2 = new Vector3d(0, h2, -pRecHeight);
        v2.sub(p1);

        Vector3d v3 = new Vector3d(pRecWidth, h3, 0);
        v3.sub(p1);

        Vector3d normal = new Vector3d();
        normal.cross(v3, v2);

        return new Plane3d(p1, normal);
    }

    private List<Double> calcHeightList(
            List<Point2d> pSplitBorder,
            Plane3d planeTop) {

        List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
        for (Point2d point : pSplitBorder) {

           double height = calcHeight(point,
                   planeTop);

           borderHeights.add(height);

        }

        return borderHeights;
    }

    /** Calc height of point in border.
     * @param point
     * @param planeTop
     * @return
     */
    private double calcHeight(Point2d point,
            Plane3d planeTop) {

        double x = point.x;
        double z = -point.y;

        return planeTop.calcYOfPlane(x, z);
    }
}
