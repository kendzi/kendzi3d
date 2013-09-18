package kendzi.kendzi3d.josm.model.attribute;

public enum OsmAttributeValues{
    WINDOW("window"),
    ENTRANCE("entrance"),
    BEGIN("begin"),
    END("end"),
    CLONE_HEIGHT("clone:height"),
    CLONE_LEVEL("clone:level"),
    YES("yes"),
    SPHERE("sphere"),
    ;

    String value;

    OsmAttributeValues(String str) {
        this.value = str;
    }

    public String getValue() {
        return this.value;
    }

}
