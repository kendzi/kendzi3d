/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.actions.JosmAction;

import com.google.inject.Inject;

import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.layer.models.ModelsLibraryLayer;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.josm.kendzi3d.ui.Resumer;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;

/**
 * Clean up action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class CleanUpAction extends JosmAction implements Resumer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Texture cache service.
     */
    private TextureCacheService textureCacheService;

    /**
     * Texture library service.
     */
    private TextureLibraryStorageService textureLibraryStorageService;

    /**
     * Model cache service.
     */
    private ModelCacheService modelCacheService;

    /**
     * Model library service.
     */
    private ModelsLibraryService modelsLibraryService;

    /**
     * Models library layer.
     */
    private ModelsLibraryLayer modelsLibraryLayer;

    private Resumable resumable = () -> {
    };

    /**
     * Constructor.
     *
     * @param textureCacheService
     *            texture cache service
     * @param TextureLibraryStorageService
     *            texture library service
     * @param modelCacheService
     *            model cache service
     * @param modelsLibraryService
     *            Model library service
     */
    @Inject
    public CleanUpAction(TextureCacheService textureCacheService, TextureLibraryStorageService TextureLibraryStorageService,
            ModelCacheService modelCacheService, ModelsLibraryService modelsLibraryService,
            ModelsLibraryLayer modelsLibraryLayer) {

        super(tr("Clean up"), "1306318208_rebuild__24", tr("Rebuild models, textures and wold offset"), null, false);

        this.textureCacheService = textureCacheService;
        textureLibraryStorageService = TextureLibraryStorageService;
        this.modelCacheService = modelCacheService;
        this.modelsLibraryService = modelsLibraryService;
        this.modelsLibraryLayer = modelsLibraryLayer;
    }

    @Override
    public void actionPerformed(ActionEvent pE) {

        modelsLibraryService.clear();

        textureLibraryStorageService.reload();

        textureCacheService.clear();

        modelCacheService.clear();

        modelsLibraryLayer.cleanUp();

        resumable.resume();
    }

    @Override
    public void setResumable(Resumable r) {
        resumable = r;
    }
}
