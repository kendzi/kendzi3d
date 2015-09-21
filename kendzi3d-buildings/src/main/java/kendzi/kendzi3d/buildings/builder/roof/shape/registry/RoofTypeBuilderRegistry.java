package kendzi.kendzi3d.buildings.builder.roof.shape.registry;

import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType0v0;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType0v1;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType0v2;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType0v3;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType0v4;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType1v0;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType1v1;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType2v0;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType2v1;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType2v2;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType2v3;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType2v4;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType2v5;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType2v6;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType2v7;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType2v8;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType2v9;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType3v0;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType4v0;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType4v2;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType5v0;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType5v2;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType5v6;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType8v0;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType9v0;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofTypeBuilder;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofTypeDome;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofTypePyramidal;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofTypeTented;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeFlat;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeGabled;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeGambrel;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeHalfHipped;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeHalfRound;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeMansard;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeOnion;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypePitched;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeRound;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeSkillion;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeSquareHipped;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.alias.RoofTypeSquarePyramidal;
import kendzi.kendzi3d.buildings.model.roof.shape.RoofTypeAliasEnum;

/**
 * Registered roof type builders for dormer roofs.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class RoofTypeBuilderRegistry {

    private RoofTypeBuilderRegistry() {
        //
    }

    /**
     * Choose builder depend on roof type.
     * 
     * @param roofTypeEnum
     *            roof type
     * @return roof builder
     */
    public static RoofTypeBuilder selectBuilder(RoofTypeAliasEnum roofTypeEnum) {
        switch (roofTypeEnum) {

        case FLAT:
            return new RoofTypeFlat();
        case PITCHED:
            return new RoofTypePitched();
        case SKILLION:
            return new RoofTypeSkillion();
        case GABLED:
            return new RoofTypeGabled();
        case GAMBREL:
            return new RoofTypeGambrel();
        case HALF_HIPPED:
            return new RoofTypeHalfHipped();
        case HIPPED:
            return new kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofTypeHipped();
        case SQUARE_HIPPED:
            return new RoofTypeSquareHipped();
        case COMPLEX_HIPPED:
            return new RoofType9v0();
        case SQUARE_PYRAMIDAL:
            return new RoofTypeSquarePyramidal();
        case PYRAMIDAL:
            return new RoofTypePyramidal();
        case TENTED:
            return new RoofTypeTented();
        case DOME:
            return new RoofTypeDome();
        case ONION:
            return new RoofTypeOnion();
        case MANSARD:
            return new RoofTypeMansard();
        case HALF_ROUND:
            return new RoofTypeHalfRound();
        case ROUND:
            return new RoofTypeRound();
        case SALTBOX:
            return new RoofType3v0();

        case ROOF_TYPE0_0:
            return new RoofType0v0();
        case ROOF_TYPE0_1:
            return new RoofType0v1();
        case ROOF_TYPE0_2:
            return new RoofType0v2();
        case ROOF_TYPE0_3:
            return new RoofType0v3();
        case ROOF_TYPE0_4:
            return new RoofType0v4();

        case ROOF_TYPE1_0:
            return new RoofType1v0();
        case ROOF_TYPE1_1:
            return new RoofType1v1();

        case ROOF_TYPE2_0:
            return new RoofType2v0();
        case ROOF_TYPE2_1:
            return new RoofType2v1();
        case ROOF_TYPE2_2:
            return new RoofType2v2();
        case ROOF_TYPE2_3:
            return new RoofType2v3();
        case ROOF_TYPE2_4:
            return new RoofType2v4();
        case ROOF_TYPE2_5:
            return new RoofType2v5();
        case ROOF_TYPE2_6:
            return new RoofType2v6();
        case ROOF_TYPE2_7:
            return new RoofType2v7();
        case ROOF_TYPE2_9:
            return new RoofType2v9();
        case ROOF_TYPE2_8:
            return new RoofType2v8();

        case ROOF_TYPE3_0:
            return new RoofType3v0();

        case ROOF_TYPE4_0:
            return new RoofType4v0();
        case ROOF_TYPE4_2:
            return new RoofType4v2();

        case ROOF_TYPE5_0:
            return new RoofType5v0();
        case ROOF_TYPE5_2:
            return new RoofType5v2();
        case ROOF_TYPE5_6:
            return new RoofType5v6();

        case ROOF_TYPE8_0:
            return new RoofType8v0();
        case ROOF_TYPE9_0:
            return new RoofType9v0();

        default:
            return new RoofTypeFlat();
        }
    }
}
