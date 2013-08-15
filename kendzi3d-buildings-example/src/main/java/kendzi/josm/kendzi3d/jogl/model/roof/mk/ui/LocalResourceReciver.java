package kendzi.josm.kendzi3d.jogl.model.roof.mk.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import kendzi.josm.kendzi3d.service.UrlReciverService;

import org.apache.log4j.Logger;

public class LocalResourceReciver implements UrlReciverService {

    /** Log. */
    private static final Logger log = Logger.getLogger(LocalResourceReciver.class);

    //FIXME TODO XXX
    private String locationOfResources = "C:/Users/kendzi/git/kendzi3d/kendzi3d-jogl-textures-library-res/src/main/resources";

    private String getFile(String str) {

        if (str == null) {
            return null;
        }

        if (str.startsWith("/") || str.startsWith("\\")) {
            str = str.substring(1);
        }

        return this.locationOfResources + "/" + str;
    }

    @Override
    public URL receivePluginDirUrl(String pFileName) {
        try {
            return (new File(getFile(pFileName)).toURI().toURL());
        } catch (MalformedURLException e) {
            log.error(e, e);
        }
        return null;
    }

    @Override
    public URL receiveFileUrl(String pFileName) {
        return receivePluginDirUrl(pFileName);
    }

    @Override
    public String getPluginDir() {
        return this.locationOfResources;
    }
}
