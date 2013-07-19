/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.texture.library;

import java.net.URL;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class UrlTextureLibrary {

    private URL url;

    private boolean overwrite;

    /**
     * @return the url
     */
    public URL getUrl() {
        return this.url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * @return the overwrite
     */
    public boolean isOverwrite() {
        return this.overwrite;
    }

    /**
     * @param overwrite the overwrite to set
     */
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

}
