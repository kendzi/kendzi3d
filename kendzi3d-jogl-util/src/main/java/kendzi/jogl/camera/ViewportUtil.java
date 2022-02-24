package kendzi.jogl.camera;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.glu.GLU;
import org.lwjgl.opengl.GL11;

public class ViewportUtil {

    /**
     * Setup camera position and direction.
     *
     * @param viewport
     *            viewport with camera position
     */
    public static void lookAt(Viewport viewport) {

        // Activate and reset model view matrix.
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        Point3d position = viewport.getPosition();
        Vector3d lookAt = viewport.getLookAt();
        Vector3d lookUp = viewport.getLookUp();

        // sets camera position and direction
        GLU.gluLookAt((float) position.x, (float) position.y, (float) position.z, (float) lookAt.x, (float) lookAt.y,
                (float) lookAt.z, (float) lookUp.x, (float) lookUp.y, (float) lookUp.z);
    }

    /**
     * Setup openGl view perspective.
     *
     * @param viewport
     *            viewport with perspective configuration
     */
    public static void reshapePerspective(Viewport viewport) {

        // size of drawing area
        GL11.glViewport(0, 0, viewport.getWidth(), viewport.getHeight());

        // activate projection matrix
        GL11.glMatrixMode(GL11.GL_PROJECTION);

        // load identity as projection
        GL11.glLoadIdentity();

        // setup projection perspective
        GLU.gluPerspective((float) viewport.getFovy(), (float) viewport.viewportAspectRatio(), (float) viewport.getZNear(),
                (float) viewport.getZFar());
    }
}
