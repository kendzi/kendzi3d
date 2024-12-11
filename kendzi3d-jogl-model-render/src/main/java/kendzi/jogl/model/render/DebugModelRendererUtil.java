package kendzi.jogl.model.render;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import org.joml.Vector3dc;
import org.lwjgl.opengl.GL11;

public class DebugModelRendererUtil {

    /**
     * Draws normals.
     * 
     * @param pModel
     *            model
     */
    public static void drawNormals(Model pModel) {

        for (Mesh mesh : pModel.mesh) {
            // blue
            GL11.glColor3f(0.5f, 0.5f, 1.0f);

            // Set line width
            GL11.glLineWidth(2);
            // Repeat count, repeat pattern
            GL11.glLineStipple(1, (short) 0xf0f0);

            GL11.glBegin(GL11.GL_LINES);

            for (Face face : mesh.getFaces()) {
                int vertLength = face.vertIndex.length;

                if (face.normalIndex != null && face.normalIndex.length > 0) {
                    for (int i = 0; i < vertLength; i++) {

                        int normalIndex = face.normalIndex[i];
                        if (mesh.getNormals().length > normalIndex) {

                            int vetexIndex = face.vertIndex[i];
                            GL11.glVertex3d(mesh.getVertices()[vetexIndex].x(), mesh.getVertices()[vetexIndex].y(),
                                    mesh.getVertices()[vetexIndex].z());

                            double normScale = 0.5;
                            GL11.glVertex3d(mesh.getVertices()[vetexIndex].x() + normScale * mesh.getNormals()[normalIndex].x(),
                                    mesh.getVertices()[vetexIndex].y() + normScale * mesh.getNormals()[normalIndex].y(),
                                    mesh.getVertices()[vetexIndex].z() + normScale * mesh.getNormals()[normalIndex].z());
                        }

                    }
                }
            }

            GL11.glEnd();
        }
    }

    /**
     * Draws edges.
     * 
     * @param pModel
     *            model
     */
    public static void drawEdges(Model pModel) {
        for (Mesh mesh : pModel.mesh) {
            // green
            GL11.glColor3f(0.5f, 1.0f, 0.5f);

            // Set line width
            GL11.glLineWidth(4);
            // Repeat count, repeat pattern
            GL11.glLineStipple(1, (short) 0xf0f0);

            for (Face face : mesh.getFaces()) {
                int vertLength = face.vertIndex.length;

                if (face.type == FaceType.TRIANGLE_STRIP.getType()) {
                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    for (int i = 0; i < vertLength; i++) {

                        int vetexIndex = face.vertIndex[i];
                        GL11.glVertex3d(mesh.getVertices()[vetexIndex].x(), mesh.getVertices()[vetexIndex].y(),
                                mesh.getVertices()[vetexIndex].z());
                    }
                    GL11.glEnd();
                    if (face.vertIndex.length > 2) {
                        GL11.glBegin(GL11.GL_LINE_STRIP);
                        for (int i = 0; i < vertLength; i = i + 2) {

                            int vetexIndex = face.vertIndex[i];
                            GL11.glVertex3d(mesh.getVertices()[vetexIndex].x(), mesh.getVertices()[vetexIndex].y(),
                                    mesh.getVertices()[vetexIndex].z());
                        }
                        GL11.glEnd();
                        GL11.glBegin(GL11.GL_LINE_STRIP);
                        for (int i = 1; i < vertLength; i = i + 2) {

                            int vetexIndex = face.vertIndex[i];
                            GL11.glVertex3d(mesh.getVertices()[vetexIndex].x(), mesh.getVertices()[vetexIndex].y(),
                                    mesh.getVertices()[vetexIndex].z());
                        }
                        GL11.glEnd();
                    }
                } else if (face.type == FaceType.TRIANGLES.getType()) {
                    int i = 0;
                    while (i < vertLength) {
                        GL11.glBegin(GL11.GL_LINE_LOOP);
                        int triangleCount = 0;
                        while (i + triangleCount < vertLength && triangleCount < 3) {

                            int vetexIndex = face.vertIndex[i + triangleCount];
                            GL11.glVertex3d(mesh.getVertices()[vetexIndex].x(), mesh.getVertices()[vetexIndex].y(),
                                    mesh.getVertices()[vetexIndex].z());
                            triangleCount++;
                        }
                        i = i + 3;
                        GL11.glEnd();
                    }
                } else if (face.type == FaceType.TRIANGLE_FAN.getType()) {
                    GL11.glBegin(GL11.GL_LINE_LOOP);
                    for (int i = 0; i < vertLength; i++) {

                        int vetexIndex = face.vertIndex[i];
                        GL11.glVertex3d(mesh.getVertices()[vetexIndex].x(), mesh.getVertices()[vetexIndex].y(),
                                mesh.getVertices()[vetexIndex].z());
                    }
                    GL11.glEnd();

                    if (vertLength > 1) {

                        Vector3dc begin = mesh.getVertices()[face.vertIndex[0]];

                        GL11.glBegin(GL11.GL_LINES);
                        for (int i = 2; i < vertLength; i++) {

                            GL11.glVertex3d(begin.x(), begin.y(), begin.z());

                            int endIndex = face.vertIndex[i];
                            GL11.glVertex3d(mesh.getVertices()[endIndex].x(), mesh.getVertices()[endIndex].y(),
                                    mesh.getVertices()[endIndex].z());
                        }
                        GL11.glEnd();
                    }

                } else if (face.type == FaceType.QUADS.getType()) {
                    int q = 0;

                    while (q < vertLength) {
                        GL11.glBegin(GL11.GL_LINE_LOOP);
                        int i = 0;
                        while (i < 4 && i + q < vertLength) {
                            // for (int i = 0; i < 4; i++) {

                            int vetexIndex = face.vertIndex[i + q];
                            GL11.glVertex3d(mesh.getVertices()[vetexIndex].x(), mesh.getVertices()[vetexIndex].y(),
                                    mesh.getVertices()[vetexIndex].z());

                            i++;
                        }
                        q = q + 4;
                        GL11.glEnd();
                    }
                } else if (face.type == FaceType.QUAD_STRIP.getType()) {
                    GL11.glBegin(GL11.GL_LINES);
                    for (int i = 0; i < vertLength; i++) {

                        int vetexIndex = face.vertIndex[i];
                        GL11.glVertex3d(mesh.getVertices()[vetexIndex].x(), mesh.getVertices()[vetexIndex].y(),
                                mesh.getVertices()[vetexIndex].z());
                    }
                    GL11.glEnd();

                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    for (int i = 0; i < vertLength; i = i + 2) {

                        int vetexIndex = face.vertIndex[i];
                        GL11.glVertex3d(mesh.getVertices()[vetexIndex].x(), mesh.getVertices()[vetexIndex].y(),
                                mesh.getVertices()[vetexIndex].z());
                    }
                    GL11.glEnd();

                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    for (int i = 1; i < vertLength; i = i + 2) {

                        int vetexIndex = face.vertIndex[i];
                        GL11.glVertex3d(mesh.getVertices()[vetexIndex].x(), mesh.getVertices()[vetexIndex].y(),
                                mesh.getVertices()[vetexIndex].z());
                    }
                    GL11.glEnd();
                }
            }
        }
    }
}
