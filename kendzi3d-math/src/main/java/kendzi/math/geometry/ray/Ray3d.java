package kendzi.math.geometry.ray;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Ray3d {

    private Point3d point;

    private Vector3d vector;

    public Ray3d() {
        this(new Point3d(), new Vector3d());
    }

    public Ray3d(Point3d point, Vector3d vector) {
        super();
        this.point = point;
        this.vector = vector;
    }

    /**
     * @return the point
     */
    public Point3d getPoint() {
        return point;
    }
    /**
     * @param point the point to set
     */
    public void setPoint(Point3d point) {
        this.point = point;
    }
    /**
     * @return the vector
     */
    public Vector3d getVector() {
        return vector;
    }
    /**
     * @param vector the vector to set
     */
    public void setVector(Vector3d vector) {
        this.vector = vector;
    }


}
