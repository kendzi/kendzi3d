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
        return lenght;
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
        return height;
    }

    /**
     * @param pHeight
     *            the height to set
     */
    public void setHeight(double pHeight) {
        this.height = pHeight;
    }

}
