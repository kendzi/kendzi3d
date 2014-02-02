/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
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
public class RoofType1v1 extends RectangleRoofTypeBuilder {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofType1v1.class);

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 0d);
        Double h2 = getHeightDegreesMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_2, 0, conf.getRecHeight(), 30d);
        Double h3 = getHeightDegreesMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_3, 0, conf.getRecWidth(), 30d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, h2, h3,
                conf.getRoofTextureData());
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
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double pRecHeight, double pRecWidth,
            Point2d[] pRectangleContur, double h1, double h2, double h3, RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);
        MultiPolygonList2d topMP = new MultiPolygonList2d(borderPolygon);

        Vector3d roofTopLineVector = new Vector3d(pRecWidth, 0, 0);

        Plane3d planeTop = createRoofPlane(h1, h2, h3, pRecWidth, pRecHeight);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);

        List<Point2d> borderSplit = RoofTypeUtil.splitBorder(borderPolygon);

        List<Double> borderHeights = calcHeightList(borderSplit, planeTop);

        // //******************

        RoofTypeUtil.makeRoofBorderMesh(

        borderSplit, borderHeights,

        meshBorder, facadeTexture);

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(Math.max(h1, h2), h3));

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs = buildRectRoofHooksSpace(pRectangleContur, new PolygonPlane(topMP, planeTop),
                new PolygonPlane(topMP, planeTop), new PolygonPlane(topMP, planeTop), new PolygonPlane(topMP, planeTop));

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    /**
     * Create roof plane.
     * 
     * @param h1
     *            height 1
     * @param h2
     *            height 2
     * @param h3
     *            height 3
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

    private List<Double> calcHeightList(List<Point2d> pSplitBorder, Plane3d planeTop) {

        List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
        for (Point2d point : pSplitBorder) {

            double height = calcHeight(point, planeTop);

            borderHeights.add(height);

        }

        return borderHeights;
    }

    /**
     * Calc height of point in border.
     * 
     * @param point
     * @param planeTop
     * @return
     */
    private double calcHeight(Point2d point, Plane3d planeTop) {

        double x = point.x;
        double z = -point.y;

        return planeTop.calcYOfPlane(x, z);
    }
}
