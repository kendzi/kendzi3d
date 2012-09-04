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

    double levelHeight = 2.5;

    private String facadeMaterialType;

    private String roofMaterialType;

//    private TextureData facadeTextureData;
//
//    private TextureData roofTextureData;

    private Color facadeColour;

    private Color roofColour;

    DormerRoofModel roof;

    //Outline
    Wall wall;

    //Inline
    List<Wall> inlineWalls;

    DormerRoofModel dormerRoofModel;

    //Windows

    //Indoor

    // XXX move to util
    public double getDefaultMinHeight() {
        if (minHeight != null) {
            return minHeight;
        }

        if (minLevel != null) {
            return minLevel * levelHeight;
        }
        return 0;
    }

 // XXX move to util
    public double getDefaultMaxHeight() {
        if (maxHeight != null) {
            return maxHeight;
        }

        if (maxLevel != null) {
            return maxLevel * levelHeight;
        }
        return getDefaultMinHeight() + DEFAULT_BUILDING_HEIGHT;
    }

 // XXX move to util
    public int getDefaultMinLevel() {
        if (minLevel != null) {
            return minLevel;
        }

        return 0;
    }

 // XXX move to util
    public int getDefaultMaxLevel() {
        if (maxLevel != null) {
            return maxLevel;
        }

        return getDefaultMinLevel() + 1;
    }


    /**
     * @return the levelHeight
     */
    public double getLevelHeight() {
        return levelHeight;
    }

    /**
     * @param levelHeight the levelHeight to set
     */
    public void setLevelHeight(double levelHeight) {
        this.levelHeight = levelHeight;
    }

    /**
     * @return the roof
     */
    public DormerRoofModel getRoof() {
        return roof;
    }

    /**
     * @param roof the roof to set
     */
    public void setRoof(DormerRoofModel roof) {
        this.roof = roof;
    }

    /**
     * @return the wall
     */
    public Wall getWall() {
        return wall;
    }

    /**
     * @param wall the wall to set
     */
    public void setWall(Wall wall) {
        this.wall = wall;
    }

    /**
     * @return the inlineWalls
     */
    public List<Wall> getInlineWalls() {
        return inlineWalls;
    }

    /**
     * @param inlineWalls the inlineWalls to set
     */
    public void setInlineWalls(List<Wall> inlineWalls) {
        this.inlineWalls = inlineWalls;
    }

//    /**
//     * @return the facadeTextureData
//     */
//    public TextureData getFacadeTextureData() {
//        return facadeTextureData;
//    }
//
//    /**
//     * @param facadeTextureData the facadeTextureData to set
//     */
//    public void setFacadeTextureData(TextureData facadeTextureData) {
//        this.facadeTextureData = facadeTextureData;
//    }


    /**
     * @return the maxHeight
     */
    public Double getMaxHeight() {
        return maxHeight;
    }


    /**
     * @param maxHeight the maxHeight to set
     */
    public void setMaxHeight(Double maxHeight) {
        this.maxHeight = maxHeight;
    }


    /**
     * @return the minHeight
     */
    public Double getMinHeight() {
        return minHeight;
    }


    /**
     * @param minHeight the minHeight to set
     */
    public void setMinHeight(Double minHeight) {
        this.minHeight = minHeight;
    }


    /**
     * @return the maxLevel
     */
    public Integer getMaxLevel() {
        return maxLevel;
    }


    /**
     * @param maxLevel the maxLevel to set
     */
    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }


    /**
     * @return the minLevel
     */
    public Integer getMinLevel() {
        return minLevel;
    }


    /**
     * @param minLevel the minLevel to set
     */
    public void setMinLevel(Integer minLevel) {
        this.minLevel = minLevel;
    }




    /**
     * @return the dormerRoofModel
     */
    public DormerRoofModel getDormerRoofModel() {
        return dormerRoofModel;
    }

    /**
     * @param dormerRoofModel the dormerRoofModel to set
     */
    public void setDormerRoofModel(DormerRoofModel dormerRoofModel) {
        this.dormerRoofModel = dormerRoofModel;
    }

//    /**
//     * @return the roofTextureData
//     */
//    public TextureData getRoofTextureData() {
//        return roofTextureData;
//    }
//
//    /**
//     * @param roofTextureData the roofTextureData to set
//     */
//    public void setRoofTextureData(TextureData roofTextureData) {
//        this.roofTextureData = roofTextureData;
//    }

    /**
     * @return the facadeMaterialType
     */
    public String getFacadeMaterialType() {
        return facadeMaterialType;
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
        return roofMaterialType;
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
        return facadeColour;
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
        return roofColour;
    }

    /**
     * @param roofColour the roofColour to set
     */
    public void setRoofColour(Color roofColour) {
        this.roofColour = roofColour;
    }








}
