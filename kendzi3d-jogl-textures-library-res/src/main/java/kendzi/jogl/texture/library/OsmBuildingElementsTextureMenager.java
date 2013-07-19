package kendzi.jogl.texture.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.TextureFindCriteria.Type;




public class OsmBuildingElementsTextureMenager extends BuildingElementsTextureManager {

    TextureLibraryStorageService textureLibraryStorageService;

    public OsmBuildingElementsTextureMenager(TextureLibraryStorageService textureLibraryService) {
        super();
        this.textureLibraryStorageService = textureLibraryService;
    }

    @Override
    public TextureData findTexture(TextureFindCriteria pTextureFindCriteria) {

        boolean colorable = pTextureFindCriteria.isColorable();

        TextureLibraryKey key = null;
        Type type = pTextureFindCriteria.getType();
        if (Type.WINDOW.equals(type)) {
            key = TextureLibraryKey.BUILDING_WINDOW;
        } else if (Type.WINDOWS.equals(type)) {
            key = TextureLibraryKey.BUILDING_WINDOWS;
        } else if (Type.ENTERENCE.equals(type)) {
            key = TextureLibraryKey.BUILDING_ENTRANCE;
        } else if (Type.FACADE.equals(type)) {
            key = TextureLibraryKey.BUILDING_FACADE;
        } else if (Type.ROOF.equals(type)) {
            key = TextureLibraryKey.BUILDING_ROOF;
        } else if (Type.FLOOR.equals(type)) {
            key = TextureLibraryKey.BUILDING_FLOOR;
        }

        if (key == null) {
            throw new RuntimeException("unknown search texture criteria type: " + type);
        }

        String keyStr = this.textureLibraryStorageService.getKey(key, pTextureFindCriteria.getTypeName()/*, pTextureFindCriteria.getSubTypeName()*/);

        List<TextureData> textureSet = this.textureLibraryStorageService.findTextureData(keyStr);
        boolean findColorable = false;
        if (colorable) {
            List<TextureData> filterByColorable = filterByColorable(colorable, textureSet);

            if (filterByColorable.size() > 0) {
                // only when colorable texture data exist.
                // otherwise we use regular (all) texture data
                textureSet = filterByColorable;
                findColorable = true;
            }
        }


        if ( pTextureFindCriteria.getHeight() != null ||  pTextureFindCriteria.getWidth() != null) {
            textureSet = filterByBestSizeMatch(pTextureFindCriteria, textureSet);
        }

        TextureData textureData = getRadnomTextureFromSet(textureSet, pTextureFindCriteria.getId());

        if (colorable && !findColorable) {
            textureData = colorableTextureData(textureData);
        }

        return textureData;
    }


    public static TextureData getRadnomTextureFromSet(List<TextureData> set, long id) {

        if (set== null || set.size() == 0) {
            return null;
        }

        // int nextInt = this.randomNumberGenerator.nextInt(id % set.size());

        return set.get((int) (id % set.size()));
    }
    /**
     * @param colorable
     * @param textureSet
     * @return
     */
    public List<TextureData> filterByColorable(
            boolean colorable, List<TextureData> textureSet) {
        List<TextureData> ret = new ArrayList<TextureData>();

        for (TextureData td : textureSet) {

            if (td.isColorable() != null && td.isColorable() == colorable) {
                ret.add(td);
            }
        }
        return ret;
    }

    public static TextureData colorableTextureData(TextureData textureData) {


        String tex0 = textureData.getTex0();

        if (tex0 == null) {
            tex0 = "#c=0xffffff";
        } else {
            tex0 = "#bw=" + tex0;
        }

        return new TextureData(
                tex0,
                textureData.getTex1(),
                textureData.getWidth(),
                textureData.getHeight(),
                true);

    }
    /**
     * @param pTextureFindCriteria
     * @param textureSet
     * @return
     */
    public List<TextureData> filterByBestSizeMatch(TextureFindCriteria pTextureFindCriteria, List<TextureData> textureSet) {
        TextureData best = null;
        double bestError = Double.MAX_VALUE;

        double height = pTextureFindCriteria.getHeight() == null ? 0 :  pTextureFindCriteria.getHeight();
        double width = pTextureFindCriteria.getWidth() == null ? 0 : pTextureFindCriteria.getWidth();

        for (TextureData td : textureSet) {
            double dH = td.getHeight() - height;
            double dW = td.getWidth() - width;

            double err = dH * dH + dW * dW;

            if (err < bestError) {
                bestError = err;
                best = td;
            }
        }
        return Arrays.asList(best);
    }
}
