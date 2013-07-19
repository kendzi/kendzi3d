package kendzi.josm.kendzi3d.jogl.model.building.model.element;

public class SquareHoleElement extends BuildingNodeElement{

    private double minHeight = 0;

    private double height = 2;

    private double width = 1d;



    public SquareHoleElement() {
        this(0, 2, 1);
    }

    public SquareHoleElement(double minHeight, double height, double width) {
        super();
        this.minHeight = minHeight;
        this.height = height;
        this.width = width;
    }

    /** Max height as minHeight + height
     * @return
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
