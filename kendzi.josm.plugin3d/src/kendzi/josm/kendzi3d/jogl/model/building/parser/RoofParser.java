package kendzi.josm.kendzi3d.jogl.model.building.parser;

import java.util.Map;

import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.roof.DormerRoof;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;

import org.openstreetmap.josm.data.osm.Way;

public class RoofParser {

    public static DormerRoofModel parse(Way way, Perspective3D perspective
            /*, TextureData facdeTexture*/) {

        Map<String, String> keys = way.getKeys();

        DormerRoofModel roof = DormerRoof.parseDormerRoof(way, perspective);

/*
        RoofTextureData rtd = new RoofTextureData();
        rtd.setFacadeTextrure(facdeTexture);
        rtd.setRoofTexture(DormerRoof.getRoofTexture(way, null));
*/
        return roof;
    }

}
