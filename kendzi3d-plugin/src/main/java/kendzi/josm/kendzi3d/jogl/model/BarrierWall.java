/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;

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
    public BarrierWall(Way pWay, Perspective perspective, ModelRender pModelRender,
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

        String wallType = getWallType(way);

        double wallHeight = metadataCacheService.getPropertitesDouble("barrier.wall_{0}.height", WALL_HEIGHT, wallType);

        hight = ModelUtil.getHeight(way, wallHeight);

        minHeight = ModelUtil.getMinHeight(way, 0d);

        // String wallColor = OsmAttributeKeys.COLOUR.primitiveValue(this.way);

        Color wallColor = takeWallColor(way);

        TextureData wallTexture = takeWallTexture(wallType, wallColor, way, textureLibraryStorageService);

        ModelFactory modelBuilder = ModelFactory.modelBuilder();

        MeshFactory meshBorder = BarrierFenceRelation.createMesh(wallTexture.getTex0(), wallColor, "wall_border",
                modelBuilder);

        BarrierFenceRelation.buildWallModel(points, null, minHeight, hight, 0, meshBorder, wallTexture);

        model = modelBuilder.toModel();
        model.setUseLight(true);
        model.setUseTexture(true);
        model.setUseTextureAlpha(true);
        model.setUseCullFaces(!PREFER_TWO_SIDED.get());
        model.setUseTwoSidedLighting(PREFER_TWO_SIDED.get());

        buildModel = true;

        heightClone = RelationCloneHeight.buildHeightClone(way);
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
    public void draw(GL2 gl, Camera camera, boolean selected) {
        draw(gl, camera);
    }

    @Override
    public void draw(GL2 gl, Camera camera) {

        gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);

        gl.glPushMatrix();
        gl.glTranslated(getGlobalX(), 0, -getGlobalY());

        try {
            modelRender.render(gl, model);

            for (RelationCloneHeight cloner : heightClone) {
                for (Double height : cloner) {

                    gl.glPushMatrix();
                    gl.glTranslated(0, height, 0);

                    modelRender.render(gl, model);
                    gl.glPopMatrix();

                }
            }

        } finally {

            gl.glPopMatrix();
        }

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
