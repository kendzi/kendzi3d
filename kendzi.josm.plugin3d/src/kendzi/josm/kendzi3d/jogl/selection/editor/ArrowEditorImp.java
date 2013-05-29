package kendzi.josm.kendzi3d.jogl.selection.editor;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class ArrowEditorImp implements ArrowEditor {

    private Point3d point;

    private Vector3d vector;

    private double length;

    private boolean selected;



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
    public void select(boolean selected) {
        this.selected = selected;
    }

    @Deprecated
    public boolean isSelect() {
        return this.selected;
    }

    @Override
    public double getValue() {
        return this.length;
    }
}
