/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.actions.ToggleAction;
import org.openstreetmap.josm.data.preferences.BooleanProperty;
import org.openstreetmap.josm.gui.MainApplication;

import com.google.inject.Inject;

import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.ui.Resumer;

/**
 * Debug toggle action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class DebugToggleAction extends ToggleAction implements Resumer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Debug view property key.
     */
    public final BooleanProperty KENDZI_3D_DEBUG_VIEW = new BooleanProperty("kendzi3d.debug.view", false);

    /**
     * Model render.
     */
    private ModelRender modelRender;

    private Resumable resumable = () -> {
    };

    /**
     * Constructor of debug toggle action.
     *
     * @param pModelRender
     *            model render
     */
    @Inject
    public DebugToggleAction(ModelRender modelRender) {
        super(tr("Debug View"), "1306318261_debugger__24", tr("Enable/disable display debug information"), null, false);
        this.modelRender = modelRender;

        MainApplication.getToolbar().register(this);
        setSelected(KENDZI_3D_DEBUG_VIEW.get());

        notifySelectedState();
        setState(isSelected());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleSelectedState(e);
        KENDZI_3D_DEBUG_VIEW.put(isSelected());

        notifySelectedState();
        setState(isSelected());
    }

    /**
     * @param pEnable
     *            enable debug
     */
    private void setState(boolean pEnable) {
        modelRender.setDebugging(pEnable);
        modelRender.setDrawEdges(pEnable);
        modelRender.setDrawNormals(pEnable);
        resumable.resume();
    }

    @Override
    public void setResumable(Resumable r) {
        resumable = r;
    }
}
