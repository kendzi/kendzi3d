/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.texture.library;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.bind.JAXBException;
import kendzi.jogl.texture.dto.TextureData;

/**
 * Texture library.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public interface TextureLibraryStorageService {

    /**
     * Always return texture data. If configuration for texture key is not set
     * return default texture data.
     *
     * @param textureKey
     *            texture key
     * @return texture data
     */
    TextureData getTextureDefault(String textureKey);

    List<TextureData> findTextureData(String textureKey);

    String getKey(TextureLibraryKey pKey, String... pKeyParts);

    void reload();

    void loadUserFile(UrlTextureLibrary pUrlTextureLibrary) throws FileNotFoundException, JAXBException, MalformedURLException;

}
