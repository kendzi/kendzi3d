package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.util.List;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;

public class BuildingPart {
    double maxHeight;
    double minHeight;

    double maxLevel;
    double minLevel;

    double levelHeight;

    DormerRoofModel roof;

    //Outline
    List<WallPart> wallParts;

    //Windows


    /**
     * @return the maxHeight
     */
    public double getMaxHeight() {
        return maxHeight;
    }

    /**
     * @param maxHeight the maxHeight to set
     */
    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }

    /**
     * @return the minHeight
     */
    public double getMinHeight() {
        return minHeight;
    }

    /**
     * @param minHeight the minHeight to set
     */
    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }

    /**
     * @return the maxLevel
     */
    public double getMaxLevel() {
        return maxLevel;
    }

    /**
     * @param maxLevel the maxLevel to set
     */
    public void setMaxLevel(double maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * @return the minLevel
     */
    public double getMinLevel() {
        return minLevel;
    }

    /**
     * @param minLevel the minLevel to set
     */
    public void setMinLevel(double minLevel) {
        this.minLevel = minLevel;
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
     * @return the wallParts
     */
    public List<WallPart> getWallParts() {
        return wallParts;
    }

    /**
     * @param wallParts the wallParts to set
     */
    public void setWallParts(List<WallPart> wallParts) {
        this.wallParts = wallParts;
    }







}
