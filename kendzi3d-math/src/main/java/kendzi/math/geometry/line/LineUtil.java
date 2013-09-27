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
import javax.vecmath.Vector2d;

import kendzi.math.geometry.point.Vector2dUtil;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class LineUtil {

    /**
     * Con.
     */
    private LineUtil() {
        //
    }

    /**
     * Determinate if line is crossing Line segment.
     * 
     * @param firstLinePoint first point of line
     * @param secondLinePoint second point of line it have to be different from
     *            first point
     * @param lineSegmentA begin of line segment
     * @param lineSegmentB end of line segment
     * @return if line is crossing line segment
     */
    public static boolean isLineCrossLineSegment(Point2d firstLinePoint, Point2d secondLinePoint, Point2d lineSegmentA,
            Point2d lineSegmentB) {
        // TODO test if begin or end of Line Segment is exactly on line !!!
        // Thanks numerical error for now this should work ;)
        return matrixDet(firstLinePoint, secondLinePoint, lineSegmentA)
                * matrixDet(firstLinePoint, secondLinePoint, lineSegmentB) > 0;

    }

    /**	Det of matrix.
     * @param A first column of matrix
     * @param B second column of matrix
     * @param Z third column of matrix
     * @return det of matrix
     */
    public static double matrixDet(Point2d A, Point2d B, Tuple2d Z) {
        return A.x * B.y + B.x * Z.y + Z.x * A.y - Z.x * B.y - A.x * Z.y - B.x * A.y;
    }

    /**
     * Test if point Z lays on Line Segment |AB|.
     *
     * @param Ax x of point A
     * @param Ay y of point A
     * @param Bx x of point B
     * @param By y of point B
     * @param Zx x of point Z
     * @param Zy y of point Z
     * @return if point Z lays on Line Segment |AB|
     */
    private static boolean pointLiesOnLineSegment(double Ax, double Ay, double Bx, double By, double Zx,
            double Zy) {
        double det; // det of matrix

        det = Ax * By + Bx * Zy + Zx * Ay - Zx * By - Ax * Zy - Bx * Ay;
        if (det != 0) {
            return false;
        } else {
            if (Math.min(Ax, Bx) <= Zx && Zx <= Math.max(Ax, Bx)
                    && Math.min(Ay, By) <= Zy && Zy <= Math.max(Ay, By)) {
                return true;
            } else {
                return false;
            }
        }
    }


    /** TODO
     * @param Ax line first point
     * @param Ay line first point
     * @param Bx line second point
     * @param By line second point
     * @param Cx line segment first point
     * @param Cy line segment first point
     * @param Dx line segment second point
     * @param Dy line segment second point
     * @return Point of crossing line with Line Segment. Or null if they don't cross
     */
    public static Point2d crossLineWithLineSegment(Point2d lA, Point2d lB, Point2d sC, Point2d sD) {
        // Sprawdzanie, czy jakis punkt nalezy do drugiego odcinka
        if (matrixDet(lA, lB, sC) == 0) {
            return new Point2d(sC.x, sC.y);
        } else if (matrixDet(lA, lB, sD) == 0) {
            return new Point2d(sD.x, sD.y);
        } else {
            // if none of Line Segment points lies on the line
            if (matrixDet(lA, lB, sC)
                    * matrixDet(lA, lB, sD) >= 0) {
                // both Line Segment end lies on the same site of Line so they don't crossing
                return null;
            } else {
                // there is crossing
                return lineCrossPoint(lA.x, lA.y, lB.x, lB.y, sC.x, sC.y, sD.x, sD.y);
            }
        }
    }

    /** TODO
     * @param Ax
     * @param Ay
     * @param Bx
     * @param By
     * @param Cx
     * @param Cy
     * @param Dx
     * @param Dy
     * @return
     */
    public static Point2d crossLineSegment(Point2d A, Point2d B, Point2d C, Point2d D) {
        // Sprawdzanie, czy jakis punkt nalezy do drugiego odcinka
        if (pointLiesOnLineSegment(A.x, A.y, B.x, B.y, C.x, C.y)) {
            // Line Segment end C lies on the Line Segment |AB|
            return new Point2d(C.x, C.y);
        } else if (pointLiesOnLineSegment(A.x, A.y, B.x, B.y, D.x, D.y)) {
            // Line Segment end D lies on the Line Segment |AB|
            return new Point2d(D.x, D.y);
        } else if (pointLiesOnLineSegment(C.x, C.y, D.x, D.y, A.x, A.y)) {
            // Line Segment end A lies on the Line Segment |CD|
            return new Point2d(A.x, A.y);
        } else if (pointLiesOnLineSegment(C.x, C.y, D.x, D.y, B.x, B.y)) {
            //			System.out.println("Odcinki sie przecinaja- przynaleznosc");
            // Line Segment end B lies on the Line Segment |CD|
            return new Point2d(B.x, B.y);
        } else {
            // if none of points lies on line segment
            // zaden punkt nie nalezy do drugego odcinka
            if (matrixDet(A, B, C)
                    * matrixDet(A, B, D) >= 0) {
                //System.out.println("Odcinki sie NIE przecinaja");
                return null;
            } else if (matrixDet(C, D, A)
                    * matrixDet(C, D, B) >= 0) {
                //System.out.println("Odcinki sie NIE przecinaja");
                return null;
            } else {
                // znaki wyznacznikow sa rowne
                //				System.out.println("Odcinki sie przecinaja- punkty leza po przeciwnych stronach");

                return lineCrossPoint(A.x, A.y, B.x, B.y, C.x, C.y, D.x, D.y);
            }
        }
    }


    /**
     * @param p1
     * @param p2
     * @param v1
     * @param v2
     * @return
     *
     * @see {http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect/565282#565282}
     */
    public static Point2d intersectLineSegments(Point2d p1, Point2d p2, Vector2d v1, Vector2d v2) {
        Point2d p = p1;
        Vector2d r = v1;
        Point2d q = p2;
        Vector2d s = v2;

        Vector2d qp = new Vector2d(q.x - p.x, q.y - p.y);
        double rs = Vector2dUtil.cross(r, s);



        //double t = Vector2dUtil.cross(qp, s) / rs;
        double u = Vector2dUtil.cross(qp, r) / rs;

        if (rs == 0) {
            if (u == 0) {
                // lines are collinear
                return new Point2d(p);
            } else {
                // never intersect
                return null;
            }
        }

        return new Point2d(q.x + u * s.x, q.y + u * s.y);

    }

    /** Calculate cross point of two lines.
     * @param Ax1 line 1
     * @param Ay1
     * @param Ax2
     * @param Ay2
     * @param Bx1 line 2
     * @param By1
     * @param Bx2
     * @param By2
     * @return
     * XXX move to LineXXX
     */
    public static Point2d lineCrossPoint(double Ax1, double Ay1, double Ax2,
            double Ay2, double Bx1, double By1, double Bx2, double By2) {
        //	calc A, B, C for line
        //	double
        double A1 = Ay1 - Ay2;
        double B1 = Ax2 - Ax1;
        double C1 = Ax1 * Ay2 - Ax2 * Ay1;
        //			-(Ay2 - Ay1) * Ax1 + (Ax2 - Ax1)*Ay1;

        double A2 = By1 - By2;
        double B2 = Bx2 - Bx1;
        double C2 = Bx1 * By2 - Bx2 * By1;


        return LineLinear2d.collide(A1, B1, C1, A2, B2, C2);
    }
}
