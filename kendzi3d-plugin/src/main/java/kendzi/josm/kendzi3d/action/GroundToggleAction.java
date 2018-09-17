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

import kendzi.josm.kendzi3d.jogl.model.ground.SelectableGround;
import kendzi.josm.kendzi3d.jogl.model.ground.SelectableGround.GroundType;

/**
 * Enable/disable display texture on ground toggle action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class GroundToggleAction extends ToggleAction {

    private static final long serialVersionUID = 1L;

    private static final String KENDZI_3D_GROUND_TEXTURED = "kendzi3d.ground.textured";

    /**
     * Selectable ground drawer.
     */
    private final SelectableGround selectableGround;

    /**
     * Constructor of ground toggle action.
     *
     * @param selectableGround
     *            ground drawer
     */
    @Inject
    public GroundToggleAction(SelectableGround selectableGround) {
        super(tr("Textured Ground"), "1306318261_debugger__24", tr("Enable/disable display texture on ground"), null, false);

        MainApplication.getToolbar().register(this);

        boolean selected = Config.getPref().getBoolean(KENDZI_3D_GROUND_TEXTURED, false);

        setSelected(selected);

        notifySelectedState();

        this.selectableGround = selectableGround;

        setTexturedGround(selected);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleSelectedState(e);
        boolean selected = isSelected();
        Config.getPref().putBoolean(KENDZI_3D_GROUND_TEXTURED, selected);
        notifySelectedState();

        setTexturedGround(selected);
    }

    /**
     * @param enable
     *            checkbox enabled
     */
    private void setTexturedGround(boolean enable) {
        if (enable) {
            selectableGround.selectGroundType(GroundType.STYLED_TITLE);
        } else {
            selectableGround.selectGroundType(GroundType.SINGLE_TEXTURE);
        }
    }
}
