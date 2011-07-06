/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Receive files stored locally in resources and plugin directory.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public final class FileUrlReciverService {

    /**
     * Plugin directory.
     */
    private static String pluginDir;

    /**
     * Utility classes should not have a public or default constructor.
     */
    private FileUrlReciverService() {
        //
    }

    /**
     * Setup plugin directory.
     * @param pPluginDir plugin directory
     */
    public static void initFileReciver(String pPluginDir) {
        pluginDir = pPluginDir;
    }
    /**
     * Try to get file URL. It is looking at:<br>
     * 1. directory: {PLUGIN_DIR_NAME}/ <br>
     * 2. resources from jar in directory: {PLUGIN_JAR}/ <br>
     *
     *
     * @param pFileName file name
     * @return file url
     */
    public static URL reciveFileUrl(String pFileName) {

        File f = new File(pluginDir, pFileName);

        if (f.exists()) {
            try {
                return f.toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return getResourceUrl(pFileName);
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
}
