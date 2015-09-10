package kendzi.jogl.camera;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class ViewportUtil {

    /**
     * Setup camera position and direction.
     *
     * @param gl
     *            gl
     * @param viewport
     *            viewport with camera position
     */
    public static void lookAt(GL2 gl, Viewport viewport) {

        // Activate and reset model view matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        Point3d position = viewport.getPosition();
        Vector3d lookAt = viewport.getLookAt();
        Vector3d lookUp = viewport.getLookUp();

        // sets camera position and direction
        new GLU().gluLookAt(position.x, position.y, position.z, lookAt.x, lookAt.y, lookAt.z, lookUp.x, lookUp.y, lookUp.z);
    }

    /**
     * Setup openGl view perspective.
     *
     * @param viewport
     *            viewport with perspective configuration
     * @param gl
     *            gl
     */
    public static void reshapePerspective(Viewport viewport, GL2 gl) {

        // size of drawing area
        gl.glViewport(0, 0, viewport.getWidth(), viewport.getHeight());

        // activate projection matrix
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);

        // load identity as projection
        gl.glLoadIdentity();

        // setup projection perspective
        new GLU().gluPerspective(viewport.getFovy(), viewport.viewportAspectRatio(), viewport.getZNear(), viewport.getZFar());
    }
}
