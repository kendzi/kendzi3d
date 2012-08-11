package kendzi.josm.kendzi3d.jogl.model.building.model;

import java.util.List;

import kendzi.josm.kendzi3d.dto.TextureData;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class Wall {

    private List<WallPart> wallParts;

    private TextureData facadeTextureData;

    private TextureData roofTextureData;



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
     * @return the facadeTextureData
     */
    public TextureData getFacadeTextureData() {
        return this.facadeTextureData;
    }

    /**
     * @param facadeTextureData the facadeTextureData to set
     */
    public void setFacadeTextureData(TextureData facadeTextureData) {
        this.facadeTextureData = facadeTextureData;
    }

    /**
     * @return the roofTextureData
     */
    public TextureData getRoofTextureData() {
        return this.roofTextureData;
    }

    /**
     * @param roofTextureData the roofTextureData to set
     */
    public void setRoofTextureData(TextureData roofTextureData) {
        this.roofTextureData = roofTextureData;
    }


}
