/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import org.apache.log4j.Logger;

/**
 * Roof type 1.0.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType1v0 extends RoofType1v1 {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofType1v0.class);

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        double h1 = getHeightDegreesMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 0, conf.getRecHeight(), 20d);

        if (h1 < 0) {
            h1 = 0d;
        }

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), 0, h1, 0,
                conf.getRoofTextureData());
    }
}
