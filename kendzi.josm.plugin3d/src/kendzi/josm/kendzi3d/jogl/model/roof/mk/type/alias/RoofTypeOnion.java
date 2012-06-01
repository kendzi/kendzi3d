/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias;

import java.util.Map;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType8_0;
import kendzi.math.geometry.polygon.CircleInsidePolygon.Circle;

/**
 * Roof type onion.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofTypeOnion extends RoofType8_0 {

    @Override
    public String getPrefixKey() {
        return RoofTypeAliasEnum.ONION.getKey();
    }


    /* (non-Javadoc)
     * @see kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType8_0#getBends(java.util.Map, kendzi.math.geometry.polygon.CircleInsidePolygon.Circle)
     */
    @Override
    protected Bend[] getBends(Map<MeasurementKey, Measurement> measurements, Circle circle) {
        // TODO Auto-generated method stub
//        return super.getBends(measurements, circle);
      //  circle

        double ratius =  circle.getRadius();

        Double h1 = getHeightMeters(measurements, MeasurementKey.HEIGHT_1, ratius * 2.25);
        Double l1 = getLenghtMetersPersent(measurements, MeasurementKey.LENGTH_1, ratius, ratius);


        return scaleBends(this.bendsShape, h1, l1);
    }

    Bend [] bendsShape = normalizeBends(new Bend[] {
//            new Bend(17, 0),
//            new Bend(19.5, 5),
//            new Bend(20, 10),
//            new Bend(19.5, 15),
//            new Bend(17, 20),
//            new Bend(13, 25),
//            new Bend(7, 30),
//            new Bend(2.7, 35),
//            new Bend(0.6, 40),
//            new Bend(0, 45)

            new Bend(0, 17),
            new Bend(5, 19.5),
            new Bend(10, 20),
            new Bend(15, 19.5),
            new Bend(20, 17),
            new Bend(25,13),
            new Bend(30, 7),
            new Bend(35,2.7),
            new Bend(40, 0.6),
            new Bend(45, 0)
    });

    private static Bend [] normalizeBends(Bend [] bendsShape) {

        if (bendsShape == null || bendsShape.length == 0) {
            return bendsShape;
        }

        double maxHeight = -Double.MAX_VALUE;
        double maxRadius = -Double.MAX_VALUE;

        for (Bend b : bendsShape) {
            if (b.getHeight() > maxHeight) {
                maxHeight = b.getHeight();
            }
            if (b.getRadius() > maxRadius) {
                maxRadius = b.getRadius();
            }
        }

        for (Bend b : bendsShape) {
            b.setHeight(b.getHeight() / maxHeight);
        }

        for (Bend b : bendsShape) {
            b.setRadius(b.getRadius() / maxRadius);
        }

        return bendsShape;
    }

    private static Bend [] scaleBends(Bend [] bendsShape, double scaleHeight, double scaleRadius) {

        Bend [] ret = new Bend[bendsShape.length];

        for (int i = 0; i < bendsShape.length; i++) {
            Bend b = bendsShape[i];
            Bend out = new Bend(
                    b.getHeight() * scaleHeight,
                    b.getRadius() * scaleRadius);
            ret[i] = out;
        }
        return ret;
    }

//
//    protected Bend [] getBends(Map<MeasurementKey, Measurement> measurements, Circle circle) {
//
//        int numOfBend = getNumOfBend(measurements);
//
//        Bend [] bends = new Bend[numOfBend + 1];
//
//        double ratius =  circle.getRadius();
//
//        Double h1 = getHeightDegreesMeters(measurements, MeasurementKey.HEIGHT_1, 0, ratius, 60);
//        Double l1 = getLenghtMetersPersent(measurements, MeasurementKey.LENGTH_1, ratius, ratius);
//
//        bends[0] = new Bend(0, l1);
//        bends[bends.length - 1] = new Bend(h1, 0);
//
//
//        if (numOfBend > 1) {
//            Double h2 = getHeightMeters(measurements, MeasurementKey.HEIGHT_2,
//                    h1 * 1 / numOfBend);
//            Double l2 = getLenghtMetersPersent(measurements, MeasurementKey.LENGTH_2, ratius,
//                    ratius * (numOfBend - 1) / numOfBend);
//
//            bends[1] = new Bend(h2, l2);
//        }
//
//
//        if (numOfBend > 2) {
//            Double h3 = getHeightMeters(measurements, MeasurementKey.HEIGHT_3,
//                    h1 * 2 / numOfBend);
//            Double l3 = getLenghtMetersPersent(measurements, MeasurementKey.LENGTH_3, ratius,
//                    ratius * (numOfBend - 2) / numOfBend);
//
//            bends[2] = new Bend(h3, l3);
//        }
//
//        if (numOfBend > 3) {
//            Double h4 = getHeightMeters(measurements, MeasurementKey.HEIGHT_4,
//                    h1 * 3 / numOfBend);
//            Double l4 = getLenghtMetersPersent(measurements, MeasurementKey.LENGTH_4, ratius,
//                    ratius * (numOfBend - 3) / numOfBend);
//
//            bends[3] = new Bend(h4, l4);
//        }
//
//        return bends;
//    }





}
