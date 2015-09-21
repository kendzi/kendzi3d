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
public class Measurement {

    private double value;

    private MeasurementUnit unit;



    public Measurement(double pValue, MeasurementUnit pUnit) {

        this.value = pValue;
        this.unit = pUnit;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return this.value;
    }

    /**
     * @param pValue the value to set
     */
    public void setValue(double pValue) {
        this.value = pValue;
    }

    /**
     * @return the unit
     */
    public MeasurementUnit getUnit() {
        return this.unit;
    }

    /**
     * @param pUnit the unit to set
     */
    public void setUnit(MeasurementUnit pUnit) {
        this.unit = pUnit;
    }





}
