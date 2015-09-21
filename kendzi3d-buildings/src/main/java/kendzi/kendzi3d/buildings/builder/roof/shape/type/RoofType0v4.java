/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;

/**
 * Roof type 0.4.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType0v4 extends RoofType0 {

    @Override
    protected int getType() {
        return 4;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        Double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 0.5d);
        Double h2 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_2, 2.5d);

        Double l1 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_1, conf.getRecHeight(),
                conf.getRecHeight() / 3d);
        Double l2 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_2, conf.getRecWidth(),
                conf.getRecWidth() / 3d);
        Double l3 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_3, conf.getRecHeight(),
                conf.getRecHeight() / 3d);
        Double l4 = getLenghtMetersPersent(conf.getMeasurements(), MeasurementKey.LENGTH_4, conf.getRecWidth(),
                conf.getRecWidth() / 3d);

        int type = getType();

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1, h2, l1,
                l2, l3, l4, type, conf.getRoofTextureData());
    }
}
