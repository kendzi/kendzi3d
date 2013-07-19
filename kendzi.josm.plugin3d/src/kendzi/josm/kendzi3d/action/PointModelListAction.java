/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */


package kendzi.josm.kendzi3d.action;

import java.awt.event.ActionEvent;

import kendzi.jogl.texture.TextureCacheService;
import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.service.impl.PointModelService;
import kendzi.josm.kendzi3d.ui.pointModel.action.PointModelListFrameAction;

import org.openstreetmap.josm.actions.JosmAction;

import com.google.inject.Inject;

import static org.openstreetmap.josm.tools.I18n.*;

/**
 * Point model list action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class PointModelListAction extends JosmAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * JOSM 3D Render.
     */
    private RenderJOSM renderJosm;

    /**
     * Texture cache service.
     */
    private TextureCacheService textureCacheService;

    /**
     * Point model service.
     */
    private PointModelService pointModelService;

    /**
     * Constructor.
     * @param renderJosm
     * @param textureCacheService
     */
    @Inject
    public PointModelListAction(
            RenderJOSM renderJosm,
            TextureCacheService textureCacheService,
            PointModelService pPointModelService) {
        super(
                tr("List of models"),
                "1306318208_rebuild__24",
                tr("Models defined in point model layer"),
                null,
                false
        );

        this.renderJosm = renderJosm;
        this.textureCacheService = textureCacheService;
        this.pointModelService = pPointModelService;
    }

    @Override
    public void actionPerformed(ActionEvent pE) {

//        this.textureCacheService.clear();
//
//        // XXX add event
//        this.renderJosm.processDatasetEvent(null);


        PointModelListFrameAction frame = new PointModelListFrameAction();
        frame.setPointModelService(this.pointModelService);
        frame.loadTableData();
        frame.setVisible(true);


    }

    @Override
    protected void updateEnabledState() {
//        setEnabled(Main.map != null && Main.main.getEditLayer() != null);
    }
}
