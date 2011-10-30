/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement;

/**
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public enum MeasurementKey {

    /**
     * Height 1.
     */
    HEIGHT_1("3dr:height1"),

    /**
     * Height 2.
     */
    HEIGHT_2("3dr:height2"),
    /**
     * Height 3.
     */
    HEIGHT_3("3dr:height3"),

    /**
     * Length 1.
     */
    LENGTH_1("3dr:length1"),

    /**
     * Lenght 2.
     */
    LENGTH_2("3dr:length2"),

    /**
     * Lenght 3.
     */
    LENGTH_3("3dr:length3"),

    /**
     * Lenght 4.
     */
    LENGTH_4("3dr:length4"),

    /**
     * Dormer height 1.
     */
    DORMER_HEIGHT_1("3dr:dormer:height1"),

    /**
     * Dormer height 2.
     */
     DORMER_HEIGHT_2("3dr:dormer:height2"),

    /**
     * Dormer height 3.
     */
    DORMER_HEIGHT_3("3dr:dormer:height3"),

    /**
     * Dormer width 1.
     */
    DORMER_WIDTH_1("3dr:dormer:width1"),

    /**
     * Dormer width 2.
     */
    DORMER_WIDTH_2("3dr:dormer:width2"),

    /**
     * Dormer width 3.
     */
    DORMER_WIDTH_3("3dr:dormer:width3");

    /**
     * Key value.
     */
    private String key;

    /** Constructor for enum.
     * @param pKey key value
     */
    private MeasurementKey(String pKey) {

        this.key = pKey;

    }

    /**
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

}
