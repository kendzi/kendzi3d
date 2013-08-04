/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.selection;

import javax.vecmath.Point3d;

public abstract class BuildingSelection implements Selection {

    Point3d center;
    double radius;

    long wayId;


    public BuildingSelection(long wayId,   Point3d center, double radius) {
        super();
        this.center = center;
        this.radius = radius;
        this.wayId = wayId;
    }

    @Override
    public Point3d getCenter() {
        return this.center;
    }

    @Override
    public double getRadius() {
        return this.radius;
    }

    @Override
    public abstract void select(boolean selected);

    /**
     * @return the wayId
     */
    public long getWayId() {
        return wayId;
    }

    /**
     * @param wayId the wayId to set
     */
    public void setWayId(long wayId) {
        this.wayId = wayId;
    }

    /**
     * @param center the center to set
     */
    public void setCenter(Point3d center) {
        this.center = center;
    }

    /**
     * @param radius the radius to set
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

}
