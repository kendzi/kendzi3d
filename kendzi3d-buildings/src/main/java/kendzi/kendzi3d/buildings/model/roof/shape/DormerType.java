package kendzi.kendzi3d.buildings.model.roof.shape;

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
