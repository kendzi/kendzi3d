/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space;

import java.util.List;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoof;


public interface RoofHooksSpaces {

    //

    List<RoofHooksSpace> getRoofHooksSpaces();

    List<RoofDormerTypeOutput> buildDormers(DormerRoof pRoof, RoofTextureData pRoofTextureData);

}
