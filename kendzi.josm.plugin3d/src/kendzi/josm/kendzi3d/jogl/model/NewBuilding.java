/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.DrawUtil;
import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.factory.BoundsFactory;
import kendzi.jogl.model.geometry.Bounds;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.BuildingElementsTextureManager;
import kendzi.jogl.texture.library.OsmBuildingElementsTextureMenager;
import kendzi.jogl.texture.library.TextureFindCriteria;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.model.building.builder.BuildingBuilder;
import kendzi.josm.kendzi3d.jogl.model.building.builder.BuildingOutput;
import kendzi.josm.kendzi3d.jogl.model.building.builder.BuildingPartOutput;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingModel;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallNode;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallPart;
import kendzi.josm.kendzi3d.jogl.model.building.parser.BuildingParser;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.selection.BuildingSelection;
import kendzi.josm.kendzi3d.jogl.selection.Selection;
import kendzi.josm.kendzi3d.jogl.selection.editor.ArrowEditorJosmImp;
import kendzi.josm.kendzi3d.jogl.selection.editor.Editor;
import kendzi.josm.kendzi3d.service.MetadataCacheService;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;


/**
 * Representing building model.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public class NewBuilding extends AbstractModel {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(NewBuilding.class);

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
     * Model of building.
     */
    private Model model;

    private Relation relation;

    private Way way;

    private List<NewBuildingDebug> debug = new ArrayList<NewBuildingDebug>();

    private List<Selection> selection = Collections.<Selection>emptyList();

    private boolean selected;

    private Bounds bounds;

    protected boolean preview;

    private BuildingModel bm;

    /**
     * Constructor for building.
     *
     * @param pWay way describing building
     * @param pPerspective perspective3
     * @param pModelRender model render
     * @param pMetadataCacheService metadata cache service
     * @param pTextureLibraryStorageService texture library service
     */
    public NewBuilding(Relation pRelation, Perspective3D pPerspective,
            ModelRender pModelRender, MetadataCacheService pMetadataCacheService,
            TextureLibraryStorageService pTextureLibraryStorageService) {
        super(pPerspective);

        this.modelRender = pModelRender;
        this.metadataCacheService = pMetadataCacheService;
        this.textureLibraryStorageService = pTextureLibraryStorageService;

        this.relation = pRelation;
    }

    public NewBuilding(Way pWay, Perspective3D pPerspective,
            ModelRender pModelRender, MetadataCacheService pMetadataCacheService,
            TextureLibraryStorageService pTextureLibraryStorageService) {
        super(pPerspective);

        this.modelRender = pModelRender;
        this.metadataCacheService = pMetadataCacheService;
        this.textureLibraryStorageService = pTextureLibraryStorageService;

        this.way = pWay;
    }





    @Override
    public void buildModel() {

        BuildingModel bm = this.bm;
        log.info("buildModel");
        log.info("buildModel: " + NewBuilding.this.preview + " buildModel: " + NewBuilding.this.buildModel);
        if (!this.preview || bm == null) {
            log.info("buildModel2");

            if (this.relation != null)  {
                if (this.relation.isMultipolygon()) {
                    bm = BuildingParser.parseBuildingMultiPolygon(this.relation, this.perspective);

                } else {
                    bm = BuildingParser.parseBuildingRelation(this.relation, this.perspective);
                }
            } else if (this.way != null) {
                bm = BuildingParser.parseBuildingWay(this.way, this.perspective);

                this.selection = parseSelection(this.way.getId(), bm);


            }
            this.preview = false;
            this.bm = bm;
        }

        if (bm != null) {

            BuildingElementsTextureManager tm = new CacheOsmBuildingElementsTextureMenager(this.textureLibraryStorageService);

            BuildingOutput buildModel = BuildingBuilder.buildModel(bm, tm);
            Model model = buildModel.getModel();
            model.useLight = true;
            model.useTexture = true;

            this.model = model;
            this.buildModel = true;

            this.debug.clear();

            if (buildModel.getBuildingPartOutput() != null) {
                for (BuildingPartOutput bo: buildModel.getBuildingPartOutput()) {
                    this.debug.add(new NewBuildingDebug(bo.getRoofDebugOut()));
                }

////                    && buildModel.getBuildingPartOutput().size() > 0) {
//                //debug inf o for first part
//                BuildingPartOutput buildingPartOutput = buildModel.getBuildingPartOutput().get(0);

            }
        }
    }

    private List<Selection> parseSelection(long wayId, final BuildingModel bm) {
        BoundsFactory bf = new BoundsFactory();

        List<BuildingPart> parts = bm.getParts();
        for (BuildingPart bp : parts) {
            List<WallPart> wallParts = bp.getWall().getWallParts();
            for (WallPart wp : wallParts) {
                for( WallNode wn : wp.getNodes()) {

                    Point2d p = wn.getPoint();

                    bf.addPoint(p.x, bp.getDefaultMinHeight(), -p.y);
                    bf.addPoint(p.x, bp.getDefaultMaxHeight(), -p.y);
                }
            }
        }

        Bounds bounds = bf.toBounds();

        this.bounds= bounds;

        if (this.way != null) {
        final ArrowEditorJosmImp ae = new ArrowEditorJosmImp() {

            @Override
            public void preview(double newValue) {
                log.info("preview: " + newValue);

                if (bm != null && bm.getParts() != null && bm.getParts().size() > 0) {
                    bm.getParts().get(0).setMaxHeight(newValue);
                }
                NewBuilding.this.preview = true;
                NewBuilding.this.buildModel = false;

                log.info("preview: " + NewBuilding.this.preview + " buildModel: " + NewBuilding.this.buildModel);

            }

        };
        ae.setPoint(bounds.getMin());
        ae.setVector(new Vector3d(0,1,0));
        ae.setLength(bounds.max.y);
        ae.setFildName("height");
        ae.setPrimitiveId(this.way.getUniqueId());
        ae.setPrimitiveType(OsmPrimitiveType.WAY);

        return Arrays.<Selection>asList(
                new BuildingSelection(wayId,bounds.getCenter(), bounds.getRadius()) {

                    @Override
                    public void select(boolean selected) {
                        NewBuilding.this.selected = selected;
                    }

                    @Override
                    public List<Editor> getEditors() {
                        // TODO Auto-generated method stub
                        return Arrays.<Editor>asList(ae);
                    }
                }
                );
        }
        return Collections.emptyList();
    }



    class CacheOsmBuildingElementsTextureMenager extends OsmBuildingElementsTextureMenager {

        Map<TextureFindCriteria, TextureData> cache = new HashMap<TextureFindCriteria, TextureData>();

        public CacheOsmBuildingElementsTextureMenager(TextureLibraryStorageService TextureLibraryStorageService) {
            super(TextureLibraryStorageService);
        }

        @Override
        public TextureData findTexture(TextureFindCriteria pTextureFindCriteria) {
            TextureData textureData = this.cache.get(pTextureFindCriteria);

            if (textureData == null) {
                textureData = super.findTexture(pTextureFindCriteria);
                this.cache.put(pTextureFindCriteria, textureData);
            }
            return textureData;
        }
    }











//    private List<Wall> parseWallParts(List<OsmPrimitive> parts, Perspective3D pPerspective) {
//
//        List<Wall> ret = new ArrayList<Wall>();
//
//        for (OsmPrimitive p : parts) {
//            if (OsmPrimitiveType.CLOSEDWAY.equals(p.getType())) {
//                ret.add(parseWall((Way) p, pPerspective));
//            } else {
//                // TODO
//            }
//        }
//
//        return null;
//    }








    @Override
    public void draw(GL2 pGl, Camera pCamera) {

        pGl.glPushMatrix();

        pGl.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());


        pGl.glColor3f((float) 188 / 255, (float) 169 / 255, (float) 169 / 255);

        this.modelRender.render(pGl, this.model);

        if (this.selected && this.bounds != null) {
            pGl.glColor3fv(Color.ORANGE.darker().getRGBComponents(new float[4]), 0);

            pGl.glLineWidth(6);

            DrawUtil.drawBox(pGl, this.bounds.getMax(), this.bounds.getMin());
        }

        pGl.glPopMatrix();

//        if (this.modelRender.isDebugging() && this.debug != null) {
//            for (NewBuildingDebug d : this.debug) {
//                d.drawDebugRoof(pGl);
//            }
//        }
    }




    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (this.model == null) {
            buildModel();
        }

        return Collections.singletonList(new ExportItem(this.model, new Point3d(this.getGlobalX(), 0, -this.getGlobalY()), new Vector3d(1,1,1)));
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.model.AbstractModel#getSelection()
     */
    @Override
    public List<Selection> getSelection() {
        return this.selection;
    }
}
