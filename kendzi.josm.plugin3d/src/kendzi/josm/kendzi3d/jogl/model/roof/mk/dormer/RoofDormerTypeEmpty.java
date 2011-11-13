/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer;

import java.util.Map;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerType;

import org.apache.log4j.Logger;

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
            RoofTextureData pRoofTextureData) {


        return null;
    }
}
