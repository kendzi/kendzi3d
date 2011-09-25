package kendzi.math.geometry.point;

import static org.junit.Assert.assertTrue;

import javax.vecmath.Vector2d;

import org.junit.Test;

public class Vector2dUtilTest {

    @Test
    public void testBisector() {

        Vector2d v0 = new Vector2d(1, 0);

        // 1
        Vector2d v1 = new Vector2d(1, 0);

        Vector2d v2 = new Vector2d(1, 1);

        Vector2d v3 = new Vector2d(0, 1);

        Vector2d v4 = new Vector2d(-1, 1);

        Vector2d v5 = new Vector2d(-1, 0);

        // 2
        Vector2d v6 = new Vector2d(1, -1);
        Vector2d v7 = new Vector2d(0, -1);
        Vector2d v8 = new Vector2d(-1, -1);

        Vector2d bisector = Vector2dUtil.bisector(v0, v1);
        assertAngle(Math.toRadians(180 - 90), v0.angle(bisector));
        assertLenght(bisector);

        bisector = Vector2dUtil.bisector(v0, v2);
        assertAngle(Math.toRadians(180 - 67.5), angle(bisector));
        assertLenght(bisector);

        bisector = Vector2dUtil.bisector(v0, v3);
        assertAngle(Math.toRadians(180 - 45), angle(bisector));
        assertLenght(bisector);

        bisector = Vector2dUtil.bisector(v0, v4);
        assertAngle(Math.toRadians(157.5), angle(bisector));
        assertLenght(bisector);

        bisector = Vector2dUtil.bisector(v0, v5);
        assertAngle(Math.toRadians(180 - 0), angle(bisector));
        assertLenght(bisector);

        bisector = Vector2dUtil.bisector(v0, v6);
        assertAngle(Math.toRadians(67.5), angle(bisector));
        assertLenght(bisector);

        bisector = Vector2dUtil.bisector(v0, v7);
        assertAngle(Math.toRadians(45), v0.angle(bisector));
        assertLenght(bisector);

        bisector = Vector2dUtil.bisector(v0, v8);
        assertAngle(Math.toRadians(22.5), v0.angle(bisector));
        assertLenght(bisector);

        // fail("Not yet implemented");
    }

    double angle(Vector2d v) {
        return Math.atan2(v.y, v.x);

    }

    public static void assertLenght(Vector2d v) {

        assertTrue("vector is to short", v.length() > 0.25);
    }

    public static void assertAngle(double d1, double d2) {
        assertTrue("expected: " + Math.toDegrees(d1) + " recived: " + Math.toDegrees(d2), e(d1, d2));
    }

    static boolean e(double d1, double d2) {
        return Math.abs(d2 - d1) < 0.0001;
    }

}
