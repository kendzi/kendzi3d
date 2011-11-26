/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.util;


/**
 * Direction parser util.
 *
 * @author Tomasz Kędziora (kendzi)
 */
public class DirectionParserUtil {

    /**
     * Parse string to direction.
     * @param pDirection direction string
     * @return direction
     */
    public static Direction parse(String pDirection) {
        if (pDirection == null) {
            return null;
        }

        Double angle = null;
        try {
            angle = Double.parseDouble(pDirection);
            if (angle != null) {
                return new AngleDirection(angle);
            }
        } catch (Exception e) {
            //
        }

        CardinalEnum cardinalEnum = CardinalEnum.valueOf(pDirection);
        if (cardinalEnum != null) {
            return new CardinalDirection(cardinalEnum);
        }
        return null;
    }

}
