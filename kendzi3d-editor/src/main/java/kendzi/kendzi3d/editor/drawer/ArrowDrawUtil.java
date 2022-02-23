package kendzi.kendzi3d.editor.drawer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import org.lwjgl.opengl.GL11;

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
        GL11.glPushMatrix();

        GL11.glRotated(-90d, 1d, 0d, 0d);

        double baseLength = length - arrowheadLength;

        GL11.glPushMatrix();
        GL11.glRotated(180d, 1d, 0d, 0d);
        glu.gluDisk(quadratic, 0, baseRadius, section, 2);
        GL11.glPopMatrix();

        glu.gluCylinder(quadratic, baseRadius, baseRadius, baseLength, section, 2);

        GL11.glTranslated(0, 0, baseLength);

        glu.gluCylinder(quadratic, arrowheadRadius, 0, arrowheadLength, section, 2);
        GL11.glRotated(180d, 1d, 0d, 0d);
        glu.gluDisk(quadratic, 0, arrowheadRadius, section, 2);

        GL11.glPopMatrix();
    }

    /**
     * Draws arrowhead. Arrowhead starts at origin and it is directed at +Y axis.
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
        GL11.glPushMatrix();

        GL11.glRotated(-90d, 1d, 0d, 0d);

        glu.gluCylinder(quadratic, radius, 0, length, section, 2);

        GL11.glRotated(180d, 1d, 0d, 0d);
        glu.gluDisk(quadratic, 0, radius, section, 2);

        GL11.glPopMatrix();
    }

    /**
     * Draws arrowhead. Arrowhead starts at origin and it is directed at +Y axis.
     * This method don't calculate normals!
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
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        GL11.glVertex3d(0, length, 0);
        GL11.glNormal3d(0, 1, 0);

        for (int i = 0; i < section; i++) {
            double x = xs[i];
            double y = ys[i];

            GL11.glVertex3d(x * radius, 0, -y * radius);
        }
        GL11.glVertex3d(xs[0] * radius, 0, -ys[0] * radius);
        GL11.glEnd();

        // bottom
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glNormal3d(0, -1, 0);

        GL11.glVertex3d(0, 0, 0);

        for (int i = section - 1; i >= 0; i--) {
            double x = xs[i];
            double y = ys[i];

            GL11.glVertex3d(x * radius, 0, -y * radius);
        }
        GL11.glVertex3d(xs[section - 1] * radius, 0, -ys[section - 1] * radius);

        GL11.glEnd();
    }

}
