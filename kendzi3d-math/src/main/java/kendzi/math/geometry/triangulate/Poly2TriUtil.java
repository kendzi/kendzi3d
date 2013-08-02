package kendzi.math.geometry.triangulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.Triangle2d;
import kendzi.math.geometry.line.LineSegment2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonUtil;

import org.poly2tri.Poly2Tri;
import org.poly2tri.triangulation.Triangulatable;
import org.poly2tri.triangulation.TriangulationAlgorithm;
import org.poly2tri.triangulation.TriangulationContext;
import org.poly2tri.triangulation.TriangulationMode;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.point.TPoint;


/**
 * @author Hannes Janetzek
 */
public class Poly2TriUtil {
    static class CDTSet implements Triangulatable {
        List<TriangulationPoint> points = new ArrayList<TriangulationPoint>(20);
        List<DelaunayTriangle> triangles = new ArrayList<DelaunayTriangle>(20);
        ArrayList<LineSegment2d> segmentSet = new ArrayList<LineSegment2d>();

        // it seems poly2tri requires points to be unique objects
        HashMap<Point2d, TriangulationPoint> pointSet
        = new HashMap<Point2d, TriangulationPoint>();

        public CDTSet(PolygonList2d polygon,
                Collection<PolygonList2d> holes,
                Collection<LineSegment2d> cSegments,
                Collection<Point2d> cPoints) {

            List<Point2d> vertices = polygon.getPoints();

            segmentSet.addAll(cSegments);

            for (Point2d p : cPoints) {
                if (!pointSet.containsKey(p)) {
                    TPoint tp = new TPoint(p.x, p.y);
                    pointSet.put(p, tp);
                    points.add(tp);
                }
            }

            for (int i = 0, n = vertices.size() - 1; i < n; i++) {
                segmentSet.add(new LineSegment2d(vertices.get(i),
                        vertices.get(i + 1)));
            }

            for (PolygonList2d hole : holes) {
                vertices = hole.getPoints();
                for (int i = 0, n = vertices.size() - 1; i < n; i++) {
                    segmentSet.add(new LineSegment2d(vertices.get(i),
                            vertices.get(i + 1)));
                }
            }

            removeDuplicateSegments();

            boolean foundIntersections = false;

            // split at intersections
            for (int i = 0, size = segmentSet.size(); i < size - 1; i++) {
                LineSegment2d l1 = segmentSet.get(i);

                for (int j = i + 1; j < size; j++) {
                    LineSegment2d l2 = segmentSet.get(j);

                    Point2d crossing;

                    if ((crossing = l1.intersectEpsilon(l2, 10E9)) != null) {
                        //					if ((crossing = l1.getIntersection(l2.getBegin(), l2.getEnd())) != null) {
                        System.out.println("split " + l1 + " " + l2 + " at "
                                + crossing);
                        foundIntersections = true;

                        segmentSet.remove(l1);
                        segmentSet.remove(l2);

                        segmentSet.add(new LineSegment2d(crossing, l1.getBegin()));
                        segmentSet.add(new LineSegment2d(crossing, l1.getEnd()));
                        segmentSet.add(new LineSegment2d(crossing, l2.getBegin()));
                        segmentSet.add(new LineSegment2d(crossing, l2.getEnd()));

                        size += 2;

                        // first segment was removed
                        i--;
                        break;
                    }
                }
            }

            if (foundIntersections) {
                removeDuplicateSegments();
            }
        }

        private void removeDuplicateSegments() {
            for (int i = 0, size = segmentSet.size(); i < size - 1; i++) {
                LineSegment2d l1 = segmentSet.get(i);

                for (int j = i + 1; j < size; j++) {
                    LineSegment2d l2 = segmentSet.get(j);

                    if ((l1.getBegin().equals(l2.getBegin()) && l1.getEnd().equals(l2.getEnd()))
                            || (l1.getBegin().equals(l2.getEnd()) && l1.getEnd().equals(l2.getBegin()))) {
                        //System.out.println("remove dup " + l1 + " " + l2);
                        segmentSet.remove(j);
                        size--;
                    }
                }
            }
        }

        @Override
        public TriangulationMode getTriangulationMode() {
            return TriangulationMode.CONSTRAINED;
        }

        @Override
        public List<TriangulationPoint> getPoints() {
            return points;
        }

        @Override
        public List<DelaunayTriangle> getTriangles() {
            return triangles;
        }

        @Override
        public void addTriangle(DelaunayTriangle t) {
            triangles.add(t);
        }

        @Override
        public void addTriangles(List<DelaunayTriangle> list) {
            triangles.addAll(list);
        }

        @Override
        public void clearTriangulation() {
            triangles.clear();
        }

        @Override
        public void prepareTriangulation(TriangulationContext<?> tcx) {
            triangles.clear();


            for (LineSegment2d l : segmentSet) {
                TriangulationPoint tp1, tp2;

                if (!pointSet.containsKey(l.getBegin())){
                    tp1 = new TPoint(l.getBegin().x, l.getBegin().y);
                    pointSet.put(l.getBegin(), tp1);
                    points.add(tp1);
                } else {
                    tp1 = pointSet.get(l.getBegin());
                }
                if (!pointSet.containsKey(l.getEnd())){
                    tp2 = new TPoint(l.getEnd().x, l.getEnd().y);
                    pointSet.put(l.getEnd(), tp2);
                    points.add(tp2);
                } else {
                    tp2 = pointSet.get(l.getEnd());
                }

                tcx.newConstraint(tp1, tp2);
            }

            segmentSet.clear();
            pointSet.clear();

            tcx.addPoints(points);
        }
    }

    public static final List<Triangle2d> triangulate(PolygonList2d polygon,
            Collection<PolygonList2d> holes,
            Collection<LineSegment2d> segments, Collection<Point2d> points) {

        CDTSet cdt = new CDTSet(polygon, holes, segments, points);
        TriangulationContext<?> tcx = Poly2Tri
                .createContext(TriangulationAlgorithm.DTSweep);
        tcx.prepareTriangulation(cdt);

        Poly2Tri.triangulate(tcx);

        List<Triangle2d> triangles = new ArrayList<Triangle2d>();

        List<DelaunayTriangle> result = cdt.getTriangles();

        if (result == null) {
            return triangles;
        }

        for (DelaunayTriangle t : result) {

            TriangulationPoint tCenter = t.centroid();
            Point2d center = new Point2d(tCenter.getX(), tCenter.getY());

            boolean triangleInHole = false;
            for (PolygonList2d hole : holes) {
                if (PolygonUtil.isInside(center, hole)) {
                    //if (hole.contains(center)) {
                    triangleInHole = true;
                    break;
                }
            }

            if (triangleInHole || !PolygonUtil.isInside(center, polygon)) {
                //			if (triangleInHole || !polygon.contains(center))
                continue;
            }

            triangles.add(new Triangle2d(new Point2d(t.points[0].getX(), t.points[0].getY()), new Point2d(t.points[1].getX(),
                    t.points[1].getY()), new Point2d(t.points[2].getX(), t.points[2].getY())));
        }

        return triangles;

    }
}