/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.Arrays;
import java.util.List;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.height.SingleSplitHeightCalculator;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.line.LineUtil;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonUtil;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper.MultiPolygonSplitResult;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * Roof type 2.1.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType2v1 extends RectangleRoofTypeBuilder {

    private static final double EPSILON = 1e-10;

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 1.5d);

        double h2 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_2, 2.5d);

        double b1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecHeight(),
                conf.getRecHeight() / 2d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, h2, b1,
                conf.getRoofTextureData());
    }

    /**
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
     * @param recHeight
     * @param recWidth
     * @param rectangleContur
     * @param roofHeightBottom
     * @param roofHeightTop
     * @param l1
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double recHeight, double recWidth,
            Vector2dc[] rectangleContur, double roofHeightBottom, double roofHeightTop, double l1,
            RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        double roofLineDistanceBottom = l1;
        double roofLineDistanceTop = recHeight - roofLineDistanceBottom;

        Vector2dc leftMiddlePoint = new Vector2d(0, l1);
        Vector2dc rightMiddlePoint = new Vector2d(recWidth, l1);

        final LinePoints2d middleRoofLine = new LinePoints2d(leftMiddlePoint, rightMiddlePoint);

        Vector3dc normalTop = new Vector3d(0, roofLineDistanceTop, -roofHeightTop).normalize();

        Vector3dc normalBottom = new Vector3d(0, roofLineDistanceBottom, roofHeightBottom).normalize();

        List<Vector2dc> outline = PolygonUtil.makeCounterClockwise(buildingPolygon.getOuter().getPoints());
        PolygonList2d borderPolygon = new PolygonList2d(outline);

        MultiPolygonSplitResult middleSplit = PolygonSplitHelper.splitMultiPolygon(new MultiPolygonList2d(borderPolygon),
                middleRoofLine);

        MultiPolygonList2d topMP = middleSplit.getLeftMultiPolygon();
        MultiPolygonList2d bottomMP = middleSplit.getRightMultiPolygon();

        Vector3dc planeLeftPoint = new Vector3d(leftMiddlePoint.x(), roofHeightBottom, -leftMiddlePoint.y());
        Vector3dc planeRightPoint_ZZZ = new Vector3d(rightMiddlePoint.x(), roofHeightTop, -rightMiddlePoint.y());

        final Plane3d planeTop = new Plane3d(planeRightPoint_ZZZ, normalTop);
        final Plane3d planeBottom = new Plane3d(planeLeftPoint, normalBottom);

        Vector3dc roofBottomLineVector = new Vector3d(recWidth, 0, 0);
        Vector3dc roofTopLineVector = new Vector3d(-recWidth, 0, 0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);

        HeightCalculator hc = new SingleSplitHeightCalculator(middleRoofLine, planeBottom, planeTop);

        // FIXME it is middle wall missing! It have to be fixed!

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(roofHeightBottom, roofHeightTop));
        rto.setHeightCalculator(hc);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));
        rto.setRoofHooksSpaces(null);

        return rto;
    }

    /**
     * Calculate height of point in border.
     *
     * @param point
     * @param mLine
     * @param planeTop
     * @param planeBottom
     * @return
     */
    private double calcHeight(Vector2dc point, Plane3d plane) {

        double x = point.x();
        double z = -point.y();

        return plane.calcYOfPlane(x, z);

    }

    boolean isSegmentInFrontOfLine(Vector2dc begin, Vector2dc end, LinePoints2d line) {

        double beginDet = LineUtil.matrixDet(line.getP1(), line.getP2(), begin);
        double endDet = LineUtil.matrixDet(line.getP1(), line.getP2(), end);

        if (equalZero(beginDet, EPSILON)) {
            beginDet = 0;
        }

        if (equalZero(endDet, EPSILON)) {
            endDet = 0;
        }

        if (beginDet > 0 && (endDet >= 0)) {
            return true;
        }
        return endDet > 0 && (beginDet >= 0);
    }

    private static boolean equalZero(double number, double epsilon) {
        return number * number < epsilon;
    }

}
