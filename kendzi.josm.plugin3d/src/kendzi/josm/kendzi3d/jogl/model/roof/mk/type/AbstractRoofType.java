package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.Map;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementParserUtil;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;

import org.apache.log4j.Logger;

public class AbstractRoofType {

    /** Log. */
    private static final Logger log = Logger.getLogger(RectangleRoofType.class);

    public AbstractRoofType() {
        super();
    }

    public double getLenghtMetersPersent(Map<MeasurementKey, Measurement> pMeasurements, MeasurementKey pMeasurementKey, double maxLenght, double pDefaultValue) {

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
    public double getHeightMetersDegrees(Map<MeasurementKey, Measurement> pMeasurements, MeasurementKey pMeasurementKey, double pAngleHeight, double pAngleDepth, double pMetersDefaultValue) {

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
    public double getHeightDegreesMeters(Map<MeasurementKey, Measurement> pMeasurements, MeasurementKey pMeasurementKey, double pAngleHeight, double pAngleDepth, double pAngleDegreesDefaultValue) {

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



}
