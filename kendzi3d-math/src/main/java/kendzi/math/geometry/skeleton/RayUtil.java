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


//http://softsurfer.com/Archive/algorithm_0102/algorithm_0102.htm

//Assume that classes are already given for the objects:
// Point and Vector with
//     coordinates {double x, y, z;}
//     operators for:
//         == to test equality
//         != to test inequality
//         Point  = Point   Vector
//         Vector = Point - Point
//         Vector = Scalar * Vector    (scalar product)
//         Vector = Vector * Vector    (3D cross product)
// Line and Ray and Segment with defining points {Point P0, P1;}
//     (a Line is infinite, Rays and Segments start at P0)
//     (a Ray extends beyond P1, but a Segment ends at P1)
// Plane with a point and a normal {Point V0; Vector n;}
//===================================================================

public class RayUtil {
    final static double SMALL_NUM = 0.00000001; // anything that avoids division overflow

    // dot product (3D) which allows vector operations in arguments

    static double dot(Vector2d u, Vector2d v) {
        return u.dot(v);
//        return ((u).x * (v).x + (u).y * (v).y// + (u).z * (v).z
//        );
    }

    /** Perp Dot Product.
     * @param u
     * @param v
     * @return
     */
    static double perp(Vector2d u, Vector2d v) {

        return u.x * v.y - u.y * v.x;

    }

    // public class Segment {
    // public Point2d P0;
    // public Point2d P1;
    // }

    // int intersectRays2d(Ray2d r1, Ray2d r2, Point2d I0, Point2d I1) {
    // Point2d
    // return intersectRays2d(new Segment(r1.p, r1.p , S2, I0, I1)
    // }

    // intersect2D_2Segments(): the intersection of 2 finite 2D segments
    // Input: two finite segments S1 and S2
    // Output: *I0 = intersect point (when it exists)
    // *I1 = endpoint of intersect segment [I0,I1] (when it exists)
    // Return: 0=disjoint (no intersect)
    // 1=intersect in unique point I0
    // 2=overlap in segment from I0 to I1
    public static Point2d intersectRays2d(Ray2d r1, Ray2d r2, Point2d I0, Point2d I1) {

        Point2d s1p0 = r1.A;
        Point2d s1p1 = new Point2d(r1.A);
        s1p1.add(r1.U);

        Point2d s2p0 = r2.A;
        Point2d s2p1 = new Point2d(r2.A);
        s2p1.add(r2.U);

        Vector2d u = r1.U;
//            new Vector2d();
//        u.sub(s1p1, s1p0);
        Vector2d v = r2.U;
//            new Vector2d();
//        v.sub(s2p1, s2p0);
        Vector2d w = new Vector2d();
        w.sub(s1p0, s2p0);

        double d = perp(u, v);

        // test if they are parallel (includes either being a point)
        if (Math.abs(d) < SMALL_NUM) { // S1 and S2 are parallel
            if (perp(u, w) != 0 || perp(v, w) != 0) {
                // return 0; // they are NOT collinear
                return null; // they are NOT collinear
            }
            // they are collinear or degenerate
            // check if they are degenerate points
            double du = dot(u, u);
            double dv = dot(v, v);
            if (du == 0 && dv == 0) { // both segments are points
                if (s1p0 != s2p0) {

                    // return 0;
                    return null;
                }
                I0 = s1p0; // they are the same point
                // return 1;
                return I0;
            }
            if (du == 0) { // S1 is a single point
                if (!inSegment(s1p0, s2p0, s2p1)) {
                    // return 0;
                    return null;
                }
                I0 = s1p0;
                // return 1;
                return I0;
            }
            if (dv == 0) { // S2 a single point
                if (!inSegment(s2p0, s1p0, s1p1)) {
                    // return 0;
                    return null;
                }
                I0 = s2p0;
                // return 1;
                return I0;
            }
            // they are collinear segments - get overlap (or not)
            double t0, t1; // endpoints of S1 in eqn for S2
            Vector2d w2 = new Vector2d();
            w2.sub(s1p1, s2p0);
            if (v.x != 0) {
                t0 = w.x / v.x;
                t1 = w2.x / v.x;
            } else {
                t0 = w.y / v.y;
                t1 = w2.y / v.y;
            }
            if (t0 > t1) { // must have t0 smaller than t1
                double t = t0;
                t0 = t1;
                t1 = t; // swap if not
            }
            if (t0 > 1 || t1 < 0) {
                // return 0; // NO overlap
                return null;
            }
            t0 = t0 < 0 ? 0 : t0; // clip to min 0
            t1 = t1 > 1 ? 1 : t1; // clip to max 1
            if (t0 == t1) { // intersect is a point

                Point2d ret = new Point2d(v);
                ret.scale(t0);
                ret.add(s2p0);
                // I0 = S2_P0 + t0 * v;
                I0 = ret;
                // return 1;
                return I0;
            }

            // they overlap in a valid subsegment

            // I0 = S2_P0 + t0 * v;
            Point2d ret = new Point2d(v);
            ret.scale(t0);
            ret.add(s2p0);
            I0 = ret;

            // I1 = S2_P0 + t1 * v;
            Point2d ret2 = new Point2d(v);
            ret2.scale(t1);
            ret2.add(s2p0);
            // return 2;
            return I0;
        }

        // the segments are skew and may intersect in a point
        // get the intersect parameter for S1
        double sI = perp(v, w) / d;
        if (sI < 0/* || sI > 1 */) {
            // return 0;
            return null;
        }

        // get the intersect parameter for S2
        double tI = perp(u, w) / d;
        if (tI < 0 /* || tI > 1 */) {
            // return 0;
            return null;
        }

        // I0 = S1_P0 + sI * u; // compute S1 intersect point
        Point2d ret = new Point2d(u);
        ret.scale(sI);
        ret.add(s1p0);
        I0 = ret;
        // return 1;
        return I0;
    }

    // ===================================================================

    // inSegment(): determine if a point is inside a segment
    // Input: a point P, and a collinear segment S
    // Return: 1 = P is inside S
    // 0 = P is not inside S
    static boolean inSegment(Point2d P, Point2d segmentP0, Point2d segmentP1) {

        if (segmentP0.x != segmentP1.x) { // S is not vertical
            if (segmentP0.x <= P.x && P.x <= segmentP1.x) {
                return true;
            }
            if (segmentP0.x >= P.x && P.x >= segmentP1.x) {
                return true;
            }
        } else { // S is vertical, so test y coordinate
            if (segmentP0.y <= P.y && P.y <= segmentP1.y) {
                return true;
            }
            if (segmentP0.y >= P.y && P.y >= segmentP1.y) {
                return true;
            }
        }
        return false;
    }

}
