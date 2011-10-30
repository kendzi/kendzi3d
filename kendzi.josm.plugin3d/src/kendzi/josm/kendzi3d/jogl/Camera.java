/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Camera position and angle.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public interface Camera {

    public final static double CAM_HEIGHT = 1.8;

    /**
     * Camera position point.
     *
     * @return position
     */
    Point3d getPoint();

    /**
     * Camera angles vector.
     *
     * @return angles
     */
    Vector3d getAngle();
}
