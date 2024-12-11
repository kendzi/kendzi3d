package kendzi.kendzi3d.editor.selection.editor;

import org.joml.Vector3d;

/**
 * Simple wrapper for Point3d class. Point3d don't have setters and getters so
 * we need simple proxy to control values before they are read.
 */
public class Point3dProvider {

    private final Vector3d point;

    /**
     * Constructor.
     * 
     * @param point
     *            provided point
     */
    public Point3dProvider(Vector3d point) {
        this.point = point;
    }

    /**
     * When value of point is provided
     * 
     * @return point
     */
    public Vector3d provide() {
        return point;
    }
}