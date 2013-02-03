package kendzi.josm.kendzi3d.jogl.selection;

import java.util.List;

import javax.vecmath.Point3d;

public interface Selection {
    Point3d getCenter();
    double getRadius();

    void select(boolean selected);

    List<Editor> getEditors();
}
