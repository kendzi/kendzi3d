package kendzi.jogl.camera;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class CameraTest implements Camera {
    private Point3d point;
    private Vector3d vector;

    public CameraTest(Point3d point, Vector3d vector) {
        super();
        this.point = point;
        this.vector = vector;
    }

    @Override
    public Point3d getPoint() {
        return point;
    }

    @Override
    public Vector3d getAngle() {
        return vector;
    }
}
