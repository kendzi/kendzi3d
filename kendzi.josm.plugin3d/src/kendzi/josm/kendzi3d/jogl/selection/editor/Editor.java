package kendzi.josm.kendzi3d.jogl.selection.editor;

import javax.vecmath.Point3d;

import kendzi.math.geometry.ray.Ray3d;

public interface Editor {
    public static final float SELECTION_ETITOR_RADIUS = 2f;

    public Double intersect(Ray3d selectionRay);

    abstract Point3d getEditorCenter();

    abstract double getEditorRadius();
}
