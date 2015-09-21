package kendzi.kendzi3d.buildings.model.element;


/**
 * Window on building part outline.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class WindowBuildingElement extends SquareHoleElement {

    /**
     * Default height of window.
     */
    private static final double DEFAULT_WINDOW_HEIGHT = 1.5;

    /**
     * Default width of window.
     */
    private static final double DEFAULT_WINDOW_WIDTH = 1.8;

    /**
     * Window type.
     */
    private String windowType;

    /**
     *
     */
    public WindowBuildingElement() {

        super(1, DEFAULT_WINDOW_HEIGHT, DEFAULT_WINDOW_WIDTH);

    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {

        WindowBuildingElement wbe = (WindowBuildingElement) super.clone();

        wbe.setWindowType(getWindowType());
        return wbe;
    }

    /**
     * @return the windowType
     */
    public String getWindowType() {
        return this.windowType;
    }

    /**
     * @param windowType the windowType to set
     */
    public void setWindowType(String windowType) {
        this.windowType = windowType;
    }
}
