package kendzi.math.geometry.polygon;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.line.LineSegment2d;

public class CircleInsidePolygon {

    /**
     *
     * @see http://stackoverflow.com/questions/4279478/maximum-circle-inside-a-non-convex-polygon
     *
     * @param poly
     * @param epsilon
     * @return
     */
    public static Circle iterativeNonConvex(PolygonList2d poly, double epsilon) {

        double divide = 20;
        Point2d maxBound = PolygonUtil.maxBound(poly);
        Point2d minBound = PolygonUtil.minBound(poly);

        double minX = minBound.x;
        double minY = minBound.y;
        double deltaX = (maxBound.x - minBound.x) / divide;
        double deltaY = (maxBound.y - minBound.y) / divide;

        Circle best = null;

        do {
            List<Point2d> insideList = new ArrayList<Point2d>();

            for (double xi = 0; xi <= divide; xi++) {
                for (double yi = 0; yi <= divide; yi++) {

                    Point2d p = new Point2d(
                            minX + xi * deltaX,
                            minY + yi * deltaY);

                    if (PolygonUtil.isInside(p, poly)) {
                        insideList.add(p);
                    }
                }
            }

            if (insideList.size() == 0) {
                //???
            }

            best = furthestFromAnyPointOnEdge(insideList, poly);

            deltaX = deltaX / Math.sqrt(2); //???
            deltaY = deltaY / Math.sqrt(2); //???

            minX = best.point.x - deltaX * divide / 2.0;
            minY = best.point.y - deltaY * divide / 2.0;


        } while (Math.max(deltaX, deltaY) > epsilon);

        return best;
    }

    private static Circle furthestFromAnyPointOnEdge(List<Point2d> insideList, PolygonList2d poly) {

        if (insideList.size() == 0 ) {
            return null;
        }

        List<Point2d> points = poly.getPoints();


        List<LineSegment2d> segmentList = new ArrayList<LineSegment2d>();

        Point2d begin = points.get(points.size() - 1);
        for (Point2d end : points) {
            LineSegment2d segment = new LineSegment2d(begin, end);
            segmentList.add(segment);
            begin = end;
        }

        double maxDistance = -Double.MAX_VALUE;
        Point2d maxDistancePoint = null;

        for (Point2d p : insideList) {

            Double distance = distanceFromAnyPointOnEdge(p, segmentList);
            if (distance > maxDistance) {
                maxDistance = distance;
                maxDistancePoint = p;
            }

        }

        return new Circle(maxDistancePoint, maxDistance);
    }

    public static class Circle {
        Point2d point;
        double radius;


        public Circle(Point2d point, double radius) {
            super();
            this.point = point;
            this.radius = radius;
        }

        /**
         * @return the point
         */
        public Point2d getPoint() {
            return this.point;
        }
        /**
         * @param point the point to set
         */
        public void setPoint(Point2d point) {
            this.point = point;
        }
        /**
         * @return the radius
         */
        public double getRadius() {
            return this.radius;
        }
        /**
         * @param radius the radius to set
         */
        public void setRadius(double radius) {
            this.radius = radius;
        }


    }

    private static Double distanceFromAnyPointOnEdge(Point2d P, List<LineSegment2d> segmentList) {

        if (segmentList.size() == 0 ) {
            return null;
        }

        double minDistance = Double.MAX_VALUE;


        for (LineSegment2d segment : segmentList) {

            double distance = LineSegment2d.distancePointToSegment(P, segment);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }
}
