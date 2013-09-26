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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import kendzi.josm.kendzi3d.jogl.model.building.model.roof.RoofOrientation;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.PolygonRoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RectangleRoofHooksSpaces;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.RoofDirection;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.RectangleUtil;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.rectangle.RectanglePointVector2d;

import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

public abstract class RectangleRoofTypeBuilder extends AbstractRoofTypeBuilder implements RoofTypeBuilder {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RectangleRoofTypeBuilder.class);

    @Override
    public RoofTypeOutput buildRoof(
            Point2d pStartPoint, PolygonWithHolesList2d buildingPolygon, DormerRoofModel roof, double height, RoofMaterials roofTextureData) {

        Point2d[] rectangleContur = null;

        RoofDirection direction = roof.getDirection();
        if (direction == null) {

            PolygonList2d outerPolygon = buildingPolygon.getOuter();

            rectangleContur = rectToList(RectangleUtil.findRectangleContur(outerPolygon.getPoints()));
            rectangleContur = findStartPoint(pStartPoint, rectangleContur);

            if (roof.getOrientation() == null) {
                //
            } else {
                // TODO
                rectangleContur = findOrientation(roof.getOrientation(), rectangleContur);

            }

            /**/

        } else {
            PolygonList2d outerPolygon = buildingPolygon.getOuter();

            if (direction.isSoft()) {
                Vector2d alignedDirection = alignedDirectionToOutline(roof.getDirection().getDirection(), outerPolygon);
                rectangleContur = calcRectangle(outerPolygon.getPoints(), alignedDirection);

            } else {
                rectangleContur = calcRectangle(outerPolygon.getPoints(), roof.getDirection().getDirection());
            }
        }

        Point2d newStartPoint = rectangleContur[0];

        //XXX test me
        double alpha = Math.atan2(
                rectangleContur[1].y - rectangleContur[0].y,
                rectangleContur[1].x - rectangleContur[0].x);

        double recHeight = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[3].x)
                + pow(rectangleContur[0].y - rectangleContur[3].y));
        double recWidth = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[1].x)
                + pow(rectangleContur[0].y - rectangleContur[1].y));


        SimpleMatrix transformLocal = trandformToLocalMatrix(newStartPoint.x, newStartPoint.y, alpha, 1d, 1d);
        //        pPolygon = TransformationMatrix2d.transformList(pPolygon, transformLocal);
        PolygonWithHolesList2d transfBuildingPolygon = transformationPolygonWithHoles(buildingPolygon, transformLocal);
        rectangleContur = TransformationMatrix2d.transformArray(rectangleContur, transformLocal);


        RectangleRoofTypeConf conf = new RectangleRoofTypeConf(transfBuildingPolygon, rectangleContur, recHeight,
                recWidth, roof.getRoofTypeParameter(), roof.getMeasurements(), roofTextureData);

        RoofTypeOutput buildRectangleRoof = buildRectangleRoof(conf);

        SimpleMatrix tr = transformToGlobalMatrix(newStartPoint, height - buildRectangleRoof.getHeight(), alpha, 1d, 1d);

        buildRectangleRoof.setTransformationMatrix(tr);

        buildRectangleRoof.setRectangle(createRectangle(rectangleContur));

        return buildRectangleRoof;

    }

    private Vector2d alignedDirectionToOutline(Vector2d direction, PolygonList2d outerPolygon) {

        List<Point2d> points = outerPolygon.getPoints();

        double maxD = -Double.MAX_VALUE;
        Vector2d maxV = null;

        Point2d end = points.get(points.size() - 1);
        for (Point2d begin : points) {
            Vector2d v = new Vector2d(end);
            v.sub(begin);
            v.normalize();

            double d = v.dot(direction);
            if (d > maxD) {
                maxD = d;
                maxV = v;
            }

            v.negate();
            d = v.dot(direction);
            if (d > maxD) {
                maxD = d;
                v.negate();
                maxV = v;
            }

            end = begin;
        }
        return maxV;
    }

    /**
     * @param rectangleContur
     * @return
     */
    public List<Point3d> createRectangle(Point2d[] rectangleContur) {
        List<Point3d> rect = new ArrayList<Point3d>();
        rect.add(new Point3d(rectangleContur[0].x, 0, -rectangleContur[0].y));
        rect.add(new Point3d(rectangleContur[1].x, 0, -rectangleContur[1].y));
        rect.add(new Point3d(rectangleContur[2].x, 0, -rectangleContur[2].y));
        rect.add(new Point3d(rectangleContur[3].x, 0, -rectangleContur[3].y));
        return rect;
    }

    private PolygonWithHolesList2d transformationPolygonWithHoles(PolygonWithHolesList2d polygonWithHoles,
            SimpleMatrix transformLocal) {

        PolygonList2d outer = transformPolygon(polygonWithHoles.getOuter(), transformLocal);

        List<PolygonList2d> inner = new ArrayList<PolygonList2d>();

        if (polygonWithHoles.getInner() != null) {
            for (PolygonList2d pi : polygonWithHoles.getInner()) {
                inner.add(transformPolygon(pi, transformLocal));
            }
        }
        return new PolygonWithHolesList2d(outer, inner);
    }

    PolygonList2d transformPolygon(PolygonList2d polygon, SimpleMatrix transformLocal) {
        return new PolygonList2d(TransformationMatrix2d.transformList(polygon.getPoints(), transformLocal));
    }

    private Point2d[] calcRectangle(List<Point2d> pPolygon, Vector2d pDirection) {

        RectanglePointVector2d contur = RectangleUtil.findRectangleContur(pPolygon,  pDirection);

        Point2d[] ret = rectToList(contur);

        return ret;
    }

    /**
     * @param contur
     * @return
     */
    public Point2d[] rectToList(RectanglePointVector2d contur) {
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
     * @param conf builder configuration
     * @return build roof
     */
    public abstract RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf);

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

        return swapRectangle(pRectangleContur, minI);
    }

    /**
     * @param pRectangleContur
     * @param pSwap
     * @return
     */
    public Point2d[] swapRectangle(Point2d[] pRectangleContur, int pSwap) {
        Point2d[] ret = new Point2d[4];
        for (int i = 0; i < 4; i++) {
            ret[i] = pRectangleContur[(i + pSwap) % 4];
        }
        return ret;
    }

    private Point2d[] findOrientation(RoofOrientation orientation, Point2d[] pRectangleContur) {
        if (orientation == null) {
            return pRectangleContur;
        }

        Point2d p1 = pRectangleContur[0];
        Point2d p2 = pRectangleContur[1];
        Point2d p3 = pRectangleContur[2];

        boolean along = p1.distanceSquared(p2) > p2.distanceSquared(p3);

        if (RoofOrientation.along.equals(orientation)) {
            if (!along) {
                return swapRectangle(pRectangleContur, 1);
            }
        } else if (RoofOrientation.across.equals(orientation)) {
            if (along) {
                return swapRectangle(pRectangleContur, 1);
            }
        }
        return pRectangleContur;
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
