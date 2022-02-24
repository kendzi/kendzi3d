package kendzi.kendzi3d.editor.drawer;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Viewport;
import kendzi.jogl.glu.GLUT;
import kendzi.jogl.util.DrawUtil;
import kendzi.math.geometry.point.Vector3dUtil;
import org.lwjgl.opengl.GL11;

/**
 * Drawer for measure tap.
 */
public class MeasureDrawer {
    /**
     * Draws measure tap with distance arrows and distance value. Measure tap is
     * draw in Y direction always rotated to camera. XXX add measure begin point
     *
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
    public void drawYMeasureWithArrows(Point3d begin, Point3d end, double value, Viewport viewport, double horizontalDistance,
            double arrowHeight, double arrowWidth, float lineWidth) {

        GL11.glLineWidth(lineWidth);

        Vector3d screenHorizontally = new Vector3d(viewport.getScreenHorizontally());
        screenHorizontally.normalize();

        Vector3d arrowheadBaseWidthVector = new Vector3d(screenHorizontally);
        arrowheadBaseWidthVector.scale(arrowWidth);

        screenHorizontally.scale(horizontalDistance);

        // top horizontal line
        drawLine(end.x, end.y, end.z, //
                end.x + screenHorizontally.x, end.y + screenHorizontally.y, end.z + screenHorizontally.z);

        // bottom horizontal line
        drawLine(begin.x, begin.y, begin.z, //
                begin.x + screenHorizontally.x, begin.y + screenHorizontally.y, begin.z + screenHorizontally.z);

        screenHorizontally.scale(0.5);

        Point3d bottomArrowhead = new Point3d(screenHorizontally);
        bottomArrowhead.add(begin);
        Point3d topArrowhead = new Point3d(screenHorizontally);
        topArrowhead.add(end);

        // vertical line
        drawLine(bottomArrowhead, topArrowhead);

        // vertical line arrows
        Vector3d arrowVector = Vector3dUtil.fromTo(bottomArrowhead, topArrowhead);
        arrowVector.normalize();
        arrowVector.scale(arrowHeight);

        Point3d bottomArrowheadRight = new Point3d(bottomArrowhead);
        bottomArrowheadRight.add(arrowVector);
        bottomArrowheadRight.sub(arrowheadBaseWidthVector);

        // bottom arrow
        drawFlatArrowhead(bottomArrowhead, arrowVector, arrowheadBaseWidthVector);

        arrowVector.negate();
        arrowheadBaseWidthVector.negate();
        // top arrow
        drawFlatArrowhead(topArrowhead, arrowVector, arrowheadBaseWidthVector);

        Point3d center = new Point3d(bottomArrowhead);
        center.add(topArrowhead);
        center.scale(0.5);

        drawNumberBox(center, value, viewport);
    }

    private void drawFlatArrowhead(Point3d arrowheadPoint, Vector3d arrowheadVector, Vector3d arrowheadWidthVector) {
        GL11.glBegin(GL11.GL_TRIANGLES);

        GL11.glVertex3d(arrowheadPoint.x, arrowheadPoint.y, arrowheadPoint.z);
        GL11.glVertex3d(//
                arrowheadPoint.x + arrowheadVector.x + arrowheadWidthVector.x, //
                arrowheadPoint.y + arrowheadVector.y + arrowheadWidthVector.y, //
                arrowheadPoint.z + arrowheadVector.z + arrowheadWidthVector.z);
        GL11.glVertex3d( //
                arrowheadPoint.x + arrowheadVector.x - arrowheadWidthVector.x, //
                arrowheadPoint.y + arrowheadVector.y - arrowheadWidthVector.y, //
                arrowheadPoint.z + arrowheadVector.z - arrowheadWidthVector.z);

        GL11.glEnd();
    }

    private void drawLine(double beginX, double beginY, double beginZ, double endX, double endY, double endZ) {

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(beginX, beginY, beginZ);
        GL11.glVertex3d(endX, endY, endZ);
        GL11.glEnd();
    }

    private void drawLine(Point3d begin, Point3d end) {

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(begin.x, begin.y, begin.z);
        GL11.glVertex3d(end.x, end.y, end.z);
        GL11.glEnd();
    }

    private void drawNumberBox(Point3d point, Double value, Viewport viewport) {

        GL11.glDisable(GL11.GL_LIGHTING);
        String msg = String.format("%.2f m", value);

        Point2d p = viewport.project(null, point);
        int fontSize = 18;
        int msgWidth = GLUT.glutBitmapLength(GLUT.BITMAP_HELVETICA_18, msg);

        // Use a bitmap font (since no scaling required)
        // get (x,y) for centering the text on screen
        int x = (int) p.x + 18;
        int y = (int) p.y + fontSize / 2;

        // Switch to 2D viewing
        DrawUtil.begin2D(viewport.getWidth(), viewport.getHeight());

        // Draw a background rectangle
        GL11.glColor4f(1f, 1f, 1f, 0.6f);
        GL11.glBegin(GL11.GL_QUADS);
        int border = 7;
        GL11.glVertex3i(x - border, y + border, 0);
        GL11.glVertex3i(x + msgWidth + border, y + border, 0);
        GL11.glVertex3i(x + msgWidth + border, y - fontSize - border, 0);
        GL11.glVertex3i(x - border, y - fontSize - border, 0);
        GL11.glEnd();
        // Write the message in the center of the screen
        GL11.glColor3f(0.1f, 0.1f, 0.1f);

        GL11.glRasterPos2i(x, y - 2);
        GLUT.glutBitmapString(GLUT.BITMAP_HELVETICA_18, msg);
        // Switch back to 3D viewing
        DrawUtil.end2D();
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
