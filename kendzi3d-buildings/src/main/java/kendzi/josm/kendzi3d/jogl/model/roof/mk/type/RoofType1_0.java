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

import org.apache.log4j.Logger;

/**
 * Roof type 1.0.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType1_0 extends RoofType1_1 {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType1_0.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE1_0;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        //        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);
        Double h1 = getHeightDegreesMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 0, conf.getRecHeight(), 20d);

        if (h1 < 0) {
            h1 = 0d;
        }

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), 0, h1, 0, conf.getRoofTextureData());
    }

}
