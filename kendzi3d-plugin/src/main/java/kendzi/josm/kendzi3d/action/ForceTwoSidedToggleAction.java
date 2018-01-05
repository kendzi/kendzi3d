/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.*;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.actions.ExpertToggleAction;
import org.openstreetmap.josm.actions.ExpertToggleAction.ExpertModeChangeListener;
import org.openstreetmap.josm.actions.ToggleAction;
import org.openstreetmap.josm.data.preferences.BooleanProperty;
import org.openstreetmap.josm.gui.MainApplication;

import com.google.inject.Inject;

import kendzi.jogl.model.render.ModelRender;

/**
 * Enable/disable display texture on models toggle action.
 */
public class ForceTwoSidedToggleAction extends ToggleAction implements ExpertModeChangeListener {

    private static final long serialVersionUID = 1L;

    private static final BooleanProperty FORCE_TWO_SIDED =
            new BooleanProperty("kendzi3d.models.forceTwoSidedLightingForAllModels", false);

    private final ModelRender modelRender;

    /**
     * Constructor of texture toggle action.
     *
     * @param pModelRender ModelRender
     */
    @Inject
    public ForceTwoSidedToggleAction(ModelRender pModelRender) {
        super(tr("Force two sided lighting"), "1306318261_debugger__24",
                tr("Force two sided lighting to be used for each model." +
                        "This is normally enabled on a per-model basis for some models."), null, false);

        this.modelRender = pModelRender;

        MainApplication.getToolbar().register(this);

        setSelected(FORCE_TWO_SIDED.get());

        notifySelectedState();

        setState(FORCE_TWO_SIDED.get());

        ExpertToggleAction.addExpertModeChangeListener(this, true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleSelectedState(e);

        FORCE_TWO_SIDED.put(isSelected());

        notifySelectedState();

        setState(FORCE_TWO_SIDED.get());

    }

    /**
     * @param pEnable
     *            enable debug
     */
    private void setState(boolean pEnable) {

        modelRender.setDrawTwoSided(pEnable);

    }

    @Override
    public void expertChanged(boolean expert) {

        this.setEnabled(expert);

    }

}
