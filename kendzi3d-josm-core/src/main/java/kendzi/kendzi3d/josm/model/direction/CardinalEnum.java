/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.josm.model.direction;

/**
 * Direction cardinal enum.
 *
 * @author Tomasz Kędziora (kendzi)
 */
public enum CardinalEnum {

    N(0),
    NNE(22),
    NE(45),
    ENE(67),
    E(90),
    ESE(112),
    SE(135),
    SSE(157),
    S(180),
    SSW(202),
    SW(225),
    WSW(247),
    W(270),
    WNW(292),
    NW(315),
    NNW(337),

    north(0),
    east(90),
    south(180),
    west(270);


    private double angle;

    CardinalEnum(double pAngle) {
        this.angle = pAngle;
    }

    /**
     * @return the angle
     */
    public double getAngle() {
        return this.angle;
    }
}
