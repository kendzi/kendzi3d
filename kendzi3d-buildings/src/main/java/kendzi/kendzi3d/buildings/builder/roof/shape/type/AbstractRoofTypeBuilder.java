package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.Map;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.Measurement;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementParserUtil;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementUnit;

import org.apache.log4j.Logger;

public abstract class AbstractRoofTypeBuilder implements RoofTypeBuilder {

    /** Log. */
    private static final Logger log = Logger.getLogger(RectangleRoofTypeBuilder.class);

    public AbstractRoofTypeBuilder() {
        super();
    }

    public double getLenghtMetersPersent(Map<MeasurementKey, Measurement> measurements, MeasurementKey measurementKey,
            double maxLenght, double pDefaultValue) {

        Measurement measurement = getMeasurement(measurementKey, measurements);

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
            log.error(MeasurementParserUtil.getErrorMessage(measurementKey, measurement));
            return pDefaultValue;
        }
    }

    public double getHeightMeters(Map<MeasurementKey, Measurement> measurements, MeasurementKey measurementKey,
            double pDefaultValue) {

        Measurement measurement = getMeasurement(measurementKey, measurements);

        if (measurement == null) {
            return pDefaultValue;
        }
        if (isUnit(measurement, MeasurementUnit.METERS)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.UNKNOWN)) {
            return measurement.getValue();
        } else {
            log.error(MeasurementParserUtil.getErrorMessage(measurementKey, measurement));
            return pDefaultValue;
        }
    }

    /**
     * Default value is in meters!
     * 
     * @param measurements
     * @param measurementKey
     * @param pAngleHeight
     * @param pAngleDepth
     * @param pMetersDefaultValue
     * @return
     */
    public double getHeightMetersDegrees(Map<MeasurementKey, Measurement> measurements, MeasurementKey measurementKey,
            double pAngleHeight, double pAngleDepth, double pMetersDefaultValue) {

        Measurement measurement = getMeasurement(measurementKey, measurements);

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
            log.error(MeasurementParserUtil.getErrorMessage(measurementKey, measurement));
            return pMetersDefaultValue;
        }
    }

    /**
     * Default value is in degrees!
     * 
     * @param measurements
     * @param measurementKey
     * @param angleHeight
     * @param angleDepth
     * @param angleDegreesDefaultValue
     * @return
     */
    public double getHeightDegreesMeters(Map<MeasurementKey, Measurement> measurements, MeasurementKey measurementKey,
            double angleHeight, double angleDepth, double angleDegreesDefaultValue) {

        Measurement measurement = getMeasurement(measurementKey, measurements);

        if (measurement == null) {
            return angleHeight + angleDepth * Math.tan(Math.toRadians(angleDegreesDefaultValue));
        }
        if (isUnit(measurement, MeasurementUnit.METERS)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.UNKNOWN)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.DEGREES)) {
            return angleHeight + angleDepth * Math.tan(Math.toRadians(measurement.getValue()));
        } else {
            log.error(MeasurementParserUtil.getErrorMessage(measurementKey, measurement));
            return angleHeight + angleDepth * Math.tan(Math.toRadians(angleDegreesDefaultValue));
        }
    }

    private Measurement getMeasurement(MeasurementKey dormerWidth1, Map<MeasurementKey, Measurement> measurements) {
        if (measurements == null) {
            return null;
        }
        return measurements.get(dormerWidth1);
    }

    boolean isUnit(Measurement measurement, MeasurementUnit measurementUnit) {
        if (measurement == null) {
            return false;
        }
        if (measurementUnit.equals(measurement.getUnit())) {
            return true;
        }
        return false;
    }

    protected MeshFactory createRoofMesh(RoofMaterials roofTextureData) {

        MeshFactory meshRoof = new MeshFactory("roof_top");

        meshRoof.materialID = roofTextureData.getRoof().getMaterialIndexInModel();
        meshRoof.hasTexture = true;
        return meshRoof;
    }

    protected MeshFactory createFacadeMesh(RoofMaterials roofTextureData) {

        MeshFactory meshBorder = new MeshFactory("roof_facade");

        meshBorder.materialID = roofTextureData.getFacade().getMaterialIndexInModel();
        meshBorder.hasTexture = true;

        return meshBorder;
    }

}
