
package kendzi.util;

import java.io.InputStream;
import java.net.URL;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class UrlUtil {

    /** Test if file from url exist.
     * @param url url to file
     * @return if file exist
     */
    public static boolean existUrl(URL url) {
        if (url == null) {
            return false;
        }

        try {
            InputStream openStream = url.openStream();

            openStream.close();

            return true;

        } catch (Exception e) {
            //
        }

        return false;
    }
}
