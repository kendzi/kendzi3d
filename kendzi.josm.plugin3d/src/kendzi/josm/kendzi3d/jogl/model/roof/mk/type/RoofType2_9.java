/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import org.apache.log4j.Logger;

/**
 * Roof type 2.9.
 *
 * @author Tomasz Kêdziora (Kendzi)
 *
 */
public class RoofType2_9 extends RoofType2_8 {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2_9.class);

    @Override
    public String getPrefixKey() {
        return "2.9";
    }

    @Override
    protected double getMiddleLineHeight(Double h1, Double h2) {
        return Math.max(h1, h2);
    }

    @Override
    protected Double getHeight1(Double h1) {
        return -h1;
    }

    @Override
    protected Double getHeight2(Double h2) {
        return -h2;
    }

}
