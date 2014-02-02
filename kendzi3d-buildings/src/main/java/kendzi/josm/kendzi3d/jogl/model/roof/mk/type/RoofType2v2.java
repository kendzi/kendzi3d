/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.Map;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;

import org.apache.log4j.Logger;

/**
 * Roof type 2.2.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofType2v2 extends RoofType2v3 {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofType2v2.class);

    @Override
    protected double getHeight2(Map<MeasurementKey, Measurement> pMeasurements) {
        return 0d;
    }

    @Override
    protected boolean getSkipLeft() {
        return true;
    }
}
