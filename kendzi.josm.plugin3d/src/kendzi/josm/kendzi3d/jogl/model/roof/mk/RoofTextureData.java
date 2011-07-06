/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk;

import kendzi.josm.kendzi3d.jogl.model.TextureData;

public class RoofTextureData {

    public final static int FACADE_TEXTRURE_INDEX = 0;
    public final static int ROOF_TEXTRURE_INDEX = 1;


    TextureData facadeTextrure;
    TextureData roofTexture;
    /**
     * @return the facadeTextrure
     */
    public TextureData getFacadeTextrure() {
        return facadeTextrure;
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
        return roofTexture;
    }
    /**
     * @param roofTexture the roofTexture to set
     */
    public void setRoofTexture(TextureData roofTexture) {
        this.roofTexture = roofTexture;
    }



}
