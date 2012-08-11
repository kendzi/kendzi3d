package kendzi.jogl.model.geometry.material;

import java.awt.Color;

public class AmbientDiffuseComponent {
    private Color ambientColor;

    private Color diffuseColor;

    public AmbientDiffuseComponent() {
        this(new Color(0.5f, 0.5f, 0.5f, 1.0f), new Color(0.8f, 0.8f, 0.8f, 1.0f));
    }

    public AmbientDiffuseComponent(Color ambientColor, Color diffuseColor) {

        if (ambientColor == null) {
            throw new IllegalArgumentException("ambientColor can't be null");
        }

        if (diffuseColor == null) {
            throw new IllegalArgumentException("diffuseColor can't be null");
        }

        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
    }

    /**
     * @return the ambientColor
     */
    public Color getAmbientColor() {
        return this.ambientColor;
    }

    /**
     * @return the diffuseColor
     */
    public Color getDiffuseColor() {
        return this.diffuseColor;
    }


}
