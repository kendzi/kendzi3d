/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.dormer;

import java.util.Map;

import org.apache.log4j.Logger;

import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space.RoofHooksSpace;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.Measurement;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerType;

public class RoofDormerTypeEmpty implements RoofDormerType {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofDormerTypeEmpty.class);

    @Override
    public DormerType getType() {
        return DormerType.EMPTY;
    }

    @Override
    public RoofDormerTypeOutput buildRoof(
            RoofHookPoint pRoofHookPoint,
            RoofHooksSpace space,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofMaterials pRoofTextureData) {


        return null;
    }
}
