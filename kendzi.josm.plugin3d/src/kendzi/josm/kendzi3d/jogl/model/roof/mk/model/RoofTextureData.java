/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.model;

import java.awt.Color;

import kendzi.josm.kendzi3d.dto.TextureData;

public class RoofTextureData {

    TextureData facadeTexture;

    Color facadeColor;

    TextureData roofTexture;

    Color roofColor;

    /**
     * @return the facadeTextrure
     */
    public TextureData getFacadeTexture() {
        return this.facadeTexture;
    }
    /**
     * @param facadeTexture the facadeTextrure to set
     */
    public void setFacadeTexture(TextureData facadeTexture) {
        this.facadeTexture = facadeTexture;
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
