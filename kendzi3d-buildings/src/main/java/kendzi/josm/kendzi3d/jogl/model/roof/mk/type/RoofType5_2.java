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
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.wall.BetweenLinesHeightCalculator;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.wall.HeightCalculator;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitUtil;
import kendzi.math.geometry.polygon.split.SplitPolygons;

import org.apache.log4j.Logger;

/**
 * Roof type 5.2.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType5_2 extends RectangleRoofTypeBuilder{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType5_2.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE5_2;
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

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, pRecHeight);

        return build(buildingPolygon, pScaleA, pScaleB, pRecHeight, pRecWidth, pRectangleContur, h1, pRoofTextureData);

    }

    @Override
    protected boolean normalizeAB() {
        return false;
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
            double pScaleA,
            double pScaleB,
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


        int segments = 6;
        List<Double> splitPoint = calcSplitPoint(true, segments);
        //        List<LinePoints2d> lines = createLines(splitPoint, pRecHeight);

        List<CrossSectionElement> crossSection = createCrossSection(height, pRecHeight);
        final LinePoints2d[] lines = createLines(crossSection);

        MultiPolygonList2d [] mps = createMP(borderPolygon, lines);
        //        Segment[] createSegments = createSegments(crossSection);

        final Plane3d[] planes = createPlanes(crossSection);

        double [] offsets = createTextureOffsets(crossSection);

        Vector3d roofLineVector = new Vector3d(
                pRecWidth,
                0,
                0);

        for (int i = 0; i< mps.length; i++) {

            MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mps[i], planes[i], roofLineVector, roofTexture, 0, offsets[i]);
        }


        HeightCalculator hc = new BetweenLinesHeightCalculator(lines, planes );


        //        List<Point2d> borderSplit = new ArrayList<Point2d>();
        //        List<Double> borderHeights = new ArrayList<Double>();
        //        {
        //            // This is only temporary, border generation code will be moved
        //            for (int i = 0; i< pBorderList.size(); i++) {
        //                Point2d p1 = pBorderList.get(i) ;
        //                Point2d p2 = pBorderList.get((i+1) %pBorderList.size()) ;
        //
        //                SegmentHeight[] height2 = hc.height(p1, p2);
        //
        //                for (int j = 0; j < height2.length; j++) {
        //                    borderSplit.add(height2[j].getBegin());
        //                    borderHeights.add(height2[j].getBeginHeight());
        //                }
        //
        //            }
        //        }
        //
        //        RoofTypeUtil.makeRoofBorderMesh(
        //
        //                               borderSplit,
        //                               borderHeights,
        //
        //                               meshBorder,
        //                               facadeTexture
        //                               );
        RoofTypeUtil.makeWallsFromHeightCalculator(pBorderList, hc, 0d, meshBorder, facadeTexture);

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(height);
        //
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));
        //
        //        RectangleRoofHooksSpaces rhs =
        //                buildRectRoofHooksSpace(
        //                        pRectangleContur,
        //                        new PolygonPlane(bottomMP, planeBottom),
        //                        null,
        //                        new PolygonPlane(topMP, planeTop),
        //                        null
        //                      );
        //
        //        rto.setRoofHooksSpaces(rhs);

        return rto;
    }

    public static double[] createTextureOffsets(List<CrossSectionElement> crossSection) {
        double [] ret = new double[crossSection.size()];
        ret[0] = 0;
        for (int i = 1; i < crossSection.size(); i++) {
            Point2d p1 = crossSection.get(i - 1).getP();
            Point2d p2 = crossSection.get(i).getP();

            ret[i] = ret[i - 1] + p1.distance(p2);
        }
        return ret;
    }

    public static MultiPolygonList2d[] createMP(PolygonList2d borderPolygon, LinePoints2d[] lines) {

        MultiPolygonList2d topMP = new MultiPolygonList2d(borderPolygon);

        MultiPolygonList2d [] mps = new MultiPolygonList2d[lines.length-1];

        for (int i = 1; i< lines.length; i++) {

            SplitPolygons middleSplit = PolygonSplitUtil.split(topMP, lines[i]);

            topMP = middleSplit.getTopMultiPolygons();
            mps[i-1]= middleSplit.getBottomMultiPolygons();
        }

        return mps;

    }

    List<Point2d> createRound(int segments) {


        List<Point2d> ret = new ArrayList<Point2d>();
        double step = Math.toRadians(90d / segments);
        ret.add(new Point2d());
        for (int i = 0; i < segments; i++) {
            ret.add(new Point2d(
                    1 - Math.cos((i+1) * step),
                    Math.sin((i+1) * step)
                    ));
        }
        return ret;

    }
    static class  CrossSectionElement {
        Point2d p;
        Vector2d v;
        /**
         * @return the p
         */
        public Point2d getP() {
            return p;
        }
        /**
         * @param p the p to set
         */
        public void setP(Point2d p) {
            this.p = p;
        }
        /**
         * @return the v
         */
        public Vector2d getV() {
            return v;
        }
        /**
         * @param v the v to set
         */
        public void setV(Vector2d v) {
            this.v = v;
        }
        public CrossSectionElement(Point2d p, Vector2d v) {
            super();
            this.p = p;
            this.v = v;
        }
        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "CSE (" + p + ") > (" + v + ">";
        }



    }
    private List<CrossSectionElement> createCrossSection(double height,  double pRecHeight) {
        List<Point2d> round = createRound(6);

        List<CrossSectionElement> split = new ArrayList<CrossSectionElement>();

        for (Point2d p : round) {
            double x = p.x * height;
            double y = p.y * height;

            split.add(new CrossSectionElement(new Point2d(x, y), new Vector2d(p.x-1d, p.y)));
        }

        if (height < pRecHeight) {
            split.add(new CrossSectionElement(new Point2d(pRecHeight, height), new Vector2d(0,1)));
        }

        return split;

    }

    public static Plane3d [] createPlanes(List<CrossSectionElement> crossSection) {
        Plane3d [] seg = new Plane3d[crossSection.size() - 1];

        for (int i = 1; i< crossSection.size(); i++) {
            CrossSectionElement first = crossSection.get(i-1);
            CrossSectionElement second = crossSection.get(i);

            Point3d p = new Point3d(0, first.p.y, -first.p.x);

            Vector3d v = new Vector3d(
                    0,
                    (second.p.x - first.p.x),
                    (second.p.y - first.p.y)
                    );
            v.normalize();
            Plane3d plane3d = new Plane3d(p,v);

            seg[i-1] = plane3d;
        }
        return seg;
    }

    /**
     * @param crossSection
     * @return
     */
    public static LinePoints2d[] createLines(List<CrossSectionElement> crossSection) {
        LinePoints2d [] lines = new LinePoints2d [crossSection.size()];
        for (int i = 0; i< crossSection.size(); i++) {
            Point2d p = crossSection.get(i).p;

            LinePoints2d l = new LinePoints2d(new Point2d(0, p.x), new Point2d(1, p.x));
            lines[i] = l;
        }
        return lines;
    }

}
