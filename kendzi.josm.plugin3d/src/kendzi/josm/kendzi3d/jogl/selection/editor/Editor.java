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

/**
 * Interface for editable features.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public interface Editor {
    public static final float SELECTION_ETITOR_RADIUS = 2f;
    public static final double SELECTION_ETITOR_CAMERA_RATIO = 0.03d;

    /**
     * Get editor active zone location.
     *
     * @return editor active zone location
     */
    public Point3d getEditorCenter();

    /**
     * Get editor active zone radius.
     *
     * @return editor active zone radius
     */
    public double getEditorRadius();
}
