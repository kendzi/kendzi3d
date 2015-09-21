/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space;

import java.util.List;

import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.RoofDormerTypeOutput;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRoofModel;


public interface RoofHooksSpaces {

    //

    List<RoofHooksSpace> getRoofHooksSpaces();

    List<RoofDormerTypeOutput> buildDormers(DormerRoofModel pRoof, RoofMaterials pRoofTextureData);

}
