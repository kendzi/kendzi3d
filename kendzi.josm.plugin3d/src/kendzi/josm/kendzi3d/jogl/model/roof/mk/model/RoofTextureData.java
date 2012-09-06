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

    TextureData facadeTextrure;

    Color facadeCoror;

    TextureData roofTexture;

    Color roofCoror;

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
    /**
     * @return the facadeCoror
     */
    public Color getFacadeCoror() {
        return facadeCoror;
    }
    /**
     * @param facadeCoror the facadeCoror to set
     */
    public void setFacadeCoror(Color facadeCoror) {
        this.facadeCoror = facadeCoror;
    }
    /**
     * @return the roofCoror
     */
    public Color getRoofCoror() {
        return roofCoror;
    }
    /**
     * @param roofCoror the roofCoror to set
     */
    public void setRoofCoror(Color roofCoror) {
        this.roofCoror = roofCoror;
    }



}
