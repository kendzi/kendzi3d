package kendzi.josm.kendzi3d.jogl.compas;

import java.awt.Color;

import kendzi.jogl.camera.Viewport;
import kendzi.jogl.util.ColorUtil;
import kendzi.kendzi3d.editor.drawer.ArrowDrawUtil;
import kendzi.math.geometry.ray.Ray3d;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.opengl.GL11;

/**
 * Drawer for compass.
 */
public class CompassDrawer {

    /**
     * Color for x axis.
     */
    public static final Color X_AXIS_COLOR = Color.GREEN.darker().darker().darker();

    /**
     * Color for y axis.
     */
    public static final Color Y_AXIS_COLOR = Color.BLUE.darker().darker().darker();

    /**
     * Color for z axis.
     */
    public static final Color Z_AXIS_COLOR = Color.RED.darker().darker().darker();

    /**
     * Color for x axis.
     */
    private static final float[] X_AXIS_COLOR_ARRAY = ColorUtil.colorToArray(X_AXIS_COLOR);

    /**
     * Color for y axis.
     */
    private static final float[] Y_AXIS_COLOR_ARRAY = ColorUtil.colorToArray(Y_AXIS_COLOR);

    /**
     * Color for z axis.
     */
    private static final float[] Z_AXIS_COLOR_ARRAY = ColorUtil.colorToArray(Z_AXIS_COLOR);

    /**
     * Initiate compass drawer.
     *
     */
    public void init() {
        // do nothing -- GLU_SMOOTH is the default normal
    }

    /**
     * Draws compass at left bottom corner of viewport.
     *
     * @param viewport
     *            viewport
     */
    public void drawAtLeftBottom(Viewport viewport) {
        int distance = 70;

        Ray3d ray3d = viewport.picking(distance, viewport.getHeight() - distance);

        Vector3dc vector = ray3d.getVector().normalize(new Vector3d()).mul(1.5);
        Vector3d point = ray3d.getPoint().add(vector, new Vector3d());

        draw(point);
    }

    /**
     * Draws compass.
     *
     * @param point
     *            location
     *
     */
    public void draw(Vector3dc point) {

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glTranslated(point.x(), point.y(), point.z());

        double camDistanceRatio = 0.07d;
        int section = 8;

        double lenght = 2d * camDistanceRatio;
        double arrowLenght = 0.7d * camDistanceRatio;
        double baseRadius = 0.05d * camDistanceRatio;
        double arrowRadius = 0.2d * camDistanceRatio;

        GL11.glColor3fv(Y_AXIS_COLOR_ARRAY);
        ArrowDrawUtil.drawArrow(null, lenght, arrowLenght, baseRadius, arrowRadius, section);

        GL11.glRotated(-90d, 0d, 0d, 1d);
        GL11.glColor3fv(X_AXIS_COLOR_ARRAY);
        ArrowDrawUtil.drawArrow(null, lenght, arrowLenght, baseRadius, arrowRadius, section);

        GL11.glRotated(90d, 1d, 0d, 0d);
        GL11.glColor3fv(Z_AXIS_COLOR_ARRAY);
        ArrowDrawUtil.drawArrow(null, lenght, arrowLenght, baseRadius, arrowRadius, section);

        GL11.glPopMatrix();
    }
}
