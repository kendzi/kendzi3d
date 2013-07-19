package kendzi.jogl.model.geometry.material;

import java.awt.Color;

public class OtherComponent {

    private Color specularColor;
    private Color emissive = Color.BLACK;
    private float shininess;


//  public float shininess2;
//  public float transparency;
//  public float uTile;
//  public float vTile;
//  public float uOffset;
//  public float vOffset;

    /**
     *
     */
    public OtherComponent() {
        this(new Color(0.0f, 0.0f, 0.0f, 1.0f), new Color(0.0f, 0.0f, 0.0f, 1.0f), 0f);
    }

    /**
     * @param specularColor
     * @param emissive
     * @param shininess
     */
    public OtherComponent(Color specularColor, Color emissive, float shininess) {

        if (specularColor == null) {
            throw new IllegalArgumentException("specularColor can't be null");
        }

        if (emissive == null) {
            throw new IllegalArgumentException("emissive can't be null");
        }

        this.specularColor = specularColor;
        this.emissive = emissive;
        this.shininess = shininess;
    }

    /**
     * @return the specularColor
     */
    public Color getSpecularColor() {
        return this.specularColor;
    }

    /**
     * @return the emissive
     */
    public Color getEmissive() {
        return this.emissive;
    }

    /**
     * @return the shininess
     */
    public float getShininess() {
        return this.shininess;
    }



}
