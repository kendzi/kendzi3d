/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.ui.fps;

/**
 * Listener for events with calculated fps.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public interface FpsListener {

    /** Fire when fps value is calculated.
     * @param fpsChangeEvent Fps event
     */
    public abstract void dispatchFpsChange(FpsChangeEvent fpsChangeEvent);
}

