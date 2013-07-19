/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.texture.library;

/**
 * Texture keys in library.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public enum TextureLibraryKey {
    BARRIER_FENCE("barrier.fence_{0}"),
    BUILDING_FACADE("buildings.facade_{0}"),
    BUILDING_FLOOR("buildings.floor_{0}"),
    BUILDING_ROOF("buildings.roof_{0}"),
    BUILDING_WINDOW("buildings.window_{0}"),
    BUILDING_WINDOWS("buildings.windows_{0}"),
    BUILDING_ENTRANCE("buildings.entrance_{0}"),
    WATER("landuse.water_{0}"),

    ;

    private String key;

    TextureLibraryKey(String pKey) {
        this.key = pKey;
    }

    public String getKey() {
        return this.key;
    }

}