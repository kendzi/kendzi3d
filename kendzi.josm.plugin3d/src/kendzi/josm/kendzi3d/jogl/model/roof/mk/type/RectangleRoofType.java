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
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.PolygonRoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementParserUtil;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;
import kendzi.math.geometry.Graham;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.RectangleUtil;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;

import org.apache.log4j.Logger;
import org.ejml.data.SimpleMatrix;

public abstract class RectangleRoofType implements RoofType {

    /** Log. */
    private static final Logger log = Logger.getLogger(RectangleRoofType.class);

    @Override
    public RoofTypeOutput buildRoof(Point2d pStartPoint, List<Point2d> border, Integer prefixParameter, double height,
            List<Double> heights, List<Double> sizesB, Map<MeasurementKey, Measurement> pMeasurements, RoofTextureData pRoofTextureData) {

        List<Point2d> graham = Graham.grahamScan(border);
        Point2d[] rectangleContur = RectangleUtil.findRectangleContur(graham);

        rectangleContur = findStartPoint(pStartPoint, rectangleContur);

        Point2d newStartPoint = rectangleContur[0];

        //XXX test me
        double alpha = Math.atan2(
                rectangleContur[1].y - rectangleContur[0].y,
                rectangleContur[1].x - rectangleContur[0].x);
        //XXX
        System.out.println(Math.toDegrees(alpha));

        boolean normalizeAB = normalizeAB();

        @Deprecated
        double scaleA = 1;
        @Deprecated
        double scaleB = 1;
        double recHeight = 1;
        double recWidth = 1;

        if (normalizeAB) {

            scaleA = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[1].x)
                    + pow(rectangleContur[0].y - rectangleContur[1].y));
            scaleB = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[3].x)
                    + pow(rectangleContur[0].y - rectangleContur[3].y));
        } else {

            recHeight = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[3].x)
                    + pow(rectangleContur[0].y - rectangleContur[3].y));
            recWidth = Math.sqrt(pow(rectangleContur[0].x - rectangleContur[1].x)
                    + pow(rectangleContur[0].y - rectangleContur[1].y));
        }


        border = transformToLocalCord(border, newStartPoint.x, newStartPoint.y, alpha, scaleA, scaleB);
        rectangleContur = transformToLocalCord(rectangleContur, newStartPoint.x, newStartPoint.y, alpha, scaleA,
                scaleB);



        RoofTypeOutput buildRectangleRoof = buildRectangleRoof(border, rectangleContur, scaleA, scaleB, recHeight, recWidth, prefixParameter,
                heights, sizesB, pMeasurements, pRoofTextureData);



        SimpleMatrix scale = TransformationMatrix3d.scaleA(scaleA, 1, scaleB);
        SimpleMatrix transf = TransformationMatrix3d.tranA(
                newStartPoint.x, height - buildRectangleRoof.getHeight(), -newStartPoint.y);
        SimpleMatrix rot = TransformationMatrix3d.rotYA(alpha);
        //XXX test me
        SimpleMatrix tr = transf.mult(rot).mult(scale);

        buildRectangleRoof.setTransformationMatrix(tr);

        List<Point3d> rect = new ArrayList<Point3d>();
        rect.add(new Point3d(rectangleContur[0].x, 0, -rectangleContur[0].y));
        rect.add(new Point3d(rectangleContur[1].x, 0, -rectangleContur[1].y));
        rect.add(new Point3d(rectangleContur[2].x, 0, -rectangleContur[2].y));
        rect.add(new Point3d(rectangleContur[3].x, 0, -rectangleContur[3].y));
        buildRectangleRoof.setRectangle(rect);

        return buildRectangleRoof;

    }



    protected boolean normalizeAB() {
        return true;
    }

    /**
     * @param pList
     * @param newStartPoint
     * @param alpha
     * @param sizeA
     * @param sizeB
     */
    public List<Point2d> transformToLocalCord(List<Point2d> pList, double x, double y, double alpha, double sizeA,
            double sizeB) {
        SimpleMatrix scaleLocal = TransformationMatrix2d.scaleA(1 / sizeA, 1 / sizeB);
        SimpleMatrix transfLocal = TransformationMatrix2d.tranA(
                -x, -y);
        SimpleMatrix rotLocal = TransformationMatrix2d.rotZA(-alpha);
        //XXX test me
        SimpleMatrix transformLocal = scaleLocal.mult(rotLocal).mult(transfLocal);

        List<Point2d> list = new ArrayList<Point2d>();
        for (Point2d p : pList) {
           Point2d transformed = TransformationMatrix2d.transform(p, transformLocal);
           list.add(transformed);
        }
        return list;
    }

    private Point2d[] transformToLocalCord(Point2d[] pList, double x, double y, double alpha, double sizeA,
            double sizeB) {

        SimpleMatrix scaleLocal = TransformationMatrix2d.scaleA(1 / sizeA, 1 / sizeB);
        SimpleMatrix transfLocal = TransformationMatrix2d.tranA(
                -x, -y);
        SimpleMatrix rotLocal = TransformationMatrix2d.rotZA(-alpha);
        //XXX test me
        SimpleMatrix transformLocal = scaleLocal.mult(rotLocal).mult(transfLocal);

        Point2d [] list = new Point2d[pList.length];
        int i = 0;
        for (Point2d p : pList) {
           Point2d transformed = TransformationMatrix2d.transform(p, transformLocal);
           list[i] = transformed;
           i++;
        }
        return list;
    }


    private double pow(double d) {

        return d * d;
    }

    /**
     * @param border
     * @param rectangleContur
     * @param scaleA
     * @param scaleB
     * @param sizeB2
     * @param sizeA
     * @param prefixParameter
     * @param heights
     * @param sizesB
     * @param pRoofTextureData
     * @return
     */
    public abstract RoofTypeOutput buildRectangleRoof(List<Point2d> border, Point2d[] rectangleContur, double scaleA, double scaleB,
            double sizeA, double sizeB2, Integer prefixParameter, List<Double> heights, List<Double> sizesB, Map<MeasurementKey, Measurement> pMeasurements, RoofTextureData pRoofTextureData);

    private Point2d[] findStartPoint(Point2d pStartPoint, Point2d[] pRectangleContur) {
        int minI = 0;
        double minDist = pStartPoint.distanceSquared(pRectangleContur[0]);
        for (int i = 1; i < 4; i++) {
            double distance = pStartPoint.distanceSquared(pRectangleContur[i]);
            if (distance < minDist) {
                minDist = distance;
                minI = i;
            }
        }

        Point2d[] ret = new Point2d[4];
        for (int i = 0; i < 4; i++) {
            ret[i] = pRectangleContur[(i + minI) % 4];
        }
        return ret;
    }

    /**
     * Get size parameter.
     *
     * @param i index
     * @param pList list of parameters
     * @param defaultSize default value
     * @return size
     */
    @Deprecated
    Double getSize(int i, List<Double> pList, Double defaultSize) {
        if (i < 0) {
            return defaultSize;
        }
        if (pList.size() - 1 < i) {
            return defaultSize;
        }
        Double ret = pList.get(i);
        if (ret == null) {
            return defaultSize;
        }
        return ret;
    }

    public double getLenghtMetersPersent(
            Map<MeasurementKey, Measurement> pMeasurements,
            MeasurementKey pMeasurementKey,
            double maxLenght,
            double pDefaultValue) {

        Measurement measurement = getMeasurement(pMeasurementKey, pMeasurements);

        if (measurement == null) {
            return pDefaultValue;
        }
        if (isUnit(measurement, MeasurementUnit.METERS)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.PERCENT)) {
            return measurement.getValue() * maxLenght / 100d;
        } else if (isUnit(measurement, MeasurementUnit.UNKNOWN)) {
            return measurement.getValue();
        } else {
            log.error(MeasurementParserUtil.getErrorMessage(pMeasurementKey, measurement));
            return pDefaultValue;
        }
    }

    public double getHeightMeters(Map<MeasurementKey, Measurement> pMeasurements, MeasurementKey pMeasurementKey, double pDefaultValue) {

        Measurement measurement = getMeasurement(pMeasurementKey, pMeasurements);

        if (measurement == null) {
            return pDefaultValue;
        }
        if (isUnit(measurement, MeasurementUnit.METERS)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.UNKNOWN)) {
            return measurement.getValue();
        } else {
            log.error(MeasurementParserUtil.getErrorMessage(pMeasurementKey, measurement));
            return pDefaultValue;
        }
    }

    /** Default value is in meters!
     * @param pMeasurements
     * @param pMeasurementKey
     * @param pAngleHeight
     * @param pAngleDepth
     * @param pMetersDefaultValue
     * @return
     */
    public double getHeightMetersDegrees(
            Map<MeasurementKey, Measurement> pMeasurements,
            MeasurementKey pMeasurementKey,
            double pAngleHeight,
            double pAngleDepth,
            double pMetersDefaultValue) {

        Measurement measurement = getMeasurement(pMeasurementKey, pMeasurements);

        if (measurement == null) {
            return pMetersDefaultValue;
        }
        if (isUnit(measurement, MeasurementUnit.METERS)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.UNKNOWN)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.DEGREES)) {
            return pAngleHeight + pAngleDepth * Math.tan(Math.toRadians(measurement.getValue()));
        } else {
            log.error(MeasurementParserUtil.getErrorMessage(pMeasurementKey, measurement));
            return pMetersDefaultValue;
        }
    }
    /** Default value is in degrees!
     * @param pMeasurements
     * @param pMeasurementKey
     * @param pAngleHeight
     * @param pAngleDepth
     * @param pAngleDegreesDefaultValue
     * @return
     */
    public double getHeightDegreesMeters(
            Map<MeasurementKey, Measurement> pMeasurements,
            MeasurementKey pMeasurementKey,
            double pAngleHeight,
            double pAngleDepth,
            double pAngleDegreesDefaultValue) {

        Measurement measurement = getMeasurement(pMeasurementKey, pMeasurements);

        if (measurement == null) {
            return pAngleHeight + pAngleDepth * Math.tan(Math.toRadians(pAngleDegreesDefaultValue));
        }
        if (isUnit(measurement, MeasurementUnit.METERS)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.UNKNOWN)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.DEGREES)) {
            return pAngleHeight + pAngleDepth * Math.tan(Math.toRadians(measurement.getValue()));
        } else {
            log.error(MeasurementParserUtil.getErrorMessage(pMeasurementKey, measurement));
            return pAngleHeight + pAngleDepth * Math.tan(Math.toRadians(pAngleDegreesDefaultValue));
        }
    }

    private Measurement getMeasurement(MeasurementKey pDormerWidth1, Map<MeasurementKey, Measurement> pMeasurements) {
        if (pMeasurements == null) {
            return null;
        }
        return pMeasurements.get(pDormerWidth1);
    }

    boolean isUnit(Measurement pMeasurement, MeasurementUnit pMeasurementUnit) {
        if (pMeasurement == null) {
            return false;
        }
        if (pMeasurementUnit.equals(pMeasurement.getUnit())) {
            return true;
        }
        return false;
    }





    /**
     * @param pRectangleContur rectangle
     * @param pP1 1 polygon and plane defining height connected with rectangle edge 1
     * @param pP2 2 polygon and plane defining height connected with rectangle edge 2
     * @param pP3 3 polygon and plane defining height connected with rectangle edge 3
     * @param pP4 4 polygon and plane defining height connected with rectangle edge 4
     * @return rectangle roof hooks space
     */
    protected RoofHooksSpace [] buildRectRoofHooksSpace(
            Point2d[] pRectangleContur,
            PolygonPlane pP1,
            PolygonPlane pP2,
            PolygonPlane pP3,
            PolygonPlane pP4
            ) {

        List<RoofHooksSpace> ret = new ArrayList<RoofHooksSpace>();
        if (pP1 != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(0, pRectangleContur, pP1);
            ret.add(rrhs);
        }

        if (pP2 != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(1, pRectangleContur, pP2);
            ret.add(rrhs);
        }

        if (pP3 != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(2, pRectangleContur, pP3);
            ret.add(rrhs);
        }

        if (pP4 != null) {
            PolygonRoofHooksSpace rrhs = buildRecHookSpace(3, pRectangleContur, pP4);
            ret.add(rrhs);
        }

        return ret.toArray(new RoofHooksSpace[ret.size()]);
    }

    /** Build roof hook space for rectangle edge.
     * @param pEdge rectangle edge number
     * @param pRectangleContur rectangle
     * @param pPolygonPlane polygon and plane defining height connected with rectangle edge
     * @return roof hook space
     */
    private PolygonRoofHooksSpace buildRecHookSpace(int pEdge, Point2d[] pRectangleContur, PolygonPlane pPolygonPlane) {

        if (pPolygonPlane == null) {
            return null;
        }

        Vector2d v1 = new Vector2d(pRectangleContur[(pEdge + 1) % 4]);
        v1.sub(pRectangleContur[pEdge]);


        Plane3d plane = new Plane3d(pPolygonPlane.getPlane().getPoint(), pPolygonPlane.getPlane().getNormal());

        PolygonRoofHooksSpace rrhs1 = new PolygonRoofHooksSpace(
                        pRectangleContur[pEdge],
                        v1,
                        pPolygonPlane.getPolygon(),
                        plane);
        return rrhs1;
    }

    class PolygonPlane {

        private MultiPolygonList2d polygon;

        private Plane3d plane;

        public PolygonPlane(MultiPolygonList2d polygon, Plane3d plane) {
            super();
            this.polygon = polygon;
            this.plane = plane;
        }

        /**
         * @return the polygon
         */
        public MultiPolygonList2d getPolygon() {
            return polygon;
        }
        /**
         * @param polygon the polygon to set
         */
        public void setPolygon(MultiPolygonList2d polygon) {
            this.polygon = polygon;
        }
        /**
         * @return the plane
         */
        public Plane3d getPlane() {
            return plane;
        }
        /**
         * @param plane the plane to set
         */
        public void setPlane(Plane3d plane) {
            this.plane = plane;
        }


    }

}
