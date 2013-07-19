package kendzi.kendzi3d.josm.model.attribute;

import kendzi.util.StringUtil;

import org.openstreetmap.josm.data.osm.OsmPrimitive;


public class OsmAttributeUtil {

    public static String primitiveValue(OsmPrimitive primitive, OsmAttributeKeys key) {
        if (key == null || primitive == null) {
            return null;
        }

        return primitive.get(key.getKey());
    }

    public static boolean primitiveKeyHaveAnyValue(OsmPrimitive primitive, OsmAttributeKeys key) {
        if (primitive == null || key == null) {
            return false;
        }
        String valu = primitive.get(key.getKey());

        if (StringUtil.isBlankOrNull(valu)) {
            return false;
        }
        return true;
    }

    //    public String parsePrimitive(OsmPrimitive primitive) {
    //        if (this.key == null || primitive == null) {
    //            return null;
    //        }
    //
    //        return primitive.get(this.key);
    //    }
}
