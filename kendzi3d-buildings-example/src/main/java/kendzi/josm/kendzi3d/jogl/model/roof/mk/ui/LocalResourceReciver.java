package kendzi.josm.kendzi3d.jogl.model.roof.mk.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import kendzi.kendzi3d.resource.inter.ResourceService;

import org.apache.log4j.Logger;

public class LocalResourceReciver implements ResourceService {

    /** Log. */
    private static final Logger log = Logger.getLogger(LocalResourceReciver.class);

    //FIXME TODO XXX
    private String locationOfResources = "/home/kendzi/git/kendzi3d/kendzi3d-jogl-textures-library-res/src/main/resources";

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
            return new File(getFile(pFileName)).toURI().toURL();
        } catch (MalformedURLException e) {
            log.error(e, e);
        }
        return null;
    }

    @Override
    public String getPluginDir() {
        return this.locationOfResources;
    }

    @Override
    public URL resourceToUrl(String resourceName) {
        return receivePluginDirUrl(resourceName);
    }
}
