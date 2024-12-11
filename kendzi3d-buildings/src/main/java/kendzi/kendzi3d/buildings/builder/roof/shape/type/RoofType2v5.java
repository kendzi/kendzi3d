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
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * Roof type 2.5.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType2v5 extends RectangleRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);

        double b1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecWidth(),
                conf.getRecWidth() / 2d);
        double b2 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_2, conf.getRecHeight(),
                conf.getRecHeight() / 2d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, b1, b2,
                conf.getRoofTextureData());
    }

    /**
     * 
     * <img src="doc-files/RoofType2_5.png">
     * 
     * @param buildingPolygon
     * 
     * @param recHeight
     * @param recWidth
     * @param rectangleContur
     * @param h1
     * @param b1
     * @param b2
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double recHeight, double recWidth,
            Vector2dc[] rectangleContur, double h1, double b1, double b2, RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        Vector2dc middlePoint = new Vector2d(b1, b2);
        Vector2dc plb = new Vector2d(0, 0);
        Vector2dc plt = new Vector2d(0, recHeight);
        Vector2dc prb = new Vector2d(recWidth, 0);
        Vector2dc prt = new Vector2d(recWidth, recHeight);

        final LinePoints2d ltLine = new LinePoints2d(plt, middlePoint);
        final LinePoints2d lbLine = new LinePoints2d(plb, middlePoint);
        final LinePoints2d rtLine = new LinePoints2d(middlePoint, prt);
        final LinePoints2d rbLine = new LinePoints2d(middlePoint, prb);

        Vector3dc nl = new Vector3d(-h1, b1, 0).normalize();
        Vector3dc nr = new Vector3d(h1, recWidth - b1, 0).normalize();

        Vector3dc nt = new Vector3d(0, recHeight - b2, -h1).normalize();
        Vector3dc nb = new Vector3d(0, b2, h1).normalize();

        Vector3dc planePoint = new Vector3d(middlePoint.x(), h1, -middlePoint.y());

        List<Vector2dc> pBorderList = buildingPolygon.getOuter().getPoints();

        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);
        MultiPolygonList2d borderMultiPolygon = new MultiPolygonList2d(borderPolygon);

        MultiPolygonList2d mpb = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, plb, prb, middlePoint, plb);
        MultiPolygonList2d mpt = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, plt, middlePoint, prt, plt);
        MultiPolygonList2d mpl = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, plb, middlePoint, plt, plb);
        MultiPolygonList2d mpr = intersectionOfLeftSideOfMultipleCuts(borderMultiPolygon, middlePoint, prb, prt, middlePoint);

        final Plane3d planeLeft = new Plane3d(planePoint, nl);

        final Plane3d planeRight = new Plane3d(planePoint, nr);

        final Plane3d planeTop = new Plane3d(planePoint, nt);

        final Plane3d planeBottom = new Plane3d(planePoint, nb);

        Vector3dc roofLeftLineVector = new Vector3d(0, 0, recHeight);

        Vector3dc roofRightLineVector = new Vector3d(0, 0, -recHeight);

        Vector3dc roofTopLineVector = new Vector3d(-recWidth, 0, 0);
        Vector3dc roofButtomLineVector = new Vector3d(recWidth, 0, 0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpl, planeLeft, roofLeftLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpr, planeRight, roofRightLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpt, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mpb, planeBottom, roofButtomLineVector, roofTexture);

        HeightCalculator hc = new MultiSplitHeightCalculator() {
            @Override
            public double calcHeight(Vector2dc point) {
                return RoofType2v5.calcHeight(point, ltLine, lbLine, rtLine, rbLine, planeLeft, planeRight, planeTop,
                        planeBottom);
            }

            @Override
            public List<LinePoints2d> getSplittingLines() {
                return Arrays.asList(ltLine, lbLine, rtLine, rbLine);
            }
        };

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);
        rto.setHeightCalculator(hc);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RectangleRoofHooksSpaces rhs = buildRectRoofHooksSpace(rectangleContur, new PolygonPlane(mpb, planeBottom),
                new PolygonPlane(mpr, planeRight), new PolygonPlane(mpt, planeTop), new PolygonPlane(mpl, planeLeft));

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    public static MultiPolygonList2d intersectionOfLeftSideOfMultipleCuts(MultiPolygonList2d polygons, Vector2dc... lines) {
        return PolygonSplitHelper.intersectionOfLeftSideOfMultipleCuts(polygons,
                PolygonSplitHelper.polygonalChaniToLineArray(lines));
    }

    /**
     * Calculates height of point in wall.
     * 
     * @param point
     *            point
     * @param lt
     * @param lb
     * @param rt
     * @param rb
     * @param planeLeft
     * @param planeRight
     * @param planeTop
     * @param planeButtom
     * @return height of point
     */
    private static double calcHeight(Vector2dc point, LinePoints2d lt, LinePoints2d lb, LinePoints2d rt, LinePoints2d rb,
            Plane3d planeLeft, Plane3d planeRight, Plane3d planeTop, Plane3d planeButtom) {

        double x = point.x();
        double z = -point.y();

        if (lt.inFront(point)) {
            if (rt.inFront(point)) {
                return planeTop.calcYOfPlane(x, z);
            }

        } else {
            if (lb.inFront(point)) {
                return planeLeft.calcYOfPlane(x, z);
            }
        }

        if (rb.inFront(point)) {
            if (!rt.inFront(point)) {
                return planeRight.calcYOfPlane(x, z);
            }
        } else {
            if (!lb.inFront(point)) {
                return planeButtom.calcYOfPlane(x, z);
            }
        }

        return 0;
    }

}
