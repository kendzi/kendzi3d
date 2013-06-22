package kendzi.josm.kendzi3d.jogl.selection.editor;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public interface ArrowEditor extends Editor {

    /**
     * @return the point
     */
   Point3d getPoint();

    /**
     * @return the vector
     */
    Vector3d getVector();

    double getLength();

    /**
     * @return
     */
    double getValue();

    public Point3d arrowEnd();
}
