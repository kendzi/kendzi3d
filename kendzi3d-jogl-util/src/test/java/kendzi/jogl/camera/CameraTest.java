package kendzi.jogl.camera;

import org.joml.Vector3dc;

public class CameraTest implements Camera {
    private final Vector3dc point;
    private final Vector3dc vector;

    public CameraTest(Vector3dc point, Vector3dc vector) {
        super();
        this.point = point;
        this.vector = vector;
    }

    @Override
    public Vector3dc getPoint() {
        return point;
    }

    @Override
    public Vector3dc getAngle() {
        return vector;
    }
}
