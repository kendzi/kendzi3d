/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias;

import java.util.Map;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType8v0;
import kendzi.math.geometry.polygon.CircleInsidePolygon.Circle;

/**
 * Roof type onion.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofTypeOnion extends RoofType8v0 {

    @Override
    protected Bend[] getBends(Map<MeasurementKey, Measurement> measurements, Circle circle) {

        double ratius = circle.getRadius();

        Double h1 = getHeightMeters(measurements, MeasurementKey.HEIGHT_1, ratius * 2.25);
        Double l1 = getLenghtMetersPersent(measurements, MeasurementKey.LENGTH_1, ratius, ratius);

        return scaleBends(this.bendsShape, h1, l1);
    }

    Bend[] bendsShape = normalizeBends(new Bend[] { new Bend(0, 17), new Bend(5, 19.5), new Bend(10, 20), new Bend(15, 19.5),
            new Bend(20, 17), new Bend(25, 13), new Bend(30, 7), new Bend(35, 2.7), new Bend(40, 0.6), new Bend(45, 0) });

    private static Bend[] normalizeBends(Bend[] bendsShape) {

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

    private static Bend[] scaleBends(Bend[] bendsShape, double scaleHeight, double scaleRadius) {

        Bend[] ret = new Bend[bendsShape.length];

        for (int i = 0; i < bendsShape.length; i++) {
            Bend b = bendsShape[i];
            Bend out = new Bend(b.getHeight() * scaleHeight, b.getRadius() * scaleRadius);
            ret[i] = out;
        }
        return ret;
    }
}
