/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.dto;

/**
 * Point model configuration DTO.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class PointModelDTO {

    private String key;

    private boolean enabled;

    /**
     * Model file key.
     */
    private String model;

    /**
     * Model matcher.
     */
    private String matcher;

    /**
     * Model scale.
     */
    private String scale;

    /**
     * Model translation x.
     */
    private String translateX;
    /**
     * Model translation y.
     */
    private String translateY;
    /**
     * Model translation z.
     */
    private String translateZ;

    // direction

    /**
     * @return the model
     */
    public String getModel() {
        return this.model;
    }

    /**
     * @param model
     *            the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the matcher
     */
    public String getMatcher() {
        return this.matcher;
    }

    /**
     * @param matcher the matcher to set
     */
    public void setMatcher(String matcher) {
        this.matcher = matcher;
    }

    /**
     * @return the scale
     */
    public String getScale() {
        return this.scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(String scale) {
        this.scale = scale;
    }

    /**
     * @return the translateX
     */
    public String getTranslateX() {
        return this.translateX;
    }

    /**
     * @param translateX the translateX to set
     */
    public void setTranslateX(String translateX) {
        this.translateX = translateX;
    }

    /**
     * @return the translateY
     */
    public String getTranslateY() {
        return this.translateY;
    }

    /**
     * @param translateY the translateY to set
     */
    public void setTranslateY(String translateY) {
        this.translateY = translateY;
    }

    /**
     * @return the translateZ
     */
    public String getTranslateZ() {
        return this.translateZ;
    }

    /**
     * @param translateZ the translateZ to set
     */
    public void setTranslateZ(String translateZ) {
        this.translateZ = translateZ;
    }



}
