package kendzi.kendzi3d.editor.selection.editor;

import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * Simple proxy around Point3d class. Store new instance of point. When point is
 * provided allow to implement method beforeProvide which could re-calculate
 * value of point.
 */
public abstract class CachePoint3dProvider extends Point3dProvider {

    /**
     * Constructor.
     */
    public CachePoint3dProvider() {
        super(new Vector3d());
    }

    /**
     * Constructor.
     * 
     * @param point
     *            point which will be cached
     */
    public CachePoint3dProvider(Vector3dc point) {
        super(new Vector3d(point));
    }

    @Override
    public Vector3d provide() {
        Vector3d point = super.provide();
        beforeProvide(point);
        return point;
    }

    /**
     * Before value is provided.
     * 
     * @param cached
     *            local cache of value
     */
    public abstract void beforeProvide(Vector3d cached);
}