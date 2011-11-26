/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.util;

import javax.vecmath.Vector2d;

/**
 * Direction in angle.
 *
 * @author Tomasz Kędziora (kendzi)
 */
public class AngleDirection implements Direction {
    private double angle;

    AngleDirection(double pAngle) {
        this.angle = pAngle;
    }

    @Override
    public boolean isCardinal() {
        return false;
    }

    @Override
    public double getAngle() {
        return this.angle;
    }

    @Override
    public Vector2d getVector() {
        double rad = Math.toRadians(this.angle);
        return new Vector2d(Math.sin(rad), Math.cos(rad));
    }
}
