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
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;


public interface RoofDormerType {

    public Character getPrefixKey();

    public RoofDormerTypeOutput buildRoof(RoofHookPoint pRoofHookPoint, RoofHooksSpace space, Map<MeasurementKey, Measurement> pMeasurements, RoofTextureData pRoofTextureData);
}
