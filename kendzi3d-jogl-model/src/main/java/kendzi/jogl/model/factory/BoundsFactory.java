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
        addPoint(p.x, p.y, p.z);
    }

    public void addPoint(double p_x, double p_y, double p_z) {
        if (this.bounds.min.x > p_x) {
            this.bounds.min.x = p_x;
        }
        if (this.bounds.min.y > p_y) {
            this.bounds.min.y = p_y;
        }
        if (this.bounds.min.z > p_z) {
            this.bounds.min.z = p_z;
        }
        if (this.bounds.max.x < p_x) {
            this.bounds.max.x = p_x;
        }
        if (this.bounds.max.y < p_y) {
            this.bounds.max.y = p_y;
        }
        if (this.bounds.max.z < p_z) {
            this.bounds.max.z = p_z;
        }
    }

    public Bounds toBounds() {

        this.bounds.center = new Point3d((this.bounds.max.x + this.bounds.min.x) / 2d,
                (this.bounds.max.y + this.bounds.min.y) / 2d, (this.bounds.max.z + this.bounds.min.z) / 2d);

        double dx = this.bounds.max.x - this.bounds.min.x;
        double dy = this.bounds.max.y - this.bounds.min.y;
        double dz = this.bounds.max.z - this.bounds.min.z;
        this.bounds.radius = 0.5d * Math.sqrt(dx * dx + dy * dy + dz * dz);

        return this.bounds;

    }
}
