/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.util;

import javax.vecmath.Point3d;
import org.lwjgl.opengl.GL11;

public class DrawUtil {

    public static void drawDotY(double radius, int numberOfPoints) {

        double x = radius;
        double y = 0d;

        double angle = 2 * Math.PI / numberOfPoints;

        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = 0; i < numberOfPoints; i++) {

            double cosA = Math.cos(angle);
            double sinA = Math.sin(angle);

            double nx = x * cosA - y * sinA;
            double ny = x * sinA + y * cosA;

            GL11.glVertex3d(x, 0, -y);

            x = nx;
            y = ny;

        }
        GL11.glEnd();

    }

    public static void drawDotOuterY(double radius, int numberOfPoints) {

        double x = radius;
        double y = 0d;

        double angle = 2 * Math.PI / numberOfPoints;

        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < numberOfPoints; i++) {

            double cosA = Math.cos(angle);
            double sinA = Math.sin(angle);

            double nx = x * cosA - y * sinA;
            double ny = x * sinA + y * cosA;

            GL11.glVertex3d(x, 0, -y);

            x = nx;
            y = ny;

        }
        GL11.glEnd();
    }

    public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2) {
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glEnd();
    }

    /**
     * Draw guads on XZ plane, y==0. Skeep odd quads. XXX
     *
     * @param size
     *            size of quads area
     * @param odd
     *            if draw odd quads
     */
    public static void drawTiles(int size, boolean odd) {

        GL11.glBegin(GL11.GL_QUADS);
        boolean aBlueTile;
        for (int z = -size / 2; z <= size / 2 - 1; z++) {
            // set color type for new
            aBlueTile = z % 2 == 0;
            // row
            for (int x = -size / 2; x <= size / 2 - 1; x++) {
                if (aBlueTile && odd) {
                    // drawing blue
                    drawTile(x, z);
                } else if (!aBlueTile && !odd) {
                    drawTile(x, z);
                }
                aBlueTile = !aBlueTile;
            }
        }
        GL11.glEnd();
    }

    /**
     * Draw single title at given coordinate.
     *
     * @param x
     *            coordinate x
     * @param z
     *            coordinate z
     */
    public static void drawTile(int x, int z) {
        // points created in counter-clockwise order
        // bottom left point
        GL11.glVertex3f(x, 0.0f, z + 1.0f);
        GL11.glVertex3f(x + 1.0f, 0.0f, z + 1.0f);
        GL11.glVertex3f(x + 1.0f, 0.0f, z);
        GL11.glVertex3f(x, 0.0f, z);
    }

    /**
     * Switch to 2D viewing (an orthographic projection).
     *
     */
    public static void begin2D() {
        begin2D(800, 800);
    }

    /**
     * Switch to 2D viewing (an orthographic projection).
     *
     * @param panelWidth
     *            width
     * @param panelHeight
     *            height
     */
    public static void begin2D(double panelWidth, double panelHeight) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        // save projection settings
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        GL11.glOrtho(0.0f, panelWidth, panelHeight, 0.0f, -1.0f, 1.0f);
        // left, right, bottom, top, near, far

        /*
         * In an orthographic projection, the y-axis runs from the bottom-left, upwards.
         * This is reversed back to the more familiar top-left, downwards, by switching
         * the the top and bottom values in glOrtho().
         */
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        // save model view settings
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        // GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    /**
     * switch back to 3D viewing.
     *
     */
    public static void end2D() {
        // GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        // restore previous projection settings
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        // restore previous model view settings
        GL11.glPopMatrix();
    }

    public static void drawBox(double size) {

        // right
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glNormal3d(1d, 0, 0);
        GL11.glVertex3d(size, size, size);
        GL11.glVertex3d(size, -size, size);
        GL11.glVertex3d(size, -size, -size);
        GL11.glVertex3d(size, size, -size);

        // back
        GL11.glNormal3d(0, 0, -1d);
        GL11.glVertex3d(size, size, -size);
        GL11.glVertex3d(size, -size, -size);
        GL11.glVertex3d(-size, -size, -size);
        GL11.glVertex3d(-size, size, -size);

        // left
        GL11.glNormal3d(-1d, 0, 0);
        GL11.glVertex3d(-size, size, -size);
        GL11.glVertex3d(-size, -size, -size);
        GL11.glVertex3d(-size, -size, size);
        GL11.glVertex3d(-size, size, size);

        // front
        GL11.glNormal3d(0, 0, 1d);
        GL11.glVertex3d(-size, size, size);
        GL11.glVertex3d(-size, -size, size);
        GL11.glVertex3d(size, -size, size);
        GL11.glVertex3d(size, size, size);

        // top
        GL11.glNormal3d(0, 1d, 0);
        GL11.glVertex3d(size, size, size);
        GL11.glVertex3d(size, size, -size);
        GL11.glVertex3d(-size, size, -size);
        GL11.glVertex3d(-size, size, size);

        // bottom
        GL11.glNormal3d(0, -1d, 0);
        GL11.glVertex3d(-size, -size, size);
        GL11.glVertex3d(-size, -size, -size);
        GL11.glVertex3d(size, -size, -size);
        GL11.glVertex3d(size, -size, size);

        GL11.glEnd();
    }

    public static void drawFullBox(Point3d max, Point3d min) {

        // right
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glNormal3d(1d, 0, 0);
        GL11.glVertex3d(max.x, max.y, max.z);
        GL11.glVertex3d(max.x, min.y, max.z);
        GL11.glVertex3d(max.x, min.y, min.z);
        GL11.glVertex3d(max.x, max.y, min.z);

        // back
        GL11.glNormal3d(0, 0, -1d);
        GL11.glVertex3d(max.x, max.y, min.z);
        GL11.glVertex3d(max.x, min.y, min.z);
        GL11.glVertex3d(min.x, min.y, min.z);
        GL11.glVertex3d(min.x, max.y, min.z);

        // left
        GL11.glNormal3d(-1d, 0, 0);
        GL11.glVertex3d(min.x, max.y, min.z);
        GL11.glVertex3d(min.x, min.y, min.z);
        GL11.glVertex3d(min.x, min.y, max.z);
        GL11.glVertex3d(min.x, max.y, max.z);

        // front
        GL11.glNormal3d(0, 0, 1d);
        GL11.glVertex3d(min.x, max.y, max.z);
        GL11.glVertex3d(min.x, min.y, max.z);
        GL11.glVertex3d(max.x, min.y, max.z);
        GL11.glVertex3d(max.x, max.y, max.z);

        // top
        GL11.glNormal3d(0, 1d, 0);
        GL11.glVertex3d(max.x, max.y, max.z);
        GL11.glVertex3d(max.x, max.y, min.z);
        GL11.glVertex3d(min.x, max.y, min.z);
        GL11.glVertex3d(min.x, max.y, max.z);

        // bottom
        GL11.glNormal3d(0, -1d, 0);
        GL11.glVertex3d(max.x, min.y, max.z);
        GL11.glVertex3d(max.x, min.y, min.z);
        GL11.glVertex3d(min.x, min.y, min.z);
        GL11.glVertex3d(min.x, min.y, max.z);

        GL11.glEnd();
    }

    public static void drawBox(Point3d max, Point3d min) {

        GL11.glBegin(GL11.GL_LINES);

        GL11.glVertex3d(max.x, max.y, min.z);
        GL11.glVertex3d(min.x, max.y, min.z);

        GL11.glVertex3d(max.x, min.y, min.z);
        GL11.glVertex3d(min.x, min.y, min.z);

        GL11.glVertex3d(max.x, min.y, max.z);
        GL11.glVertex3d(min.x, min.y, max.z);

        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_LOOP);

        GL11.glVertex3d(max.x, max.y, max.z);
        GL11.glVertex3d(max.x, min.y, max.z);
        GL11.glVertex3d(max.x, min.y, min.z);
        GL11.glVertex3d(max.x, max.y, min.z);
        GL11.glVertex3d(max.x, max.y, max.z);

        GL11.glVertex3d(min.x, max.y, max.z);
        GL11.glVertex3d(min.x, min.y, max.z);
        GL11.glVertex3d(min.x, min.y, min.z);
        GL11.glVertex3d(min.x, max.y, min.z);
        GL11.glVertex3d(min.x, max.y, max.z);

        GL11.glEnd();
    }

    public static void drawFlatArrowY(double lenght, double lenghtArrow, double widthBase, double widthArrow) {

        double lenghtBase = lenght - lenghtArrow;

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex3d(lenght, 0, 0);
        GL11.glVertex3d(lenghtBase, 0, -widthArrow);
        GL11.glVertex3d(lenghtBase, 0, -widthBase);
        GL11.glVertex3d(0, 0, -widthBase);
        GL11.glVertex3d(0, 0, widthBase);
        GL11.glVertex3d(lenghtBase, 0, widthBase);
        GL11.glVertex3d(lenghtBase, 0, widthArrow);

        GL11.glEnd();
    }
}
