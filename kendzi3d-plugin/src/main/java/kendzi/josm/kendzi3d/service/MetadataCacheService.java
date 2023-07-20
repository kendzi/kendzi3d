/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.service;

import com.google.inject.Inject;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import kendzi.josm.kendzi3d.metadata.ModelMetadata;
import kendzi.josm.kendzi3d.metadata.TextureMetadata;
import kendzi.kendzi3d.resource.inter.ResourceService;
import kendzi.util.UrlUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Store metadata for 3d object like default height, textures. Metadata are
 * stored in propertis files.
 * 
 * @author Tomasz Kędziora (kendzi)
 * 
 */
public class MetadataCacheService {

    /** Log. */
    private static final Logger log = LogManager.getLogger(MetadataCacheService.class);

    /**
     * File url reciver service.
     */
    private ResourceService urlReciverService;

    private final Map<String, TextureMetadata> cacheTexture = new HashMap<>();

    private final Map<String, ModelMetadata> cacheModel = new HashMap<>();

    private Properties metadataProperties;

    /**
     * Constructor.
     * 
     * @param pUrlReciverService
     *            url reciver service
     */
    @Inject
    private MetadataCacheService(ResourceService pUrlReciverService) {
        this.urlReciverService = pUrlReciverService;

        init();
    }

    /**
     * Init metadata cache service.
     */
    public void init() {
        loadMetadataProperties();
    }

    /**
     * Read textures properties.
     */
    public void loadMetadataProperties() {
        this.metadataProperties = new Properties();

        loadFile("/metadata.properties");

        loadFile("/textures/wikimetadata.properties");
    }

    /**
     * @param fileName
     */
    public void loadFile(String fileName) {

        try {
            URL fileUrl = this.urlReciverService.resourceToUrl(fileName);

            if (!UrlUtil.existUrl(fileUrl)) {
                log.warn("cant find url for file: " + fileName);
                return;
            }

            this.metadataProperties.load(fileUrl.openStream());

        } catch (Exception e) {
            log.error("error loading metadata file: " + fileName, e);
        }
    }

    /**
     * Clean up all textures from cache.
     */
    public void clear() {
        this.cacheModel.clear();
        this.cacheTexture.clear();
        loadMetadataProperties();
    }

    /**
     * Gets metadata for texture from cache or load it from properties file.
     * Location of properties file is
     * {PLUGIN_DIR_NAME}/resources/metadata.properties
     * 
     * @param pId
     *            name of texture, "textures." prefix is added.
     * @return metadata of texture
     */
    public TextureMetadata getTexture(String pId) {
        String key = "textures." + pId;
        TextureMetadata textureMetadata = this.cacheTexture.get(key);
        if (textureMetadata == null) {
            textureMetadata = loadTextureMetadata(key);
            this.cacheTexture.put(key, textureMetadata);
        }
        return textureMetadata;
    }

    /**
     * Gets metadata for model from cache or load it from properties file. Location
     * of properties file is {PLUGIN_DIR_NAME}/resources/metadata.properties
     * 
     * @param pId
     *            of model
     * @return metadata of model
     */
    public ModelMetadata getModel(String pId) {
        String key = "models." + pId;
        ModelMetadata modelMetadata = this.cacheModel.get(key);
        if (modelMetadata == null) {
            modelMetadata = loadModelMetadata(key);
            this.cacheModel.put(key, modelMetadata);
        }
        return modelMetadata;
    }

    /**
     * Loads metadata of texture from file.
     * 
     * @param pName
     *            name of texture
     * @return metadata of texture
     */
    private TextureMetadata loadTextureMetadata(String pName) {
        TextureMetadata textureMetadata = new TextureMetadata();

        textureMetadata.setSizeU(parseDouble(pName + ".sizeU", 1d));
        textureMetadata.setSizeV(parseDouble(pName + ".sizeV", 1d));
        textureMetadata.setFile(this.metadataProperties.getProperty(pName + ".file", null));

        // TODO
        return textureMetadata;
    }

    /**
     * Loads metadata of texture from file.
     * 
     * @param pName
     *            name of texture
     * @return metadata of texture
     */
    private ModelMetadata loadModelMetadata(String pName) {
        ModelMetadata modelMetadata = new ModelMetadata();
        modelMetadata.setFile(this.metadataProperties.getProperty(pName + ".file", null));

        // TODO
        return modelMetadata;
    }

    /**
     * Parse Double value for key.
     * 
     * @param pKey
     *            properties key
     * @param pDefaultValue
     *            default value
     * @return double value
     */
    private Double parseDouble(String pKey, Double pDefaultValue) {
        Double value = null;
        try {
            String property = this.metadataProperties.getProperty(pKey, null);
            if (property != null) {
                value = Double.valueOf(property);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (value == null) {
            value = pDefaultValue;
        }
        return value;
    }

    public Double getPropertitesDouble(String pKey, Double pDefaultValue) {
        return parseDouble(pKey, pDefaultValue);
    }

    public String getPropertites(String pKey, String pDefaultValue) {
        return this.metadataProperties.getProperty(pKey, pDefaultValue);
    }

    public String getPropertites(String pPattern, String pDefaultValue, String... pKeyParts) {
        return getPropertites(pPattern, pDefaultValue, true, pKeyParts);
    }

    public String getPropertites(String pPattern, String pDefaultValue, boolean pTakeUnknown, String... pKeyParts) {

        if (pTakeUnknown && pKeyParts != null) {
            for (int i = 0; i < pKeyParts.length; i++) {

                if (pKeyParts[i] == null) {
                    pKeyParts[i] = "unknown";
                }
            }
        }

        String key = MessageFormat.format(pPattern, (Object[]) pKeyParts);

        return this.metadataProperties.getProperty(key, pDefaultValue);
    }

    public Double getPropertitesDouble(String pPattern, Double pDefaultValue, String... pKeyParts) {
        return getPropertitesDouble(pPattern, pDefaultValue, true, pKeyParts);
    }

    public Double getPropertitesDouble(String pPattern, Double pDefaultValue, boolean pTakeUnknown, String... pKeyParts) {

        if (pTakeUnknown && pKeyParts != null) {
            for (int i = 0; i < pKeyParts.length; i++) {

                if (pKeyParts[i] == null) {
                    pKeyParts[i] = "unknown";
                }
            }
        }

        String key = MessageFormat.format(pPattern, (Object[]) pKeyParts);

        return parseDouble(key, pDefaultValue);
    }

    /**
     * @return the resourceService
     */
    public ResourceService getFileUrlReciverService() {
        return this.urlReciverService;
    }

    /**
     * @param resourceService
     *            the resourceService to set
     */
    public void setFileUrlReciverService(ResourceService resourceService) {
        this.urlReciverService = resourceService;
    }

}
