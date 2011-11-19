/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import kendzi.josm.kendzi3d.service.ColorTextureBuilder;
import kendzi.josm.kendzi3d.service.FileUrlReciverService;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.TextureCacheService;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLEventListener;
import kendzi.josm.kendzi3d.ui.View3dGLFrame;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

import test.TestRener;

public class Kendzi3DPlugin extends Plugin {

    /**
     * Name of plugin properties file.
     */
    static final String PLUGINPROPERTIES_FILENAME = "pluginProperties.properties";

    /**
     * Main plugin properties.
     */
    private Properties pluginProperties;


    /**
     * Menu in JOSM.
     */
    JMenu view3dJMenu;

    /**
     * Frame with gl canvas.
     */
    private View3dGLFrame ogl;


    /**
     * Will be invoked by JOSM to bootstrap the plugin.
     *
     * @param pInfo
     *            information about the plugin and its local installation
     */
    public Kendzi3DPlugin(PluginInformation pInfo) {
        super(pInfo);

        // requre to set up before library are loaded
        FileUrlReciverService.initFileReciver(getPluginDir());

        try {
            loadLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // where is spring ?!
        // init rest of services
        TextureCacheService.initTextureCache(getPluginDir());
        MetadataCacheService.initMetadataCache(getPluginDir());

        TextureCacheService.addTextureBuilder(new ColorTextureBuilder());

        refreshMenu();

        openJOGLWindow();
    }

    @Override
    public void copy(String from, String to) throws FileNotFoundException, IOException {
        try {
            System.out.println("copying file from jar: " + from + " to: " + to);
            makeDirs(to);

            URL url = getClass().getResource(from);
            System.out.println("url to res: " + (url == null ? null : url.toString()));

            String pluginDirName = getPluginDir();
            File pluginDir = new File(pluginDirName);
            if (!pluginDir.exists()) {
                pluginDir.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(new File(pluginDirName, to));
            InputStream in = getResourceAsStream(from);
            //            InputStream in = getClass().getResourceAsStream(from);
            byte[] buffer = new byte[8192];
            long l = 0;
            for (int len = in.read(buffer); len > 0; len = in.read(buffer)) {
                out.write(buffer, 0, len);
                l = l + len;
            }
            in.close();
            out.close();
            System.out.println("end of copying bytes: " + l + " from file: " + from);


            System.out.println(this.getClass().getResource(""));
            System.out.println(this.getClass().getResource("/"));

        } catch (java.lang.NullPointerException e) {
            // for debbuging and testing I don't care.
            e.printStackTrace();

        } catch (Exception e) {
            // for debbuging and testing I don't care.
            e.printStackTrace();
        }
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
    URL getResourceUrl(String pResName) {
        return FileUrlReciverService.getResourceUrl(pResName);
    }

    /**
     * Returns an input stream for reading the specified resource.
     *
     * <p> The search order is described in the documentation for {@link
     * #getResource(String)}.  </p>
     *
     * @param  pName
     *         The resource name
     *
     * @return  An input stream for reading the resource, or <tt>null</tt>
     *          if the resource could not be found
     *
     * @since  1.1
     */
    public InputStream getResourceAsStream(String pName) {
        URL url = getResourceUrl(pName);
        try {
            return url != null ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * Make all sub directorys.
     * @param pFileName file which require directory
     */
    void makeDirs(String pFileName) {

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
     * Check if file exist in plugin directory.
     * @param pFileName file path
     * @return if file exist
     */
    public boolean isFileExis(String pFileName) {
        String pluginDirName = getPluginDir();
        File pluginDir = new File(pluginDirName);
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
        }
        return (new File(pluginDirName, pFileName)).exists();
    }


    /**
     * Plugin is loaded dynamically so we need to load required jars and native library.
     *
     * @throws IOException ups
     * @throws InvocationTargetException ups
     * @throws IllegalAccessException ups
     * @throws NoSuchMethodException ups
     */
    private void loadLibrary() throws IOException, InvocationTargetException, NoSuchMethodException,
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

    /**
     * Refreshing menu.
     */
    public void refreshMenu() {
        MainMenu menu = Main.main.menu;

        System.err.println("3d test");
        if (this.view3dJMenu == null) {
            this.view3dJMenu = menu.addMenu("3D", KeyEvent.VK_W, menu.defaultMenuPos,
                    ht("/Plugin/WMS"));
        } else {
            this.view3dJMenu.removeAll();
        }

        // // for each configured WMSInfo, add a menu entry.
        // for (final WMSInfo u : info.layers) {
        // wmsJMenu.add(new JMenuItem(new WMSDownloadAction(u)));
        // }

        this.view3dJMenu.addSeparator();
        //		wmsJMenu.add(new JMenuItem(new OpenViewAction()));
        this.view3dJMenu.add(new JMenuItem(new JosmAction(tr("Kendzi 3D View"), "stock_3d-effects24", tr("Open 3D View"), null, false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                //				super(tr("3D View"), "stock_3d-effects24", tr("Open 3D View"), null, false);
                putValue("toolbar", "3dView_run");

                openJOGLWindow();

            }

        }));

        this.view3dJMenu.add(
                new JMenuItem(
                        new JosmAction(tr("Test imb"), "stock_3d-effects24", tr("Test imb"), null, false) {

            @Override
            public void actionPerformed(ActionEvent e) {
               TestRener t = new TestRener();
               try {
                t.test();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            }

        }));

        this.view3dJMenu.add(new JMenuItem(
                new JosmAction(tr("Move camera"), "1306318146_build__24", tr("Move camera"), null, false) {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (Kendzi3DPlugin.this.ogl != null) {
                    // XXX add event, and rethink
                    double x = Kendzi3DPlugin.this.ogl.getCanvasListener().getRenderJosm().getPerspective().calcX(Main.map.mapView.getCenter().getX());
                    double y = Kendzi3DPlugin.this.ogl.getCanvasListener().getRenderJosm().getPerspective().calcY(Main.map.mapView.getCenter().getY());

                    Kendzi3DPlugin.this.ogl.getCanvasListener().setCamPos(x, y);
                }
            }

        }));

        this.view3dJMenu.add(new JMenuItem(
                new JosmAction(
                        tr("Rebuild models, textures and wold offset"),
                        "1306318208_rebuild__24",
                        tr("Rebuild Models And Wold Offset"),
                        null,
                        false) {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (Kendzi3DPlugin.this.ogl != null) {
                    // XXX add event
                    TextureCacheService.clearTextures();

                    Kendzi3DPlugin.this.ogl.getCanvasListener().getRenderJosm().processDatasetEvent(null);

                    Kendzi3DPlugin.this.ogl.getCanvasListener().setCamPos(0, 0);
                }
            }

        }));


        // -- Ground Toggle Action
        GroundToggleAction groundToggleAction = new GroundToggleAction(this);
        if (groundToggleAction.canDebug()) {
            final JCheckBoxMenuItem debug = new JCheckBoxMenuItem(groundToggleAction);
            this.view3dJMenu.addSeparator();
            this.view3dJMenu.add(debug);
            debug.setAccelerator(groundToggleAction.getShortcut().getKeyStroke());
            groundToggleAction.addButtonModel(debug.getModel());
        }

        // -- debug toggle action
        DebugToggleAction debugToggleAction = new DebugToggleAction();
        if (debugToggleAction.canDebug()) {
            final JCheckBoxMenuItem debug = new JCheckBoxMenuItem(debugToggleAction);
            this.view3dJMenu.addSeparator();
            this.view3dJMenu.add(debug);
            debug.setAccelerator(debugToggleAction.getShortcut().getKeyStroke());
            debugToggleAction.addButtonModel(debug.getModel());
        }

        // -- debug toggle action
        TextureFilterToggleAction filterToggleAction = new TextureFilterToggleAction();
        if (filterToggleAction.canDebug()) {
            final JCheckBoxMenuItem debug = new JCheckBoxMenuItem(filterToggleAction);
            this.view3dJMenu.addSeparator();
            this.view3dJMenu.add(debug);
            debug.setAccelerator(filterToggleAction.getShortcut().getKeyStroke());
            filterToggleAction.addButtonModel(debug.getModel());
        }



        this.view3dJMenu.addSeparator();
        // wmsJMenu.add(new JMenuItem(new JosmAction(tr("Blank Layer"),
        // "blankmenu",
        // tr("Open a blank WMS layer to load data from a file"), null,
        // false) {
        // public void actionPerformed(ActionEvent ev) {
        // Main.main.addLayer(new WMSLayer());
        // }
        // }));
        setEnabledAll(true);
    }

    public Kendzi3dGLEventListener getCanvasListener() {
        if (this.ogl != null) {
            return this.ogl.getCanvasListener();
        }
        return null;
    }

    private void setEnabledAll(boolean isEnabled) {
        for (int i = 0; i < this.view3dJMenu.getItemCount(); i++) {
            JMenuItem item = this.view3dJMenu.getItem(i);

            if (item != null) {
                item.setEnabled(isEnabled);
            }
        }
    }

    private void openJOGLWindow() {
        if (this.ogl == null || !this.ogl.isDisplayable()) {

            this.ogl = new View3dGLFrame();

            this.ogl.setVisible(true);
        }
//        else {
//            this.ogl.resume();
//            this.ogl.setVisible(true);
//        }
    }
}
