package kendzi.jogl.texture.builder;

import java.awt.image.BufferedImage;

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
     * @throws Exception
     */
    Texture buildTexture(String pKey) throws Exception;

    /**
     * Image for model export.
     * @param pKey key
     * @return build image
     */
    BufferedImage buildImage(String pKey);


}
