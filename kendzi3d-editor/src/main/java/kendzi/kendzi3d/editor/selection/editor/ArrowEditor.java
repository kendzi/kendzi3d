/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.editor.selection.editor;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.kendzi3d.editor.selection.event.EditorChangeEvent;
import kendzi.math.geometry.ray.Ray3d;

/**
 * Arrow like editor.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public interface ArrowEditor extends Editor {

    /**
     * Check if selection ray intersect with active zone of editor. The size of
     * active zone may depends on distance from camera or selection source. *
     *
     * @param selectionRay
     *            ray
     *
     * @return <code>null</code> if no intersection, otherwise distance from
     *         selection ray source to editor active spot
     */
    public Double intersect(Ray3d selectionRay);

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

    // XXX
    public EditorChangeEvent move(Ray3d moveRay, boolean finish);

    /**
     * Type of editor active spot.
     * 
     * @return the editorType type of editor
     */
    public EditorType getEditorType();
}
