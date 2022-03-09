/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.editor.selection.editor;

import kendzi.kendzi3d.editor.selection.event.EditorChangeEvent;
import kendzi.math.geometry.ray.Ray3d;
import org.joml.Vector3dc;

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
    Double intersect(Ray3d selectionRay);

    /**
     * Get editor origin.
     *
     * @return editor origin
     */
    Vector3dc getEditorOrigin();

    /**
     * Arrow direction.
     *
     * @return the vector
     */
    Vector3dc getVector();

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
    Vector3dc arrowEnd();

    // XXX
    EditorChangeEvent move(Ray3d moveRay, boolean finish);

    /**
     * Type of editor active spot.
     * 
     * @return the editorType type of editor
     */
    EditorType getEditorType();
}
