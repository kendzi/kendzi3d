package kendzi.josm.kendzi3d.jogl.camera;

import org.openstreetmap.josm.data.preferences.DoubleProperty;

import kendzi.jogl.camera.Viewport;

public class Kendzi3dViewport extends Viewport {

    /**
     * View angle of camera (fovy).
     */
    public final DoubleProperty PERSP_VIEW_ANGLE_MIN = new DoubleProperty("kendzi3d.viewport.angle.min", 5);
    public final DoubleProperty PERSP_VIEW_ANGLE_MAX = new DoubleProperty("kendzi3d.viewport.angle.max", 110);
    public final DoubleProperty PERSP_VIEW_ANGLE = new DoubleProperty("kendzi3d.viewport.angle", 45);

    /**
     * The distance from the viewer to the far clipping plane (zFar).
     *
     * The precision of the depth buffer is dependent on r = zFar / zNear,
     * roughly log(2) r bits are lost in precision.
     */
    public final DoubleProperty PERSP_FAR_CLIPPING_PLANE_DISTANCE_MIN = new DoubleProperty("kendzi3d.viewport.far.clipping.min",
            1.5E+1d);
    public final DoubleProperty PERSP_FAR_CLIPPING_PLANE_DISTANCE_MAX = new DoubleProperty("kendzi3d.viewport.far.clipping.max",
            1.5E+6d);
    public final DoubleProperty PERSP_FAR_CLIPPING_PLANE_DISTANCE = new DoubleProperty("kendzi3d.viewport.far.clipping", 1.5E+3d);

    @Override
    public double getFovy() {
        return PERSP_VIEW_ANGLE.get();
    }

    @Override
    public double getZFar() {
        return PERSP_FAR_CLIPPING_PLANE_DISTANCE.get();
    }

    /**
     * Sets the field of view angle, in degrees, in the y direction.
     */
    public void setFovy(double fovy) {
        if (fovy > PERSP_VIEW_ANGLE_MAX.get()) {
            fovy = PERSP_VIEW_ANGLE_MAX.get();
        } else if (fovy < PERSP_VIEW_ANGLE_MIN.get()) {
            fovy = PERSP_VIEW_ANGLE_MIN.get();
        }
        PERSP_VIEW_ANGLE.put(fovy);
    }

    /**
     * Sets the distance from the viewer to the far clipping plane.
     */
    public void setZFar(double zFar) {
        if (zFar > PERSP_FAR_CLIPPING_PLANE_DISTANCE_MAX.get()) {
            zFar = PERSP_FAR_CLIPPING_PLANE_DISTANCE_MAX.get();
        } else if (zFar < PERSP_FAR_CLIPPING_PLANE_DISTANCE_MIN.get()) {
            zFar = PERSP_FAR_CLIPPING_PLANE_DISTANCE_MIN.get();
        }
        PERSP_FAR_CLIPPING_PLANE_DISTANCE.put(zFar);
    }
}
