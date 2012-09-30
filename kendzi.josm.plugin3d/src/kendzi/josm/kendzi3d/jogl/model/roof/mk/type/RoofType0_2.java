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

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
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
            RoofMaterials pRoofTextureData
            ) {

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 0.5d);
        Double h2 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_2, 2.5d);

        Double l1 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_1, pRecHeight, pRecHeight / 3d);
        Double l2 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_2, pRecWidth, pRecWidth / 3d);

        int type = getType();

        return build(border, scaleA, scaleB, pRecHeight, pRecWidth, rectangleContur, h1, h2, l1, l2, 0, 0, type, pRoofTextureData);
    }
}
