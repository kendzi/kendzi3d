package kendzi.josm.kendzi3d.jogl.model.building.texture;

import java.util.HashSet;
import java.util.Set;

import kendzi.josm.kendzi3d.dto.TextureData;

public class SimpleBuildingElementsTextureMenager extends BuildingElementsTextureMenager {

    private Set<TextureData> tdSet = new HashSet();

    public void registerTexture(TextureData td) {
        this.tdSet.add(td);
    }

    @Override
    public TextureData findTexture(TextureFindCriteria pTextureFindCriteria) {
        for (TextureData td : this.tdSet) {
            return td;
        }
        return null;
    }
}
