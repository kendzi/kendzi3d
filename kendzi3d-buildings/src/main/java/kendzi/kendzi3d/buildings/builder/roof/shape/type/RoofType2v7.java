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
import kendzi.kendzi3d.buildings.builder.height.MultiSplitHeightCalculator;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space.RectangleRoofHooksSpaces;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper.MultiPolygonSplitResult;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * Roof type 2.7.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType2v7 extends RectangleRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        boolean left = isLeft();

        double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);

        double l1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecWidth(),
                conf.getRecWidth() / 2d);
        double l2 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_2, conf.getRecHeight(),
                conf.getRecHeight() / 2d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, l1, l2,
                conf.getRoofTextureData(), left);
    }

    public boolean isLeft() {
        return true;
    }

    /**
     * @param buildingPolygon
     * @param recHeight
     * @param recWidth
     * @param rectangleContur
     * @param h1
     * @param l1
     * @param l2
     * @param roofTextureData
     * @param isLeft
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double recHeight, double recWidth,
            Vector2dc[] rectangleContur, double h1, double l1, double l2, RoofMaterials roofTextureData, boolean isLeft) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        Vector2dc rightTopPoint = new Vector2d(recWidth - l1, recHeight);
        Vector2dc rightBottomPoint = new Vector2d(recWidth, 0);

        Vector2dc leftTopPoint = new Vector2d(l2, recHeight);
        Vector2dc leftBottomPoint = new Vector2d(0, 0);

        if (!isLeft) {
            leftTopPoint = new Vector2d(-1, recHeight);
            leftBottomPoint = new Vector2d(-1, 0);
        }

        final LinePoints2d lLine = new LinePoints2d(leftBottomPoint, leftTopPoint);
        final LinePoints2d rLine = new LinePoints2d(rightTopPoint, rightBottomPoint);

        Vector3dc nl = new Vector3d(-h1, l2, 0).normalize();
        Vector3dc nr = new Vector3d(h1, l1, 0).normalize();
        Vector3dc nb = new Vector3d(0, recHeight, h1).normalize();

        List<Vector2dc> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        MultiPolygonSplitResult leftSplit = PolygonSplitHelper.splitMultiPolygon(new MultiPolygonList2d(borderPolygon), lLine);

        MultiPolygonList2d leftMP = leftSplit.getLeftMultiPolygon();
        MultiPolygonList2d middleMP = leftSplit.getRightMultiPolygon();

        MultiPolygonSplitResult rightSplit = PolygonSplitHelper.splitMultiPolygon(middleMP, rLine);

        MultiPolygonList2d rightMP = rightSplit.getLeftMultiPolygon();
        middleMP = rightSplit.getRightMultiPolygon();

        Vector3dc planeLeftPoint = new Vector3d(leftTopPoint.x(), h1, -leftTopPoint.y());

        Vector3dc planeRightPoint = new Vector3d(rightTopPoint.x(), h1, -rightTopPoint.y());

        final Plane3d planeLeft = new Plane3d(planeLeftPoint, nl);

        final Plane3d planeRight = new Plane3d(planeRightPoint, nr);

        final Plane3d planeBottom = new Plane3d(planeRightPoint, nb);

        Vector3dc roofLeftLineVector = new Vector3d(0, 0, recHeight);

        Vector3dc roofRightLineVector = new Vector3d(0, 0, -recHeight);

        Vector3dc roofButtomLineVector = new Vector3d(recWidth, 0, 0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, leftMP, planeLeft, roofLeftLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, rightMP, planeRight, roofRightLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, middleMP, planeBottom, roofButtomLineVector, roofTexture);

        HeightCalculator hc = new MultiSplitHeightCalculator() {
            @Override
            public double calcHeight(Vector2dc point) {
                return RoofType2v7.calcHeight(point, rLine, lLine, planeLeft, planeRight, planeBottom);
            }

            @Override
            public List<LinePoints2d> getSplittingLines() {
                return Arrays.asList(lLine, rLine);
            }
        };

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);
        rto.setHeightCalculator(hc);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs = buildRectRoofHooksSpace(rectangleContur, new PolygonPlane(middleMP, planeBottom),
                new PolygonPlane(rightMP, planeRight), null, isLeft ? new PolygonPlane(leftMP, planeLeft) : null);

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    /**
     * Calculates height of wall point under roof.
     * 
     * @param point
     *            the point
     * @param rLine
     *            the right split line
     * @param lLine
     *            the left split line
     * @param planeLeft
     *            the left plane
     * @param planeRight
     *            the right plane
     * @param planeButtom
     *            the bottom plane
     * @return height of point
     */
    private static double calcHeight(Vector2dc point, LinePoints2d rLine, LinePoints2d lLine, Plane3d planeLeft,
            Plane3d planeRight, Plane3d planeButtom) {

        double x = point.x();
        double z = -point.y();

        if (rLine.inFront(point)) {
            return planeRight.calcYOfPlane(x, z);

        } else if (lLine.inFront(point)) {

            return planeLeft.calcYOfPlane(x, z);

        } else {
            return planeButtom.calcYOfPlane(x, z);
        }

    }

}
