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
import kendzi.josm.kendzi3d.action.DebugToggleAction;
import kendzi.josm.kendzi3d.action.GroundToggleAction;
import kendzi.josm.kendzi3d.action.TextureFilterToggleAction;
import kendzi.josm.kendzi3d.action.WikiTextureLoaderAction;
import kendzi.josm.kendzi3d.context.ApplicationContextFactory;
import kendzi.josm.kendzi3d.context.ApplicationContextUtil;
import kendzi.josm.kendzi3d.module.Kendzi3dModule;
import kendzi.josm.kendzi3d.service.TextureCacheService;
import kendzi.josm.kendzi3d.service.impl.FileUrlReciverService;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLEventListener;
import kendzi.josm.kendzi3d.ui.View3dGLFrame;

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
    private View3dGLFrame ogl;


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

//        this.view3dJMenu.add(
//                new JMenuItem(
//                        new JosmAction(tr("Test imb"), "stock_3d-effects24", tr("Test imb"), null, false) {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//               TestRener t = new TestRener();
//               try {
//                t.test();
//            } catch (IOException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//
//            }
//
//        }));

        this.view3dJMenu.add(new JMenuItem(
                new JosmAction(tr("Move camera"), "1306318146_build__24", tr("Move camera"), null, false) {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (Kendzi3DPlugin.this.ogl != null) {
                    // XXX add event, and rethink
                    double x = ApplicationContextUtil.getRenderJosm().getPerspective().calcX(Main.map.mapView.getCenter().getX());
                    double y = ApplicationContextUtil.getRenderJosm().getPerspective().calcY(Main.map.mapView.getCenter().getY());

                    ApplicationContextUtil.getKendzi3dGLEventListener().setCamPos(x, y);
                }
            }

        }));

        this.view3dJMenu.add(new JMenuItem(
                new JosmAction(
                        tr("Clean up"),
                        "1306318208_rebuild__24",
                        tr("Rebuild models, textures and wold offset"),
                        null,
                        false) {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (Kendzi3DPlugin.this.ogl != null) {
                    // XXX add event
                    ApplicationContextUtil.getTextureCacheService().clearTextures();

                    ApplicationContextUtil.getRenderJosm().processDatasetEvent(null);
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
        debugToggleAction.setModelRender(ApplicationContextUtil.getModelRender());
        if (debugToggleAction.canDebug()) {
            final JCheckBoxMenuItem debug = new JCheckBoxMenuItem(debugToggleAction);
            this.view3dJMenu.addSeparator();
            this.view3dJMenu.add(debug);
            debug.setAccelerator(debugToggleAction.getShortcut().getKeyStroke());
            debugToggleAction.addButtonModel(debug.getModel());
        }

        System.out.println(injector.getInstance(TextureCacheService.class));

        // -- debug toggle action
        TextureFilterToggleAction filterToggleAction = injector.getInstance(TextureFilterToggleAction.class);
//        TextureFilterToggleAction filterToggleAction = new TextureFilterToggleAction();
        if (filterToggleAction.canDebug()) {
            final JCheckBoxMenuItem debug = new JCheckBoxMenuItem(filterToggleAction);
            this.view3dJMenu.addSeparator();
            this.view3dJMenu.add(debug);
            debug.setAccelerator(filterToggleAction.getShortcut().getKeyStroke());
            filterToggleAction.addButtonModel(debug.getModel());
        }

        // -- debug toggle action
        WikiTextureLoaderAction wikiTextureLoaderAction = new WikiTextureLoaderAction();
        if (wikiTextureLoaderAction.canDebug()) {
            final JMenuItem debug = new JMenuItem(wikiTextureLoaderAction);
            this.view3dJMenu.addSeparator();
            this.view3dJMenu.add(debug);
            debug.setAccelerator(wikiTextureLoaderAction.getShortcut().getKeyStroke());
            wikiTextureLoaderAction.addButtonModel(debug.getModel());
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




    private void openJOGLWindow(Injector injector) {
        if (this.ogl == null || !this.ogl.isDisplayable()) {


            View3dGLFrame frame = new View3dGLFrame();
//            frame.setCanvasListener(ApplicationContextUtil.getKendzi3dGLEventListener());
            frame.setCanvasListener(injector.getInstance(Kendzi3dGLEventListener.class));

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
