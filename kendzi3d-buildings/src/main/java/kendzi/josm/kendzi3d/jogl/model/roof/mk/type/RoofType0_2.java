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
 * Roof type 0.2.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType0_2 extends RoofType0 {

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE0_2;
    }

    @Override
    int getType() {
        return 2;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 0.5d);
        Double h2 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_2, 2.5d);

        Double l1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecHeight(), conf.getRecHeight() / 3d);
        Double l2 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_2, conf.getRecWidth(), conf.getRecWidth() / 3d);

        int type = getType();

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, h2, l1, l2, 0, 0, type, conf.getRoofTextureData());
    }
}
