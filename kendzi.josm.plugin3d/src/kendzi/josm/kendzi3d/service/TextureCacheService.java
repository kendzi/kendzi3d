/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import org.apache.log4j.Logger;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class TextureCacheService {

    /**
     * Undefined texture.
     */
    public static final String TEXTURES_UNDEFINED_PNG = "/textures/undefined.png";

    /** Log. */
    private static final Logger log = Logger.getLogger(TextureCacheService.class);

    private static TextureCacheService textureCache = null;

    private Map<String, Texture> cache = new HashMap<String, Texture>();

    private String textureDir = null;

    private boolean filter;

    private List<TextureBuilder> textureBuilderList = new ArrayList<TextureBuilder>();


    /** Set texture filter.
     * @param pEnabled enabled
     */
    public static void setTextureFilter(boolean pEnabled) {
        if (textureCache.filter != pEnabled) {
            textureCache.filter = pEnabled;
            textureCache.clear();
        }
    }


    /**
     * Initialize texture cache. It need plugin dir to load external textures.
     *
     * @param pTexDir
     *            plugin dir where are external textures
     */
    public static void initTextureCache(String pTexDir) {
        textureCache = new TextureCacheService(pTexDir);
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
    public static Texture getTexture(String pName) {
        return textureCache.get(pName);
    }
    /**
     * Get texture from cache or load it to cache.
     *
     * @param pName
     *            file name from: <br>
     *            1. directory {PLUGIN_DIR_NAME}/textures <br>
     *            2. from resources from jar in dir {PLUGIN_JAR}/textures <br>
     * @return texture
     */
    public static Texture getTextureFromDir(String pName) {
        return textureCache.get("/textures/" + pName);
    }

    /**
     * Clean up all textures from cache.
     */
    public static void clearTextures() {
        textureCache.clear();
    }

    /**
     * Add texture builder.
     * @param pTextureBuilder texture builder
     */
    public static void addTextureBuilder(TextureBuilder pTextureBuilder) {
        textureCache.textureBuilderList.add(pTextureBuilder);
    }

 	public TextureCacheService(String pTexDir) {
        this.textureDir = pTexDir;
    }

    /**
     * DONT use Only for test!!
     * @throws IOException
     *
     * @depricated
     */
    public static void setTexture(String pName, BufferedImage pImg) throws IOException {
        textureCache.addTexture(pName, pImg, textureCache.filter);
    }

    /**
     * DONT use Only for test!!
     * @throws IOException
     *
     * @depricated
     */
    public static void setTexture(String pName, Texture pImg) {
        textureCache.addTexture(pName, pImg, textureCache.filter);
    }


    /** Test if texture exist in cache.
     * @param pName name of texture
     * @return if texture exist
     */
    public static boolean isTexture(String pName) {
        return textureCache.isText(pName);
    }





    /**
     * Clean up all textures from cache.
     */
    public void clear() {
        this.cache.clear();
    }


    /** Test if texture exist in cache.
     * @param pName name of texture
     * @return if texture exist
     */
    public boolean isText(String pName) {
        return null != this.cache.get(pName);
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

            texture = loadTexture(pName, this.filter);

            if (texture == null && pName != null && this.textureBuilderList != null) {
                //builders

                for (TextureBuilder tb : this.textureBuilderList) {
                    if (pName.startsWith(tb.getBuilderPrefix())) {
                        texture = tb.buildTexture(pName);
                        if (texture != null) {
                            break;
                        }
                    }
                }
            }

            if (texture == null) {
                texture = loadTexture(TEXTURES_UNDEFINED_PNG, this.filter);
            }

            this.cache.put(pName, texture);

        }
        return texture;
    }


    public void addTexture(String pName, Texture img, boolean filter) {

        Texture tex = img;

        this.cache.put(pName, tex);

    }
    public void addTexture(String pName, BufferedImage img, boolean filter) throws IOException {

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        ImageIO.write(img, "png", baos);
//
//        baos.close();
//
//        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        Texture tex = null;

        GLProfile glp = GLProfile.get(GLProfile.GL2);
        tex = AWTTextureIO.newTexture(glp, img, filter);

        //tex = TextureIO.newTexture(bais, filter, null);

        if (tex == null) {
            log.error("error importing buffered image");
            // TODO load default empty texture
            // XXX never throw exeption, allways load default texture!!
            // throw new RuntimeException("faild load texture: " + fileName);
        } else {

            if (filter) {

            } else {
                tex.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
                tex.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
            }

        }

        this.cache.put(pName, tex);

    }

    public static Texture loadTexture(String pName, boolean filter) {

        // String fileName = pName;

//        URL textUrl = FileUrlReciverService.getResourceUrl(pName);
        URL textUrl = FileUrlReciverService.reciveFileUrl(pName);
        Texture tex = null;
        try {
            tex = TextureIO.newTexture(textUrl, filter, null);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error loading texture: " + pName + " texture url: " + textUrl);
        }

//
//        File f = new File(this.textureDir, fileName);
//
//        if (f.exists()) {
//            try {
//                tex = TextureIO.newTexture(f, false);
//            } catch (Exception e) {
//                e.printStackTrace();
//                log.error("Error loading texture " + fileName);
//            }
//        }
//        if (tex == null) {
//            try {
//                // URL fileUrl = Kendzi3DPlugin.getPluginResource(fileName);
//                URL fileUrl = TextureCache.class.getResource(fileName);
//
//                // tex = TextureIO.newTexture(new File(fileUrl.toURI()), false);
//                tex = TextureIO.newTexture(fileUrl, false, null);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                log.error("Error loading texture from jar " + fileName);
//            }
//        }
        if (tex == null) {
            log.error("no texture to load!!"  + " texture url: " + textUrl);
            // TODO load default empty texture
            // XXX never throw exeption, allways load default texture!!
            //throw new RuntimeException("faild load texture: " + fileName);
        } else {

//            tex.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
//            tex.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);


            if (filter) {
//          GL_LINEAR / GL_LINEAR_MIPMAP_LINEAR
//          tex.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
//          tex.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);

//          tex.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
//          tex.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);

            } else {
          tex.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
          tex.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
            }
//        tex.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);

//          tex.setTexParameteri(GL2.GL_GENERATE_MIPMAP, GL2.GL_TRUE);

//          tex.setUsingAutoMipmapGeneration
//          gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);



        }

        return tex;
    }

    /**
     * @return the filter
     */
    public boolean isFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(boolean filter) {
        this.filter = filter;
    }


    /**
     * @return the textureBuilderList
     */
    public List<TextureBuilder> getTextureBuilderList() {
        return textureBuilderList;
    }


    /**
     * @param textureBuilderList the textureBuilderList to set
     */
    public void setTextureBuilderList(List<TextureBuilder> textureBuilderList) {
        this.textureBuilderList = textureBuilderList;
    }
}
