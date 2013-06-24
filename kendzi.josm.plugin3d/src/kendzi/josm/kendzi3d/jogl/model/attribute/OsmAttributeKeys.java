package kendzi.josm.kendzi3d.jogl.model.attribute;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public enum OsmAttributeKeys {

    BUILDING("building"),

    BUILDING_PART("building:part"),

    BUILDING_HEIGHT("building:height"),

    BUILDING_MIN_LEVEL("building:min_level"),

    BUILDING_MAX_LEVEL("building:max_level"),

    BUILDING_LEVELS("building:levels"),

    BUILDING_LEVELS_ABOVEGROUND("building:levels:aboveground"),

    BUILDING_LEVELS_UNDERGROUND("building:levels:underground"),

    BUILDING_FACADE_COLOR("building:facade:color"),

    BUILDING_FACADE_COLOUR("building:facade:colour"),

    BUILDING_COLOR("building:color"),

    BUILDING_COLOUR("building:colour"),

    BUILDING_MATERIAL("building:material"),

    BUILDING_FACADE_MATERIAL("building:facade:material"),

    FACADE_MATERIAL("facade:material"),

    BUILDING_ROOF_SHAPE("building:roof:shape"),

    ROOF_SHAPE("roof:shape"),

    ROOF_LEVELS("roof:levels"),

    ROOF_ANGLE("roof:angle"),

    ROOF_HEIGHT("roof:height"),

    ROOF_MATERIAL("roof:material"),

    ROOF_SLOPE_DIRECTION("roof:slope:direction"),

    ROOF_RIDGE_DIRECTION("roof:ridge:direction"),

    ROOF_DIRECTION("roof:direction"),

    DIRECTION("direction"),

    BUILDING_ROOF_MATERIAL("building:roof:material"),

    ROOF_COLOUR("roof:colour"),

    ROOF_COLOR("roof:color"),

    FLOOR_MATERIAL("floor:material"),

    FLOOR_COLOUR("floor:colour"),

    FLOOR_COLOR("floor:color"),

    BUILDING_ROOF_COLOUR("building:roof:colour"),

    BUILDING_ROOF_COLOR("building:roof:color"),

    _3DR_TYPE("3dr:type"),

    _3DR_DORMERS("3dr:dormers"),

    _3DR_DIRECTION("3dr:direction"),

    WINDOWS("windows"),

    TYPE("type"),

    FENCE_COLOR("fence:color"),

    FENCE_TYPE("fence_type"),

    FENCE__TYPE("fence:type"),

    ENTRANCE("entrance"),

    ;

    String key;

    OsmAttributeKeys(String str) {
        this.key = str;
    }

    public String getKey() {
        return this.key;
    }

    public String primitiveValue(OsmPrimitive primitive) {
        return OsmAttributeUtil.primitiveValue(primitive, this);
    }

    public boolean primitiveKeyHaveAnyValue(OsmPrimitive primitive) {
        return OsmAttributeUtil.primitiveKeyHaveAnyValue(primitive, this);
    }

}
