/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.camera;

import org.joml.Vector3dc;

/**
 * Camera position and angle.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public interface Camera {

    double CAM_HEIGHT = 1.8;

    /**
     * Camera position point.
     *
     * @return position
     */
    Vector3dc getPoint();

    /**
     * Camera angles vector.
     *
     * @return angles
     */
    Vector3dc getAngle();
}
