/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.height.SingleSplitHeightCalculator;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space.RectangleRoofHooksSpaces;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonUtil;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper.MultiPolygonSplitResult;

/**
 * Roof type 2.0.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType2v0 extends RectangleRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        double l1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecHeight(),
                conf.getRecHeight() / 2d);

        double h1 = getHeightDegreesMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 0, l1, 30);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, l1,
                conf.getRoofTextureData());
    }

    /**
     * @param buildingPolygon
     * @param recHeight
     * @param recWidth
     * @param rectangleContur
     * @param roofHeight
     * @param l1
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double recHeight, double recWidth,
            Point2d[] rectangleContur, double roofHeight, double l1, RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        Point2d leftMiddlePoint = new Point2d(0, l1);
        Point2d rightMiddlePoint = new Point2d(recWidth, l1);

        final LinePoints2d middleRoofLine = new LinePoints2d(leftMiddlePoint, rightMiddlePoint);

        Vector3d normalTop = new Vector3d(0, l1, -roofHeight);
        normalTop.normalize();

        Vector3d normalBottom = new Vector3d(0, l1, roofHeight);
        normalBottom.normalize();

        List<Point2d> outline = PolygonUtil.makeCounterClockwise(buildingPolygon.getOuter().getPoints());
        PolygonList2d borderPolygon = new PolygonList2d(outline);

        MultiPolygonSplitResult middleSplit = PolygonSplitHelper.splitMultiPolygon(new MultiPolygonList2d(borderPolygon),
                middleRoofLine);

        MultiPolygonList2d topMP = middleSplit.getLeftMultiPolygon();
        MultiPolygonList2d bottomMP = middleSplit.getRightMultiPolygon();

        Point3d planeLeftPoint = new Point3d(leftMiddlePoint.x, roofHeight, -leftMiddlePoint.y);
        Point3d planeRightPoint = new Point3d(rightMiddlePoint.x, roofHeight, -rightMiddlePoint.y);

        final Plane3d planeTop = new Plane3d(planeRightPoint, normalTop);
        final Plane3d planeBottom = new Plane3d(planeLeftPoint, normalBottom);

        Vector3d roofBottomLineVector = new Vector3d(recWidth, 0, 0);
        Vector3d roofTopLineVector = new Vector3d(-recWidth, 0, 0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);

        HeightCalculator hc = new SingleSplitHeightCalculator(middleRoofLine, planeBottom, planeTop);

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(roofHeight);
        rto.setHeightCalculator(hc);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs = buildRectRoofHooksSpace(rectangleContur, new PolygonPlane(bottomMP, planeBottom), null,
                new PolygonPlane(topMP, planeTop), null);

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

}
