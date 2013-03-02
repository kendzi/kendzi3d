/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.dto;

/**
 * Texture data.
 *
 * @author kendzi
 *
 */
public class TextureData {
    private String tex0;
    private String tex1;
    private double width;
    private double height;
    private Boolean colorable;

    public TextureData(String pTex0, double pWidth, double pHeight) {
        this(pTex0, null, pWidth, pHeight, false);
    }
    public TextureData(String pTex0, String pTex1, double pWidth, double pHeight, Boolean colorable) {
        super();
        this.tex0 = pTex0;
        this.tex1 = pTex1;
        this.width = pWidth;
        this.height = pHeight;
        this.colorable = colorable;
    }

    /**
     * @return the file
     */
    public String getTex0() {
        return this.tex0;
    }

    /**
     * @param pTex0
     *            the file to set
     */
    public void setTex0(String pTex0) {
        this.tex0 = pTex0;
    }

    /**
     * @return the lenght
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * @param pLenght
     *            the lenght to set
     */
    public void setLenght(double pLenght) {
        this.width = pLenght;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * @param pHeight
     *            the height to set
     */
    public void setHeight(double pHeight) {
        this.height = pHeight;
    }
    /**
     * @return the tex1
     */
    public String getTex1() {
        return tex1;
    }

    /**
     * @param tex1 the tex1 to set
     */
    public void setTex1(String tex1) {
        this.tex1 = tex1;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tex0 == null) ? 0 : tex0.hashCode());
        long temp;
        temp = Double.doubleToLongBits(height);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(width);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((tex1 == null) ? 0 : tex1.hashCode());
        return result;
    }
    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TextureData other = (TextureData) obj;
        if (tex0 == null) {
            if (other.tex0 != null)
                return false;
        } else if (!tex0.equals(other.tex0))
            return false;
        if (Double.doubleToLongBits(height) != Double.doubleToLongBits(other.height))
            return false;
        if (Double.doubleToLongBits(width) != Double.doubleToLongBits(other.width))
            return false;
        if (tex1 == null) {
            if (other.tex1 != null)
                return false;
        } else if (!tex1.equals(other.tex1))
            return false;
        return true;
    }
    /**
     * @return the colorable
     */
    public Boolean isColorable() {
        return colorable;
    }
    /**
     * @param colorable the colorable to set
     */
    public void setColorable(Boolean colorable) {
        this.colorable = colorable;
    }
    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        this.width = width;
    }

}
