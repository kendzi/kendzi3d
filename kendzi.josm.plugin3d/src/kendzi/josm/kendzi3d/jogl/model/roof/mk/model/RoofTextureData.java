/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.model;

import kendzi.josm.kendzi3d.dto.TextureData;

public class RoofTextureData {

    TextureData facadeTextrure;

    TextureData roofTexture;

    /**
     * @return the facadeTextrure
     */
    public TextureData getFacadeTextrure() {
        return this.facadeTextrure;
    }
    /**
     * @param facadeTextrure the facadeTextrure to set
     */
    public void setFacadeTextrure(TextureData facadeTextrure) {
        this.facadeTextrure = facadeTextrure;
    }
    /**
     * @return the roofTexture
     */
    public TextureData getRoofTexture() {
        return this.roofTexture;
    }
    /**
     * @param roofTexture the roofTexture to set
     */
    public void setRoofTexture(TextureData roofTexture) {
        this.roofTexture = roofTexture;
    }



}
