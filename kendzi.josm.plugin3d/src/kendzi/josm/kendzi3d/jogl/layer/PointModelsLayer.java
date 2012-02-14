/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.layer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.model.Model;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.josm.kendzi3d.service.UrlReciverService;
import kendzi.josm.kendzi3d.util.expression.ModelScaleContext;
import kendzi.josm.kendzi3d.util.expression.SimpleDoubleExpressionParser;
import kendzi.josm.kendzi3d.util.expression.fun.SimpleFunction;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.actions.search.SearchCompiler;
import org.openstreetmap.josm.actions.search.SearchCompiler.Match;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Layer allow loading custom models.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class PointModelsLayer implements Layer {

    /** Log. */
    private static final Logger log = Logger.getLogger(PointModelsLayer.class);

    /**
     * List of layer models.
     */
    private List<Model> modelList = new ArrayList<Model>();

    /**
     * List of model definitions.
     */
    private List<PointModelConf> pointModelsList = new ArrayList<PointModelsLayer.PointModelConf>();

    /**
     * Model renderer.
     */
    //@Inject
    private ModelRender modelRender;

    //@Inject
    private ModelCacheService modelCacheService;

    //@Inject
    private UrlReciverService urlReciverService;

    /**
     * Constructor.
     */
    public PointModelsLayer() {
//        init();
    }

    /**
     * Model configuration.
     *
     * @author Tomasz Kędziora (kendzi)
     *
     */
    public static class PointModelConf {

        /**
         * Model file key.
         */
        private String model;

        /**
         * Model matcher.
         */
        private Match matcher;

        /**
         * Model scale.
         */
        private SimpleFunction scale;

        /**
         * Model translation.
         */
        private Vector3d translate;

        // direction

        /**
         * @return the model
         */
        public String getModel() {
            return this.model;
        }
        /**
         * @param model the model to set
         */
        public void setModel(String model) {
            this.model = model;
        }
        /**
         * @return the matcher
         */
        public Match getMatcher() {
            return this.matcher;
        }
        /**
         * @param matcher the matcher to set
         */
        public void setMatcher(Match matcher) {
            this.matcher = matcher;
        }
        /**
         * @return the scale
         */
        public SimpleFunction getScale() {
            return this.scale;
        }
        /**
         * @param simpleFunction the scale to set
         */
        public void setScale(SimpleFunction simpleFunction) {
            this.scale = simpleFunction;
        }
        /**
         * @return the translate
         */
        public Vector3d getTranslate() {
            return this.translate;
        }
        /**
         * @param translate the translate to set
         */
        public void setTranslate(Vector3d translate) {
            this.translate = translate;
        }
    }

    private static List<PointModelConf> parseXmlFile(String pFileUrl) {

        List<PointModelConf> ret = new ArrayList<PointModelConf>();

        // get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            Document dom = db.parse(pFileUrl);

            // get the root element
            Element docEle = dom.getDocumentElement();

            // get a nodelist of elements
            NodeList nl = docEle.getElementsByTagName("pointModel");

            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {

                    // get the employee element
                    try {
                        Element el = (Element) nl.item(i);

                        PointModelConf pm = new PointModelConf();
                        pm.setModel(el.getAttribute("model"));
                        pm.setMatcher(SearchCompiler.compile(el.getAttribute("matcher"), false, false));

                        pm.setTranslate(parseVector(el.getAttribute("translateX"), el.getAttribute("translateY"),
                                el.getAttribute("translateZ")));
                        String scale = el.getAttribute("scale");

                        pm.setScale(SimpleDoubleExpressionParser.compile(scale, new ModelScaleContext()));

                        ret.add(pm);
                    } catch (Exception e) {
                        log.error("cant parse point model xml config: " + pFileUrl + " element: " + nl, e);
                    }
                }
            }

        } catch (Exception e) {
            log.error("error parsing point model xml config: " + pFileUrl, e);
        }
        return ret;
    }

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
     * Initialize layer.
     */
    public void init() {
        this.pointModelsList = new ArrayList<PointModelConf>();
        try {

            URL pointModelConf = this.urlReciverService.reciveFileUrl("/models/pointModelLayer.xml");

            // XXX ?!@
            List<PointModelConf> parseXmlFile = parseXmlFile(pointModelConf.toString());

            this.pointModelsList = parseXmlFile;
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    /**
     * Or match on list.
     *
     * @author Tomasz Kędziora (kendzi)
     *
     */
    public static class OrList extends Match {
        private final List<Match> lhs;

        /**
         * @param lhs list of match
         */
        public OrList(List<Match> lhs) {
            this.lhs = lhs;
        }

        @Override
        public boolean match(OsmPrimitive osm) {
            for (Match m : this.lhs) {
                if (m.match(osm)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            String ret = "";
            for (Match m : this.lhs) {
                ret += " || " + m;
            }
            return ret;
        }

        /**
         * @return list of match
         */
        public List<Match> getLhs() {
            return this.lhs;
        }
    }

    @Override
    public
    Match getNodeMatcher() {
        List<Match> matchersList = new ArrayList<SearchCompiler.Match>();
        for (PointModelConf pointModel : this.pointModelsList) {

            matchersList.add(pointModel.matcher);
        }

        Match match = new OrList(matchersList);
        return match;
    }

    @Override
    public Match getWayMatcher() {
        return null;
    }

    @Override
    public Match getRelationMatcher() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public List<Model> getModels() {
        return this.modelList;
    }

    @Override
    public void addModel(Node pNode, Perspective3D pPerspective3D) {

        for (PointModelConf pointModel : this.pointModelsList) {

            if (pointModel.getMatcher().match(pNode)) {

                this.modelList.add(new kendzi.josm.kendzi3d.jogl.model.PointModel(pNode, pointModel, pPerspective3D, this.modelRender, this.modelCacheService));
            }
        }
    }

    @Override
    public void addModel(Way pWay, Perspective3D pPerspective3D) {
        //
    }

    @Override
    public void addModel(Relation pRelation, Perspective3D pPerspective3D) {
        //
    }

    @Override
    public void clear() {
        this.modelList.clear();
        this.pointModelsList.clear();
        init();
    }

    /**
     * @return the modelRender
     */
    public ModelRender getModelRender() {
        return this.modelRender;
    }

    /**
     * @param modelRender the modelRender to set
     */
    public void setModelRender(ModelRender modelRender) {
        this.modelRender = modelRender;
    }

    /**
     * @param modelCacheService the modelCacheService to set
     */
    public void setModelCacheService(ModelCacheService modelCacheService) {
        this.modelCacheService = modelCacheService;
    }

    /**
     * @param urlReciverService the urlReciverService to set
     */
    public void setUrlReciverService(UrlReciverService urlReciverService) {
        this.urlReciverService = urlReciverService;
    }

}
