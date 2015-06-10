/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.wall.HeightCalculator;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.line.LineUtil;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonUtil;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.EnrichPolygonalChainUtil;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper.MultiPolygonSplitResult;

import org.apache.log4j.Logger;

/**
 * Roof type 2.1.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType2v1 extends RectangleRoofTypeBuilder {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2v1.class);
    private static final double EPSILON = 1e-10;

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 1.5d);

        Double h2 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_2, 2.5d);

        Double b1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecHeight(),
                conf.getRecHeight() / 2d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1,
                h2, b1, conf.getRoofTextureData());
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
            Point2d[] rectangleContur, double roofHeightBottom, double roofHeightTop, double l1,
            RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        double roofLineDistanceBottom = l1;
        double roofLineDistanceTop = recHeight - roofLineDistanceBottom;

        Point2d leftMiddlePoint = new Point2d(0, l1);
        Point2d rightMiddlePoint = new Point2d(recWidth, l1);

        final LinePoints2d middleRoofLine = new LinePoints2d(leftMiddlePoint, rightMiddlePoint);

        Vector3d normalTop = new Vector3d(0, roofLineDistanceTop, -roofHeightTop);
        normalTop.normalize();

        Vector3d normalBottom = new Vector3d(0, roofLineDistanceBottom, roofHeightBottom);
        normalBottom.normalize();

        List<Point2d> outline = PolygonUtil.makeCounterClockwise(buildingPolygon.getOuter().getPoints());
        PolygonList2d borderPolygon = new PolygonList2d(outline);

        MultiPolygonSplitResult middleSplit = PolygonSplitHelper.splitMultiPolygon(
                new MultiPolygonList2d(borderPolygon), middleRoofLine);

        MultiPolygonList2d topMP = middleSplit.getLeftMultiPolygon();
        MultiPolygonList2d bottomMP = middleSplit.getRightMultiPolygon();

        Point3d planeLeftPoint = new Point3d(leftMiddlePoint.x, roofHeightBottom, -leftMiddlePoint.y);
        Point3d planeRightPoint_ZZZ = new Point3d(rightMiddlePoint.x, roofHeightTop, -rightMiddlePoint.y);

        final Plane3d planeTop = new Plane3d(planeRightPoint_ZZZ, normalTop);
        final Plane3d planeBottom = new Plane3d(planeLeftPoint, normalBottom);

        Vector3d roofBottomLineVector = new Vector3d(recWidth, 0, 0);
        Vector3d roofTopLineVector = new Vector3d(-recWidth, 0, 0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);
        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, bottomMP, planeBottom, roofBottomLineVector, roofTexture);

        List<Point2d> borderWithSplits = RoofTypeUtil.splitBorder(borderPolygon, middleRoofLine);

        HeightCalculator hc = new HeightCalculator() {

            @Override
            public List<SegmentHeight> height(Point2d p1, Point2d p2) {

                List<Point2d> chain = new ArrayList<Point2d>();
                chain.add(p1);
                chain.add(p2);

                List<Point2d> enrichedChain = EnrichPolygonalChainUtil.enrichOpenPolygonalChainByLineCrossing(chain,
                        middleRoofLine);

                List<SegmentHeight> ret = new ArrayList<SegmentHeight>();
                // XXX TODO FIXME run it and test it!

                for (int i = 0; i < enrichedChain.size() - 1; i++) {
                    Point2d begin = enrichedChain.get(i);
                    Point2d end = enrichedChain.get(i + 1);

                    Plane3d plane = planeBottom;
                    if (isSegmentInFrontOfLine(begin, end, middleRoofLine)) {
                        plane = planeTop;
                    }
                    ret.add(new SegmentHeight( //
                            begin, calcHeight(begin, plane), //
                            end, calcHeight(end, plane)));
                }

                return ret;
            }
        };

        double minHeight = 0;

        // FIXME it is middle wall missing! It have to be fixed!

        RoofTypeUtil.makeWallsFromHeightCalculator(borderWithSplits, hc, minHeight, meshBorder, facadeTexture);

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
    private double calcHeight(Point2d point, Plane3d plane) {

        double x = point.x;
        double z = -point.y;

        return plane.calcYOfPlane(x, z);

    }

    boolean isSegmentInFrontOfLine(Point2d begin, Point2d end, LinePoints2d line) {

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
        if (endDet > 0 && (beginDet >= 0)) {
            return true;
        }
        return false;
    }

    private static boolean equalZero(double number, double epsilon) {
        return number * number < epsilon;
    }

}
