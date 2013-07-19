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
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.service.ModelCacheService;

import org.openstreetmap.josm.actions.JosmAction;

import com.google.inject.Inject;

import static org.openstreetmap.josm.tools.I18n.*;

/**
 * Clean up action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class CleanUpAction extends JosmAction {

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
     * Texture library service.
     */
    private TextureLibraryStorageService textureLibraryStorageService;

    /**
     * Model cache service.
     */
    private ModelCacheService modelCacheService;

    /**
     * Constructor.
     * @param renderJosm
     * @param textureCacheService
     */
    @Inject
    public CleanUpAction(RenderJOSM renderJosm,
            TextureCacheService textureCacheService,
            TextureLibraryStorageService TextureLibraryStorageService,
            ModelCacheService modelCacheService) {

        super(
                tr("Clean up"),
                "1306318208_rebuild__24",
                tr("Rebuild models, textures and wold offset"),
                null,
                false
        );

        this.renderJosm = renderJosm;
        this.textureCacheService = textureCacheService;
        this.textureLibraryStorageService = TextureLibraryStorageService;
        this.modelCacheService = modelCacheService;
    }

    @Override
    public void actionPerformed(ActionEvent pE) {

        this.textureLibraryStorageService.reload();

        this.textureCacheService.clear();

        this.modelCacheService.clear();

        // XXX add event
        this.renderJosm.processDatasetEvent(null);
    }

    @Override
    protected void updateEnabledState() {
//        setEnabled(Main.map != null && Main.main.getEditLayer() != null);
    }
}
