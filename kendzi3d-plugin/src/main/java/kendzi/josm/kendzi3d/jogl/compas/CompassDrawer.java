package kendzi.josm.kendzi3d.jogl.compas;

import com.jogamp.opengl.glu.GLUquadric;

import java.awt.*;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Viewport;
import kendzi.jogl.glu.GLU;
import kendzi.jogl.util.ColorUtil;
import kendzi.kendzi3d.editor.drawer.ArrowDrawUtil;
import kendzi.math.geometry.ray.Ray3d;
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
     * Storage For Our Quadratic Objects
     */
    private GLUquadric quadratic;

    /**
     * Initiate compass drawer.
     *
     */
    public void init() {
        quadratic = GLU.gluNewQuadric();
        // Create Smooth Normals
        GLU.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH);
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

        Point3d point = ray3d.getPoint();

        Vector3d vector = ray3d.getVector();
        vector.normalize();
        vector.scale(1.5);

        point.add(vector);

        draw(point);
    }

    /**
     * Draws compass.
     *
     * @param point
     *            location
     *
     */
    public void draw(Point3d point) {

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glTranslated(point.x, point.y, point.z);

        double camDistanceRatio = 0.07d;
        int section = 8;

        double lenght = 2d * camDistanceRatio;
        double arrowLenght = 0.7d * camDistanceRatio;
        double baseRadius = 0.05d * camDistanceRatio;
        double arrowRadius = 0.2d * camDistanceRatio;

        GL11.glColor3fv(Y_AXIS_COLOR_ARRAY);
        ArrowDrawUtil.drawArrow(null, quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        GL11.glRotated(-90d, 0d, 0d, 1d);
        GL11.glColor3fv(X_AXIS_COLOR_ARRAY);
        ArrowDrawUtil.drawArrow(null, quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        GL11.glRotated(90d, 1d, 0d, 0d);
        GL11.glColor3fv(Z_AXIS_COLOR_ARRAY);
        ArrowDrawUtil.drawArrow(null, quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        GL11.glPopMatrix();
    }
}
