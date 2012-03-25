package kendzi.jogl.model.factory;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.TextCoord;

public class MeshFactoryUtil {

    public static kendzi.jogl.model.factory.MeshFactory cubeMesh(Point3d start) {
        return cubeMesh(start, new Vector3d(1,1,1));
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

        int tld = mf.addTextCoord(new TextCoord(0,0));
        int trd = mf.addTextCoord(new TextCoord(1,0));
        int tlu = mf.addTextCoord(new TextCoord(0,1));
        int tru = mf.addTextCoord(new TextCoord(1,1));

        int nf = mf.addNormal(new Vector3d(0,0,1));
        int nb = mf.addNormal(new Vector3d(0,0,-1));
        int nl = mf.addNormal(new Vector3d(-1,0,0));
        int nr = mf.addNormal(new Vector3d(1,0,0));
        int nt = mf.addNormal(new Vector3d(0,1,0));
        int nd = mf.addNormal(new Vector3d(0,-1,0));

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
}

