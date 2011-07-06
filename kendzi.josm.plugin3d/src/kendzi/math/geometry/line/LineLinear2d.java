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
import javax.vecmath.Tuple2d;

/**
 * Geometry line in linear form.
 * General form:
 * Ax + By + C = 0;
 *
 * TODO
 *
 * @see http://pl.wikipedia.org/wiki/Prosta
 * @see http://en.wikipedia.org/wiki/Linear_equation
 *
 * @author kendzi
 *
 */
public class LineLinear2d {
    public double A;
    public double B;
    public double C;


    /** Linear line.
     * @param pA A
     * @param pB B
     * @param pC C
     */
    public LineLinear2d(double pA, double pB, double pC) {
        this.A = pA;
        this.B = pB;
        this.C = pC;
    }

    /** Linear line from two points on line.
     * @param pP1 first point
     * @param pP2 second point
     */
    public LineLinear2d(Point2d pP1, Point2d pP2) {
        this.A = pP1.y - pP2.y;
        this.B = pP2.x - pP1.x;
        this.C = pP1.x * pP2.y - pP2.x * pP1.y;
    }



    /** Collision point of two lines.
     * @param pLine line to collision
     * @return collision point
     */
    public Point2d collide(LineLinear2d pLine) {
        return collide(this, pLine);
    }



    /** Collision point of two lines.
     * @param A1 line 1 in linear form
     * @param B1
     * @param C1
     * @param A2 line 2 in linear form
     * @param B2
     * @param C2
     * @return collision point
     */
    public static Point2d collide(double A1, double B1, double C1, double A2, double B2, double C2) {

        double WAB = A1*B2 - A2*B1;
        double WBC = B1*C2 - B2*C1;
        double WCA = C1*A2 - C2*A1;

        if (WAB == 0) {
            return null;
        }

        return new Point2d(WBC / WAB, WCA / WAB);
    }

    /** Collision point of two lines.
     * @param pLine1 line 1 in linear form
     * @param pLine2 line 2 in linear form
     * @return collision point
     */
    public static Point2d collide(LineLinear2d pLine1, LineLinear2d pLine2) {
        return collide(pLine1.A, pLine1.B, pLine1.C, pLine2.A, pLine2.B, pLine2.C);
    }


    /** Determinate if point is under line.
     * <img src="doc-files/LineLinear2d_point.png">
     * @param pPoint point
     * @return point is under line
     */
    public boolean pointIsUnder(Tuple2d pPoint) {
        return A * pPoint.x + B * pPoint.y + C > 0;
    }

    /** Determinate if point is over line.
     * <img src="doc-files/LineLinear2d_point.png">
     * @param pPoint point
     * @return point is over line
     */
    public boolean pointIsOver(Tuple2d pPoint) {
        return A * pPoint.x + B * pPoint.y + C < 0;
    }

    /** Determinate if point is on line.
     * <img src="doc-files/LineLinear2d_point.png">
     * <img src="doc-files/test.png">
     * @param pPoint point
     * @return point is on line
     */
    public boolean pointIsOn(Tuple2d pPoint) {
        return A * pPoint.x + B * pPoint.y + C == 0;
    }

    /** Determinate if point is over line or on line.
     * <img src="doc-files/LineLinear2d_point.png">
     * @param pPoint point
     * @return point is over line or on line
     */
    public boolean pointInFront(Tuple2d pPoint) {
        return A * pPoint.x + B * pPoint.y + C >= 0;
    }

    /** Determinate if point is under line or on line.
     * <img src="doc-files/LineLinear2d_point.png">
     * @param pPoint point
     * @return point is under line or on line
     */
    public boolean pointInBack(Tuple2d pPoint) {
        return A * pPoint.x + B * pPoint.y + C <= 0;
    }
}
