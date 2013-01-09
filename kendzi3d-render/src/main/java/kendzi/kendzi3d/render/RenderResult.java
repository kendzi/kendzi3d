package kendzi.kendzi3d.render;

public class RenderResult {
    byte [] image;


    public RenderResult(byte[] image) {
        super();
        this.image = image;
    }

    /**
     * @return the image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

}
