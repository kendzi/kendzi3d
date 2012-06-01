/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias;

/**
 * Roof type alias enum.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public enum RoofTypeAliasEnum {
    FLAT("flat"),
    GABLED("gabled"),
    GAMBREL("gambrel"),
    HALF_HIPPED("half hipped"),
    HIPPED("hipped"),
    PITCHED("pitched"),
    PYRAMIDAL("pyramidal"),
    SKILLION("skillion"),
    DOME("dome"),
    ONION("onion")
    ;

    private String key;

    RoofTypeAliasEnum(String pKey) {
        this.key = pKey;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

//    public static parse(String pName) {
//
//        if (pName == null) {
//            return pName;
//        }
//
//        for (RoofTypeAliasEnum type : RoofTypeAliasEnum.values()) {
//            if (type.key.equals(pName)) {
//                return type;
//            }
//        }
//        return null;
//
//
//    }


}
