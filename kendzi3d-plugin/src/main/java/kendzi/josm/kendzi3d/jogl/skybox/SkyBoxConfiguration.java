package kendzi.josm.kendzi3d.jogl.skybox;

/**
 * Skybox configuration.
 */
public class SkyBoxConfiguration {

    private String frontTexture;
    private String rightTexture;
    private String leftTexture;
    private String backTexture;
    private String topTexture;

    /**
     * @return the frontTexture
     */
    public String getFrontTexture() {
        return frontTexture;
    }

    /**
     * @param frontTexture
     *            the frontTexture to set
     */
    public void setFrontTexture(String frontTexture) {
        this.frontTexture = frontTexture;
    }

    /**
     * @return the rightTexture
     */
    public String getRightTexture() {
        return rightTexture;
    }

    /**
     * @param rightTexture
     *            the rightTexture to set
     */
    public void setRightTexture(String rightTexture) {
        this.rightTexture = rightTexture;
    }

    /**
     * @return the leftTexture
     */
    public String getLeftTexture() {
        return leftTexture;
    }

    /**
     * @param leftTexture
     *            the leftTexture to set
     */
    public void setLeftTexture(String leftTexture) {
        this.leftTexture = leftTexture;
    }

    /**
     * @return the backTexture
     */
    public String getBackTexture() {
        return backTexture;
    }

    /**
     * @param backTexture
     *            the backTexture to set
     */
    public void setBackTexture(String backTexture) {
        this.backTexture = backTexture;
    }

    /**
     * @return the topTexture
     */
    public String getTopTexture() {
        return topTexture;
    }

    /**
     * @param topTexture
     *            the topTexture to set
     */
    public void setTopTexture(String topTexture) {
        this.topTexture = topTexture;
    }
}