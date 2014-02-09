package kendzi.jogl.model.factory;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangle2d;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.triangulate.Poly2TriSimpleUtil;

import org.apache.log4j.Logger;

public class MeshFactoryUtil {

    /** Log. */
    private static final Logger log = Logger.getLogger(MeshFactoryUtil.class);

    public static kendzi.jogl.model.factory.MeshFactory cubeMesh(Point3d start) {
        return cubeMesh(start, new Vector3d(1, 1, 1));
    }

    public static kendzi.jogl.model.factory.MeshFactory cubeMesh(Point3d start, Vector3d size) {

        MeshFactory mf = new MeshFactory();
        FaceFactory face = mf.addFace(FaceType.QUADS);

        double x = start.x;
        double y = start.y;
        double z = start.z;

        double sx = size.x / 2d;
        double sy = size.y / 2d;
        double sz = size.z / 2d;

        int tld = mf.addTextCoord(new TextCoord(0, 0));
        int trd = mf.addTextCoord(new TextCoord(1, 0));
        int tlu = mf.addTextCoord(new TextCoord(0, 1));
        int tru = mf.addTextCoord(new TextCoord(1, 1));

        int nf = mf.addNormal(new Vector3d(0, 0, 1));
        int nb = mf.addNormal(new Vector3d(0, 0, -1));
        int nl = mf.addNormal(new Vector3d(-1, 0, 0));
        int nr = mf.addNormal(new Vector3d(1, 0, 0));
        int nt = mf.addNormal(new Vector3d(0, 1, 0));
        int nd = mf.addNormal(new Vector3d(0, -1, 0));

        int ruf = mf.addVertex(new Point3d(x + sx, y + sy, z + sz));
        int rub = mf.addVertex(new Point3d(x + sx, y + sy, z - sz));
        int rdf = mf.addVertex(new Point3d(x + sx, y - sy, z + sz));
        int rdb = mf.addVertex(new Point3d(x + sx, y - sy, z - sz));

        int luf = mf.addVertex(new Point3d(x - sx, y + sy, z + sz));
        int lub = mf.addVertex(new Point3d(x - sx, y + sy, z - sz));
        int ldf = mf.addVertex(new Point3d(x - sx, y - sy, z + sz));
        int ldb = mf.addVertex(new Point3d(x - sx, y - sy, z - sz));

        // front
        face.addVert(ldf, tld, nf);
        face.addVert(rdf, trd, nf);
        face.addVert(ruf, tru, nf);
        face.addVert(luf, tlu, nf);

        // back
        face.addVert(rdb, tld, nb);
        face.addVert(ldb, trd, nb);
        face.addVert(lub, tru, nb);
        face.addVert(rub, tlu, nb);

        // right
        face.addVert(rdf, tld, nr);
        face.addVert(rdb, trd, nr);
        face.addVert(rub, tru, nr);
        face.addVert(ruf, tlu, nr);

        // left
        face.addVert(ldb, tld, nl);
        face.addVert(ldf, trd, nl);
        face.addVert(luf, tru, nl);
        face.addVert(lub, tlu, nl);

        // top
        face.addVert(luf, tld, nt);
        face.addVert(ruf, trd, nt);
        face.addVert(rub, tru, nt);
        face.addVert(lub, tlu, nt);

        // down
        face.addVert(ldb, tld, nd);
        face.addVert(rdb, trd, nd);
        face.addVert(rdf, tru, nd);
        face.addVert(ldf, tlu, nd);

        return mf;
    }

    /**
     * Add flat polygon to mesh.
     * 
     * @param polygonWithHolesList2d
     * @param height
     * @param meshFactory
     * @param textureData
     * @param textureStartPointX
     * @param textureStartPointY
     * @param textureDirection
     */
    public static void addPolygonWithHolesInY(PolygonWithHolesList2d polygonWithHolesList2d, double height,
            MeshFactory meshFactory, TextureData textureData, double textureStartPointX, double textureStartPointY,
            Vector3d textureDirection) {

        addPolygonWithHolesInY(polygonWithHolesList2d, height, meshFactory, textureData, textureStartPointX, textureStartPointY,
                textureDirection, true);
    }

    /**
     * @see kendzi.jogl.model.factory.MeshFactoryUtil#addPolygonWithHolesInY(PolygonWithHolesList2d,
     *      double, MeshFactory, TextureData, double, double, Vector3d)
     * 
     * @param polygonWithHolesList2d
     * @param height
     * @param meshFactory
     * @param textureData
     * @param textureStartPointX
     * @param textureStartPointY
     * @param textureDirection
     */
    public static void addPolygonWithHolesInYRevert(PolygonWithHolesList2d polygonWithHolesList2d, double height,
            MeshFactory meshFactory, TextureData textureData, double textureStartPointX, double textureStartPointY,
            Vector3d textureDirection) {

        addPolygonWithHolesInY(polygonWithHolesList2d, height, meshFactory, textureData, textureStartPointX, textureStartPointY,
                textureDirection, false);
    }

    private static void addPolygonWithHolesInY(PolygonWithHolesList2d polygonWithHolesList2d, double height,
            MeshFactory meshFactory, TextureData textureData, double textureStartPointX, double textureStartPointY,
            Vector3d textureDirection, boolean top) {

        List<Triangle2d> topMP = Poly2TriSimpleUtil.triangulate(polygonWithHolesList2d);
        Vector3d yt = new Vector3d(0, 1, 0);
        if (!top) {
            yt.negate();
        }
        Point3d textureStartPoint = new Point3d(textureStartPointX, height, -textureStartPointY);
        Plane3d planeTop = new Plane3d(textureStartPoint, yt);

        addPolygonToRoofMesh(meshFactory, topMP, planeTop, textureDirection, textureData, 0, 0);
    }

    /**
     * Add polygons to roof mesh.
     * 
     * @param pMeshRoof
     *            roof mesh
     * @param pMultiPolygons
     *            point of polygons
     * @param plane2
     * @param pRoofLineVector
     * @param roofTexture
     * 
     */
    public static void addPolygonToRoofMesh(MeshFactory pMeshRoof, MultiPolygonList2d pMultiPolygons, Plane3d plane2,
            Vector3d pRoofLineVector, TextureData roofTexture) {
        addPolygonToRoofMesh(pMeshRoof, pMultiPolygons, plane2, pRoofLineVector, roofTexture, 0, 0);
    }

    /**
     * Add polygons to roof mesh.
     * 
     * @param pMeshRoof
     *            roof mesh
     * @param pMultiPolygons
     *            point of polygons
     * @param plane2
     * @param pRoofLineVector
     * @param roofTexture
     * @param textureOffsetU
     *            offset for texture U
     * @param textureOffsetV
     *            offset for texture V
     * 
     * @deprecated FIXME this method need to be rewrite!
     */
    @Deprecated
    public static void addPolygonToRoofMesh(MeshFactory pMeshRoof, MultiPolygonList2d pMultiPolygons, Plane3d plane2,
            Vector3d pRoofLineVector, TextureData roofTexture, double textureOffsetU, double textureOffsetV) {

        // FIXME this method need to be rewrite!
        int normalIndex = pMeshRoof.addNormal(plane2.getNormal());

        // at last we create model
        // connect all polygon in mesh
        for (PolygonList2d polygon : pMultiPolygons.getPolygons()) {

            List<Point2d> poly = polygon.getPoints();

            Integer[] pointsIndex = new Integer[poly.size()];

            // List<Point2d> poly = makeListFromIndex(pPolygonsPoints,
            // polyIndex);

            if (poly.size() < 3) {
                log.error("error polygon should have more then 3 vertex - skiping!");
                continue;
            }
            int s1 = poly.size();

            // XXX switch to PolygonUtil !!!
            Triangulate t = new Triangulate();
            poly = t.removeClosePoints1(poly);

            if (s1 != poly.size()) {
                log.error("error polygon have dublet points!! it require to fix");
            }

            List<Integer> trianglePoly = t.processIndex(poly);

            if (trianglePoly == null) {
                log.error("******* trianglePoly: == null");
                // XXX good joke
                trianglePoly = trianglePolytriangulateSweeped(poly);
            }

            if (trianglePoly == null) {

                log.error("******* trianglePoly: == null");
                continue;
            }

            FaceFactory face = pMeshRoof.addFace(FaceType.TRIANGLES);
            for (Integer i : trianglePoly) {
                // index magic

                Integer pointIndex = i;
                if (pointsIndex[pointIndex] == null) {
                    // don't calc points twice.

                    Point2d point2d = poly.get(i);

                    double h = plane2.calcYOfPlane(point2d.x, -point2d.y);

                    int vi = pMeshRoof.addVertex(new Point3d(point2d.x, h, -point2d.y));

                    pointsIndex[pointIndex] = vi;
                }

                int vi = pointsIndex[pointIndex];

                Point3d point3d = pMeshRoof.vertices.get(vi);

                face.addVertIndex(vi);

                face.addNormalIndex(normalIndex);

                TextCoord calcUV = TextCordFactory.calcFlatSurfaceUV(point3d, plane2.getNormal(), pRoofLineVector,
                        plane2.getPoint(), roofTexture, textureOffsetU, textureOffsetV);

                int tci = pMeshRoof.addTextCoord(calcUV);

                face.addCoordIndex(tci);
            }
        }
    }

    /**
     * Add 2d triangles list to mesh. Triangle height is calculate from plane.
     * Texture offset is taken from plane point, textureVector and
     * textureOffset.
     * 
     * @param pMeshRoof
     *            roof mesh
     * @param pTriangles
     *            point of polygons
     * @param pPlane
     * @param pTextureVector
     * @param pTextureData
     * @param textureOffsetU
     *            offset for texture U
     * @param textureOffsetV
     *            offset for texture V
     * 
     */
    public static void addPolygonToRoofMesh(MeshFactory pMeshRoof, List<Triangle2d> pTriangles, Plane3d pPlane,
            Vector3d pTextureVector, TextureData pTextureData, double textureOffsetU, double textureOffsetV) {

        int normalIndex = pMeshRoof.addNormal(pPlane.getNormal());

        FaceFactory face = pMeshRoof.addFace(FaceType.TRIANGLES);

        for (Triangle2d triangle : pTriangles) {

            addPointToTriangleFace(pMeshRoof, pPlane, pTextureVector, pTextureData, textureOffsetU, textureOffsetV, normalIndex,
                    face, triangle.getP1());
            addPointToTriangleFace(pMeshRoof, pPlane, pTextureVector, pTextureData, textureOffsetU, textureOffsetV, normalIndex,
                    face, triangle.getP2());
            addPointToTriangleFace(pMeshRoof, pPlane, pTextureVector, pTextureData, textureOffsetU, textureOffsetV, normalIndex,
                    face, triangle.getP3());

        }
    }

    /**
     * @param pMeshRoof
     * @param pPlane
     * @param pTextureVector
     * @param pTextureData
     * @param textureOffsetU
     * @param textureOffsetV
     * @param normalIndex
     * @param face
     * @param point2d
     */
    private static void addPointToTriangleFace(MeshFactory pMeshRoof, Plane3d pPlane, Vector3d pTextureVector,
            TextureData pTextureData, double textureOffsetU, double textureOffsetV, int normalIndex, FaceFactory face,
            Point2d point2d) {

        double h = pPlane.calcYOfPlane(point2d.x, -point2d.y);

        Point3d point3d = new Point3d(point2d.x, h, -point2d.y);

        int vi = pMeshRoof.addVertex(point3d);

        // Point3d point3d = pMeshRoof.vertices.get(vi);

        face.addVertIndex(vi);

        face.addNormalIndex(normalIndex);

        TextCoord calcUV = TextCordFactory.calcFlatSurfaceUV(point3d, pPlane.getNormal(), pTextureVector, pPlane.getPoint(),
                pTextureData, textureOffsetU, textureOffsetV);

        int tci = pMeshRoof.addTextCoord(calcUV);

        face.addCoordIndex(tci);
    }

    @Deprecated
    private static List<Integer> trianglePolytriangulateSweeped(List<Point2d> poly) {

        // FIXME remove this method and switch to PolygonUtil
        int size = poly.size();

        List<Point2d> polySweeped = new ArrayList<Point2d>();
        for (Point2d point2d : poly) {
            polySweeped.add(new Point2d(point2d.x + 0.3 + Double.MIN_VALUE, point2d.y + 0.3 + Double.MIN_VALUE));
        }

        // sweep
        polySweeped.add(polySweeped.remove(0));

        Triangulate t = new Triangulate();
        List<Integer> trianglePoly = t.processIndex(poly);

        if (trianglePoly == null) {
            return null;
        }

        List<Integer> ret = new ArrayList<Integer>();
        for (Integer integer : trianglePoly) {
            // sweep back
            ret.add((integer + 1) % size);
        }

        return ret;
    }
}
