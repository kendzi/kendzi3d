/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.factory;

import java.awt.Color;

import kendzi.jogl.model.geometry.material.Material;

public class MaterialFactory {

//    @Deprecated
//    public static Material emptyMaterial() {
//        Material m = new Material();
////        m.diffuseColor = new Color(0.8f, 0.8f, 0.8f, 1.0f);
////        m.ambientColor = new Color(0.2f, 0.2f, 0.2f, 1.0f);
////        m.specularColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
////        m.shininess = 0;
////        m.emissive = new Color(0.0f, 0.0f, 0.0f, 1.0f);
//
//        return m;
//    }

    /** Create default material.
     * @return default material
     */
    public static Material getDefaultMaterial() {

        Material m = new Material();
//        m.diffuseColor = new Color(0.8f, 0.8f, 0.8f, 1.0f);
//        m.ambientColor = new Color(0.5f, 0.5f, 0.5f, 1.0f);
//        m.specularColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
//
//        m.shininess = 0.0f;
//        m.emissive = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        return m;
    }

    /**
     * Create default material for texture0.
     *
     * @param pTexId
     *            texture id
     * @return material
     */
    public static Material createTextureMaterial(String pTexId) {
        Material m = getDefaultMaterial();
//        m.strFile = pTexId;
//        m.strName = pTexId;
        m.setTexture0(pTexId);
        return m;
    }

    public static Material createTextureColorMaterial(String pTexId, Color pColor) {
        Material m = getDefaultMaterial();
//        if (pColor != null) {
//            m.setAmbientDiffuse(new AmbientDiffuseComponent(pColor, pColor));
//        }
        m.setTexture0(pTexId);
        m.setTexture0Color(pColor);
        return m;
    }

}
