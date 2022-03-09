/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import static kendzi.kendzi3d.buildings.builder.roof.shape.type.RectangleRoofTransformationUtil.TransformedHeightCalculator;
import static kendzi.kendzi3d.buildings.builder.roof.shape.type.RectangleRoofTransformationUtil.prepareTransformMatrixToGlobal2d;
import static kendzi.kendzi3d.buildings.builder.roof.shape.type.RectangleRoofTransformationUtil.prepareTransformToGlobalMatrix3d;
import static kendzi.kendzi3d.buildings.builder.roof.shape.type.RectangleRoofTransformationUtil.trandformToLocalMatrix2d;
import static kendzi.kendzi3d.buildings.builder.roof.shape.type.RectangleRoofTransformationUtil.transformationPolygonWithHoles;

import java.util.ArrayList;
import java.util.List;

import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space.PolygonRoofHooksSpace;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space.RectangleRoofHooksSpaces;
import kendzi.kendzi3d.buildings.model.roof.RoofFrontDirection;
import kendzi.kendzi3d.buildings.model.roof.RoofOrientation;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRoofModel;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.RectangleUtil;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.rectangle.RectanglePointVector2d;
import org.ejml.simple.SimpleMatrix;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RectangleRoofTypeBuilder extends AbstractRoofTypeBuilder implements RoofTypeBuilder {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(RectangleRoofTypeBuilder.class);

    @Override
    public RoofTypeOutput buildRoof(Vector2dc startPoint, PolygonWithHolesList2d buildingPolygon, DormerRoofModel roof,
            double height, RoofMaterials roofTextureData) {

        Vector2dc[] rectangleContur = null;

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
                Vector2dc alignedDirection = RectangleTypeRoofUtil.snapsDirectionToOutline(direction.getDirection(),
                        outerPolygon);
                rectangleContur = calcRectangle(outerPolygon.getPoints(), alignedDirection);

            } else {
                rectangleContur = calcRectangle(outerPolygon.getPoints(), direction.getDirection());
            }
        }

        Vector2dc newStartPoint = rectangleContur[0];

        // XXX test me
        double alpha = Math.atan2(rectangleContur[1].y() - rectangleContur[0].y(),
                rectangleContur[1].x() - rectangleContur[0].x());

        double recHeight = Math.sqrt(
                pow(rectangleContur[0].x() - rectangleContur[3].x()) + pow(rectangleContur[0].y() - rectangleContur[3].y()));
        double recWidth = Math.sqrt(
                pow(rectangleContur[0].x() - rectangleContur[1].x()) + pow(rectangleContur[0].y() - rectangleContur[1].y()));

        SimpleMatrix transformToLocal = trandformToLocalMatrix2d(newStartPoint.x(), newStartPoint.y(), alpha);

        PolygonWithHolesList2d transfBuildingPolygon = transformationPolygonWithHoles(buildingPolygon, transformToLocal);
        rectangleContur = TransformationMatrix2d.transformArray(rectangleContur, transformToLocal);

        RectangleRoofTypeConf conf = new RectangleRoofTypeConf(transfBuildingPolygon, rectangleContur, recHeight, recWidth,
                roof.getRoofTypeParameter(), roof.getMeasurements(), roofTextureData);

        RoofTypeOutput buildRectangleRoof = buildRectangleRoof(conf);

        double roofMinHeight = height - buildRectangleRoof.getHeight();

        buildRectangleRoof.setTransformationMatrix(prepareTransformToGlobalMatrix3d(newStartPoint, roofMinHeight, alpha));

        buildRectangleRoof.setRectangle(createRectangle(rectangleContur));

        if (buildRectangleRoof.getHeightCalculator() != null) {
            SimpleMatrix toGlobalTransformation2d = prepareTransformMatrixToGlobal2d(newStartPoint, alpha);

            buildRectangleRoof.setHeightCalculator(new TransformedHeightCalculator(buildRectangleRoof.getHeightCalculator(),
                    toGlobalTransformation2d, roofMinHeight, transformToLocal));
        }

        return buildRectangleRoof;

    }

    public List<Vector3dc> createRectangle(Vector2dc[] rectangleContur) {
        List<Vector3dc> rect = new ArrayList<>();
        rect.add(new Vector3d(rectangleContur[0].x(), 0, -rectangleContur[0].y()));
        rect.add(new Vector3d(rectangleContur[1].x(), 0, -rectangleContur[1].y()));
        rect.add(new Vector3d(rectangleContur[2].x(), 0, -rectangleContur[2].y()));
        rect.add(new Vector3d(rectangleContur[3].x(), 0, -rectangleContur[3].y()));
        return rect;
    }

    private Vector2dc[] calcRectangle(List<Vector2dc> polygon, Vector2dc direction) {

        RectanglePointVector2d contur = RectangleUtil.findRectangleContur(polygon, direction);

        return rectToList(contur);
    }

    /**
     * @param contur
     * @return
     */
    public Vector2dc[] rectToList(RectanglePointVector2d contur) {
        Vector2dc p1 = contur.getPoint();
        Vector2dc p2 = new Vector2d(contur.getVector()).mul(contur.getWidth()).add(contur.getPoint());

        Vector2dc ort = new Vector2d(-contur.getVector().y() * contur.getHeight(), contur.getVector().x() * contur.getHeight());

        Vector2dc p3 = new Vector2d(p2).add(ort);

        Vector2dc p4 = new Vector2d(p1).add(ort);

        Vector2dc[] ret = new Vector2dc[4];
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

    private Vector2dc[] findStartPoint(Vector2dc startPoint, Vector2dc[] rectangleContur) {
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
    public Vector2dc[] swapRectangle(Vector2dc[] pRectangleContur, int pSwap) {
        Vector2dc[] ret = new Vector2dc[4];
        for (int i = 0; i < 4; i++) {
            ret[i] = pRectangleContur[(i + pSwap) % 4];
        }
        return ret;
    }

    private Vector2dc[] findOrientation(RoofOrientation orientation, Vector2dc[] pRectangleContur) {
        if (orientation == null) {
            return pRectangleContur;
        }

        Vector2dc p1 = pRectangleContur[0];
        Vector2dc p2 = pRectangleContur[1];
        Vector2dc p3 = pRectangleContur[2];

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
     *            1 polygon and plane defining height connected with rectangle edge
     *            1
     * @param leftPlane
     *            2 polygon and plane defining height connected with rectangle edge
     *            2
     * @param backPlane
     *            3 polygon and plane defining height connected with rectangle edge
     *            3
     * @param rightPlane
     *            4 polygon and plane defining height connected with rectangle edge
     *            4
     * @return rectangle roof hooks space
     */
    protected RectangleRoofHooksSpaces buildRectRoofHooksSpace(Vector2dc[] rectangleContur, PolygonPlane frontPlane,
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
     *            polygon and plane defining height connected with rectangle edge
     * @return roof hook space
     */
    private PolygonRoofHooksSpace buildRecHookSpace(int edge, Vector2dc[] rectangleContur, PolygonPlane polygonPlane) {

        Vector2dc v1 = new Vector2d(rectangleContur[(edge + 1) % 4]).sub(rectangleContur[edge]);

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
     *            polygon and plane defining height connected with rectangle edge
     * @return roof hook space
     */
    public static PolygonRoofHooksSpace buildRecHookSpace(Vector2dc p1, Vector2dc v1, PolygonPlane pPolygonPlane) {

        if (pPolygonPlane == null) {
            return null;
        }

        Plane3d plane = new Plane3d(pPolygonPlane.getPlane().getPoint(), pPolygonPlane.getPlane().getNormal());

        return new PolygonRoofHooksSpace(p1, v1, pPolygonPlane.getPolygon(), plane);
    }

}
