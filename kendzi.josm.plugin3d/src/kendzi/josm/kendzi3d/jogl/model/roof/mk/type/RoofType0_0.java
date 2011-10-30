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

/**
 * Roof type 0.0.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType0_0 extends RoofType0 {

    @Override
    public String getPrefixKey() {
        return "0.0";
    }

    @Override
    int getType() {
        return 0;
    }

    @Override
    protected boolean normalizeAB() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(
            List<Point2d> border,
            Point2d[] rectangleContur,
            double scaleA,
            double scaleB,
            double pRecHeight,
            double pRecWidth,
            Integer prefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData
            ) {

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 0.5d);

        int type = getType();

        return build(border, scaleA, scaleB, pRecHeight, pRecWidth, rectangleContur, h1, 0, 0, 0, 0, 0, type, pRoofTextureData);
    }
}
