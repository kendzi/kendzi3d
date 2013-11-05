package kendzi.josm.kendzi3d.jogl.model.roof.mk.ui;

import javax.media.opengl.GL2;

public class DrawUtil {
    /** Draw guads on XZ plane, y==0. Skeep odd quads.
     * XXX
     * @param gl gl
     * @param size size of quads area
     * @param odd if draw odd quads
     */
    public static void drawTiles(GL2 gl, int size, boolean odd)  {


        gl.glBegin(GL2.GL_QUADS);
        boolean aBlueTile;
        for (int z = -size / 2; z <= size / 2 - 1; z++) {
            // set color type for new
            aBlueTile = z % 2 == 0 ? true : false;
            // row
            for (int x = -size / 2; x <= size / 2 - 1; x++) {
                if (aBlueTile && odd) {
                    // drawing blue
                    drawTile(gl, x, z);
                } else if (!aBlueTile && !odd) {
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
