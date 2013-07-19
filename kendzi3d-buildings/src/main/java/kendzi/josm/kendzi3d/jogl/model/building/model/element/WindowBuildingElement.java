package kendzi.josm.kendzi3d.jogl.model.building.model.element;

import kendzi.jogl.texture.dto.TextureData;


public class WindowBuildingElement extends SquareHoleElement {

    String windowType;

    @Deprecated
    TextureData textureData;

    /**
     *
     */
    public WindowBuildingElement() {

        super(1, 1.5, 1.8);




//        super();
//        this.minHeight = 1;
//
//        double height = 1.5;
//        this.height = this.minHeight + height;
//
//        this.width = 1.8;
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

    /**
     * @return the textureData
     */
    @Deprecated
    public TextureData getTextureData() {
        return textureData;
    }

    /**
     * @param textureData the textureData to set
     */
    @Deprecated
    public void setTextureData(TextureData textureData) {
        this.textureData = textureData;
    }


}
