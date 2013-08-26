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

    private AmbientDiffuseComponent ambientDiffuse = AMBIENT_DIFFUSE_COMPONENT;

    private OtherComponent other = OTHER_COMPONENT;

    private List<String> texturesComponent = new ArrayList<String>();

    private Color texture0Color = null; //Color.WHITE;

    public Material() {
        //
    }

    public Material(AmbientDiffuseComponent pAmbientDiffuseComponent) {
        this.ambientDiffuse = pAmbientDiffuseComponent;
    }

    /**
     * Number of textures.
     * 
     * @return number of textures
     */
    public int getNumOfTextures() {
        return this.texturesComponent.size();
    }

    /**
     * Set first texture.
     * 
     * @param key texture key
     */
    public void setTexture0(String key) {
        if (this.texturesComponent.size() == 0) {
            this.texturesComponent.add(key);
        } else {
            this.texturesComponent.set(0, key);
        }
    }

    /**
     * Gets first texture.
     * 
     * @return first texture
     */
    public String getTexture0() {
        if (this.texturesComponent.size() == 0) {
            return null;
        }
        return this.texturesComponent.get(0);
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

    /**
     * @return the texture0Color
     */
    public Color getTexture0Color() {
        return texture0Color;
    }

    /**
     * @param texture0Color the texture0Color to set
     */
    public void setTexture0Color(Color texture0Color) {
        this.texture0Color = texture0Color;
    }
}
