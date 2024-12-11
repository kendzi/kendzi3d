package kendzi.kendzi3d.editor.drawer;

import java.util.stream.IntStream;

import kendzi.jogl.MatrixMath;
import kendzi.jogl.glu.GLU;
import kendzi.jogl.util.VertexArrayObject;

/**
 * Util for drawing arrows.
 */
public class ArrowDrawUtil {
    private ArrowDrawUtil() {
        /* Hide constructor */}

    /**
     * Draws arrow. Arrow starts at origin and it is directed at +Y axis.
     *
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
    public static void drawArrow(double length, double arrowheadLength, double baseRadius, double arrowheadRadius, int section) {
        MatrixMath.glPushMatrix();

        MatrixMath.glRotated(-90d, 1d, 0d, 0d);

        double baseLength = length - arrowheadLength;

        MatrixMath.glPushMatrix();
        MatrixMath.glRotated(180d, 1d, 0d, 0d);
        GLU.gluDisk(0, (float) baseRadius, section, 2);
        MatrixMath.glPopMatrix();

        GLU.gluCylinder((float) baseRadius, (float) baseRadius, (float) baseLength, section, 2);

        MatrixMath.glTranslated(0, 0, baseLength);

        GLU.gluCylinder((float) arrowheadRadius, 0, (float) arrowheadLength, section, 2);
        MatrixMath.glRotated(180d, 1d, 0d, 0d);
        GLU.gluDisk(0, (float) arrowheadRadius, section, 2);

        MatrixMath.glPopMatrix();
    }

    /**
     * Draws arrowhead. Arrowhead starts at origin and it is directed at +Y axis.
     *
     * @param length
     *            length of arrowhead
     * @param radius
     *            radius of arrowhead
     * @param section
     *            number of section
     */
    public static void drawArrowhead(double length, double radius, int section) {
        MatrixMath.glPushMatrix();

        MatrixMath.glRotated(-90d, 1d, 0d, 0d);

        GLU.gluCylinder((float) radius, 0, (float) length, section, 2);

        MatrixMath.glRotated(180d, 1d, 0d, 0d);
        GLU.gluDisk(0, (float) radius, section, 2);

        MatrixMath.glPopMatrix();
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

        try (VertexArrayObject top = getTop(xs, ys, radius, section, length);
                VertexArrayObject bottom = getBottom(xs, ys, radius, section)) {
            top.draw();
            bottom.draw();
        }
    }

    private static VertexArrayObject getBottom(double[] xs, double[] ys, double radius, int section) {
        // This was originally GL_TRIANGLE_FAN
        // If we were switching entirely to GL 3+ (core context), we could use indices
        // instead of regenerating the points
        final double[] vertex = new double[3 * (section + 2)];
        vertex[0] = 0;
        vertex[1] = 0;
        vertex[2] = 0;
        final double[] normal = new double[vertex.length];
        normal[0] = 0;
        normal[1] = -1;
        normal[2] = 0;

        for (int i = section - 1; i >= 0; i--) {
            int index = (section - 1) - i;
            double x = xs[i];
            double y = ys[i];
            vertex[index] = x * radius;
            vertex[index + 1] = 0;
            vertex[index + 2] = -y * radius;
            normal[index] = normal[0];
            normal[index + 1] = normal[1];
            normal[index + 2] = normal[2];
        }

        vertex[vertex.length - 3] = xs[section - 1] * radius;
        normal[normal.length - 3] = normal[0];
        vertex[vertex.length - 2] = 0;
        normal[normal.length - 2] = normal[1];
        vertex[vertex.length - 1] = -ys[section - 1] * radius;
        normal[normal.length - 1] = normal[2];
        return VertexArrayObject.createVertexArrayObject(IntStream.range(0, vertex.length / 3).toArray(),
                convertTriangleFan(vertex), convertTriangleFan(normal), null, null);
    }

    private static VertexArrayObject getTop(double[] xs, double[] ys, double radius, int section, double length) {
        // This was originally GL_TRIANGLE_FAN
        // If we were switching entirely to GL 3+ (core context), we could use indices
        // instead of regenerating the points
        final double[] vertex = new double[3 * (section + 2)];
        vertex[0] = 0;
        vertex[1] = length;
        vertex[2] = 0;
        final double[] normal = new double[vertex.length];
        normal[0] = 0;
        normal[1] = 1;
        normal[2] = 0;

        for (int i = 0; i < section; i++) {
            double x = xs[i];
            double y = ys[i];
            int index = (i + 1) * 3;
            vertex[index] = x * radius;
            vertex[index + 1] = 0;
            vertex[index + 2] = -y * radius;
            normal[index] = normal[0];
            normal[index + 1] = normal[1];
            normal[index + 2] = normal[2];
        }
        vertex[vertex.length - 3] = xs[0] * radius;
        normal[normal.length - 3] = normal[0];
        vertex[vertex.length - 2] = 0;
        normal[normal.length - 2] = normal[1];
        vertex[vertex.length - 1] = -ys[0] * radius;
        normal[normal.length - 1] = normal[2];
        return VertexArrayObject.createVertexArrayObject(IntStream.range(0, vertex.length / 3).toArray(),
                convertTriangleFan(vertex), convertTriangleFan(normal), null, null);
    }

    private static double[] convertTriangleFan(final double[] input) {
        final double[] output = new double[(input.length - 2) * 3];
        final double[] origin = new double[] { input[0], input[1], input[2] };
        double[] previous = new double[] { input[3], input[4], input[5] };
        for (int i = 2; i < input.length / 3; i++) {
            int index = (i - 2) * 9;
            output[index] = origin[0];
            output[++index] = origin[1];
            output[++index] = origin[2];
            output[++index] = previous[0];
            output[++index] = previous[1];
            output[++index] = previous[2];

            previous[0] = input[i * 3];
            previous[1] = input[i * 3 + 1];
            previous[2] = input[i * 3 + 2];
            output[++index] = previous[0];
            output[++index] = previous[1];
            output[++index] = previous[2];
        }
        return output;
    }

}
