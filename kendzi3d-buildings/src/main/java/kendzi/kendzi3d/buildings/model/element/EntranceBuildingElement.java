/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.buildings.model.element;

/**
 * Entrance on building part outline.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class EntranceBuildingElement extends SquareHoleElement {

    /**
     * Default height of entrance.
     */
    private static final double DEFAULT_ENTRANCE_HEIGHT = 2.36;

    /**
     * Default width of entrance.
     */
    private static final double DEFAULT_ENTRANCE_WIDTH = 1.2;

    /**
     * Entrance type.
     */
    private String entranceType;

    /**
     * Con.
     */
    public EntranceBuildingElement() {

        super(0, DEFAULT_ENTRANCE_HEIGHT, DEFAULT_ENTRANCE_WIDTH);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {

        EntranceBuildingElement wbe = (EntranceBuildingElement) super.clone();

        wbe.setEntranceType(getEntranceType());
        return wbe;
    }

    /**
     * @return the entranceType
     */
    public String getEntranceType() {
        return entranceType;
    }

    /**
     * @param entranceType the entranceType to set
     */
    public void setEntranceType(String entranceType) {
        this.entranceType = entranceType;
    }
}
