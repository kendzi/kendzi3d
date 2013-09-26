/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;

/**
 * Roof type 0.0.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType0_0 extends RoofType0 {

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE0_0;
    }

    @Override
    int getType() {
        return 0;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 0.5d);

        int type = getType();

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, 0, 0, 0, 0, 0, type, conf.getRoofTextureData());
    }
}
