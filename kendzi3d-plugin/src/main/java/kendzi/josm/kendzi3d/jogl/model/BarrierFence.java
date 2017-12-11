/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openstreetmap.josm.data.osm.Way;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractWayModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.kendzi3d.josm.model.clone.RelationCloneHeight;
import kendzi.kendzi3d.josm.model.perspective.Perspective;

/**
 * Fence for shapes defined as way.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class BarrierFence extends AbstractWayModel {

    private static final java.lang.Double FENCE_HEIGHT = 1d;

    /**
     * Hight.
     */
    private double hight;

    /**
     * Min height.
     */
    private double minHeight;

    /**
     * Model of building.
     */
    private Model model;

    /**
     * Height cloner.
     */
    private List<RelationCloneHeight> heightClone;

    /**
     * Renderer of model.
     */
    private final ModelRender modelRender;

    /**
     * Metadata cache service.
     */
    private final MetadataCacheService metadataCacheService;

    /**
     * Texture library service.
     */
    private final TextureLibraryStorageService textureLibraryStorageService;

    /**
     * Fence constructor.
     *
     * @param pWay
     *            way
     * @param perspective
     *            perspective
     * @param pModelRender
     *            model render
     * @param pMetadataCacheService
     *            metadata cache service
     * @param pTextureLibraryStorageService
     *            texture library service
     */
    public BarrierFence(Way pWay, Perspective perspective, ModelRender pModelRender,
            MetadataCacheService pMetadataCacheService, TextureLibraryStorageService pTextureLibraryStorageService) {
        super(pWay, perspective);

        modelRender = pModelRender;
        metadataCacheService = pMetadataCacheService;
        textureLibraryStorageService = pTextureLibraryStorageService;
    }

    @Override
    public void buildWorldObject() {

        if (!(points.size() > 1)) {
            return;
        }

        String fenceType = BarrierFenceRelation.getFenceType(way);

        double fenceHeight = metadataCacheService.getPropertitesDouble("barrier.fence_{0}.height", FENCE_HEIGHT,
                fenceType);

        hight = ModelUtil.getHeight(way, fenceHeight);

        minHeight = ModelUtil.getMinHeight(way, 0d);

        TextureData facadeTexture = BarrierFenceRelation.getFenceTexture(fenceType, way, textureLibraryStorageService);

        ModelFactory modelBuilder = ModelFactory.modelBuilder();

        MeshFactory meshBorder = BarrierFenceRelation.createMesh(facadeTexture.getTex0(), null, "fence_border",
                modelBuilder);

        BarrierFenceRelation.buildWallModel(points, null, minHeight, hight, 0, meshBorder, facadeTexture);

        model = modelBuilder.toModel();
        model.setUseLight(true);
        model.setUseTexture(true);
        model.setUseTextureAlpha(true);

        buildModel = true;

        heightClone = RelationCloneHeight.buildHeightClone(way);
    }

    @Override
    public void draw(GL2 gl, Camera camera, boolean selected) {
        draw(gl, camera);
    }

    @Override
    public void draw(GL2 gl, Camera camera) {

        // replace the quad colors with the texture
        // gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE,
        // GL2.GL_REPLACE);
        gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);

        gl.glEnable(GL.GL_CULL_FACE);
        gl.glPushMatrix();
        gl.glTranslated(getGlobalX(), 0, -getGlobalY());

        modelRender.render(gl, model);

        for (RelationCloneHeight cloner : heightClone) {
            for (Double height : cloner) {

                gl.glPushMatrix();
                gl.glTranslated(0, height, 0);
                modelRender.render(gl, model);
                gl.glPopMatrix();
            }
        }

        gl.glPopMatrix();
        gl.glDisable(GL.GL_CULL_FACE);

    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (model == null) {
            buildWorldObject();
        }

        return Collections.singletonList(new ExportItem(model, new Point3d(getGlobalX(), 0, -getGlobalY()),
                new Vector3d(1, 1, 1)));
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public Point3d getPosition() {
        return getPoint();
    }
}
