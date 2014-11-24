/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.editor.selection.editor;

import javax.vecmath.Point3d;

/**
 * Interface for editable features.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public interface Editor {
    public static final float SELECTION_ETITOR_RADIUS = 2f;
    public static final double SELECTION_ETITOR_CAMERA_RATIO = 0.02d;

    /**
     * Gets center location of editor active zone.
     *
     * @return editor active zone location
     */
    public Point3d getActiveSpot();

    /**
     * Gets center location of editor active zone. Location of active spot may
     * depends on distance from camera.
     * 
     * @param camera
     *            camera location
     *
     * @return editor active zone location
     */
    public Point3d getActiveSpot(Point3d camera);

    /**
     * Gets radius of editor active zone.
     *
     * @return editor active zone radius
     */
    public double getEditorRadius();
}
