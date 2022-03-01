package kendzi.jogl.util;

import com.jogamp.opengl.GL2;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.math.geometry.point.Vector3dUtil;
import org.lwjgl.opengl.GL11;

/**
 * Util for drawing lines.
 */
public class LineDrawUtil {

    /**
     * Draw dotted line, which segments size depends on distance from camera.
     *
     * @param gl
     *            gl
     * @param begin
     *            begin point
     * @param end
     *            end point
     * @param segmentLength
     *            length of line segment
     */
    public static void drawDottedLine(GL2 gl, Point3d begin, Point3d end, double segmentLength) {

        double distance = begin.distance(end);

        Vector3d segmentVector = Vector3dUtil.fromTo(begin, end);
        segmentVector.normalize();
        segmentVector.scale(segmentLength);

        boolean fill = true;
        double drawedDistance = 0;

        Point3d drawPoint = new Point3d(begin);

        GL11.glBegin(GL11.GL_LINES);

        while (distance > drawedDistance + segmentLength) {
            drawedDistance += segmentLength;

            if (fill) {
                GL11.glVertex3d(drawPoint.x, drawPoint.y, drawPoint.z);
                GL11.glVertex3d(drawPoint.x + segmentVector.x, //
                        drawPoint.y + segmentVector.y, //
                        drawPoint.z + segmentVector.z);
            }
            fill = !fill;
            drawPoint.add(segmentVector);
        }

        if (fill) {
            GL11.glVertex3d(drawPoint.x, drawPoint.y, drawPoint.z);
            GL11.glVertex3d(end.x, end.y, end.z);

        }

        GL11.glEnd();
    }
}
