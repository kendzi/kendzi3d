package kendzi.kendzi3d.editor.selection.editor;

import javax.vecmath.Point3d;

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
        super(new Point3d());
    }

    /**
     * Constructor.
     * 
     * @param point
     *            point which will be cached
     */
    public CachePoint3dProvider(Point3d point) {
        super(new Point3d(point));
    }

    @Override
    public Point3d provide() {
        Point3d point = super.provide();
        beforeProvide(point);
        return point;
    }

    /**
     * Before value is provided.
     * 
     * @param cached
     *            local cache of value
     */
    public abstract void beforeProvide(Point3d cached);
}