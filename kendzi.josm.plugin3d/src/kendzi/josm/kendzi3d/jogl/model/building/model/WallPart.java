package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.awt.Color;
import java.util.List;

public class WallPart {

//    private TextureData facadeTextureData;

    private String facadeMaterialType;

    private String roofMaterialType;

    private Color facadeColor;

    private Color roofColor;


//    // XXX move to set in Building! (to chose best match);
//    private TextureData windowsColumnsTextureData;

    private List<WallNode> nodes;

    //List<BuildingElementGrid> buildingElementGrids;

    private List<BuildingWallElement> buildingElements;

    /**
     * @return the nodes
     */
    public List<WallNode> getNodes() {
        return this.nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(List<WallNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the buildingElements
     */
    public List<BuildingWallElement> getBuildingElements() {
        return this.buildingElements;
    }

    /**
     * @param buildingElements the buildingElements to set
     */
    public void setBuildingElements(List<BuildingWallElement> buildingElements) {
        this.buildingElements = buildingElements;
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
