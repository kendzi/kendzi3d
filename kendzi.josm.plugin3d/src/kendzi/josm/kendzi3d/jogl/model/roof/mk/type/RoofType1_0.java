/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
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
    public RoofTypeOutput buildRectangleRoof(List<Point2d> border,
            Point2d[] rectangleContur,
            double pScaleA,
            double pScaleB,
            double pRecHeight,
            double pRecWidth,
            Integer prefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData
            ) {

//        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 2.5d);
        Double h1 = getHeightDegreesMeters(pMeasurements, MeasurementKey.HEIGHT_1, 0, pRecHeight, 20d);

        return build(border, pScaleA, pScaleB, pRecHeight, pRecWidth, rectangleContur, 0, h1, 0, pRoofTextureData);
    }

}
