/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.factory;

import kendzi.jogl.model.geometry.Bounds;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class BoundsFactory {
    Bounds bounds;

    public BoundsFactory() {
        this.bounds = new Bounds();
        this.bounds.min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        this.bounds.max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
    }

    public void addPoint(Vector3dc p) {
        addPoint(p.x(), p.y(), p.z());
    }

    public void addPoint(double pX, double pY, double pZ) {
        if (this.bounds.min.x() > pX) {
            this.bounds.min.x = pX;
        }
        if (this.bounds.min.y() > pY) {
            this.bounds.min.y = pY;
        }
        if (this.bounds.min.z() > pZ) {
            this.bounds.min.z = pZ;
        }
        if (this.bounds.max.x() < pX) {
            this.bounds.max.x = pX;
        }
        if (this.bounds.max.y() < pY) {
            this.bounds.max.y = pY;
        }
        if (this.bounds.max.z() < pZ) {
            this.bounds.max.z = pZ;
        }
    }

    public Bounds toBounds() {

        this.bounds.center = new Vector3d((this.bounds.max.x() + this.bounds.min.x()) / 2d,
                (this.bounds.max.y() + this.bounds.min.y()) / 2d, (this.bounds.max.z() + this.bounds.min.z()) / 2d);

        double dx = this.bounds.max.x() - this.bounds.min.x();
        double dy = this.bounds.max.y() - this.bounds.min.y();
        double dz = this.bounds.max.z() - this.bounds.min.z();
        this.bounds.radius = 0.5d * Math.sqrt(dx * dx + dy * dy + dz * dz);

        return this.bounds;

    }
}
