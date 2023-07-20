package kendzi.kendzi3d.editor.drawer;

import kendzi.jogl.glu.GLU;
import org.lwjgl.opengl.GL11;

/**
 * Util for drawing arrows.
 */
public class ArrowDrawUtil {

    /**
     * Draws arrow. Arrow starts at origin and it is directed at +Y axis.
     *
     * @param glu
     *            glu
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
    public static void drawArrow(Object glu, double length, double arrowheadLength, double baseRadius, double arrowheadRadius,
            int section) {
        GL11.glPushMatrix();

        GL11.glRotated(-90d, 1d, 0d, 0d);

        double baseLength = length - arrowheadLength;

        GL11.glPushMatrix();
        GL11.glRotated(180d, 1d, 0d, 0d);
        GLU.gluDisk(0, (float) baseRadius, section, 2);
        GL11.glPopMatrix();

        GLU.gluCylinder((float) baseRadius, (float) baseRadius, (float) baseLength, section, 2);

        GL11.glTranslated(0, 0, baseLength);

        GLU.gluCylinder((float) arrowheadRadius, 0, (float) arrowheadLength, section, 2);
        GL11.glRotated(180d, 1d, 0d, 0d);
        GLU.gluDisk(0, (float) arrowheadRadius, section, 2);

        GL11.glPopMatrix();
    }

    /**
     * Draws arrowhead. Arrowhead starts at origin and it is directed at +Y axis.
     *
     * @param glu
     *            glu
     * @param length
     *            length of arrowhead
     * @param radius
     *            radius of arrowhead
     * @param section
     *            number of section
     */
    public static void drawArrowhead(Object glu, double length, double radius, int section) {
        GL11.glPushMatrix();

        GL11.glRotated(-90d, 1d, 0d, 0d);

        GLU.gluCylinder((float) radius, 0, (float) length, section, 2);

        GL11.glRotated(180d, 1d, 0d, 0d);
        GLU.gluDisk(0, (float) radius, section, 2);

        GL11.glPopMatrix();
    }

    /**
     * Draws arrowhead. Arrowhead starts at origin and it is directed at +Y axis.
     * This method don't calculate normals!
     *
     * @param length
     *            length of arrowhead
     * @param radius
     *            radius of arrowhead
     * @param section
     *            number of section
     */
    public static void drawArrowheadSimple(double length, double radius, int section) {

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
