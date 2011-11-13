/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerType;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerTypeA;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerTypeEmpty;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RoofHooksSpaces;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoof;

public class DormerTypeBuilder {

    public static RoofDormerType [] dormerTypeBuilders = {
        new RoofDormerTypeA(),
        new RoofDormerTypeEmpty(),
    };

    public static List<RoofDormerTypeOutput> build(
            RoofHooksSpaces pRoofHooksSpaces,
            DormerRoof roof,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData) {

        List<RoofDormerTypeOutput> ret = new ArrayList<RoofDormerTypeOutput>();

        if (pRoofHooksSpaces == null) {
            return ret;
        }


        List<RoofDormerTypeOutput> buildRoof  = pRoofHooksSpaces.buildDormers(roof, pRoofTextureData);

        return buildRoof;



//        for (int i = 0; i < pRoofHooksSpace.length; i++) {
//            RoofHooksSpace space = pRoofHooksSpace[i];
//
//            char[] extensionTypes = getExtensionType(i, roofExtensions);
//
//            RoofHookPoint[] roofHookPoints = space.getRoofHookPoints(extensionTypes.length);
//
//            for (int ei = 0; ei < extensionTypes.length && ei < roofHookPoints.length; ei++) {
//
//                RoofDormerType roofType = getRoofExtansionType(extensionTypes[ei]);
//
//                RoofDormerTypeOutput buildRoof = roofType.buildRoof(roofHookPoints[ei], space, pMeasurements, pRoofTextureData);
//
//                ret.add(buildRoof);
//
//            }
//        }
//
//        return ret;
    }
//
//    private static char[] getExtensionType(int i, List<char[]> roofExtensions) {
//        if (roofExtensions == null) {
//            return new char[0];
//        }
//
//        if (i >= roofExtensions.size()) {
//            return new char[0];
//        }
//
//        char[] characters = roofExtensions.get(i);
//        if (characters == null) {
//            characters = new char[0];
//        }
//        return characters;
//    }

//    private static RoofDormerType getRoofExtansionType(Character pKey) {
//        if (pKey == null) {
//            return null;
//        }
//        for (RoofDormerType rt : dormerTypeBuilders) {
//            if (pKey.equals(rt.getPrefixKey())) {
//                return rt;
//            }
//        }
//        return null;
//    }



}
