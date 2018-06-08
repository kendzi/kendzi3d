package kendzi.josm.kendzi3d.jogl.camera;

import org.openstreetmap.josm.data.preferences.DoubleProperty;

import kendzi.jogl.camera.Viewport;

public class Kendzi3dViewport extends Viewport {

    /**
     * View angle of camera (fovy).
     */
    private final DoubleProperty perspViewAngleMin = new DoubleProperty("kendzi3d.viewport.angle.min", 5);
    private final DoubleProperty perspViewAngleMax = new DoubleProperty("kendzi3d.viewport.angle.max", 110);
    private final DoubleProperty perspViewAngle = new DoubleProperty("kendzi3d.viewport.angle", 45);

    /**
     * The distance from the viewer to the far clipping plane (zFar).
     *
     * The precision of the depth buffer is dependent on r = zFar / zNear,
     * roughly log(2) r bits are lost in precision.
     */
    private final DoubleProperty perspFarClippingMin = new DoubleProperty("kendzi3d.viewport.far.clipping.min", 1.5E+1d);
    private final DoubleProperty perspFarClippingMax = new DoubleProperty("kendzi3d.viewport.far.clipping.max", 1.5E+6d);
    private final DoubleProperty perspFarClipping = new DoubleProperty("kendzi3d.viewport.far.clipping", 1.5E+3d);

    @Override
    public double getFovy() {
        return perspViewAngle.get();
    }

    @Override
    public double getFovyDefault() {
        return perspViewAngle.getDefaultValue();
    }

    @Override
    public void setFovy(double fov) {
        if (fov > perspViewAngleMax.get()) {
            fov = perspViewAngleMax.get();
        } else if (fov < perspViewAngleMin.get()) {
            fov = perspViewAngleMin.get();
        }
        perspViewAngle.put(fov);
    }

    @Override
    public void resetFovy() {
        perspViewAngle.put(perspViewAngle.getDefaultValue());
    }

    @Override
    public double getZFar() {
        return perspFarClipping.get();
    }

    @Override
    public double getZFarDefault() {
        return perspFarClipping.getDefaultValue();
    }

    @Override
    public void setZFar(double zFar) {
        if (zFar > perspFarClippingMax.get()) {
            zFar = perspFarClippingMax.get();
        } else if (zFar < perspFarClippingMin.get()) {
            zFar = perspFarClippingMin.get();
        }
        perspFarClipping.put(zFar);
    }

    @Override
    public void resetZFar() {
        perspFarClipping.put(perspFarClipping.getDefaultValue());
    }
}
