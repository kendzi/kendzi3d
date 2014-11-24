/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.TextureLibraryKey;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.josm.model.polygon.PolygonWithHolesUtil;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangle2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.triangulate.Poly2TriSimpleUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Water model.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class Water extends AbstractModel {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Water.class);

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
     * Model of water.
     */
    private Model model;

    private OsmPrimitive primitive;

    /**
     * Constructor for water.
     *
     * @param primitive
     *            primitive represent water
     * @param perspective
     *            perspective
     * @param pModelRender
     * @param pMetadataCacheService
     * @param pTextureLibraryStorageService
     */
    public Water(OsmPrimitive primitive, Perspective perspective, ModelRender pModelRender,
            MetadataCacheService pMetadataCacheService, TextureLibraryStorageService pTextureLibraryStorageService) {

        super(perspective);

        modelRender = pModelRender;
        metadataCacheService = pMetadataCacheService;
        textureLibraryStorageService = pTextureLibraryStorageService;

        if (primitive instanceof Node) {
            throw new IllegalArgumentException("node is not supported");
        }

        this.primitive = primitive;
    }

    List<PolygonWithHolesList2d> getMultiPolygonWithHoles() {
        if (primitive instanceof Relation) {
            return getMultiPolygonWithHolesRelation((Relation) primitive, perspective);
        }
        return getMultiPolygonWithHolesWay((Way) primitive, perspective);
    }

    List<PolygonWithHolesList2d> getMultiPolygonWithHolesWay(Way way, Perspective perspective) {
        List<PolygonWithHolesList2d> ret = new ArrayList<PolygonWithHolesList2d>();

        List<Point2d> poly = new ArrayList<Point2d>();

        int size = way.getNodesCount();
        if (size > 0) {
            if (way.getNode(0).equals(way.getNode(way.getNodesCount() - 1))) {
                size--;
            }
            for (int i = 0; i < size; i++) {
                Point2d p = perspective.calcPoint(way.getNode(i));
                poly.add(p);
            }
            ret.add(new PolygonWithHolesList2d(new PolygonList2d(poly), null));
        }
        return ret;
    }

    List<PolygonWithHolesList2d> getMultiPolygonWithHolesRelation(Relation pRelation, Perspective perspective) {

        return PolygonWithHolesUtil.findPolygonsWithHoles(pRelation, perspective);
    }

    @Override
    public void buildWorldObject() {

        ModelFactory model = ModelFactory.modelBuilder();
        MeshFactory mesh = model.addMesh("water");

        TextureData waterTexture = getWaterTextureData();// new
        // TextureData("#c=#008EFF",
        // 1d, 1d);
        Material waterMaterial = MaterialFactory.createTextureMaterial(waterTexture.getTex0());
        int waterMaterialIndex = model.addMaterial(waterMaterial);

        mesh.materialID = waterMaterialIndex;
        mesh.hasTexture = true;

        Vector3d nt = new Vector3d(0, 1, 0);

        Point3d planeRightTopPoint = new Point3d(0, 0.05, 0);

        List<PolygonWithHolesList2d> polyList = getMultiPolygonWithHoles();

        for (PolygonWithHolesList2d poly : polyList) {

            List<Triangle2d> triangles = Poly2TriSimpleUtil.triangulate(poly);

            Plane3d planeTop = new Plane3d(planeRightTopPoint, nt);

            Vector3d roofTopLineVector = new Vector3d(-1, 0, 0);

            MeshFactoryUtil.addPolygonToRoofMesh(mesh, triangles, planeTop, roofTopLineVector, waterTexture, 0, 0);
        }

        this.model = model.toModel();
        this.model.setUseLight(true);
        this.model.setUseTexture(true);

        buildModel = true;
    }

    @Override
    public void draw(GL2 gl, Camera camera, boolean selected) {
        draw(gl, camera);
    }

    @Override
    public void draw(GL2 pGl, Camera camera) {

        pGl.glPushMatrix();
        pGl.glTranslated(getGlobalX(), 0, -getGlobalY());

        try {
            modelRender.render(pGl, model);

        } finally {
            pGl.glPopMatrix();
        }
    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (model == null) {
            buildWorldObject();
        }

        return Collections
                .singletonList(new ExportItem(model, new Point3d(getGlobalX(), 0, -getGlobalY()), new Vector3d(1, 1, 1)));
    }

    @Override
    public Model getModel() {
        return model;
    }

    public TextureData getWaterTextureData() {

        String keyStr = textureLibraryStorageService.getKey(TextureLibraryKey.WATER, (String) null);
        return textureLibraryStorageService.getTextureDefault(keyStr);

    }

    @Override
    public Point3d getPosition() {
        return getPoint();
    }

    @Override
    public void rebuildWorldObject(OsmPrimitive primitive, Perspective perspective) {
        // clean up everything
        this.primitive = primitive;
        this.perspective = perspective;

        buildWorldObject();
    }
}
