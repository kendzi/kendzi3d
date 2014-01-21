package kendzi.josm.kendzi3d.jogl.compas;

import java.awt.Color;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.josm.kendzi3d.jogl.selection.draw.SelectionDrawUtil;

public class Compass {

    private GLUquadric quadratic;   // Storage For Our Quadratic Objects
    private GLU glu = new GLU();

    public void init(GL2 gl) {
        this.quadratic = this.glu.gluNewQuadric();
        this.glu.gluQuadricNormals(this.quadratic, GLU.GLU_SMOOTH); // Create Smooth Normals
    }

    public void draw(GL2 gl, Point3d p, Vector3d v) {

//        gl.glDisable(GL2.GL_DEPTH_TEST);
//        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
//        gl.glLoadIdentity();
//
//        glu.gluLookAt(0, 0, 0,
//                0, 0, 10,
//                0, 1, 0);
//
//
//        gl.glTranslated(1, 0, 5);

        gl.glTranslated(p.x, p.y, p.z);

        double camDistanceRatio = 0.07d;
        int section = 8;

        double lenght = 2d * camDistanceRatio;
        double arrowLenght = 0.7d * camDistanceRatio;
        double baseRadius = 0.05d * camDistanceRatio;
        double arrowRadius = 0.2d * camDistanceRatio;

        float[] compArray = new float[4];

        gl.glColor3fv(Color.BLUE.darker().darker().darker().getRGBComponents(compArray), 0);


        SelectionDrawUtil.drawArrow(gl, glu, this.quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        gl.glRotated(-90d, 0d, 0d, 1d);
        gl.glColor3fv(Color.GREEN.darker().darker().darker().getRGBComponents(compArray), 0);
        SelectionDrawUtil.drawArrow(gl, glu, this.quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);

        gl.glRotated(90d, 1d, 0d, 0d);
        gl.glColor3fv(Color.RED.darker().darker().darker().getRGBComponents(compArray), 0);
        SelectionDrawUtil.drawArrow(gl, glu, this.quadratic, lenght, arrowLenght, baseRadius, arrowRadius, section);



//        pGlu.gluLookAt(position.x, position.y, position.z,
//                lookAt.x, lookAt.y, lookAt.z,
//                lookUp.x, lookUp.y, lookUp.z);



        gl.glPopMatrix();
//        gl.glEnable(GL2.GL_DEPTH_TEST);

    }
}
