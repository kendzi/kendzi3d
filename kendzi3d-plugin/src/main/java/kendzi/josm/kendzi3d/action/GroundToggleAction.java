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

import kendzi.josm.kendzi3d.jogl.model.ground.SelectableGround;
import kendzi.josm.kendzi3d.jogl.model.ground.SelectableGround.GroundType;
import kendzi.josm.kendzi3d.ui.Resumer;

/**
 * Enable/disable display texture on ground toggle action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class GroundToggleAction extends ToggleAction implements Resumer {

    private static final long serialVersionUID = 1L;

    /**
     * Debug view property key.
     */
    private final BooleanProperty KENDZI_3D_GROUND_TEXTURED = new BooleanProperty("kendzi3d.ground.textured", false);

    /**
     * Selectable ground drawer.
     */
    private final SelectableGround selectableGround;

    private Resumable resumable = () -> {
    };

    /**
     * Constructor of ground toggle action.
     *
     * @param selectableGround
     *            ground drawer
     */
    @Inject
    public GroundToggleAction(SelectableGround selectableGround) {
        super(tr("Textured Ground"), "1306318261_debugger__24", tr("Enable/disable display texture on ground"), null, false);
        this.selectableGround = selectableGround;

        MainApplication.getToolbar().register(this);
        setSelected(KENDZI_3D_GROUND_TEXTURED.get());

        notifySelectedState();
        setTexturedGround(isSelected());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleSelectedState(e);
        KENDZI_3D_GROUND_TEXTURED.put(isSelected());

        notifySelectedState();
        setTexturedGround(isSelected());
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
        resumable.resume();
    }

    @Override
    public void setResumable(Resumable r) {
        resumable = r;
    }
}
