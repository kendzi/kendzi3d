package kendzi.math.geometry.triangulate;

import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class Poly2TriUtil {

    public static MultiPolygonList2d triangulate(PolygonWithHolesList2d ph) {

        Polygon outer = convert(ph);

        Poly2Tri.triangulate(outer);

        return convert(outer);
    }

    private static MultiPolygonList2d convert(Polygon p) {

        List<DelaunayTriangle> triangles = p.getTriangles();

        if (triangles == null || triangles.size() == 0) {
            return null;
        }

        MultiPolygonList2d out = new MultiPolygonList2d();
        for (DelaunayTriangle t : triangles) {
            PolygonList2d poly = new PolygonList2d();

            for (int i = 0; i < 3; i++) {
                poly.getPoints().add(new Point2d(t.points[i].getX(), t.points[i].getY()));
            }
            out.getPolygons().add(poly);
        }
        return out;
    }

    private static Polygon convert(PolygonWithHolesList2d polygonWithHoles) {

        Polygon outer = convert(polygonWithHoles.getOuter());

        if (polygonWithHoles.getInner() != null) {
            for (PolygonList2d inner : polygonWithHoles.getInner()) {
                Polygon innerPoly = convert(inner);

                outer.addHole(innerPoly);
            }
        }
        return outer;
    }

    public static Polygon convert(PolygonList2d polygon) {

        List<Point2d> p = polygon.getPoints();

        int n = p.size();
        PolygonPoint[] points = new PolygonPoint[n];
        for (int i = 0; i < n; i++) {
            points[i] = new PolygonPoint(p.get(i).x, p.get(i).y);
        }
        return new Polygon(points);
    }
}
