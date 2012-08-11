package kendzi.josm.kendzi3d.jogl.model.roof.mk.model;

import java.util.List;
import java.util.Map;

import javax.vecmath.Vector2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.josm.kendzi3d.util.BuildingRoofOrientation;
import kendzi.math.geometry.polygon.PolygonList2d;

public class DormerRoofModel {
    @Deprecated
    PolygonList2d building;

    // Roof Type
    RoofTypeAliasEnum roofType;

    Integer roofTypeParameter;

    // starting point ?

    // direction
    Vector2d direction;

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
     * @return the building
     */@Deprecated
    public PolygonList2d getBuilding() {
        return this.building;
    }


    /**
     * @param building the building to set
     */
    public void setBuilding(PolygonList2d building) {
        this.building = building;
    }





    /**
     * @return the direction
     */
    public Vector2d getDirection() {
        return this.direction;
    }


    /**
     * @param direction the direction to set
     */
    public void setDirection(Vector2d direction) {
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




}
