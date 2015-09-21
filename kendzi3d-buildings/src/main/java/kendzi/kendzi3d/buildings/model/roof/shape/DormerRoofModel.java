package kendzi.kendzi3d.buildings.model.roof.shape;

import java.util.List;
import java.util.Map;

import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.Measurement;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementUnit;
import kendzi.kendzi3d.buildings.model.roof.RoofFrontDirection;
import kendzi.kendzi3d.buildings.model.roof.RoofModel;
import kendzi.kendzi3d.buildings.model.roof.RoofOrientation;

public class DormerRoofModel implements RoofModel {

    // Roof Type
    RoofTypeAliasEnum roofType;

    Integer roofTypeParameter;

    // starting point ?

    // direction
    RoofFrontDirection direction;

    /**
     * Only for simple buildings. Should not be used!
     */
    RoofOrientation orientation;

    // Point2d directionBegin;
    //
    // Point2d directionEnd;

    // dormers
    // - as relations?

    // - as string
    List<List<DormerType>> dormers;

    // - as rectangle
    Map<DormerRow, List<DormerType>> dormersFront;
    Map<DormerRow, List<DormerType>> dormersRight;
    Map<DormerRow, List<DormerType>> dormersBack;
    Map<DormerRow, List<DormerType>> dormersLeft;

    // walls
    // - windows
    // - dormers

    // orientation ?

    // Measurements
    Map<MeasurementKey, Measurement> measurements;

    /**
     * @return the direction
     */
    public RoofFrontDirection getDirection() {
        return direction;
    }

    /**
     * @param direction
     *            the direction to set
     */
    public void setDirection(RoofFrontDirection direction) {
        this.direction = direction;
    }

    /**
     * @return the pMeasurements
     */
    public Map<MeasurementKey, Measurement> getMeasurements() {
        return measurements;
    }

    /**
     * @param pMeasurements
     *            the pMeasurements to set
     */
    public void setMeasurements(Map<MeasurementKey, Measurement> pMeasurements) {
        measurements = pMeasurements;
    }

    /**
     * @return the roofType
     */
    public RoofTypeAliasEnum getRoofType() {
        return roofType;
    }

    /**
     * @param roofType
     *            the roofType to set
     */
    public void setRoofType(RoofTypeAliasEnum roofType) {
        this.roofType = roofType;
    }

    /**
     * @return the roofTypeParameter
     */
    public Integer getRoofTypeParameter() {
        return roofTypeParameter;
    }

    /**
     * @param roofTypeParameter
     *            the roofTypeParameter to set
     */
    public void setRoofTypeParameter(Integer roofTypeParameter) {
        this.roofTypeParameter = roofTypeParameter;
    }

    /**
     * @return the dormers
     */
    public List<List<DormerType>> getDormers() {
        return dormers;
    }

    /**
     * @param dormers
     *            the dormers to set
     */
    public void setDormers(List<List<DormerType>> dormers) {
        this.dormers = dormers;
    }

    /**
     * @return the dormersFront
     */
    public Map<DormerRow, List<DormerType>> getDormersFront() {
        return dormersFront;
    }

    /**
     * @param dormersFront
     *            the dormersFront to set
     */
    public void setDormersFront(Map<DormerRow, List<DormerType>> dormersFront) {
        this.dormersFront = dormersFront;
    }

    /**
     * @return the dormersRight
     */
    public Map<DormerRow, List<DormerType>> getDormersRight() {
        return dormersRight;
    }

    /**
     * @param dormersRight
     *            the dormersRight to set
     */
    public void setDormersRight(Map<DormerRow, List<DormerType>> dormersRight) {
        this.dormersRight = dormersRight;
    }

    /**
     * @return the dormersBack
     */
    public Map<DormerRow, List<DormerType>> getDormersBack() {
        return dormersBack;
    }

    /**
     * @param dormersBack
     *            the dormersBack to set
     */
    public void setDormersBack(Map<DormerRow, List<DormerType>> dormersBack) {
        this.dormersBack = dormersBack;
    }

    /**
     * @return the dormersLeft
     */
    public Map<DormerRow, List<DormerType>> getDormersLeft() {
        return dormersLeft;
    }

    /**
     * @param dormersLeft
     *            the dormersLeft to set
     */
    public void setDormersLeft(Map<DormerRow, List<DormerType>> dormersLeft) {
        this.dormersLeft = dormersLeft;
    }

    /**
     * @return the orientation
     */
    public RoofOrientation getOrientation() {
        return orientation;
    }

    /**
     * @param orientation
     *            the orientation to set
     */
    public void setOrientation(RoofOrientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public double getRoofHeight() {
        Measurement measurement = measurements.get(MeasurementKey.HEIGHT_1);
        if (measurement == null) {
            // XXX FIXME workaround assume 0 height
            return 0;
        }
        return measurement.getValue();
    }

    @Override
    public void setRoofHeight(double roofHeight) {
        measurements.put(MeasurementKey.HEIGHT_1, new Measurement(roofHeight, MeasurementUnit.METERS));
    }

}
