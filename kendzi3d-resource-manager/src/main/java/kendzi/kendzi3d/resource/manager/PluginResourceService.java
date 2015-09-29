/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.resource.manager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import kendzi.kendzi3d.resource.inter.ResourceService;

/**
 * Receive files stored locally in resources or plugin directory.
 * 
 * @author Tomasz Kedziora (Kendzi)
 */
public final class PluginResourceService implements ResourceService {

    /** Log. */
    private static final Logger log = Logger.getLogger(PluginResourceService.class);

    /**
     * Plug-in directory.
     */
    private final String pluginDirectory;

    /**
     * Constructor.
     * 
     * @param pluginDirectory
     *            location of resources
     * 
     */
    public PluginResourceService(String pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
    }

    /**
     * @param fileName
     */
    @Override
    public URL receivePluginDirUrl(String fileName) {
        File f = new File(this.pluginDirectory, fileName);
        log.info("reciveFileUrl: " + f.getAbsoluteFile());
        if (f.exists()) {
            try {
                return f.toURI().toURL();
            } catch (MalformedURLException e) {
                log.error("error reciving URL for: " + fileName, e);
            }
        }
        return null;
    }

    /**
     * Try to find URL of file in resources. In some reason
     * getClass().getResource(...) can't find file if it is in jar and file in
     * sub dir. So at this location it work fine: /res/file but if file is
     * deeper like this: /res/dir/file url returned by getResource is bad. It is
     * possible that it is bug in URLClassLoader or ClassLoader require some
     * strange configuration. This function is overround for this bug.
     * 
     * Function require resource name to be taken from root.
     * 
     * @param pResName
     *            started from "/" of jar or project in eclipse
     * @return url to resource
     */
    public static URL getResourceUrl(String pResName) {

        // URL resource = PluginResourceService.class.getResource("");
        // // log.info("resource: " + resource);
        //
        // String resUrl = resource.toString();
        // if (resUrl.startsWith("jar:") && resUrl.contains("kendzi3d.jar")) {
        // // XXX only for JOSN plugin,
        // // FIXME remove
        // // if we are in jar
        // try {
        // String newURL = resUrl.substring(0, resUrl.indexOf("!") + 1) +
        // pResName;
        // // log.info("new url: " + newURL);
        // return new URL(newURL);
        // } catch (MalformedURLException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // }

        // if we run from eclipse ide
        URL res = PluginResourceService.class.getResource(pResName);
        if (res != null) {
            return res;
        }

        // if it is not from root
        // THIS MAGICALLY WORK:

        // When the string was not a valid URL, try to load it as a resource
        // using
        // an anonymous class in the tree.
        Object objectpart = new Object() {
        };
        Class classpart = objectpart.getClass();
        ClassLoader loaderpart = classpart.getClassLoader();
        URL result = loaderpart.getResource(pResName);

        return result;
    }

    @Override
    public String getPluginDir() {
        throw new RuntimeException("DEPRICATED");
    }

    @Override
    public URL resourceToUrl(String resourceName) {
        URL url = receivePluginDirUrl(resourceName);
        if (url != null) {
            return url;
        }

        return getResourceUrl(resourceName);
    }
}
