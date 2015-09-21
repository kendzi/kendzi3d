/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.Measurement;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementUnit;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRoofModel;
import kendzi.math.geometry.Algebra;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LineSegment2d;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.point.Tuple2dUtil;
import kendzi.math.geometry.point.Vector2dUtil;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2dUtil;
import kendzi.math.geometry.skeleton.EdgeOutput;
import kendzi.math.geometry.skeleton.Skeleton;
import kendzi.math.geometry.skeleton.SkeletonOutput;

import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

/**
 * Roof type 9.0.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 *         TODO rename to RoofTypeComplexHipped
 */
public class RoofType9v0 extends AbstractRoofTypeBuilder {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType9v0.class);

    @Override
    public RoofTypeOutput buildRoof(Point2d startPoint, PolygonWithHolesList2d buildingPolygon, DormerRoofModel roof,
            double height, RoofMaterials roofTextureData) {

        SimpleMatrix transformLocal = TransformationMatrix2d.tranA(-startPoint.x, -startPoint.y);
        PolygonWithHolesList2d buildingTransformed = PolygonWithHolesList2dUtil.transform(buildingPolygon, transformLocal);

        Measurement measurement = roof.getMeasurements().get(MeasurementKey.HEIGHT_1);

        Double h1 = null;
        Double angle = null;

        if (isUnit(measurement, MeasurementUnit.DEGREES)) {
            angle = measurement.getValue();
        } else {
            h1 = getHeightMeters(roof.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);
        }

        RoofTypeOutput rto = build(buildingTransformed, h1, angle, 0, 0, roofTextureData);

        SimpleMatrix transformGlobal = TransformationMatrix3d.tranA(startPoint.x, height - rto.getHeight(), -startPoint.y);
        rto.setTransformationMatrix(transformGlobal);

        return rto;
    }

    protected RoofTypeOutput build(PolygonWithHolesList2d buildingTransformed, Double h1, Double angle, double l1, double l2,
            RoofMaterials roofTextureData) {

        log.info(debugPolygon(buildingTransformed));

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        List<Point2d> outer = buildingTransformed.getOuter().getPoints();
        List<List<Point2d>> inners = PolygonWithHolesList2dUtil.getListOfHolePoints(buildingTransformed);

        SkeletonOutput sk = Skeleton.skeleton(outer, inners);

        // List<PolygonRoofHooksSpace> polygonRoofHooksSpace = new
        // ArrayList<PolygonRoofHooksSpace>();
        Map<Point2d, Double> distance = new IdentityHashMap<Point2d, Double>();

        calcDistances(sk, distance);

        double heightFactor = calcDistanceToHeight(distance, h1, angle);

        for (EdgeOutput edgeOutput : sk.getEdgeOutputs()) {
            PolygonList2d polygon = edgeOutput.getPolygon();
            List<Point2d> points = polygon.getPoints();

            if (points.size() < 3) {
                log.error("not enought vertex for face");
                continue;
            }

            LineSegment2d edge = sk.getEdges().get(polygon);

            Vector3d edgeNormal = new Vector3d(edge.getEnd().x - edge.getBegin().x, 0, -(edge.getEnd().y - edge.getBegin().y));

            Plane3d plane = createEdgePlane(edge, heightFactor);

            MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, new MultiPolygonList2d(polygon), plane, edgeNormal, roofTexture);

            // Vector2d v1 = new Vector2d(edge.getEnd());
            // v1.sub(edge.getBegin());
            //
            // PolygonRoofHooksSpace hookSpace =
            // RectangleRoofTypeBuilder.buildRecHookSpace(edge.getBegin(), v1,
            // new PolygonPlane(
            // multiPolygonList2d, plane));
            // polygonRoofHooksSpace.add(hookSpace);
        }

        RoofTypeOutput rto = new RoofTypeOutput();

        rto.setHeight(findMaxDistance(distance));

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        rto.setRectangle(RoofTypeUtil.findRectangle(outer, 0));

        return rto;
    }

    private Plane3d createEdgePlane(LineSegment2d edge, double heightFactor) {

        Vector3d faceNormal = calcFaceNormal(edge, heightFactor);

        return new Plane3d(new Point3d(edge.getBegin().x, 0, -edge.getBegin().y), faceNormal);
    }

    private String debugPolygon(PolygonWithHolesList2d buildingTransformed) {
        StringBuffer sb = new StringBuffer();
        sb.append("** Debug for polygon **\n");

        List<Point2d> outer = buildingTransformed.getOuter().getPoints();
        sb.append("List<Point2d> polygon = new ArrayList<Point2d>();\n");

        for (Point2d p : outer) {
            sb.append("polygon.add(new Point2d(" + p.x + ",  " + p.y + "));\n");
        }

        List<List<Point2d>> inners = PolygonWithHolesList2dUtil.getListOfHolePoints(buildingTransformed);

        int holeCount = 0;
        for (List<Point2d> polygonList2d : inners) {
            holeCount++;
            sb.append("\nList<Point2d> hole" + holeCount + " = new ArrayList<Point2d>();\n");
            for (Point2d p : polygonList2d) {
                sb.append("hole" + holeCount + ".add(new Point2d(" + p.x + ",  " + p.y + "));\n");
            }
        }

        sb.append("****");
        return sb.toString();
    }

    private double calcDistanceToHeight(Map<Point2d, Double> distance, Double h1, Double angle) {

        double correction = 1;

        if (angle != null) {
            correction = Math.tan(Math.toRadians(angle));

        } else if (h1 != null) {
            double maxDistance = findMaxDistance(distance);

            correction = h1 / maxDistance;
        }

        for (Point2d p : distance.keySet()) {
            Double d = distance.get(p);
            if (d != null) {
                distance.put(p, d * correction);
            }
        }

        return correction;
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

    private void calcDistances(SkeletonOutput sk, Map<Point2d, Double> distance) {

        for (EdgeOutput edgeOutput : sk.getEdgeOutputs()) {
            PolygonList2d polygon = edgeOutput.getPolygon();
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

    private Vector3d calcFaceNormal(LineSegment2d edge, double heightFactor) {
        Vector2d edgeVector = Vector2dUtil.fromTo(edge.getBegin(), edge.getEnd());
        edgeVector.normalize();

        Vector2d edgeOrthogonal = Vector2dUtil.orthogonalLeft(edgeVector);

        Vector3d v1 = new Vector3d(edgeVector.x, 0, -edgeVector.y);
        Vector3d v2 = new Vector3d(edgeOrthogonal.x, heightFactor, -edgeOrthogonal.y);

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

        return Tuple2dUtil.distance(intersect, pointOnVector);
    }

}
