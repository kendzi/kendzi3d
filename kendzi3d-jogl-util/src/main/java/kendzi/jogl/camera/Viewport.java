package kendzi.jogl.camera;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import kendzi.jogl.glu.GLU;
import kendzi.math.geometry.ray.Ray3d;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * View port for opengl. Configuration of perspective to convert from 3d space
 * into 2d. Conversion of 2d coordinates into rays in 3d space.
 */
public class Viewport implements ViewportPicker {

    /**
     * View angle of camera (fovy).
     */
    public static final double PERSP_VIEW_ANGLE = 45;

    /**
     * The distance from the viewer to the near clipping plane (zNear).
     */
    public static final double PERSP_NEAR_CLIPPING_PLANE_DISTANCE = 1d;

    /**
     * The distance from the viewer to the far clipping plane (zFar).
     */
    public static final double PERSP_FAR_CLIPPING_PLANE_DISTANCE = 1500d;

    /**
     * Width of viewport.
     */
    private int width;

    /**
     * Height of viewport.
     */
    private int height;

    /**
     * Last position of camera.
     */
    private final transient Vector3d position = new Vector3d();

    /**
     * Look at vector.
     */
    private transient final Vector3d lookAt = new Vector3d();

    /**
     * Look up vector.
     */
    private transient final Vector3d lookUp = new Vector3d();

    /**
     * Look direction.
     */
    private transient final Vector3d view = new Vector3d();

    private transient Vector3d screenHorizontally = new Vector3d();

    private transient Vector3d screenVertically = new Vector3d();

    /**
     * Creates example viewport.
     */
    public Viewport() {
        width = 1;
        height = 1;
    }

    /**
     * Creates viewport.
     *
     * @param width
     *            viewport width
     * @param height
     *            viewport height
     */
    public Viewport(int width, int height) {
        super();
        this.width = width;
        this.height = height;
    }

    public void reshape(int width, int height, Camera camera) {
        this.width = width;
        this.height = height;

        updateViewport(camera);
    }

    /**
     * Calculates viewport aspect ratio.
     *
     * @return viewport aspect ratio
     */
    public double viewportAspectRatio() {
        return (double) width / (double) height;
    }

    /**
     * Update viewport position using current camera position and angle. Calculates
     * parameters of viewport required to back trace click of mouse in 3d space.
     *
     * @param camera
     *            camera position and angle
     */
    public void updateViewport(Camera camera) {
        this.position.set(camera.getPoint());
        Vector3dc rotate = camera.getAngle();

        this.lookAt.set(100, 0, 0).rotateZ(rotate.z()).rotateY(rotate.y()).add(this.position);
        this.lookUp.set(0, 1, 0).rotateZ(rotate.z()).rotateY(rotate.y());

        // look direction
        this.lookAt.sub(this.position, this.view).normalize();

        // screenX
        this.view.cross(this.lookUp, this.screenHorizontally).normalize();

        // screenY
        this.screenHorizontally.cross(this.view, this.screenVertically).normalize();

        final float radians = (float) (PERSP_VIEW_ANGLE * Math.PI / 180f);
        float halfHeight = (float) (Math.tan(radians / 2) * PERSP_NEAR_CLIPPING_PLANE_DISTANCE);
        float halfScaledAspectRatio = (float) (halfHeight * viewportAspectRatio());

        this.screenVertically.mul(halfHeight);
        this.screenHorizontally.mul(halfScaledAspectRatio);
    }

    /**
     * Converts given 2d coordinates on screen view into ray in 3d space. Depends on
     * last camera position and set viewport configuration.
     *
     * @param screenX
     *            screen x coordinate
     * @param screenY
     *            screen y coordinate
     * @return ray in 3d space from camera location and in direction of given screen
     *         coordinates
     */
    public Ray3d picking(float screenX, float screenY) {

        double viewportWidth = width;
        double viewportHeight = height;

        Vector3d vector = new Vector3d(position).add(view);

        screenX -= (float) viewportWidth / 2f;
        screenY = (float) viewportHeight / 2f - screenY;

        // normalize to 1
        screenX /= (float) viewportWidth / 2f;
        screenY /= (float) viewportHeight / 2f;

        vector.x += screenHorizontally.x() * screenX + screenVertically.x() * screenY;
        vector.y += screenHorizontally.y() * screenX + screenVertically.y() * screenY;
        vector.z += screenHorizontally.z() * screenX + screenVertically.z() * screenY;

        vector.sub(position);

        return new Ray3d(new Vector3d(position), vector);
    }

    public Vector2dc project(Vector3dc point) {
        // FIXME
        FloatBuffer modelview = BufferUtils.createFloatBuffer(16);
        FloatBuffer projectionview = BufferUtils.createFloatBuffer(16);
        IntBuffer viewportview = BufferUtils.createIntBuffer(4);
        FloatBuffer objectPos = BufferUtils.createFloatBuffer(4);
        // objectPos.clear();
        // modelview.clear();
        // projectionview.clear();
        // viewportview.clear();
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projectionview);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewportview);
        GLU.gluProject((float) point.x(), (float) point.y(), (float) point.z(), modelview, projectionview, viewportview,
                objectPos);

        return new Vector2d(objectPos.get(0), height - objectPos.get(1));
        // The above was p

        // Matrix4f view = new Matrix4f ();
        // Matrix4f projection = new Matrix4f ();
        //
        // float [] buf = new float[16];
        //
        //
        // GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, buf, 0);
        // view = new Matrix4f(buf);
        //
        //
        // GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, buf,0);
        // projection = new Matrix4f(buf);
        //
        //
        // view.mul(projection);
        //
        //
        //
        // Vector3dc screen = v * (view * projection);
        // v.x() = v.x() * 0.5 / v.w + 0.5;
        // v.y() = v.y() * 0.5 / v.w + 0.5;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width
     *            the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the lookAt
     */
    public Vector3dc getLookAt() {
        return lookAt;
    }

    /**
     * @return the lookUp
     */
    public Vector3dc getLookUp() {
        return lookUp;
    }

    /**
     * @return the position
     */
    public Vector3dc getPosition() {
        return position;
    }

    /**
     * Gets the field of view angle, in degrees, in the y direction.
     *
     * @return field of view angle
     */
    public double getFovy() {
        return Viewport.PERSP_VIEW_ANGLE;
    }

    /**
     * Gets the distance from the viewer to the near clipping plane (always
     * positive).
     *
     * @return distance to the near clipping plane
     */
    public double getZNear() {
        return Viewport.PERSP_NEAR_CLIPPING_PLANE_DISTANCE;
    }

    /**
     * Gets the distance from the viewer to the far clipping plane (always
     * positive).
     *
     * @return distance to the far clipping plane
     */
    public double getZFar() {
        return Viewport.PERSP_FAR_CLIPPING_PLANE_DISTANCE;
    }

    /**
     * @return the screenHorizontally
     */
    public Vector3dc getScreenHorizontally() {
        return screenHorizontally;
    }

    /**
     * @return the screenVertically
     */
    public Vector3dc getScreenVertically() {
        return screenVertically;
    }

}
