/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer;

import java.util.Map;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementParserUtil;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;

import org.apache.log4j.Logger;

public abstract class AbstractRoofDormerType implements RoofDormerType {

    /** Log. */
    private static final Logger log = Logger.getLogger(AbstractRoofDormerType.class);

    protected static double getWidth(MeasurementKey pMeasurementKey, Map<MeasurementKey, Measurement> pMeasurements,
            double pDefaultValue) {

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

    protected double getHeight(MeasurementKey pMeasurementKey, Map<MeasurementKey, Measurement> pMeasurements,
            double pDefaultValue) {

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

    protected static Measurement getMeasurement(MeasurementKey pDormerWidth1,
            Map<MeasurementKey, Measurement> pMeasurements) {
        if (pMeasurements == null) {
            return null;
        }
        return pMeasurements.get(pDormerWidth1);
    }

    protected static boolean isUnit(Measurement pMeasurement, MeasurementUnit pMeasurementUnit) {
        if (pMeasurement == null) {
            return false;
        }
        if (pMeasurementUnit.equals(pMeasurement.getUnit())) {
            return true;
        }
        return false;
    }

}
