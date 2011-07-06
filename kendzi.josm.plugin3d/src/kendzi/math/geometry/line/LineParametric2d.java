/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.line;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

/**
 * Geometry line in parametric form.
 *
 *  x = x_A + t * u_x;
 *  y = y_A + t * u_y;
 *  where t in R
 *
 *  TODO
 *
 * @see http://pl.wikipedia.org/wiki/Prosta#R.C3.B3wnanie_w_postaci_kierunkowej
 * @see http://en.wikipedia.org/wiki/Linear_equation
 *
 * @author kendzi
 *
 */
public class LineParametric2d {
    public Point2d A;
    public Vector2d U;

    public LineParametric2d(Point2d pA, Vector2d pU) {
        this.A = pA;
        this.U = pU;
    }

    public LineLinear2d getLinearForm() {

        double x = this.A.x;
        double y = this.A.y;

        double B = -this.U.x;
        double A = this.U.y;

        double C = - (A * x + B * y);
        return new LineLinear2d(A, B, C);
    }
}
