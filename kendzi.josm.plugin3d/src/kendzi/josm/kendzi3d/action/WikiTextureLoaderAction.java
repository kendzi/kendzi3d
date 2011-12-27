/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */


package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonModel;
import javax.swing.JOptionPane;

import kendzi.josm.kendzi3d.service.WikiTextureLoaderService;
import kendzi.josm.kendzi3d.service.WikiTextureLoaderService.LoadRet;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.actions.JosmAction;

/**
 * Texture filter toggle action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class WikiTextureLoaderAction extends JosmAction {

    /** Log. */
    private static final Logger log = Logger.getLogger(WikiTextureLoaderAction.class);

    /**
     * Button models.
     */
    private final List<ButtonModel> buttonModels = new ArrayList<ButtonModel>();
    //FIXME: replace with property Action.SELECTED_KEY when migrating to
    // Java 6
    private boolean selected;

    /**
     * Constructor of debug toggle action.
     */
    public WikiTextureLoaderAction() {
        super(
                tr("Load textures from wiki"),
                "1323558253_wikipedia-icon_24",
                tr("Load textures from wiki"),
//                Shortcut.registerShortcut("menu:view:wireframe", tr("Toggle Wireframe view"),KeyEvent.VK_W, Shortcut.GROUP_MENU),
                null,
                true /* register shortcut */
        );
        this.selected = true;
//            Main.pref.getBoolean("draw.wireframe", false);
        notifySelectedState();
    }

    /**
     * @param pModel button model
     */
    public void addButtonModel(ButtonModel pModel) {
        if (pModel != null && !this.buttonModels.contains(pModel)) {
            this.buttonModels.add(pModel);
            pModel.setSelected(this.selected);
        }
    }

    /**
     * @param pModel button model
     */
    public void removeButtonModel(ButtonModel pModel) {
        if (pModel != null && this.buttonModels.contains(pModel)) {
            this.buttonModels.remove(pModel);
        }
    }

    /**
     *
     */
    protected void notifySelectedState() {
        for (ButtonModel model : this.buttonModels) {
            if (model.isSelected() != this.selected) {
                model.setSelected(this.selected);
            }
        }
    }

    /**
     *
     */
    protected void toggleSelectedState() {



//        TextureCacheService.clearTextures();


        this.selected = !this.selected;
//        Main.pref.put("draw.wireframe", this.selected);
        notifySelectedState();


        loadFromWiki();
    }

    /**
     *
     */
    public void loadFromWiki() {
        List<String> errors = null;
        String timestamp = null;
        try {
            LoadRet load = WikiTextureLoaderService.getInstance().load();
            errors = load.getErrors();
            timestamp = load.getTimestamp();

        } catch (MalformedURLException e) {
            log.error(e);
            showError(e);
        } catch (IOException e) {
            log.error(e);
            showError(e);
        }

        if (errors != null && !errors.isEmpty()) {

            StringBuffer sb = new StringBuffer();
            for (String err: errors ) {
                sb.append(err);
                sb.append("\n");
            }

            JOptionPane.showMessageDialog(null,
                    tr("Error downloding textures from urls: ") + "\n" + sb,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    tr("Downloded textures from wiki timestamp: " + timestamp + " to path: ") + "\n"
                            + WikiTextureLoaderService.getInstance().getTexturesPath() ,
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showError(Exception e) {
      //custom title, error icon
        JOptionPane.showMessageDialog(null,
            "Error downloding textures from wiki: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent pE) {
        toggleSelectedState();
    }

    @Override
    protected void updateEnabledState() {
//        setEnabled(Main.map != null && Main.main.getEditLayer() != null);
    }

    /** Is selected.
     * @return selected
     */
    public boolean isSelected() {
        return this.selected;
    }

    /** If can be in debug mode.
     * @return debug mode
     */
    public boolean canDebug() {
        return true;
    }
}
