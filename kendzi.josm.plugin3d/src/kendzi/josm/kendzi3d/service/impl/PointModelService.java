package kendzi.josm.kendzi3d.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Vector3d;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import kendzi.josm.kendzi3d.dto.xsd.ObjectFactory;
import kendzi.josm.kendzi3d.dto.xsd.PointModel;
import kendzi.josm.kendzi3d.dto.xsd.PointModels;
import kendzi.josm.kendzi3d.service.UrlReciverService;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

public class PointModelService {

    /** Log. */
    private static final Logger log = Logger.getLogger(PointModelService.class);

  //@Inject
    private UrlReciverService urlReciverService;

    private List<PointModelDataChange> pointModelDataChange = new ArrayList<PointModelDataChange>();


    public void addPointModelDataChangeListener(PointModelDataChange pPointModelDataChange) {
        this.pointModelDataChange.add(pPointModelDataChange);
    }


    protected void firePointModelDataChange() {
        for (PointModelDataChange l : this.pointModelDataChange) {
            l.firePointModelDataChange();
        }
    }


//    private Map<String, PointModel> pointModelsMap = new HashMap<String, PointModel>();

//    private Set<String> internalPointModelsSet = new HashSet<String>();

    /** Constructor.
     * @param urlReciverService url reciver service
     */
    @Inject
    public PointModelService(UrlReciverService urlReciverService) {
        super();
        this.urlReciverService = urlReciverService;

        init();
    }


    //    private List<PointModel> pointModelsInternalList;
    private Map<Long, PointModel> pointModelsUserMap = new LinkedHashMap<Long, PointModel>();
    private Map<Long, PointModel> pointModelsInternalMap = new LinkedHashMap<Long, PointModel>();

//    private List<PointModel> pointModelsList;

    public List<PointModel> findAll() {

        Set<Long> keySet = new HashSet<Long>();

        List<PointModel> ret = new ArrayList<PointModel>();

        for (Long id : this.pointModelsUserMap.keySet()) {
            ret.add(this.pointModelsUserMap.get(id));
            keySet.add(id);
        }
        for (Long id : this.pointModelsInternalMap.keySet()) {
            if (!keySet.contains(id) ) {
                ret.add(this.pointModelsUserMap.get(id));
            }
        }
        return ret;
    }

    public void remove(Long id) {
        this.pointModelsUserMap.remove(id);
    }

    public void saveOrUpdate(PointModel pPointModel) {

        if (pPointModel.getId() == null) {
            pPointModel.setId(genId());
        }

        this.pointModelsUserMap.put(pPointModel.getId(), pPointModel);

        firePointModelDataChange();
    }

    private Long genId() {
        return System.currentTimeMillis();
    }

    public PointModel load(Long pID) {
        PointModel ret = this.pointModelsUserMap.get(pID);
        if (ret == null) {
            ret = this.pointModelsInternalMap.get(pID);
        }
        return ret;
    }

    public boolean isUser(String key) {

//        for (PointModel p : this.pointModelsList) {
//            if (key.equals(p.getKey())) {
//                return true;
//            }
//        }
        return false;
    }


//    private static List<PointModel> parseXmlFile(String pFileUrl) {
//
//        List<PointModel> ret = new ArrayList<PointModel>();
//
//        // get the factory
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//
//        try {
//
//            // Using factory get an instance of document builder
//            DocumentBuilder db = dbf.newDocumentBuilder();
//
//            // parse using builder to get DOM representation of the XML file
//            Document dom = db.parse(pFileUrl);
//
//            // get the root element
//            Element docEle = dom.getDocumentElement();
//
//            // get a nodelist of elements
//            NodeList nl = docEle.getElementsByTagName("pointModel");
//
//            if (nl != null && nl.getLength() > 0) {
//                for (int i = 0; i < nl.getLength(); i++) {
//
//                    Element el = (Element) nl.item(i);
//
//                    PointModel pm = new PointModel();
//                    pm.setModel(el.getAttribute("model"));
//                    pm.setMatcher(el.getAttribute("matcher"));
//
//                    pm.setTranslateX(parseDouble(el.getAttribute("translateX"), null));
//                    pm.setTranslateY(parseDouble(el.getAttribute("translateY"), null));
//                    pm.setTranslateZ(parseDouble(el.getAttribute("translateZ"), null));
//
//                    pm.setScale(el.getAttribute("scale"));
//
//                    ret.add(pm);
//
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("error parsing point model xml config: " + pFileUrl, e);
//        }
//        return ret;
//    }

    static Vector3d parseVector(String x, String y, String z) {
        return new Vector3d(
                parseDouble(x, 0d),
                parseDouble(y, 0d),
                parseDouble(z, 0d));
    }

    private static Double parseDouble(String z, Double d) {
        if (z == null || "".equals(z)) {
            return d;
        }

        try {
            return Double.parseDouble(z);
        } catch (Exception e) {
            log.error(e,e);
        }
        return d;
    }

    /**
     * Initialize.
     */
    public void init() {

        try {
            loadInternal();
        } catch (Exception e) {
            log.error(e,e);
        }

        try {
            loadUser();
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    private void loadInternal() {
        this.pointModelsInternalMap.clear();

        try {
            URL pointModelConf = this.urlReciverService.receiveFileUrl("/models/pointModelLayerInternal.xml");

            List<PointModel> pointModelsInternalList;

            pointModelsInternalList = loadXml(pointModelConf);

            for (PointModel p : pointModelsInternalList) {
                this.pointModelsInternalMap.put(p.getId(), p);
            }
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    private void loadUser() {
        this.pointModelsUserMap.clear();

        try {
            URL pointModelConf = this.urlReciverService.receiveFileUrl("/models/pointModelLayer.xml");

            List<PointModel> pointModelsList = loadXml(pointModelConf);

            for (PointModel p : pointModelsList) {
                this.pointModelsUserMap.put(p.getId(), p);
            }
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    public static  void saveXml(List<PointModel> pPointModelList, File file) throws JAXBException, FileNotFoundException {

        JAXBContext jaxbContext=JAXBContext.newInstance("kendzi.josm.kendzi3d.dto.xsd");
        Marshaller marshaller=jaxbContext.
               createMarshaller();

//        File file = new File("c:/jaxb2example.xml");
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );


        ObjectFactory factory=new ObjectFactory();

        PointModels pointModels=(factory.createPointModels());
//        PointModel pointModel = factory.createPointModel();
//        pointModel.setMatcher("matcher");

        pointModels.getPointModel().addAll(pPointModelList);

        JAXBElement<PointModels> gl =
                factory.createPointModels(pointModels );

        marshaller.marshal(gl, new FileOutputStream(file));

    }


    public static List<PointModel> loadXml(URL url) throws JAXBException, FileNotFoundException {

        JAXBContext jaxbContext=JAXBContext.newInstance("kendzi.josm.kendzi3d.dto.xsd");

//        Marshaller marshaller=jaxbContext.createMarshaller();
////        File file = new File("c:/jaxb2example.xml");
//        marshaller.setProperty("jaxb.formatted.output", true);
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

        Unmarshaller unmarshaller =
                jaxbContext.createUnmarshaller();
        JAXBElement<PointModels> c = (JAXBElement<PointModels>)
                unmarshaller.unmarshal(url);

        return c.getValue().getPointModel();
    }


    /**
     * @return the urlReciverService
     */
    public UrlReciverService getUrlReciverService() {
        return urlReciverService;
    }





//    public static void main(String[] args) throws FileNotFoundException, JAXBException {
//        PointModelService.saveXml();
//    }
}
