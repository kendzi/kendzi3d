package kendzi.josm.kendzi3d.jogl.compas;

import java.awt.Color;

import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Viewport;
import kendzi.jogl.util.ColorUtil;
import kendzi.kendzi3d.editor.drawer.ArrowDrawUtil;
import kendzi.math.geometry.ray.Ray3d;

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
    private GLU glu = new GLU();

    /**
     * Initiate compass drawer.
     *
     * @param gl
     *            gl
     */
    public void init(GL2 gl) {
        quadratic = glu.gluNewQuadric();
        // Create Smooth Normals
        glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH);
    }

    /**
     * Draws compass at left bottom corner of viewport.
     *
     * @param gl
     *            gl
     * @param viewport
     *            viewport
     */
    public void drawAtLeftBottom(GL2 gl, Viewport viewport) {
        int distance = 70;

        Ray3d ray3d = viewport.picking(distance, viewport.getHeight() - distance);

        Point3d point = ray3d.getPoint();

        Vector3d vector = ray3d.getVector();
        vector.normalize();
        vector.scale(1.5);

        point.add(vector);

        draw(gl, point);
    }

    /**
     * Draws compass.
     *
     * @param gl
     *            gl
     * @param point
     *            location
     *
     */
    public void draw(GL2 gl, Point3d point) {

        gl.glPushMatrix();
        gl.glDisable(GLLightingFunc.GL_LIGHTING);

        gl.glTranslated(point.x, point.y, point.z);

        double camDistanceRatio = 0.07d;
        int section = 8;

        double lenght = 2d * camDistanceRatio;
        double arrowLenght = 0.7d * camDistanceRatio;
        double baseRadius = 0.05d * camDistanceRatio;
        double arrowRadius = 0.2d * camDistanceRatio;

        gl.glColor3fv(Y_AXIS_COLOR_ARRAY, 0);
        ArrowDrawUtil.drawArrow(gl, glu, quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        gl.glRotated(-90d, 0d, 0d, 1d);
        gl.glColor3fv(X_AXIS_COLOR_ARRAY, 0);
        ArrowDrawUtil.drawArrow(gl, glu, quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        gl.glRotated(90d, 1d, 0d, 0d);
        gl.glColor3fv(Z_AXIS_COLOR_ARRAY, 0);
        ArrowDrawUtil.drawArrow(gl, glu, quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        gl.glPopMatrix();
    }
}
