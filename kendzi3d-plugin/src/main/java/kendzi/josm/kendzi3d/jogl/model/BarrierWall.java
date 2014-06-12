/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.OsmBuildingElementsTextureMenager;
import kendzi.jogl.texture.library.TextureLibraryKey;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractWayModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.util.ColorUtil;
import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.kendzi3d.josm.model.attribute.OsmAttributeKeys;
import kendzi.kendzi3d.josm.model.clone.RelationCloneHeight;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.util.StringUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Fence for shapes defined as way.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class BarrierWall extends AbstractWayModel {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(BarrierWall.class);

    private static final java.lang.Double WALL_HEIGHT = 1d;

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
    private ModelRender modelRender;

    /**
     * Metadata cache service.
     */
    private MetadataCacheService metadataCacheService;

    /**
     * Texture library service.
     */
    private TextureLibraryStorageService textureLibraryStorageService;

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
    public BarrierWall(Way pWay, Perspective perspective, ModelRender pModelRender, MetadataCacheService pMetadataCacheService,
            TextureLibraryStorageService pTextureLibraryStorageService) {
        super(pWay, perspective);

        this.modelRender = pModelRender;
        this.metadataCacheService = pMetadataCacheService;
        this.textureLibraryStorageService = pTextureLibraryStorageService;
    }

    @Override
    public void buildWorldObject() {

        if (!(this.points.size() > 1)) {
            return;
        }

        String wallType = getWallType(this.way);

        double wallHeight = this.metadataCacheService.getPropertitesDouble("barrier.wall_{0}.height", WALL_HEIGHT, wallType);

        this.hight = ModelUtil.getHeight(this.way, wallHeight);

        this.minHeight = ModelUtil.getMinHeight(this.way, 0d);

        // String wallColor = OsmAttributeKeys.COLOUR.primitiveValue(this.way);

        Color wallColor = takeWallColor(this.way);

        TextureData wallTexture = takeWallTexture(wallType, wallColor, this.way, this.textureLibraryStorageService);

        ModelFactory modelBuilder = ModelFactory.modelBuilder();

        MeshFactory meshBorder = BarrierFenceRelation.createMesh(wallTexture.getTex0(), wallColor, "wall_border", modelBuilder);

        BarrierFenceRelation.buildWallModel(this.points, null, this.minHeight, this.hight, 0, meshBorder, wallTexture);

        this.model = modelBuilder.toModel();
        this.model.setUseLight(true);
        this.model.setUseTexture(true);

        this.buildModel = true;

        this.heightClone = RelationCloneHeight.buildHeightClone(this.way);
    }

    private Color takeWallColor(Way way) {

        String wallColor = OsmAttributeKeys.COLOUR.primitiveValue(this.way);
        if (StringUtil.isBlankOrNull(wallColor)) {
            wallColor = OsmAttributeKeys.COLOR.primitiveValue(this.way);
        }

        if (!StringUtil.isBlankOrNull(wallColor)) {
            return ColorUtil.parseColor(wallColor);
        }
        return null;
    }

    /**
     * Gets wall type.
     * 
     * @param osmPrimitive
     *            osm primitive
     * @return fence type
     */
    private static String getWallType(OsmPrimitive osmPrimitive) {
        return OsmAttributeKeys.WALL.primitiveValue(osmPrimitive);
    }

    /**
     * Gets wall texture data.
     * 
     * @param wallType
     *            wall type
     * @param wallColor
     *            wall color
     * @param osmPrimitive
     *            primitive
     * @param TextureLibraryStorageService
     *            texture library service
     * @return texture data
     */
    public static TextureData takeWallTexture(String wallType, Color wallColor, OsmPrimitive osmPrimitive,
            TextureLibraryStorageService TextureLibraryStorageService) {

        String textureKey = TextureLibraryStorageService.getKey(TextureLibraryKey.BARRIER_WALL, wallType);

        TextureData textureData = TextureLibraryStorageService.getTextureDefault(textureKey);

        if (!Boolean.FALSE.equals(textureData.isColorable()) && wallColor != null) {
            return OsmBuildingElementsTextureMenager.colorableTextureData(textureData);
        }

        return textureData;
    }

    @Override
    public void draw(GL2 pGl, Camera pCamera) {

        BarrierFence.enableTransparentText(pGl);

        pGl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);

        pGl.glEnable(GL.GL_CULL_FACE);

        pGl.glPushMatrix();
        pGl.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());

        try {
            this.modelRender.render(pGl, this.model);

            for (RelationCloneHeight cloner : this.heightClone) {
                for (Double height : cloner) {

                    pGl.glPushMatrix();
                    pGl.glTranslated(0, height, 0);

                    this.modelRender.render(pGl, this.model);
                    pGl.glPopMatrix();

                }
            }

        } finally {

            pGl.glPopMatrix();

            pGl.glDisable(GL.GL_CULL_FACE);
        }

        BarrierFence.disableTransparentText(pGl);

    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (this.model == null) {
            buildWorldObject();
        }

        return Collections.singletonList(new ExportItem(this.model, new Point3d(this.getGlobalX(), 0, -this.getGlobalY()),
                new Vector3d(1, 1, 1)));
    }

    @Override
    public Model getModel() {
        return model;
    }
}
