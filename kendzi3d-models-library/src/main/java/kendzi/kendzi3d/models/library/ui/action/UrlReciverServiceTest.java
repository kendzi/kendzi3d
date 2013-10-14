package kendzi.kendzi3d.models.library.ui.action;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import kendzi.kendzi3d.resource.inter.ResourceService;

public class UrlReciverServiceTest implements ResourceService {

    @Override
    public URL receivePluginDirUrl(String pFileName) {
        return null;
    }

    @Override
    public String getPluginDir() {
        return null;
    }

    @Override
    public URL resourceToUrl(String resourceName) {
        try {
            // FIXME TODO XXX
            String base = "C:/Users/kendzi/git/kendzi3d/kendzi.josm.plugin3d";
            return new File(base + resourceName).toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}