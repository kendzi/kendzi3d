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
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonModel;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;

/**
 * Autostart toggle action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class AutostartToggleAction extends JosmAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Autostart property key.
     */
    public final static String KENDZI_3D_AUTOSTART = "kendzi3d.autostart";

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
    public AutostartToggleAction() {
        super(
                tr("Plugin autostart"),
                "1323594394_apply-icon24.png",
                tr("Enable/disable autostart"),
//                Shortcut.registerShortcut("menu:view:wireframe", tr("Toggle Wireframe view"),KeyEvent.VK_W, Shortcut.GROUP_MENU),
                null,
                true /* register shortcut */
        );

        this.selected = Main.pref.getBoolean(KENDZI_3D_AUTOSTART, true);

        notifySelectedState();

        //setAutostart(this.selected);
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
        this.selected = !this.selected;
        Main.pref.put(KENDZI_3D_AUTOSTART, this.selected);
        notifySelectedState();

        setAutostart(this.selected);
    }

    /**
     * @param pEnable enable filter
     */
    private void setAutostart(boolean pEnable) {
        //
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
