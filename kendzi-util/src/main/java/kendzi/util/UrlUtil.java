package kendzi.util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class UrlUtil {

    /** Log. */
    private static final Logger log = Logger.getLogger(UrlUtil.class);

    /**
     * Test if file from url exist.
     * 
     * @param url
     *            url to file
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

    public static URL getParent(URL url) {
        URI uri;
        try {
            uri = url.toURI();
            URI paretnt = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
            return paretnt.toURL();
        } catch (URISyntaxException e) {
            log.error(e, e);
        } catch (MalformedURLException e) {
            log.error(e, e);
        }
        return null;
    }
}
