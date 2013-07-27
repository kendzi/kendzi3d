/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.wall.HeightCalculator;

/**
 * Roof builder output.
 * 
 * @author Tomasz Kedziora (kendzi)
 * 
 */
public class RoofOutput {

    /**
     * Debug information.
     */
    private RoofDebugOut debug;

    /**
     * Heights of wall parts under roof.
     */
    private HeightCalculator heightCalculator;

    /**
     * Roof height.
     */
    private double height;

    /**
     * @return the height
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the debug
     */
    public RoofDebugOut getDebug() {
        return this.debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(RoofDebugOut debug) {
        this.debug = debug;
    }

    /**
     * @return the heightCalculator
     */
    public HeightCalculator getHeightCalculator() {
        return this.heightCalculator;
    }

    /**
     * @param heightCalculator the heightCalculator to set
     */
    public void setHeightCalculator(HeightCalculator heightCalculator) {
        this.heightCalculator = heightCalculator;
    }

}
