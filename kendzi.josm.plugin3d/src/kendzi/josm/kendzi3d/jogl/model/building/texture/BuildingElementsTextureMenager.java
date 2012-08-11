package kendzi.josm.kendzi3d.jogl.model.building.texture;

import kendzi.josm.kendzi3d.dto.TextureData;

public abstract class BuildingElementsTextureMenager {

    public enum Type {
        WINDOW, WINDOWS, ENTERENCE,
    }

    abstract public TextureData findTexture(TextureFindCriteria pTextureFindCriteria);
}
