package kendzi.josm.kendzi3d.service.textures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
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
    public BufferedImage buildImage(String pKey) {
        TextureRenderer build = build(pKey);
        if (build != null) {
            return imageToBufferedImage(build.getImage());
        }
        return null;
    }

    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bi = new BufferedImage
           (im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
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

    public static Color parseColor(String pColor) {
        // XXX move method to ColorUtil
        Color color = getColor(pColor);

        if (color != null) {
            color = color.brighter().brighter().brighter().brighter();
        }
//        if (Color.black.equals(color)) {
//        color = color.brighter().brighter().brighter().brighter().brighter();
//        }

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
    public static Color getColor(String colorName) {
        try {
            // Find the field and value of colorName
            Field field = Class.forName("java.awt.Color").getField(colorName);
            return (Color) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

}
