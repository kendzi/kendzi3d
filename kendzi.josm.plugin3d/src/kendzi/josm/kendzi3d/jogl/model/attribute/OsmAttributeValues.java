package kendzi.josm.kendzi3d.jogl.model.attribute;

public enum OsmAttributeValues{
    WINDOW("window"),
    ENTRANCE("entrance"),
    BEGIN("begin"),
    END("end"),
    CLONE_HEIGHT("clone:height"),
    CLONE_LEVEL("clone:level"),
    ;

    String value;

    OsmAttributeValues(String str) {
        this.value = str;
    }

    public String getValue() {
        return this.value;
    }

}
