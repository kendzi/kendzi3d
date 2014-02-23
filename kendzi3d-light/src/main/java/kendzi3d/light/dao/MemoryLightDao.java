/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi3d.light.dao;

import kendzi3d.light.dto.LightConfiguration;

/**
 * Light configuration stored in memory.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public class MemoryLightDao implements LightDao {

    private LightConfiguration lightLocation;

    @Override
    public LightConfiguration load() {
        return lightLocation;
    }

    @Override
    public void save(LightConfiguration lightLocation) {
        this.lightLocation = lightLocation;
    }

}
