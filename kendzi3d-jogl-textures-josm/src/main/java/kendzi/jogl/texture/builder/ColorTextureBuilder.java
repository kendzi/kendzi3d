package kendzi.jogl.texture.builder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import kendzi.josm.kendzi3d.util.ColorUtil;

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

        Color color = ColorUtil.parseColor(pKey.substring(3));
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



}
