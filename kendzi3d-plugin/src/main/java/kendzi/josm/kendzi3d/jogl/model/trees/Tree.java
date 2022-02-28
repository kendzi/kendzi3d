/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.trees;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.geometry.Bounds;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.material.AmbientDiffuseComponent;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.loader.ModelLoadException;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.lod.DLODSuport;
import kendzi.josm.kendzi3d.jogl.model.lod.LOD;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractPointModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

/**
 * Tree for nodes.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class Tree extends AbstractPointModel implements DLODSuport {

    /** Log. */
    private static final Logger log = Logger.getLogger(Tree.class);

    /**
     * Renderer of model.
     */
    private final ModelRender modelRender;

    /**
     * Model cache service
     */
    private final ModelCacheService modelCacheService;

    /**
     * Metadata cache service.
     */
    private final MetadataCacheService metadataCacheService;

    float[] verts;

    private final EnumMap<LOD, Model> modelLod;
    private String type;
    private String genus;
    private String species;

    Vector3d scale;

    private double minHeight;

    /**
     * @param node
     *            node
     * @param perspective
     *            perspective
     * @param pModelRender
     *            model render
     * @param pMetadataCacheService
     *            metadata cache service
     * @param pModelCacheService
     *            model cache service
     */
    public Tree(Node node, Perspective perspective, ModelRender pModelRender, MetadataCacheService pMetadataCacheService,
            ModelCacheService pModelCacheService) {

        super(node, perspective);

        modelLod = new EnumMap<LOD, Model>(LOD.class);

        scale = new Vector3d(1d, 1d, 1d);

        modelRender = pModelRender;
        metadataCacheService = pMetadataCacheService;
        modelCacheService = pModelCacheService;

    }

    @Override
    public void buildWorldObject() {

        buildModel(LOD.LOD1);

        buildModel = true;
    }

    @Override
    public void buildModel(LOD pLod) {

        type = node.get("type");
        if (type == null) {
            type = "unknown";
        }
        genus = node.get("genus");
        species = node.get("species");

        Double maxHeight = getMaxHeight(node, species, genus, type, metadataCacheService);
        minHeight = getMinHeight(node, species, genus, type, metadataCacheService);

        if (maxHeight == null) {
            double height = getHeight(node, species, genus, type, metadataCacheService);
            maxHeight = minHeight + height;
        }

        Model model = null;

        model = findSimpleModel(species, genus, type, pLod, metadataCacheService, modelCacheService);

        setupScale(model, maxHeight, minHeight);

        modelLod.put(pLod, model);
    }

    private void setupScale(Model model2, double maxHeight, double minHeight) {

        double height = maxHeight - minHeight;

        Bounds bounds = model2.getBounds();

        double modelHeight = bounds.max.y;

        double modelScaleHeight = height / modelHeight;

        double modelScaleWidht = modelScaleHeight;

        scale.x = modelScaleWidht;
        scale.y = modelScaleHeight;
        scale.z = modelScaleWidht;

    }

    /**
     * Finds simple model for tree. Order of finding is: - species - genus - type
     * 
     * @param species
     *            tree specius
     * @param genus
     *            tree genus
     * @param type
     *            tree type
     * @param pLod
     *            lod level
     * @param metadataCacheService
     * @param modelCacheService
     * 
     * @return model
     */
    public static Model findSimpleModel(String species, String genus, String type, LOD pLod,
            MetadataCacheService metadataCacheService, ModelCacheService modelCacheService) {
        // let go
        String model = null;

        String spacesModel = metadataCacheService.getPropertites("models.trees.species.{0}.{1}.model", null, species, "" + pLod);
        String genusModel = metadataCacheService.getPropertites("models.trees.genus.{0}.{1}.model", null, genus, "" + pLod);
        String typeModel = metadataCacheService.getPropertites("models.trees.type.{0}.{1}.model", null, type, "" + pLod);
        String unknownModel = metadataCacheService.getPropertites("models.trees.type.{0}.{1}.model", null, null, "" + pLod);

        // XXX add StringUtil
        if (spacesModel != null) {
            model = spacesModel;
        } else if (genusModel != null) {
            model = genusModel;
        } else if (typeModel != null) {
            model = typeModel;
        } else {
            model = unknownModel;
        }

        if (model != null && !"#flat".equals(model)) {
            try {
                Model loadModel = modelCacheService.loadModel(model);
                loadModel.useLight = true;
                setAmbientColor(loadModel);
                return loadModel;

            } catch (ModelLoadException e) {
                log.error(e, e);
            }
        }

        return null;
    }

    /**
     * Finds height for tree. Order of finding is: - node attribute - species -
     * genus - type
     * 
     * @param node
     * @param species
     * @param genus
     * @param type
     * @param metadataCacheService
     * 
     * @return height
     */
    public static double getHeight(OsmPrimitive node, String species, String genus, String type,
            MetadataCacheService metadataCacheService) {

        Double height = 1d;

        Double nodeHeight = ModelUtil.getObjHeight(node, null);

        Double spacesHeight = metadataCacheService.getPropertitesDouble("models.trees.species.{0}.height", null, species);
        Double genusHeight = metadataCacheService.getPropertitesDouble("models.trees.genus.{0}.height", null, genus);
        Double typeHeight = metadataCacheService.getPropertitesDouble("models.trees.type.{0}.height", null, type);
        Double unknownHeight = metadataCacheService.getPropertitesDouble("models.trees.type.{0}.height", null, (String) null);

        // XXX add StringUtil
        if (nodeHeight != null) {
            height = nodeHeight;
        } else if (spacesHeight != null) {
            height = spacesHeight;
        } else if (genusHeight != null) {
            height = genusHeight;
        } else if (typeHeight != null) {
            height = typeHeight;
        } else {
            height = unknownHeight;
        }

        if (height == null) {
            height = 1d;
        }

        return height;
    }

    public static Double getMaxHeight(OsmPrimitive node, String species, String genus, String type,
            MetadataCacheService metadataCacheService) {
        return ModelUtil.getHeight(node, null);
    }

    public static double getMinHeight(OsmPrimitive node, String species, String genus, String type,
            MetadataCacheService metadataCacheService) {
        return ModelUtil.getMinHeight(node, 0d);
    }

    @Override
    public boolean isModelBuild(LOD pLod) {

        if (modelLod.get(pLod) != null) {
            return true;
        }
        return false;
    }

    private static void setAmbientColor(Model pModel) {
        for (int i = 0; i < pModel.getNumberOfMaterials(); i++) {
            Material material = pModel.getMaterial(i);

            material.setAmbientDiffuse(new AmbientDiffuseComponent(material.getAmbientDiffuse().getDiffuseColor(),
                    material.getAmbientDiffuse().getDiffuseColor()));
        }
    }

    @Override
    public void draw(GL2 gl, Camera camera, LOD pLod) {
        Model model2 = modelLod.get(pLod);
        if (model2 != null) {

            gl.glPushMatrix();
            gl.glTranslated(getGlobalX(), minHeight, -getGlobalY());

            gl.glEnable(GLLightingFunc.GL_NORMALIZE);
            gl.glScaled(scale.x, scale.y, scale.z);

            modelRender.render(gl, model2);

            gl.glDisable(GLLightingFunc.GL_NORMALIZE);

            // rotate in the opposite direction to the camera

            gl.glPopMatrix();

        }
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
