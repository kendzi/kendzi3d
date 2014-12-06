package kendzi.jogl.model.util;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Mesh;

/**
 * Util for mesh conversion.
 * 
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class MeshTriangleUtil {

    /**
     * Converts given mesh into list of triangles. Triangles points are returned
     * in single list. Size of that list is always factor of three. <br>
     * <b> XXX This method is not optimal, should be re-write as iterator. </b>
     *
     * @param mesh
     *            mesh
     * @return list of triangles read from mesh
     */
    public static List<Point3d> toTriangles(Mesh mesh) {

        List<Point3d> points = new ArrayList<Point3d>();

        Point3d[] vertices = mesh.vertices;
        for (Face f : mesh.face) {
            List<Integer> ti = convertToTriangles(f.vertIndex, f.type);

            for (Integer i : ti) {
                points.add(vertices[i]);
            }
        }
        return points;
    }

    private static List<Integer> convertToTriangles(int[] vertIndex, int type) {
        ArrayList<Integer> ret = new ArrayList<Integer>();

        if (type == FaceType.QUADS.getType()) {

            for (int offset = 0; offset < vertIndex.length / 4; offset++) {
                int i = offset * 4;

                int i0 = vertIndex[i];
                int i1 = vertIndex[i + 1];
                int i2 = vertIndex[i + 2];
                int i3 = vertIndex[i + 3];

                ret.add(i0);
                ret.add(i1);
                ret.add(i2);

                ret.add(i0);
                ret.add(i2);
                ret.add(i3);
            }

        } else if (type == FaceType.TRIANGLES.getType()) {

            for (int offset = 0; offset < vertIndex.length; offset++) {

                int i0 = vertIndex[offset];

                ret.add(i0);
            }
        } else if (type == FaceType.TRIANGLE_FAN.getType()) {

            for (int offset = 2; offset < vertIndex.length; offset++) {

                int i0 = vertIndex[0];
                int i1 = vertIndex[offset - 1];
                int i2 = vertIndex[offset];

                ret.add(i0);
                ret.add(i1);
                ret.add(i2);
            }
        } else if (type == FaceType.QUAD_STRIP.getType()) {

            for (int offset = 1; offset < vertIndex.length / 2; offset++) {
                int i = offset * 2;

                int i0 = vertIndex[i - 2];
                int i1 = vertIndex[i - 1];
                int i2 = vertIndex[i];
                int i3 = vertIndex[i + 1];

                ret.add(i0);
                ret.add(i1);
                ret.add(i2);

                ret.add(i1);
                ret.add(i2);
                ret.add(i3);
            }

        } else {
            throw new RuntimeException("Face type : " + type + " not supported");
        }

        return ret;
    }

}
