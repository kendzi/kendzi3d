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
    String file;
    double lenght;
    double height;

    public TextureData(String pFile, double pLenght, double pHeight) {
        super();
        this.file = pFile;
        this.lenght = pLenght;
        this.height = pHeight;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return this.file;
    }

    /**
     * @param pFile
     *            the file to set
     */
    public void setFile(String pFile) {
        this.file = pFile;
    }

    /**
     * @return the lenght
     */
    public double getLenght() {
        return this.lenght;
    }

    /**
     * @param pLenght
     *            the lenght to set
     */
    public void setLenght(double pLenght) {
        this.lenght = pLenght;
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
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.file == null) ? 0 : this.file.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.height);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.lenght);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        if (this.file == null) {
            if (other.file != null)
                return false;
        } else if (!this.file.equals(other.file))
            return false;
        if (Double.doubleToLongBits(this.height) != Double.doubleToLongBits(other.height))
            return false;
        if (Double.doubleToLongBits(this.lenght) != Double.doubleToLongBits(other.lenght))
            return false;
        return true;
    }
}
