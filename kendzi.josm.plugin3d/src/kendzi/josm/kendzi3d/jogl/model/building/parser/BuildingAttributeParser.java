package kendzi.josm.kendzi3d.jogl.model.building.parser;

import java.awt.Color;

import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.service.ColorTextureBuilder;
import kendzi.josm.kendzi3d.service.TextureLibraryService;
import kendzi.josm.kendzi3d.service.TextureLibraryService.TextureLibraryKey;
import kendzi.josm.kendzi3d.util.StringUtil;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class BuildingAttributeParser {



    /** Gets facade texture.
     * @param facadeMaterialName
     * @param pTextureLibraryService
     * @return facade texture data
     */
    public static TextureData parseFacadeTexture(String facadeMaterialName, TextureLibraryService pTextureLibraryService) {

        String facadeMaterial = facadeMaterialName;

        if (!StringUtil.isBlankOrNull(facadeMaterial)) {

            String textureKey = pTextureLibraryService.getKey(TextureLibraryKey.BUILDING_FACADE, facadeMaterial);
            return pTextureLibraryService.getTextureDefault(textureKey);
        }

        return null;
    }




    public static String parseFacadeName(OsmPrimitive p1, OsmPrimitive p2) {
        String name = parseFacadeName(p1);
        if (name != null) {
            return name;
        }
        return parseFacadeName(p2);
    }

    /** Gets facade texture.
     * @param primitive
     * @return facade texture data
     */
    public static String parseFacadeName(OsmPrimitive primitive) {

        String facadeMaterial = primitive.get("building:material");
        if (StringUtil.isBlankOrNull(facadeMaterial)) {
            facadeMaterial = primitive.get("building:facade:material");
        }
        if (StringUtil.isBlankOrNull(facadeMaterial)) {
            facadeMaterial = primitive.get("facade:material");
        }

        return facadeMaterial;

//        if (!StringUtil.isBlankOrNull(facadeMaterial)) {
//
//            String textureKey = pTextureLibraryService.getKey("buildings.facade_{0}", facadeMaterial);
//            return pTextureLibraryService.getTextureDefault(textureKey);
//        }
//
//        return null;
    }


    public static Color parseFacadeColour(OsmPrimitive p1, OsmPrimitive p2) {
        Color name = parseFacadeColour(p1);
        if (name != null) {
            return name;
        }
        return parseFacadeColour(p2);
    }

    /** Gets facade texture.
     * @param primitive
     * @return facade texture
     */
    public static Color parseFacadeColour(OsmPrimitive primitive) {

        String facadeColor = primitive.get("building:facade:color");
        if (StringUtil.isBlankOrNull(facadeColor)) {
            facadeColor = primitive.get("building:facade:colour");
        }
        if (StringUtil.isBlankOrNull(facadeColor)) {
            facadeColor = primitive.get("building:color");
        }
        if (StringUtil.isBlankOrNull(facadeColor)) {
            facadeColor = primitive.get("building:colour");
        }

        if (!StringUtil.isBlankOrNull(facadeColor)) {
            return ColorTextureBuilder.parseColor(facadeColor);
        }
        return null;
    }






    public static Double parseMaxHeight(OsmPrimitive p1) {
        return ModelUtil.getHeight(p1, null);
    }

    public static Double parseMaxHeight(OsmPrimitive p1, OsmPrimitive p2) {
        Double height = ModelUtil.getHeight(p1, null);
        if (height != null) {
            return height;
        }

        return ModelUtil.getHeight(p2, null);
    }

    public static Double parseMinHeight(OsmPrimitive p1) {
        return ModelUtil.getMinHeight(p1, null);
    }

    public static Double parseMinHeight(OsmPrimitive p1, OsmPrimitive p2) {
        Double height = ModelUtil.getMinHeight(p1, null);
        if (height != null) {
            return height;
        }

        return ModelUtil.getMinHeight(p2, null);
    }

    public static Double parseMaxLevel(OsmPrimitive p1) {
        Double level = ModelUtil.getNumberAttribute(p1, "building:max_level", null);
        if (level != null) {
            return level;
        }
        return ModelUtil.getNumberAttribute(p1, "building:levels", null);
    }

    public static Double parseMaxLevel(OsmPrimitive p1, OsmPrimitive p2) {
        Double level = parseMaxLevel(p1);
        if (level != null) {
            return level;
        }

        return parseMaxLevel(p2);
    }

    public static Double parseMinLevel(OsmPrimitive p1) {
        return ModelUtil.getNumberAttribute(p1, "building:min_level", null);
    }

    public static Double parseMinLevel(OsmPrimitive p1, OsmPrimitive p2) {
        Double level = parseMinLevel(p1);
        if (level != null) {
            return level;
        }

        return parseMinLevel(p2);
    }
}
