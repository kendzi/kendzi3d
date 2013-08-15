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

import kendzi.kendzi3d.models.library.dao.LibraryResourcesMemoryDao;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.models.library.ui.action.ModelLibraryResourcesListFrameAction;

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
     * Point model service.
     */
    private ModelsLibraryService modelsLibraryService;

    /**
     * Constructor.
     * @param renderJosm
     * @param textureCacheService
     * @param modelsLibraryService
     */
    @Inject
    public PointModelListAction(
            ModelsLibraryService modelsLibraryService) {
        super(
                tr("Models library"),
                "1306318208_rebuild__24",
                tr("Models library allow to define model for nodes"),
                null,
                false
        );

        this.modelsLibraryService = modelsLibraryService;
    }

    @Override
    public void actionPerformed(ActionEvent pE) {

        // this.textureCacheService.clear();
        //
        // XXX add event
        // this.renderJosm.processDatasetEvent(null);

        ModelLibraryResourcesListFrameAction frame = new ModelLibraryResourcesListFrameAction(this.modelsLibraryService,
                new LibraryResourcesMemoryDao());
        frame.loadTableData();

        frame.setVisible(true);
    }

    @Override
    protected void updateEnabledState() {
        // setEnabled(Main.map != null && Main.main.getEditLayer() != null);
    }
}
