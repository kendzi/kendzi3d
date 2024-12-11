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
import org.ejml.simple.SimpleMatrix;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Roof type 9.0.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 *         TODO rename to RoofTypeComplexHipped
 */
public class RoofType9v0 extends AbstractRoofTypeBuilder {

    /** Log. */
    private static final Logger log = LoggerFactory.getLogger(RoofType9v0.class);

    @Override
    public RoofTypeOutput buildRoof(Vector2dc startPoint, PolygonWithHolesList2d buildingPolygon, DormerRoofModel roof,
            double height, RoofMaterials roofTextureData) {

        SimpleMatrix transformLocal = TransformationMatrix2d.tranA(-startPoint.x(), -startPoint.y());
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

        SimpleMatrix transformGlobal = TransformationMatrix3d.tranA(startPoint.x(), height - rto.getHeight(), -startPoint.y());
        rto.setTransformationMatrix(transformGlobal);

        return rto;
    }

    protected RoofTypeOutput build(PolygonWithHolesList2d buildingTransformed, Double h1, Double angle, double l1, double l2,
            RoofMaterials roofTextureData) {

        log.info(debugPolygon(buildingTransformed));

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        List<Vector2dc> outer = buildingTransformed.getOuter().getPoints();
        List<List<Vector2dc>> inners = PolygonWithHolesList2dUtil.getListOfHolePoints(buildingTransformed);

        SkeletonOutput sk = Skeleton.skeleton(outer, inners);

        // List<PolygonRoofHooksSpace> polygonRoofHooksSpace = new
        // ArrayList<PolygonRoofHooksSpace>();
        Map<Vector2dc, Double> distance = new IdentityHashMap<>();

        calcDistances(sk, distance);

        double heightFactor = calcDistanceToHeight(distance, h1, angle);

        for (EdgeOutput edgeOutput : sk.getEdgeOutputs()) {
            PolygonList2d polygon = edgeOutput.getPolygon();
            List<Vector2dc> points = polygon.getPoints();

            if (points.size() < 3) {
                log.error("not enought vertex for face");
                continue;
            }

            LineSegment2d edge = sk.getEdges().get(polygon);

            Vector3dc edgeNormal = new Vector3d(edge.getEnd().x() - edge.getBegin().x(), 0,
                    -(edge.getEnd().y() - edge.getBegin().y()));

            Plane3d plane = createEdgePlane(edge, heightFactor);

            MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, new MultiPolygonList2d(polygon), plane, edgeNormal, roofTexture);

            // Vector2dc v1 = new Vector2dc(edge.getEnd());
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

        Vector3dc faceNormal = calcFaceNormal(edge, heightFactor);

        return new Plane3d(new Vector3d(edge.getBegin().x(), 0, -edge.getBegin().y()), faceNormal);
    }

    private String debugPolygon(PolygonWithHolesList2d buildingTransformed) {
        StringBuilder sb = new StringBuilder();
        sb.append("** Debug for polygon **\n");

        List<Vector2dc> outer = buildingTransformed.getOuter().getPoints();
        sb.append("List<Vector2dc> polygon = new ArrayList<Vector2dc>();\n");

        for (Vector2dc p : outer) {
            sb.append("polygon.add(new Vector2dc(").append(p.x()).append(",  ").append(p.y()).append("));\n");
        }

        List<List<Vector2dc>> inners = PolygonWithHolesList2dUtil.getListOfHolePoints(buildingTransformed);

        int holeCount = 0;
        for (List<Vector2dc> polygonList2d : inners) {
            holeCount++;
            sb.append("\nList<Vector2dc> hole").append(holeCount).append(" = new ArrayList<Vector2dc>();\n");
            for (Vector2dc p : polygonList2d) {
                sb.append("hole").append(holeCount).append(".add(new Vector2dc(").append(p.x()).append(",  ").append(p.y())
                        .append("));\n");
            }
        }

        sb.append("****");
        return sb.toString();
    }

    private double calcDistanceToHeight(Map<Vector2dc, Double> distance, Double h1, Double angle) {

        double correction = 1;

        if (angle != null) {
            correction = Math.tan(Math.toRadians(angle));

        } else if (h1 != null) {
            double maxDistance = findMaxDistance(distance);

            correction = h1 / maxDistance;
        }

        for (Vector2dc p : distance.keySet()) {
            Double d = distance.get(p);
            if (d != null) {
                distance.put(p, d * correction);
            }
        }

        return correction;
    }

    private double findMaxDistance(Map<Vector2dc, Double> distance) {
        double maxDistance = 0;

        for (Vector2dc p : distance.keySet()) {
            Double d = distance.get(p);
            if (d != null) {
                if (d > maxDistance) {
                    maxDistance = d;
                }
            }
        }
        return maxDistance;
    }

    private void calcDistances(SkeletonOutput sk, Map<Vector2dc, Double> distance) {

        for (EdgeOutput edgeOutput : sk.getEdgeOutputs()) {
            PolygonList2d polygon = edgeOutput.getPolygon();
            LineSegment2d edge = sk.getEdges().get(polygon);
            List<Vector2dc> points = polygon.getPoints();
            calcDistance(edge, points, distance);
        }
    }

    private void calcDistance(LineSegment2d edge, List<Vector2dc> points, Map<Vector2dc, Double> distance) {

        for (Vector2dc p : points) {
            Double d = distance.get(p);

            if (d == null) {
                d = calcDistance(p, edge);
                distance.put(p, d);
            }
        }
    }

    private Vector3dc calcFaceNormal(LineSegment2d edge, double heightFactor) {
        Vector2dc edgeVector = Vector2dUtil.fromTo(edge.getBegin(), edge.getEnd()).normalize();

        Vector2dc edgeOrthogonal = Vector2dUtil.orthogonalLeft(edgeVector);

        Vector3dc v2 = new Vector3d(edgeOrthogonal.x(), heightFactor, -edgeOrthogonal.y());
        Vector3dc v1 = new Vector3d(edgeVector.x(), 0, -edgeVector.y()).cross(v2).normalize();

        return v1;

    }

    private static double calcDistance(Vector2dc pIntersect, LineSegment2d edgeLine) {
        Vector2dc edge = new Vector2d(edgeLine.getEnd()).sub(edgeLine.getBegin());

        Vector2dc intersect = new Vector2d(pIntersect).sub(edgeLine.getBegin());

        Vector2dc pointOnVector = Algebra.orthogonalProjection(edge, intersect);

        return Tuple2dUtil.distance(intersect, pointOnVector);
    }

}
