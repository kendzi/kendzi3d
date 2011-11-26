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
 * Direction in Cardinal.
 *
 * @author Tomasz Kędziora (kendzi)
 */
public class CardinalDirection implements Direction {
    CardinalEnum direction = CardinalEnum.N;

    /**
     * Direction in Cardinal.
     * @param pCardinalEnum Cardinal enum
     */
    public CardinalDirection(CardinalEnum pCardinalEnum) {
        this.direction = pCardinalEnum;
    }

    @Override
    public boolean isCardinal() {
        return true;
    }

    @Override
    public double getAngle() {
        return this.direction.getAngle();
    }

    @Override
    public Vector2d getVector() {
        double angle = Math.toRadians(getAngle());
        return new Vector2d(Math.sin(angle), Math.cos(angle));
    }
}
