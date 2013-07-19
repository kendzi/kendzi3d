/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.texture;

import java.awt.image.BufferedImage;

import javax.media.opengl.GL;

import com.jogamp.opengl.util.texture.Texture;

/**
 * Textures cache.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public interface TextureCacheService {

    /**
     * Undefined texture.
     */
    public static final String TEXTURES_UNDEFINED_PNG = "/textures/undefined.png";

    /**
     * Get texture from cache or load it to cache.
     * 
     * @param pGl OpenGl context
     * @param pFileName file name from
     * 
     * @return texture texture object
     */
    public Texture getTexture(GL pGl, String pFileName);

    /**
     * Test if texture exist in cache.
     * 
     * @param pFileName name of texture
     * @return if texture exist
     */
    public boolean isTexture(String pFileName);

    /**
     * Clean up all textures from cache.
     */
    public void clear();

    /**
     * Get texture image from cache.
     *
     * @param pFileName file name from
     * 
     * @return texture
     */
    public BufferedImage getImage(String pFileName);

}
