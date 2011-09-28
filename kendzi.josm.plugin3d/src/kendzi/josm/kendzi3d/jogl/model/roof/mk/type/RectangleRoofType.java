/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.PolygonRoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.math.geometry.Graham;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.RectangleUtil;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;

import org.ejml.data.SimpleMatrix;

public abstract class RectangleRoofType extends AbstractRoofType implements RoofType {

    @Override
    public RoofTypeOutput buildRoof(Point2d pStartPoint, List<Point2d> border, Integer prefixParameter, double height,
            Map<MeasurementKey, Measurement> pMeasurements, RoofTextureData pRoofTextureData) {

        List<Point2d> graham = Graham.grahamScan(border);
        Point2d[] rectangleContur = RectangleUtil.findRectangleContur(graham);

        rectangleContur = findStartPoint(pStartPoint, rectangleContur);

        Point2d newStartPoint = rectangleContur[0];

        //XXX test me
        double alpha = Math.atan2(
                rectangleContur[1].y - rectangleContur[0].y,
                rectangleContur[1].x - rectangleContur[0].x);
        //XXX
        System.out.println(Math.toDegrees(alpha));

        boolean normalizeAB = normalizeAB();

        @Deprecated
        double scaleA = 1;
        @Deprecated
        double scaleB = 1;
        double recHeight = 1;
        double recWidth = 1;

        if (normalizeAB) {

            scaleA = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[1].x)
                    + pow(rectangleContur[0].y - rectangleContur[1].y));
            scaleB = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[3].x)
                    + pow(rectangleContur[0].y - rectangleContur[3].y));
        } else {

            recHeight = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[3].x)
                    + pow(rectangleContur[0].y - rectangleContur[3].y));
            recWidth = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[1].x)
                    + pow(rectangleContur[0].y - rectangleContur[1].y));
        }

        SimpleMatrix transformLocal = trandformToLocalMatrix(newStartPoint.x, newStartPoint.y, alpha, scaleA, scaleB);
        border = TransformationMatrix2d.transformList(border, transformLocal);
        rectangleContur = TransformationMatrix2d.transformArray(rectangleContur, transformLocal);


        RoofTypeOutput buildRectangleRoof = buildRectangleRoof(border, rectangleContur, scaleA, scaleB, recHeight, recWidth, prefixParameter,
                pMeasurements, pRoofTextureData);

        SimpleMatrix tr = transformToGlobalMatrix(newStartPoint, height - buildRectangleRoof.getHeight(), alpha, scaleA, scaleB);

        buildRectangleRoof.setTransformationMatrix(tr);

        List<Point3d> rect = new ArrayList<Point3d>();
        rect.add(new Point3d(rectangleContur[0].x, 0, -rectangleContur[0].y));
        rect.add(new Point3d(rectangleContur[1].x, 0, -rectangleContur[1].y));
        rect.add(new Point3d(rectangleContur[2].x, 0, -rectangleContur[2].y));
        rect.add(new Point3d(rectangleContur[3].x, 0, -rectangleContur[3].y));
        buildRectangleRoof.setRectangle(rect);

        return buildRectangleRoof;

    }



    /**
     * @param startPoint
     * @param height
     * @param alpha
     * @param scaleA
     * @param scaleB
     * @return
     */
    private SimpleMatrix transformToGlobalMatrix(Point2d startPoint, double height, double alpha, double scaleA,
            double scaleB) {
        SimpleMatrix scale = TransformationMatrix3d.scaleA(scaleA, 1, scaleB);
        SimpleMatrix transf = TransformationMatrix3d.tranA(
                startPoint.x, height, -startPoint.y);
        SimpleMatrix rot = TransformationMatrix3d.rotYA(alpha);
        //XXX test me
        SimpleMatrix tr = transf.mult(rot).mult(scale);
        return tr;
    }



    protected boolean normalizeAB() {
        return true;
    }

    /**
     * @param x
     * @param y
     * @param alpha
     * @param sizeA
     * @param sizeB
     * @return
     */
    private SimpleMatrix trandformToLocalMatrix(double x, double y, double alpha, double sizeA, double sizeB) {
        SimpleMatrix scaleLocal = TransformationMatrix2d.scaleA(1 / sizeA, 1 / sizeB);
        SimpleMatrix transfLocal = TransformationMatrix2d.tranA(
                -x, -y);
        SimpleMatrix rotLocal = TransformationMatrix2d.rotZA(-alpha);
        //XXX test me
        SimpleMatrix transformLocal = scaleLocal.mult(rotLocal).mult(transfLocal);
        return transformLocal;
    }


    private double pow(double d) {
        return d * d;
    }

    /**
     * @param border
     * @param rectangleContur
     * @param scaleA
     * @param scaleB
     * @param sizeB2
     * @param sizeA
     * @param prefixParameter
     * @param heights
     * @param sizesB
     * @param pRoofTextureData
     * @return
     */
    public abstract RoofTypeOutput buildRectangleRoof(List<Point2d> border, Point2d[] rectangleContur, double scaleA, double scaleB,
            double sizeA, double sizeB2, Integer prefixParameter, Map<MeasurementKey, Measurement> pMeasurements, RoofTextureData pRoofTextureData);

    private Point2d[] findStartPoint(Point2d pStartPoint, Point2d[] pRectangleContur) {
        int minI = 0;
        double minDist = pStartPoint.distanceSquared(pRectangleContur[0]);
        for (int i = 1; i < 4; i++) {
            double distance = pStartPoint.distanceSquared(pRectangleContur[i]);
            if (distance < minDist) {
                minDist = distance;
                minI = i;
            }
        }

        Point2d[] ret = new Point2d[4];
        for (int i = 0; i < 4; i++) {
            ret[i] = pRectangleContur[(i + minI) % 4];
        }
        return ret;
    }

    /**
     * @param pRectangleContur rectangle
     * @param pP1 1 polygon and plane defining height connected with rectangle edge 1
     * @param pP2 2 polygon and plane defining height connected with rectangle edge 2
     * @param pP3 3 polygon and plane defining height connected with rectangle edge 3
     * @param pP4 4 polygon and plane defining height connected with rectangle edge 4
     * @return rectangle roof hooks space
     */
    protected RoofHooksSpace [] buildRectRoofHooksSpace(
            Point2d[] pRectangleContur,
            PolygonPlane pP1,
            PolygonPlane pP2,
            PolygonPlane pP3,
            PolygonPlane pP4
            ) {

        List<RoofHooksSpace> ret = new ArrayList<RoofHooksSpace>();
        if (pP1 != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(0, pRectangleContur, pP1);
            ret.add(rrhs);
        }

        if (pP2 != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(1, pRectangleContur, pP2);
            ret.add(rrhs);
        }

        if (pP3 != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(2, pRectangleContur, pP3);
            ret.add(rrhs);
        }

        if (pP4 != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(3, pRectangleContur, pP4);
            ret.add(rrhs);
        }

        return ret.toArray(new RoofHooksSpace[ret.size()]);
    }

    /** Build roof hook space for rectangle edge.
     * @param pEdge rectangle edge number
     * @param pRectangleContur rectangle
     * @param pPolygonPlane polygon and plane defining height connected with rectangle edge
     * @return roof hook space
     */
    private PolygonRoofHooksSpace buildRecHookSpace(int pEdge, Point2d[] pRectangleContur, PolygonPlane pPolygonPlane) {



        Vector2d v1 = new Vector2d(pRectangleContur[(pEdge + 1) % 4]);
        v1.sub(pRectangleContur[pEdge]);

        return buildRecHookSpace(pRectangleContur[pEdge], v1, pPolygonPlane);
    }

    /** Build roof hook space for rectangle edge.
     * @param pEdge rectangle edge number XXX
     * @param pRectangleContur rectangle XXX
     * @param pPolygonPlane polygon and plane defining height connected with rectangle edge
     * @return roof hook space
     */
    public static PolygonRoofHooksSpace buildRecHookSpace(Point2d p1, Vector2d v1, PolygonPlane pPolygonPlane) {

        if (pPolygonPlane == null) {
            return null;
        }

        Plane3d plane = new Plane3d(pPolygonPlane.getPlane().getPoint(), pPolygonPlane.getPlane().getNormal());

        PolygonRoofHooksSpace rrhs1 = new PolygonRoofHooksSpace(
                        p1,
                        v1,
                        pPolygonPlane.getPolygon(),
                        plane);
        return rrhs1;
    }



}
