/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk;

import kendzi.josm.kendzi3d.dto.TextureData;


public class RoofTextureIndex {

    int materialIndexInModel;

    TextureData textureData;


    public RoofTextureIndex(int materialIndexInModel, TextureData textureData) {
        super();
        this.materialIndexInModel = materialIndexInModel;
        this.textureData = textureData;
    }

    /**
     * @return the materialIndexInModel
     */
    public int getMaterialIndexInModel() {
        return materialIndexInModel;
    }

    /**
     * @param materialIndexInModel the materialIndexInModel to set
     */
    public void setMaterialIndexInModel(int materialIndexInModel) {
        this.materialIndexInModel = materialIndexInModel;
    }

    /**
     * @return the textureData
     */
    public TextureData getTextureData() {
        return textureData;
    }

    /**
     * @param textureData the textureData to set
     */
    public void setTextureData(TextureData textureData) {
        this.textureData = textureData;
    }

}
