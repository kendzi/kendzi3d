package kendzi.math.geometry;

import javax.vecmath.Tuple2d;

public class AngleUtil {
    private static final double PI_2 = Math.PI * 2;

    public static double angleBetweenOriented(Tuple2d tip1, Tuple2d tail, Tuple2d tip2) {
        double a1 = angle(tail, tip1);
        double a2 = angle(tail, tip2);
        double angDel = a2 - a1;

        if (angDel <= -Math.PI) {
            return angDel + PI_2;
        }
        if (angDel > Math.PI) {
            return angDel - PI_2;
        }
        return angDel;
    }

    public static double angle(Tuple2d p0, Tuple2d p1) {
        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;
        return Math.atan2(dy, dx);
    }

    public static double angle(Tuple2d p) {
        return Math.atan2(p.y, p.x);
    }
}
