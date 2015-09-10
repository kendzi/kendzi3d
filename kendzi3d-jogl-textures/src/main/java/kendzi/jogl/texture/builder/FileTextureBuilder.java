package kendzi.jogl.texture.builder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import com.jogamp.opengl.GLException;

import kendzi.kendzi3d.resource.inter.ResourceService;

import org.apache.log4j.Logger;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class FileTextureBuilder implements TextureBuilder {
    /** Log. */
    private static final Logger log = Logger.getLogger(FileTextureBuilder.class);

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
