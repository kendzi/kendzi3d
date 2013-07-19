package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.awt.Color;
import java.util.List;

public class BuildingModel {

    List<BuildingPart> parts;

    private String facadeMaterialType;

    private String roofMaterialType;

    private String floorMaterialType;

    private Color facadeColor;

    private Color roofColor;

    private Color floorColor;

    /**
     * @return the parts
     */
    public List<BuildingPart> getParts() {
        return this.parts;
    }

    /**
     * @param parts
     *            the parts to set
     */
    public void setParts(List<BuildingPart> parts) {
        this.parts = parts;
    }

    /**
     * @return the facadeMaterialType
     */
    public String getFacadeMaterialType() {
        return this.facadeMaterialType;
    }

    /**
     * @param facadeMaterialType
     *            the facadeMaterialType to set
     */
    public void setFacadeMaterialType(String facadeMaterialType) {
        this.facadeMaterialType = facadeMaterialType;
    }

    /**
     * @return the roofMaterialType
     */
    public String getRoofMaterialType() {
        return this.roofMaterialType;
    }

    /**
     * @param roofMaterialType
     *            the roofMaterialType to set
     */
    public void setRoofMaterialType(String roofMaterialType) {
        this.roofMaterialType = roofMaterialType;
    }

    /**
     * @return the facadeColor
     */
    public Color getFacadeColor() {
        return this.facadeColor;
    }

    /**
     * @param facadeColor
     *            the facadeColor to set
     */
    public void setFacadeColor(Color facadeColor) {
        this.facadeColor = facadeColor;
    }

    /**
     * @return the roofColor
     */
    public Color getRoofColor() {
        return this.roofColor;
    }

    /**
     * @param roofColor
     *            the roofColor to set
     */
    public void setRoofColor(Color roofColor) {
        this.roofColor = roofColor;
    }

    /**
     * @return the floorMaterialType
     */
    public String getFloorMaterialType() {
        return this.floorMaterialType;
    }

    /**
     * @param floorMaterialType the floorMaterialType to set
     */
    public void setFloorMaterialType(String floorMaterialType) {
        this.floorMaterialType = floorMaterialType;
    }

    /**
     * @return the floorColor
     */
    public Color getFloorColor() {
        return this.floorColor;
    }

    /**
     * @param floorColor the floorColor to set
     */
    public void setFloorColor(Color floorColor) {
        this.floorColor = floorColor;
    }
}
