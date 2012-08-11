package kendzi.jogl.model.render;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;

public class DebugModelRendererUtil {


    /** Draws normals.
     * @param pGl gl
     * @param pModel model
     */
    public static void drawNormals(GL2 pGl, Model pModel) {

        for (Mesh mesh : pModel.mesh) {
            //blue
            pGl.glColor3f(0.5f, 0.5f, 1.0f);

            // Set line width
            pGl.glLineWidth(2);
            // Repeat count, repeat pattern
            pGl.glLineStipple(1, (short) 0xf0f0);

            pGl.glBegin(GL2.GL_LINES);

            for (Face face : mesh.face) {
                int vertLength = face.vertIndex.length;

                if (face.normalIndex != null && face.normalIndex.length > 0) {
                    for (int i = 0; i < vertLength; i++) {

                        int normalIndex = face.normalIndex[i];
                        if (mesh.normals.length > normalIndex) {

                            int vetexIndex = face.vertIndex[i];
                            pGl.glVertex3d(
                                    mesh.vertices[vetexIndex].x,
                                    mesh.vertices[vetexIndex].y,
                                    mesh.vertices[vetexIndex].z);

                            double normScale = 0.5;
                            pGl.glVertex3d(
                            mesh.vertices[vetexIndex].x + normScale * mesh.normals[normalIndex].x,
                            mesh.vertices[vetexIndex].y + normScale * mesh.normals[normalIndex].y,
                            mesh.vertices[vetexIndex].z + normScale * mesh.normals[normalIndex].z);
                        }

                    }
                }
            }

            pGl.glEnd();
        }
    }

    /** Draws edges.
     * @param pGl gl
     * @param pModel model
     */
    public static void drawEdges(GL2 pGl, Model pModel) {
        for (Mesh mesh : pModel.mesh) {
            //green
            pGl.glColor3f(0.5f, 1.0f, 0.5f);

            // Set line width
            pGl.glLineWidth(4);
            // Repeat count, repeat pattern
            pGl.glLineStipple(1, (short) 0xf0f0);


            for (Face face : mesh.face) {
                int vertLength = face.vertIndex.length;

                if (face.type == FaceType.TRIANGLE_STRIP.getType()) {
                    pGl.glBegin(GL2.GL_LINE_STRIP);
                    for (int i = 0; i < vertLength; i++) {

                        int vetexIndex = face.vertIndex[i];
                        pGl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }
                    pGl.glEnd();
                    if (face.vertIndex.length > 2) {
                        pGl.glBegin(GL2.GL_LINE_STRIP);
                        for (int i = 0; i < vertLength; i = i + 2) {

                            int vetexIndex = face.vertIndex[i];
                            pGl.glVertex3d(
                                    mesh.vertices[vetexIndex].x,
                                    mesh.vertices[vetexIndex].y,
                                    mesh.vertices[vetexIndex].z);
                        }
                        pGl.glEnd();
                        pGl.glBegin(GL2.GL_LINE_STRIP);
                        for (int i = 1; i < vertLength; i = i + 2) {

                            int vetexIndex = face.vertIndex[i];
                            pGl.glVertex3d(
                                    mesh.vertices[vetexIndex].x,
                                    mesh.vertices[vetexIndex].y,
                                    mesh.vertices[vetexIndex].z);
                        }
                        pGl.glEnd();
                    }
                } else if (face.type == FaceType.TRIANGLES.getType()) {
                    int i = 0;
                    while (i < vertLength) {
                        pGl.glBegin(GL2.GL_LINE_LOOP);
                        int triangleCount = 0;
                        while (i + triangleCount < vertLength && triangleCount < 3) {

                            int vetexIndex = face.vertIndex[i + triangleCount];
                            pGl.glVertex3d(
                                    mesh.vertices[vetexIndex].x,
                                    mesh.vertices[vetexIndex].y,
                                    mesh.vertices[vetexIndex].z);
                            triangleCount++;
                        }
                        i = i + 3;
                        pGl.glEnd();
                    }
                } else if (face.type == FaceType.TRIANGLE_FAN.getType()) {
                    pGl.glBegin(GL2.GL_LINE_LOOP);
                    for (int i = 0; i < vertLength; i++) {

                        int vetexIndex = face.vertIndex[i];
                        pGl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }
                    pGl.glEnd();

                    if (vertLength > 1) {

                        Point3d begin = mesh.vertices[face.vertIndex[0]];

                        pGl.glBegin(GL2.GL_LINES);
                        for (int i = 2; i < vertLength; i++) {

                            pGl.glVertex3d(begin.x, begin.y, begin.z);

                            int endIndex = face.vertIndex[i];
                            pGl.glVertex3d(
                                    mesh.vertices[endIndex].x,
                                    mesh.vertices[endIndex].y,
                                    mesh.vertices[endIndex].z);
                        }
                        pGl.glEnd();
                    }

                } else if (face.type == FaceType.QUADS.getType()) {
                    int q = 0;

                    while (q < vertLength) {
                        pGl.glBegin(GL2.GL_LINE_LOOP);
                        int i = 0;
                        while (i < 4 && i + q < vertLength) {
//                        for (int i = 0; i < 4; i++) {

                            int vetexIndex = face.vertIndex[i + q];
                            pGl.glVertex3d(
                                    mesh.vertices[vetexIndex].x,
                                    mesh.vertices[vetexIndex].y,
                                    mesh.vertices[vetexIndex].z);

                            i++;
                        }
                        q = q + 4;
                        pGl.glEnd();
                    }
                } else if (face.type == FaceType.QUAD_STRIP.getType()) {
                    pGl.glBegin(GL2.GL_LINES);
                    for (int i = 0; i < vertLength; i++) {

                        int vetexIndex = face.vertIndex[i];
                        pGl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }
                    pGl.glEnd();

                    pGl.glBegin(GL2.GL_LINE_STRIP);
                    for (int i = 0; i < vertLength; i = i + 2) {

                        int vetexIndex = face.vertIndex[i];
                        pGl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }
                    pGl.glEnd();

                    pGl.glBegin(GL2.GL_LINE_STRIP);
                    for (int i = 1; i < vertLength; i = i + 2) {

                        int vetexIndex = face.vertIndex[i];
                        pGl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }
                    pGl.glEnd();
                }
            }
        }
    }
}
