/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.TextureLibraryKey;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractRelationModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.kendzi3d.josm.model.attribute.OsmAttributeKeys;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.util.StringUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;

/**
 * Fence for shapes defined as relation.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class BarrierFenceRelation extends AbstractRelationModel {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(BarrierFenceRelation.class);

    private static final java.lang.Double FENCE_HEIGHT = 1d;

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

    private List<Point2d> points;

    private List<Double> heights;

    private List<Node> nodes;

    /**
     * Fence constructor.
     * 
     * @param pRelation
     *            way
     * @param pers
     *            Perspective
     * @param pModelRender
     *            model render
     * @param pMetadataCacheService
     *            metadata cache service
     * @param pTextureLibraryStorageService
     *            texture library service
     */
    public BarrierFenceRelation(Relation pRelation, Perspective pers, ModelRender pModelRender,
            MetadataCacheService pMetadataCacheService, TextureLibraryStorageService pTextureLibraryStorageService) {

        super(pRelation, pers);

        List<Double> heights = new ArrayList<Double>();
        List<Node> nodes = new ArrayList<Node>();
        for (int i = 0; i < pRelation.getMembersCount(); i++) {
            RelationMember member = pRelation.getMember(i);

            Node node = member.getNode();
            String role = member.getRole();

            if (node != null) {
                nodes.add(node);

                Double parseHeight = ModelUtil.parseHeight(role, 0d);
                heights.add(parseHeight);
            }
        }

        calcModelCenter(nodes);

        List<Point2d> points = new ArrayList<Point2d>();
        for (Node node : nodes) {
            Point2d point2d = toModelFrame(node);
            points.add(point2d);
        }

        this.nodes = nodes;
        this.points = points;
        this.heights = heights;
        this.modelRender = pModelRender;
        this.metadataCacheService = pMetadataCacheService;
        this.textureLibraryStorageService = pTextureLibraryStorageService;
    }

    /**
     * {@inheritDoc}
     * 
     * @see kendzi.josm.kendzi3d.jogl.model.AbstractModel#getOsmPrimitives()
     */
    @Override
    public Set<OsmPrimitive> getOsmPrimitives() {

        HashSet<OsmPrimitive> set = new HashSet<OsmPrimitive>();

        set.addAll(this.nodes);

        return set;
    }

    @Override
    public void buildWorldObject() {

        if (!(this.points.size() > 1)) {
            // FIXME
            this.model = new Model();
            this.buildModel = true;
            return;
        }

        String fenceType = getFenceType(this.relation);

        double fenceHeight = metadataCacheService.getPropertitesDouble("barrier.fence_{0}.height", FENCE_HEIGHT, fenceType);

        this.hight = ModelUtil.getHeight(this.relation, fenceHeight);

        this.minHeight = ModelUtil.getMinHeight(this.relation, 0d);

        TextureData facadeTexture = getFenceTexture(fenceType, this.relation, this.textureLibraryStorageService);

        ModelFactory modelBuilder = ModelFactory.modelBuilder();

        MeshFactory meshBorder = createMesh(facadeTexture.getTex0(), null, "fence_border", modelBuilder);

        buildWallModel(this.points, this.heights, this.minHeight, this.hight, 0, meshBorder, facadeTexture);

        this.model = modelBuilder.toModel();
        this.model.setUseLight(true);
        this.model.setUseTexture(true);

        this.buildModel = true;
    }

    /**
     * @param tex0Key
     * @param textColor
     * @param meshName
     * @param modelBuilder
     * @return
     */
    public static MeshFactory createMesh(String tex0Key, Color textColor, String meshName, ModelFactory modelBuilder) {
        MeshFactory meshBorder = modelBuilder.addMesh(meshName);

        // Material fenceMaterial = MaterialFactory.createTextureMaterial(tex0);

        Material mat = MaterialFactory.createTextureColorMaterial(tex0Key, textColor);

        int facadeMaterialIndex = modelBuilder.addMaterial(mat);

        meshBorder.materialID = facadeMaterialIndex;
        meshBorder.hasTexture = true;
        return meshBorder;
    }

    /**
     * Build wall model. XXX move to util.
     * 
     * @param pPoints
     *            wall points
     * @param pHeights
     *            wall node height
     * @param pMinHeight
     *            wall min height
     * @param pHeight
     *            wall height
     * @param pWidth
     *            wall width
     * @param pMeshBorder
     *            mesh
     * @param pWallTexture
     *            texture
     */
    public static void buildWallModel(List<Point2d> pPoints, List<Double> pHeights, double pMinHeight, double pHeight,
            double pWidth, MeshFactory pMeshBorder, TextureData pWallTexture

    ) {
        FaceFactory faceRight = pMeshBorder.addFace(FaceType.QUADS);
        FaceFactory faceLeft = pMeshBorder.addFace(FaceType.QUADS);

        Point2d start = pPoints.get(0);
        Double startHeight = getHeight(0, pHeights);

        int startMi = pMeshBorder.addVertex(new Point3d(start.x, startHeight + pMinHeight, -start.y));
        int startHi = pMeshBorder.addVertex(new Point3d(start.x, startHeight + pHeight, -start.y));

        TextCoord bm = new TextCoord(0, 0);
        TextCoord bh = new TextCoord(0, 1);

        int bmi = pMeshBorder.addTextCoord(bm);
        int bhi = pMeshBorder.addTextCoord(bh);

        for (int i = 1; i < pPoints.size(); i++) {
            Point2d end = pPoints.get(i);
            Double endHeight = getHeight(i, pHeights);

            int endMi = pMeshBorder.addVertex(new Point3d(end.x, endHeight + pMinHeight, -end.y));
            int endHi = pMeshBorder.addVertex(new Point3d(end.x, endHeight + pHeight, -end.y));

            Vector3d normal = new Vector3d(end.y - start.y, 0, end.x - start.x);
            normal.normalize();

            int n1i = pMeshBorder.addNormal(normal);

            Vector3d normal2 = new Vector3d(-normal.x, -normal.y, -normal.z);

            int n2i = pMeshBorder.addNormal(normal2);

            double dist = start.distance(end);
            double uvEnd = (int) (dist / pWallTexture.getWidth());

            TextCoord em = new TextCoord(uvEnd, 0);
            TextCoord eh = new TextCoord(uvEnd, 1);

            int emi = pMeshBorder.addTextCoord(em);
            int ehi = pMeshBorder.addTextCoord(eh);

            faceRight.addVert(startHi, bhi, n1i);
            faceRight.addVert(startMi, bmi, n1i);
            faceRight.addVert(endMi, emi, n1i);
            faceRight.addVert(endHi, ehi, n1i);

            faceLeft.addVert(startMi, emi, n2i);
            faceLeft.addVert(startHi, ehi, n2i);
            faceLeft.addVert(endHi, bhi, n2i);
            faceLeft.addVert(endMi, bmi, n2i);

            // new start point.
            start = end;

            startMi = endMi;
            startHi = endHi;
        }
    }

    private static Double getHeight(int i, List<Double> heights2) {
        if (heights2 == null) {
            return 0d;
        }
        return heights2.get(i);
    }

    /**
     * Gets fence texture data.
     * 
     * @param fenceType
     *            fence type
     * @param pOsmPrimitive
     *            primitive
     * @param TextureLibraryStorageService
     *            texture library service
     * @return texture data
     */
    public static TextureData getFenceTexture(String fenceType, OsmPrimitive pOsmPrimitive,
            TextureLibraryStorageService TextureLibraryStorageService) {

        // FIXME add colored textures!
        String facadeColor = OsmAttributeKeys.FENCE_COLOR.primitiveValue(pOsmPrimitive);

        if (!StringUtil.isBlankOrNull(fenceType) || StringUtil.isBlankOrNull(facadeColor)) {

            String textureKey = TextureLibraryStorageService.getKey(TextureLibraryKey.BARRIER_FENCE, fenceType);
            return TextureLibraryStorageService.getTextureDefault(textureKey);

            // String facadeTextureFile = metadataCacheService.getPropertites(
            // "barrier.fence_{0}.texture.file", null, fenceType);
            //
            // double facadeTextureLenght =
            // metadataCacheService.getPropertitesDouble(
            // "barrier.fence_{0}.texture.lenght", 1d, fenceType);
            // double facadeTextureHeight = 1d;
            //
            // return new TextureData(facadeTextureFile, facadeTextureLenght,
            // facadeTextureHeight);

        } else {

            String facadeColorFile = "#c=" + facadeColor;

            return new TextureData(facadeColorFile, 1d, 1d);
        }
    }

    /**
     * Gets Fence type.
     * 
     * @param pOsmPrimitive
     *            osm primitive
     * @return fence type
     */
    public static String getFenceType(OsmPrimitive pOsmPrimitive) {
        String fenceType = OsmAttributeKeys.FENCE__TYPE.primitiveValue(pOsmPrimitive);
        if (StringUtil.isBlankOrNull(fenceType)) {
            fenceType = OsmAttributeKeys.FENCE_TYPE.primitiveValue(pOsmPrimitive);
        }
        return fenceType;
    }

    @Override
    public void draw(GL2 pGl, Camera pCamera) {

        // do not draw the transparent parts of the texture
        pGl.glEnable(GL.GL_BLEND);
        pGl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        // don't show source alpha parts in the destination

        // determine which areas of the polygon are to be rendered
        pGl.glEnable(GL2ES1.GL_ALPHA_TEST);
        pGl.glAlphaFunc(GL.GL_GREATER, 0); // only render if alpha > 0

        // replace the quad colors with the texture
        // gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE,
        // GL2.GL_REPLACE);
        pGl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);

        pGl.glEnable(GL.GL_CULL_FACE);

        pGl.glPushMatrix();
        pGl.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());

        try {

            this.modelRender.render(pGl, this.model);
        } finally {

            pGl.glPopMatrix();

            pGl.glDisable(GL.GL_CULL_FACE);
        }
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
