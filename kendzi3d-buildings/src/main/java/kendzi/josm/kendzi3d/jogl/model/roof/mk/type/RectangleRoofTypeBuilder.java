/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import static kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RectangleRoofTransformationUtil.prepareTransformMatrixToGlobal2d;
import static kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RectangleRoofTransformationUtil.prepareTransformToGlobalMatrix3d;
import static kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RectangleRoofTransformationUtil.trandformToLocalMatrix2d;
import static kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RectangleRoofTransformationUtil.transformationPolygonWithHoles;

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
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.RoofFrontDirection;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RectangleRoofTransformationUtil.TransformedHeightCalculator;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.RectangleUtil;
import kendzi.math.geometry.point.TransformationMatrix2d;
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
    public RoofTypeOutput buildRoof(Point2d startPoint, PolygonWithHolesList2d buildingPolygon, DormerRoofModel roof,
            double height, RoofMaterials roofTextureData) {

        Point2d[] rectangleContur = null;

        RoofFrontDirection direction = roof.getDirection();
        if (direction == null) {

            PolygonList2d outerPolygon = buildingPolygon.getOuter();

            rectangleContur = rectToList(RectangleUtil.findRectangleContur(outerPolygon.getPoints()));
            rectangleContur = findStartPoint(startPoint, rectangleContur);

            if (roof.getOrientation() == null) {
                rectangleContur = findOrientation(RoofOrientation.along, rectangleContur);
            } else {
                rectangleContur = findOrientation(roof.getOrientation(), rectangleContur);
            }

            /**/

        } else {
            PolygonList2d outerPolygon = buildingPolygon.getOuter();

            if (direction.isSoft()) {
                Vector2d alignedDirection = RectangleTypeRoofUtil.snapsDirectionToOutline(direction.getDirection(),
                        outerPolygon);
                rectangleContur = calcRectangle(outerPolygon.getPoints(), alignedDirection);

            } else {
                rectangleContur = calcRectangle(outerPolygon.getPoints(), direction.getDirection());
            }
        }

        Point2d newStartPoint = rectangleContur[0];

        // XXX test me
        double alpha = Math.atan2(rectangleContur[1].y - rectangleContur[0].y, rectangleContur[1].x
                - rectangleContur[0].x);

        double recHeight = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[3].x)
                + pow(rectangleContur[0].y - rectangleContur[3].y));
        double recWidth = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[1].x)
                + pow(rectangleContur[0].y - rectangleContur[1].y));

        SimpleMatrix transformToLocal = trandformToLocalMatrix2d(newStartPoint.x, newStartPoint.y, alpha);

        PolygonWithHolesList2d transfBuildingPolygon = transformationPolygonWithHoles(buildingPolygon, transformToLocal);
        rectangleContur = TransformationMatrix2d.transformArray(rectangleContur, transformToLocal);

        RectangleRoofTypeConf conf = new RectangleRoofTypeConf(transfBuildingPolygon, rectangleContur, recHeight,
                recWidth, roof.getRoofTypeParameter(), roof.getMeasurements(), roofTextureData);

        RoofTypeOutput buildRectangleRoof = buildRectangleRoof(conf);

        double roofMinHeight = height - buildRectangleRoof.getHeight();

        buildRectangleRoof
                .setTransformationMatrix(prepareTransformToGlobalMatrix3d(newStartPoint, roofMinHeight, alpha));

        buildRectangleRoof.setRectangle(createRectangle(rectangleContur));

        if (buildRectangleRoof.getHeightCalculator() != null) {
            SimpleMatrix toGlobalTransformation2d = prepareTransformMatrixToGlobal2d(newStartPoint, alpha);

            buildRectangleRoof.setHeightCalculator(new TransformedHeightCalculator(buildRectangleRoof
                    .getHeightCalculator(), toGlobalTransformation2d, roofMinHeight, transformToLocal));
        }

        return buildRectangleRoof;

    }

    public List<Point3d> createRectangle(Point2d[] rectangleContur) {
        List<Point3d> rect = new ArrayList<Point3d>();
        rect.add(new Point3d(rectangleContur[0].x, 0, -rectangleContur[0].y));
        rect.add(new Point3d(rectangleContur[1].x, 0, -rectangleContur[1].y));
        rect.add(new Point3d(rectangleContur[2].x, 0, -rectangleContur[2].y));
        rect.add(new Point3d(rectangleContur[3].x, 0, -rectangleContur[3].y));
        return rect;
    }

    private Point2d[] calcRectangle(List<Point2d> polygon, Vector2d direction) {

        RectanglePointVector2d contur = RectangleUtil.findRectangleContur(polygon, direction);

        return rectToList(contur);
    }

    /**
     * @param contur
     * @return
     */
    public Point2d[] rectToList(RectanglePointVector2d contur) {
        Point2d p1 = contur.getPoint();
        Point2d p2 = new Point2d(contur.getVector());
        p2.scaleAdd(contur.getWidth(), contur.getPoint());

        Vector2d ort = new Vector2d(-contur.getVector().y * contur.getHeight(), contur.getVector().x
                * contur.getHeight());

        Point2d p3 = new Point2d(p2);
        p3.add(ort);

        Point2d p4 = new Point2d(p1);
        p4.add(ort);

        Point2d[] ret = new Point2d[4];
        ret[0] = p2;
        ret[1] = p3;
        ret[2] = p4;
        ret[3] = p1;

        return ret;
    }

    private double pow(double d) {
        return d * d;
    }

    /**
     * @param conf
     *            builder configuration
     * @return build roof
     */
    public abstract RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf);

    private Point2d[] findStartPoint(Point2d startPoint, Point2d[] rectangleContur) {
        int minI = 0;
        double minDist = startPoint.distanceSquared(rectangleContur[0]);
        for (int i = 1; i < 4; i++) {
            double distance = startPoint.distanceSquared(rectangleContur[i]);
            if (distance < minDist) {
                minDist = distance;
                minI = i;
            }
        }

        return swapRectangle(rectangleContur, minI);
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
     * @param rectangleContur
     *            rectangle
     * @param frontPlane
     *            1 polygon and plane defining height connected with rectangle
     *            edge 1
     * @param leftPlane
     *            2 polygon and plane defining height connected with rectangle
     *            edge 2
     * @param backPlane
     *            3 polygon and plane defining height connected with rectangle
     *            edge 3
     * @param rightPlane
     *            4 polygon and plane defining height connected with rectangle
     *            edge 4
     * @return rectangle roof hooks space
     */
    protected RectangleRoofHooksSpaces buildRectRoofHooksSpace(Point2d[] rectangleContur, PolygonPlane frontPlane,
            PolygonPlane leftPlane, PolygonPlane backPlane, PolygonPlane rightPlane) {

        RectangleRoofHooksSpaces ret = new RectangleRoofHooksSpaces();

        if (frontPlane != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(0, rectangleContur, frontPlane);
            ret.setFrontSpace(rrhs);
        }

        if (leftPlane != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(1, rectangleContur, leftPlane);
            ret.setRightSpace(rrhs);
        }

        if (backPlane != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(2, rectangleContur, backPlane);
            ret.setBackSpace(rrhs);
        }

        if (rightPlane != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(3, rectangleContur, rightPlane);
            ret.setLeftSpace(rrhs);
        }

        return ret;
    }

    /**
     * Build roof hook space for rectangle edge.
     *
     * @param edge
     *            rectangle edge number
     * @param rectangleContur
     *            rectangle
     * @param polygonPlane
     *            polygon and plane defining height connected with rectangle
     *            edge
     * @return roof hook space
     */
    private PolygonRoofHooksSpace buildRecHookSpace(int edge, Point2d[] rectangleContur, PolygonPlane polygonPlane) {

        Vector2d v1 = new Vector2d(rectangleContur[(edge + 1) % 4]);
        v1.sub(rectangleContur[edge]);

        return buildRecHookSpace(rectangleContur[edge], v1, polygonPlane);
    }

    /**
     * Build roof hook space for rectangle edge.
     *
     * @param pEdge
     *            rectangle edge number XXX
     * @param pRectangleContur
     *            rectangle XXX
     * @param pPolygonPlane
     *            polygon and plane defining height connected with rectangle
     *            edge
     * @return roof hook space
     */
    public static PolygonRoofHooksSpace buildRecHookSpace(Point2d p1, Vector2d v1, PolygonPlane pPolygonPlane) {

        if (pPolygonPlane == null) {
            return null;
        }

        Plane3d plane = new Plane3d(pPolygonPlane.getPlane().getPoint(), pPolygonPlane.getPlane().getNormal());

        return new PolygonRoofHooksSpace(p1, v1, pPolygonPlane.getPolygon(), plane);
    }

}
