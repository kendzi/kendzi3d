package kendzi.josm.kendzi3d.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kendzi.josm.kendzi3d.module.binding.Kendzi3dPluginDirectory;
import kendzi.josm.kendzi3d.util.StringUtil;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

/**
 * Downloads and setup textures and metadata from wiki page.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class WikiTextureLoaderService {

    /** Log. */
    private static final Logger log = Logger.getLogger(WikiTextureLoaderService.class);

    /**
     * Plugin directory.
     */
    @Inject @Kendzi3dPluginDirectory
    private String pluginDir;

    /**
     * Metadata cache service.
     */
    @Inject
    MetadataCacheService metadataCacheService;

    /**
     * Texture cache service
     */
    @Inject
    TextureCacheService textureCacheService;
    /**
     * Wiki page with textures and metadata.
     */
    private final static String wikiUrl = "http://wiki.openstreetmap.org/wiki/Special:Export/Kendzi3d/textures";

    /**
     * Constructor.
     */
    public WikiTextureLoaderService() {
        //
    }

    /** Downloads and setup textures and metadata from wiki page.
     * @return list of errors
     * @throws MalformedURLException error
     * @throws IOException error
     */
    public LoadRet load() throws MalformedURLException, IOException {

        LoadRet ret = new LoadRet();

        StringBuffer sb = readWikiStream();
        log.debug(sb);

        WikiRet wikiRet = parseXmlFile(null);
        ret.setTimestamp(wikiRet.getTimestamp());
        log.debug(wikiRet.getText());


        List<WikiTextures> wikiTextures = parseWikiTextures(wikiRet.getText());

        List<String> downloadErrors = downloadTexturesFiles(wikiTextures);
        ret.getErrors().addAll(downloadErrors);

        Properties properties = createProperties(wikiTextures);

        if (properties != null) {

            saveProperties(properties);

            this.metadataCacheService.loadMetadataProperties();
        }

        this.textureCacheService.clearTextures();

        return ret;
    }


    public class LoadRet {

        List<String> errors;
        String timestamp;



        /** Constructor.
         */
        public LoadRet() {
            this.errors = new ArrayList<String>();
        }

        /**
         * @return the errors
         */
        public List<String> getErrors() {
            return this.errors;
        }
        /**
         * @param errors the errors to set
         */
        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
        /**
         * @return the timestamp
         */
        public String getTimestamp() {
            return this.timestamp;
        }
        /**
         * @param timestamp the timestamp to set
         */
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }

    private void saveProperties(Properties prop) throws FileNotFoundException, IOException {

        File textureProp = new File(getTexturesPath() +
                "/wikimetadata.properties");

        prop.store(new FileOutputStream(textureProp), null);

    }

    private static Properties createProperties(List<WikiTextures> parseWiki) {
        Properties prop = new Properties();
        for (WikiTextures wt : parseWiki) {
            String key = wt.getKey();

            prop.setProperty(key + ".texture.file", "/textures/" +  wt.getFileKey());
            prop.setProperty(key + ".texture.height", blankOnNull(wt.getHeight()));
            prop.setProperty(key + ".texture.lenght", blankOnNull(wt.getLenght()));
            prop.setProperty(key + ".texture.url", blankOnNull(wt.getFileUrl()));
        }

        return prop;
    }

    static String blankOnNull(String pStr) {
        if (pStr == null) {
            return "";
        }
        return pStr;
    }

    private List<String> downloadTexturesFiles(List<WikiTextures> parseWiki) throws FileNotFoundException, IOException {

        List<String> errors = new ArrayList<String>();

        for (WikiTextures wt : parseWiki) {
            String fileUrl = wt.getFileUrl();
            String fileKey = wt.getFileKey();

            if (StringUtil.isBlankOrNull(fileKey) || StringUtil.isBlankOrNull(fileUrl)) {
                continue;
            }

            try {
                // no parent dir
                fileKey = fileKey.replaceAll("\\.\\.+", "");
                // no home dir
                fileKey = fileKey.replaceAll("~", "");

                File textureDir = new File(
                        getTexturesPath());

                if (!textureDir.exists()) {
                    textureDir.mkdirs();
                }

                File textureFile = new File(textureDir, fileKey);

                URL u = new URL(fileUrl);

                copy(u.openStream(),  new FileOutputStream(textureFile));

            } catch (Exception e) {
                String errorStr = "Error downloadinig texture from url: " + fileUrl;
                log.error(errorStr, e);
                errors.add(errorStr);
            }
        }
        return errors;
    }

    static void copy(InputStream in, OutputStream out) throws IOException {

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private static List<WikiTextures> parseWikiTextures(String wikiText) {

        List<WikiTextures> ret = new ArrayList<WikiTextures>();

        if (wikiText == null) {
            return ret;
        }

        String[] lines = wikiText.split("\n");
        for (String line : lines) {
            WikiTextures parseWikiLine = parseWikiLine(line);
            if (parseWikiLine != null) {
                ret.add(parseWikiLine);
            }
        }
        return ret;
    }

    private static WikiTextures parseWikiLine(String line) {
        if (line == null) {
            return null;
        }

        if (!line.startsWith("|")) {
            return null;
        }

        if (line.startsWith("|-")) {
            return null;
        }
        if (line.startsWith("|}")) {
            return null;
        }

        line = line.substring(line.indexOf("|")+1);

        String[] split = line.split("\\|\\|");

        String key = get(split,0);
        String height = get(split,1);
        String lenght  = get(split,2);
        String fileKey  = get(split,3);
        String fileUrl  = get(split,4);

        if (!StringUtil.isBlankOrNull(key)) {
            WikiTextures wt = new WikiTextures();
            wt.setKey(trim(key));
            wt.setHeight(trim(height));
            wt.setLenght(trim(lenght));
            wt.setFileKey(trim(fileKey));
            wt.setFileUrl(trim(fileUrl));

            return wt;
        }
        return null;
    }

    static class WikiTextures {
        String key ;
        String height;
        String lenght ;
        String fileKey ;
        String fileUrl ;
        /**
         * @return the key
         */
        public String getKey() {
            return this.key;
        }
        /**
         * @param key the key to set
         */
        public void setKey(String key) {
            this.key = key;
        }
        /**
         * @return the height
         */
        public String getHeight() {
            return this.height;
        }
        /**
         * @param height the height to set
         */
        public void setHeight(String height) {
            this.height = height;
        }
        /**
         * @return the lenght
         */
        public String getLenght() {
            return this.lenght;
        }
        /**
         * @param lenght the lenght to set
         */
        public void setLenght(String lenght) {
            this.lenght = lenght;
        }
        /**
         * @return the fileKey
         */
        public String getFileKey() {
            return this.fileKey;
        }
        /**
         * @param fileKey the fileKey to set
         */
        public void setFileKey(String fileKey) {
            this.fileKey = fileKey;
        }
        /**
         * @return the fileUrl
         */
        public String getFileUrl() {
            return this.fileUrl;
        }
        /**
         * @param fileUrl the fileUrl to set
         */
        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }

    private static String trim(String pStr) {
        if (pStr == null) {
            return null;
        }

        return pStr.trim();

    }

    private static String get(String[] split, int i) {
        if (split == null || split.length <= i) {
            return null;
        }

        return split[i];
    }


    private static WikiRet parseXmlFile(String string) {

        WikiRet ret = new WikiRet();

        // get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            Document dom = db.parse(wikiUrl);

            // get the root element
            Element docEle = dom.getDocumentElement();

            // get a nodelist of elements
            NodeList nl = docEle.getElementsByTagName("text");

            if (nl != null && nl.getLength() > 0) {
                // for (int i = 0; i < nl.getLength(); i++) {
                if (nl.getLength() > 0) {

                    // get the employee element
                    Element el = (Element) nl.item(0);
                    ret.setText(el.getTextContent());
                }
            }

            // get a nodelist of elements
            nl = docEle.getElementsByTagName("timestamp");

            if (nl != null && nl.getLength() > 0) {
                // for (int i = 0; i < nl.getLength(); i++) {
                if (nl.getLength() > 0) {

                    // get the employee element
                    Element el = (Element) nl.item(0);
                    ret.setTimestamp(el.getTextContent());
                }
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return ret;
    }

    static class WikiRet {
        String text;
        String timestamp;
        /**
         * @return the text
         */
        public String getText() {
            return this.text;
        }
        /**
         * @param text the text to set
         */
        public void setText(String text) {
            this.text = text;
        }
        /**
         * @return the timestamp
         */
        public String getTimestamp() {
            return this.timestamp;
        }
        /**
         * @param timestamp the timestamp to set
         */
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    private static StringBuffer readWikiStream() throws MalformedURLException, IOException {
        URL url = new URL(wikiUrl);

        // URL oracle = new URL("http://www.oracle.com/");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        StringBuffer sb = new StringBuffer();

        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            // XXX buffer!
            log.debug(inputLine);

            sb.append(inputLine);
        }

        in.close();
        return sb;
    }

    /** Local path for textures.
     * @return path for textures
     */
    public String getTexturesPath() {
        return this.pluginDir + File.separator + "textures";
    }

    /**
     * @return the pluginDir
     */
    public String getPluginDir() {
        return this.pluginDir;
    }

    /**
     * @param pluginDir the pluginDir to set
     */
    public void setPluginDir(String pluginDir) {
        this.pluginDir = pluginDir;
    }

    /**
     * @return the textureCacheService
     */
    public TextureCacheService getTextureCacheService() {
        return this.textureCacheService;
    }

    /**
     * @param textureCacheService the textureCacheService to set
     */
    public void setTextureCacheService(TextureCacheService textureCacheService) {
        this.textureCacheService = textureCacheService;
    }

    /**
     * @param metadataCacheService the metadataCacheService to set
     */
    public void setMetadataCacheService(MetadataCacheService metadataCacheService) {
        this.metadataCacheService = metadataCacheService;
    }

}
