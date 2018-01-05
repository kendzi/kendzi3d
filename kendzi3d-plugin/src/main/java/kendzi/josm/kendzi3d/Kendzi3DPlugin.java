/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.actions.ToggleAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.ImageProvider;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kendzi.josm.jogl.JoglPlugin;
import kendzi.josm.kendzi3d.action.AutostartToggleAction;
import kendzi.josm.kendzi3d.action.CleanUpAction;
import kendzi.josm.kendzi3d.action.DebugPointModelToggleAction;
import kendzi.josm.kendzi3d.action.DebugToggleAction;
import kendzi.josm.kendzi3d.action.ExportAction;
import kendzi.josm.kendzi3d.action.ForceTwoSidedToggleAction;
import kendzi.josm.kendzi3d.action.GroundToggleAction;
import kendzi.josm.kendzi3d.action.LightConfigurationAction;
import kendzi.josm.kendzi3d.action.LoadTextureLibraryAction;
import kendzi.josm.kendzi3d.action.MoveCameraAction;
import kendzi.josm.kendzi3d.action.PointModelListAction;
import kendzi.josm.kendzi3d.action.ShowPluginDirAction;
import kendzi.josm.kendzi3d.action.TextureFilterToggleAction;
import kendzi.josm.kendzi3d.action.TextureToggleAction;
import kendzi.josm.kendzi3d.action.WikiTextureLoaderAction;
import kendzi.josm.kendzi3d.data.producer.EditorObjectsProducer;
import kendzi.josm.kendzi3d.module.Kendzi3dModule;
import kendzi.josm.kendzi3d.ui.Kendzi3dGlFrame;
import kendzi.josm.kendzi3d.ui.layer.CameraLayer;

public class Kendzi3DPlugin extends NativeLibPlugin {

    /**
     * Menu in JOSM.
     */
    JMenu view3dJMenu;

    /**
     * Frame with gl canvas.
     */
    private Kendzi3dGlFrame singleWindow;

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
            openKendzi3dWindow(injector);
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
        MainMenu menu = MainApplication.getMenu();

        System.err.println("3d test");
        if (view3dJMenu == null) {
            view3dJMenu = menu.addMenu("3D", "3D", KeyEvent.VK_D, menu.getDefaultMenuPos(), ht("/Plugin/WMS"));
        } else {
            view3dJMenu.removeAll();
        }

        view3dJMenu.addSeparator();

        view3dJMenu
        .add(new JMenuItem(new JosmAction(tr("Kendzi 3D View"), "stock_3d-effects24", tr("Open 3D View"), null, false) {

            @Override
            public void actionPerformed(ActionEvent e) {
                putValue("toolbar", "3dView_run");

                openKendzi3dWindow(injector);

            }

        }));

        AutostartToggleAction autostartToggleAction = new AutostartToggleAction();
        registerCheckBoxAction(autostartToggleAction, view3dJMenu);

        MoveCameraAction moveCameraAction = injector.getInstance(MoveCameraAction.class);
        registerAction(moveCameraAction, view3dJMenu);

        CleanUpAction cleanUpAction = injector.getInstance(CleanUpAction.class);
        registerAction(cleanUpAction, view3dJMenu);

        PointModelListAction pointModelListAction = injector.getInstance(PointModelListAction.class);
        registerAction(pointModelListAction, view3dJMenu);

        LightConfigurationAction lightConfigurationAction = injector.getInstance(LightConfigurationAction.class);
        registerAction(lightConfigurationAction, view3dJMenu);

        view3dJMenu.addSeparator();

        TextureToggleAction textureToggleAction = injector.getInstance(TextureToggleAction.class);
        registerCheckBoxAction(textureToggleAction, view3dJMenu);

        GroundToggleAction groundToggleAction = injector.getInstance(GroundToggleAction.class);
        registerCheckBoxAction(groundToggleAction, view3dJMenu);

        view3dJMenu.addSeparator();

        DebugToggleAction debugToggleAction = injector.getInstance(DebugToggleAction.class);
        registerCheckBoxAction(debugToggleAction, view3dJMenu);

        DebugPointModelToggleAction debugPointModelToggleAction = injector.getInstance(DebugPointModelToggleAction.class);
        registerCheckBoxAction(debugPointModelToggleAction, view3dJMenu);

        view3dJMenu.addSeparator();

        /* only serves to override a per-model setting, i.e. enables two sided lighting for each model globally */
        ForceTwoSidedToggleAction doubleSidedLightingToggleAction = injector.getInstance(ForceTwoSidedToggleAction.class);
        registerCheckBoxAction(doubleSidedLightingToggleAction, view3dJMenu);

        TextureFilterToggleAction filterToggleAction = injector.getInstance(TextureFilterToggleAction.class);
        registerCheckBoxAction(filterToggleAction, view3dJMenu);

        view3dJMenu.addSeparator();

        WikiTextureLoaderAction wikiTextureLoaderAction = injector.getInstance(WikiTextureLoaderAction.class);
        registerAction(wikiTextureLoaderAction, view3dJMenu);

        LoadTextureLibraryAction loadTextureLibraryAction = injector.getInstance(LoadTextureLibraryAction.class);
        registerAction(loadTextureLibraryAction, view3dJMenu);

        view3dJMenu.addSeparator();

        JMenu advanceMenu = new JMenu(tr("Advance"));
        view3dJMenu.add(advanceMenu);

        ExportAction exportAction = injector.getInstance(ExportAction.class);
        registerAction(exportAction, advanceMenu);

        ShowPluginDirAction showPluginDirAction = injector.getInstance(ShowPluginDirAction.class);
        registerAction(showPluginDirAction, advanceMenu);

        setEnabledAll(true);
    }

    private void registerAction(JosmAction josmAction, JMenu menu) {
        final JMenuItem menuItem = new JMenuItem(josmAction);
        menuItem.setAccelerator(josmAction.getShortcut().getKeyStroke());
        menu.add(menuItem);
    }

    private void registerCheckBoxAction(ToggleAction action, JMenu menu) {
        final JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(action);
        checkBox.setAccelerator(action.getShortcut().getKeyStroke());

        action.addButtonModel(checkBox.getModel());
        action.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("enabled")) {
                    checkBox.setVisible((Boolean) evt.getNewValue());
                }

            }
        });

        menu.add(checkBox);
    }

    private void setEnabledAll(boolean isEnabled) {
        for (int i = 0; i < view3dJMenu.getItemCount(); i++) {
            JMenuItem item = view3dJMenu.getItem(i);

            if (item != null) {
                item.setEnabled(isEnabled);
            }
        }
    }

    private void openKendzi3dWindow(Injector injector) {
        try {
            if (singleWindow == null || !singleWindow.isDisplayable()) {
                // XXX to create instance before frame
                injector.getInstance(EditorObjectsProducer.class);

                Kendzi3dGlFrame frame = injector.getInstance(Kendzi3dGlFrame.class);

                frame.initUi();

                ImageIcon img = ImageProvider.get("stock_3d-effects24");
                if (img != null) {
                    frame.setIconImage(img.getImage());
                }

                frame.setVisible(true);

                singleWindow = frame;

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
