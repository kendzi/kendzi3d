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
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.PolygonRoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RectangleRoofHooksSpaces;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoof;
import kendzi.math.geometry.Graham;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.RectangleUtil;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.rectangle.RectanglePointVector2d;

import org.ejml.data.SimpleMatrix;

public abstract class RectangleRoofType extends AbstractRoofType implements RoofType {

    @Override
    public RoofTypeOutput buildRoof(
            Point2d pStartPoint, List<Point2d> pPolygon, DormerRoof pRoof, double height, RoofTextureData pRoofTextureData) {

//            Point2d pStartPoint, List<Point2d> border, Integer prefixParameter, double height,
//            Map<MeasurementKey, Measurement> pMeasurements, RoofTextureData pRoofTextureData) {


        /**/
        List<Point2d> graham = Graham.grahamScan(pPolygon);
        Point2d[] rectangleContur = RectangleUtil.findRectangleContur(graham);

        rectangleContur = findStartPoint(pStartPoint, rectangleContur);

        /**/

        if (pRoof.getDirection() != null) {
            rectangleContur = calcRectangle(pPolygon, pRoof.getDirection());
        }

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
        pPolygon = TransformationMatrix2d.transformList(pPolygon, transformLocal);
        rectangleContur = TransformationMatrix2d.transformArray(rectangleContur, transformLocal);


        RoofTypeOutput buildRectangleRoof = buildRectangleRoof(pPolygon, rectangleContur, scaleA, scaleB, recHeight, recWidth, pRoof.getRoofTypeParameter(),
                pRoof.getMeasurements(), pRoofTextureData);

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



    private Point2d[] calcRectangle(List<Point2d> pPolygon, Vector2d pDirection) {
        RectanglePointVector2d contur = RectangleUtil.findRectangleContur(new PolygonList2d(pPolygon),  pDirection);


        Point2d p1 = contur.getPoint();
        Point2d p2 = new Point2d(contur.getVector());
        p2.scaleAdd(contur.getWidth(), contur.getPoint());

        Vector2d ort = new Vector2d(-contur.getVector().y * contur.getHeight(), contur.getVector().x * contur.getHeight());

        Point2d p3 = new Point2d(p2);
        p3.add(ort);

        Point2d p4 = new Point2d(p1);
        p4.add(ort);


        Point2d[] ret = new Point2d[4];
        ret[0] = p1;
        ret[1] = p2;
        ret[2] = p3;
        ret[3] = p4;

        return ret;
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
     * @param pFrontPlane 1 polygon and plane defining height connected with rectangle edge 1
     * @param pLeftPlane 2 polygon and plane defining height connected with rectangle edge 2
     * @param pBackPlane 3 polygon and plane defining height connected with rectangle edge 3
     * @param pRightPlane 4 polygon and plane defining height connected with rectangle edge 4
     * @return rectangle roof hooks space
     */
    protected RectangleRoofHooksSpaces buildRectRoofHooksSpace(
            Point2d[] pRectangleContur,
            PolygonPlane pFrontPlane,
            PolygonPlane pLeftPlane,
            PolygonPlane pBackPlane,
            PolygonPlane pRightPlane
            ) {

        RectangleRoofHooksSpaces ret = new RectangleRoofHooksSpaces();

//        List<RoofHooksSpace> ret = new ArrayList<RoofHooksSpace>();
        if (pFrontPlane != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(0, pRectangleContur, pFrontPlane);
            ret.setFrontSpace(rrhs);
        }

        if (pLeftPlane != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(1, pRectangleContur, pLeftPlane);
            ret.setRightSpace(rrhs);
        }

        if (pBackPlane != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(2, pRectangleContur, pBackPlane);
            ret.setBackSpace(rrhs);
        }

        if (pRightPlane != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(3, pRectangleContur, pRightPlane);
            ret.setLeftSpace(rrhs);
        }

        return ret;
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
