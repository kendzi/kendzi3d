package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.util.List;

public class BuildingModel {

    BuildingPart outline;

    List<BuildingPart> parts;

    String facadeMaterial;

    String roofMaterial;

    /**
     * @return the outline
     */
    public BuildingPart getOutline() {
        return outline;
    }

    /**
     * @param outline the outline to set
     */
    public void setOutline(BuildingPart outline) {
        this.outline = outline;
    }

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
}
