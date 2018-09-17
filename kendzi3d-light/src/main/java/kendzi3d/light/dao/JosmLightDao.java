/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi3d.light.dao;

import java.awt.Color;

import org.openstreetmap.josm.spi.preferences.Config;

import kendzi.util.ParseUtil;
import kendzi3d.light.dto.LightConfiguration;

/**
 * Light configuration stored in memory.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public class JosmLightDao implements LightDao {
    /**
     * Property key for list of resources used by models library.
     */
    private static final String KENDZI_3D_LIGHT_DIRECTION = "kendzi3d.light.direction";
    private static final String KENDZI_3D_LIGHT_ANGLE = "kendzi3d.light.angle";
    private static final String KENDZI_3D_LIGHT_AMBIENT_COLOR = "kendzi3d.light.ambient_color";
    private static final String KENDZI_3D_LIGHT_DIFFUSE_COLOR = "kendzi3d.light.diffuse_color";

    @Override
    public LightConfiguration load() {
        LightConfiguration conf = new LightConfiguration();
        conf.setDirection( Config.getPref().getDouble(KENDZI_3D_LIGHT_DIRECTION, DEFAULT_DIRECTION));
        conf.setAngle( Config.getPref().getDouble(KENDZI_3D_LIGHT_ANGLE, DEFAULT_ANGLE));

        Color ambientColor = ParseUtil.parseHexColor( Config.getPref().get(KENDZI_3D_LIGHT_AMBIENT_COLOR, ""));
        if (ambientColor == null) {
            ambientColor = DEFAULT_AMBIENT_COLOR;
        }
        conf.setAmbientColor(ambientColor);

        Color diffuseColor = ParseUtil.parseHexColor( Config.getPref().get(KENDZI_3D_LIGHT_DIFFUSE_COLOR, ""));
        if (diffuseColor == null) {
            diffuseColor = DEFAULT_DIFFUSE_COLOR;
        }
        conf.setDiffuseColor(diffuseColor);

        return conf;
    }

    @Override
    public void save(LightConfiguration lightLocation) {

         Config.getPref().putDouble(KENDZI_3D_LIGHT_DIRECTION, lightLocation.getDirection());
         Config.getPref().putDouble(KENDZI_3D_LIGHT_ANGLE, lightLocation.getAngle());
         Config.getPref().put(KENDZI_3D_LIGHT_AMBIENT_COLOR, formatHexColor(lightLocation.getAmbientColor()));
         Config.getPref().put(KENDZI_3D_LIGHT_DIFFUSE_COLOR, formatHexColor(lightLocation.getDiffuseColor()));
    }

    private String formatHexColor(Color color) {
        String rgb = Integer.toHexString(color.getRGB());
        return "#" + rgb.substring(2, rgb.length());
    }
}
