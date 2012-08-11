package kendzi.josm.kendzi3d.jogl.model;

public enum OsmAttributeKeys {

    BUILDING("building"),

    _3DR_TYPE("3dr:type"),

    _3DR_DORMERS("3dr:dormers"),

    ;

    String key;

    OsmAttributeKeys(String str) {
        this.key = str;
    }

    public String getKey() {
        return this.key;
    }

}
