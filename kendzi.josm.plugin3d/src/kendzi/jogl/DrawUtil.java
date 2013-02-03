/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;

public class DrawUtil {

    public static void drawDotY(GL2 pGl, double radius, int numberOfPoints) {

        double x = radius;
        double y = 0d;

        double angle = 2 * Math.PI / numberOfPoints;

        pGl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i < numberOfPoints; i++) {

            double cosA = Math.cos(angle);
            double sinA = Math.sin(angle);

            double nx = x * cosA - y * sinA;
            double ny = x * sinA + y * cosA;

            pGl.glVertex3d(x, 0, -y);

            x = nx;
            y = ny;

        }
        pGl.glEnd();

    }
    public static void drawDotOuterY(GL2 pGl, double radius, int numberOfPoints) {

        double x = radius;
        double y = 0d;

        double angle = 2 * Math.PI / numberOfPoints;

        pGl.glBegin(GL2.GL_LINE_LOOP);
        for (int i = 0; i < numberOfPoints; i++) {

            double cosA = Math.cos(angle);
            double sinA = Math.sin(angle);

            double nx = x * cosA - y * sinA;
            double ny = x * sinA + y * cosA;

            pGl.glVertex3d(x, 0, -y);

            x = nx;
            y = ny;

        }
        pGl.glEnd();
    }

    /** Draw guads on XZ plane, y==0. Skeep odd quads.
     * XXX
     * @param gl gl
     * @param color
     * @param size size of quads area
     * @param odd if draw odd quads
     */
    public static void drawTiles(GL2 gl, int size, boolean odd)  {


        gl.glBegin(GL2.GL_QUADS);
        boolean aBlueTile;
        for (int z = -size / 2; z <= (size / 2) - 1; z++) {
            // set colour type for new
            aBlueTile = (z % 2 == 0) ? true : false;
            // row
            for (int x = -size / 2; x <= (size / 2) - 1; x++) {
                if (aBlueTile && (odd)) {
                    // drawing blue
                    drawTile(gl, x, z);
                } else if (!aBlueTile && (!odd)) {
                    drawTile(gl, x, z);
                }
                aBlueTile = !aBlueTile;
            }
        }
        gl.glEnd();
    }

    /** Draw single title at given coordinate.
     * @param gl gl
     * @param x coordinate x
     * @param z coordinate z
     */
    public static  void drawTile(GL2 gl, int x, int z) {
        // points created in counter-clockwise order
        // bottom left point
        gl.glVertex3f(x, 0.0f, z + 1.0f);
        gl.glVertex3f(x + 1.0f, 0.0f, z + 1.0f);
        gl.glVertex3f(x + 1.0f, 0.0f, z);
        gl.glVertex3f(x, 0.0f, z);
    }

    /**
     * Switch to 2D viewing (an orthographic projection).
     * @param gl
     */
    public void begin2D(GL2 gl) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        // save projection settings
        gl.glPushMatrix();
        gl.glLoadIdentity();
        double panelWidth = 800;
        double panelHeight = 800;
        gl.glOrtho(0.0f, panelWidth, panelHeight, 0.0f, -1.0f, 1.0f);
        // left, right, bottom, top, near, far

        /*
         * In an orthographic projection, the y-axis runs from the bottom-left,
         * upwards. This is reversed back to the more familiar top-left,
         * downwards, by switching the the top and bottom values in glOrtho().
         */
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        // save model view settings
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glDisable(GL2.GL_DEPTH_TEST);
    }

    /**
     * switch back to 3D viewing.
     * @param gl
     */
    public void end2D(GL2 gl) {
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        // restore previous projection settings
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        // restore previous model view settings
        gl.glPopMatrix();
    }

    public static void drawBox(GL2 pGl, Point3d max, Point3d min) {

        pGl.glBegin(GL2.GL_LINES);

        pGl.glVertex3d(max.x, max.y, min.z);
        pGl.glVertex3d(min.x, max.y, min.z);

        pGl.glVertex3d(max.x, min.y, min.z);
        pGl.glVertex3d(min.x, min.y, min.z);

        pGl.glVertex3d(max.x, min.y, max.z);
        pGl.glVertex3d(min.x, min.y, max.z);

        pGl.glEnd();

        pGl.glBegin(GL2.GL_LINE_LOOP);

        pGl.glVertex3d(max.x, max.y, max.z);
        pGl.glVertex3d(max.x, min.y, max.z);
        pGl.glVertex3d(max.x, min.y, min.z);
        pGl.glVertex3d(max.x, max.y, min.z);
        pGl.glVertex3d(max.x, max.y, max.z);

        pGl.glVertex3d(min.x, max.y, max.z);
        pGl.glVertex3d(min.x, min.y, max.z);
        pGl.glVertex3d(min.x, min.y, min.z);
        pGl.glVertex3d(min.x, max.y, min.z);
        pGl.glVertex3d(min.x, max.y, max.z);

        pGl.glEnd();
    }
}

