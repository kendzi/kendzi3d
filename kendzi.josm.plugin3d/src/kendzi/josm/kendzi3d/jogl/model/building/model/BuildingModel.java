package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.awt.Color;
import java.util.List;

public class BuildingModel {

    List<BuildingPart> parts;

    private String facadeMaterialType;

    private String roofMaterialType;

    private String floorMaterialType;

    private Color facadeColour;

    private Color roofColour;

    private Color floorColour;

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
     * @return the facadeColour
     */
    public Color getFacadeColour() {
        return this.facadeColour;
    }

    /**
     * @param facadeColour
     *            the facadeColour to set
     */
    public void setFacadeColour(Color facadeColour) {
        this.facadeColour = facadeColour;
    }

    /**
     * @return the roofColour
     */
    public Color getRoofColour() {
        return this.roofColour;
    }

    /**
     * @param roofColour
     *            the roofColour to set
     */
    public void setRoofColour(Color roofColour) {
        this.roofColour = roofColour;
    }

    /**
     * @return the floorMaterialType
     */
    public String getFloorMaterialType() {
        return floorMaterialType;
    }

    /**
     * @param floorMaterialType the floorMaterialType to set
     */
    public void setFloorMaterialType(String floorMaterialType) {
        this.floorMaterialType = floorMaterialType;
    }

    /**
     * @return the floorColour
     */
    public Color getFloorColour() {
        return floorColour;
    }

    /**
     * @param floorColour the floorColour to set
     */
    public void setFloorColour(Color floorColour) {
        this.floorColour = floorColour;
    }
}
