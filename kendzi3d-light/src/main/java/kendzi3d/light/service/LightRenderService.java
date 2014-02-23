/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi3d.light.service;

/**
 * Return data for rendering light.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public interface LightRenderService {

    /**
     * Get data array for diffuse light color.
     * 
     * @return diffuse light color
     */
    float[] getDiffuseLightColor();

    /**
     * Get data array for ambient light color.
     * 
     * @return ambient light color
     */
    float[] getAmbientLightColor();

    /**
     * Get data array for light position.
     * 
     * @return light position
     */
    float[] getLightPosition();
}
