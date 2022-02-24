/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import com.jogamp.opengl.GL2;

import java.awt.*;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.glu.GLU;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.material.AmbientDiffuseComponent;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.loader.ModelLoadException;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.util.DrawUtil;
import kendzi.josm.kendzi3d.jogl.compas.CompassDrawer;
import kendzi.josm.kendzi3d.jogl.layer.models.NodeModelConf;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.lod.DLODSuport;
import kendzi.josm.kendzi3d.jogl.model.lod.LOD;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractPointModel;
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
import kendzi.util.StringUtil;
import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.openstreetmap.josm.data.osm.Node;

/**
 * Model builder for objects loaded from obj files.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class PointModel extends AbstractPointModel implements DLODSuport {

    /** Log. */
    private static final Logger log = Logger.getLogger(PointModel.class);

    public static boolean debug;

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
    private Vector3d scale;

    /**
     * Model translation.
     */
    private Vector3d translate;

    /**
     * Model configuration.
     */
    private final NodeModelConf nodeModelConf;

    private double rotateY;

    /**
     * Constructor.
     *
     * @param node
     *            node
     * @param pNodeModelConf
     *            model configuration
     * @param perspective
     *            perspective 3d
     */
    public PointModel(Node node, NodeModelConf pNodeModelConf, Perspective perspective, ModelRender pModelRender,
            ModelCacheService modelCacheService) {
        super(node, perspective);

        modelLod = new EnumMap<>(LOD.class);

        scale = new Vector3d(1d, 1d, 1d);

        modelRenderer = pModelRender;

        nodeModelConf = pNodeModelConf;
        this.modelCacheService = modelCacheService;
    }

    @Override
    public void buildWorldObject() {

        buildModel(LOD.LOD1);

        buildModel = true;
    }

    @Override
    public void buildModel(LOD pLod) {

        Model model = getModel(nodeModelConf, pLod, modelCacheService);

        kendzi.kendzi3d.expressions.Context c = new kendzi.kendzi3d.expressions.Context();

        c.getVariables().put("osm", node);
        c.getVariables().put("osm_node", node);
        Double modelNormalFactor = getModelNormal(model);
        c.getVariables().put("normal", modelNormalFactor);

        c.registerFunction(new HeightFunction());
        c.registerFunction(new MinHeightFunction());
        c.registerFunction(new DirectionFunction());
        c.registerFunction(new WayNodeDirectionFunction());

        c.registerFunction(new Vector3dFunction());
        c.registerFunction(new Vector3dXFunction());
        c.registerFunction(new Vector3dYFunction());
        c.registerFunction(new Vector3dZFunction());

        Context context = new Context();
        context.putVariable("osm", node);
        context.putVariable("normal", modelNormalFactor);

        double scale = 1d;

        try {
            scale = modelNormalFactor * ExpressiongBuilder.evaluateExpectedDouble(nodeModelConf.getScale(), c, 1);

        } catch (Exception e) {
            throw new RuntimeException("error eval of scale function", e);
        }

        this.scale = new Vector3d(scale, scale, scale);

        translate = ExpressiongBuilder.evaluateExpectedDefault(nodeModelConf.getTranslate(), c, new Vector3d());

        rotateY = ExpressiongBuilder.evaluateExpectedDouble(nodeModelConf.getDirection(), c, 180);

        modelLod.put(pLod, model);
    }

    private Double getModelNormal(Model pModel) {
        if (pModel == null) {
            return null;
        }
        return 1d / (pModel.getBounds().max.y - 0);// pModel.getBounds().min.y);
    }

    private static Model getModel(NodeModelConf nodeModelConf, LOD pLod, ModelCacheService modelCacheService) {
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
            loadModel.setUseLight(true);
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
    public void draw(GL2 gl, Camera camera, LOD pLod) {
        //
        Model model2 = modelLod.get(pLod);
        if (model2 != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(getGlobalX(), 0, -getGlobalY());
            drawDebug(translate, 0);

            GL11.glTranslated(translate.x, translate.y, translate.z);

            GL11.glEnable(GL11.GL_NORMALIZE); // XXX
            GL11.glScaled(scale.x, scale.y, scale.z);
            GL11.glRotated(rotateY, 0d, 1d, 0d);

            modelRenderer.render(gl, model2);

            GL11.glDisable(GL11.GL_NORMALIZE);

            GL11.glPopMatrix();
        }
    }

    public static void drawDebug(Vector3d translate, double direction) {

        if (!debug) {
            return;
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        float[] colorArrays = new float[4];
        Color color = Color.ORANGE.darker();
        GL11.glColor3fv(color.getRGBComponents(colorArrays));

        GLU.gluSphere(0.3f, 9, 9);

        DrawUtil.drawLine(0, 0, 0, //
                translate.x, translate.y, translate.z);

        // bottom Y
        GL11.glPushMatrix();
        GL11.glColor3fv(CompassDrawer.Y_AXIS_COLOR.getRGBComponents(colorArrays));

        DrawUtil.drawLine(translate.x, 0, translate.z, //
                translate.x, translate.y, translate.z);

        GL11.glTranslated(translate.x, 0.15, translate.z);

        DrawUtil.drawDotY(0.3, 9);

        GL11.glPopMatrix();

        // back X
        GL11.glPushMatrix();

        GL11.glColor3fv(CompassDrawer.X_AXIS_COLOR.getRGBComponents(colorArrays));

        DrawUtil.drawLine(0, translate.y, translate.z, //
                translate.x, translate.y, translate.z);

        GL11.glTranslated(0, translate.y, translate.z);

        // GL11.glRotated(90, 0d, 0d, 1d);

        DrawUtil.drawDotY(0.3, 9);

        // XXX

        GL11.glPopMatrix();

        // right Z
        GL11.glPushMatrix();

        GL11.glColor3fv(CompassDrawer.Z_AXIS_COLOR.getRGBComponents(colorArrays));

        DrawUtil.drawLine(translate.x, translate.y, 0, //
                translate.x, translate.y, translate.z);

        GL11.glTranslated(translate.x, translate.y, 0);

        GL11.glRotated(90, 1d, 0d, 0d);

        DrawUtil.drawDotY(0.3, 9);

        GL11.glPopMatrix();

        // model center
        GL11.glPushMatrix();
        GL11.glColor3fv(color.darker().getRGBComponents(colorArrays));

        GL11.glTranslated(translate.x, translate.y, translate.z);

        // GL11.glRotated(90, 0d, 1d, 0d);

        GLU.gluSphere(0.3f, 9, 9);
        // drawYArrow
        double scale = 2;

        GL11.glRotated(direction, 0d, 1d, 0d);

        DrawUtil.drawFlatArrowY(scale * 1.2, scale * 0.3, scale * 0.11, scale * 0.3);

        GL11.glPopMatrix();

    }

    @Override
    public void draw(GL2 gl, Camera camera, boolean selected) {
        draw(gl, camera);
    }

    @Override
    public void draw(GL2 gl, Camera camera) {
        draw(gl, camera, LOD.LOD1);
    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (modelLod.get(LOD.LOD1) == null) {
            buildModel(LOD.LOD1);
        }

        return Collections.singletonList(
                new ExportItem(modelLod.get(LOD.LOD1), new Point3d(getGlobalX(), 0, -getGlobalY()), new Vector3d(1, 1, 1)));
    }

    @Override
    public Model getModel() {
        return modelLod.get(LOD.LOD1);
    }

    @Override
    public Point3d getPosition() {
        return getPoint();
    }
}
