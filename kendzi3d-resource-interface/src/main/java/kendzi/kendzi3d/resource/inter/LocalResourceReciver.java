package kendzi.kendzi3d.resource.inter;

import java.net.URL;

/**
 * Simple resource locator from classpath.
 */
public class LocalResourceReciver implements ResourceService {

    @Override
    public URL receivePluginDirUrl(String fileName) {
        return null;
    }

    @Override
    public String getPluginDir() {
        return null;
    }

    @Override
    public URL resourceToUrl(String resourceName) {
        return this.getClass().getResource(resourceName);
    }
}
