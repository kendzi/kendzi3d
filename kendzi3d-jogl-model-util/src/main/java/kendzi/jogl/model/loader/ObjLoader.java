package kendzi.jogl.model.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.math.geometry.NormalUtil;
import org.joml.Vector3dc;

public class ObjLoader {

    public static void createMissingNormals(Model model) {

        for (int m = 0; m < model.mesh.length; m++) {
            Mesh mesh = model.mesh[m];

            if (mesh.face != null && mesh.face.length > 0) {

                mesh.normals = addMissingNormals(mesh.normals, mesh.vertices, mesh.face);

            }

        }

    }

    public static Vector3dc[] addMissingNormals(Vector3dc[] normalsArray, Vector3dc[] vertices, Face[] faces) {
        if (faces == null) {
            return null;
        }

        List<Vector3dc> normals = new ArrayList<>();
        if (normalsArray != null) {
            normals.addAll(Arrays.asList(normalsArray)); // XXX
        }

        for (Face face : faces) {

            if (!isNeedToRecalcNormals(face)) {

                // }
                // if (face.normalIndex != null && face.normalIndex.length ==
                // face.vertIndex.length) {
                continue;
            }

            if (face.type == FaceType.TRIANGLES.getType()) {

                int[] normalsIndex = new int[face.vertIndex.length];

                for (int t = 0; t + 3 <= face.vertIndex.length; t = t + 3) {
                    Vector3dc p1 = vertices[face.vertIndex[t]];
                    Vector3dc p2 = vertices[face.vertIndex[t + 1]];
                    Vector3dc p3 = vertices[face.vertIndex[t + 2]];

                    Vector3dc normal = NormalUtil.normal(p1, p2, p3);

                    normals.add(normal);
                    int ni = normals.indexOf(normal);

                    normalsIndex[t] = ni;
                    normalsIndex[t + 1] = ni;
                    normalsIndex[t + 2] = ni;
                }

                face.normalIndex = normalsIndex;

            } else if (face.type == FaceType.TRIANGLE_FAN.getType()) {

                if (face.vertIndex.length < 3) {
                    throw new RuntimeException("TRIANGLE_FAN can't have less then 3 vertex");
                }

                int[] normalsIndex = new int[face.vertIndex.length];

                Vector3dc p1 = vertices[face.vertIndex[0]];

                for (int t = 2; t < face.vertIndex.length; t++) {

                    Vector3dc p2 = vertices[face.vertIndex[t - 1]];
                    Vector3dc p3 = vertices[face.vertIndex[t]];

                    Vector3dc normal = NormalUtil.normal(p1, p2, p3);

                    normals.add(normal);
                    int ni = normals.indexOf(normal);

                    normalsIndex[t - 1] = ni;
                    normalsIndex[t] = ni;
                }

                normalsIndex[0] = normalsIndex[1];

                face.normalIndex = normalsIndex;
            } else {
                throw new RuntimeException("unsuported type: " + face.type);
            }
        }

        return normals.toArray(new Vector3dc[0]);
    }

    private static boolean isNeedToRecalcNormals(Face face) {
        if (face.normalIndex == null) {
            return true;
        }
        if (face.normalIndex.length < face.vertIndex.length) {
            return true;
        }
        for (int i = 0; i < face.normalIndex.length; i++) {
            if (face.normalIndex[i] < 0) {
                return true;
            }
        }

        return false;
    }

}
