/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.editor.selection;

import java.util.List;

import kendzi.kendzi3d.editor.selection.editor.Editor;
import kendzi.kendzi3d.editor.selection.event.SelectEvent;
import kendzi.math.geometry.ray.Ray3d;
import kendzi.math.geometry.ray.Ray3dUtil;
import org.joml.Vector3dc;

/**
 * Implementation of selectable element in shape of sphere.
 */
public abstract class SphereSelection implements Selection {

    /**
     * Sphere center.
     */
    private final Vector3dc center;

    /**
     * Sphere radius.
     */
    private final double radius;

    /**
     * Constructor.
     * 
     * @param center
     *            sphere center
     * @param radius
     *            sphere radius
     */
    public SphereSelection(Vector3dc center, double radius) {
        super();
        this.center = center;
        this.radius = radius;
    }

    public Vector3dc getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public Double intersect(Ray3d ray) {
        return Ray3dUtil.intersect(ray, getCenter(), getRadius());
    }

    @Override
    public boolean intersectCandidate(Ray3d ray) {
        return intersect(ray) != null;
    }

    @Override
    public List<Editor> getEditors() {
        return null;
    }

    @Override
    public void onSelectEvent(SelectEvent event) {
        //
    }
}
