package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.util.List;

import kendzi.josm.kendzi3d.dto.TextureData;

public class BuildingModel {
//    @Deprecated
//    BuildingPart outline;

    List<BuildingPart> parts;

    String facadeMaterial;

    String roofMaterial;

    private TextureData facadeTextureData;

    private TextureData roofTextureData;

    // XXX move to set in Building! (to chose best match);
    private TextureData windowsColumnsTextureData;


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
        return parts;
    }

    /**
     * @param parts the parts to set
     */
    public void setParts(List<BuildingPart> parts) {
        this.parts = parts;
    }

    /**
     * @return the facadeMaterial
     */
    public String getFacadeMaterial() {
        return facadeMaterial;
    }

    /**
     * @param facadeMaterial the facadeMaterial to set
     */
    public void setFacadeMaterial(String facadeMaterial) {
        this.facadeMaterial = facadeMaterial;
    }

    /**
     * @return the roofMaterial
     */
    public String getRoofMaterial() {
        return roofMaterial;
    }

    /**
     * @param roofMaterial the roofMaterial to set
     */
    public void setRoofMaterial(String roofMaterial) {
        this.roofMaterial = roofMaterial;
    }

    /**
     * @return the facadeTextureData
     */
    public TextureData getFacadeTextureData() {
        return facadeTextureData;
    }

    /**
     * @param facadeTextureData the facadeTextureData to set
     */
    public void setFacadeTextureData(TextureData facadeTextureData) {
        this.facadeTextureData = facadeTextureData;
    }

    /**
     * @return the windowsColumnsTextureData
     */
    public TextureData getWindowsColumnsTextureData() {
        return windowsColumnsTextureData;
    }

    /**
     * @param windowsColumnsTextureData the windowsColumnsTextureData to set
     */
    public void setWindowsColumnsTextureData(TextureData windowsColumnsTextureData) {
        this.windowsColumnsTextureData = windowsColumnsTextureData;
    }

    /**
     * @return the roofTextureData
     */
    public TextureData getRoofTextureData() {
        return roofTextureData;
    }

    /**
     * @param roofTextureData the roofTextureData to set
     */
    public void setRoofTextureData(TextureData roofTextureData) {
        this.roofTextureData = roofTextureData;
    }
}
