package kendzi.jogl.model.factory;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofTypeUtil;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.triangulate.Poly2TriUtil;

public class MeshFactoryUtil {

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

        addPolygonWithHolesInY(polygonWithHolesList2d, height, meshFactory, textureData, textureStartPointX,
                textureStartPointY, textureDirection, true);
    }

    /**
     * @see kendzi.jogl.model.factory.MeshFactoryUtil#addPolygonWithHolesInY(PolygonWithHolesList2d, double, MeshFactory, TextureData, double, double, Vector3d)
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

        addPolygonWithHolesInY(polygonWithHolesList2d, height, meshFactory, textureData, textureStartPointX,
                textureStartPointY, textureDirection, false);
    }

    private static void addPolygonWithHolesInY(PolygonWithHolesList2d polygonWithHolesList2d, double height,
            MeshFactory meshFactory, TextureData textureData, double textureStartPointX, double textureStartPointY,
            Vector3d textureDirection, boolean top) {

        MultiPolygonList2d topMP = Poly2TriUtil.triangulate(polygonWithHolesList2d);
        Vector3d yt = new Vector3d(0, top ? 1 : -1, 0);
        Point3d textureStartPoint = new Point3d(textureStartPointX, height, -textureStartPointY);
        Plane3d planeTop = new Plane3d(textureStartPoint, yt);
        RoofTypeUtil.addPolygonToRoofMesh(meshFactory, topMP, planeTop, textureDirection, textureData);
    }
}
