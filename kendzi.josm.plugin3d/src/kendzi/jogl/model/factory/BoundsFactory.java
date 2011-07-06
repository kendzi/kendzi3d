/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.factory;

import javax.vecmath.Point3d;

import kendzi.jogl.model.geometry.Bounds;

public class BoundsFactory {
    Bounds bounds;

    public BoundsFactory() {
        this.bounds = new Bounds();
        this.bounds.min = new Point3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        this.bounds.max = new Point3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
    }

    public void addPoint(Point3d p) {
        if (this.bounds.min.x > p.x) {
            this.bounds.min.x = p.x;
        }
        if (this.bounds.min.y > p.y) {
            this.bounds.min.y = p.y;
        }
        if (this.bounds.min.z > p.z) {
            this.bounds.min.z = p.z;
        }
        if (this.bounds.max.x < p.x) {
            this.bounds.max.x = p.x;
        }
        if (this.bounds.max.y < p.y) {
            this.bounds.max.y = p.y;
        }
        if (this.bounds.max.z < p.z) {
            this.bounds.max.z = p.z;
        }
    }

    public Bounds toBounds() {

        this.bounds.center = new Point3d(
                (this.bounds.max.x + this.bounds.max.x) / 2d,
                (this.bounds.max.y + this.bounds.max.y) / 2d,
                (this.bounds.max.z + this.bounds.max.z) / 2d);

        double dx = this.bounds.max.x - this.bounds.min.x;
        double dy = this.bounds.max.y - this.bounds.min.y;
        double dz = this.bounds.max.z - this.bounds.min.z;
        this.bounds.radius = 0.5d * Math.sqrt(dx * dx + dy * dy + dz * dz);

        return this.bounds;

    }
}
