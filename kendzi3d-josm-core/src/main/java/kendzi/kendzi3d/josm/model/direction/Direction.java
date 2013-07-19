/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.josm.model.direction;

import javax.vecmath.Vector2d;

/**
 * Direction.
 *
 * @author Tomasz Kędziora (kendzi)
 */
public interface Direction {

    /**
     * If direction is cardinal.
     * @return direction is cardinal
     */
    public abstract boolean isCardinal();

    /**
     * Direction angle.
     * @return Direction angle
     */
    public abstract double getAngle();

    /**
     * Direction vector.
     * @return Direction vector
     */
    public abstract Vector2d getVector();

}
