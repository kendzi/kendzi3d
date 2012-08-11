package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.awt.Color;
import java.util.List;

import kendzi.josm.kendzi3d.dto.TextureData;

public class WallPart {

    private TextureData facadeTextureData;

    private Color colour;

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





}
