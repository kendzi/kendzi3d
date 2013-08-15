package kendzi.kendzi3d.models.library.ui.action;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import kendzi.josm.kendzi3d.service.UrlReciverService;

public class UrlReciverServiceTest implements UrlReciverService {
    @Override
    public URL receiveFileUrl(String pFileName) {
        try {
            // FIXME TODO XXX
            String base = "C:/Users/kendzi/git/kendzi3d/kendzi.josm.plugin3d";
            return (new File(base + pFileName)).toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public URL receivePluginDirUrl(String pFileName) {
        return null;
    }

    @Override
    public String getPluginDir() {
        return null;
    }
}