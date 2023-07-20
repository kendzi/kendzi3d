/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.material.AmbientDiffuseComponent;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.loader.ModelLoadException;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.layer.models.WayNodeModelConf;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.lod.DLODSuport;
import kendzi.josm.kendzi3d.jogl.model.lod.LOD;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractWayModel;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.josm.kendzi3d.util.expression.Context;
import kendzi.kendzi3d.expressions.ExpressiongBuilder;
import kendzi.kendzi3d.expressions.functions.DirectionFunction;
import kendzi.kendzi3d.expressions.functions.HeightFunction;
import kendzi.kendzi3d.expressions.functions.MinHeightFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dXFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dYFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dZFunction;
import kendzi.kendzi3d.expressions.functions.WayNodeDirectionFunction;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.math.geometry.point.Vector2dUtil;
import kendzi.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.opengl.GL11;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Model builder for objects loaded from obj files.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class WayNodeModel extends AbstractWayModel implements DLODSuport {

    /** Log. */
    private static final Logger log = LogManager.getLogger(WayNodeModel.class);

    /**
     * Model renderer.
     */
    private final ModelRender modelRenderer;

    ModelCacheService modelCacheService;

    /**
     * Lod support.
     */
    private final EnumMap<LOD, Model> modelLod;

    /**
     * Model scale;
     */
    private Vector3dc scale;

    /**
     * Model translation.
     */
    private Vector3dc translate;

    /**
     * Model configuration.
     */
    private final WayNodeModelConf nodeModelConf;

    private List<ModelPoint> modelPoints;

    private final List<Integer> nodeFilter;

    /**
     * Constructor.
     *
     * @param way
     *            way
     * @param nodeFilter
     *            selected nodes
     * @param wayNodeModelConf
     *            model configuration
     * @param Perspective
     *            perspective 3d
     * @param modelRender
     * @param modelCacheService
     */
    public WayNodeModel(Way way, List<Integer> nodeFilter, WayNodeModelConf wayNodeModelConf, Perspective Perspective,
            ModelRender modelRender, ModelCacheService modelCacheService) {

        super(way, Perspective);

        this.nodeFilter = nodeFilter;

        modelLod = new EnumMap<>(LOD.class);

        scale = new Vector3d(1d, 1d, 1d);

        modelRenderer = modelRender;

        nodeModelConf = wayNodeModelConf;

        this.modelCacheService = modelCacheService;
    }

    @Override
    public void buildWorldObject() {

        buildModel(LOD.LOD1);

        buildModel = true;
    }

    // XXX move
    static class ModelPoint {
        Vector3dc point;
        double direction;
        Vector2dc offsetVector;

        public ModelPoint(Vector3dc point, double direction, Vector2dc offsetVector) {
            super();
            this.point = point;
            this.direction = direction;
            this.offsetVector = offsetVector;
        }

        /**
         * @return the point
         */
        public Vector3dc getPoint() {
            return point;
        }

        /**
         * @param point
         *            the point to set
         */
        public void setPoint(Vector3dc point) {
            this.point = point;
        }

        /**
         * @return the direction
         */
        public double getDirection() {
            return direction;
        }

        /**
         * @param direction
         *            the direction to set
         */
        public void setDirection(double direction) {
            this.direction = direction;
        }

        public Vector2dc getOffsetVector() {
            return offsetVector;
        }

        public void setOffsetVector(Vector2dc offsetVector) {
            this.offsetVector = offsetVector;
        }
    }

    @Override
    public void buildModel(LOD pLod) {

        Model model = getModel(nodeModelConf, pLod, modelCacheService);

        double scale = 1d;
        Context contextDouble = new Context();
        contextDouble.putVariable("osm", way);
        contextDouble.putVariable("osm_way", way);
        contextDouble.putVariable("osm_node", way);
        Double modelNormalFactor = getModelNormal(model);
        contextDouble.putVariable("normal", modelNormalFactor);

        kendzi.kendzi3d.expressions.Context c = new kendzi.kendzi3d.expressions.Context();

        c.getVariables().put("osm", way);
        c.getVariables().put("osm_way", way);
        c.getVariables().put("osm_node", way);
        c.getVariables().put("normal", modelNormalFactor);

        c.registerFunction(new HeightFunction());
        c.registerFunction(new MinHeightFunction());
        c.registerFunction(new DirectionFunction());
        c.registerFunction(new WayNodeDirectionFunction());

        c.registerFunction(new Vector3dFunction());
        c.registerFunction(new Vector3dXFunction());
        c.registerFunction(new Vector3dYFunction());
        c.registerFunction(new Vector3dZFunction());

        double offset = ExpressiongBuilder.evaluateExpectedDouble(nodeModelConf.getOffset(), c, 0);

        boolean closed = way.isClosed();
        List<ModelPoint> modelPoints = new ArrayList<>();
        for (int i : nodeFilter) {
            Node node = way.getNode(i);

            Integer prev = getPrevious(i, way.getNodesCount(), closed);
            Integer next = getNext(i, way.getNodesCount(), closed);

            Vector2dc p = transform(node, perspective);

            Vector2d bisector = createBisector(prev, next, p);

            Vector3dc point = new Vector3d(p.x(), 0, -p.y());

            if (bisector != null) {
                bisector.normalize();
                // point.x() += bisector.x() * offset;
                // point.z() += -bisector.y() * offset;
            }

            c.getVariables().put("osm_way", way);
            c.getVariables().put("osm_node", node);
            c.getVariables().put("bisector", bisector);

            double direction = ExpressiongBuilder.evaluateExpectedDouble(nodeModelConf.getDirection(), c, 0);

            modelPoints.add(new ModelPoint(point, direction, bisector));
        }

        this.modelPoints = modelPoints;

        try {
            scale = modelNormalFactor * ExpressiongBuilder.evaluateExpectedDouble(nodeModelConf.getScale(), c, 1);

        } catch (Exception e) {
            throw new RuntimeException("error eval of scale function", e);
        }

        this.scale = new Vector3d(scale, scale, scale);

        Vector3d translateWithOffset = ExpressiongBuilder.evaluateExpectedDefault(nodeModelConf.getTranslate(), c,
                new Vector3d());
        translateWithOffset.z += offset;

        translate = translateWithOffset;

        modelLod.put(pLod, model);

    }

    private Vector2d createBisector(Integer prev, Integer next, Vector2dc p) {
        Vector2d bisector = null;
        if (prev != null && next != null) {
            Vector2dc p1 = transform(way.getNode(prev), perspective);
            Vector2dc p2 = p;
            Vector2dc p3 = transform(way.getNode(next), perspective);

            bisector = getBisector(p1, p2, p3);

        } else if (prev != null) {
            Vector2dc p1 = transform(way.getNode(prev), perspective);
            Vector2dc p2 = p;
            bisector = getBisector(p1, p2);
        } else if (next != null) {
            Vector2dc p1 = p;
            Vector2dc p2 = transform(way.getNode(next), perspective);
            bisector = getBisector(p1, p2);
        }
        return bisector;
    }

    private Vector2d transform(Node node, Perspective perspective) {
        return perspective.calcPoint(node);
    }

    private Vector2d getBisector(Vector2dc p1, Vector2dc p2) {
        return Vector2dUtil.orthogonalRight(Vector2dUtil.fromTo(p1, p2));
    }

    private Vector2d getBisector(Vector2dc p1, Vector2dc p2, Vector2dc p3) {
        return Vector2dUtil.bisector(p1, p2, p3).negate();
    }

    private Integer getNext(int i, int nodesCount, boolean closed) {
        if (i < nodesCount - 1) {
            return i + 1;
        }
        if (closed) {
            return 1;
        }
        return null;
    }

    private Integer getPrevious(int i, int nodesCount, boolean closed) {

        if (i != 0) {
            return i - 1;
        }

        if (closed) {
            return nodesCount - 2;
        }
        return null;
    }

    private double getModelNormal(Model pModel) {
        if (pModel == null) {
            return 1d;
        }
        // normalize from origin to highest point in model
        return 1d / (pModel.getBounds().max.y() - 0);// pModel.getBounds().min.y());
    }

    private static Model getModel(WayNodeModelConf nodeModelConf, LOD pLod, ModelCacheService modelCacheService) {
        if (nodeModelConf == null) {
            return null;
        }
        String key = nodeModelConf.getModel();
        String parameter = nodeModelConf.getModelParameter();

        try {
            Model loadModel = null;
            if (StringUtil.isBlankOrNull(parameter)) {
                loadModel = modelCacheService.loadModel(key);
            } else {
                loadModel = modelCacheService.generateModel(key, parameter);
            }
            loadModel.useLight = true;
            setAmbientColor(loadModel);
            return loadModel;

        } catch (ModelLoadException e) {
            log.error("error loading model file: " + key, e);
        }
        return null;
    }

    @Override
    public boolean isModelBuild(LOD pLod) {

        return modelLod.get(pLod) != null;
    }

    private static void setAmbientColor(Model pModel) {
        for (int i = 0; i < pModel.getNumberOfMaterials(); i++) {
            Material material = pModel.getMaterial(i);
            // material.ambientColor = material.diffuseColor;
            material.setAmbientDiffuse(new AmbientDiffuseComponent(material.getAmbientDiffuse().getDiffuseColor(),
                    material.getAmbientDiffuse().getDiffuseColor()));
        }
    }

    @Override
    public void draw(Camera camera, LOD pLod) {
        Model model2 = modelLod.get(pLod);
        if (model2 != null) {
            GL11.glPushMatrix();
            // GL11.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());
            GL11.glEnable(GL11.GL_NORMALIZE); // XXX

            for (ModelPoint modelPoint : modelPoints) {

                GL11.glPushMatrix();

                GL11.glTranslated(modelPoint.getPoint().x(), modelPoint.getPoint().y(), modelPoint.getPoint().z());
                // double cos = modelPoint.getOffsetVector().x();
                // double sin = -modelPoint.getOffsetVector().y();

                double cos = -modelPoint.getOffsetVector().y();
                double sin = -modelPoint.getOffsetVector().x();

                GL11.glMultMatrixd( //
                        new double[] { cos, 0, sin, 0, //
                                0, 1, 0, 0, //
                                -sin, 0, cos, 0, //
                                0, 0, 0, 1 });

                PointModel.drawDebug(translate, modelPoint.getDirection());

                GL11.glTranslated(translate.x(), translate.y(), translate.z());

                GL11.glScaled(scale.x(), scale.y(), scale.z());
                GL11.glRotated(modelPoint.getDirection(), 0d, 1d, 0d);

                modelRenderer.render(model2);
                GL11.glPopMatrix();
            }

            GL11.glDisable(GL11.GL_NORMALIZE);

            GL11.glPopMatrix();
        }
    }

    @Override
    public void draw(Camera camera, boolean selected) {
        draw(camera);
    }

    @Override
    public void draw(Camera camera) {
        draw(camera, LOD.LOD1);
    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (modelLod.get(LOD.LOD1) == null) {
            buildModel(LOD.LOD1);
        }

        return Collections.singletonList(
                new ExportItem(modelLod.get(LOD.LOD1), new Vector3d(getGlobalX(), 0, -getGlobalY()), new Vector3d(1, 1, 1)));
    }

    @Override
    public Model getModel() {
        return modelLod.get(LOD.LOD1);
    }

    @Override
    public Vector3dc getPosition() {
        return getPoint();
    }
}
