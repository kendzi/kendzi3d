/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.model.building.builder;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofDebugOut;

/**
 * Additional debug information for building part.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class BuildingPartOutput {

    /**
     * Debug output for building part roof.
     */
    private RoofDebugOut roofDebugOut;

    /**
     * @return the roofDebugOut
     */
    public RoofDebugOut getRoofDebugOut() {
        return roofDebugOut;
    }

    /**
     * @param roofDebugOut the roofDebugOut to set
     */
    public void setRoofDebugOut(RoofDebugOut roofDebugOut) {
        this.roofDebugOut = roofDebugOut;
    }
}
