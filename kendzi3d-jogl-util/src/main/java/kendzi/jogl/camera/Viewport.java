package kendzi.jogl.camera;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

import kendzi.math.geometry.point.PointUtil;
import kendzi.math.geometry.ray.Ray3d;

/**
 * View port for opengl. Configuration of perspective to convert from 3d space
 * into 2d. Conversion of 2d coordinates into rays in 3d space.
 */
public class Viewport implements ViewportPicker {

    /**
     * View angle of camera (fovy).
     */
    private static final double PERSP_VIEW_ANGLE_DEFAULT = 45;
    private double perspViewAngle = PERSP_VIEW_ANGLE_DEFAULT;

    /**
     * The distance from the viewer to the near clipping plane (zNear).
     */
    private static final double PERSP_NEAR_CLIPPING_PLANE_DISTANCE_DEFAULT = 1d;
    private double perspNearClippingPlaneDistance = PERSP_NEAR_CLIPPING_PLANE_DISTANCE_DEFAULT;

    /**
     * The distance from the viewer to the far clipping plane (zFar).
     */
    private static final double PERSP_FAR_CLIPPING_PLANE_DISTANCE_DEFAULT = 1500d;
    private double perspFarClippingPlaneDistance = PERSP_FAR_CLIPPING_PLANE_DISTANCE_DEFAULT;

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
    private transient Point3d position;

    /**
     * Look at vector.
     */
    private transient Vector3d lookAt;

    /**
     * Look up vector.
     */
    private transient Vector3d lookUp;

    /**
     * Look direction.
     */
    private transient Vector3d view;

    private transient Vector3d screenHorizontally = new Vector3d();

    private transient Vector3d screenVertically = new Vector3d();

    /**
     * Creates example viewport.
     */
    public Viewport() {
        width = 1;
        height = 1;
        position = new Point3d();
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

        position = new Point3d();
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
     * Update viewport position using current camera position and angle.
     * Calculates parameters of viewport required to back trace click of mouse
     * in 3d space.
     *
     * @param camera
     *            camera position and angle
     */
    public void updateViewport(Camera camera) {
        Point3d position = camera.getPoint();

        Vector3d lookAt = new Vector3d(100, 0, 0);
        Vector3d lookUp = new Vector3d(0, 1, 0);

        Vector3d rotate = camera.getAngle();

        lookAt = PointUtil.rotateZ3d(lookAt, rotate.z);
        lookAt = PointUtil.rotateY3d(lookAt, rotate.y);
        // posLookAt = PointUtil.rotateX3d(posLookAt, rotate.x);

        lookUp = PointUtil.rotateZ3d(lookUp, rotate.z);
        lookUp = PointUtil.rotateY3d(lookUp, rotate.y);

        lookAt.add(position);

        Vector3d view = new Vector3d();
        Vector3d screenHorizontally = new Vector3d();
        Vector3d screenVertically = new Vector3d();

        // look direction
        view.sub(lookAt, position);
        view.normalize();

        // screenX
        screenHorizontally.cross(view, lookUp);
        screenHorizontally.normalize();

        // screenY
        screenVertically.cross(screenHorizontally, view);
        screenVertically.normalize();

        final float radians = (float) (getFovy() * Math.PI / 180f);
        float halfHeight = (float) (Math.tan(radians / 2) * getZNear());
        float halfScaledAspectRatio = (float) (halfHeight * viewportAspectRatio());

        screenVertically.scale(halfHeight);
        screenHorizontally.scale(halfScaledAspectRatio);

        this.position.set(position);
        this.lookAt = lookAt;
        this.lookUp = lookUp;
        this.view = view;
        this.screenHorizontally = screenHorizontally;
        this.screenVertically = screenVertically;

    }

    /**
     * Converts given 2d coordinates on screen view into ray in 3d space.
     * Depends on last camera position and set viewport configuration.
     *
     * @param screenX
     *            screen x coordinate
     * @param screenY
     *            screen y coordinate
     * @return ray in 3d space from camera location and in direction of given
     *         screen coordinates
     */
    @Override
    public Ray3d picking(float screenX, float screenY) {

        double viewportWidth = width;
        double viewportHeight = height;

        Vector3d vector = new Vector3d(position);
        vector.add(view);

        screenX -= (float) viewportWidth / 2f;
        screenY = (float) viewportHeight / 2f - screenY;

        // normalize to 1
        screenX /= (float) viewportWidth / 2f;
        screenY /= (float) viewportHeight / 2f;

        vector.x += screenHorizontally.x * screenX + screenVertically.x * screenY;
        vector.y += screenHorizontally.y * screenX + screenVertically.y * screenY;
        vector.z += screenHorizontally.z * screenX + screenVertically.z * screenY;

        vector.sub(position);

        return new Ray3d(new Point3d(position), vector);
    }

    public Point2d project(GL2 gl, GLU glu, Point3d point) {
        // FIXME
        float[] modelview = new float[16];
        float[] projectionview = new float[16];
        int[] viewportview = new int[4];
        float[] objectPos = new float[4];
        // objectPos.clear();
        // modelview.clear();
        // projectionview.clear();
        // viewportview.clear();
        gl.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, modelview, 0);
        gl.glGetFloatv(GLMatrixFunc.GL_PROJECTION_MATRIX, projectionview, 0);
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewportview, 0);
        glu.gluProject((float) point.x, (float) point.y, (float) point.z, modelview, 0, projectionview, 0, viewportview, 0,
                objectPos, 0);

        Point2d p = new Point2d(objectPos[0], height - objectPos[1]);

        return p;
        // Matrix4f view = new Matrix4f ();
        // Matrix4f projection = new Matrix4f ();
        //
        // float [] buf = new float[16];
        //
        //
        // gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, buf, 0);
        // view = new Matrix4f(buf);
        //
        //
        // gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, buf,0);
        // projection = new Matrix4f(buf);
        //
        //
        // view.mul(projection);
        //
        //
        //
        // Vector3d screen = v * (view * projection);
        // v.x = v.x * 0.5 / v.w + 0.5;
        // v.y = v.y * 0.5 / v.w + 0.5;
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
    public Vector3d getLookAt() {
        return lookAt;
    }

    /**
     * @return the lookUp
     */
    public Vector3d getLookUp() {
        return lookUp;
    }

    /**
     * @return the position
     */
    public Point3d getPosition() {
        return position;
    }

    /**
     * Gets the field of view angle, in degrees, in y direction.
     *
     * @return field of view angle
     */
    public double getFovy() {
        return perspViewAngle;
    }

    /**
     * Gets the default field of view angle, in degrees, in y direction.
     *
     * @return default field of view angle
     */
    public double getFovyDefault() {
        return PERSP_VIEW_ANGLE_DEFAULT;
    }

    /**
     * Correction factor for objects that are sized and drawn in dependence of
     * camera distance to the object.
     *
     * This concerns elements of the 3d gui rendered conditionally into the
     * viewport for user interaction and that are expected to be equal sized
     * after projecting the 3d scene to the 2d canvas, for example mouse drag
     * handles of selected objects:
     *
     * If there are two editable objects in the scene, one closer to the camera
     * than the other, then the 3d gui elements are expected to be equal-sized
     * regardless of which one of these objects is manipulated.
     *
     * Multiplying with this factor should ensure, that these fixed size 3d gui
     * elements maintain their size, even if the viewport is zoomed, that is,
     * even if the perspective view angle is not at its default.
     *
     * @return correction factor for fixed size scene elements that should
     *         maintain size, regardless of the viewport zoom state
     */
    public double getFovyRatio() {
        return getFovy() / getFovyDefault();
    }

    /**
     * Sets the field of view angle, in degrees, in y direction.
     */
    public void setFovy(double fov) {
        if (fov < PERSP_VIEW_ANGLE_DEFAULT / 10) {
            fov = PERSP_VIEW_ANGLE_DEFAULT / 10;
        } else if (fov > Math.min(PERSP_VIEW_ANGLE_DEFAULT * 2, 110)) {
            fov = Math.min(PERSP_VIEW_ANGLE_DEFAULT * 2, 110);
        }
        perspViewAngle = fov;
    }

    /**
     * Resets the field of view angle, in degrees, in y direction.
     */
    public void resetFovy() {
        perspViewAngle = PERSP_VIEW_ANGLE_DEFAULT;
    }

    /**
     * Gets distance from viewer to near clipping plane (always positive).
     *
     * @return distance to near clipping plane
     */
    public double getZNear() {
        return perspNearClippingPlaneDistance;
    }

    /**
     * Gets default distance from viewer to near clipping plane.
     *
     * @return default distance to near clipping plane
     */
    public double getZNearDefault() {
        return PERSP_NEAR_CLIPPING_PLANE_DISTANCE_DEFAULT;
    }

    /**
     * Sets distance from viewer to near clipping plane.
     */
    public void setZNear(double zNear) {
        perspNearClippingPlaneDistance = zNear;
    }

    /**
     * Resets distance from viewer to near clipping plane.
     */
    public void resetZNear() {
        perspNearClippingPlaneDistance = PERSP_NEAR_CLIPPING_PLANE_DISTANCE_DEFAULT;
    }

    /**
     * Gets distance from viewer to far clipping plane (always positive).
     *
     * @return distance to far clipping plane
     */
    public double getZFar() {
        return perspFarClippingPlaneDistance;
    }

    /**
     * Gets default distance from viewer to far clipping plane.
     *
     * @return default distance to far clipping plane
     */
    public double getZFarDefault() {
        return PERSP_FAR_CLIPPING_PLANE_DISTANCE_DEFAULT;
    }

    /**
     * Sets distance from viewer to far clipping plane.
     */
    public void setZFar(double zFar) {
        if (zFar < Math.max(PERSP_FAR_CLIPPING_PLANE_DISTANCE_DEFAULT / 100, 10)) {
            zFar = Math.max(PERSP_FAR_CLIPPING_PLANE_DISTANCE_DEFAULT / 100, 10);
        } else if (zFar > PERSP_FAR_CLIPPING_PLANE_DISTANCE_DEFAULT * 100) {
            zFar = PERSP_FAR_CLIPPING_PLANE_DISTANCE_DEFAULT * 100;
        }
        perspFarClippingPlaneDistance = zFar;
    }

    /**
     * Resets distance from viewer to far clipping plane.
     */
    public void resetZFar() {
        perspFarClippingPlaneDistance = PERSP_FAR_CLIPPING_PLANE_DISTANCE_DEFAULT;
    }

    /**
     * @return the screenHorizontally
     */
    public Vector3d getScreenHorizontally() {
        return screenHorizontally;
    }

    /**
     * @return the screenVertically
     */
    public Vector3d getScreenVertically() {
        return screenVertically;
    }

}