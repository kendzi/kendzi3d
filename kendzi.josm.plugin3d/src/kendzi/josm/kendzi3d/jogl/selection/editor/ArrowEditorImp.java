package kendzi.josm.kendzi3d.jogl.selection.editor;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class ArrowEditorImp extends AbstractEditor implements ArrowEditor {

    private Point3d point;

    private Vector3d vector;

    private double length;


    public ArrowEditorImp() {
        super();
    }

    public ArrowEditorImp(Point3d point, Vector3d vector, double length, boolean selected) {
        super();
        this.point = point;
        this.vector = vector;
        this.length = length;
    }


    @Override
    public Point3d arrowEnd() {
        return new Point3d (
                this.point.x + this.vector.x * this.length,
                this.point.y + this.vector.y * this.length,
                this.point.z + this.vector.z * this.length
                );
    }


    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.selection.editor.ArrowEditor#getPoint()
     */
    @Override
    public Point3d getPoint() {
        return this.point;
    }
    /**
     * @param point the point to set
     */
    public void setPoint(Point3d point) {
        this.point = point;
    }
    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.selection.editor.ArrowEditor#getVector()
     */
    @Override
    public Vector3d getVector() {
        return this.vector;
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
    @Override
    public double getLength() {
        return this.length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public double getValue() {
        return this.length;
    }

    @Override
    public Point3d getEditorCenter() {
        return this.arrowEnd();
    }
}
