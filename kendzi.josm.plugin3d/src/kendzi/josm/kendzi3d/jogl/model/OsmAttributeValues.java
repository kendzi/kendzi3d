package kendzi.josm.kendzi3d.jogl.model;

public enum OsmAttributeValues{
    WINDOW("window"),
    ENTRANCE("entrance"),
    BEGIN("begin"),
    END("end"),
    ;

    String value;

    OsmAttributeValues(String str) {
        this.value = str;
    }

    public String getValue() {
        return this.value;
    }

}
