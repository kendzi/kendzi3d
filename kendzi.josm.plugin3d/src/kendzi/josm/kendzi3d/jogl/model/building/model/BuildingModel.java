package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.awt.Color;
import java.util.List;

public class BuildingModel {
//    @Deprecated
//    BuildingPart outline;

    List<BuildingPart> parts;


    private String facadeMaterialType;

    private String roofMaterialType;

    private Color facadeColour;

    private Color roofColour;


//    String facadeMaterial;
//
//    String roofMaterial;
//
//    private TextureData facadeTextureData;
//
//    private TextureData roofTextureData;
//
//    // XXX move to set in Building! (to chose best match);
//    private TextureData windowsColumnsTextureData;


//    /**
//     * @return the outline
//     */
//    @Deprecated
//    public BuildingPart getOutline() {
//        return outline;
//    }
//
//    /**
//     * @param outline the outline to set
//     */
//    public void setOutline(BuildingPart outline) {
//        this.outline = outline;
//    }

    /**
     * @return the parts
     */
    public List<BuildingPart> getParts() {
        return this.parts;
    }

    /**
     * @param parts the parts to set
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
     * @param facadeMaterialType the facadeMaterialType to set
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
     * @param roofMaterialType the roofMaterialType to set
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
     * @param facadeColour the facadeColour to set
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
     * @param roofColour the roofColour to set
     */
    public void setRoofColour(Color roofColour) {
        this.roofColour = roofColour;
    }
}
