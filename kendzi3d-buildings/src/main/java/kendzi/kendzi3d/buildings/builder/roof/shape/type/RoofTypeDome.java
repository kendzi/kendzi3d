/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.dto.TextureQuadIndex;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.Measurement;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementUnit;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRoofModel;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.point.Vector3dUtil;
import kendzi.math.geometry.polygon.CircleInsidePolygon;
import kendzi.math.geometry.polygon.CircleInsidePolygon.Circle;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

import org.ejml.simple.SimpleMatrix;

/**
 * Roof type 5.6.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class RoofTypeDome extends AbstractRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRoof(Point2d startPoint, PolygonWithHolesList2d buildingPolygon, DormerRoofModel roof,
            double height, RoofMaterials roofTextureData) {

        SimpleMatrix transformLocal = TransformationMatrix2d.tranA(-startPoint.x, -startPoint.y);

        List<Point2d> polygon = buildingPolygon.getOuter().getPoints();

        polygon = TransformationMatrix2d.transformList(polygon, transformLocal);

        Double h1 = null;
        Double angle = null;
        Measurement measurement = roof.getMeasurements().get(MeasurementKey.HEIGHT_1);
        if (isUnit(measurement, MeasurementUnit.DEGREES)) {
            angle = measurement.getValue();
        } else {
            h1 = getHeightMeters(roof.getMeasurements(), MeasurementKey.HEIGHT_1, DEFAULT_ROOF_HEIGHT);
        }

        RoofTypeOutput rto = build(polygon, h1, angle, roofTextureData);

        SimpleMatrix transformGlobal = TransformationMatrix3d.tranA(startPoint.x, height - rto.getHeight(), -startPoint.y);
        rto.setTransformationMatrix(transformGlobal);

        return rto;

    }

    protected RoofTypeOutput build(List<Point2d> borderList, Double height, Double angle, RoofMaterials roofTextureData) {

        MeshFactory meshDome = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        PolygonList2d borderPolygon = new PolygonList2d(borderList);

        // build circle
        Circle circle = CircleInsidePolygon.iterativeNonConvex(borderPolygon, 0.01);

        int pIcross = 5;
        buildRotaryShape(meshDome, borderList, circle.getPoint(), height, pIcross, roofTexture);

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(height);

        rto.setMesh(Arrays.asList(meshDome));

        rto.setRoofHooksSpaces(null);

        rto.setRectangle(RoofTypeUtil.findRectangle(borderList, 0));

        return rto;
    }

    private void buildRotaryShape(MeshFactory meshFactory, List<Point2d> borderList, Point2d center, double height,
            int numberOfCrossSplits, TextureData roofTexture) {

        int crossCount = numberOfCrossSplits + 1;

        // create cross section
        Point2d[] crossSection = new Point2d[crossCount];
        for (int i = 0; i < crossCount; i++) {

            double a = Math.toRadians(90) / (crossCount - 1) * i;

            crossSection[i] = new Point2d(Math.cos(a), Math.sin(a) * height);
        }

        buildRotaryShape(meshFactory, center, borderList, crossSection, roofTexture);
    }

    public static void buildRotaryShape(MeshFactory meshFactory, Point2d center, List<Point2d> borderList,
            Point2d[] crossSection, TextureData roofTexture) {

        int crossCount = crossSection.length;
        int pointCount = borderList.size();

        // create points
        Point3d[][] mesh = buildMesh(center, borderList, crossSection);

        TextureQuadIndex[][] tc = buildTextureMapping(meshFactory, mesh, pointCount, crossCount);

        FaceFactory face = meshFactory.addFace(FaceType.QUADS);

        // add vertex to mesh
        int[][] pointsIntex = RoofType5v6.addVertexToMeshFactory(meshFactory, mesh, pointCount, crossCount);

        Point3d center3d = new Point3d(center.x, 0, -center.y);

        // add soft normals vectors
        int[][] softNormalsIndexs = buildNormalsIndexs(meshFactory, mesh, center3d, pointCount, crossCount);

        // add faces to mesh
        for (int i = 0; i < pointCount; i++) {

            int i2 = (i + 1) % pointCount;

            for (int j = 0; j < crossCount - 1; j++) {

                int ic1p1 = pointsIntex[i][j];
                int ic2p1 = pointsIntex[i2][j];
                int ic1p2 = pointsIntex[i][j + 1];
                int ic2p2 = pointsIntex[i2][j + 1];

                int ic1p1n = softNormalsIndexs[i][j];
                int ic2p1n = softNormalsIndexs[i2][j];
                int ic1p2n = softNormalsIndexs[i][j + 1];
                int ic2p2n = softNormalsIndexs[i2][j + 1];

                TextureQuadIndex tq = tc[i][j];

                face.addVert(ic1p1, tq.getLd(), ic1p1n);
                face.addVert(ic2p1, tq.getRd(), ic2p1n);
                face.addVert(ic2p2, tq.getRt(), ic2p2n);
                face.addVert(ic1p2, tq.getLt(), ic1p2n);
            }
        }
    }

    private static Point3d[][] buildMesh(Point2d center, List<Point2d> borderList, Point2d[] crossSection) {

        int pointCount = borderList.size();
        int crossCount = crossSection.length;

        Point3d[][] mesh = new Point3d[pointCount][];
        for (int i = 0; i < pointCount; i++) {
            Point2d outlinePoint = borderList.get(i);

            Point3d point = new Point3d(outlinePoint.x - center.x, 1, -(outlinePoint.y - center.y));

            Point3d[] crossMesh = new Point3d[crossCount];

            for (int j = 0; j < crossCount; j++) {
                // point
                Point2d cross = crossSection[j];

                crossMesh[j] = new Point3d(point.x * cross.x + center.x, point.y * cross.y, point.z * cross.x - center.y);

            }
            mesh[i] = crossMesh;
        }
        return mesh;
    }

    public static int[][] buildNormalsIndexs(MeshFactory meshFactory, Point3d[][] mesh, Point3d center3d, int pointCount,
            int crossCount) {

        int[][] softNormalsIndexs = new int[pointCount][];

        for (int i = 0; i < pointCount; i++) {

            softNormalsIndexs[i] = new int[crossCount];

            for (int j = 0; j < crossCount; j++) {

                Point3d p = mesh[i][j];

                Vector3d n = Vector3dUtil.fromTo(center3d, p);
                n.normalize();

                int in = meshFactory.addNormal(n);
                softNormalsIndexs[i][j] = in;
            }
        }
        return softNormalsIndexs;
    }

    private static TextureQuadIndex[][] buildTextureMapping(MeshFactory meshFactory, Point3d[][] mesh, int pointCount,
            int crossCount) {

        // pointCount - number of points on outline
        // crossCount - number of cross segments

        TextureQuadIndex[][] tc = new TextureQuadIndex[pointCount][];

        // texture mapping for all points
        for (int i = 0; i < pointCount; i++) {

            TextureQuadIndex[] crossTc = RoofType5v6.buildTextureMappingForCross(meshFactory, mesh, pointCount, crossCount, i);

            tc[i] = crossTc;
        }
        return tc;
    }

}
