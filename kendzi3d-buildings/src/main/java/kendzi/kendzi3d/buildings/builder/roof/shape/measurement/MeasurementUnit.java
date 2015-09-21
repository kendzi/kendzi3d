/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.measurement;

/**
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public enum MeasurementUnit {

    /**
     * Unit in meters.
     */
    METERS("m"),

    /**
     * Unit in degrees.
     */
    DEGREES("d"),

    /**
     * Unit in percent.
     */
    PERCENT("%"),

    /**
     * Unit unknown.
     */
    UNKNOWN("u");

    /**
     * Key value.
     */
    private String key;

    /** Constructor for enum.
     * @param pKey key value
     */
    private MeasurementUnit(String pKey) {

        this.key = pKey;

    }

    /**
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

}
