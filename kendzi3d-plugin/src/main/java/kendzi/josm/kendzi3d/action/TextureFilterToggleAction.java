/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.*;

import java.awt.event.ActionEvent;

import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.texture.TextureCacheServiceImpl;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.ToggleAction;

import com.google.inject.Inject;

/**
 * Texture filter toggle action.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class TextureFilterToggleAction extends ToggleAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Texture cache service.
     */
    private TextureCacheService textureCacheService;

    /**
     * Constructor of debug toggle action.
     */
    @Inject
    public TextureFilterToggleAction(TextureCacheService textureCacheService) {
        super(tr("Texture filter"), "1306318261_debugger__24", tr("Enable/disable texture filter"), null, true);

        this.textureCacheService = textureCacheService;

        Main.toolbar.register(this);

        boolean selected = true;

        setSelected(selected);

        notifySelectedState();

        setTextureFilter(selected);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        toggleSelectedState(e);
        boolean selected = isSelected();

        notifySelectedState();

        setTextureFilter(selected);
    }

    /**
     * @param pEnable
     *            enable filter
     */
    private void setTextureFilter(boolean pEnable) {
        if (this.textureCacheService instanceof TextureCacheServiceImpl) {
            ((TextureCacheServiceImpl) this.textureCacheService).setTextureFilter(pEnable);
            this.textureCacheService.clear();
        } else {
            throw new RuntimeException("unsupported textureCacheService");
        }
    }
}
