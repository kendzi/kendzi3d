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

import org.apache.log4j.Logger;

/**
 * Roof type 1.0.
 *
 * @author Tomasz Kêdziora (Kendzi)
 *
 */
public class RoofType1_0 extends RoofType1_1 {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType1_0.class);

    @Override
    public String getPrefixKey() {
        return "1.0";
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(List<Point2d> border,
            Point2d[] rectangleContur,
            double scaleA,
            double scaleB,
            double pSizeA,
            double pSizeB,
            Integer prefixParameter,
            List<Double> heights, List<Double> sizeB,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData
            ) {

        Double h1 = getSize(0, heights, 1d);

        return build(border, scaleA, scaleB, pSizeA, pSizeB, rectangleContur, 0, h1, 0, pRoofTextureData);
    }

}
