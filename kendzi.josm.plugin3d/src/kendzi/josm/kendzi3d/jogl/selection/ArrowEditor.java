package kendzi.josm.kendzi3d.jogl.selection;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class ArrowEditor implements Editor {

    private Point3d point;

    private Vector3d vector;

    private double length;

    private boolean selected;

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
    /**
     * @return the length
     */
    public double getLength() {
        return length;
    }
    /**
     * @param length the length to set
     */
    public void setLength(double length) {
        this.length = length;
    }
    @Override
    public void select(boolean selected) {
        this.selected = selected;
    }


    public boolean isSelect() {
        return this.selected;
    }


}
