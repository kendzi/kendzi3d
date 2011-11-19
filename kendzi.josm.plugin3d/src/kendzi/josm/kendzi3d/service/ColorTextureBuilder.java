package kendzi.josm.kendzi3d.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.lang.reflect.Field;

import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;

/**
 * Color texture builder.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class ColorTextureBuilder implements TextureBuilder {

    @Override
    public String getBuilderPrefix() {
        return "#c=";
    }

    @Override
    public Texture buildTexture(String pKey) {
        TextureRenderer build = build(pKey);
        if (build != null) {
            return build.getTexture();
        }
        return null;
    }

    @Override
    public Image buildImage(String pKey) {
        TextureRenderer build = build(pKey);
        if (build != null) {
            return build.getImage();
        }
        return null;
    }

    private TextureRenderer build(String pKey) {

        Color color = parseColor(pKey.substring(3));
        if (color == null) {
            return null;
        }

        TextureRenderer img = new TextureRenderer(1, 1, true, true);

        Graphics2D g = img.createGraphics();

        g.setClip(0, 0, 1, 1);

        g.setColor(color);

        g.fillRect(0, 0, 1, 1);

        return img;
    }

    private Color parseColor(String pColor) {

        Color color = getColor(pColor);
        if (Color.black.equals(color)) {
            color = color.brighter().brighter().brighter().brighter().brighter();
        }

        if (color == null) {
            try {
                color = Color.decode(pColor);
            } catch (Exception e) {
                //
            }
        }

        return color;
    }

    /**
     * Returns a Color based on 'colorName' which must be one of the predefined colors in
     * java.awt.Color. Returns null if colorName is not valid.
     * @param colorName
     * @return
     */
    public Color getColor(String colorName) {
        try {
            // Find the field and value of colorName
            Field field = Class.forName("java.awt.Color").getField(colorName);
            return (Color) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

}
