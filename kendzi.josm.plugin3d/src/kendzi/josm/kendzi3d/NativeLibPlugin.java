package kendzi.josm.kendzi3d;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kendzi.josm.kendzi3d.service.FileUrlReciverService;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * Implements loaders for native library for JOGL.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public abstract class NativeLibPlugin extends Plugin {


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
     * Plugin is loaded dynamically so we need to load required jars and native library.
     *
     * @throws IOException ups
     * @throws InvocationTargetException ups
     * @throws IllegalAccessException ups
     * @throws NoSuchMethodException ups
     */
    protected void loadLibrary() throws IOException, InvocationTargetException, NoSuchMethodException,
            IllegalAccessException {

//        XXX for now don't copy files, it is required some validation if user don't mess them
//
//        if (!isFileExis(PLUGINPROPERTIES_FILENAME)) {
//            copy("/resources/" + PLUGINPROPERTIES_FILENAME, PLUGINPROPERTIES_FILENAME);
//        }
//
//        if (!isFileExis("log4j.properties")) {
            copy("/resources/log4j.properties", "log4j.properties");
//        }

        // If resources are available load properties from plugin directory
        if (this.pluginProperties == null || this.pluginProperties.isEmpty()) {
            this.pluginProperties = new Properties();
            URL pluginPropertiesUrl = FileUrlReciverService.reciveFileUrl("/resources/" + PLUGINPROPERTIES_FILENAME);
            this.pluginProperties.load(pluginPropertiesUrl.openStream());

        }


        String system = this.pluginProperties.getProperty("jogl_system_version", null);
        if (system == null || "auto".equals(system)) {
            system = getOsAndArch();
        }

        // for general jars
        List<String> liblaryNamesList = loadPropertitesValues("library", system);

        copyFilesFromJar(liblaryNamesList);

        addJarsToClassLoader(liblaryNamesList);

        // for platform dependent jars
        List<String> platformLiblaryNamesList = loadPropertitesValues("library-" + system + "-", system);

        copyFilesFromJar(platformLiblaryNamesList);

        addJarsToClassLoader(platformLiblaryNamesList);

        // for native libraries
        List<String> platformNativeLiblaryNamesList = loadPropertitesValues("nativeLibrary-" + system + "-", system);
        copyFilesFromJar(platformNativeLiblaryNamesList);

        String platformNativeLiblaryPath = this.pluginProperties.getProperty("jogl_dir", "/lib");
        platformNativeLiblaryPath =
            getPluginDir()
            + platformNativeLiblaryPath.replaceAll("\\{jogl_system_version\\}", system);


        setUpJavaLibraryPath(platformNativeLiblaryPath);

    }

    /** Load list of values from plugin properties file. Loading keys in form "key{number}".
     * @param pKeyPrefix key prefix
     * @param pSystemVariable system version variable
     * @return list of values matching to keyPrefix
     */
    private List<String> loadPropertitesValues(String pKeyPrefix, String pSystemVariable) {
        List<String> liblaryNamesList = new ArrayList<String>();

        for (int i = 0; i < 100; i++) {

            String libraryName = this.pluginProperties.getProperty(pKeyPrefix + i);
            if (libraryName != null && !"".equals(libraryName)) {
                liblaryNamesList.add(libraryName.replaceAll("\\{jogl_system_version\\}", pSystemVariable));
            }
        }
        return liblaryNamesList;
    }

    /** Try to find operation system name and arch.
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
            throw new RuntimeException("unknown arch: " + arch
                    + ", arch is not supported. Change commputer "
                    + "or setup variable 'jogl_system_version' in pluginProperties.properties");
        }

        String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Windows")) {
            os = "windows";
        } else if (os != null && os.startsWith("Linux")) {
            os = "linux";
        } else {
            throw new RuntimeException("unknown OS: " + os
                    + ", OS is not supported. Change OS "
                    + "or setup variable 'jogl_system_version' in pluginProperties.properties");
        }


        //sun.desktop

        system = os + "-" + arch;
        return system;
    }

    /**
     * Registering external jars to ClassLoader.
     *
     * @param pLiblaryNamesList list of jars
     *
     * @throws NoSuchMethodException ups
     * @throws IllegalAccessException ups
     * @throws InvocationTargetException ups
     * @throws MalformedURLException ups
     */
    private void addJarsToClassLoader(List<String> pLiblaryNamesList)
    throws NoSuchMethodException, IllegalAccessException,
    InvocationTargetException, MalformedURLException {

        // URLClassLoader sysLoader = (URLClassLoader)
        // ClassLoader.getSystemClassLoader();
        URLClassLoader sysLoader = (URLClassLoader) Main.class.getClassLoader();

        // try to load jars and dll
        Class<URLClassLoader> sysclass = URLClassLoader.class;
        Method method = sysclass.getDeclaredMethod("addURL",
                new Class[] { URL.class });
        method.setAccessible(true);

        File library = new File(getPluginDir() + "/");
        if (library.exists()) {
            System.out.println("loading lib: " + library.getAbsoluteFile());
        } else {
            System.err.println("lib don't exist!: " + library.getAbsoluteFile());
        }

        method.invoke(sysLoader, new Object[] { library.toURI().toURL() });

        for (int i = 0; i < pLiblaryNamesList.size(); i++) {
            library = new File(getPluginDir() + "/" + pLiblaryNamesList.get(i));
            System.out.println("loadin lib: " + library.getAbsoluteFile());
            method.invoke(sysLoader, new Object[] { library.toURI().toURL() });
        }
    }

    /** Coping file list from jar to plugin dir.
     * @param pFilesPathList list of files path in jar
     *
     * @throws IOException ups
     */
    private void copyFilesFromJar(List<String> pFilesPathList)
    throws IOException {
        for (String libName : pFilesPathList) {

            //            if (!isFileExis(libName)) {
            copy(libName, libName);
            //            }
        }
    }

    /** To run jogl we need setup path to native libelers. Probably there are many ways to do that.
     * This is probably the worst.
     * To load native libelers we need to setup java.library.path. Because plugins are
     * load dynamically and we don't want to setup variables on command line when jogl start we need
     * mess up with classLoader.
     *
     * DIRTY WORK
     *
     * @param pNativeLiblarysPath path to jogl native library
     */
    private void setUpJavaLibraryPath(String pNativeLiblarysPath) {

        Class<ClassLoader> classLoaderClass = ClassLoader.class;

        Field field;
        try {
            // we need to add path with jogl library's to class loader field "usr_paths"
            // get field and make it accessible
            field = classLoaderClass.getDeclaredField("usr_paths");
            field.setAccessible(true);

            // get field value
            String[] usrPaths = (String[]) field.get(classLoaderClass);

            if (usrPaths != null) {
                // add dir to array
                String[] newUsrPaths = new String[usrPaths.length + 1];
                newUsrPaths[0] = pNativeLiblarysPath;
                //"C:/josm_dev/plugins/plugin3d";

                System.arraycopy(usrPaths, 0, newUsrPaths, 1, usrPaths.length);

                // set value to field
                field.set(classLoaderClass, newUsrPaths);
            }

        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
