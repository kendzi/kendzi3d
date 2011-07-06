/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.metadata;

public class ModelMetadata {
    /**
     * Model location.
     */
    String file;
    /**
     * Default height.
     */
    double defaultHeight;
    /**
     * Default width. If not determinate
     */
    Double defaultWidth;
//    /**
//     *
//     */
//    boolean keepRatio;

    /**
     * @return the file
     */
    public String getFile() {
        return this.file;
    }
    /**
     * @param pFile the file to set
     */
    public void setFile(String pFile) {
        this.file = pFile;
    }
    /**
     * @return the defaultHeight
     */
    public double getDefaultHeight() {
        return this.defaultHeight;
    }
    /**
     * @param pDefaultHeight the defaultHeight to set
     */
    public void setDefaultHeight(double pDefaultHeight) {
        this.defaultHeight = pDefaultHeight;
    }
    /**
     * @return the defaultWidth
     */
    public Double getDefaultWidth() {
        return this.defaultWidth;
    }
    /**
     * @param pDefaultWidth the defaultWidth to set
     */
    public void setDefaultWidth(Double pDefaultWidth) {
        this.defaultWidth = pDefaultWidth;
    }

}
