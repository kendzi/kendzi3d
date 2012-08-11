package kendzi.josm.kendzi3d.jogl.model;

public enum OsmAttributeValues{
    WINDOW("window"),
    ENTRANCE("entrance");

    String value;

    OsmAttributeValues(String str) {
        this.value = str;
    }

    public Object getValue() {
        return this.value;
    }

}
