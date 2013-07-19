/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofHookPoint;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRow;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LineLinear2d;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;

import org.ejml.simple.SimpleMatrix;

public class PolygonRoofHooksSpace implements RoofHooksSpace {
    Point2d p1;
    Vector2d v1;
    double b;
    SimpleMatrix transformationMatrix;
    // FIXME

    Plane3d plane;
    private List<List<Point2d>> polygon;

    public PolygonRoofHooksSpace(Point2d p1, Vector2d v1, MultiPolygonList2d pPolygon, Plane3d pPlane) {
        this(p1, v1, toList(pPolygon), pPlane);
    }

    private static List<List<Point2d>> toList(MultiPolygonList2d pPolygon) {

        List<List<Point2d>> ret = new ArrayList<List<Point2d>>();

        for (PolygonList2d polygon : pPolygon.getPolygons()) {

            List<Point2d> poly = polygon.getPoints();

            ret.add(poly);
        }
        return ret;
    }

    public PolygonRoofHooksSpace(Point2d p1, Vector2d v1, List<List<Point2d>> pPolygon, Plane3d pPlane) {
        super();
//        this.p1 = p1;
//        this.v1 = v1;
        this.b = this.b;


        double angle = Math.atan2(v1.y, v1.x);
        // Math.toDegrees(angle);
        SimpleMatrix tr2d = TransformationMatrix2d.rotZA(-angle).mult(TransformationMatrix2d.tranA(-p1.x, -p1.y));
        SimpleMatrix tr3d = TransformationMatrix3d.rotYA(-angle).mult(TransformationMatrix3d.tranA(-p1.x, 0, p1.y));

        this.p1 = TransformationMatrix2d.transform(p1, tr2d);
        this.v1 = TransformationMatrix2d.transform(v1, tr2d);

        List<List<Point2d>> transformPolygons = new ArrayList<List<Point2d>>();
        for (List<Point2d> polygon : pPolygon) {
            List<Point2d> transformPolygon = new ArrayList<Point2d>();
            for (Point2d point : polygon) {
                transformPolygon.add(TransformationMatrix2d.transform(point, tr2d));
            }
            transformPolygons.add(transformPolygon);
        }
        this.polygon = transformPolygons;


        Point3d planePoint= TransformationMatrix3d.transform(pPlane.getPoint(), tr3d);
        Vector3d planeNormal = TransformationMatrix3d.transform(pPlane.getNormal(), tr3d);

        this.plane = new Plane3d(planePoint, planeNormal);
        SimpleMatrix trBack = TransformationMatrix3d.tranA(p1.x, 0, -p1.y).mult(TransformationMatrix3d.rotYA(angle));
        //SimpleMatrix trBack = TransformationMatrix3d.rotYA(angle).mult(TransformationMatrix3d.tranA(-p1.x, 0, p1.y));
       // TransformationMatrix3d.transform(planePoint, trBack)
        this.transformationMatrix = trBack;
    }

    @Override
    public RoofHookPoint[] getRoofHookPoints(int pNumber, DormerRow dormerRow, int dormerRowNum) {
        Vector2d v = new Vector2d(this.v1);

        MinMax polygonMinMaxY = findMinMaxY(this.polygon);
        if (polygonMinMaxY == null) {
            // XXX
            polygonMinMaxY = new MinMax(0, 1d);
        }

        v.scale(1d / (pNumber + 1d));

        Point2d p = new Point2d(this.p1);

        RoofHookPoint[] ret = new RoofHookPoint[pNumber];
        for (int i = 0; i < pNumber; i++) {
            p.add(v);

            MinMax minMaxY =
                limitZToPolygon(p.x);

            double z = calcRowPosition(minMaxY, dormerRow,  dormerRowNum);

//            double z = minMaxY.getMin();

            double y = this.plane.calcYOfPlane(p.x, -z);


            Point3d pp = new Point3d(p.x, y, -z);

            double b = minMaxY.getMax() - z;//(minMaxY.getMax() - minMaxY.getMin()) - z;

            RoofHookPoint hook = new RoofHookPoint(pp, Math.toRadians(0), b, Math.toRadians(0));

            ret[i] = hook;
        }

        return ret;

    }

    private double calcRowPosition(MinMax minMaxY, DormerRow dormerRow, int dormerRowNum) {
        double row = (dormerRow.getRowNum() - 1) / (double) dormerRowNum;
        return minMaxY.getMin() + (minMaxY.getMax() - minMaxY.getMin()) * row ;
    }

//    /**
//     * @param y
//     * @return
//     */
//    private double limitZToMultiPolygons(double x) {
//        int size = this.polygon.size();
//
//        double minY = 0;
//        double maxY = 0;
//
//        for (List<Point2d> polygon : polygon) {
//            double limitZToPolygon = limitZToPolygon(x, polygon);
//
//            xxx
//        }
//
//
//    }

    private MinMax findMinMaxY(List<List<Point2d>> pPolygon) {

        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        boolean empty = true;

        if (pPolygon == null) {
            return null;
        }

        for (List<Point2d> polygon : pPolygon) {

            int size = polygon.size();
            for (int i = 0; i < size; i++) {
                Point2d p1 = polygon.get(i);

                empty = false;

                if (p1.y < minY) {
                    minY = p1.y;
                }

                if (p1.y > maxY) {
                    maxY = p1.y;
                }
            }
        }
        if (empty) {
            return null;
        }
        return new MinMax(minY, maxY);
    }

    class MinMax {
        double min;
        double max;


        public MinMax(double min, double max) {
            super();
            this.min = min;
            this.max = max;
        }

        /**
         * @return the min
         */
        public double getMin() {
            return min;
        }
        /**
         * @param min the min to set
         */
        public void setMin(double min) {
            this.min = min;
        }
        /**
         * @return the max
         */
        public double getMax() {
            return max;
        }
        /**
         * @param max the max to set
         */
        public void setMax(double max) {
            this.max = max;
        }

    }

    /**
     * @param y
     * @return
     */
    private MinMax limitZToPolygon(double x) {

        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        boolean isMach = false;

        LineLinear2d parnel = new LineLinear2d(new Point2d(x,0), new Point2d(x,1));

        for (List<Point2d> polygon : this.polygon) {


            int size = polygon.size();
            for (int i = 0; i < size; i++) {
                Point2d p1 = polygon.get(i);
                Point2d p2 = polygon.get((i + 1) % size);

                if (Math.min(p1.x, p2.x) < x
                        && Math.max(p1.x, p2.x) >= x) {

                    isMach = true;

                    LineLinear2d edge = new LineLinear2d(p1, p2);

                    Point2d collide = parnel.collide(edge);

                    if (collide.y < minY) {
                        minY = collide.y;
                    }
                    if (collide.y > maxY) {
                        maxY = collide.y;
                    }
                }
            }
        }

        //if (isMach) {
        return new MinMax(minY, maxY);
        //}
        //return null;
    }

    @Override
    public SimpleMatrix getTransformationMatrix() {
        return this.transformationMatrix;
    }

}
