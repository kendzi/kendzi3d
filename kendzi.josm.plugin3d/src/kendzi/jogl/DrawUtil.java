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

public class DrawUtil {

    public static void drawDotY(GL2 pGl, Double size, int numberOfPoints) {

        double x = 1d;
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
}
