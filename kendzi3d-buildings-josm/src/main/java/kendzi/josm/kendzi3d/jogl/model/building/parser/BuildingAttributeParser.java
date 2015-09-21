/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.model.building.parser;

import java.awt.Color;

import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.TextureLibraryKey;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.util.ColorUtil;
import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.kendzi3d.buildings.model.WindowGridBuildingElement;
import kendzi.kendzi3d.josm.model.attribute.OsmAttributeKeys;
import kendzi.util.StringUtil;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Parser for building attributes.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public final class BuildingAttributeParser {

    /**
     * Utility con.
     */
    private BuildingAttributeParser() {
        //
    }

    /**
     * Gets facade texture.
     * 
     * @param facadeMaterialName facade material name
     * @param textureLibraryStorageService texture library storage service
     * @return facade texture data
     */
    public static TextureData parseFacadeTexture(String facadeMaterialName,
            TextureLibraryStorageService textureLibraryStorageService) {

        String facadeMaterial = facadeMaterialName;

        if (!StringUtil.isBlankOrNull(facadeMaterial)) {

            String textureKey = textureLibraryStorageService.getKey(TextureLibraryKey.BUILDING_FACADE, facadeMaterial);
            return textureLibraryStorageService.getTextureDefault(textureKey);
        }

        return null;
    }

    /**
     * Gets facade texture.
     * 
     * @param primitive osm primitive
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
    }

    /**
     * Gets floor texture.
     * 
     * @param primitive osm primitive
     * @return facade texture data
     */
    public static String parseFloorMaterialName(OsmPrimitive primitive) {
        return OsmAttributeKeys.FLOOR_MATERIAL.primitiveValue(primitive);
    }

    /**
     * Gets facade texture.
     * 
     * @param primitive osm primitive
     * @return facade texture data
     */
    public static String parseRoofMaterialName(OsmPrimitive primitive) {

        String roofMaterial = OsmAttributeKeys.ROOF_MATERIAL.primitiveValue(primitive);
        if (StringUtil.isBlankOrNull(roofMaterial)) {
            roofMaterial = OsmAttributeKeys.BUILDING_ROOF_MATERIAL.primitiveValue(primitive);
        }

        return roofMaterial;
    }

    /**
     * Gets facade texture.
     * 
     * @param primitive osm primitive
     * @return facade texture
     */
    public static Color parseFacadeColor(OsmPrimitive primitive) {

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
            return ColorUtil.parseColor(facadeColor);
        }
        return null;
    }

    /**
     * Gets floor texture.
     * 
     * @param primitive osm primitive
     * @return facade texture
     */
    public static Color parseFloorColor(OsmPrimitive primitive) {

        String facadeColor = OsmAttributeKeys.FLOOR_COLOR.primitiveValue(primitive);
        if (StringUtil.isBlankOrNull(facadeColor)) {
            facadeColor = OsmAttributeKeys.FLOOR_COLOUR.primitiveValue(primitive);
        }

        if (!StringUtil.isBlankOrNull(facadeColor)) {
            return ColorUtil.parseColor(facadeColor);
        }
        return null;
    }

    /**
     * Gets roof color.
     * 
     * @param primitive osm primitive
     * @return roof color
     */
    public static Color parseRoofColor(OsmPrimitive primitive) {

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
            return ColorUtil.parseColor(roofColor);
        }
        return null;
    }

    /**
     * Parse maximal height value.
     * 
     * @param osmPrimitive osm primitive
     * @return maximal height
     */
    public static Double parseMaxHeight(OsmPrimitive osmPrimitive) {
        Double height = ModelUtil.getHeight(osmPrimitive, null);
        if (height != null) {
            return height;
        }

        return ModelUtil.parseHeight(OsmAttributeKeys.BUILDING_HEIGHT.primitiveValue(osmPrimitive), null);
    }

    /**
     * Parse minimal height value.
     * 
     * @param osmPrimitive osm primitive
     * @return minimal height
     */
    public static Double parseMinHeight(OsmPrimitive osmPrimitive) {
        return ModelUtil.getMinHeight(osmPrimitive, null);
    }

    /**
     * Parse number of roof levels.
     * 
     * @param osmPrimitive osm primitive
     * @return number of roof levels
     */
    public static Integer parseRoofLevels(OsmPrimitive osmPrimitive) {
        Integer level = roundToInteger(ModelUtil.getNumberAttribute(osmPrimitive, OsmAttributeKeys.ROOF_LEVELS.getKey(), null));

        return level;
    }

    /**
     * Parse maximal number of levels.
     * 
     * @param osmPrimitive osm primitive
     * @return maximal number of levels
     */
    public static Integer parseMaxLevel(OsmPrimitive osmPrimitive) {
        Integer level = roundToInteger(ModelUtil.getNumberAttribute(osmPrimitive, OsmAttributeKeys.BUILDING_MAX_LEVEL.getKey(),
                null));

        if (level == null) {
            level = roundToInteger(ModelUtil.getNumberAttribute(osmPrimitive, OsmAttributeKeys.BUILDING_LEVELS.getKey(), null));
        }

        if (level == null) {
            level = roundToInteger(ModelUtil.getNumberAttribute(osmPrimitive,
                    OsmAttributeKeys.BUILDING_LEVELS_ABOVEGROUND.getKey(), null));
        }

        if (level != null) {

            Integer roofLevels = BuildingAttributeParser.parseRoofLevels(osmPrimitive);

            if (roofLevels != null) {
                level += roofLevels;
            }
        }

        return level;
    }

    private static Integer roundToInteger(Double d) {
        if (d == null) {
            return null;
        }
        return (int) Math.round(d);
    }

    /**
     * Parse minimal number of levels.
     * 
     * @param osmPrimitive osm primitive
     * @return minimal number of levels
     */
    public static Integer parseMinLevel(OsmPrimitive osmPrimitive) {
        Integer level = roundToInteger(ModelUtil.getNumberAttribute(osmPrimitive, OsmAttributeKeys.BUILDING_MIN_LEVEL.getKey(),
                null));

        if (level == null) {
            level = roundToInteger(ModelUtil.getNumberAttribute(osmPrimitive,
                    OsmAttributeKeys.BUILDING_LEVELS_UNDERGROUND.getKey(), null));
            if (level != null) {
                level = -level;
            }
        }

        return level;
    }

    /**
     * Parse number of window columns on wall.
     * 
     * @param way way
     * @return number of window columns on wall
     */
    public static WindowGridBuildingElement parseWallWindowsColumns(Way way) {

        String windowsCols = OsmAttributeKeys.WINDOWS.primitiveValue(way);

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
