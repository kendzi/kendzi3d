/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.polygon.CircleInsidePolygon;
import kendzi.math.geometry.polygon.CircleInsidePolygon.Circle;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

import org.ejml.simple.SimpleMatrix;

/**
 * Roof type 8.0.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType8v0 extends AbstractRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRoof(Point2d pStartPoint, PolygonWithHolesList2d buildingPolygon, DormerRoofModel pRoof,
            double height, RoofMaterials roofTextureData) {

        List<Point2d> pPolygon = buildingPolygon.getOuter().getPoints();

        SimpleMatrix transformLocal = TransformationMatrix2d.tranA(-pStartPoint.x, -pStartPoint.y);

        pPolygon = TransformationMatrix2d.transformList(pPolygon, transformLocal);

        // rectangleContur =
        // TransformationMatrix2d.transformArray(rectangleContur,
        // transformLocal);

        PolygonList2d borderPolygon = new PolygonList2d(pPolygon);
        Circle circle = CircleInsidePolygon.iterativeNonConvex(borderPolygon, 0.01);

        int isection = getIsection(pRoof.getRoofTypeParameter());
        boolean soft = isSoft(pRoof.getRoofTypeParameter());

        Bend[] bends = getBends(pRoof.getMeasurements(), circle);

        RoofTypeOutput rto = build(pPolygon, circle.getPoint(), bends, isection, soft, roofTextureData);

        SimpleMatrix transformGlobal = TransformationMatrix3d.tranA(pStartPoint.x, height - rto.getHeight(), -pStartPoint.y);
        rto.setTransformationMatrix(transformGlobal);

        return rto;

    }

    protected RoofTypeOutput build(List<Point2d> borderList, Point2d point, Bend[] bends, int sectionCount, boolean pSoft,
            RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        PolygonList2d borderPolygon = new PolygonList2d(borderList);

        RoofType5v6.buildFlatRoof(borderPolygon, meshRoof, roofTexture);

        double height = bends[bends.length - 1].getHeight();

        Point2d[] crossSection = new Point2d[bends.length];
        crossSection[0] = new Point2d(bends[0].getRadius(), 0);
        crossSection[crossSection.length - 1] = new Point2d(0, height);

        for (int i = 1; i < bends.length; i++) {
            crossSection[i] = new Point2d(bends[i].getRadius(), bends[i].getHeight());
        }

        RoofType5v6.buildRotaryShape(meshRoof, point, sectionCount, crossSection, pSoft);

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(height);

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        rto.setRoofHooksSpaces(null);

        rto.setRectangle(RoofTypeUtil.findRectangle(borderList, 0));

        return rto;
    }

    protected Bend[] getBends(Map<MeasurementKey, Measurement> measurements, Circle circle) {

        int numOfBend = getNumOfBend(measurements);

        Bend[] bends = new Bend[numOfBend + 1];

        double ratius = circle.getRadius();

        Double h1 = getHeightDegreesMeters(measurements, MeasurementKey.HEIGHT_1, 0, ratius, 60);
        Double l1 = getLenghtMetersPersent(measurements, MeasurementKey.LENGTH_1, ratius, ratius);

        bends[0] = new Bend(0, l1);
        bends[bends.length - 1] = new Bend(h1, 0);

        if (numOfBend > 1) {
            Double h2 = getHeightMeters(measurements, MeasurementKey.HEIGHT_2, h1 * 1 / numOfBend);
            Double l2 = getLenghtMetersPersent(measurements, MeasurementKey.LENGTH_2, ratius, ratius * (numOfBend - 1)
                    / numOfBend);

            bends[1] = new Bend(h2, l2);
        }

        if (numOfBend > 2) {
            Double h3 = getHeightMeters(measurements, MeasurementKey.HEIGHT_3, h1 * 2 / numOfBend);
            Double l3 = getLenghtMetersPersent(measurements, MeasurementKey.LENGTH_3, ratius, ratius * (numOfBend - 2)
                    / numOfBend);

            bends[2] = new Bend(h3, l3);
        }

        if (numOfBend > 3) {
            Double h4 = getHeightMeters(measurements, MeasurementKey.HEIGHT_4, h1 * 3 / numOfBend);
            Double l4 = getLenghtMetersPersent(measurements, MeasurementKey.LENGTH_4, ratius, ratius * (numOfBend - 3)
                    / numOfBend);

            bends[3] = new Bend(h4, l4);
        }

        return bends;
    }

    private int getNumOfBend(Map<MeasurementKey, Measurement> measurements) {
        int ret = 1;
        if (measurements.get(MeasurementKey.HEIGHT_1) != null || measurements.get(MeasurementKey.LENGTH_1) != null) {
            ret = 1;
        }
        if (measurements.get(MeasurementKey.HEIGHT_2) != null || measurements.get(MeasurementKey.LENGTH_2) != null) {
            ret = 2;
        }
        if (measurements.get(MeasurementKey.HEIGHT_3) != null || measurements.get(MeasurementKey.LENGTH_3) != null) {
            ret = 3;
        }
        if (measurements.get(MeasurementKey.HEIGHT_4) != null || measurements.get(MeasurementKey.LENGTH_4) != null) {
            ret = 4;
        }
        return ret;
    }

    public static class Bend {
        private double height;
        private double radius;

        public Bend(double height, double radius) {
            super();
            this.height = height;
            this.radius = radius;
        }

        /**
         * @return the height
         */
        public double getHeight() {
            return height;
        }

        /**
         * @param height
         *            the height to set
         */
        public void setHeight(double height) {
            this.height = height;
        }

        /**
         * @return the radius
         */
        public double getRadius() {
            return radius;
        }

        /**
         * @param radius
         *            the radius to set
         */
        public void setRadius(double radius) {
            this.radius = radius;
        }

    }

    protected boolean isSoft(Integer pRoofParameter) {
        return pRoofParameter == null;
    }

    protected int getIsection(Integer pRoofParameter) {
        final int def = 9;

        if (pRoofParameter == null) {
            return def;
        }
        if (pRoofParameter < 3) {
            return def;
        }
        if (pRoofParameter > 40) {
            return def;
        }

        return pRoofParameter;
    }

}
