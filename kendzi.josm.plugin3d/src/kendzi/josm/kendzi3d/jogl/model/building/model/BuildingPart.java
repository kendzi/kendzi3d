package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.awt.Color;
import java.util.List;

import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;

public class BuildingPart {

    private static final double DEFAULT_BUILDING_HEIGHT = 8;

    Double maxHeight;
    Double minHeight;

    Double maxLevel;
    Double minLevel;

    double levelHeight = 2.5;

    private TextureData facadeTextureData;

    private TextureData roofTextureData;

    private Color colour;

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
    public double getDefaultMinLevel() {
        if (minLevel != null) {
            return minLevel;
        }

        return 0;
    }

 // XXX move to util
    public double getDefaultMaxLevel() {
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
    public Double getMaxLevel() {
        return maxLevel;
    }


    /**
     * @param maxLevel the maxLevel to set
     */
    public void setMaxLevel(Double maxLevel) {
        this.maxLevel = maxLevel;
    }


    /**
     * @return the minLevel
     */
    public Double getMinLevel() {
        return minLevel;
    }


    /**
     * @param minLevel the minLevel to set
     */
    public void setMinLevel(Double minLevel) {
        this.minLevel = minLevel;
    }


    /**
     * @return the colour
     */
    public Color getColour() {
        return colour;
    }

    /**
     * @param colour the colour to set
     */
    public void setColour(Color colour) {
        this.colour = colour;
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
