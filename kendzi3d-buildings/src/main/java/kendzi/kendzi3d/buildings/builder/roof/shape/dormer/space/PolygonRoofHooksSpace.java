/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space;

import java.util.ArrayList;
import java.util.List;

import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.RoofHookPoint;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRow;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LineLinear2d;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import org.ejml.simple.SimpleMatrix;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PolygonRoofHooksSpace implements RoofHooksSpace {
    Vector2dc p1;
    Vector2dc v1;
    double b;
    SimpleMatrix transformationMatrix;
    // FIXME

    Plane3d plane;
    private final List<List<Vector2dc>> polygon;

    public PolygonRoofHooksSpace(Vector2dc p1, Vector2dc v1, MultiPolygonList2d pPolygon, Plane3d pPlane) {
        this(p1, v1, toList(pPolygon), pPlane);
    }

    private static List<List<Vector2dc>> toList(MultiPolygonList2d pPolygon) {

        List<List<Vector2dc>> ret = new ArrayList<>();

        for (PolygonList2d polygon : pPolygon.getPolygons()) {

            List<Vector2dc> poly = polygon.getPoints();

            ret.add(poly);
        }
        return ret;
    }

    public PolygonRoofHooksSpace(Vector2dc p1, Vector2dc v1, List<List<Vector2dc>> pPolygon, Plane3d pPlane) {
        super();
        // this.p1 = p1;
        // this.v1 = v1;
        this.b = this.b;

        double angle = Math.atan2(v1.y(), v1.x());
        // Math.toDegrees(angle);
        SimpleMatrix tr2d = TransformationMatrix2d.rotZA(-angle).mult(TransformationMatrix2d.tranA(-p1.x(), -p1.y()));
        SimpleMatrix tr3d = TransformationMatrix3d.rotYA(-angle).mult(TransformationMatrix3d.tranA(-p1.x(), 0, p1.y()));

        this.p1 = TransformationMatrix2d.transform(p1, tr2d, true);
        this.v1 = TransformationMatrix2d.transform(v1, tr2d, false);

        List<List<Vector2dc>> transformPolygons = new ArrayList<>();
        for (List<Vector2dc> polygon : pPolygon) {
            List<Vector2dc> transformPolygon = new ArrayList<>();
            for (Vector2dc point : polygon) {
                transformPolygon.add(TransformationMatrix2d.transform(point, tr2d, true));
            }
            transformPolygons.add(transformPolygon);
        }
        this.polygon = transformPolygons;

        Vector3dc planePoint = TransformationMatrix3d.transform(pPlane.getPoint(), tr3d, true);
        Vector3dc planeNormal = TransformationMatrix3d.transform(pPlane.getNormal(), tr3d, false);

        this.plane = new Plane3d(planePoint, planeNormal);
        SimpleMatrix trBack = TransformationMatrix3d.tranA(p1.x(), 0, -p1.y()).mult(TransformationMatrix3d.rotYA(angle));
        // SimpleMatrix trBack =
        // TransformationMatrix3d.rotYA(angle).mult(TransformationMatrix3d.tranA(-p1.x(),
        // 0, p1.y()));
        // TransformationMatrix3d.transform(planePoint, trBack)
        this.transformationMatrix = trBack;
    }

    @Override
    public RoofHookPoint[] getRoofHookPoints(int pNumber, DormerRow dormerRow, int dormerRowNum) {
        Vector2d v = new Vector2d(this.v1).div(pNumber + 1d);

        MinMax polygonMinMaxY = findMinMaxY(this.polygon);
        if (polygonMinMaxY == null) {
            // XXX
            polygonMinMaxY = new MinMax(0, 1d);
        }

        Vector2d p = new Vector2d(this.p1);

        RoofHookPoint[] ret = new RoofHookPoint[pNumber];
        for (int i = 0; i < pNumber; i++) {
            p.add(v);

            MinMax minMaxY = limitZToPolygon(p.x());

            double z = calcRowPosition(minMaxY, dormerRow, dormerRowNum);

            // double z = minMaxY.getMin();

            double y = this.plane.calcYOfPlane(p.x(), -z);

            Vector3dc pp = new Vector3d(p.x(), y, -z);

            double b = minMaxY.getMax() - z;// (minMaxY.getMax() - minMaxY.getMin()) - z;

            RoofHookPoint hook = new RoofHookPoint(pp, Math.toRadians(0), b, Math.toRadians(0));

            ret[i] = hook;
        }

        return ret;

    }

    private double calcRowPosition(MinMax minMaxY, DormerRow dormerRow, int dormerRowNum) {
        double row = (dormerRow.getRowNum() - 1) / (double) dormerRowNum;
        return minMaxY.getMin() + (minMaxY.getMax() - minMaxY.getMin()) * row;
    }

    // /**
    // * @param y
    // * @return
    // */
    // private double limitZToMultiPolygons(double x) {
    // int size = this.polygon.size();
    //
    // double minY = 0;
    // double maxY = 0;
    //
    // for (List<Vector2dc> polygon : polygon) {
    // double limitZToPolygon = limitZToPolygon(x, polygon);
    //
    // xxx
    // }
    //
    //
    // }

    private MinMax findMinMaxY(List<List<Vector2dc>> pPolygon) {

        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        boolean empty = true;

        if (pPolygon == null) {
            return null;
        }

        for (List<Vector2dc> polygon : pPolygon) {

            int size = polygon.size();
            for (Vector2dc p1 : polygon) {
                empty = false;

                if (p1.y() < minY) {
                    minY = p1.y();
                }

                if (p1.y() > maxY) {
                    maxY = p1.y();
                }
            }
        }
        if (empty) {
            return null;
        }
        return new MinMax(minY, maxY);
    }

    static class MinMax {
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
         * @param min
         *            the min to set
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
         * @param max
         *            the max to set
         */
        public void setMax(double max) {
            this.max = max;
        }

    }

    /**
     * @param x
     * @return
     */
    private MinMax limitZToPolygon(double x) {

        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        boolean isMach = false;

        LineLinear2d parnel = new LineLinear2d(new Vector2d(x, 0), new Vector2d(x, 1));

        for (List<Vector2dc> polygon : this.polygon) {

            int size = polygon.size();
            for (int i = 0; i < size; i++) {
                Vector2dc p1 = polygon.get(i);
                Vector2dc p2 = polygon.get((i + 1) % size);

                if (Math.min(p1.x(), p2.x()) < x && Math.max(p1.x(), p2.x()) >= x) {

                    isMach = true;

                    LineLinear2d edge = new LineLinear2d(p1, p2);

                    Vector2dc collide = parnel.collide(edge);

                    if (collide.y() < minY) {
                        minY = collide.y();
                    }
                    if (collide.y() > maxY) {
                        maxY = collide.y();
                    }
                }
            }
        }

        // if (isMach) {
        return new MinMax(minY, maxY);
        // }
        // return null;
    }

    @Override
    public SimpleMatrix getTransformationMatrix() {
        return this.transformationMatrix;
    }

}
