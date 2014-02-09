/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.texture.TextureQuadIndex;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.point.Vector2dUtil;
import kendzi.math.geometry.polygon.CircleInsidePolygon;
import kendzi.math.geometry.polygon.CircleInsidePolygon.Circle;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

import org.ejml.simple.SimpleMatrix;

/**
 * Roof type 5.6.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class RoofType5v6 extends AbstractRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRoof(Point2d pStartPoint, PolygonWithHolesList2d buildingPolygon, DormerRoofModel pRoof,
            double height, RoofMaterials roofTextureData) {

        SimpleMatrix transformLocal = TransformationMatrix2d.tranA(-pStartPoint.x, -pStartPoint.y);

        List<Point2d> polygon = buildingPolygon.getOuter().getPoints();

        polygon = TransformationMatrix2d.transformList(polygon, transformLocal);

        Double h1 = null;
        Double angle = null;
        Measurement measurement = pRoof.getMeasurements().get(MeasurementKey.HEIGHT_1);
        if (isUnit(measurement, MeasurementUnit.DEGREES)) {
            angle = measurement.getValue();
        } else {
            h1 = getHeightMeters(pRoof.getMeasurements(), MeasurementKey.HEIGHT_1, DEFAULT_ROOF_HEIGHT);
        }

        RoofTypeOutput rto = build(polygon, h1, angle, roofTextureData);

        SimpleMatrix transformGlobal = TransformationMatrix3d.tranA(pStartPoint.x, height - rto.getHeight(), -pStartPoint.y);
        rto.setTransformationMatrix(transformGlobal);

        return rto;
    }

    protected RoofTypeOutput build(List<Point2d> borderList, Double height, Double angle, RoofMaterials roofTextureData) {

        MeshFactory meshDome = createRoofMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        PolygonList2d borderPolygon = new PolygonList2d(borderList);

        buildFlatRoof(borderPolygon, meshRoof, roofTexture);

        // build circle
        Circle circle = CircleInsidePolygon.iterativeNonConvex(borderPolygon, 0.01);
        circle.setRadius(Math.min(height, circle.getRadius()));

        int pIcross = 5;
        int pIsection = 9;
        buildRotaryShape(meshDome, circle, pIcross, pIsection, true);

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(circle.getRadius());

        rto.setMesh(Arrays.asList(meshDome, meshRoof));

        rto.setRoofHooksSpaces(null);

        rto.setRectangle(RoofTypeUtil.findRectangle(borderList, 0));

        return rto;
    }

    public static void buildFlatRoof(PolygonList2d borderPolygon, MeshFactory meshRoof, TextureData roofTexture) {

        MultiPolygonList2d topMP = new MultiPolygonList2d(borderPolygon);
        // build flat
        Point3d planeRightTopPoint = new Point3d(0, 0, 0);

        Vector3d nt = new Vector3d(0, 1, 0);

        Plane3d planeTop = new Plane3d(planeRightTopPoint, nt);

        Vector3d roofTopLineVector = new Vector3d(-1d, 0, 0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);

    }

    private void buildRotaryShape(MeshFactory meshFactory, Circle circle, int numberOfCrossSplits, int pIsection, boolean soft) {

        int crossCount = numberOfCrossSplits + 1;

        // create cross section
        Point2d[] crossSection = new Point2d[crossCount];
        for (int i = 0; i < crossCount; i++) {
            double a = Math.toRadians(90) / (crossCount - 1) * i;

            crossSection[i] = new Point2d(Math.cos(a) * circle.getRadius(), Math.sin(a) * circle.getRadius());
        }

        buildRotaryShape(meshFactory, circle.getPoint(), pIsection, crossSection, soft);
    }

    public static void buildRotaryShape(MeshFactory meshFactory, Point2d center, int sectionCount, Point2d[] crossSection,
            boolean soft) {

        int crossCount = crossSection.length;

        // create points
        Point3d[][] mesh = buildMesh(center, crossSection, sectionCount);

        TextureQuadIndex[] tc = buildRotaryShapeTextureMapping(meshFactory, crossCount, sectionCount, mesh);

        FaceFactory face = meshFactory.addFace(FaceType.QUADS);

        // add vertex to mesh
        int[][] pointsIntex = addVertexToMeshFactory(meshFactory, mesh, sectionCount, crossCount);

        // add soft normals vectors
        int[][] softNormalsIntex = new int[sectionCount][];
        if (soft) {
            softNormalsIntex = buildSoftNormalsIndexs(meshFactory, sectionCount, crossSection, crossCount);
        }

        // add faces to mesh
        for (int i = 0; i < sectionCount; i++) {
            Point3d[] c1 = mesh[i];
            Point3d[] c2 = mesh[(i + 1) % sectionCount];

            int i2 = (i + 1) % sectionCount;

            for (int j = 0; j < crossCount - 1; j++) {

                int ic1p1 = pointsIntex[i][j];
                int ic2p1 = pointsIntex[i2][j];
                int ic1p2 = pointsIntex[i][j + 1];
                int ic2p2 = pointsIntex[i2][j + 1];

                int ic1p1n;
                int ic2p1n;
                int ic1p2n;
                int ic2p2n;

                if (!soft) {
                    // hard normals

                    Point3d c1p1 = c1[j];
                    Point3d c2p1 = c2[j];
                    Point3d c1p2 = c1[j + 1];

                    Vector3d n = calcNormal(c1p1, c2p1, c1p2);
                    int in = meshFactory.addNormal(n);

                    ic1p1n = in;
                    ic2p1n = in;
                    ic1p2n = in;
                    ic2p2n = in;

                } else {
                    // soft normals
                    ic1p1n = softNormalsIntex[i][j];
                    ic2p1n = softNormalsIntex[i2][j];
                    ic1p2n = softNormalsIntex[i][j + 1];
                    ic2p2n = softNormalsIntex[i2][j + 1];
                }

                TextureQuadIndex tq = tc[j];

                face.addVert(ic1p1, tq.getLd(), ic1p1n);
                face.addVert(ic2p1, tq.getRd(), ic2p1n);
                face.addVert(ic2p2, tq.getRt(), ic2p2n);
                face.addVert(ic1p2, tq.getLt(), ic1p2n);
            }
        }
    }

    public static int[][] addVertexToMeshFactory(MeshFactory meshFactory, Point3d[][] mesh, int pointCount, int crossCount) {
        int[][] pointsIntex = new int[pointCount][];
        for (int i = 0; i < pointCount; i++) {
            pointsIntex[i] = new int[crossCount];
            for (int j = 0; j < crossCount; j++) {
                Point3d p = mesh[i][j];
                int ip = meshFactory.addVertex(p);
                pointsIntex[i][j] = ip;
            }
        }
        return pointsIntex;
    }

    private static Point3d[][] buildMesh(Point2d center, Point2d[] crossSection, int sectionCount) {

        int crossCount = crossSection.length;

        Point3d[][] mesh = new Point3d[sectionCount][];
        for (int i = 0; i < sectionCount; i++) {
            double a = Math.toRadians(360) / sectionCount * i;

            SimpleMatrix tranA = TransformationMatrix3d.tranA(center.x, 0, -center.y);
            SimpleMatrix rotY = TransformationMatrix3d.rotYA(a);

            SimpleMatrix trans = tranA.mult(rotY);

            Point3d[] crossMesh = new Point3d[crossCount];

            for (int j = 0; j < crossSection.length; j++) {
                // point
                Point2d cross = crossSection[j];
                Point3d p = new Point3d(cross.x, cross.y, 0);

                crossMesh[j] = TransformationMatrix3d.transform(p, trans);

            }
            mesh[i] = crossMesh;
        }
        return mesh;
    }

    private static int[][] buildSoftNormalsIndexs(MeshFactory meshFactory, int sectionCount, Point2d[] crossSection,
            int crossCount) {

        Vector2d[] crossSectionSoftNormals = calsSoftNormals(crossSection);

        int[][] softNormalsIntex = new int[sectionCount][];
        for (int i = 0; i < sectionCount; i++) {
            double a = Math.toRadians(360) / sectionCount * i;

            SimpleMatrix rotY = TransformationMatrix3d.rotYA(a);
            softNormalsIntex[i] = new int[crossCount];

            for (int j = 0; j < crossSection.length; j++) {
                // point
                Vector2d n2d = crossSectionSoftNormals[j];
                Vector3d n = new Vector3d(n2d.x, n2d.y, 0);

                Vector3d transform = TransformationMatrix3d.transform(n, rotY);

                int in = meshFactory.addNormal(transform);
                softNormalsIntex[i][j] = in;
            }
        }
        return softNormalsIntex;
    }

    private static Vector2d[] calsSoftNormals(Point2d[] crossSection) {

        Vector2d[] ret = new Vector2d[crossSection.length];

        Vector2d[] normals = new Vector2d[crossSection.length - 1];
        for (int i = 0; i < crossSection.length - 1; i++) {
            Vector2d n = new Vector2d(crossSection[i + 1]);
            n.sub(crossSection[i]);
            n.normalize();
            normals[i] = n;
        }

        for (int i = 1; i < crossSection.length - 1; i++) {
            Vector2d n1 = normals[i - 1];
            Vector2d n2 = normals[i];

            Vector2d n = Vector2dUtil.bisectorNormalized(n1, n2);
            n.normalize();

            ret[i] = n;
        }

        ret[0] = Vector2dUtil.orthogonalLeft(normals[0]);

        ret[crossSection.length - 1] = Vector2dUtil.orthogonalLeft(normals[normals.length - 1]);

        for (Vector2d element : ret) {
            element.negate();
        }

        return ret;
    }

    private static TextureQuadIndex[] buildRotaryShapeTextureMapping(MeshFactory meshFactory, int crossCount, int sectionCount,
            Point3d[][] mesh) {
        // texture mapping only for one section, all others are the same
        int i = 0;

        return buildTextureMappingForCross(meshFactory, mesh, sectionCount, crossCount, i);
    }

    private static Vector3d calcNormal(Point3d p1, Point3d p2, Point3d p3) {
        Vector3d n = new Vector3d(p2);
        n.sub(p1);

        Vector3d n2 = new Vector3d(p3);
        n2.sub(p2);

        n.cross(n, n2);
        n.normalize();
        return n;
    }

    public static TextureQuadIndex[] buildTextureMappingForCross(MeshFactory meshFactory, Point3d[][] mesh, int pointCount,
            int crossCount, int i) {

        TextureQuadIndex[] crossTc = new TextureQuadIndex[crossCount];

        double textHeightDown = 0;

        Point3d[] c1 = mesh[i];
        Point3d[] c2 = mesh[(i + 1) % pointCount];

        Point3d middleDown = new Point3d((c1[0].x + c2[0].x) / 2d, (c1[0].y + c2[0].y) / 2d, (c1[0].z + c2[0].z) / 2d);

        double widthDown = middleDown.distance(c2[0]);

        for (int j = 1; j < crossCount; j++) {

            Point3d middleTop = new Point3d((c1[j].x + c2[j].x) / 2d, (c1[j].y + c2[j].y) / 2d, (c1[j].z + c2[j].z) / 2d);

            double widthTop = middleTop.distance(c2[j]);
            double height = middleDown.distance(middleTop);
            double textHeightTop = textHeightDown + height;
            TextureQuadIndex tq = new TextureQuadIndex();

            tq.setLd(meshFactory.addTextCoord(new TextCoord(-widthDown, textHeightDown)));
            tq.setRd(meshFactory.addTextCoord(new TextCoord(widthDown, textHeightDown)));

            tq.setRt(meshFactory.addTextCoord(new TextCoord(widthTop, textHeightTop)));
            tq.setLt(meshFactory.addTextCoord(new TextCoord(-widthTop, textHeightTop)));

            crossTc[j - 1] = tq;

            middleDown = middleTop;
            widthDown = widthTop;
            textHeightDown = textHeightTop;
        }
        return crossTc;
    }

}
