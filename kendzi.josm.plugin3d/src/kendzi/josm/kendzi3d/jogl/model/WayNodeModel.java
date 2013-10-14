/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

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
import kendzi.kendzi3d.expressions.functions.HeightFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dXFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dYFunction;
import kendzi.kendzi3d.expressions.functions.Vector3dZFunction;
import kendzi.kendzi3d.expressions.functions.WayNodeDirectionFunction;
import kendzi.math.geometry.point.Vector2dUtil;
import kendzi.util.StringUtil;

import org.apache.log4j.Logger;
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
    private static final Logger log = Logger.getLogger(WayNodeModel.class);

    /**
     * Model renderer.
     */
    private ModelRender modelRenderer;

    ModelCacheService modelCacheService;

    /**
     * Lod support.
     */
    private EnumMap<LOD, Model> modelLod;

    /**
     * Model scale;
     */
    private Vector3d scale;

    /**
     * Model translation.
     */
    private Vector3d translate;

    /**
     * Model configuration.
     */
    private WayNodeModelConf nodeModelConf;

    private List<ModelPoint> modelPoints;

    private List<Integer> nodeFilter;

    /** Constructor.
     * @param way way
     * @param nodeFilter selected nodes
     * @param wayNodeModelConf model configuration
     * @param perspective3D perspective 3d
     * @param modelRender
     * @param modelCacheService
     */
    public WayNodeModel(Way way, List<Integer> nodeFilter, WayNodeModelConf wayNodeModelConf, Perspective3D perspective3D,
            ModelRender modelRender, ModelCacheService modelCacheService) {

        super(way, perspective3D);

        this.nodeFilter = nodeFilter;

        this.modelLod = new EnumMap<LOD, Model>(LOD.class);

        this.scale = new Vector3d(1d, 1d, 1d);

        this.modelRenderer = modelRender;

        this.nodeModelConf = wayNodeModelConf;

        this.modelCacheService = modelCacheService;
    }

    @Override
    public void buildModel() {

        buildModel(LOD.LOD1);

        this.buildModel = true;
    }
    static class ModelPoint {
        Point3d point;
        double direction;

        public ModelPoint(Point3d point, double direction) {
            super();
            this.point = point;
            this.direction = direction;
        }
        /**
         * @return the point
         */
        public Point3d getPoint() {
            return point;
        }
        /**
         * @param point the point to set
         */
        public void setPoint(Point3d point) {
            this.point = point;
        }
        /**
         * @return the direction
         */
        public double getDirection() {
            return direction;
        }
        /**
         * @param direction the direction to set
         */
        public void setDirection(double direction) {
            this.direction = direction;
        }
    }
    @Override
    public void buildModel(LOD pLod) {

        Model model = getModel(this.nodeModelConf, pLod, this.modelCacheService);

        double scale = 1d;
        Context contextDouble = new Context();
        contextDouble.putVariable("osm", this.way);
        contextDouble.putVariable("osm_way", this.way);
        contextDouble.putVariable("osm_node", this.way);
        Double modelNormalFactor = getModelNormal(model);
        contextDouble.putVariable("normal", modelNormalFactor);

        kendzi.kendzi3d.expressions.Context c = new kendzi.kendzi3d.expressions.Context();

        c.getVariables().put("osm", this.way);
        c.getVariables().put("osm_way", this.way);
        c.getVariables().put("osm_node", this.way);
        c.getVariables().put("normal", modelNormalFactor);

        c.registerFunction(new HeightFunction());
        c.registerFunction(new WayNodeDirectionFunction());

        c.registerFunction(new Vector3dFunction());
        c.registerFunction(new Vector3dXFunction());
        c.registerFunction(new Vector3dYFunction());
        c.registerFunction(new Vector3dZFunction());



        double offset = ExpressiongBuilder.evaluateExpectedDouble(this.nodeModelConf.getOffset(), c, 0);

        boolean closed = way.isClosed();
        List<ModelPoint> modelPoints = new ArrayList<ModelPoint>();
        for (int i : nodeFilter) {
            Node node = way.getNode(i);

            Integer prev = getPrevious(i, way.getNodesCount(), closed);
            Integer next = getNext(i, way.getNodesCount(), closed);

            Point2d p = transform(node, perspective);

            Vector2d bisector = null;
            if (prev != null && next != null) {
                Point2d p1 = transform(way.getNode(prev), perspective);
                Point2d p2 = p;
                Point2d p3 = transform(way.getNode(next), perspective);

                bisector = getBisector(p1, p2, p3);

            } else if (prev != null) {
                Point2d p1 = transform(way.getNode(prev), perspective);
                Point2d p2 = p;
                bisector = getBisector(p1, p2);
            } else if (next != null) {
                Point2d p1 = p;
                Point2d p2 = transform(way.getNode(next), perspective);
                bisector = getBisector(p1, p2);
            }

            Point3d point = new Point3d(p.x, 0, -p.y);

            if (bisector != null) {
                bisector.normalize();
                point.x += bisector.x * offset;
                point.z += -bisector.y * offset;
            }

            c.getVariables().put("osm_way", this.way);
            c.getVariables().put("osm_node", node);
            c.getVariables().put("bisector", bisector);

            Double direction = ExpressiongBuilder.evaluateExpectedDouble(this.nodeModelConf.getDirection(), c, 0);

            modelPoints.add(new ModelPoint(point, direction));
        }


        this.modelPoints = modelPoints;




        try {
            scale = modelNormalFactor * ExpressiongBuilder.evaluateExpectedDouble(nodeModelConf.getScale(), c, 1);

        } catch (Exception e) {
            throw new RuntimeException("error eval of scale function", e);
        }




        this.scale = new Vector3d(scale, scale, scale);

        this.translate =  ExpressiongBuilder.evaluateExpectedDefault(nodeModelConf.getTranslate(), c, new Vector3d());

        this.modelLod.put(pLod, model);

    }

    private Point2d transform(Node node, Perspective3D perspective) {
        return perspective.calcPoint(node);
    }

    private Vector2d getBisector(Point2d p1, Point2d p2) {
        return Vector2dUtil.ortagonalRight(Vector2dUtil.fromTo(p1, p2));
    }

    private Vector2d getBisector(Point2d p1, Point2d p2, Point2d p3) {
        Vector2d v = Vector2dUtil.bisector(p1, p2 ,p3);
        v.negate();
        return v;
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
        return 1d / (pModel.getBounds().max.y - 0);//pModel.getBounds().min.y);
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

        if (this.modelLod.get(pLod) != null) {
            return true;
        }
        return false;
    }

    private static void setAmbientColor(Model pModel) {
        for (int i = 0; i < pModel.getNumberOfMaterials(); i++) {
            Material material = pModel.getMaterial(i);
//            material.ambientColor = material.diffuseColor;
            material.setAmbientDiffuse(new AmbientDiffuseComponent(
                    material.getAmbientDiffuse().getDiffuseColor(),
                    material.getAmbientDiffuse().getDiffuseColor()
                    ));
        }
    }



    @Override
    public void draw(GL2 gl, Camera camera, LOD pLod) {
        Model model2 = this.modelLod.get(pLod);
        if (model2 != null) {
            BarrierFence.enableTransparentText(gl);
            gl.glPushMatrix();
           // gl.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());
            gl.glEnable(GL2.GL_NORMALIZE); //XXX

            ;
            for (ModelPoint modelPoint : this.modelPoints)
            {
                gl.glPushMatrix();
                gl.glTranslated(
                         modelPoint.getPoint().x,
                        modelPoint.getPoint().y,
                         modelPoint.getPoint().z);

                gl.glScaled(this.scale.x, this.scale.y, this.scale.z);
                gl.glRotated(modelPoint.getDirection(), 0d, 1d, 0d);
                gl.glTranslated(this.translate.x, this.translate.y, this.translate.z);

                this.modelRenderer.render(gl, model2);
                gl.glPopMatrix();
            }

            gl.glDisable(GL2.GL_NORMALIZE);

            gl.glPopMatrix();
            BarrierFence.disableTransparentText(gl);
        }
    }

    @Override
    public void draw(GL2 gl, Camera camera) {
        draw(gl, camera, LOD.LOD1);
    }

    @Override
    public Point3d getPoint() {
        return new Point3d(this.x, 0, -this.y);
    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (this.modelLod.get(LOD.LOD1) == null) {
            buildModel(LOD.LOD1);
        }

        return Collections.singletonList(new ExportItem(this.modelLod.get(LOD.LOD1), new Point3d(this.getGlobalX(), 0, -this.getGlobalY()), new Vector3d(1,1,1)));
    }
}
