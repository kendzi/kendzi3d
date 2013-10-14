package kendzi.kendzi3d.resource.manager;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import kendzi.kendzi3d.resource.inter.ResourceService;

public class ResourceManagerService implements ResourceService {

    @Inject
    PluginResourceService pluginResourceService;



    @Override
    public URL receivePluginDirUrl(String pFileName) {
        throw new RuntimeException("DEPRICATED");
    }

    @Override
    public String getPluginDir() {
        throw new RuntimeException("DEPRICATED");
    }

    @Override
    public URL resourceToUrl(String resourceName) {
        try {
            URL url = null;

            if (resourceName.startsWith(PLUGIN_FILE_PREFIX)) {
                url = pluginResourceService.resourceToUrl(resourceName.substring(PLUGIN_FILE_PREFIX.length()));
            } else if (resourceName.startsWith("file:") || resourceName.startsWith("http://") || resourceName.startsWith("https://")) {
                url = new URL(resourceName);
            } else {
                //url = new File(resourceName).toURI().toURL();
                url = pluginResourceService.resourceToUrl(resourceName);
            }
            return url;
        } catch (MalformedURLException e) {
            throw new RuntimeException("can't recive URL for resource: " + resourceName);
        }
    }
}
