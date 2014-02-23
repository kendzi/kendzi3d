/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi3d.light.dao;

import java.awt.Color;

import kendzi3d.light.dto.LightConfiguration;

/**
 * Service for storing light configuration.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public interface LightDao {

    static final double DEFAULT_DIRECTION = 180;

    static final double DEFAULT_ANGLE = 45;

    static final Color DEFAULT_AMBIENT_COLOR = new Color(0.5f, 0.5f, 0.5f, 1f);

    static final Color DEFAULT_DIFFUSE_COLOR = new Color(1f, 1f, 1f, 1f);

    /**
     * Load light source configuration.
     * 
     * @return light location configuration
     */
    LightConfiguration load();

    /**
     * Save light source configuration.
     * 
     * @param lightLocation
     *            light location configuration
     */
    void save(LightConfiguration lightLocation);

}
