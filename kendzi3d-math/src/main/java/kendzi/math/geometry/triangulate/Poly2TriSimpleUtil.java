package kendzi.math.geometry.triangulate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Point2d;

import kendzi.math.geometry.Triangle2d;
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
public class Poly2TriSimpleUtil {

    /**
     * Triangulate polygon with holes.
     *
     * @param polygonWithHoles polygon with holes
     * @return list of triangles
     */
    public static List<Triangle2d> triangulate(PolygonWithHolesList2d polygonWithHoles) {

        Polygon outer = convert(polygonWithHoles);

        Poly2Tri.triangulate(outer);

        return convert(outer);
    }

    private static List<Triangle2d> convert(Polygon p) {

        List<DelaunayTriangle> triangles = p.getTriangles();

        if (triangles == null || triangles.size() == 0) {
            return null;
        }

        List<Triangle2d> out = new ArrayList<Triangle2d>();
        for (DelaunayTriangle t : triangles) {
            Triangle2d triangle = new Triangle2d(
                    new Point2d(t.points[0].getX(), t.points[0].getY()),
                    new Point2d(t.points[1].getX(), t.points[1].getY()),
                    new Point2d(t.points[2].getX(), t.points[2].getY()));

            out.add(triangle);
        }
        return out;
    }

    private static Polygon convert(PolygonWithHolesList2d polygonWithHoles) {

        Polygon outer = convert(polygonWithHoles.getOuter());

        if (polygonWithHoles.getInner() != null) {
            for (PolygonList2d inner : polygonWithHoles.getInner()) {
                outer.addHole(convert(inner));
            }
        }
        return outer;
    }

    private static Polygon convert(PolygonList2d polygon) {

        return new Polygon(polygon.getPoints().stream()
                .map(p -> new PolygonPoint(p.x, p.y)).collect(Collectors.toCollection(ArrayList<PolygonPoint>::new)));
    }
}
