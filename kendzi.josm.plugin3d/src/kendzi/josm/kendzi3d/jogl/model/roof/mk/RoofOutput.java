/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk;

import kendzi.jogl.model.geometry.Model;

/**
 * Roof builder output.
 *
 * @author Tomasz Kedziora (kendzi)
 *
 */
public class RoofOutput {

    RoofDebugOut debug;
    /**
     * Roof model.
     */
    Model model;
    /**
     * Roof height.
     */
    double height;
    /**
     * @return the model
     */
    public Model getModel() {
        return this.model;
    }
    /**
     * @param model the model to set
     */
    public void setModel(Model model) {
        this.model = model;
    }
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


}
