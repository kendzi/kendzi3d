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
    ROOF_TYPE0_0("0.0"),
    ROOF_TYPE0_1("0.1"),
    ROOF_TYPE0_2("0.2"),
    ROOF_TYPE0_3("0.3"),
    ROOF_TYPE0_4("0.4"),

    ROOF_TYPE1_0("1.0"),
    ROOF_TYPE1_1("1.1"),

    ROOF_TYPE2_0("2.0"),
    ROOF_TYPE2_1("2.1"),
    ROOF_TYPE2_2("2.2"),
    ROOF_TYPE2_3("2.3"),
    ROOF_TYPE2_4("2.4"),
    ROOF_TYPE2_5("2.5"),
    ROOF_TYPE2_6("2.6"),
    ROOF_TYPE2_7("2.7"),
    ROOF_TYPE2_8("2.8"),
    ROOF_TYPE2_9("2.9"),

    ROOF_TYPE3_0("3.0"),

    ROOF_TYPE4_0("4.0"),
    ROOF_TYPE4_2("4.2"),

    ROOF_TYPE5_0("5.0"),
    ROOF_TYPE5_2("5.2"),
    ROOF_TYPE5_6("5.6"),
    ROOF_TYPE8_0("8.0"),
    ROOF_TYPE9_0("9.0"),

    FLAT("flat"),
    GABLED("gabled"),
    GAMBREL("gambrel"),
    MANSARD("mansard"),
    HALF_HIPPED("half hipped"),
    HIPPED("hipped"),
    PITCHED("pitched"),
    PYRAMIDAL("pyramidal"),
    SKILLION("skillion"),
    DOME("dome"),
    ONION("onion"),
    HALF_ROUND("half round"),
    ROUND("round"),
    TENTED("tented"),
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
