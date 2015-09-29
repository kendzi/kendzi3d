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
import java.net.URL;

import org.apache.log4j.Logger;
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
     * @param info
     *            plugin information
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

        // copy("/log4j.properties", "log4j.properties");

        log.info("starting for os: " + getOsAndArch());
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
            log.info(
                    String.format("Can't open stream to resource: %1s url is: %2s current location is: %3s", name, url, current));
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

}
