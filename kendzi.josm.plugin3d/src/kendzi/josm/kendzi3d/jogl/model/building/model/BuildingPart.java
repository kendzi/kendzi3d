package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.awt.Color;
import java.util.List;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;

public class BuildingPart {

    private static final double DEFAULT_BUILDING_HEIGHT = 8;

    Double maxHeight;

    Double minHeight;

    Integer maxLevel;

    Integer minLevel;

    Integer roofLevels;

    double levelHeight = 2.5;

    private String facadeMaterialType;

    private String roofMaterialType;

    private String floorMaterialType;

    private Color facadeColor;

    private Color roofColor;

    private Color floorColor;

    DormerRoofModel roof;

    // Outline
    Wall wall;

    // Inline
    List<Wall> inlineWalls;

    DormerRoofModel dormerRoofModel;

    // Windows

    // Indoor

    // XXX move to util
    public double getDefaultMinHeight() {
        if (this.minHeight != null) {
            return this.minHeight;
        }

        if (this.minLevel != null) {
            return this.minLevel * this.levelHeight;
        }
        return 0;
    }

    // XXX move to util
    public double getDefaultMaxHeight() {
        if (this.maxHeight != null) {
            return this.maxHeight;
        }

        if (this.maxLevel != null) {
            return this.maxLevel * this.levelHeight;
        }
        return getDefaultMinHeight() + DEFAULT_BUILDING_HEIGHT;
    }

    // XXX move to util
    public int getDefaultMinLevel() {
        if (this.minLevel != null) {
            return this.minLevel;
        }

        return 0;
    }

    // XXX move to util
    public int getDefaultMaxLevel() {
        if (this.maxLevel != null) {
            return this.maxLevel;
        }

        return getDefaultMinLevel() + 1;
    }

    public int getDefaultRoofLevels() {
        if (this.roofLevels != null) {
            return this.roofLevels;
        }
        return 0;
    }

    public double getDefaultRoofHeight() {
        if (this.roofLevels != null) {
            return this.roofLevels * levelHeight;
        }
        return 0;
    }

    /**
     * @return the levelHeight
     */
    public double getLevelHeight() {
        return this.levelHeight;
    }

    /**
     * @param levelHeight
     *            the levelHeight to set
     */
    public void setLevelHeight(double levelHeight) {
        this.levelHeight = levelHeight;
    }

    /**
     * @return the roof
     */
    public DormerRoofModel getRoof() {
        return this.roof;
    }

    /**
     * @param roof
     *            the roof to set
     */
    public void setRoof(DormerRoofModel roof) {
        this.roof = roof;
    }

    /**
     * @return the wall
     */
    public Wall getWall() {
        return this.wall;
    }

    /**
     * @param wall
     *            the wall to set
     */
    public void setWall(Wall wall) {
        this.wall = wall;
    }

    /**
     * @return the inlineWalls
     */
    public List<Wall> getInlineWalls() {
        return this.inlineWalls;
    }

    /**
     * @param inlineWalls
     *            the inlineWalls to set
     */
    public void setInlineWalls(List<Wall> inlineWalls) {
        this.inlineWalls = inlineWalls;
    }

    // /**
    // * @return the facadeTextureData
    // */
    // public TextureData getFacadeTextureData() {
    // return facadeTextureData;
    // }
    //
    // /**
    // * @param facadeTextureData the facadeTextureData to set
    // */
    // public void setFacadeTextureData(TextureData facadeTextureData) {
    // this.facadeTextureData = facadeTextureData;
    // }

    /**
     * @return the maxHeight
     */
    public Double getMaxHeight() {
        return this.maxHeight;
    }

    /**
     * @param maxHeight
     *            the maxHeight to set
     */
    public void setMaxHeight(Double maxHeight) {
        this.maxHeight = maxHeight;
    }

    /**
     * @return the minHeight
     */
    public Double getMinHeight() {
        return this.minHeight;
    }

    /**
     * @param minHeight
     *            the minHeight to set
     */
    public void setMinHeight(Double minHeight) {
        this.minHeight = minHeight;
    }

    /**
     * @return the maxLevel
     */
    public Integer getMaxLevel() {
        return this.maxLevel;
    }

    /**
     * @param maxLevel
     *            the maxLevel to set
     */
    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * @return the minLevel
     */
    public Integer getMinLevel() {
        return this.minLevel;
    }

    /**
     * @param minLevel
     *            the minLevel to set
     */
    public void setMinLevel(Integer minLevel) {
        this.minLevel = minLevel;
    }

    /**
     * @return the dormerRoofModel
     */
    public DormerRoofModel getDormerRoofModel() {
        return this.dormerRoofModel;
    }

    /**
     * @param dormerRoofModel
     *            the dormerRoofModel to set
     */
    public void setDormerRoofModel(DormerRoofModel dormerRoofModel) {
        this.dormerRoofModel = dormerRoofModel;
    }

    // /**
    // * @return the roofTextureData
    // */
    // public TextureData getRoofTextureData() {
    // return roofTextureData;
    // }
    //
    // /**
    // * @param roofTextureData the roofTextureData to set
    // */
    // public void setRoofTextureData(TextureData roofTextureData) {
    // this.roofTextureData = roofTextureData;
    // }

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
     * @return the roofLevels
     */
    public Integer getRoofLevels() {
        return this.roofLevels;
    }

    /**
     * @param roofLevels
     *            the roofLevels to set
     */
    public void setRoofLevels(Integer roofLevels) {
        this.roofLevels = roofLevels;
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
     * @return the floorColor
     */
    public Color getFloorColor() {
        return floorColor;
    }

    /**
     * @param floorColor the floorColor to set
     */
    public void setFloorColor(Color floorColor) {
        this.floorColor = floorColor;
    }

}
