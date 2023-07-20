package kendzi.jogl.texture.builder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import kendzi.jogl.glu.GLException;
import kendzi.jogl.util.texture.Texture;
import kendzi.jogl.util.texture.TextureIO;
import kendzi.kendzi3d.resource.inter.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTextureBuilder implements TextureBuilder {
    /** Log. */
    private static final Logger log = LoggerFactory.getLogger(FileTextureBuilder.class);

    /**
     * File url reciver service.
     */
    ResourceService resourceService;

    @Override
    public String getBuilderPrefix() {
        return "";
    }

    @Override
    public Texture buildTexture(String pKey) throws GLException, IOException {
        return loadTexture(pKey);
    }

    @Override
    public BufferedImage buildImage(String pKey) {
        // TODO Auto-generated method stub
        return null;
    }

    public Texture loadTexture(String name) throws GLException, IOException {

        if (name == null) {
            return null;
        }

        URL textUrl = this.resourceService.resourceToUrl(name);
        if (textUrl == null) {
            log.info("No file to load: " + name);
            return null;
        }

        return TextureIO.newTexture(textUrl, true, null);
    }

}
