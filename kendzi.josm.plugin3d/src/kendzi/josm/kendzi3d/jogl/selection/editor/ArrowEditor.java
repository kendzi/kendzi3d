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

/**
 * Arrow like editor.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public interface ArrowEditor extends Editor {

    /**
     * Check if selection ray intersect with active zone of editor.
     *
     * @param selectionRay ray
     * @return intersection distance from editor origin
     */
    public Double intersect(Ray3d selectionRay);

    /**
     * Check if selection ray intersect with active zone of editor. This method
     * change size of active zone. Size is depend on distance from ray center
     * and editor location.
     *
     * XXX should this method be extracted to new interface like
     * ChangeSizeArrowEditor?
     *
     * @param selectionRay ray
     * @param distanceRatio ratio value
     * @return intersection distance from editor origin
     */
    public Double intersect(Ray3d selectionRay, double distanceRatio);

    /**
     * Get editor origin.
     *
     * @return editor origin
     */
    public Point3d getEditorOrigin();

    /**
     * Arrow direction.
     *
     * @return the vector
     */
    Vector3d getVector();

    /**
     * Arrow length.
     *
     * @return arrow length
     */
    double getLength();

    /**
     * End arrow point.
     *
     * @return point from arrow end.
     */
    public Point3d arrowEnd();
}
