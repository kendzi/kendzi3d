package kendzi.kendzi3d.editor.drawer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Util for drawing arrows.
 */
public class ArrowDrawUtil {

    /**
     * Draws arrow. Arrow starts at origin and it is directed at +Y axis.
     *
     * @param gl
     *            gl
     * @param glu
     *            glu
     * @param quadratic
     *            quadratic
     * @param length
     *            total length of arrow (arrowhead and base)
     * @param arrowheadLength
     *            length of arrowhead
     * @param baseRadius
     *            radius of base
     * @param arrowheadRadius
     *            radius of arrowhead
     * @param section
     *            number of section
     */
    public static void drawArrow(GL2 gl, GLU glu, GLUquadric quadratic, double length, double arrowheadLength, double baseRadius,
            double arrowheadRadius, int section) {
        gl.glPushMatrix();

        gl.glRotated(-90d, 1d, 0d, 0d);

        double baseLength = length - arrowheadLength;

        gl.glPushMatrix();
        gl.glRotated(180d, 1d, 0d, 0d);
        glu.gluDisk(quadratic, 0, baseRadius, section, 2);
        gl.glPopMatrix();

        glu.gluCylinder(quadratic, baseRadius, baseRadius, baseLength, section, 2);

        gl.glTranslated(0, 0, baseLength);

        glu.gluCylinder(quadratic, arrowheadRadius, 0, arrowheadLength, section, 2);
        gl.glRotated(180d, 1d, 0d, 0d);
        glu.gluDisk(quadratic, 0, arrowheadRadius, section, 2);

        gl.glPopMatrix();
    }

    /**
     * Draws arrowhead. Arrowhead starts at origin and it is directed at +Y
     * axis.
     *
     * @param gl
     *            gl
     * @param glu
     *            glu
     * @param quadratic
     *            quadratic
     * @param length
     *            length of arrowhead
     * @param radius
     *            radius of arrowhead
     * @param section
     *            number of section
     */
    public static void drawArrowhead(GL2 gl, GLU glu, GLUquadric quadratic, double length, double radius, int section) {
        gl.glPushMatrix();

        gl.glRotated(-90d, 1d, 0d, 0d);

        glu.gluCylinder(quadratic, radius, 0, length, section, 2);

        gl.glRotated(180d, 1d, 0d, 0d);
        glu.gluDisk(quadratic, 0, radius, section, 2);

        gl.glPopMatrix();
    }

    /**
     * Draws arrowhead. Arrowhead starts at origin and it is directed at +Y
     * axis. This method don't calculate normals!
     *
     * @param gl
     *            gl
     * @param glu
     *            glu
     * @param quadratic
     *            quadratic
     * @param length
     *            length of arrowhead
     * @param radius
     *            radius of arrowhead
     * @param section
     *            number of section
     */
    public static void drawArrowheadSimple(GL2 gl, GLU glu, GLUquadric quadratic, double length, double radius, int section) {

        double[] xs = new double[section];
        double[] ys = new double[section];

        for (int i = 0; i < section; i++) {
            double steep = (double) i / (double) section;
            double angle = steep * Math.PI * 2d;
            xs[i] = Math.cos(angle);
            ys[i] = Math.sin(angle);
        }

        // top
        gl.glBegin(GL.GL_TRIANGLE_FAN);

        gl.glVertex3d(0, length, 0);
        gl.glNormal3d(0, 1, 0);

        for (int i = 0; i < section; i++) {
            double x = xs[i];
            double y = ys[i];

            gl.glVertex3d(x * radius, 0, -y * radius);
        }
        gl.glVertex3d(xs[0] * radius, 0, -ys[0] * radius);
        gl.glEnd();

        // bottom
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glNormal3d(0, -1, 0);

        gl.glVertex3d(0, 0, 0);

        for (int i = section - 1; i >= 0; i--) {
            double x = xs[i];
            double y = ys[i];

            gl.glVertex3d(x * radius, 0, -y * radius);
        }
        gl.glVertex3d(xs[section - 1] * radius, 0, -ys[section - 1] * radius);

        gl.glEnd();
    }

}
