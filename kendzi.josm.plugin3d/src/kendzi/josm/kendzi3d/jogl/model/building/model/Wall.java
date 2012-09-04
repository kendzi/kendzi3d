package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.awt.Color;
import java.util.List;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class Wall {

    private List<WallPart> wallParts;

    private String facadeMaterialType;

    private String roofMaterialType;

    private Color facadeColour;

    private Color roofColour;


    /**
     * @return the wallParts
     */
    public List<WallPart> getWallParts() {
        return this.wallParts;
    }

    /**
     * @param wallParts the wallParts to set
     */
    public void setWallParts(List<WallPart> wallParts) {
        this.wallParts = wallParts;
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
