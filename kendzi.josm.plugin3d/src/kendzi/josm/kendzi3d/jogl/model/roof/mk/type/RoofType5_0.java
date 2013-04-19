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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType5_2.CrossSectionElement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplit;

import org.apache.log4j.Logger;

/**
 * Roof type 5.0.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType5_0 extends RectangleRoofTypeBuilder{

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofType5_0.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE5_0;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(
            PolygonWithHolesList2d buildingPolygon,
            Point2d[] pRectangleContur,
            double pScaleA,
            double pScaleB,
            double pRecHeight,
            double pRecWidth,
            Integer pPrefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofMaterials pRoofTextureData
            ) {


//        Double l1 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_1, pRecHeight, pRecHeight);

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, pRecHeight/2d);

        return build(buildingPolygon, pRecHeight, pRecWidth, pRectangleContur, h1, pRoofTextureData);

    }

    @Override
    protected boolean normalizeAB() {
        return false;
    }

    /**
     * @see {http://en.wikipedia.org/wiki/Circular_segment}
     * @param h
     * @param c
     * @return
     */
    double calcR(double h, double c) {
        return h/2d + (c*c)/(8d*h);
    }

    double calcTeta(double c, double r) {
        return 2d * Math.asin(c / (2d * r));
    }



    List<Double> calcSplitPoint(boolean half, int segments) {

        if (half) {
            List<Double> ret = new ArrayList();
            double step = Math.toRadians(90d / segments);
            ret.add(0d);
            for (int i = 0; i < segments; i++) {
                ret.add(1 - Math.sin((i+1) * step));

            }
            return ret;
        }

        List<Double> ret = new ArrayList();
        double step = Math.toRadians(180d / segments);
        ret.add(0d);
        for (int i = 0; i < segments; i++) {
            ret.add(1 - Math.sin((i+1) * step));

        }
        return ret;

    }

    public static abstract class HeightCalculator {
        abstract SegmentHeight [] height(Point2d p1, Point2d p2);
    }

    /**
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param height
     * @param pRoofTextureData
     * @return
     */
    protected RoofTypeOutput build(
            PolygonWithHolesList2d buildingPolygon,
            double pRecHeight,
            double pRecWidth,
            Point2d[] pRectangleContur,
            double height,
            RoofMaterials pRoofTextureData) {


        MeshFactory meshBorder = createFacadeMesh(pRoofTextureData);
        MeshFactory meshRoof = createRoofMesh(pRoofTextureData);

        TextureData facadeTexture = pRoofTextureData.getFacade().getTextureData();
        TextureData roofTexture = pRoofTextureData.getRoof().getTextureData();



        List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();
        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);


        int segments = 16;

        if (height > pRecHeight / 2d) {
            height = pRecHeight / 2d;
        }



        List<CrossSectionElement> crossSection = createCrossSection(height, pRecHeight, segments);


        final LinePoints2d[] lines = RoofType5_2.createLines(crossSection);

        MultiPolygonList2d [] mps = RoofType5_2.createMP(borderPolygon, lines);
//        Segment[] createSegments = createSegments(crossSection);

        final Plane3d[] planes = RoofType5_2.createPlanes(crossSection);

        double [] offsets = RoofType5_2.createTextureOffsets(crossSection);

        Vector3d roofLineVector = new Vector3d(
                pRecWidth,
                0,
                0);

        for (int i = 0; i< mps.length; i++) {

            RoofTypeUtil.addPolygonToRoofMesh(meshRoof, mps[i], planes[i], roofLineVector, roofTexture, 0, offsets[i]);
        }

        HeightCalculator hc  = new HeightCalculator( ) {

            @Override
            SegmentHeight [] height(Point2d p1, Point2d p2) {

                 List<Point2d> splitPolygon = Arrays.asList(p1, p2);

                 for (LinePoints2d line : lines) {
                     splitPolygon = PolygonSplit.splitLineSegmentsOnLineBBB(line, splitPolygon);
                 }

                 SegmentHeight [] ret = new SegmentHeight [splitPolygon.size()-1];

                 for (int i = 0; i < splitPolygon.size() - 1; i++) {
                     int j = i + 1;

                     Point2d begin = splitPolygon.get(i);
                     Point2d end = splitPolygon.get(j);

                     double beginHeight = calcHeight(begin, lines, planes);
                     double endHeight = calcHeight(end, lines, planes);

                     SegmentHeight sh = new SegmentHeight(begin, beginHeight, end, endHeight);
                     ret[i] = sh;
                 }

                 return ret;
            }



            /** Calc height of point in border.
             * @param point
             * @param lines
             * @param planes
             * @return
             */
            private double calcHeight(Point2d point, LinePoints2d[] lines, Plane3d[] planes) {

                double x = point.x;
                double z = -point.y;

                for (int i = 1; i < lines.length; i++) {
                    LinePoints2d line = lines[i];

                    if (!line.inFront(point)) {
                        return planes[i-1].calcYOfPlane(x, z);
                    }
                }

                return planes[planes.length-1].calcYOfPlane(x, z);
            }

        };


        List<Point2d> borderSplit = new ArrayList<Point2d>();
        List<Double> borderHeights = new ArrayList<Double>();
        {
            // This is only temporary, border generation code will be moved
            for (int i = 0; i< pBorderList.size(); i++) {
                Point2d p1 = pBorderList.get(i) ;
                Point2d p2 = pBorderList.get((i+1) %pBorderList.size()) ;

                SegmentHeight[] height2 = hc.height(p1, p2);

                for (int j = 0; j < height2.length; j++) {
                    borderSplit.add(height2[j].getBegin());
                    borderHeights.add(height2[j].getBeginHeight());
                }

            }
        }

        RoofTypeUtil.makeRoofBorderMesh(

                               borderSplit,
                               borderHeights,

                               meshBorder,
                               facadeTexture
                               );


        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(height);

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        return rto;
    }


    List<Point2d> createRound(double recHeight, double r, double h, double maxTeta, int segments) {

        double cx = recHeight / 2d;
        double cy = h - r;

        List<Point2d> ret = new ArrayList<Point2d>();
        ret.add(new Point2d());

        if (segments <= 0) {
            ret.add(new Point2d(recHeight, 0));
            return ret;
        }

        double dTeta = maxTeta / segments;

        double startTeta = Math.PI / 2d - maxTeta / 2d;

        for (int i = 1; i < segments; i++) {
            double teta = startTeta + i * dTeta;
            double px = -Math.cos(teta) * r + cx;
            double py =  Math.sin(teta) * r + cy;

            ret.add(new Point2d(px, py));
        }
        ret.add(new Point2d(recHeight, 0));

        return ret;
    }


    private List<RoofType5_2.CrossSectionElement> createCrossSection(double height,  double pRecHeight, int segments) {

        double c = pRecHeight;
        double h = height;

        double r = calcR(h, c);
        double teta = calcTeta(c, r);

        List<Point2d> round = createRound(pRecHeight, r, height, teta, segments);



        List<RoofType5_2.CrossSectionElement> split = new ArrayList<RoofType5_2.CrossSectionElement>();

        for (Point2d p : round) {
            double x = p.x;
            double y = p.y;

            split.add(new RoofType5_2.CrossSectionElement(new Point2d(x, y), new Vector2d(p.x-1d, p.y)));
        }

        if (height < pRecHeight) {
            split.add(new RoofType5_2.CrossSectionElement(new Point2d(pRecHeight, height), new Vector2d(0,1)));
        }

        return split;
    }

}
