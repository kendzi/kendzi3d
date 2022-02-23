package kendzi.josm.kendzi3d.jogl.model.building;

import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.util.DrawUtil;
import kendzi.kendzi3d.buildings.output.RoofDebugOutput;
import org.lwjgl.opengl.GL11;

/**
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class BuildingDebugDrawer {

    /**
     * XXX Font for axis.
     */
    private final Font font = new Font("SansSerif", Font.BOLD, 24);

    /**
     * XXX For axis labels.
     */
    private final TextRenderer axisLabelRenderer = new TextRenderer(this.font);

    /**
     * XXX For the axis labels.
     */
    private static final float SCALE_FACTOR = 0.01f;

    protected RoofDebugOutput debug;
    private List<Point3d> scaledBBox;

    /**
     * @param debug
     */
    public BuildingDebugDrawer(RoofDebugOutput debug) {
        this.debug = debug;
    }

    /**
     */
    public void drawDebugRoof() {

        GL11.glDisable(GL11.GL_LIGHTING);

        // red
        GL11.glColor3f(1.0f, 0f, 0f);

        // Set line width to 4
        GL11.glLineWidth(6);
        // Repeat count, repeat pattern
        GL11.glLineStipple(1, (short) 0xf0f0);

        GL11.glBegin(GL11.GL_LINE_LOOP);

        List<Point3d> rectangle = scaledBBox();

        for (Point3d point3d : rectangle) {

            GL11.glVertex3d(point3d.x, point3d.y, point3d.z);

        }
        GL11.glEnd();

        for (int i = 0; i < rectangle.size(); i++) {
            Point3d point3d = rectangle.get(i);
            drawAxisText(("rec point " + (i + 1)), point3d.x, point3d.y, point3d.z);
        }
        //
        // Point2d point2d = this.firstPoint;

        float[] rgba = new float[4];
        // green
        GL11.glColor3fv(Color.RED.darker().getRGBComponents(rgba));

        if (this.debug != null && this.debug.getStartPoint() != null) {

            double x = this.debug.getStartPoint().x;
            double y = this.debug.getStartPoint().y;
            double z = this.debug.getStartPoint().z;
            double d = 0.25;

            GL11.glPushMatrix();

            GL11.glTranslated(x, y, z);

            DrawUtil.drawDotY(d, 12);

            GL11.glPopMatrix();
        }

    }

    private List<Point3d> scaledBBox() {
        if (this.scaledBBox == null) {
            this.scaledBBox = scalePolygon3d(this.debug.getBbox(), 0.1d);
        }
        return this.scaledBBox;
    }

    /**
     * Scale polygon described by list of points. Scale from polygon "middle" point.
     * 
     * @param points
     *            polygon
     * @param scale
     *            scale
     * @return scaled polygon
     */
    public static List<Point3d> scalePolygon3d(List<Point3d> points, double scale) {

        if (points == null) {
            return null;
        }

        List<Point3d> ret = new ArrayList<>();
        double middleX = 0;
        double middleY = 0;
        double middleZ = 0;

        for (Point3d p : points) {
            middleX = middleX + p.x;
            middleY = middleY + p.y;
            middleZ = middleZ + p.z;
        }

        Point3d middle = new Point3d(middleX / points.size(), middleY / points.size(), middleZ / points.size());

        for (Point3d p : points) {
            Vector3d v = new Vector3d(p);
            v.sub(middle);
            v.normalize();
            v.scale(scale);

            Point3d bigger = new Point3d(p);
            bigger.add(v);
            ret.add(bigger);
        }

        return ret;
    }

    /**
     * Draw txt at (x,y,z), with the text centered in the x-direction, facing along
     * the +z axis.
     * 
     * @param txt
     * @param x
     * @param y
     * @param z
     */
    private void drawAxisText(String txt, double x, double y, double z) {

        Rectangle2D dim = this.axisLabelRenderer.getBounds(txt);
        float width = (float) dim.getWidth() * SCALE_FACTOR;

        this.axisLabelRenderer.begin3DRendering();
        this.axisLabelRenderer.draw3D(txt, (float) x - width / 2, (float) y, (float) z, SCALE_FACTOR);
        this.axisLabelRenderer.end3DRendering();
    }
}
