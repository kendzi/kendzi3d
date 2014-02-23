/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi3d.light.service;

import kendzi3d.light.dto.LightConfiguration;

/**
 * Service for storing light configuration.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public interface LightStorageService {

    /**
     * Load light source configuration.
     * 
     * @return light location configuration
     */
    LightConfiguration load();

    /**
     * Load default light source configuration.
     * 
     * @return default light location configuration
     */
    LightConfiguration loadDefault();

    /**
     * Save light source configuration.
     * 
     * @param lightLocation
     *            light location configuration
     */
    void save(LightConfiguration lightLocation);

}
