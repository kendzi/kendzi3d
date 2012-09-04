/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry.material;
import java.util.ArrayList;
import java.util.List;

public class Material {

    public static final AmbientDiffuseComponent AMBIENT_DIFFUSE_COMPONENT = new AmbientDiffuseComponent();

    public static final OtherComponent OTHER_COMPONENT = new OtherComponent();

    AmbientDiffuseComponent ambientDiffuse = AMBIENT_DIFFUSE_COMPONENT;

    OtherComponent other = OTHER_COMPONENT;

    List<String> texturesComponent = new ArrayList<String>();

    public Material() {
        //
    }

    public Material(AmbientDiffuseComponent pAmbientDiffuseComponent) {
        this.ambientDiffuse = pAmbientDiffuseComponent;
    }

    public int getNumOfTextures() {
        return this.texturesComponent.size();
    }

    public void setTexture0(String key) {
        if (this.texturesComponent.size() == 0) {
            this.texturesComponent.add(key);
        } else {
            this.texturesComponent.set(0, key);
        }
    }

    public String getTexture0() {
        if (this.texturesComponent.size() == 0) {
            return null;
        }
        return this.texturesComponent.get(0);
    }

//    @Deprecated
//    public String strName;
//    @Deprecated
//    public String strFile;

//    @Deprecated
//    public Color ambientColor;
//    @Deprecated
//    public Color specularColor;
//    @Deprecated
//    public Color diffuseColor;
//    @Deprecated
//    public Color emissive = Color.BLACK;
//    @Deprecated
//    public float shininess;
//    @Deprecated
//    public int textureId;
//
//    @Deprecated
//    public MatType matType = MatType.TEXTURE0;
//    @Deprecated
//    public String texture1;
//    @Deprecated
//    public String texture2;



    public enum MatType {
        COLOR,
        TEXTURE0,
        COLOR_TEXTURE0,
        COLOR_TEXTURE2,
        COLOR_MultT0_MultT1,
    }



    /**
     * @return the ambientDiffuse
     */
    public AmbientDiffuseComponent getAmbientDiffuse() {
        return this.ambientDiffuse;
    }



    /**
     * @param ambientDiffuse the ambientDiffuse to set
     */
    public void setAmbientDiffuse(AmbientDiffuseComponent ambientDiffuse) {
        this.ambientDiffuse = ambientDiffuse;
    }



    /**
     * @return the other
     */
    public OtherComponent getOther() {
        return this.other;
    }



    /**
     * @param other the other to set
     */
    public void setOther(OtherComponent other) {
        this.other = other;
    }



    /**
     * @return the texturesComponent
     */
    public List<String> getTexturesComponent() {
        return this.texturesComponent;
    }



    /**
     * @param texturesComponent the texturesComponent to set
     */
    public void setTexturesComponent(List<String> texturesComponent) {
        this.texturesComponent = texturesComponent;
    }
}
