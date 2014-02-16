package kendzi.josm.kendzi3d.jogl.compas;

import java.awt.Color;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.josm.kendzi3d.jogl.selection.draw.SelectionDrawUtil;

public class Compass {

    public static final Color Z_AXIS_COLOR = Color.RED.darker().darker().darker();
    public static final Color X_AXIS_COLOR = Color.GREEN.darker().darker().darker();
    public static final Color Y_AXIS_COLOR = Color.BLUE.darker().darker().darker();

    /**
     * Storage For Our Quadratic Objects
     */
    private GLUquadric quadratic;
    private GLU glu = new GLU();

    public void init(GL2 gl) {
        this.quadratic = this.glu.gluNewQuadric();
        // Create Smooth Normals
        this.glu.gluQuadricNormals(this.quadratic, GLU.GLU_SMOOTH);
    }

    public void draw(GL2 gl, Point3d p, Vector3d v) {

        gl.glPushMatrix();

        gl.glTranslated(p.x, p.y, p.z);

        double camDistanceRatio = 0.07d;
        int section = 8;

        double lenght = 2d * camDistanceRatio;
        double arrowLenght = 0.7d * camDistanceRatio;
        double baseRadius = 0.05d * camDistanceRatio;
        double arrowRadius = 0.2d * camDistanceRatio;

        float[] compArray = new float[4];

        gl.glColor3fv(Y_AXIS_COLOR.getRGBComponents(compArray), 0);

        SelectionDrawUtil.drawArrow(gl, glu, this.quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        gl.glRotated(-90d, 0d, 0d, 1d);
        gl.glColor3fv(X_AXIS_COLOR.getRGBComponents(compArray), 0);
        SelectionDrawUtil.drawArrow(gl, glu, this.quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        gl.glRotated(90d, 1d, 0d, 0d);
        gl.glColor3fv(Z_AXIS_COLOR.getRGBComponents(compArray), 0);
        SelectionDrawUtil.drawArrow(gl, glu, this.quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        gl.glPopMatrix();
    }
}
