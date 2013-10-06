package kendzi.math.geometry.point;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;

public class Point2dUtil {
	public static Point2d sub(Tuple2d p1, Tuple2d p2) {
		return new Point2d(p1.x - p2.x, p1.y - p2.y	);
	}
}
