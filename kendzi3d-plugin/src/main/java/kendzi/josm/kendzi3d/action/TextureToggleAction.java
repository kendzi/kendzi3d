/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.*;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.ToggleAction;
import org.openstreetmap.josm.gui.MainApplication;

import com.google.inject.Inject;

import kendzi.jogl.model.render.ModelRender;

/**
 * Enable/disable display texture on models toggle action.
 */
public class TextureToggleAction extends ToggleAction {

    private static final long serialVersionUID = 1L;

    private static final String KENDZI_3D_MODELS_TEXTURED = "kendzi3d.models.textured";

    private final ModelRender modelRender;

    /**
     * Constructor of texture toggle action.
     *
     * @param pModelRender ModelRender
     */
    @Inject
    public TextureToggleAction(ModelRender pModelRender) {
        super(tr("Textured Models"), "1306318261_debugger__24", tr("Enable/disable display texture on models"), null, false);

        this.modelRender = pModelRender;

        MainApplication.getToolbar().register(this);

        boolean selected = Main.pref.getBoolean(KENDZI_3D_MODELS_TEXTURED, true);

        setSelected(selected);

        notifySelectedState();

        setState(selected);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleSelectedState(e);
        boolean selected = isSelected();
        Main.pref.putBoolean(KENDZI_3D_MODELS_TEXTURED, selected);
        notifySelectedState();

        setState(selected);

    }

    /**
     * @param pEnable
     *            enable debug
     */
    private void setState(boolean pEnable) {

        modelRender.setDrawTextures(pEnable);

    }

}
