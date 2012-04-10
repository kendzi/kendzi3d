package kendzi.josm.kendzi3d.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.dto.xsd.ObjectFactory;
import kendzi.josm.kendzi3d.dto.xsd.TextureLibrary;
import kendzi.josm.kendzi3d.dto.xsd.TextureSet;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class TextureLibraryService {

    private static final String TEXTURE_LIBRARY_INTERNAL_XML = "/resources/textureLibraryInternal.xml";
    public static final String TEXTURE_LIBRARY_WIKI_XML = "/resources/textureLibraryWiki.xml";

    /** Log. */
    private static final Logger log = Logger.getLogger(TextureLibraryService.class);

    Map<String, ArrayList<kendzi.josm.kendzi3d.dto.TextureData>> textureMap = new HashMap<String, ArrayList<TextureData>>();

    private UrlReciverService urlReciverService;

    private Random randomNumberGenerator = new Random();

    private File userTextureLibraryFile = null;

    /** Constructor.
     * @param urlReciverService url reciver service
     */
    @Inject
    public TextureLibraryService(UrlReciverService urlReciverService) {
        super();
        this.urlReciverService = urlReciverService;

        init();
    }

    /** Test if texture exist for key.
     * @param key texture key
     * @return is texture exist
     */
    public boolean isTexture(String key) {
        ArrayList<TextureData> set = this.textureMap.get(key);
        if (set== null || set.size() == 0) {
            return false;
        }
        return true;
    }

    /** Return random texture for key.
     * @param key texture key
     * @return is texture exist
     */
    public TextureData getTexture(String key) {
        ArrayList<TextureData> set = this.textureMap.get(key);
        if (set== null || set.size() == 0) {
            return null;
        }

        int nextInt = this.randomNumberGenerator.nextInt(set.size());

        return set.get(nextInt);
    }

    /**
     * Always return texture data. If configuration for texture key is not set return default texture data.
     *
     * @param key texture key
     * @return texture data
     */
    public TextureData getTextureDefault(String key) {
        TextureData texture = getTexture(key);
        if (texture == null) {
            return new TextureData(null, 1, 1);
        }
        return texture;
    }

    public String getKey(String pPattern, String ... pKeyParts) {
        return getKey(pPattern, true, pKeyParts);
    }

    public String getKey(String pPattern, boolean pTakeUnknown, String ... pKeyParts) {

        if (pTakeUnknown && pKeyParts != null) {
            for (int i = 0; i < pKeyParts.length; i++) {

                if (pKeyParts[i] == null) {
                    pKeyParts[i] = "unknown";
                }
            }
        }

        return MessageFormat.format(pPattern, (Object []) pKeyParts);
    }


    /**
     * Initialize.
     */
    private void init() {
        long t1 = System.currentTimeMillis();

        this.textureMap.clear();

        try {
            //load internal
            load(TEXTURE_LIBRARY_INTERNAL_XML);
        } catch (Exception e) {
            log.error(e,e);
        }
        try {
            // load wiki
            load(TEXTURE_LIBRARY_WIKI_XML);
        } catch (Exception e) {
            log.error(e,e);
        }

        try {
            // load wiki
            loadUserFile(userTextureLibraryFile);
        } catch (Exception e) {
            log.error(e,e);
        }


        System.out.println("load text libr: " + (System.currentTimeMillis() - t1));
        System.out.println("tst2");
    }


    private void load(String pUrl) throws FileNotFoundException, JAXBException {
//        this.textureMap.clear();

        URL pointModelConf = this.urlReciverService.receiveFileUrl(pUrl);

        loadUrl(pointModelConf);
    }

    private void loadUrl(URL pUrl) throws JAXBException, FileNotFoundException {
        List<TextureSet> pointModelsInternalList = loadXml(pUrl);

        for (TextureSet p : pointModelsInternalList) {

            for (kendzi.josm.kendzi3d.dto.xsd.TextureData td : p.getTextureData()) {
                TextureData td2 = convert(td);

                addTexture(p.getKey(), td2);
            }
        }
    }

    public void loadUserFile(File file) throws FileNotFoundException, JAXBException, MalformedURLException {
//        this.textureMap.clear();

        URL pointModelConf = file.toURI().toURL();

        loadUrl(pointModelConf);
    }

    private TextureData convert(kendzi.josm.kendzi3d.dto.xsd.TextureData td) {
        TextureData textureData = new TextureData(
                td.getFileKey(),
                td.getWidth() == null ? 1d : td.getWidth(),
                td.getHeight() == null ? 1d :td.getHeight());

        return textureData;
    }



    private void addTexture(String key, TextureData data) {
        ArrayList<TextureData> set = this.textureMap.get(key);

        if (set == null) {
            set = new ArrayList<TextureData>();
            this.textureMap.put(key, set);
        }

        set.add(data);
    }


    public static List<TextureSet> loadXml(URL url) throws JAXBException, FileNotFoundException {

        JAXBContext jaxbContext=JAXBContext.newInstance("kendzi.josm.kendzi3d.dto.xsd");

        Unmarshaller unmarshaller =
                jaxbContext.createUnmarshaller();

        @SuppressWarnings("unchecked")
        JAXBElement<TextureLibrary> c = (JAXBElement<TextureLibrary>)
                unmarshaller.unmarshal(url);

        return c.getValue().getTextureSet();
    }

    public static void saveXml(File file, TextureLibrary pTextureLibrary) throws JAXBException, FileNotFoundException {

        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        JAXBContext jaxbContext=JAXBContext.newInstance("kendzi.josm.kendzi3d.dto.xsd");
        Marshaller marshaller=jaxbContext.
               createMarshaller();

        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
//        marshaller.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, Boolean.TRUE);


        ObjectFactory factory=new ObjectFactory();

        JAXBElement<TextureLibrary> gl =
                factory.createTextureLibrary(pTextureLibrary);

        marshaller.marshal(gl, new FileOutputStream(file));

        log.info("saved textrure library: " + file.getAbsoluteFile());

    }

    public void clear() {
        this.textureMap.clear();

        init();
    }
}
