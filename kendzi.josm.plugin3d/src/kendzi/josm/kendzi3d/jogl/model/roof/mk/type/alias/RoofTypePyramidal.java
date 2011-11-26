/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_5;

/**
 * Roof type pyramidal.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofTypePyramidal extends RoofType2_5 {

    @Override
    public String getPrefixKey() {
        return RoofTypeAliasEnum.PYRAMIDAL.getKey();
    }

}
