/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * Implements loaders for native library for JOGL.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public abstract class NativeLibPlugin extends Plugin {

    /** Log. */
    private static final Logger log = Logger.getLogger(NativeLibPlugin.class);

    /**
     * Name of plugin properties file.
     */
    static final String PLUGINPROPERTIES_FILENAME = "pluginProperties.properties";

    /**
     * Main plugin properties.
     */
    private Properties pluginProperties;

    /**
     * @param info
     */
    public NativeLibPlugin(PluginInformation info) {
        super(info);
    }

    /**
     * Plugin is loaded dynamically so we need to load required jars and native
     * library.
     * 
     * @throws IOException
     *             ups
     * @throws InvocationTargetException
     *             ups
     * @throws IllegalAccessException
     *             ups
     * @throws NoSuchMethodException
     *             ups
     */
    protected void loadLibrary() throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        copy("/log4j.properties", "log4j.properties");

        // If resources are available load properties from plugin directory
        if (this.pluginProperties == null || this.pluginProperties.isEmpty()) {
            this.pluginProperties = new Properties();

            this.pluginProperties.load(getResourceAsStream(PLUGINPROPERTIES_FILENAME));
        }

        log.info("starting for os: " + getOsAndArch());

        // for general jars
        // XXX this will be drop in future
        List<String> liblaryNamesList = loadPropertitesValues("library", "");

        copyFilesFromJar(liblaryNamesList);

        addJarsToClassLoader(liblaryNamesList);
    }

    /**
     * Load list of values from plugin properties file. Loading keys in form
     * "key{number}".
     * 
     * @param keyPrefix
     *            key prefix
     * @param systemVariable
     *            system version variable
     * @return list of values matching to keyPrefix
     */
    private List<String> loadPropertitesValues(String keyPrefix, String systemVariable) {
        List<String> liblaryNamesList = new ArrayList<String>();

        for (int i = 0; i < 100; i++) {

            String libraryName = this.pluginProperties.getProperty(keyPrefix + i);
            if (libraryName != null && !"".equals(libraryName)) {
                liblaryNamesList.add(libraryName.replaceAll("\\{jogl_system_version\\}", systemVariable));
            }
        }
        return liblaryNamesList;
    }

    /**
     * Try to find operation system name and arch.
     * 
     * @return os and arch
     */
    private String getOsAndArch() {
        String system;
        String arch = System.getProperty("os.arch");
        if ("amd64".equals(arch) || "x86_64".equals(arch)) {
            arch = "amd64";
        } else if ("x86".equals(arch) || "i386".equals(arch)) {
            arch = "i586";
        } else {
            throw new RuntimeException("unknown arch: " + arch + ", arch is not supported. Change commputer "
                    + "or setup variable 'jogl_system_version' in pluginProperties.properties");
        }

        String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Windows")) {
            os = "windows";
        } else if (os != null && os.startsWith("Linux")) {
            os = "linux";
        } else if (os != null && os.startsWith("Mac OS X")) {
            os = "macosx";
            arch = "universal";
        } else {
            throw new RuntimeException("unknown OS: " + os + ", OS is not supported. Change OS "
                    + "or setup variable 'jogl_system_version' in pluginProperties.properties");
        }

        // sun.desktop

        system = os + "-" + arch;
        return system;
    }

    /**
     * Registering external jars to ClassLoader.
     * 
     * @param pLiblaryNamesList
     *            list of jars
     * 
     * @throws NoSuchMethodException
     *             ups
     * @throws IllegalAccessException
     *             ups
     * @throws InvocationTargetException
     *             ups
     * @throws MalformedURLException
     *             ups
     */
    private void addJarsToClassLoader(List<String> pLiblaryNamesList) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, MalformedURLException {

        URLClassLoader sysLoader = (URLClassLoader) Main.class.getClassLoader();

        // try to load jars and dll
        Class<URLClassLoader> sysclass = URLClassLoader.class;
        Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
        method.setAccessible(true);

        File library = new File(getPluginDir() + "/");

        for (int i = 0; i < pLiblaryNamesList.size(); i++) {
            library = new File(getPluginDir() + "/" + pLiblaryNamesList.get(i));
            if (library.exists()) {
                System.out.println("loading lib: " + library.getAbsoluteFile());
            } else {
                System.err.println("lib don't exist!: " + library.getAbsoluteFile());
            }
            method.invoke(sysLoader, new Object[] { library.toURI().toURL() });
        }
    }

    /**
     * Coping file list from jar to plugin dir.
     * 
     * @param pFilesPathList
     *            list of files path in jar
     * 
     * @throws IOException
     *             ups
     */
    private void copyFilesFromJar(List<String> pFilesPathList) throws IOException {
        for (String libName : pFilesPathList) {
            copy(libName, libName);
        }
    }

    @Override
    public void copy(String from, String to) throws FileNotFoundException, IOException {
        try {
            System.out.println("copying file from jar: " + from + " to: " + to);
            makeDirs(to);

            String pluginDirName = getPluginDir();
            File pluginDir = new File(pluginDirName);
            if (!pluginDir.exists()) {
                pluginDir.mkdirs();
            }

            URL fromUrl = getResourceUrl(from);
            if (fromUrl == null) {
                throw new Exception("Can't get url for from location: " + from);
            }

            InputStream in = null;
            try {
                in = fromUrl.openStream();
            } catch (IOException e) {
                throw new Exception("Can't open stream to resource: " + fromUrl, e);
            }

            FileOutputStream out = new FileOutputStream(new File(pluginDirName, to));

            byte[] buffer = new byte[8192];
            long l = 0;
            for (int len = in.read(buffer); len > 0; len = in.read(buffer)) {
                out.write(buffer, 0, len);
                l = l + len;
            }
            in.close();
            out.close();

            if (log.isInfoEnabled()) {
                log.info("end of copying bytes: " + l + " from file: " + from + " at url: " + fromUrl);
            }

        } catch (java.lang.NullPointerException e) {
            // for debugging and testing I don't care.
            log.error(e, e);
        } catch (Exception e) {
            // for debugging and testing I don't care.
            log.error(e, e);
        }
    }

    /**
     * Make all sub directories.
     * 
     * @param pFileName
     *            file which require directory
     */
    private void makeDirs(String pFileName) {

        String pluginDirName = getPluginDir();
        File pluginDir = new File(pluginDirName);
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
        }
        File dir = new File(pluginDirName, pFileName).getParentFile();
        if (!dir.exists()) {
            System.out.println("Dir don't exist. Creatin new dir: " + dir.getAbsolutePath());
            dir.mkdirs();
        }
    }

    /**
     * Returns an input stream for reading the specified resource.
     * 
     * <p>
     * The search order is described in the documentation for
     * {@link #getResource(String)}.
     * </p>
     * 
     * @param name
     *            The resource name
     * 
     * @return An input stream for reading the resource, or <tt>null</tt> if the
     *         resource could not be found
     * 
     * @since 1.1
     */
    public InputStream getResourceAsStream(String name) {
        URL url = getResourceUrl(name);
        InputStream ret = null;
        try {
            ret = url != null ? url.openStream() : null;
        } catch (IOException e) {
            log.error("can't open stream: " + name, e);
        }
        if (ret == null) {
            URL current = getResourceUrl(".");
            log.info(String.format("Can't open stream to resource: %1s url is: %2s current location is: %3s", name, url, current));
        }
        return ret;
    }

    public URL getResourceUrl(String pResName) {

        if (pResName != null) {
            pResName = pResName.trim();
            if (pResName.startsWith("/")) {
                pResName = pResName.substring(1);
            }
        }

        URL url = getPluginResourceClassLoader().getResource(pResName);
        if (url == null) {
            URL current = getPluginResourceClassLoader().getResource(".");
            log.warn(String.format("can't find resource name: %1s in current location: %2s", pResName, current));
        }

        return url;
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
    public static URL getResourceUrl_OLD(String pResName) {
        // FIXME make util with FileUrlReciverService.getResourceUrl(...)

        URL resource = NativeLibPlugin.class.getResource("");
        // log.info("resource: " + resource);

        String resUrl = resource.toString();
        if (resUrl.startsWith("jar:")) {
            // if we are in jar
            try {
                String newURL = resUrl.substring(0, resUrl.indexOf("!") + 1) + pResName;
                // log.info("new url: " + newURL);
                return new URL(newURL);
            } catch (MalformedURLException e) {
                log.error(e, e);
            }

        } else {
            // if we run from eclipse ide
            URL res = NativeLibPlugin.class.getResource(pResName);
            if (res != null) {
                return res;
            }
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

}
