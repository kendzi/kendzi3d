package kendzi.josm.kendzi3d.service;

import java.awt.Image;

import com.jogamp.opengl.util.texture.Texture;

/**
 * Texture builder.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public interface TextureBuilder {

    /**
     * Builder prefix.
     * @return prefix
     */
    String getBuilderPrefix();


    /**
     * Generate texture.
     * @param pKey key
     * @return build texture
     */
    Texture buildTexture(String pKey);

    /**
     * Image for model export.
     * @param pKey key
     * @return build image
     */
    Image buildImage(String pKey);


}
