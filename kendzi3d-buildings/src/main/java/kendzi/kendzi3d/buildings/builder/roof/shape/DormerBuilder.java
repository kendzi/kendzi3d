/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.buildings.builder.roof.shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.RoofDormerType;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.RoofDormerTypeA;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.RoofDormerTypeB;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.RoofDormerTypeEmpty;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.RoofDormerTypeOutput;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space.RoofHooksSpaces;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.Measurement;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRoofModel;

public class DormerBuilder {

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
