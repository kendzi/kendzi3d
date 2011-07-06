/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.metadata;

public class TextureMetadata {
    /**
     * Texture location.
     */
    String file;
    /**
     * Size U of texture in meters.
     */
    double sizeU;
    /**
     * Size V of texture in meters.
     */
    double sizeV;

    /**
     * Can be breaked in U.
     */
    boolean noBreakU;

    /**
     * Can be breaked in V.
     */
    boolean noBreakV;

    /** (X).
     * @return the sizeU
     */
    public double getSizeU() {
        return sizeU;
    }

    /** (X).
     * @param sizeU the sizeU to set
     */
    public void setSizeU(double sizeU) {
        this.sizeU = sizeU;
    }

    /** (Y).
     * @return the sizeV
     */
    public double getSizeV() {
        return sizeV;
    }

    /** (Y).
     * @param sizeV the sizeV to set
     */
    public void setSizeV(double sizeV) {
        this.sizeV = sizeV;
    }

    /**
     * @return the noBreakU
     */
    public boolean isNoBreakU() {
        return noBreakU;
    }

    /**
     * @param noBreakU the noBreakU to set
     */
    public void setNoBreakU(boolean noBreakU) {
        this.noBreakU = noBreakU;
    }

    /**
     * @return the noBreakV
     */
    public boolean isNoBreakV() {
        return noBreakV;
    }

    /**
     * @param noBreakV the noBreakV to set
     */
    public void setNoBreakV(boolean noBreakV) {
        this.noBreakV = noBreakV;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
