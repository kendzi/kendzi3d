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

    private Color facadeColor;

    private Color roofColor;


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
     * @return the facadeColor
     */
    public Color getFacadeColor() {
        return this.facadeColor;
    }

    /**
     * @param facadeColor the facadeColor to set
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
     * @param roofColor the roofColor to set
     */
    public void setRoofColor(Color roofColor) {
        this.roofColor = roofColor;
    }


}
