package kendzi.math.geometry.skeleton;

import static org.junit.Assert.*;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import kendzi.math.geometry.skeleton.RayUtil.IntersectPoints;

import org.junit.Test;

public class RayUtilTest {

    @Test
    public void testIsPointOnRay_1() {

        Point2d point = new Point2d(-2.0, 2.0);
        Ray2d ray = new Ray2d(new Point2d(-1.0, 1.0), new Vector2d(-1.0, 1.0));

        assertTrue(RayUtil.isPointOnRay(point, ray, 0.0000001));
    }

    @Test
    public void testIsPointOnRay_2() {

        Point2d point = new Point2d(-2.1, 2.0);
        Ray2d ray = new Ray2d(new Point2d(-1.0, 1.0), new Vector2d(-1.0, 1.0));

        assertFalse(RayUtil.isPointOnRay(point, ray, 0.0000001));
    }

    @Test
    public void testIsPointOnRay_3() {

        Point2d point = new Point2d(2.0, -2.0);
        Ray2d ray = new Ray2d(new Point2d(-1.0, 1.0), new Vector2d(-1.0, 1.0));

        assertFalse(RayUtil.isPointOnRay(point, ray, 0.0000001));
    }


    @Test
    public void testIntersectRays2d_1() {
        Ray2d r1 = new Ray2d(new Point2d(0, 0), new Vector2d(1, 0));
        Ray2d r2 = new Ray2d(new Point2d(0, -1), new Vector2d(1, 1));

        IntersectPoints intersectRays2d = RayUtil.intersectRays2d(r1, r2);
        assertPoint(1, 0, intersectRays2d.getIntersect());
    }

    @Test
    public void testIntersectRays2d_2() {
        Ray2d r1 = new Ray2d(new Point2d(0, 0), new Vector2d(1, 0));
        Ray2d r2 = new Ray2d(new Point2d(0, -1), new Vector2d(1, 0));

        IntersectPoints intersectRays2d = RayUtil.intersectRays2d(r1, r2);
        assertNull(intersectRays2d.getIntersect());
    }

    @Test
    public void testIntersectRays2d_3() {
        Ray2d r1 = new Ray2d(new Point2d(0, 0), new Vector2d(1, 0));
        Ray2d r2 = new Ray2d(new Point2d(0.5, 0), new Vector2d(1, 0));

        IntersectPoints intersectRays2d = RayUtil.intersectRays2d(r1, r2);
        assertPoint(0.5, 0, intersectRays2d.getIntersect());
        assertPoint(1, 0, intersectRays2d.getIntersectEnd());
    }



    private void assertPoint(double x, double y, Point2d point) {
        if (doubleIsDifferent(x, point.x, 0.01) || doubleIsDifferent(y, point.y, 0.01)) {
            fail(String.format("points don't match expected <%s> but was: <%s>", new Point2d(x, y), point));
        }
    }

    static private boolean doubleIsDifferent(double d1, double d2, double delta) {
        if (Double.compare(d1, d2) == 0) {
            return false;
        }
        if (Math.abs(d1 - d2) <= delta) {
            return false;
        }

        return true;
    }

}
