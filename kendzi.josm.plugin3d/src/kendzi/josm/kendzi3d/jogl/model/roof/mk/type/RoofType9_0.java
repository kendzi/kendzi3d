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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.PolygonRoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.math.geometry.Algebra;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.line.LineSegment2d;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.skeleton.Skeleton;
import kendzi.math.geometry.skeleton.Skeleton.Output;

import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

/**
 * Roof type 9.0.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType9_0 extends AbstractRoofTypeBuilder {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType9_0.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE9_0;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRoof(
            Point2d pStartPoint, PolygonWithHolesList2d buildingPolygon, DormerRoofModel pRoof, double height,
            RoofMaterials pRoofTextureData) {

        List<Point2d> pPolygon = buildingPolygon.getOuter().getPoints();

//            Point2d pStartPoint, List<Point2d> border, Integer prefixParameter, double height,
//            Map<MeasurementKey, Measurement> pMeasurements, RoofTextureData pRoofTextureData) {

        SimpleMatrix transformLocal = TransformationMatrix2d.tranA(-pStartPoint.x, -pStartPoint.y);

        pPolygon = TransformationMatrix2d.transformList(pPolygon, transformLocal);

        // rectangleContur = TransformationMatrix2d.transformArray(rectangleContur, transformLocal);

        Double h1 = null;
        Double angle = null;
        Measurement measurement = pRoof.getMeasurements().get(MeasurementKey.HEIGHT_1);
        if (isUnit(measurement, MeasurementUnit.DEGREES)) {
//            return pAngleHeight + pAngleDepth * Math.tan(Math.toRadians(measurement.getValue()));
            angle = measurement.getValue();
        } else {
            h1 = getHeightMeters(pRoof.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);
        }



        RoofTypeOutput rto = build(pPolygon, h1, angle, 0, 0, pRoofTextureData);

        SimpleMatrix transformGlobal = TransformationMatrix3d.tranA(pStartPoint.x, height - rto.getHeight(),
                -pStartPoint.y);
        rto.setTransformationMatrix(transformGlobal);

        return rto;

    }

    protected RoofTypeOutput build(List<Point2d> pBorderList,

        Double h1, Double angle, double l1, double l2,
        RoofMaterials pRoofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(pRoofTextureData);
        MeshFactory meshRoof = createRoofMesh(pRoofTextureData);

        TextureData facadeTexture = pRoofTextureData.getFacade().getTextureData();
        TextureData roofTexture = pRoofTextureData.getRoof().getTextureData();


        // XXX temporary ?
        if (0.0f > Triangulate.area(pBorderList)) {

            pBorderList = PolygonList2d.reverse(pBorderList);
        }


        log.info("** TO TEST IN JUNIT TEST: **");
        for (Point2d p : pBorderList) {
            log.info("polygon.add(new Point2d("+p.x+",  "+p.y+"));");
        }
        log.info("****");

        Output sk = Skeleton.sk(pBorderList);

        Map<Point2d, Double> distance = new HashMap<Point2d, Double>();
        calcDistance(sk, distance);

        calcDistanceToHeight(distance, h1, angle);

        List<PolygonRoofHooksSpace> polygonRoofHooksSpace = new ArrayList<PolygonRoofHooksSpace>();


        for (PolygonList2d polygon : sk.getFaces2()) {
            List<Point2d> points = polygon.getPoints();

            if (points.size() < 3) {
                log.error("blad za malo wiezcholkow !!!!!!");
                continue;
            }

            LineSegment2d edge = sk.getEdges().get(polygon);
            Vector3d faceNormal = calcNormal(edge, points, distance);

            MultiPolygonList2d multiPolygonList2d = new MultiPolygonList2d(polygon);



            Vector3d faceEdgeNormal = new Vector3d(edge.getEnd().x - edge.getBegin().x, 0,  -(edge.getEnd().y - edge.getBegin().y) );

            Plane3d plane = new Plane3d(new Point3d(edge.getBegin().x, 0, -edge.getBegin().y), faceNormal);



            RoofTypeUtil.addPolygonToRoofMesh(meshRoof, multiPolygonList2d, plane, faceEdgeNormal, roofTexture);

            Vector2d v1 = new Vector2d(edge.getEnd());
            v1.sub(edge.getBegin());
            PolygonRoofHooksSpace hookSpace = RectangleRoofTypeBuilder.buildRecHookSpace(edge.getBegin(), v1, new PolygonPlane(multiPolygonList2d, plane));
            polygonRoofHooksSpace.add(hookSpace);
        }

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(findMaxDistance(distance));

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));


      //FIXME
        //TODO
        //XXX
        // !!!
        rto.setRoofHooksSpaces(null);

//        rto.setRoofHooksSpaces(
//                polygonRoofHooksSpace.toArray(
//                        new RoofHooksSpace [polygonRoofHooksSpace.size()]));

        rto.setRectangle(findRectangle(pBorderList, 0));

        return rto;
    }

    private void calcDistanceToHeight(Map<Point2d, Double> distance, Double h1, Double angle) {

        double correction = 1;

        if (angle != null) {
            correction = Math.tan(Math.toRadians(angle));

        } else if (h1 != null){
            double maxDistance = findMaxDistance(distance);

            correction = h1 / maxDistance;
        }

        for (Point2d p : distance.keySet()) {
            Double d = distance.get(p);
            if (d != null) {
                distance.put(p, d * correction);
            }
        }
    }

    private double findMaxDistance(Map<Point2d, Double> distance) {
        double maxDistance = 0;

        for (Point2d p : distance.keySet()) {
            Double d = distance.get(p);
            if (d != null) {
                if (d > maxDistance) {
                    maxDistance = d;
                }
            }
        }
        return maxDistance;
    }

    private void calcDistance(Output sk, Map<Point2d, Double> distance) {
        for (PolygonList2d polygon : sk.getFaces2()) {
            LineSegment2d edge = sk.getEdges().get(polygon);
            List<Point2d> points = polygon.getPoints();
            calcDistance(edge, points, distance);
        }
    }

    private void calcDistance(LineSegment2d edge, List<Point2d> points, Map<Point2d, Double> distance) {

        for (Point2d p : points) {
            Double d = distance.get(p);

            if (d == null) {
                d = calcDistance(p, edge);
                distance.put(p, d);
            }
        }
    }

    private Vector3d calcNormal(LineSegment2d edge, List<Point2d> points, Map<Point2d, Double> distance) {

        Point2d p1 = edge.getBegin();
        Point2d p2 = edge.getEnd();
        Point2d p3 = null;

        double d3 = -Double.MAX_VALUE;
        for (Point2d p : points) {
            Double d = distance.get(p);

            if (d3 < d) {
                d3 = d;
                p3 = p;
            }

//            if (d3 > 1) {
//                break;
//            }
        }

        Double d1 = distance.get(p1);
        Double d2 = distance.get(p2);

        // Due Bug in skeleton algorithm recalculate distance
        // XXX
//        d3 = calcDistance(p3, edge);


        Vector3d v1 = new Vector3d(p2.x - p1.x, d2 - d1, -p2.y + p1.y);
        Vector3d v2 = new Vector3d(p3.x - p2.x, d3 - d2, -p3.y + p2.y);

        v1.cross(v1, v2);
        v1.normalize();
        return v1;
    }

    private static double calcDistance(Point2d pIntersect, LineSegment2d edgeLine) {
        Vector2d edge = new Vector2d(edgeLine.getEnd());
        edge.sub(edgeLine.getBegin());

        Point2d intersect = new Point2d(pIntersect);
        intersect.sub(edgeLine.getBegin());

        Vector2d pointOnVector = Algebra.orthogonalProjection(edge, intersect);

        return distance(intersect, pointOnVector);
    }

    /**
     * Computes the distance between this point and point p1.
     * @param p0
     *
     * @param p1
     *            the other point
     * @return
     */
    private static double distance(Tuple2d p0, Tuple2d p1) {
        double dx, dy;

        dx = p0.x - p1.x;
        dy = p0.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** Find minimal rectangle containing list of points.
     * Save as list of 3d points to display.
     *
     * XXX this should by changed!
     *
     * @param pPolygon list of points
     * @param height height
     * @return minimal rectangle
     */
    public static List<Point3d> findRectangle(List<Point2d> pPolygon, double height) {

        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;

        for (Point2d p : pPolygon) {
            if (minx > p.x) {
                minx = p.x;
            }
            if (miny > p.y) {
                miny = p.y;
            }
            if (maxx < p.x) {
                maxx = p.x;
            }
            if (maxy < p.y) {
                maxy = p.y;
            }
        }


        List<Point3d> rect = new ArrayList<Point3d>();
        rect.add(new Point3d(minx, height, -miny));
        rect.add(new Point3d(minx, height, -maxy));
        rect.add(new Point3d(maxx, height, -maxy));
        rect.add(new Point3d(maxx, height, -miny));

        return rect;
    }

}
