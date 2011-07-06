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

import kendzi.math.geometry.line.LineParametric2d;

/** Math ray. Defined by point and vector.
 * @author kendzi
 *
 */
public class Ray2d extends LineParametric2d {
    public Ray2d(Point2d pA, Vector2d pU) {
        super(pA, pU);
        // TODO Auto-generated constructor stub
    }



//    public Ray2d(Point2d p2, Vector2d pV) {
//        A = p2;
//        U = pV;
//    }



}
