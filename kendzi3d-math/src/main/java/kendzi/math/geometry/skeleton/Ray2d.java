/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.skeleton;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import kendzi.math.geometry.line.LineLinear2d;
import kendzi.math.geometry.line.LineParametric2d;

/**
 * Math ray. Defined by point and vector.
 * 
 * @author kendzi
 * 
 */
@Deprecated
public class Ray2d extends LineParametric2d {
    public Ray2d(Point2d pA, Vector2d pU) {
        super(pA, pU);
    }



    //    public Ray2d(Point2d p2, Vector2d pV) {
    //        A = p2;
    //        U = pV;
    //    }

    public static Point2d collide(Ray2d ray, LineLinear2d line, double epsilon) {
        // FIXME rewrite?
        Point2d collide = LineLinear2d.collide(ray.getLinearForm(), line);
        if (collide == null) {
            return null;
        }

        // Portably there is better way to do this.
        // this is from graphical .

        Vector2d collideVector = new Vector2d(collide);
        collideVector.sub(ray.A);

        double dot = ray.U.dot(collideVector);

        if (dot < epsilon) {
            return null;
        }

        return collide;

    }



    @Override
    public String toString() {
        return "Ray2d [A=" + A + ", U=" + U + "]";
    }



}
