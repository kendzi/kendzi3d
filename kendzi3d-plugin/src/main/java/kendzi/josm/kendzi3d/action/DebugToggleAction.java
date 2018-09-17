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

import com.google.inject.Inject;

import kendzi.jogl.model.render.ModelRender;

/**
 * Debug toggle action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class DebugToggleAction extends ToggleAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Debug view property key.
     */
    public final static String KENDZI_3D_DEBUG_VIEW = "kendzi3d.debug.view";

    /**
     * Model render.
     */
    private ModelRender modelRender;

    /**
     * Constructor of debug toggle action.
     *
     * @param pModelRender
     *            model render
     */
    @Inject
    public DebugToggleAction(ModelRender pModelRender) {
        super(tr("Debug View"), "1306318261_debugger__24", tr("Enable/disable display debug information"), null, false);

        modelRender = pModelRender;

        MainApplication.getToolbar().register(this);

        boolean selected = Config.getPref().getBoolean(KENDZI_3D_DEBUG_VIEW, false);

        setSelected(selected);

        notifySelectedState();

        setState(selected);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleSelectedState(e);
        boolean selected = isSelected();
        Config.getPref().putBoolean(KENDZI_3D_DEBUG_VIEW, selected);
        notifySelectedState();

        setState(selected);

    }

    /**
     * @param pEnable
     *            enable debug
     */
    private void setState(boolean pEnable) {

        modelRender.setDebugging(pEnable);
        modelRender.setDrawEdges(pEnable);
        modelRender.setDrawNormals(pEnable);

    }

}
