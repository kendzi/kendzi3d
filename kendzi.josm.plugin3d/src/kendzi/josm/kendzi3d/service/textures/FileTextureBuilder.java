package kendzi.josm.kendzi3d.service.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.media.opengl.GLException;

import kendzi.josm.kendzi3d.service.UrlReciverService;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class FileTextureBuilder implements TextureBuilder {
    /** Log. */
    private static final Logger log = Logger.getLogger(FileTextureBuilder.class);

    /**
     * File url reciver service.
     */
    @Inject
    UrlReciverService fileUrlReciverService;

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

    public Texture loadTexture(String pName) throws GLException, IOException {

        if (pName == null) {
            return null;
        }

        URL textUrl = this.fileUrlReciverService.receiveFileUrl(pName);
        if (textUrl == null) {
            log.info("No file to load: " + pName);
            return null;
        }

        return TextureIO.newTexture(textUrl, true, null);
    }

}
