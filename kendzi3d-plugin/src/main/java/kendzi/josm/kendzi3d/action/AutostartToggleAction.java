/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.actions.ToggleAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.spi.preferences.Config;

/**
 * Autostart toggle action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class AutostartToggleAction extends ToggleAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Autostart property key.
     */
    public final static String KENDZI_3D_AUTOSTART = "kendzi3d.autostart";

    /**
     * Constructor of debug toggle action.
     */
    public AutostartToggleAction() {
        super(tr("Plugin autostart"), "1323594394_apply-icon24.png", tr("Enable/disable autostart"), null, false);

        MainApplication.getToolbar().register(this);

        boolean selected = Config.getPref().getBoolean(KENDZI_3D_AUTOSTART, false);

        setSelected(selected);

        notifySelectedState();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleSelectedState(e);
        boolean selected = isSelected();
        Config.getPref().putBoolean(KENDZI_3D_AUTOSTART, selected);
        notifySelectedState();
    }
}
