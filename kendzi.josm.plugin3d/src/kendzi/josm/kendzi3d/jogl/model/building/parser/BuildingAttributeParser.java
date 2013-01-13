package kendzi.josm.kendzi3d.jogl.model.building.parser;

import java.awt.Color;

import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeKeys;
import kendzi.josm.kendzi3d.jogl.model.building.model.WindowGridBuildingElement;
import kendzi.josm.kendzi3d.service.ColorTextureBuilder;
import kendzi.josm.kendzi3d.service.TextureLibraryService;
import kendzi.josm.kendzi3d.service.TextureLibraryService.TextureLibraryKey;
import kendzi.josm.kendzi3d.util.StringUtil;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

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
        String name = parseFacadeMaterialName(p1);
        if (name != null) {
            return name;
        }
        return parseFacadeMaterialName(p2);
    }

    /** Gets facade texture.
     * @param primitive
     * @return facade texture data
     */
    public static String parseFacadeMaterialName(OsmPrimitive primitive) {

        String facadeMaterial = OsmAttributeKeys.BUILDING_MATERIAL.primitiveValue(primitive);

        if (StringUtil.isBlankOrNull(facadeMaterial)) {
            facadeMaterial = OsmAttributeKeys.BUILDING_FACADE_MATERIAL.primitiveValue(primitive);
        }

        if (StringUtil.isBlankOrNull(facadeMaterial)) {
            facadeMaterial = OsmAttributeKeys.FACADE_MATERIAL.primitiveValue(primitive);
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

    /** Gets facade texture.
     * @param primitive
     * @return facade texture data
     */
    public static String parseRoofMaterialName(OsmPrimitive primitive) {


        String roofMaterial = OsmAttributeKeys.ROOF_MATERIAL.primitiveValue(primitive);
        if (StringUtil.isBlankOrNull(roofMaterial)) {
            roofMaterial = OsmAttributeKeys.BUILDING_ROOF_MATERIAL.primitiveValue(primitive);
        }

        return roofMaterial;
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

        String facadeColor = OsmAttributeKeys.BUILDING_FACADE_COLOR.primitiveValue(primitive);
        if (StringUtil.isBlankOrNull(facadeColor)) {
            facadeColor = OsmAttributeKeys.BUILDING_FACADE_COLOUR.primitiveValue(primitive);
        }
        if (StringUtil.isBlankOrNull(facadeColor)) {
            facadeColor = OsmAttributeKeys.BUILDING_COLOR.primitiveValue(primitive);
        }
        if (StringUtil.isBlankOrNull(facadeColor)) {
            facadeColor = OsmAttributeKeys.BUILDING_COLOUR.primitiveValue(primitive);
        }

        if (!StringUtil.isBlankOrNull(facadeColor)) {
            return ColorTextureBuilder.parseColor(facadeColor);
        }
        return null;
    }



    /** Gets roof colour.
     * @param primitive
     * @return roof colour
     */
    public static Color parseRoofColour(OsmPrimitive primitive) {

        String roofColor = OsmAttributeKeys.ROOF_COLOUR.primitiveValue(primitive);
        if (StringUtil.isBlankOrNull(roofColor)) {
            roofColor = OsmAttributeKeys.ROOF_COLOR.primitiveValue(primitive);
        }
        if (StringUtil.isBlankOrNull(roofColor)) {
            roofColor = OsmAttributeKeys.BUILDING_ROOF_COLOUR.primitiveValue(primitive);
        }
        if (StringUtil.isBlankOrNull(roofColor)) {
            roofColor = OsmAttributeKeys.BUILDING_ROOF_COLOR.primitiveValue(primitive);
        }

        if (!StringUtil.isBlankOrNull(roofColor)) {
            return ColorTextureBuilder.parseColor(roofColor);
        }
        return null;
    }






    public static Double parseMaxHeight(OsmPrimitive p1) {
         Double height = ModelUtil.getHeight(p1, null);
         if (height != null) {
             return height;
         }

         return ModelUtil.parseHeight(OsmAttributeKeys.BUILDING_HEIGHT.primitiveValue(p1), null);
    }

//    public static Double parseMaxHeight(OsmPrimitive p1, OsmPrimitive p2) {
//        Double height = ModelUtil.getHeight(p1, null);
//        if (height != null) {
//            return height;
//        }
//
//        return ModelUtil.getHeight(p2, null);
//    }

    public static Double parseMinHeight(OsmPrimitive p1) {
        return ModelUtil.getMinHeight(p1, null);
    }

//    public static Double parseMinHeight(OsmPrimitive p1, OsmPrimitive p2) {
//        Double height = ModelUtil.getMinHeight(p1, null);
//        if (height != null) {
//            return height;
//        }
//
//        return ModelUtil.getMinHeight(p2, null);
//    }

    public static Integer parseMaxLevel(OsmPrimitive p1) {
        Integer level = roundToInteger(
                ModelUtil.getNumberAttribute(p1, OsmAttributeKeys.BUILDING_MAX_LEVEL.getKey(), null));

        if (level == null) {
            level = roundToInteger(
                    ModelUtil.getNumberAttribute(p1, OsmAttributeKeys.BUILDING_LEVELS.getKey(), null));
        }

        if (level == null) {
            level = roundToInteger(
                    ModelUtil.getNumberAttribute(p1, OsmAttributeKeys.BUILDING_LEVELS_ABOVEGROUND.getKey(), null));
        }

        return level;
    }

    private static Integer roundToInteger(Double d) {
        if (d == null) {
            return null;
        }
        return (int) Math.round(d);
    }

//    public static Integer parseMaxLevel(OsmPrimitive p1, OsmPrimitive p2) {
//        Integer level = parseMaxLevel(p1);
//        if (level != null) {
//            return level;
//        }
//
//        return parseMaxLevel(p2);
//    }

    public static Integer parseMinLevel(OsmPrimitive p1) {
        Integer level = roundToInteger(ModelUtil.getNumberAttribute(p1, OsmAttributeKeys.BUILDING_MIN_LEVEL.getKey(), null));

        if (level == null) {
            level = roundToInteger(ModelUtil.getNumberAttribute(p1, OsmAttributeKeys.BUILDING_LEVELS_UNDERGROUND.getKey(), null));
            if (level != null) {
                level = -level;
            }
        }

        return level;
    }

//    public static Integer parseMinLevel(OsmPrimitive p1, OsmPrimitive p2) {
//        Integer level = parseMinLevel(p1);
//        if (level != null) {
//            return level;
//        }
//
//        return parseMinLevel(p2);
//    }


    public static WindowGridBuildingElement parseWallWindowsColumns(Way w) {

        String windowsCols = OsmAttributeKeys.WINDOWS.primitiveValue(w);

        if (StringUtil.isBlankOrNull(windowsCols)) {
            return null;
        }

        Integer cols = null;

        try {
            cols = Integer.parseInt(windowsCols);
        } catch (Exception e) {
            //
        }
        if (cols != null) {
            return new WindowGridBuildingElement(cols);
        }

        return null;
    }
}
