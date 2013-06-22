package kendzi.josm.kendzi3d.jogl.selection.editor;

import kendzi.math.geometry.ray.Ray3d;
import kendzi.math.geometry.ray.Ray3dUtil;

public abstract class AbstractEditor implements Editor {

    @Override
    public Double intersect(Ray3d selectionRay) {
        return Ray3dUtil.intersect(selectionRay, getEditorCenter(), getEditorRadius());
    }

    @Override
    public double getEditorRadius() {
        return SELECTION_ETITOR_RADIUS;
    }
}
