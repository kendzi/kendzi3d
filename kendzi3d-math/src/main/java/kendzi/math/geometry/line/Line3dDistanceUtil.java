package kendzi.math.geometry.line;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;


/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class Line3dDistanceUtil {


    // anything that avoids division overflow
    final static double EPSILON  =  0.00000001;


    static double dot(Tuple3d v0, Tuple3d v1) {
        return (v0.x*v1.x + v0.y*v1.y + v0.z*v1.z);
    }

    static Vector3d sub(Tuple3d t0, Tuple3d t1) {
        return new Vector3d(t0.x - t1.x, t0.y - t1.y, t0.z - t0.z);
    }

    /**
     * Get the 3D minimum distance between 2 lines.
     * @see <a href="http://geomalgorithms.com/a07-_distance.html">Distance</a>
     *
     * @param L1 3D lines
     * @param L2 3D lines
     * @return the shortest distance between L1 and L2
     */
    public static double distance(LinePoints3d L1, LinePoints3d L2) {

        Vector3d   u = sub(L1.getP2(), L1.getP1());
        Vector3d   v = sub(L2.getP2(), L2.getP1());
        Vector3d   w = sub(L1.getP1(), L2.getP1());

        // always >= 0
        double    a = dot(u,u);
        double    b = dot(u,v);

        // always >= 0
        double    c = dot(v,v);
        double    d = dot(u,w);
        double    e = dot(v,w);

        // always >= 0
        double    D = a*c - b*b;
        double    sc, tc;

        // compute the line parameters of the two closest points
        if (D < EPSILON) {
            // the lines are almost parallel
            sc = 0.0;
            // use the largest denominator
            tc = (b > c ? d / b : e / c);
        } else {
            sc = (b*e - c*d) / D;
            tc = (a*e - b*d) / D;
        }


        // get the difference of the two closest points
        // dP = L1(sc) - L2(tc)
        // dP = w + (sc * u) - (tc * v)

        u.scale(sc);
        v.scale(tc);

        Vector3d dP = w;
        dP.add(u);
        dP.sub(v);

        // return the closest distance
        return dP.length();
    }
}
