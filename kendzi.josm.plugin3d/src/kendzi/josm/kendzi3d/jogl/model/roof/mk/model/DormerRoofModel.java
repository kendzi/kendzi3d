package kendzi.josm.kendzi3d.jogl.model.roof.mk.model;

import java.util.List;
import java.util.Map;

import kendzi.josm.kendzi3d.jogl.model.building.model.roof.RoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.josm.kendzi3d.util.BuildingRoofOrientation;

public class DormerRoofModel implements RoofModel {

    // Roof Type
    RoofTypeAliasEnum roofType;

    Integer roofTypeParameter;

    // starting point ?

    // direction
    RoofDirection direction;

    /**
     * Only for simple buildings.
     * Should not be used!
     */
    BuildingRoofOrientation orientation;

//    Point2d directionBegin;
//
//    Point2d directionEnd;

    // dormers
    //- as relations?

    //- as string
    List<List<DormerType>> dormers;

    //- as rectangle
    Map<DormerRow,List<DormerType>> dormersFront;
    Map<DormerRow,List<DormerType>> dormersRight;
    Map<DormerRow,List<DormerType>> dormersBack;
    Map<DormerRow,List<DormerType>> dormersLeft;


    // walls
    // - windows
    // - dormers

    // orientation ?

    // Measurements
    Map<MeasurementKey, Measurement> measurements;





    /**
     * @return the direction
     */
    public RoofDirection getDirection() {
        return this.direction;
    }


    /**
     * @param direction the direction to set
     */
    public void setDirection(RoofDirection direction) {
        this.direction = direction;
    }








    /**
     * @return the pMeasurements
     */
    public Map<MeasurementKey, Measurement> getMeasurements() {
        return this.measurements;
    }


    /**
     * @param pMeasurements the pMeasurements to set
     */
    public void setMeasurements(Map<MeasurementKey, Measurement> pMeasurements) {
        this.measurements = pMeasurements;
    }


    /**
     * @return the roofType
     */
    public RoofTypeAliasEnum getRoofType() {
        return this.roofType;
    }


    /**
     * @param roofType the roofType to set
     */
    public void setRoofType(RoofTypeAliasEnum roofType) {
        this.roofType = roofType;
    }


    /**
     * @return the roofTypeParameter
     */
    public Integer getRoofTypeParameter() {
        return this.roofTypeParameter;
    }


    /**
     * @param roofTypeParameter the roofTypeParameter to set
     */
    public void setRoofTypeParameter(Integer roofTypeParameter) {
        this.roofTypeParameter = roofTypeParameter;
    }


    /**
     * @return the dormers
     */
    public List<List<DormerType>> getDormers() {
        return this.dormers;
    }


    /**
     * @param dormers the dormers to set
     */
    public void setDormers(List<List<DormerType>> dormers) {
        this.dormers = dormers;
    }


    /**
     * @return the dormersFront
     */
    public Map<DormerRow, List<DormerType>> getDormersFront() {
        return this.dormersFront;
    }


    /**
     * @param dormersFront the dormersFront to set
     */
    public void setDormersFront(Map<DormerRow, List<DormerType>> dormersFront) {
        this.dormersFront = dormersFront;
    }


    /**
     * @return the dormersRight
     */
    public Map<DormerRow, List<DormerType>> getDormersRight() {
        return this.dormersRight;
    }


    /**
     * @param dormersRight the dormersRight to set
     */
    public void setDormersRight(Map<DormerRow, List<DormerType>> dormersRight) {
        this.dormersRight = dormersRight;
    }


    /**
     * @return the dormersBack
     */
    public Map<DormerRow, List<DormerType>> getDormersBack() {
        return this.dormersBack;
    }


    /**
     * @param dormersBack the dormersBack to set
     */
    public void setDormersBack(Map<DormerRow, List<DormerType>> dormersBack) {
        this.dormersBack = dormersBack;
    }


    /**
     * @return the dormersLeft
     */
    public Map<DormerRow, List<DormerType>> getDormersLeft() {
        return this.dormersLeft;
    }


    /**
     * @param dormersLeft the dormersLeft to set
     */
    public void setDormersLeft(Map<DormerRow, List<DormerType>> dormersLeft) {
        this.dormersLeft = dormersLeft;
    }


    /**
     * @return the orientation
     */
    public BuildingRoofOrientation getOrientation() {
        return this.orientation;
    }


    /**
     * @param orientation the orientation to set
     */
    public void setOrientation(BuildingRoofOrientation orientation) {
        this.orientation = orientation;
    }


    @Override
    public double getRoofHeight() {
        throw new RuntimeException("TODO");
    }




}
