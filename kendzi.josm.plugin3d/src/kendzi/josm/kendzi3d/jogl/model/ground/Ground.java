/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.ground;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;

import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;

public class Ground {

    private Perspective3D pers;

//    Ground(Perspective3D pers) {
//        this.pers = pers;
//    }

    public void init() {

    }

    public void draw(GL2 gl , Camera camera, Perspective3D perspective3d ) {
        gl.glDisable(GL2.GL_LIGHTING);

        gl.glColor3f((float) 188/255, (float)169/255, (float)169/255);

        gl.glBegin(GL2.GL_POLYGON);
        gl.glColor3f((float)123/256, (float)111/256, (float)100/255);

        double groundSize = 1500.0;

        Point3d c = camera.getPoint();
        gl.glVertex3d(c.x + groundSize, -0.01, c.z + groundSize);
        gl.glVertex3d(c.x - groundSize, -0.01, c.z + groundSize);
        gl.glVertex3d(c.x - groundSize, -0.01, c.z - groundSize);
        gl.glVertex3d(c.x + groundSize, -0.01, c.z - groundSize);

        gl.glEnd();

    }

}
