package kendzi.math.geometry.point;

import javax.vecmath.Vector2d;

public class Vector2dUtil {

    public static Vector2d orthogonal(Vector2d v) {
        return new Vector2d(-v.y, v.x);
    }

    public static Vector2d bisector(Vector2d v1, Vector2d v2) {
        Vector2d norm1 = new Vector2d(v1);
        norm1.normalize();

        Vector2d norm2 = new Vector2d(v2);
        norm2.normalize();

        return bisectorNormalized(norm1, norm2);
    }

    public static Vector2d bisectorNormalized(Vector2d norm1, Vector2d norm2) {
        Vector2d e1v = orthogonal(norm1);
        Vector2d e2v = orthogonal(norm2);


        // 90 - 180 || 180 - 270
      //  if (norm1.dot(e2v) <= 0 && ) { //XXX >= !!
        if (norm1.dot(norm2) > 0) {

             e1v.add(e2v);
             return e1v;

        }


        // 0 - 180
        Vector2d ret = new Vector2d(norm1);
        ret.negate();
        ret.add(norm2);


        if (e1v.dot(norm2) < 0) {
            // 270 - 360
            ret.negate();
        }
        return ret;



//        // e1v.normalize();
//        // e2v.normalize();
//
//        if (e1v.dot(e2v) >= 0) {
//
//            e1v.add(e2v);
//
//            if (equalsEpsilon(e2v.x) || equalsEpsilon(e2v.x)) {
//                // ???
//            }
//
//            if (e1v.x == 0 && e1v.y == 0) {
//                // edges are parnell. Chose any one
//                // later we calc direction
//                return new Vector2d(norm1);
//            }
//
//            return e1v;
//
//        } else {
//
////        e1v = new Vector2d(norm1);
////        e2v = new Vector2d(norm2);
//
//        if (e1v.dot(norm1) > 0) {
//
//            norm1.negate();
//            norm1.add(norm2);
//
//
//            e1v.negate();
//
//        } else {
//            e2v.negate();
//        }
//        }
//        return e1v;
    }

    static boolean equalsEpsilon(double pNumber) {
        if((pNumber<0?-pNumber:pNumber) > EPSILON) {
            return false;
        }
        return false;

    }

    private static double EPSILON = 0.00000001;


}
