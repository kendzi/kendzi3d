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
import java.net.URL;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import kendzi.josm.kendzi3d.action.AutostartToggleAction;
import kendzi.josm.kendzi3d.action.CleanUpAction;
import kendzi.josm.kendzi3d.action.DebugToggleAction;
import kendzi.josm.kendzi3d.action.GroundToggleAction;
import kendzi.josm.kendzi3d.action.MoveCameraAction;
import kendzi.josm.kendzi3d.action.TextureFilterToggleAction;
import kendzi.josm.kendzi3d.action.WikiTextureLoaderAction;
import kendzi.josm.kendzi3d.context.ApplicationContextFactory;
import kendzi.josm.kendzi3d.module.Kendzi3dModule;
import kendzi.josm.kendzi3d.service.impl.FileUrlReciverService;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLFrame;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.PluginInformation;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Kendzi3DPlugin extends NativeLibPlugin {



    /**
     * Menu in JOSM.
     */
    JMenu view3dJMenu;

    /**
     * Frame with gl canvas.
     */
    private Kendzi3dGLFrame ogl;


    /**
     * Will be invoked by JOSM to bootstrap the plugin.
     *
     * @param pInfo
     *            information about the plugin and its local installation
     */
    public Kendzi3DPlugin(PluginInformation pInfo) {
        super(pInfo);



//        // requre to set up before library are loaded
//        FileUrlReciverService.initFileReciver(getPluginDir());

        try {
            loadLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Injector injector = Guice.createInjector(new Kendzi3dModule(getPluginDir()));

        ApplicationContextFactory.initContext(getPluginDir());



        // where is spring ?!
        // init rest of services
//        TextureCacheService.initTextureCache(getPluginDir());
//        MetadataCacheService.initMetadataCache(getPluginDir());
//        WikiTextureLoaderService.init(getPluginDir());

//        TextureCacheService.addTextureBuilder(new ColorTextureBuilder());

        refreshMenu(injector);
        // System.out.println("****************************************************************");
        // System.out.println(Main.pref.get("kendzi3d.autostart1"));
        // System.out.println(Main.pref.get("kendzi3d.autostart"));

        if (!Boolean.FALSE.equals(
                Main.pref.getBoolean(
                        AutostartToggleAction.KENDZI_3D_AUTOSTART, true))) {
//            Main.pref.put("kendzi3d.autostart", "true");
//            Main.pref.put("kendzi3d.autostart", "false");
            openJOGLWindow(injector);
        }
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
     * Refreshing menu.
     * @param injector
     */
    public void refreshMenu(final Injector injector) {
        MainMenu menu = Main.main.menu;

        System.err.println("3d test");
        if (this.view3dJMenu == null) {
            this.view3dJMenu = menu.addMenu("3D", KeyEvent.VK_W, menu.defaultMenuPos,
                    ht("/Plugin/WMS"));
        } else {
            this.view3dJMenu.removeAll();
        }


        this.view3dJMenu.addSeparator();
        //		wmsJMenu.add(new JMenuItem(new OpenViewAction()));
        this.view3dJMenu.add(new JMenuItem(new JosmAction(tr("Kendzi 3D View"), "stock_3d-effects24", tr("Open 3D View"), null, false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                //				super(tr("3D View"), "stock_3d-effects24", tr("Open 3D View"), null, false);
                putValue("toolbar", "3dView_run");

                openJOGLWindow(injector);

            }

        }));


        AutostartToggleAction autostartToggleAction = new AutostartToggleAction();
        if (autostartToggleAction.canDebug()) {
            final JCheckBoxMenuItem action = new JCheckBoxMenuItem(autostartToggleAction);

            this.view3dJMenu.add(action);
            action.setAccelerator(autostartToggleAction.getShortcut().getKeyStroke());
            autostartToggleAction.addButtonModel(action.getModel());
        }

        MoveCameraAction moveCameraAction = injector.getInstance(MoveCameraAction.class);
        final JMenuItem moveCameraItem = new JMenuItem(moveCameraAction);
        this.view3dJMenu.add(moveCameraItem);
        moveCameraItem.setAccelerator(moveCameraAction.getShortcut().getKeyStroke());


        CleanUpAction cleanUpAction = injector.getInstance(CleanUpAction.class);
        final JMenuItem cleanUpItem = new JMenuItem(cleanUpAction);
        this.view3dJMenu.add(cleanUpItem);
        cleanUpItem.setAccelerator(cleanUpAction.getShortcut().getKeyStroke());


        // -- Ground Toggle Action
        GroundToggleAction groundToggleAction = injector.getInstance(GroundToggleAction.class);
        if (groundToggleAction.canDebug()) {
            final JCheckBoxMenuItem debug = new JCheckBoxMenuItem(groundToggleAction);
            this.view3dJMenu.addSeparator();
            this.view3dJMenu.add(debug);
            debug.setAccelerator(groundToggleAction.getShortcut().getKeyStroke());
            groundToggleAction.addButtonModel(debug.getModel());
        }


        // -- debug toggle action
        DebugToggleAction debugToggleAction = injector.getInstance(DebugToggleAction.class);
        if (debugToggleAction.canDebug()) {
            final JCheckBoxMenuItem debug = new JCheckBoxMenuItem(debugToggleAction);
            this.view3dJMenu.addSeparator();
            this.view3dJMenu.add(debug);
            debug.setAccelerator(debugToggleAction.getShortcut().getKeyStroke());
            debugToggleAction.addButtonModel(debug.getModel());
        }


        // -- debug toggle action
        TextureFilterToggleAction filterToggleAction = injector.getInstance(TextureFilterToggleAction.class);
        if (filterToggleAction.canDebug()) {
            final JCheckBoxMenuItem debug = new JCheckBoxMenuItem(filterToggleAction);
            this.view3dJMenu.addSeparator();
            this.view3dJMenu.add(debug);
            debug.setAccelerator(filterToggleAction.getShortcut().getKeyStroke());
            filterToggleAction.addButtonModel(debug.getModel());
        }

        this.view3dJMenu.addSeparator();

        // -- debug toggle action
        WikiTextureLoaderAction wikiTextureLoaderAction = injector.getInstance(WikiTextureLoaderAction.class);
        final JMenuItem wikiTextureLoaderItem = new JMenuItem(wikiTextureLoaderAction);
        this.view3dJMenu.add(wikiTextureLoaderItem);
        wikiTextureLoaderItem.setAccelerator(wikiTextureLoaderAction.getShortcut().getKeyStroke());

        this.view3dJMenu.addSeparator();

        setEnabledAll(true);
    }

    private void setEnabledAll(boolean isEnabled) {
        for (int i = 0; i < this.view3dJMenu.getItemCount(); i++) {
            JMenuItem item = this.view3dJMenu.getItem(i);

            if (item != null) {
                item.setEnabled(isEnabled);
            }
        }
    }




    private void openJOGLWindow(Injector injector) {
        if (this.ogl == null || !this.ogl.isDisplayable()) {

            Kendzi3dGLFrame frame = injector.getInstance(Kendzi3dGLFrame.class);

//            Kendzi3dGLFrame frame = new Kendzi3dGLFrame();
////            frame.setCanvasListener(ApplicationContextUtil.getKendzi3dGLEventListener());
//            frame.setCanvasListener(injector.getInstance(Kendzi3dGLEventListener.class));

            frame.initUI();

            frame.setVisible(true);

            this.ogl = frame;
        }
//        else {
//            this.ogl.resume();
//            this.ogl.setVisible(true);
//        }
    }


}
