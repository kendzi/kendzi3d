/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry;
import java.awt.Color;

public class Material {

    public String strName;
    public String strFile;

    public Color ambientColor;
    public Color specularColor;
    public Color diffuseColor;
    public Color emissive = Color.BLACK;
    public float shininess;
    public int textureId;


//    public float shininess2;
//    public float transparency;
//    public float uTile;
//    public float vTile;
//    public float uOffset;
//    public float vOffset;
}
