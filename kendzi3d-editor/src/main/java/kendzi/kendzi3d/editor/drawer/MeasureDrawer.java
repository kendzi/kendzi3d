package kendzi.kendzi3d.editor.drawer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Viewport;
import kendzi.jogl.util.DrawUtil;
import kendzi.math.geometry.point.Vector3dUtil;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Drawer for measure tap.
 */
public class MeasureDrawer {

    private final GLU glu = new GLU();

    private final GLUT glut = new GLUT();

    /**
     * Draws measure tap with distance arrows and distance value. Measure tap is
     * draw in Y direction always rotated to camera. XXX add measure begin point
     *
     * @param gl
     *            gl
     * @param begin
     *            measure begin
     * @param end
     *            measure end
     * @param value
     *            measure value
     * @param viewport
     *            viewport
     * @param horizontalDistance
     *            horizontal distance
     * @param arrowHeight
     *            arrow height
     * @param arrowWidth
     *            arrow width
     * @param lineWidth
     *            line width
     */
    public void drawYMeasureWithArrows(GL2 gl, Point3d begin, Point3d end, double value, Viewport viewport,
            double horizontalDistance, double arrowHeight, double arrowWidth, float lineWidth) {

        gl.glLineWidth(lineWidth);

        Vector3d screenHorizontally = new Vector3d(viewport.getScreenHorizontally());
        screenHorizontally.normalize();

        Vector3d arrowheadBaseWidthVector = new Vector3d(screenHorizontally);
        arrowheadBaseWidthVector.scale(arrowWidth);

        screenHorizontally.scale(horizontalDistance);

        // top horizontal line
        drawLine(gl, end.x, end.y, end.z, //
                end.x + screenHorizontally.x, end.y + screenHorizontally.y, end.z + screenHorizontally.z);

        // bottom horizontal line
        drawLine(gl, begin.x, begin.y, begin.z, //
                begin.x + screenHorizontally.x, begin.y + screenHorizontally.y, begin.z + screenHorizontally.z);

        screenHorizontally.scale(0.5);

        Point3d bottomArrowhead = new Point3d(screenHorizontally);
        bottomArrowhead.add(begin);
        Point3d topArrowhead = new Point3d(screenHorizontally);
        topArrowhead.add(end);

        // vertical line
        drawLine(gl, bottomArrowhead, topArrowhead);

        // vertical line arrows
        Vector3d arrowVector = Vector3dUtil.fromTo(bottomArrowhead, topArrowhead);
        arrowVector.normalize();
        arrowVector.scale(arrowHeight);

        Point3d bottomArrowheadRight = new Point3d(bottomArrowhead);
        bottomArrowheadRight.add(arrowVector);
        bottomArrowheadRight.sub(arrowheadBaseWidthVector);

        // bottom arrow
        drawFlatArrowhead(gl, bottomArrowhead, arrowVector, arrowheadBaseWidthVector);

        arrowVector.negate();
        arrowheadBaseWidthVector.negate();
        // top arrow
        drawFlatArrowhead(gl, topArrowhead, arrowVector, arrowheadBaseWidthVector);

        Point3d center = new Point3d(bottomArrowhead);
        center.add(topArrowhead);
        center.scale(0.5);

        drawNumberBox(gl, glu, glut, center, value, viewport);
    }

    private void drawFlatArrowhead(GL2 gl, Point3d arrowheadPoint, Vector3d arrowheadVector, Vector3d arrowheadWidthVector) {
        gl.glBegin(GL.GL_TRIANGLES);

        gl.glVertex3d(arrowheadPoint.x, arrowheadPoint.y, arrowheadPoint.z);
        gl.glVertex3d(//
                arrowheadPoint.x + arrowheadVector.x + arrowheadWidthVector.x,//
                arrowheadPoint.y + arrowheadVector.y + arrowheadWidthVector.y,//
                arrowheadPoint.z + arrowheadVector.z + arrowheadWidthVector.z);
        gl.glVertex3d( //
                arrowheadPoint.x + arrowheadVector.x - arrowheadWidthVector.x,//
                arrowheadPoint.y + arrowheadVector.y - arrowheadWidthVector.y,//
                arrowheadPoint.z + arrowheadVector.z - arrowheadWidthVector.z);

        gl.glEnd();
    }

    private void drawLine(GL2 gl, double beginX, double beginY, double beginZ, double endX, double endY, double endZ) {

        gl.glBegin(GL.GL_LINES);
        gl.glVertex3d(beginX, beginY, beginZ);
        gl.glVertex3d(endX, endY, endZ);
        gl.glEnd();
    }

    private void drawLine(GL2 gl, Point3d begin, Point3d end) {

        gl.glBegin(GL.GL_LINES);
        gl.glVertex3d(begin.x, begin.y, begin.z);
        gl.glVertex3d(end.x, end.y, end.z);
        gl.glEnd();
    }

    private void drawNumberBox(GL2 gl, GLU glu, GLUT glut, Point3d point, Double value, Viewport viewport) {

        gl.glDisable(GLLightingFunc.GL_LIGHTING);
        String msg = String.format("%.2f m", (double) value);

        Point2d p = viewport.project(gl, glu, point);
        int fontSize = 18;
        int msgWidth = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_18, msg);

        // Use a bitmap font (since no scaling required)
        // get (x,y) for centering the text on screen
        int x = (int) p.x + 18;
        int y = (int) p.y + fontSize / 2;

        // Switch to 2D viewing
        DrawUtil.begin2D(gl, viewport.getWidth(), viewport.getHeight());

        // Draw a background rectangle
        gl.glColor4f(1f, 1f, 1f, 0.6f);
        gl.glBegin(GL2GL3.GL_QUADS);
        int border = 7;
        gl.glVertex3i(x - border, y + border, 0);
        gl.glVertex3i(x + msgWidth + border, y + border, 0);
        gl.glVertex3i(x + msgWidth + border, y - fontSize - border, 0);
        gl.glVertex3i(x - border, y - fontSize - border, 0);
        gl.glEnd();
        // Write the message in the center of the screen
        gl.glColor3f(0.1f, 0.1f, 0.1f);

        gl.glRasterPos2i(x, y - 2);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, msg);
        // Switch back to 3D viewing
        DrawUtil.end2D(gl);
        gl.glEnable(GLLightingFunc.GL_LIGHTING);
    }
}
