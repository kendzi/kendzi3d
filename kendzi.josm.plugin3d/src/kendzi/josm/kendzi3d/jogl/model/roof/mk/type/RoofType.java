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

public interface RoofType {

    public String getPrefixKey();

    public boolean isPrefixParameter();

    public RoofTypeOutput buildRoof(Point2d pStartPoint, List<Point2d> border, Integer prefixParameter, double height, Map<MeasurementKey, Measurement> pMeasurements, RoofTextureData pRoofTextureData);
}
