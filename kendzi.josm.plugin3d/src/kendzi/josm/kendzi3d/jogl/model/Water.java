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
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.NewBuilding.ReversableWay;
import kendzi.josm.kendzi3d.jogl.model.PolygonWithHolesUtil.AreaWithHoles;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofTypeUtil;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.TextureLibraryService;
import kendzi.josm.kendzi3d.service.TextureLibraryService.TextureLibraryKey;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.triangulate.Poly2TriUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

/** Water model.
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
    private ModelRender modelRender;

    /**
     * Metadata cache service.
     */
    private MetadataCacheService metadataCacheService;

    /**
     * Texture library service.
     */
    private TextureLibraryService textureLibraryService;

    /**
     * Model of water.
     */
    private Model model;

    private Relation relation;

    private Way way;


	/** Constructor for water.
	 *
	 * @param way way represent water
	 * @param pPerspective3D perspective
	 * @param pModelRender
	 * @param pMetadataCacheService
	 * @param pTextureLibraryService
	 */
	public Water(Way way, Perspective3D pPerspective3D, ModelRender pModelRender, MetadataCacheService pMetadataCacheService,
            TextureLibraryService pTextureLibraryService) {

	    super(pPerspective3D);

        this.modelRender = pModelRender;
        this.metadataCacheService = pMetadataCacheService;
        this.textureLibraryService = pTextureLibraryService;

        this.way = way;
	}

	/** Constructor for water.
	 *
	 * @param pRelation relation represent water
     * @param pPerspective perspective3d
     * @param pModelRender model render
	 * @param pMetadataCacheService
	 * @param pTextureLibraryService
     */
    public Water(Relation pRelation, Perspective3D pPerspective,
            ModelRender pModelRender, MetadataCacheService pMetadataCacheService,
            TextureLibraryService pTextureLibraryService) {

        super(pPerspective);

        this.modelRender = pModelRender;
        this.metadataCacheService = pMetadataCacheService;
        this.textureLibraryService = pTextureLibraryService;

        this.relation = pRelation;
    }

    List<PolygonWithHolesList2d> getMultiPolygonWithHoles() {
        if (this.relation != null) {
            return getMultiPolygonWithHolesRelation(this.relation, perspective);
        }
        return getMultiPolygonWithHolesWay(way, perspective);
    }

    List<PolygonWithHolesList2d> getMultiPolygonWithHolesWay(Way way, Perspective3D pPerspective) {
        List<PolygonWithHolesList2d> ret = new ArrayList<PolygonWithHolesList2d>();

        List<Point2d> poly = new ArrayList<Point2d>();

        int size =  way.getNodesCount();
        if (size > 0) {
            if (way.getNode(0).equals(way.getNode(way.getNodesCount() - 1))) {
                size--;
            }
            for (int i = 0; i < size; i++) {
                Point2d p = pPerspective.calcPoint(way.getNode(i));
                poly.add(p);
            }
            ret.add(new PolygonWithHolesList2d(new PolygonList2d(poly),null));
        }
        return ret;
    }

    List<PolygonWithHolesList2d> getMultiPolygonWithHolesRelation(Relation pRelation, Perspective3D pPerspective) {

        List<PolygonWithHolesList2d> ret = new ArrayList<PolygonWithHolesList2d>();

        List<AreaWithHoles> waysPolygon = PolygonWithHolesUtil.findAreaWithHoles(pRelation);


        for (AreaWithHoles waysPolygon2 : waysPolygon) {
            List<PolygonList2d> inner  = new ArrayList<PolygonList2d>();

            PolygonList2d outer = parse(waysPolygon2.getOuter(), pPerspective);

            if (waysPolygon2.getInner() != null) {
//                List<PolygonList2d> inner = new ArrayList<PolygonList2d>();
                for (List<ReversableWay> rwList : waysPolygon2.getInner()) {
                    inner.add(parse(rwList, pPerspective));
                }

            }
            ret.add(new PolygonWithHolesList2d(outer,inner));
        }
        return ret;
    }

	private PolygonList2d parse(List<ReversableWay> outer, Perspective3D pPerspective) {
	    List<Point2d> poly = new ArrayList<Point2d>();

	    for (ReversableWay rw : outer) {

    	    Way way = rw.getWay();


    	    int size =  way.getNodesCount();
            if (size > 0) {

                if (way.getNode(0).equals(way.getNode(way.getNodesCount() - 1))) {
                    size--;
                }

                if (!rw.isReversed()) {

                    for (int i = 0; i < size; i++) {
                        Point2d p = pPerspective.calcPoint(way.getNode(i));
    //                    WallNode wn = parseWallNode(way.getNode(i), pPerspective);
    //
                        poly.add(p);
                    }
                } else {

                    for (int i = size - 1; i >= 0; i--) {

                        Point2d p = pPerspective.calcPoint(way.getNode(i));

                      poly.add(p);
                    }
                }
            }
	    }
        return new PolygonList2d(poly);
    }

    @Override
	public void buildModel() {

	    ModelFactory model = ModelFactory.modelBuilder();
	    MeshFactory mesh = model.addMesh("water");

        TextureData waterTexture = getWaterTextureData();//new TextureData("#c=#008EFF", 1d, 1d);
        Material waterMaterial = MaterialFactory.createTextureMaterial(waterTexture.getTex0());
        int waterMaterialIndex = model.addMaterial(waterMaterial);

        mesh.materialID = waterMaterialIndex;
        mesh.hasTexture = true;

        Vector3d nt = new Vector3d(0, 1, 0);

		Point3d planeRightTopPoint =  new Point3d(
	             0 ,
	             0.05,
	             0);

//        MultiPolygonList2d topMP = new MultiPolygonList2d(new PolygonList2d(this.points));

        List<PolygonWithHolesList2d> polyList = getMultiPolygonWithHoles();
        for (PolygonWithHolesList2d poly : polyList) {

//            List<Point2d> pBorderList = buildingPolygon.getOuter().getPoints();
//            List<List<Point2d>> innerLists = innerLists(buildingPolygon);

            MultiPolygonList2d topMP = Poly2TriUtil.triangulate(poly);


            Plane3d planeTop = new Plane3d(
                   planeRightTopPoint,
                   nt);

            Vector3d roofTopLineVector = new Vector3d(
                   -1,
                   0,
                   0);



    	    RoofTypeUtil.addPolygonToRoofMesh(mesh, topMP, planeTop, roofTopLineVector, waterTexture);
        }

	    this.model = model.toModel();
        this.model.setUseLight(true);
        this.model.setUseTexture(true);

        this.buildModel = true;
	}

	@Override
	public void draw(GL2 pGl, Camera camera) {

	    pGl.glPushMatrix();
        pGl.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());

        //pGl.glColor3f((float) 188 / 255, (float) 169 / 255, (float) 169 / 255);

        try {
            this.modelRender.render(pGl, this.model);

        } finally {
            pGl.glPopMatrix();
        }
	}

	@Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (this.model == null) {
            buildModel();
        }

        return Collections.singletonList(new ExportItem(this.model, new Point3d(this.getGlobalX(), 0, -this.getGlobalY()), new Vector3d(1,1,1)));
    }

	 public TextureData getWaterTextureData() {

         String keyStr = this.textureLibraryService.getKey(TextureLibraryKey.WATER, (String) null);
         return textureLibraryService.getTextureDefault(keyStr);

     }

}
