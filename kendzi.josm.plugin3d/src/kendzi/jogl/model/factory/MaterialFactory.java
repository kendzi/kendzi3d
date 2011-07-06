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

import kendzi.jogl.model.geometry.Material;

public class MaterialFactory {

    public static Material emptyMaterial() {
        Material m = new Material();
        m.diffuseColor = new Color(0.8f, 0.8f, 0.8f, 1.0f);
        m.ambientColor = new Color(0.2f, 0.2f, 0.2f, 1.0f);
        m.specularColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        m.shininess = 0;
        m.emissive = new Color(0.0f, 0.0f, 0.0f, 1.0f);

        return m;
    }
    /** Create material for texture.
   * @param pTexId texture id
   * @return material
   */
    public static Material createTextureMaterial(String pTexId) {
        Material m = new Material();
        m.strFile = pTexId;
        m.strName = pTexId;
        return m;
    }

}
