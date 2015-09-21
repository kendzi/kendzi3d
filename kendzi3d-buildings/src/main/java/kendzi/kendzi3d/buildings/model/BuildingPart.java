package kendzi.kendzi3d.buildings.model;

import java.awt.Color;
import java.util.List;

import kendzi.kendzi3d.buildings.model.roof.RoofModel;

public class BuildingPart {

    private static final double DEFAULT_BUILDING_HEIGHT = 8;

    private Double maxHeight;

    private Double minHeight;

    private Integer maxLevel;

    private Integer minLevel;

    private Integer roofLevels;

    private double levelHeight = 2.5;

    private String facadeMaterialType;

    private String roofMaterialType;

    private String floorMaterialType;

    private Color facadeColor;

    private Color roofColor;

    private Color floorColor;

    private RoofModel roof;

    private Object context;

    // Outline
    private Wall wall;

    // Inline
    private List<Wall> inlineWalls;

    // Windows

    // Indoor

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

    public int getDefaultRoofLevels() {
        if (roofLevels != null) {
            return roofLevels;
        }
        return 0;
    }

    public double getDefaultRoofHeight() {
        if (roofLevels != null) {
            return roofLevels * levelHeight;
        }
        return 0;
    }

    /**
     * @return the levelHeight
     */
    public double getLevelHeight() {
        return levelHeight;
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
    public RoofModel getRoof() {
        return roof;
    }

    /**
     * @param roof
     *            the roof to set
     */
    public void setRoof(RoofModel roof) {
        this.roof = roof;
    }

    /**
     * @return the wall
     */
    public Wall getWall() {
        return wall;
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
        return inlineWalls;
    }

    /**
     * @param inlineWalls
     *            the inlineWalls to set
     */
    public void setInlineWalls(List<Wall> inlineWalls) {
        this.inlineWalls = inlineWalls;
    }

    /**
     * @return the maxHeight
     */
    public Double getMaxHeight() {
        return maxHeight;
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
        return minHeight;
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
        return maxLevel;
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
        return minLevel;
    }

    /**
     * @param minLevel
     *            the minLevel to set
     */
    public void setMinLevel(Integer minLevel) {
        this.minLevel = minLevel;
    }

    /**
     * @return the facadeMaterialType
     */
    public String getFacadeMaterialType() {
        return facadeMaterialType;
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
        return roofMaterialType;
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
        return facadeColor;
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
        return roofColor;
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
        return roofLevels;
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
     * @param floorMaterialType
     *            the floorMaterialType to set
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
     * @param floorColor
     *            the floorColor to set
     */
    public void setFloorColor(Color floorColor) {
        this.floorColor = floorColor;
    }

    /**
     * @return the context
     */
    public Object getContext() {
        return context;
    }

    /**
     * @param context
     *            the context to set
     */
    public void setContext(Object context) {
        this.context = context;
    }
}
