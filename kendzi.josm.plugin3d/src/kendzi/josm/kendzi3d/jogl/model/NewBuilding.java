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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.building.builder.BuildingBuilder;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingModel;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingWallElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.Wall;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallNode;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.BuildingNodeElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.EntranceBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.WindowBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.parser.BuildingAttributeParser;
import kendzi.josm.kendzi3d.jogl.model.building.parser.RoofParser;
import kendzi.josm.kendzi3d.jogl.model.building.texture.BuildingElementsTextureMenager;
import kendzi.josm.kendzi3d.jogl.model.building.texture.TextureFindCriteria;
import kendzi.josm.kendzi3d.jogl.model.clone.RelationCloneHeight;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.TextureLibraryService;
import kendzi.josm.kendzi3d.service.TextureLibraryService.TextureLibraryKey;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
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
    private TextureLibraryService textureLibraryService;

    /**
     * Model of building.
     */
    private Model model;

    private Relation relation;

    private Way way;



    /**
     * Constructor for building.
     *
     * @param pWay way describing building
     * @param pPerspective perspective3
     * @param pModelRender model render
     * @param pMetadataCacheService metadata cache service
     * @param pTextureLibraryService texture library service
     */
    public NewBuilding(Relation pRelation, Perspective3D pPerspective,
            ModelRender pModelRender, MetadataCacheService pMetadataCacheService,
            TextureLibraryService pTextureLibraryService) {
        super(pPerspective);

        this.modelRender = pModelRender;
        this.metadataCacheService = pMetadataCacheService;
        this.textureLibraryService = pTextureLibraryService;

        this.relation = pRelation;
    }

    public NewBuilding(Way pWay, Perspective3D pPerspective,
            ModelRender pModelRender, MetadataCacheService pMetadataCacheService,
            TextureLibraryService pTextureLibraryService) {
        super(pPerspective);

        this.modelRender = pModelRender;
        this.metadataCacheService = pMetadataCacheService;
        this.textureLibraryService = pTextureLibraryService;

        this.way = pWay;
    }




    @Override
    public void buildModel() {

        BuildingModel bm = null;

        String key = "building";

        if (this.relation != null)  {
            bm = parseBuilding(this.relation, key, this.perspective);
        } else if (this.way != null) {
            bm = parseBuilding(this.way, this.perspective);
        }

        if (bm != null) {

            BuildingElementsTextureMenager tm = new OsmBuildingElementsTextureMenager(this.textureLibraryService);
            Model model = BuildingBuilder.buildModel(bm, tm);
            model.useLight = true;
            model.useTexture = true;

            this.model = model;
            this.buildModel = true;
        }
    }

    class OsmBuildingElementsTextureMenager extends BuildingElementsTextureMenager {
        TextureLibraryService textureLibraryService;

        public OsmBuildingElementsTextureMenager(TextureLibraryService textureLibraryService) {
            super();
            this.textureLibraryService = textureLibraryService;
        }

        @Override
        public TextureData findTexture(TextureFindCriteria pTextureFindCriteria) {

            TextureLibraryKey key = null;
            Type type = pTextureFindCriteria.getType();
            if (Type.WINDOW.equals(type)) {
                key = TextureLibraryKey.BUILDING_WINDOW;
            } else if (Type.ENTERENCE.equals(type)) {
                key = TextureLibraryKey.BUILDING_ENTRANCE;
            }


            String keyStr = this.textureLibraryService.getKey(key, pTextureFindCriteria.getTypeName()/*, pTextureFindCriteria.getSubTypeName()*/);
            List<TextureData> textureSet = this.textureLibraryService.getTextureSet(keyStr);

            TextureData best = null;
            double bestError = Double.MAX_VALUE;

            double height = pTextureFindCriteria.getHeight() == null ? 0 :  pTextureFindCriteria.getHeight();
            double width = pTextureFindCriteria.getWidth() == null ? 0 : pTextureFindCriteria.getWidth();

            for (TextureData td : textureSet) {
                double dH = td.getHeight() - height;
                double dW = td.getLenght() - width;

                double err = dH * dH + dW * dW;

                if (err < bestError) {
                    bestError = err;
                    best = td;
                }
            }
            return best;
        }
    }


    BuildingModel parseBuilding(Way way, Perspective3D perspective) {
        BuildingModel bm = new BuildingModel();

        BuildingPart bp = parseBuildingPart(way, perspective);

        bm.setParts(Arrays.asList(bp));

        return bm;
    }

    BuildingModel parseBuilding(Relation pRelation, String key, Perspective3D pers) {



        BuildingModel bm = new BuildingModel();
        List<BuildingPart> bps = new ArrayList<BuildingPart>();
        bm.setParts(bps);
//        List<OsmPrimitive> memberParts
      //this.facadeTexture = getFacadeTexture();
        for (int i = 0; i < pRelation.getMembersCount(); i++) {
            RelationMember member = pRelation.getMember(i);

            OsmPrimitive primitive = member.getMember();

            if (primitive instanceof Way) {
                BuildingPart bp = parseBuildingPart((Way) primitive, pers);
                if (bp != null) {
                    bps.add(bp);
                }
            } else if (primitive instanceof Relation) {
                Relation r = (Relation) primitive;
                if (r.isMultipolygon()) {
                    List<BuildingPart> bp = parseBuildingMultiPolygon((Relation) primitive, key, pers);
                    if (bp != null) {
                        bps.addAll(bp);
                    }
                }
            }
        }
        return bm;
    }

    List<BuildingPart> parseBuildingMultiPolygon(Relation pRelation, String key, Perspective3D pPerspective) {

        List<BuildingPart> ret = new ArrayList<BuildingPart>();

        List<OsmPrimitive> outersClosed = filterByRoleAndKey(pRelation, OsmPrimitiveType.CLOSEDWAY, "outer", key);
        List<OsmPrimitive> innersClosed = filterByRoleAndKey(pRelation, OsmPrimitiveType.CLOSEDWAY, "inner", key);
        List<OsmPrimitive> outersParts = filterByRoleAndKey(pRelation, OsmPrimitiveType.WAY, "outer", key);
        List<OsmPrimitive> innersParts = filterByRoleAndKey(pRelation, OsmPrimitiveType.WAY, "inner", null);

       // List<Wall> outerWalls = parseWallParts(outersParts);
//        List<Wall> outerWalls = parseWall(outersParts);


        List<Wall> innersWalls = new ArrayList<Wall>();
        for (OsmPrimitive p : innersClosed) {
            innersWalls.add(parseWall((Way) p, pPerspective));
        }

        for (OsmPrimitive p : outersClosed) {
            BuildingPart bp = new BuildingPart();
            bp.setMaxHeight(BuildingAttributeParser.parseMaxHeight(p, pRelation));
            bp.setMinHeight(BuildingAttributeParser.parseMinHeight(p, pRelation));
            bp.setMaxLevel(BuildingAttributeParser.parseMaxLevel(p, pRelation));
            bp.setMinLevel(BuildingAttributeParser.parseMinLevel(p, pRelation));

            String parseFacadeName = BuildingAttributeParser.parseFacadeName(pRelation);
            bp.setFacadeTextureData(BuildingAttributeParser.parseFacadeTexture(parseFacadeName, this.textureLibraryService));
            bp.setColour(BuildingAttributeParser.parseFacadeColour(pRelation));

            //XXX
//            bp.setColor()
//            bp.setMaterial()

            bp.setWall(parseWall((Way) p, pPerspective));
            bp.setInlineWalls(innersWalls);

            DormerRoofModel roof = RoofParser.parse((Way) p, pPerspective
                    /*, facdeTexture*/);

            bp.setRoof(roof);

            ret.add(bp);
        }

//        for (Wall w : outersWalls) {
//            BuildingPart bp = new BuildingPart
//            ret.add
//        }
        return ret;
    }


    private List<Wall> parseWallParts(List<OsmPrimitive> parts, Perspective3D pPerspective) {

        List<Wall> ret = new ArrayList<Wall>();

        for (OsmPrimitive p : parts) {
            if (OsmPrimitiveType.CLOSEDWAY.equals(p.getType())) {
                ret.add(parseWall((Way) p, pPerspective));
            } else {
                // TODO
            }
        }

        return null;
    }

    private Wall parseWall(Way p, Perspective3D pPerspective) {
        Wall wall = new Wall();

        WallPart wp = parseWallPart(p, pPerspective);

        wall.setWallParts(Arrays.asList(wp));

        return wall;
    }

    private WallPart parseWallPart(Way w, Perspective3D pPerspective) {
        WallPart wp = new WallPart();

        List<WallNode> wnList = new ArrayList<WallNode>();
        for (int i = 0; i < w.getNodesCount(); i++) {
            Node n = w.getNode(i);

            WallNode wn = parseWallNode(n, pPerspective);

            wnList.add(wn);
        }

        wp.setNodes(wnList);
        wp.setBuildingElements(parseBuildingWallElement(w));

        String parseFacadeName = BuildingAttributeParser.parseFacadeName(w);
        wp.setFacadeTextureData(BuildingAttributeParser.parseFacadeTexture(parseFacadeName, this.textureLibraryService));
        wp.setColour(BuildingAttributeParser.parseFacadeColour(w));


        return wp;
    }

    private List<BuildingWallElement> parseBuildingWallElement(Way w) {
        // TODO Auto-generated method stub
        return null;
    }

    private WallNode parseWallNode(Node node, Perspective3D pPerspective) {
        WallNode wn = new WallNode();
        wn.setPoint(pPerspective.calcPoint(node));


        List<BuildingNodeElement> buildingElements = new ArrayList<BuildingNodeElement>();
        wn.setBuildingNodeElements(buildingElements);

        List<RelationCloneHeight> buildHeightClone = RelationCloneHeight.buildHeightClone(node);

        if (isAttribute(node, OsmAttributeKeys.BUILDING, OsmAttributeValues.ENTRANCE)) {

            EntranceBuildingElement entrance = new EntranceBuildingElement();
            entrance.setHeight(ModelUtil.getHeight(node,  entrance.getHeight()));
            entrance.setMinHeight(ModelUtil.getMinHeight(node,  entrance.getMinHeight()));
            entrance.setWidth(parseWidth(node, entrance.getWidth()));

            buildingElements.add(entrance);

            for (RelationCloneHeight rch : buildHeightClone) {
                for (Double cloner : rch) {

                    entrance = (EntranceBuildingElement) clone(entrance);

                    entrance.setMinHeight(entrance.getMinHeight() + cloner);
                    buildingElements.add(entrance);
                }
            }

        }
//        if ("entrance".equals(node.get("building"))) {


//            Entrances entrance = new Entrances();
//
//            Point2d p = this.points.get(i);
//            Vector2d direction = findWindowDirection(this.points, i);
//
//            entrance.setPoint(p);
//            entrance.setDirection(direction);
//            entrance.setCloneHeight(RelationCloneHeight.buildHeightClone(node));
//
//            entrance.setHeight(ModelUtil.getHeight(node,  entrance.getHeight()));
//            entrance.setMinHeight(ModelUtil.getMinHeight(node,  entrance.getMinHeight()));
//
//            windowEntrances.add(entrance);
//        }


        if (isAttribute(node, OsmAttributeKeys.BUILDING, OsmAttributeValues.WINDOW)) {
//            Window entrance = new Window();
//            Point2d p = this.points.get(i);
//            Vector2d direction = findWindowDirection(this.points, i);
//            entrance.setPoint(p);
//            entrance.setDirection(direction);
//            entrance.setCloneHeight(RelationCloneHeight.buildHeightClone(node));


            WindowBuildingElement entrance = new WindowBuildingElement();
            entrance.setHeight(ModelUtil.getHeight(node,  entrance.getHeight()));
            entrance.setMinHeight(ModelUtil.getMinHeight(node,  entrance.getMinHeight()));

            entrance.setWidth(parseWidth(node, entrance.getWidth()));

            buildingElements.add(entrance);


            for (RelationCloneHeight rch : buildHeightClone) {
                for (Double cloner : rch) {


                    entrance = (WindowBuildingElement) clone(entrance);

                    entrance.setMinHeight(entrance.getMinHeight() + cloner);
                    buildingElements.add(entrance);
                }

            }

        }


        return wn;
    }

    boolean isAttribute(OsmPrimitive prim, OsmAttributeKeys key, OsmAttributeValues val) {
        return val.getValue().equals(prim.get(key.getKey()));
    }

//    private WindowBuildingElement clone(WindowBuildingElement pWbe) {
//        WindowBuildingElement wbe = new WindowBuildingElement();
//        wbe.setMinHeight(pWbe.getMinHeight());
//        wbe.setHeight(pWbe.getHeight());
//        wbe.setWidth(pWbe.getWidth());
//        return wbe;
//    }

    private BuildingNodeElement clone(BuildingNodeElement entrance) {
        try {
            return (WindowBuildingElement) entrance.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("error cloning WindowBuildingElement", e);
        }
    }

    Double parseWidth(OsmPrimitive primitive, Double defaultValue) {
        return ModelUtil.parseHeight(primitive.get("width"),  defaultValue);
    }

    List<OsmPrimitive> filterByRoleAndKey(Relation pRelation, OsmPrimitiveType type, String role, String key) {
        List<OsmPrimitive> ret = new ArrayList<OsmPrimitive>();

        for (int i = 0; i < pRelation.getMembersCount(); i++) {
            RelationMember member = pRelation.getMember(i);

            if (!type.equals(member.getType())) {
                continue;
            }

            if (role.equals(member.getRole())) {

                if (member.getMember().hasKey(key)) {
                    ret.add(member.getMember());
                }
            }
        }
        return ret;
    }


    private BuildingPart parseBuildingPart(Way primitive, Perspective3D pPerspective) {

        //if (OsmPrimitiveType.CLOSEDWAY.equals(primitive.getType())) {
        if (primitive.isClosed()) {
            OsmPrimitive p = primitive;
                BuildingPart bp = new BuildingPart();
                bp.setMaxHeight(BuildingAttributeParser.parseMaxHeight(p));
                bp.setMinHeight(BuildingAttributeParser.parseMinHeight(p));
                bp.setMaxLevel(BuildingAttributeParser.parseMaxLevel(p));
                bp.setMinLevel(BuildingAttributeParser.parseMinLevel(p));
                //XXX
    //            bp.setColor()
    //            bp.setMaterial()

                bp.setWall(parseWall((Way) p, pPerspective));
                bp.setInlineWalls(null);


                DormerRoofModel roof = RoofParser.parse(primitive, pPerspective
                        /*, facdeTexture*/);

                bp.setRoof(roof);

                return bp;

            }


            return null;
        }

    @Override
    public void draw(GL2 pGl, Camera pCamera) {

        pGl.glPushMatrix();

        pGl.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());


        pGl.glColor3f((float) 188 / 255, (float) 169 / 255, (float) 169 / 255);

        this.modelRender.render(pGl, this.model);

        pGl.glPopMatrix();
    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (this.model == null) {
            buildModel();
        }

        return Collections.singletonList(new ExportItem(this.model, new Point3d(this.getGlobalX(), 0, -this.getGlobalY()), new Vector3d(1,1,1)));
    }
}
