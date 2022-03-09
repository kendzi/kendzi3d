package kendzi.jogl.util;

import kendzi.math.geometry.point.Vector3dUtil;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.opengl.GL11;

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

        Vector3dc segmentVector = Vector3dUtil.fromTo(begin, end).normalize().mul(segmentLength);

        boolean fill = true;
        double drawedDistance = 0;

        Vector3d drawPoint = new Vector3d(begin);

        GL11.glBegin(GL11.GL_LINES);

        while (distance > drawedDistance + segmentLength) {
            drawedDistance += segmentLength;

            if (fill) {
                GL11.glVertex3d(drawPoint.x(), drawPoint.y(), drawPoint.z());
                GL11.glVertex3d(drawPoint.x() + segmentVector.x(), //
                        drawPoint.y() + segmentVector.y(), //
                        drawPoint.z() + segmentVector.z());
            }
            fill = !fill;
            drawPoint.add(segmentVector);
        }

        if (fill) {
            GL11.glVertex3d(drawPoint.x(), drawPoint.y(), drawPoint.z());
            GL11.glVertex3d(end.x(), end.y(), end.z());

        }

        GL11.glEnd();
    }
}
