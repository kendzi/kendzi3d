/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

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
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.math.geometry.Graham;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.CircleInsidePolygon;
import kendzi.math.geometry.polygon.CircleInsidePolygon.Circle;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitUtil;

import org.apache.log4j.Logger;

/**
 * Roof type Tented.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofTypeTented extends RectangleRoofTypeBuilder{

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofTypeTented.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.TENTED;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(
            PolygonWithHolesList2d buildingPolygon,
            Point2d[] rectangleContur,
            double scaleA,
            double scaleB,
            double pRecHeight,
            double pRecWidth,
            Integer prefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofMaterials pRoofTextureData
            ) {

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 2.5d);

        return build(buildingPolygon, pRecHeight, pRecWidth, rectangleContur, h1, pRoofTextureData);

    }

    @Override
    protected boolean normalizeAB() {
        return false;
    }

    /**
     *
     * @param buildingPolygon
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param pRoofTextureData
     * @return
     */
    protected RoofTypeOutput build(
            PolygonWithHolesList2d buildingPolygon,
            double pRecHeight,
            double pRecWidth,
            Point2d[] pRectangleContur,
            double h1,
            RoofMaterials pRoofTextureData) {


        MeshFactory meshBorder = createFacadeMesh(pRoofTextureData);
        MeshFactory meshRoof = createRoofMesh(pRoofTextureData);

      //  TextureData facadeTexture = pRoofTextureData.getFacade().getTextureData();
        TextureData roofTexture = pRoofTextureData.getRoof().getTextureData();


        List<Point2d> buildingOutline = buildingPolygon.getOuter().getPoints();

        // XXX temporary ?
        if (0.0f > Triangulate.area(buildingOutline)) {
            buildingOutline = PolygonList2d.reverse(buildingOutline);
        }
        List<Point2d> grahamScan = Graham.grahamScan(buildingOutline);
        Circle circle = CircleInsidePolygon.iterativeNonConvex(new PolygonList2d(grahamScan), 0.01);

        PolygonList2d borderPolygon = new PolygonList2d(buildingOutline);

        Point2d middlePoint = circle.getPoint();


        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon1 = new PolygonList2d(pBorderList);
        MultiPolygonList2d borderMultiPolygon = new MultiPolygonList2d(borderPolygon1);

        MultiPolygonList2d mp[] = createMP(buildingOutline, borderMultiPolygon, middlePoint);
        Plane3d planse[] = createPlanes(buildingOutline, h1, middlePoint);

       // LinePoints2d [] lines = createLines(buildingOutline, middlePoint);



        Vector3d [] roofLine = createRoofLines(buildingOutline);

        double [] textureOffset = createTextureOffset(buildingOutline);

        for (int i = 0; i < mp.length; i++) {

            RoofTypeUtil.addPolygonToRoofMesh(meshRoof, mp[i], planse[i], roofLine[i], roofTexture, textureOffset[i], 0);

        }

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

//        RectangleRoofHooksSpaces rhs =
//            buildRectRoofHooksSpace(
//                    pRectangleContur,
//                    new PolygonPlane(mpb, planeBottom),
//                    new PolygonPlane(mpr, planeRight),
//                    new PolygonPlane(mpt, planeTop),
//                    new PolygonPlane(mpl, planeLeft)
//                  );
//
//        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    private double[] createTextureOffset(List<Point2d> polygon) {
        int size = polygon.size();
        double[] ret = new double[size];

        double distance = 0;

        for (int i = 0; i < size; i++) {
            ret[i] = distance;

            Point2d p1 = polygon.get(i);
            Point2d p2 = polygon.get((i+1) % size);

            distance += p1.distance(p2);
        }

        return ret;
    }

    private Vector3d[] createRoofLines(List<Point2d> polygon) {

        int size = polygon.size();
        Vector3d[] ret = new Vector3d[size];
        for (int i = 0; i < size; i++) {
            Point2d p1 = polygon.get(i);
            Point2d p2 = polygon.get((i+1) % size);

            ret[i] = new Vector3d(p2.x - p1.x, 0, -(p2.y - p1.y));
           // ret[i].negate();
        }

        return ret;
    }

    private LinePoints2d [] createLines(List<Point2d> polygon, Point2d middlePoint) {
        int size = polygon.size();
        LinePoints2d[] ret = new LinePoints2d[size];
        for (int i = 0; i < size; i++) {
            Point2d p1 = polygon.get(i);

            LinePoints2d l = new LinePoints2d(p1, middlePoint);

            ret[i] = l;
        }

        return ret;
    }

    private Plane3d[] createPlanes(List<Point2d> polygon, double height, Point2d m) {

        int size = polygon.size();
        Plane3d[] ret = new Plane3d[size];
       // Point3d center = new Point3d(m.x, height, -m.y);
        for (int i = 0; i < size; i++) {
            Point2d p1 = polygon.get(i);
            Point2d p2 = polygon.get((i+1) % size);

            Vector3d v1 = new Vector3d(p1.x - m.x, -height, -(p1.y - m.y));
            Vector3d v2 = new Vector3d(p2.x - m.x, -height, -(p2.y - m.y));

            v1.cross(v1, v2);
            v1.normalize();

            Point3d point = new Point3d(p1.x, 0, -p1.y);
//            Point3d point = new Point3d(p2.x, 0, -p2.y);

            Plane3d p = new Plane3d(point, v1);

            ret[i] = p;
        }

        return ret;
    }

    private MultiPolygonList2d[] createMP(List<Point2d> polygon, MultiPolygonList2d borderMultiPolygon, Point2d middlePoint) {

        int size = polygon.size();
        MultiPolygonList2d[] ret = new MultiPolygonList2d[size];

        for (int i = 0; i < size; i++) {
            Point2d p1 = polygon.get(i);
            Point2d p2 = polygon.get((i+1) % size);

            ret[i] = PolygonSplitUtil.intersectionOfFrontPart(borderMultiPolygon, p2, middlePoint, p1);
        }

        return ret;
    }
}
