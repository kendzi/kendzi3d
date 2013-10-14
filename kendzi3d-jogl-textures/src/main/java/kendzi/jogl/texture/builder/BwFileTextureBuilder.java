package kendzi.jogl.texture.builder;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import kendzi.kendzi3d.resource.inter.ResourceService;

import org.apache.log4j.Logger;

import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;

public class BwFileTextureBuilder implements TextureBuilder {
    /** Log. */
    private static final Logger log = Logger.getLogger(BwFileTextureBuilder.class);

    /**
     * File url reciver service.
     */
    ResourceService resourceService;

    public BwFileTextureBuilder(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public String getBuilderPrefix() {
        return "#bw=";
    }

    @Override
    public Texture buildTexture(String pKey) throws Exception {

        if (pKey == null) {
            return null;
        }

        if (pKey.startsWith(getBuilderPrefix())) {
            pKey = pKey.substring(getBuilderPrefix().length());
        }

        TextureRenderer tr = loadTextureRenderer(pKey);

        if (tr != null) {
            return tr.getTexture();
        }

        return null;
    }

    @Override
    public BufferedImage buildImage(String pKey) {
        if (pKey == null) {
            return null;
        }

        if (pKey.startsWith(getBuilderPrefix())) {
            pKey = pKey.substring(getBuilderPrefix().length());
        }

        return loadBufferedImage(pKey);
    }

    public BufferedImage loadBufferedImage(String key) {
        if (key == null) {
            return null;
        }

        URL url = this.resourceService.resourceToUrl(key);
        if (url == null) {
            log.info("No file to load: " + key);
            return null;
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(url);
        } catch (IOException e) {
            log.warn("cant load image: " + key, e);
            return null;
        }

        filterBw(img);

        return img;
    }

    private TextureRenderer loadTextureRenderer(String pKey) {

        BufferedImage img = loadBufferedImage(pKey);
        if (img == null) {
            return null;
        }


        TextureRenderer tr = new TextureRenderer(img.getWidth(), img.getHeight(), true, true);

        Graphics2D g = tr.createGraphics();

        g.setClip(0, 0, img.getWidth(), img.getHeight());

        g.drawImage(img, new AffineTransform(1f,0f,0f,1f,0,0),  null);

        g.dispose();

        return tr;
    }



    protected void filterBw(BufferedImage colorFrame) {

        BufferedImage grayFrame = colorFrame;

        WritableRaster raster = grayFrame.getRaster();

        for(int x = 0; x < raster.getWidth(); x++) {
            for(int y = 0; y < raster.getHeight(); y++){
                int argb = colorFrame.getRGB(x,y);
                int r = argb >> 16 & 0xff;
            int g = argb >>  8 & 0xff;
            int b = argb & 0xff;

            int l = (int) (.299 * r + .587 * g + .114 * b);
            raster.setSample(x, y, 0, l);
            }
        }
    }
}
