package kendzi.math.geometry.polygon;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.polygon.CircleInsidePolygon.Circle;

import org.junit.Test;

public class CircleInsidePolygonTest {

    @Test
    public void test1() {

        double epsilon = 0.01;

        List<Point2d> points = new ArrayList<Point2d>();
        points.add(new Point2d(0,0));
        points.add(new Point2d(1,0));
        points.add(new Point2d(1,1));
        points.add(new Point2d(0,1));

        PolygonList2d poly = new PolygonList2d(points);
        Circle p = CircleInsidePolygon.iterativeNonConvex(poly, epsilon);

        assertNotNull(p);
//
//        fail("Not yet implemented");
    }

//    @Test
//    public void test() {
//        fail("Not yet implemented");
//    }

}
