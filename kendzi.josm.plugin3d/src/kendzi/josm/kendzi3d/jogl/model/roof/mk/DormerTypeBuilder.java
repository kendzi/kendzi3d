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
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerTypeB;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerTypeEmpty;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RoofHooksSpaces;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;

public class DormerTypeBuilder {

    public static RoofDormerType [] dormerTypeBuilders = {
        new RoofDormerTypeA(),
        new RoofDormerTypeB(),
        new RoofDormerTypeEmpty(),
    };

    public static List<RoofDormerTypeOutput> build(
            RoofHooksSpaces pRoofHooksSpaces,
            DormerRoofModel roof,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofMaterials pRoofTextureData) {

        List<RoofDormerTypeOutput> ret = new ArrayList<RoofDormerTypeOutput>();

        if (pRoofHooksSpaces == null) {
            return ret;
        }


        List<RoofDormerTypeOutput> buildRoof  = pRoofHooksSpaces.buildDormers(roof, pRoofTextureData);

        return buildRoof;
    }
}
