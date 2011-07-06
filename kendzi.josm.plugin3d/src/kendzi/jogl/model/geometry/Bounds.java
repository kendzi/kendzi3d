/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry;

import javax.vecmath.Point3d;

/**
 * Bounds of model. Minimal border, maximal border, radius and center.
 *
 * @author Tomasz Kêdziora (Kendzi)
 */
public class Bounds {
    /**
     * Minimal point of bounds.
     */
    public Point3d min;

    /**
     * Maximal point of bounds.
     */
    public Point3d max;

    /**
     * Radius of bounds.
     */
    public double radius;
    /**
     * Center of bounds.
     */
    public Point3d center;
}
