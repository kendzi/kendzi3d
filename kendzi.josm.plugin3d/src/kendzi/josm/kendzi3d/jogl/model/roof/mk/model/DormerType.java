package kendzi.josm.kendzi3d.jogl.model.roof.mk.model;

public enum DormerType {

    A("a"),

    B("b"),

    C("c"),

    EMPTY("-"),

    UKNOWN("uknown");

    String key;

    DormerType(String pKey) {
        this.key = pKey;
    }

    public String getKey() {
        return key;
    }
}
