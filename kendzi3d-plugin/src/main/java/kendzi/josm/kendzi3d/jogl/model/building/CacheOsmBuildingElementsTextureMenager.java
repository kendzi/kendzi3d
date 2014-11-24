package kendzi.josm.kendzi3d.jogl.model.building;

import java.util.HashMap;
import java.util.Map;

import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.OsmBuildingElementsTextureMenager;
import kendzi.jogl.texture.library.TextureFindCriteria;
import kendzi.jogl.texture.library.TextureLibraryStorageService;

public class CacheOsmBuildingElementsTextureMenager extends OsmBuildingElementsTextureMenager {

    private final Map<TextureFindCriteria, TextureData> cache = new HashMap<TextureFindCriteria, TextureData>();

    public CacheOsmBuildingElementsTextureMenager(TextureLibraryStorageService textureLibraryStorageService) {
        super(textureLibraryStorageService);
    }

    @Override
    public TextureData findTexture(TextureFindCriteria pTextureFindCriteria) {
        TextureData textureData = cache.get(pTextureFindCriteria);

        if (textureData == null) {
            textureData = super.findTexture(pTextureFindCriteria);
            cache.put(pTextureFindCriteria, textureData);
        }
        return textureData;
    }
}