/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.service.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import kendzi.josm.kendzi3d.module.binding.Kendzi3dPluginDirectory;
import kendzi.josm.kendzi3d.service.UrlReciverService;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Receive files stored locally in resources and plugin directory.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public final class FileUrlReciverService implements UrlReciverService {

    /** Log. */
    private static final Logger log = Logger.getLogger(FileUrlReciverService.class);

    /**
     * Plugin directory.
     */
    private final String pluginDir;


    /** Constructor.
     * @param pPluginDir location of resources
     *
     * XXX rename to JoglPluginUrlReciverService
     */
    @Inject
    public FileUrlReciverService(@Kendzi3dPluginDirectory String pPluginDir) {
        this.pluginDir = pPluginDir;
    }
//    /**
//     * Constructor.
//     */
//    public FileUrlReciverService() {
//        //
//    }

//    /**
//     * Setup plugin directory.
//     * @param pPluginDir plugin directory
//     */
//    public static void initFileReciver(String pPluginDir) {
//        pluginDir = pPluginDir;
//    }
    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.service.UrlReciverService#receiveFileUrl(java.lang.String)
     */
    @Override
    public URL receiveFileUrl(String pFileName) {

        URL url = receivePluginDirUrl(pFileName);
        if (url != null) {
            return url;
        }

        return getResourceUrl(pFileName);
    }

    /**
     * @param pFileName
     */
    @Override
    public URL receivePluginDirUrl(String pFileName) {
        File f = new File(this.pluginDir, pFileName);
        System.out.println("reciveFileUrl: " + f.getAbsoluteFile());
        if (f.exists()) {
            try {
                return f.toURI().toURL();
            } catch (MalformedURLException e) {
                log.error("error reciving URL for: " + pFileName, e);
            }
        }
        return null;
    }

    /**
     * Try to find URL of file in resources. In some reason getClass().getResource(...) can't find file if it is in jar
     * and file in sub dir. So at this location it work fine: /res/file but if file is deeper like this: /res/dir/file
     * url returned by getResource is bad. It is possible that it is bug in URLClassLoader or ClassLoader require some
     * strange configuration. This function is overround for this bug.
     *
     * Function require resource name to be taken from root.
     *
     * @param pResName
     *            started from "/" of jar or project in eclipse
     * @return url to resource
     */
    public static URL getResourceUrl(String pResName) {
//        ProtectionDomain pDomain = FileReciver.class.getProtectionDomain();
//        CodeSource codeSource = pDomain.getCodeSource();
//        //        if (codeSource == null) throw new CannotFindDirException();
//        URL loc = codeSource.getLocation();
//        log.info("loc: " + loc);

        URL resource = FileUrlReciverService.class.getResource("");
//        log.info("resource: " + resource);

        String resUrl = resource.toString();
        if (resUrl.startsWith("jar:")) {
            // if we are in jar
            try {
                String newURL = resUrl.substring(0, resUrl.indexOf("!") + 1) + pResName;
//                log.info("new url: " + newURL);
                return new URL(newURL);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            // if we run from eclipse ide
            URL res = FileUrlReciverService.class.getResource(pResName);
            if (res != null) {
                return res;
            }
        }

        // if it is not from root
        //THIS MAGICALLY WORK:

        // When the string was not a valid URL, try to load it as a resource using
        // an anonymous class in the tree.
        Object objectpart = new Object() { };
        Class classpart = objectpart.getClass();
        ClassLoader loaderpart = classpart.getClassLoader();
        URL result = loaderpart.getResource(pResName);

        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.service.UrlReciverService#getPluginDir()
     */
    @Override
    public String getPluginDir() {
        return this.pluginDir;
    }

//    /**
//     * @param pluginDir the pluginDir to set
//     */
//    public void setPluginDir(String pluginDir) {
//        this.pluginDir = pluginDir;
//    }
}
