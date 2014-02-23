/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.*;

import java.awt.event.ActionEvent;

import kendzi3d.light.service.impl.LightService;
import kendzi3d.light.ui.action.LightFrameAction;

import org.openstreetmap.josm.actions.JosmAction;

import com.google.inject.Inject;

/**
 * Light configuration action.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class LightConfigurationAction extends JosmAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /** 
     */
    private LightService lightService;

    private LightFrameAction lastFrame;

    /**
     * Constructor.
     * 
     * @param modelsLibraryService
     */
    @Inject
    public LightConfigurationAction(LightService lightService) {
        super(tr("Light configuration"), "1306318208_rebuild__24",
                tr("Light configuration allow to define light source color and location"), null, false);

        this.lightService = lightService;
    }

    @Override
    public void actionPerformed(ActionEvent action) {

        if (lastFrame == null || !lastFrame.isDisplayable()) {
            // only one instance
            LightFrameAction frame = new LightFrameAction(lightService);
            frame.setVisible(true);

            this.lastFrame = frame;
        }
    }
}
