/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.texture.library;

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

import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.service.UrlReciverService;
import kendzi.util.UrlUtil;

import org.apache.log4j.Logger;
import org.kendzi3d.ObjectFactory;
import org.kendzi3d.TextureLibrary;
import org.kendzi3d.TextureSet;

public class TextureLibraryService implements TextureLibraryStorageService {

    private static final String TEXTURE_LIBRARY_INTERNAL_XML = "/textures/textureLibraryInternal.xml";
    public static final String TEXTURE_LIBRARY_WIKI_XML = "/textures/textureLibraryWiki.xml";



    /** Log. */
    private static final Logger log = Logger.getLogger(TextureLibraryService.class);

    Map<String, ArrayList<TextureData>> textureMap = new HashMap<String, ArrayList<TextureData>>();

    private UrlReciverService urlReciverService;

    private Random randomNumberGenerator = new Random();

    private UrlTextureLibrary userTextureLibraryUrl = null;

    /** Constructor.
     * @param urlReciverService url reciver service
     */
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
    @Deprecated
    public TextureData getRadnomTextureForKey(String key) {

        ArrayList<TextureData> set = this.textureMap.get(key);

        return getRadnomTextureFromSet(set);
    }

    @Deprecated
    public TextureData getRadnomTextureFromSet(List<TextureData> set) {

        if (set== null || set.size() == 0) {
            return null;
        }

        int nextInt = this.randomNumberGenerator.nextInt(set.size());

        return set.get(nextInt);
    }



    /** Return texture list for key.
     * @param key texture key
     * @return texture list for key
     */
    public List<TextureData> findTextureData(String key) {
        ArrayList<TextureData> set = this.textureMap.get(key);
        if (set == null) {
            return new ArrayList<TextureData>();
        }

        return set;
    }



    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.service.TextureLibrary#getTextureDefault(java.lang.String)
     */
    @Override
    public TextureData getTextureDefault(String key) {
        TextureData texture = getRadnomTextureForKey(key);
        if (texture == null) {
            return new TextureData(null, 1, 1);
        }
        return texture;
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.service.TextureLibrary#getKey(kendzi.josm.kendzi3d.service.TextureLibraryService.TextureLibraryKey, java.lang.String)
     */
    @Override
    public String getKey(TextureLibraryKey pKey, String ... pKeyParts) {
        return getKey(pKey.getKey(), true, pKeyParts);
    }

    @Deprecated
    private String getKey(String pPattern, String ... pKeyParts) {
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
            loadUserFile(this.userTextureLibraryUrl);
        } catch (Exception e) {
            log.error(e,e);
        }


        System.out.println("load text libr: " + (System.currentTimeMillis() - t1));
        System.out.println("tst2");
    }


    private void load(String pUrl) throws FileNotFoundException, JAXBException {
        //        this.textureMap.clear();

        URL pointModelConf = this.urlReciverService.receiveFileUrl(pUrl);

        if (!UrlUtil.existUrl(pointModelConf)) {
            log.warn("cant load texture configuration from: " + pUrl + " url don't exist: " + pointModelConf);
            return;
        }

        loadUrl(pointModelConf);
    }



    private void loadUrl(URL pUrl) throws JAXBException, FileNotFoundException {
        List<TextureSet> pointModelsInternalList = loadXml(pUrl);

        for (TextureSet p : pointModelsInternalList) {

            for (org.kendzi3d.TextureData td : p.getTextureData()) {
                TextureData td2 = convert(td);

                addTexture(p.getKey(), td2);
            }
        }
    }

    public void loadUserFile(UrlTextureLibrary pUrlTextureLibrary) throws FileNotFoundException, JAXBException, MalformedURLException {

        if (pUrlTextureLibrary == null) {
            return;
        }

        if (pUrlTextureLibrary.isOverwrite()) {
            this.textureMap.clear();
        }

        loadUrl(pUrlTextureLibrary.getUrl());
        this.userTextureLibraryUrl = pUrlTextureLibrary;
    }

    private TextureData convert(org.kendzi3d.TextureData td) {
        TextureData textureData = new TextureData(
                td.getTex0(),
                td.getTex1(),
                td.getWidth() == null ? 1d : td.getWidth(),
                        td.getHeight() == null ? 1d :td.getHeight(),
                                td.isColorable());

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

        //        JAXBContext jaxbContext=JAXBContext.newInstance("kendzi.josm.kendzi3d.dto.xsd");

        JAXBContext jaxbContext=JAXBContext.newInstance( "org.kendzi3d", TextureLibraryService.class.getClassLoader());
        // JAXBContext jaxbContext=JAXBContext.newInstance("kendzi.josm.kendzi3d.dto.xsd");

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

        JAXBContext jaxbContext=JAXBContext.newInstance("org.kendzi3d", TextureLibraryService.class.getClassLoader());
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

    public void reload() {
        this.textureMap.clear();
        init();
    }


}
