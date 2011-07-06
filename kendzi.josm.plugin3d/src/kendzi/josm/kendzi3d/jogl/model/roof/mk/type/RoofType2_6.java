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
 * Roof type 2.6.
 *
 * @author Tomasz Kêdziora (Kendzi)
 *
 */
public class RoofType2_6 extends RoofType2_7 {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2_6.class);

    @Override
    public String getPrefixKey() {
        return "2.6";
    }


    @Override
    public boolean isLeft() {
        return false;
    }

}
