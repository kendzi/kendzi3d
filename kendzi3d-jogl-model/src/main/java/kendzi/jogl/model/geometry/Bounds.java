/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry;

import javax.vecmath.Point3d;

/**
 * Bounds of model. Minimal border, maximal border, radius and center.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class Bounds {
    /**
     * Minimal point of bounds.
     */
    public Point3d min;

    /**
     * Maximal point of bounds.
     */
    public Point3d max;

    /**
     * Radius of bounds.
     */
    public double radius;
    /**
     * Center of bounds.
     */
    public Point3d center;
    /**
     * @return the min
     */
    public Point3d getMin() {
        return min;
    }
    /**
     * @param min the min to set
     */
    public void setMin(Point3d min) {
        this.min = min;
    }
    /**
     * @return the max
     */
    public Point3d getMax() {
        return max;
    }
    /**
     * @param max the max to set
     */
    public void setMax(Point3d max) {
        this.max = max;
    }
    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }
    /**
     * @param radius the radius to set
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }
    /**
     * @return the center
     */
    public Point3d getCenter() {
        return center;
    }
    /**
     * @param center the center to set
     */
    public void setCenter(Point3d center) {
        this.center = center;
    }


}
