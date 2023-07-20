/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.tr;

import com.google.inject.Inject;

import java.awt.event.ActionEvent;

import kendzi.josm.kendzi3d.jogl.model.PointModel;
import org.openstreetmap.josm.actions.ToggleAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.spi.preferences.Config;

/**
 * Debug model library toggle action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class DebugPointModelToggleAction extends ToggleAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Debug view property key.
     */
    public static final String KENDZI_3D_MODEL_LIBRARY_DEBUG_VIEW = "kendzi3d.model.library.debug";

    /**
     * Constructor of debug toggle action.
     *
     * @param pModelRender
     *            model render
     */
    @Inject
    public DebugPointModelToggleAction() {
        super(tr("Debug models library"), "1306318261_debugger__24",
                tr("Enable/disable display debug information for model library"), null, false);

        // putValue("help", ht("/Action/FullscreenView"));
        // putValue("toolbar", "fullscreen");
        MainApplication.getToolbar().register(this);

        boolean selected = Config.getPref().getBoolean(KENDZI_3D_MODEL_LIBRARY_DEBUG_VIEW, false);

        setSelected(selected);

        notifySelectedState();

        setState(selected);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleSelectedState(e);
        boolean selected = isSelected();
        Config.getPref().putBoolean(KENDZI_3D_MODEL_LIBRARY_DEBUG_VIEW, selected);
        notifySelectedState();

        setState(selected);
    }

    private void setState(boolean selected) {
        // XXX
        PointModel.debug = selected;
    }

}
