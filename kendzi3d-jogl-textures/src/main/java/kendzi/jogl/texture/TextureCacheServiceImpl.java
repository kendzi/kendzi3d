/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import kendzi.jogl.glu.GLException;
import kendzi.jogl.glu.GLU;
import kendzi.jogl.texture.builder.TextureBuilder;
import kendzi.jogl.util.texture.Texture;
import kendzi.jogl.util.texture.TextureIO;
import kendzi.kendzi3d.resource.inter.ResourceService;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextureCacheServiceImpl implements kendzi.jogl.texture.TextureCacheService {

    /** Log. */
    private static final Logger log = LoggerFactory.getLogger(TextureCacheServiceImpl.class);

    /**
     * File url reciver service.
     */
    ResourceService resourceService;

    private final Map<String, Texture> cache = new HashMap<>();

    private boolean filter;

    private List<TextureBuilder> textureBuilderList = new ArrayList<>();

    /**
     * Set texture filter.
     *
     * @param pEnabled
     *            enabled
     */
    public void setTextureFilter(boolean pEnabled) {
        if (this.filter != pEnabled) {
            this.filter = pEnabled;
            this.clear();
        }
    }

    /**
     * Get texture from cache or load it to cache.
     *
     * @param pName
     *            file name from: <br>
     *            1. directory {PLUGIN_DIR_NAME}/ <br>
     *            2. from resources from jar in dir {PLUGIN_JAR}/ <br>
     * @return texture
     */
    @Override
    public Texture getTexture(String pName) {
        return this.get(pName);
    }

    /**
     * Get texture image from.
     *
     * @param pName
     *            file name from: <br>
     *            1. directory {PLUGIN_DIR_NAME}/ <br>
     *            2. from resources from jar in dir {PLUGIN_JAR}/ <br>
     * @return texture
     */
    @Override
    public BufferedImage getImage(String pName) {
        return loadImage(pName);
    }

    private BufferedImage loadImage(String pName) {
        BufferedImage texture = null;

        if (pName != null && this.textureBuilderList != null) {
            // builders

            for (TextureBuilder tb : this.textureBuilderList) {
                if (pName.startsWith(tb.getBuilderPrefix())) {

                    texture = tb.buildImage(pName);

                    if (texture != null) {
                        return texture;
                    }
                }
            }

            try {
                return loadTextureImageFile(pName);
            } catch (Exception e) {
                log.error("can't load texture", e);
                return null;
            }
        }

        return null;
    }

    public BufferedImage loadTextureImageFile(String name) throws GLException, IOException {

        if (name == null) {
            return null;
        }

        URL textUrl = this.resourceService.resourceToUrl(name);
        if (textUrl == null) {
            log.info("No file to load: " + name);
            return null;
        }
        // return Toolkit.createImage(textUrl);
        return ImageIO.read(textUrl);
    }

    /**
     * Add texture builder.
     *
     * @param pTextureBuilder
     *            texture builder
     */
    public void addTextureBuilder(TextureBuilder pTextureBuilder) {
        this.textureBuilderList.add(pTextureBuilder);
    }

    /**
     * DONT use Only for test!!
     *
     * @throws IOException
     *
     * @depricated
     */
    public void setTexture(String pName, BufferedImage pImg) throws IOException {
        this.addTexture(pName, pImg, this.filter);
    }

    /**
     * DONT use Only for test!!
     *
     * @throws IOException
     *
     * @depricated
     */
    public void setTexture(String pName, Texture pImg) {
        this.addTexture(pName, pImg, this.filter);
    }

    /**
     * Test if texture exist in cache.
     *
     * @param pName
     *            name of texture
     * @return if texture exist
     */
    @Override
    public boolean isTexture(String pName) {
        return null != this.cache.get(pName);
    }

    /**
     * Clean up all textures from cache.
     */
    @Override
    public void clear() {
        this.cache.clear();
    }

    /**
     * Try to get texture. If it isn't laded it well be loaded from:<br>
     * 1. directory {PLUGIN_DIR_NAME}/textures <br>
     * 2. from resources from jar in dir {PLUGIN_JAR}/textures <br>
     *
     * @param pName
     * @return
     */
    public Texture get(String pName) {
        Texture texture = this.cache.get(pName);
        if (texture == null) {

            texture = loadTexture(pName);

            if (texture == null) {
                texture = loadTexture(TEXTURES_UNDEFINED_PNG);
            }

            if (texture == null) {
                log.error("no texture to load!!" + " texture url: " + pName);
            } else {
                setupFilter(texture);
            }

            this.cache.put(pName, texture);

        }
        return texture;
    }

    /**
     * @param texture
     */
    public void setupFilter(Texture texture) {
        if (filter) {
            // GL_LINEAR / GL_LINEAR_MIPMAP_LINEAR
            // tex.setTexParameteri(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            // tex.setTexParameteri(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

            // tex.setTexParameteri(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            // tex.setTexParameteri(GL11.GL_TEXTURE_MIN_FILTER,
            // GL11.GL_LINEAR_MIPMAP_LINEAR);

        } else {
            texture.setTexParameteri(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            texture.setTexParameteri(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        }
    }

    /**
     * @param pName
     * @return
     */
    public Texture loadTexture(String pName) {
        Texture texture = null;

        if (pName != null && this.textureBuilderList != null) {
            // builders

            for (TextureBuilder tb : this.textureBuilderList) {
                if (pName.startsWith(tb.getBuilderPrefix())) {
                    try {
                        texture = tb.buildTexture(pName);
                    } catch (Exception e) {
                        // GL11.getGL4().glGetErrorString
                        int errorCode = GL11.glGetError();
                        String errorStr = "";
                        errorStr = GLU.gluErrorString(errorCode);
                        System.err.println(errorStr);
                        log.error("Error loading texture: " + pName + " texture url: " + pName, e);
                    }

                    if (texture != null) {
                        return texture;
                    }
                }
            }

            try {
                return loadTextureFile(pName, this.filter);
            } catch (Exception e) {
                log.error("can't load texture", e);
                return null;
            }
        }

        return null;
    }

    public Texture loadTextureFile(String name, boolean filter) throws GLException, IOException {

        if (name == null) {
            return null;
        }

        URL textUrl = resourceService.resourceToUrl(name);
        if (textUrl == null) {
            log.info("No file to load: " + name);
            return null;
        }

        return TextureIO.newTexture(textUrl, filter, null);
    }

    public void addTexture(String pName, Texture img, boolean filter) {

        cache.put(pName, img);
    }

    public void addTexture(String pName, BufferedImage img, boolean filter) throws IOException {

        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //
        // ImageIO.write(img, "png", baos);
        //
        // baos.close();
        //
        // ByteArrayInputStream bais = new
        // ByteArrayInputStream(baos.toByteArray());

        Texture tex = null;

        // tex = TextureIO.newTexture(bais, filter, null);

        if (tex == null) {
            log.error("error importing buffered image");
            // TODO load default empty texture
            // XXX never throw exeption, allways load default texture!!
            // throw new RuntimeException("faild load texture: " + fileName);
        } else {

            if (filter) {

            } else {
                tex.setTexParameteri(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                tex.setTexParameteri(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            }

        }

        this.cache.put(pName, tex);

    }

    /**
     * @return the filter
     */
    public boolean isFilter() {
        return this.filter;
    }

    /**
     * @param filter
     *            the filter to set
     */
    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    /**
     * @return the textureBuilderList
     */
    public List<TextureBuilder> getTextureBuilderList() {
        return this.textureBuilderList;
    }

    /**
     * @param textureBuilderList
     *            the textureBuilderList to set
     */
    public void setTextureBuilderList(List<TextureBuilder> textureBuilderList) {
        this.textureBuilderList = textureBuilderList;
    }

    /**
     * @return the resourceService
     */
    public ResourceService getFileUrlReciverService() {
        return this.resourceService;
    }

    /**
     * @param resourceService
     *            the resourceService to set
     */
    public void setFileUrlReciverService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
}
