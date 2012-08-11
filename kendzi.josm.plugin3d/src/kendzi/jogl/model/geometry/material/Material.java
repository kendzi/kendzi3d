/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry.material;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Material {

    public static final AmbientDiffuseComponent AMBIENT_DIFFUSE_COMPONENT = new AmbientDiffuseComponent();

    public static final OtherComponent OTHER_COMPONENT = new OtherComponent();

    AmbientDiffuseComponent ad = AMBIENT_DIFFUSE_COMPONENT;

    OtherComponent oc = OTHER_COMPONENT;

    List<String> texturesComponent = new ArrayList<String>();

    public int getNumOfTextures() {
        return texturesComponent.size();
    }

    @Deprecated
    public String strName;
    @Deprecated
    public String strFile;

    @Deprecated
    public Color ambientColor;
    @Deprecated
    public Color specularColor;
    @Deprecated
    public Color diffuseColor;
    @Deprecated
    public Color emissive = Color.BLACK;
    @Deprecated
    public float shininess;
    @Deprecated
    public int textureId;

    @Deprecated
    public MatType matType = MatType.TEXTURE0;
    @Deprecated
    public String texture1;
    @Deprecated
    public String texture2;



    public enum MatType {
        COLOR,
        TEXTURE0,
        COLOR_TEXTURE0,
        COLOR_TEXTURE2,
        COLOR_MultT0_MultT1,
    }
}
