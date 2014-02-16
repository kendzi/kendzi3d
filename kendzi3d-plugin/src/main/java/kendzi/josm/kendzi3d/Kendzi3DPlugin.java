/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d;

import static org.openstreetmap.josm.gui.help.HelpUtil.*;
import static org.openstreetmap.josm.tools.I18n.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import kendzi.josm.jogl.JoglPlugin;
import kendzi.josm.kendzi3d.action.AutostartToggleAction;
import kendzi.josm.kendzi3d.action.CleanUpAction;
import kendzi.josm.kendzi3d.action.DebugPointModelToggleAction;
import kendzi.josm.kendzi3d.action.DebugToggleAction;
import kendzi.josm.kendzi3d.action.ExportAction;
import kendzi.josm.kendzi3d.action.GroundToggleAction;
import kendzi.josm.kendzi3d.action.LoadTextureLibraryAction;
import kendzi.josm.kendzi3d.action.MoveCameraAction;
import kendzi.josm.kendzi3d.action.PointModelListAction;
import kendzi.josm.kendzi3d.action.ShowPluginDirAction;
import kendzi.josm.kendzi3d.action.TextureFilterToggleAction;
import kendzi.josm.kendzi3d.action.WikiTextureLoaderAction;
import kendzi.josm.kendzi3d.module.Kendzi3dModule;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLFrame;
import kendzi.josm.kendzi3d.ui.layer.CameraLayer;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.actions.ToggleAction;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.ImageProvider;

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

        ExceptionHandler.registerExceptionHandler();

        try {

            JoglPlugin.addJoglToClassPath();

            loadLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Injector injector = Guice.createInjector(new Kendzi3dModule(getPluginDir()));

        refreshMenu(injector);

        if (!Boolean.FALSE.equals(Main.pref.getBoolean(AutostartToggleAction.KENDZI_3D_AUTOSTART, false))) {
            openJOGLWindow(injector);
        }
    }

    /**
     * Check if file exist in plugin directory.
     * 
     * @param pFileName
     *            file path
     * @return if file exist
     */
    public boolean isFileExis(String pFileName) {
        String pluginDirName = getPluginDir();
        File pluginDir = new File(pluginDirName);
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
        }
        return new File(pluginDirName, pFileName).exists();
    }

    /**
     * Refreshing menu.
     * 
     * @param injector
     */
    public void refreshMenu(final Injector injector) {
        MainMenu menu = Main.main.menu;

        System.err.println("3d test");
        if (this.view3dJMenu == null) {
            this.view3dJMenu = menu.addMenu("3D", KeyEvent.VK_D, menu.getDefaultMenuPos(), ht("/Plugin/WMS"));
        } else {
            this.view3dJMenu.removeAll();
        }

        this.view3dJMenu.addSeparator();
        // wmsJMenu.add(new JMenuItem(new OpenViewAction()));
        this.view3dJMenu.add(new JMenuItem(new JosmAction(tr("Kendzi 3D View"), "stock_3d-effects24", tr("Open 3D View"), null,
                false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                putValue("toolbar", "3dView_run");

                openJOGLWindow(injector);

            }

        }));

        AutostartToggleAction autostartToggleAction = new AutostartToggleAction();
        registerCheckBoxAction(autostartToggleAction, view3dJMenu);

        MoveCameraAction moveCameraAction = injector.getInstance(MoveCameraAction.class);
        final JMenuItem moveCameraItem = new JMenuItem(moveCameraAction);
        this.view3dJMenu.add(moveCameraItem);
        moveCameraItem.setAccelerator(moveCameraAction.getShortcut().getKeyStroke());

        CleanUpAction cleanUpAction = injector.getInstance(CleanUpAction.class);
        final JMenuItem cleanUpItem = new JMenuItem(cleanUpAction);
        this.view3dJMenu.add(cleanUpItem);
        cleanUpItem.setAccelerator(cleanUpAction.getShortcut().getKeyStroke());

        PointModelListAction pointModelListAction = injector.getInstance(PointModelListAction.class);
        final JMenuItem pointModelListItem = new JMenuItem(pointModelListAction);
        this.view3dJMenu.add(pointModelListItem);
        pointModelListItem.setAccelerator(pointModelListAction.getShortcut().getKeyStroke());

        this.view3dJMenu.addSeparator();

        // -- Ground Toggle Action
        GroundToggleAction groundToggleAction = injector.getInstance(GroundToggleAction.class);
        registerCheckBoxAction(groundToggleAction, view3dJMenu);

        this.view3dJMenu.addSeparator();

        // -- debug toggle action
        DebugToggleAction debugToggleAction = injector.getInstance(DebugToggleAction.class);
        registerCheckBoxAction(debugToggleAction, view3dJMenu);

        DebugPointModelToggleAction debugPointModelToggleAction = injector.getInstance(DebugPointModelToggleAction.class);
        registerCheckBoxAction(debugPointModelToggleAction, view3dJMenu);

        this.view3dJMenu.addSeparator();

        // -- debug toggle action
        TextureFilterToggleAction filterToggleAction = injector.getInstance(TextureFilterToggleAction.class);
        registerCheckBoxAction(filterToggleAction, view3dJMenu);

        this.view3dJMenu.addSeparator();

        // -- debug toggle action
        WikiTextureLoaderAction wikiTextureLoaderAction = injector.getInstance(WikiTextureLoaderAction.class);
        final JMenuItem wikiTextureLoaderItem = new JMenuItem(wikiTextureLoaderAction);
        this.view3dJMenu.add(wikiTextureLoaderItem);
        wikiTextureLoaderItem.setAccelerator(wikiTextureLoaderAction.getShortcut().getKeyStroke());

        LoadTextureLibraryAction loadTextureLibraryAction = injector.getInstance(LoadTextureLibraryAction.class);
        final JMenuItem loadTextureLibraryItem = new JMenuItem(loadTextureLibraryAction);
        this.view3dJMenu.add(loadTextureLibraryItem);
        loadTextureLibraryItem.setAccelerator(loadTextureLibraryAction.getShortcut().getKeyStroke());

        this.view3dJMenu.addSeparator();

        JMenu advMenu = new JMenu(tr("Adv"));
        this.view3dJMenu.add(advMenu);

        ExportAction exportAction = injector.getInstance(ExportAction.class);
        final JMenuItem exportActionItem = new JMenuItem(exportAction);
        advMenu.add(exportActionItem);
        exportActionItem.setAccelerator(loadTextureLibraryAction.getShortcut().getKeyStroke());

        ShowPluginDirAction showPluginDirAction = injector.getInstance(ShowPluginDirAction.class);
        final JMenuItem showPluginDirActionItem = new JMenuItem(showPluginDirAction);
        advMenu.add(showPluginDirActionItem);
        showPluginDirActionItem.setAccelerator(loadTextureLibraryAction.getShortcut().getKeyStroke());

        setEnabledAll(true);
    }

    private void registerCheckBoxAction(ToggleAction action, JMenu menu) {
        final JCheckBoxMenuItem debugPointModel = new JCheckBoxMenuItem(action);
        menu.add(debugPointModel);
        debugPointModel.setAccelerator(action.getShortcut().getKeyStroke());
        action.addButtonModel(debugPointModel.getModel());
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
        try {
            if (this.ogl == null || !this.ogl.isDisplayable()) {

                Kendzi3dGLFrame frame = injector.getInstance(Kendzi3dGLFrame.class);

                frame.initUI();

                ImageIcon img = ImageProvider.get("stock_3d-effects24");
                if (img != null) {
                    frame.setIconImage(img.getImage());
                }

                frame.setVisible(true);

                this.ogl = frame;

                CameraLayer oglListener = injector.getInstance(CameraLayer.class);
                initializeKendzi3dLayer(oglListener);
            }

        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            throw e;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error opening kendzi3d window", e);
        }
    }

    /** The preferences prefix */
    public static final String PREFIX = "kendzi3d";

    /** The preferences key for error layer */
    public static final String PREF_LAYER = PREFIX + ".layer";

    public void initializeKendzi3dLayer(CameraLayer cameraLayer) {
        if (!Main.pref.getBoolean(PREF_LAYER, true)) {
            return;
        }

        cameraLayer.addCameraLayer();
    }

}
