/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.model.building.model.element;

/**
 * Square hole in wall on building part outline.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class SquareHoleElement extends BuildingNodeElement{

    /**
     * Minimal height of hole.
     */
    private double minHeight = 0;

    /**
     * Height of hole. From the lower edge to upper edge.
     */
    private double height = 2;

    /**
     * Width of hole.
     */
    private double width = 1d;



    /**
     * Con.
     */
    public SquareHoleElement() {
        this(0, 2, 1);
    }

    /**
     * Con.
     * 
     * @param minHeight minimal height of hole
     * @param height height of hole
     * @param width width of hole
     */
    public SquareHoleElement(double minHeight, double height, double width) {
        super();
        this.minHeight = minHeight;
        this.height = height;
        this.width = width;
    }

    /**
     * Max height as minHeight + height
     * 
     * @return the maxHeight
     */
    public double getMaxHeight() {
        return this.minHeight + this.height;
    }

    /**
     * @return the minHeight
     */
    public double getMinHeight() {
        return this.minHeight;
    }

    /**
     * @param minHeight the minHeight to set
     */
    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        SquareHoleElement wbe = (SquareHoleElement) super.clone();
        wbe.setMinHeight(this.getMinHeight());
        wbe.setHeight(this.getHeight());
        wbe.setWidth(this.getWidth());
        return wbe;
    }



}
