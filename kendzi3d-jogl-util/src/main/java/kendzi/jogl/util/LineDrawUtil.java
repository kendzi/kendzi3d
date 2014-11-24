package kendzi.jogl.util;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.math.geometry.point.Vector3dUtil;

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

        gl.glBegin(GL.GL_LINES);

        while (distance > drawedDistance + segmentLength) {
            drawedDistance += segmentLength;

            if (fill) {
                gl.glVertex3d(drawPoint.x, drawPoint.y, drawPoint.z);
                gl.glVertex3d(drawPoint.x + segmentVector.x, //
                        drawPoint.y + segmentVector.y, //
                        drawPoint.z + segmentVector.z);
            }
            fill = !fill;
            drawPoint.add(segmentVector);
        }

        if (fill) {
            gl.glVertex3d(drawPoint.x, drawPoint.y, drawPoint.z);
            gl.glVertex3d(end.x, end.y, end.z);

        }

        gl.glEnd();
    }
}
