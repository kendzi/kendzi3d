/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.Map;

import org.apache.log4j.Logger;

import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.Measurement;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;

/**
 * Roof type 2.4.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType2v4 extends RoofType2v3 {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofType2v4.class);

    @Override
    protected double getHeight2(Map<MeasurementKey, Measurement> pMeasurements) {
        return 0d;
    }
}
