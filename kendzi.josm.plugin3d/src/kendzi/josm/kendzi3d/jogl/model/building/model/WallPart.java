package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.awt.Color;
import java.util.List;

public class WallPart {

//    private TextureData facadeTextureData;
//
//    private Color colour;

    private String facadeMaterialType;

    private String roofMaterialType;

    private Color facadeColour;

    private Color roofColour;


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
