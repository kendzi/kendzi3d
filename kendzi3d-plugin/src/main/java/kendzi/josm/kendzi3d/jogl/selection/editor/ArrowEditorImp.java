/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.selection.editor;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.math.geometry.ray.Ray3d;
import kendzi.math.geometry.ray.Ray3dUtil;

/**
 * Implementation of simple arrow editor.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class ArrowEditorImp extends AbstractEditor implements ArrowEditor {

    private Point3d point;

    private Vector3d vector;

    private double length;

    /**
     * Con.
     */
    public ArrowEditorImp() {
        super();
    }

    /**
     * Con.
     *
     * @param point origin
     * @param vector direction of arrow
     * @param length length
     * @param selected XXX remove
     */
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

    @Override
    public Double intersect(Ray3d selectionRay) {
        return Ray3dUtil.intersect(selectionRay, getEditorCenter(), getEditorRadius());
    }

    @Override
    public Double intersect(Ray3d selectionRay, double ratio) {
        // this implementation is depended on distance from ray center!

        double camRatio = selectionRay.getPoint().distance(getEditorCenter()) * ratio;
        return Ray3dUtil.intersect(selectionRay, getEditorCenter(), getEditorRadius() * camRatio);
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.selection.editor.ArrowEditor#getEditorOrigin()
     */
    @Override
    public Point3d getEditorOrigin() {
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
    public Point3d getEditorCenter() {
        return this.arrowEnd();
    }
}
