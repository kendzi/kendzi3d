package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.Map;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementParserUtil;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;

import org.apache.log4j.Logger;

public abstract class AbstractRoofTypeBuilder implements RoofTypeBuilder {

    /** Log. */
    private static final Logger log = Logger.getLogger(RectangleRoofTypeBuilder.class);

    public AbstractRoofTypeBuilder() {
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

    protected MeshFactory createRoofMesh(ModelFactory model, RoofMaterials pRoofTextureData) {

        MeshFactory meshRoof = model.addMesh("roof_top");

//        TextureData roofTexture = pRoofTextureData.getRoofTexture();
//        Material roofMaterial = MaterialFactory.createTextureMaterial(roofTexture.getFile());
//        int roofMaterialIndex = model.addMaterial(roofMaterial);

        meshRoof.materialID = pRoofTextureData.getRoof().getMaterialIndexInModel();
        meshRoof.hasTexture = true;
        return meshRoof;
    }

    protected MeshFactory createFacadeMesh(ModelFactory model, RoofMaterials pRoofTextureData) {

        MeshFactory meshBorder = model.addMesh("roof_facade");
//        TextureData facadeTexture = pRoofTextureData.getFacade().getTextureData();
//        Material facadeMaterial = MaterialFactory.createTextureMaterial(facadeTexture.getFile());
//        int facadeMaterialIndex = model.addMaterial(facadeMaterial);

        meshBorder.materialID = pRoofTextureData.getFacade().getMaterialIndexInModel();
        meshBorder.hasTexture = true;

        return meshBorder;
    }




}
