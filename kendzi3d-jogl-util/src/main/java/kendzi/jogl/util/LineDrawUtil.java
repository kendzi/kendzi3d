package kendzi.jogl.util;

import java.util.stream.IntStream;

import kendzi.math.geometry.point.Vector3dUtil;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.opengl.GL11C;

/**
 * Util for drawing lines.
 */
public class LineDrawUtil {

    /**
     * Draw dotted line, which segments size depends on distance from camera.
     *
     * @param begin
     *            begin point
     * @param end
     *            end point
     * @param segmentLength
     *            length of line segment
     */
    public static void drawDottedLine(Vector3dc begin, Vector3dc end, double segmentLength) {

        double distance = begin.distance(end);
        // No line to draw
        if (distance == 0d) {
            return;
        }

        Vector3dc segmentVector = new Vector3d(end).sub(begin).normalize().mul(segmentLength);

        boolean fill = true;
        double drawedDistance = 0;

        Vector3d drawPoint = new Vector3d(begin);
        Vector3d nextPoint = drawPoint.add(segmentVector, new Vector3d());

        final double[] lines = new double[6
                * ((int) (distance / (2 /* Divide by two, as we are skipping every other */ * segmentLength))
                        + (distance % segmentLength != 0 ? 1 : 0))];
        int index = 0;
        while (distance > drawedDistance + segmentLength) {
            drawedDistance += segmentLength;
            if (fill) {
                fillArray(index, lines, drawPoint, nextPoint);
                index++;
            }
            fill = !fill;
            drawPoint.set(nextPoint);
            nextPoint.add(segmentVector);
        }

        if (fill) {
            fillArray(index, lines, drawPoint, end);
        }

        try (VertexArrayObject vao = VertexArrayObject.createVertexArrayObject(IntStream.of(0, lines.length / 3).toArray(), lines,
                null, null, null)) {
            vao.draw(GL11C.GL_LINES);
        }
    }

    private static void fillArray(int index, double[] array, Vector3dc start, Vector3dc end) {
        int i = 6 * index;
        array[i++] = start.x();
        array[i++] = start.y();
        array[i++] = start.z();
        array[i++] = end.x();
        array[i++] = end.y();
        array[i] = end.z();
    }
}
