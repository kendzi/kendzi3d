package kendzi.math.geometry.ray;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import kendzi.math.geometry.Sphere3d;

public class Ray3dUtil {

    public static Double intersect(Ray3d ray, Sphere3d sphere) {
        return intersect(ray, sphere.getCenter(), sphere.getRadius());
    }

    public static Double intersect(Ray3d ray, Point3d sphereCenter, double sphereRadius) {

//        double r = sphere.getRadius();
        double r = sphereRadius;

        Vector3d ray_o = new Vector3d(ray.getPoint());
        ray_o.sub(sphereCenter);
//        ray_o.sub(sphere.getCenter());


        Vector3d ray_d = ray.getVector();
//        Point3d ray_o = ray.getPoint();



        // Compute A, B and C coefficients
        double a = dot(ray_d, ray_d);
        double b = 2 * dot(ray_d, ray_o);
        double c = dot(ray_o, ray_o) - (r * r);

        double t;

        // Find discriminant
        double disc = b * b - 4 * a * c;

        // if discriminant is negative there are no real roots, so return
        // false as ray misses sphere
        if (disc < 0) {
            return null;
        }

        // compute q as described above
        double distSqrt = Math.sqrt(disc);
        double q;
        if (b < 0) {
            q = (-b - distSqrt) / 2.0;
        } else {
            q = (-b + distSqrt) / 2.0;
        }

        // compute t0 and t1
        double t0 = q / a;
        double t1 = c / q;

        // make sure t0 is smaller than t1
        if (t0 > t1) {
            // if t0 is bigger than t1 swap them around
            double temp = t0;
            t0 = t1;
            t1 = temp;
        }

        // if t1 is less than zero, the object is in the ray's negative direction
        // and consequently the ray misses the sphere
        if (t1 < 0) {
//            return false;
            return null;
        }

        // if t0 is less than zero, the intersection point is at t1
        if (t0 < 0) {
            t = t1;
//            return true;
            return t;
        }
        // else the intersection point is at t0
        else {
            t = t0;
//            return true;
            return t;
        }
    }

    /**
     *  Test intersection of sphere with center in 0,0 radius r, and ray
     * @param ray
     * @param r
     * @param t
     * @return
     *
     * @see {http://wiki.cgsociety.org/index.php/Ray_Sphere_Intersection}
     */
    Double intersect(Ray3d ray, double r) {

        double t;

        Vector3d ray_d = ray.getVector();
        Point3d ray_o = ray.getPoint();

        // Compute A, B and C coefficients
        double a = dot(ray_d, ray_d);
        double b = 2 * dot(ray_d, ray_o);
        double c = dot(ray_o, ray_o) - (r * r);

        // Find discriminant
        double disc = b * b - 4 * a * c;

        // if discriminant is negative there are no real roots, so return
        // false as ray misses sphere
        if (disc < 0) {
            return null;
        }

        // compute q as described above
        double distSqrt = Math.sqrt(disc);
        double q;
        if (b < 0) {
            q = (-b - distSqrt) / 2.0;
        } else {
            q = (-b + distSqrt) / 2.0;
        }

        // compute t0 and t1
        double t0 = q / a;
        double t1 = c / q;

        // make sure t0 is smaller than t1
        if (t0 > t1) {
            // if t0 is bigger than t1 swap them around
            double temp = t0;
            t0 = t1;
            t1 = temp;
        }

        // if t1 is less than zero, the object is in the ray's negative direction
        // and consequently the ray misses the sphere
        if (t1 < 0) {
//            return false;
            return null;
        }

        // if t0 is less than zero, the intersection point is at t1
        if (t0 < 0) {
            t = t1;
//            return true;
            return t;
        }
        // else the intersection point is at t0
        else {
            t = t0;
//            return true;
            return t;
        }
    }

    /**
     * Returns the dot product of this vector and vector v1.
     *
     * @param v1
     *            the other vector
     * @return the dot product of this and v1
     */
    public final static double dot(Tuple3d v, Tuple3d v1) {
        return (v.x * v1.x + v.y * v1.y + v.z * v1.z);
    }

    public final static Vector3d sub(Tuple3d v, Tuple3d v1) {
        return new Vector3d(v.x - v1.x, v.y - v1.y, v.z - v1.z);
    }

    /** Return closest point to ray, The point is laying on ray baseRay.
     * @param ray
     * @param baseRay
     * @return
     */
    public static Point3d closestPointOnBaseRay(Ray3d ray, Ray3d baseRay) {
        //http://geomalgorithms.com/a07-_distance.html
        Ray3d P = ray;
        Ray3d Q = baseRay;

        Vector3d u = P.getVector();
        Vector3d v = Q.getVector();

        Point3d Q0 = Q.getPoint();
        Point3d P0 = P.getPoint();

        Vector3d w0 = sub(P0, Q0);

        double a = dot(u, u);
        double b = dot(u, v);
        double c = dot(v, v);
        double d = dot(u, w0);
        double e = dot(v, w0);

        double m = a * c - b * b;
        if (m == 0) {
            return new Point3d(Q0);
        }

        double tc = (a * e - b * d) / m;

        return new Point3d(
                Q0.x + v.x * tc,
                Q0.y + v.y * tc,
                Q0.z + v.z * tc
                );

    }
}
