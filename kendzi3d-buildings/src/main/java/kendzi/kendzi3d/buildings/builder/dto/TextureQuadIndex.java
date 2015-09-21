/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.buildings.builder.dto;

/**
 * Texture indexes for quad.
 * 
 * @author kendzi
 * 
 */
public class TextureQuadIndex {
    private int ld;
    private int rd;
    private int rt;
    private int lt;

    /**
     * @return the ld
     */
    public int getLd() {
        return ld;
    }

    /**
     * @param ld
     *            the ld to set
     */
    public void setLd(int ld) {
        this.ld = ld;
    }

    /**
     * @return the rd
     */
    public int getRd() {
        return rd;
    }

    /**
     * @param rd
     *            the rd to set
     */
    public void setRd(int rd) {
        this.rd = rd;
    }

    /**
     * @return the rt
     */
    public int getRt() {
        return rt;
    }

    /**
     * @param rt
     *            the rt to set
     */
    public void setRt(int rt) {
        this.rt = rt;
    }

    /**
     * @return the lt
     */
    public int getLt() {
        return lt;
    }

    /**
     * @param lt
     *            the lt to set
     */
    public void setLt(int lt) {
        this.lt = lt;
    }

}