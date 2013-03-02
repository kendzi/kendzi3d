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
import kendzi.jogl.model.factory.BoundsFactory;
import kendzi.jogl.model.geometry.Bounds;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeKeys;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeValues;
import kendzi.josm.kendzi3d.jogl.model.building.builder.BuildingBuilder;
import kendzi.josm.kendzi3d.jogl.model.building.builder.BuildingOutput;
import kendzi.josm.kendzi3d.jogl.model.building.builder.BuildingPartOutput;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingModel;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingWallElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.Wall;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallNode;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.WindowGridBuildingElement;
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
import kendzi.josm.kendzi3d.jogl.selection.ArrowEditor;
import kendzi.josm.kendzi3d.jogl.selection.BuildingSelection;
import kendzi.josm.kendzi3d.jogl.selection.Editor;
import kendzi.josm.kendzi3d.jogl.selection.Selection;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.TextureLibraryService;
import kendzi.josm.kendzi3d.service.TextureLibraryService.TextureLibraryKey;
import kendzi.josm.kendzi3d.util.StringUtil;
import kendzi.math.geometry.polygon.MultiPartPolygonUtil;
import kendzi.math.geometry.polygon.MultiPartPolygonUtil.Edge;
import kendzi.math.geometry.polygon.MultiPartPolygonUtil.EdgeOut;
import kendzi.math.geometry.polygon.MultiPartPolygonUtil.Vertex;

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

    private List<NewBuildingDebug> debug = new ArrayList<NewBuildingDebug>();

    private List<Selection> selection = Collections.<Selection>emptyList();

    private boolean selected;

    private Bounds bounds;

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

        if (this.relation != null)  {
            if (this.relation.isMultipolygon()) {
                bm = parseBuildingMultiPolygon(this.relation, this.perspective);

            } else {
                bm = parseBuildingRelation(this.relation, this.perspective);
            }
        } else if (this.way != null) {
            bm = parseBuildingWay(this.way, this.perspective);

            this.selection = parseSelection(this.way.getId(), bm);


        }

        if (bm != null) {

            BuildingElementsTextureMenager tm = new CacheOsmBuildingElementsTextureMenager(this.textureLibraryService);

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

    private List<Selection> parseSelection(long wayId, BuildingModel bm) {
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

        final ArrowEditor ae = new ArrowEditor();
        ae.setPoint(bounds.getMin());
        ae.setVector(new Vector3d(0,1,0));
        ae.setLength(bounds.max.y);

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



    class CacheOsmBuildingElementsTextureMenager extends OsmBuildingElementsTextureMenager {

        Map<TextureFindCriteria, TextureData> cache = new HashMap<TextureFindCriteria, TextureData>();

        public CacheOsmBuildingElementsTextureMenager(TextureLibraryService textureLibraryService) {
            super(textureLibraryService);
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

    class OsmBuildingElementsTextureMenager extends BuildingElementsTextureMenager {
        TextureLibraryService textureLibraryService;

        public OsmBuildingElementsTextureMenager(TextureLibraryService textureLibraryService) {
            super();
            this.textureLibraryService = textureLibraryService;
        }

        @Override
        public TextureData findTexture(TextureFindCriteria pTextureFindCriteria) {

            boolean colorable = pTextureFindCriteria.isColorable();

            TextureLibraryKey key = null;
            Type type = pTextureFindCriteria.getType();
            if (Type.WINDOW.equals(type)) {
                key = TextureLibraryKey.BUILDING_WINDOW;
            } else if (Type.WINDOWS.equals(type)) {
                key = TextureLibraryKey.BUILDING_WINDOWS;
            } else if (Type.ENTERENCE.equals(type)) {
                key = TextureLibraryKey.BUILDING_ENTRANCE;
            } else if (Type.FACADE.equals(type)) {
                key = TextureLibraryKey.BUILDING_FACADE;
            } else if (Type.ROOF.equals(type)) {
                key = TextureLibraryKey.BUILDING_ROOF;
            }

            if (key == null) {
                throw new RuntimeException("unknown search texture criteria type: " + type);
            }

            String keyStr = this.textureLibraryService.getKey(key, pTextureFindCriteria.getTypeName()/*, pTextureFindCriteria.getSubTypeName()*/);

            // FIXME
            if ("buildings.facade_test1".equals(keyStr)) {
//                return new TextureData("test1", 2,  2);
              //  return new TextureData("#c=0xffffff", "/textures/pd/test/channel1gray.png", 2, 2);
//                return new TextureData("#c=0xffffff", "/textures/pd/MarekCompositeWall00001.png", 2, 2);
            }
            // FIXME

//            if (colorable) {
//                keyStr = "#bw=" + keyStr;
//            }

            List<TextureData> textureSet = this.textureLibraryService.getTextureSet(keyStr);
            boolean findColorable = false;
            if (colorable) {
                List<TextureData> filterByColorable = filterByColorable(colorable, textureSet);

                if (filterByColorable.size() > 0) {
                    // only when colorable texture data exist.
                    // otherwise we use regular (all) texture data
                    textureSet = filterByColorable;
                    findColorable = true;
                }
            }


            if ( pTextureFindCriteria.getHeight() != null ||  pTextureFindCriteria.getWidth() != null) {
                textureSet = filterByBestSizeMatch(pTextureFindCriteria, textureSet);
            }

            TextureData textureData = this.textureLibraryService.getRadnomTextureFromSet(textureSet);

            if (colorable && !findColorable) {
                textureData = this.textureLibraryService.colorableTextureData(textureData);
            }



            return textureData;
        }





        /**
         * @param colorable
         * @param textureSet
         * @return
         */
        public List<TextureData> filterByColorable(
                boolean colorable, List<TextureData> textureSet) {
            List<TextureData> ret = new ArrayList<TextureData>();

            for (TextureData td : textureSet) {

                if (td.isColorable() != null && td.isColorable() == colorable) {
                    ret.add(td);
                }
            }
            return ret;
        }
        /**
         * @param pTextureFindCriteria
         * @param textureSet
         * @return
         */
        public List<TextureData> filterByBestSizeMatch(TextureFindCriteria pTextureFindCriteria, List<TextureData> textureSet) {
            TextureData best = null;
            double bestError = Double.MAX_VALUE;

            double height = pTextureFindCriteria.getHeight() == null ? 0 :  pTextureFindCriteria.getHeight();
            double width = pTextureFindCriteria.getWidth() == null ? 0 : pTextureFindCriteria.getWidth();

            for (TextureData td : textureSet) {
                double dH = td.getHeight() - height;
                double dW = td.getWidth() - width;

                double err = dH * dH + dW * dW;

                if (err < bestError) {
                    bestError = err;
                    best = td;
                }
            }
            return Arrays.asList(best);
        }
    }


    BuildingModel parseBuildingWay(Way way, Perspective3D perspective) {
        BuildingModel bm = new BuildingModel();

        List<BuildingPart> bpList = parseBuildingPart(way, perspective);
        bm.setParts(bpList);
        return bm;
    }

    private BuildingPart cloneBuildingPart(BuildingPart bp, Double height) {
        BuildingPart ret = new BuildingPart();
        if (bp.getMaxHeight() == null) {
            ret.setMaxHeight(bp.getDefaultMaxHeight() + height);
        } else {
            ret.setMaxHeight(bp.getMaxHeight() + height);
        }

        if (bp.getMinHeight() == null) {
            ret.setMinHeight(bp.getDefaultMinHeight() + height);
        } else {
            ret.setMinHeight(bp.getMinHeight() + height);
        }

        ret.setDormerRoofModel(bp.getDormerRoofModel());
        ret.setFacadeColour(bp.getFacadeColour());
        ret.setFacadeMaterialType(bp.getFacadeMaterialType());
        ret.setInlineWalls(bp.getInlineWalls());
        ret.setLevelHeight(bp.getLevelHeight());

        ret.setMaxLevel(bp.getMaxLevel());
        ret.setMinLevel(bp.getMinLevel());

        ret.setRoof(bp.getRoof());
        ret.setRoofColour(bp.getRoofColour());
        ret.setRoofLevels(bp.getRoofLevels());
        ret.setRoofMaterialType(bp.getRoofMaterialType());

        ret.setWall(bp.getWall());

        return ret;
    }

    BuildingModel parseBuildingRelation(Relation pRelation, Perspective3D pers) {
        // TODO for relation type=building !
        BuildingType bt = BuildingType.OUTLINE;
        if (isRelationHaveBuildingParts(pRelation)) {
            bt = BuildingType.PARTS;
        }

        BuildingModel bm = new BuildingModel();
        List<BuildingPart> bps = new ArrayList<BuildingPart>();
        bm.setParts(bps);

        for (int i = 0; i < pRelation.getMembersCount(); i++) {
            RelationMember member = pRelation.getMember(i);

            OsmPrimitive primitive = member.getMember();

            if (BuildingType.OUTLINE.equals(bt)
                && !OsmAttributeKeys.BUILDING.primitiveKeyHaveAnyValue(member.getMember())) {
                continue;
            } else if (BuildingType.PARTS.equals(bt)
                && !OsmAttributeKeys.BUILDING_PART.primitiveKeyHaveAnyValue(member.getMember())) {
                continue;
            }

            if (primitive instanceof Way) {
                bps.addAll(parseBuildingPart((Way) primitive, pers));

            } else if (primitive instanceof Relation) {
                Relation r = (Relation) primitive;
                if (r.isMultipolygon()) {
                    List<BuildingPart> bp = parseBuildingMultiPolygonPart((Relation) primitive, pers);
                    if (bp != null) {
                        bps.addAll(bp);
                    }
                }
            }
        }
        return bm;
    }

    enum BuildingType {
        OUTLINE,
        PARTS
    }

    private boolean isRelationHaveBuildingParts(Relation pRelation) {

        boolean haveParts = false;
        for (int i = 0; i < pRelation.getMembersCount(); i++) {
            RelationMember member = pRelation.getMember(i);

            if (OsmAttributeKeys.BUILDING_PART.primitiveKeyHaveAnyValue(member.getMember())) {
                haveParts = true;
                break;
            }
        }

        return haveParts;
    }

    BuildingModel parseBuildingMultiPolygon(Relation pRelation, Perspective3D pers) {
        if (!pRelation.isMultipolygon()) {
            throw new IllegalArgumentException("for multipolygon relations!");
        }

        BuildingModel bm = new BuildingModel();
        List<BuildingPart> bps = new ArrayList<BuildingPart>();
        bm.setParts(bps);

        List<BuildingPart> bp = parseBuildingMultiPolygonPart(pRelation, pers);
        if (bp != null) {
            bps.addAll(bp);
        }
        return bm;
    }

    class AreaWithHoles {
        List<ReversableWay> outer;
        List<List<ReversableWay>> inner;
        /**
         * @return the outer
         */
        public List<ReversableWay> getOuter() {
            return this.outer;
        }
        /**
         * @param outer the outer to set
         */
        public void setOuter(List<ReversableWay> outer) {
            this.outer = outer;
        }
        /**
         * @return the inner
         */
        public List<List<ReversableWay>> getInner() {
            return this.inner;
        }
        /**
         * @param inner the inner to set
         */
        public void setInner(List<List<ReversableWay>> inner) {
            this.inner = inner;
        }


    }
    List<BuildingPart> parseBuildingMultiPolygonPart(Relation pRelation, Perspective3D pPerspective) {

        List<AreaWithHoles> waysPolygon = findAreaWithHoles(pRelation);


        List<BuildingPart> ret = new ArrayList<BuildingPart>();

        for (AreaWithHoles waysPolygon2 : waysPolygon) {
            BuildingPart bp = parseBuildingPartAttributes(pRelation);

            DormerRoofModel roof = RoofParser.parse(pRelation,  pPerspective);
//            DormerRoofModel roofClosedWay = RoofParser.parse(p, pPerspective);
//            bp.setRoof(marge(roof, roofClosedWay));
            bp.setRoof(roof);

            bp.setWall(parseWall(waysPolygon2.getOuter(), pPerspective));

            if (waysPolygon2.getInner() != null) {
                List<Wall> innerWall = new ArrayList<Wall>();
                for (List<ReversableWay> rwList : waysPolygon2.getInner()) {
                    innerWall.add(parseWall(rwList, pPerspective));
                }
                bp.setInlineWalls(innerWall);
            }

            ret.add(bp);

        }

        return ret;
    }

    private List<AreaWithHoles> findAreaWithHoles(Relation pRelation) {

        // outers
        List<OsmPrimitive> outersClosed = filterByRoleAndKey(pRelation, OsmPrimitiveType.CLOSEDWAY, "outer", null);
        outersClosed.addAll(filterByRoleAndKey(pRelation, OsmPrimitiveType.CLOSEDWAY, null, null));

        List<OsmPrimitive> outersParts = filterByRoleAndKey(pRelation, OsmPrimitiveType.WAY, "outer", null);
        outersParts.addAll(filterByRoleAndKey(pRelation, OsmPrimitiveType.WAY, null,  null ));

        List<OsmPrimitive> innersClosed = filterByRoleAndKey(pRelation, OsmPrimitiveType.CLOSEDWAY, "inner", null);
        List<OsmPrimitive> innersParts = filterByRoleAndKey(pRelation, OsmPrimitiveType.WAY, "inner", null);

        List<List<ReversableWay>> outers = convertWay(outersClosed);
        List<List<ReversableWay>> outerWallParts = connectMultiPolygonParts(outersParts);
        outers.addAll(outerWallParts);

        List<List<ReversableWay>> inners = convertWay(innersClosed);
        List<List<ReversableWay>> innersWallParts = connectMultiPolygonParts(innersParts);
        inners.addAll(innersWallParts);

        return connectPolygonHoles(outers, inners);
    }

    private List<AreaWithHoles> connectPolygonHoles(List<List<ReversableWay>> outers, List<List<ReversableWay>> inners) {
        List<AreaWithHoles> ret = new ArrayList<NewBuilding.AreaWithHoles>();
        for (List<ReversableWay> o : outers) {
            AreaWithHoles wp = new AreaWithHoles();
            wp.setOuter(o);

            //FIXME TODO filter out inners from outers!!
            wp.setInner(inners);

            ret.add(wp);
        }
        return ret;
    }

    /**
     * @param outersParts
     * @return
     */
    public List<List<ReversableWay>> connectMultiPolygonParts(List<OsmPrimitive> outersParts) {
        List<Edge<Way, Node>> in = new ArrayList<MultiPartPolygonUtil.Edge<Way,Node>>();
        for (OsmPrimitive osmPrimitive : outersParts) {
            Way w =((Way) osmPrimitive);
            Vertex<Node> v1 = new Vertex<Node>(w.getNode(0));
            Vertex<Node> v2 = new Vertex<Node>(w.getNode(w.getNodesCount()-1));

            Edge<Way, Node> e = new Edge<Way, Node>(v1, v2, w);
            in.add(e);
        }

        List<List<EdgeOut<Way,Node>>> connect = MultiPartPolygonUtil.connect(in);


        List<List<ReversableWay>> outerWallParts = convert(connect);
        return outerWallParts;
    }

    private List<List<ReversableWay>> convertWay(List<OsmPrimitive> outersClosed) {
        List<List<ReversableWay>> ret = new ArrayList<List<ReversableWay>>();
        for (OsmPrimitive osmPrimitive : outersClosed) {
            ret.add(Arrays.asList(new ReversableWay((Way) osmPrimitive, false)));
        }
        return ret;
    }


    private DormerRoofModel marge(DormerRoofModel roof, DormerRoofModel roofClosedWay) {
        // TODO
        if (roof.getRoofType() == null) {
            roof.setRoofType(roofClosedWay.getRoofType());
        }
        if (roof.getRoofTypeParameter() == null) {
            roof.setRoofTypeParameter(roofClosedWay.getRoofTypeParameter());
        }
        if (roof.getDirection() == null) {
            roof.setDirection(roofClosedWay.getDirection());
        }
        if (roof.getOrientation() == null) {
            roof.setOrientation(roofClosedWay.getOrientation());
        }

        return roof;
    }

    /**
     * @param pRelation
     * @return
     */
    public BuildingPart parseBuildingPartAttributes(OsmPrimitive pRelation) {
        BuildingPart bp = new BuildingPart();
        bp.setMaxHeight(BuildingAttributeParser.parseMaxHeight(pRelation));
        bp.setMinHeight(BuildingAttributeParser.parseMinHeight(pRelation));
        bp.setMaxLevel(BuildingAttributeParser.parseMaxLevel(pRelation));
        bp.setRoofLevels(BuildingAttributeParser.parseRoofLevels(pRelation));
        bp.setMinLevel(BuildingAttributeParser.parseMinLevel(pRelation));

        bp.setFacadeMaterialType(BuildingAttributeParser.parseFacadeMaterialName(pRelation));
        bp.setFacadeColour(BuildingAttributeParser.parseFacadeColour(pRelation));

        bp.setRoofMaterialType(BuildingAttributeParser.parseRoofMaterialName(pRelation));
        bp.setRoofColour(BuildingAttributeParser.parseRoofColour(pRelation));
        return bp;
    }


    /**
     * @param connect
     * @return
     */
    public List<List<ReversableWay>> convert(List<List<EdgeOut<Way, Node>>> connect) {

        List<List<ReversableWay>> outerWallParts = new ArrayList<List<ReversableWay>>();
        for (List<EdgeOut<Way, Node>> list : connect) {
            List<ReversableWay> wallParts = new ArrayList<NewBuilding.ReversableWay>();
            for (EdgeOut<Way, Node> edgeOut : list) {
                wallParts.add(new ReversableWay(edgeOut.getEdge().getData(), edgeOut.isReverted()));
            }
            outerWallParts.add(wallParts);
        }
        return outerWallParts;
    }

    class ReversableWay {
        Way way;
        boolean reversed;



        public ReversableWay(Way way, boolean reversed) {
            super();
            this.way = way;
            this.reversed = reversed;
        }
        /**
         * @return the way
         */
        public Way getWay() {
            return this.way;
        }
        /**
         * @param way the way to set
         */
        public void setWay(Way way) {
            this.way = way;
        }
        /**
         * @return the reversed
         */
        public boolean isReversed() {
            return this.reversed;
        }
        /**
         * @param reversed the reversed to set
         */
        public void setReversed(boolean reversed) {
            this.reversed = reversed;
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

    private Wall parseWall(Way way, Perspective3D pPerspective) {
        Wall wall = new Wall();

        WallPart wp = parseWallPart(new ReversableWay(way, false), pPerspective);

        wall.setWallParts(Arrays.asList(wp));

        return wall;
    }

    private Wall parseWall(List<ReversableWay> rwList, Perspective3D pPerspective) {
        Wall wall = new Wall();
        List<WallPart> wp = new ArrayList<WallPart>();
        for (ReversableWay rw : rwList) {
            wp.add(parseWallPart(rw, pPerspective));
        }

        wall.setWallParts(wp);

        return wall;
    }

    private WallPart parseWallPart(ReversableWay rw, Perspective3D pPerspective) {

        Way way = rw.getWay();

        WallPart wp = new WallPart();

        List<WallNode> wnList = new ArrayList<WallNode>();

        if (!rw.isReversed()) {

            for (int i = 0; i < way.getNodesCount(); i++) {

                WallNode wn = parseWallNode(way.getNode(i), pPerspective);

                wnList.add(wn);
            }
        } else {

            for (int i = way.getNodesCount() - 1; i >= 0; i--) {

                WallNode wn = parseWallNode(way.getNode(i), pPerspective);

                wnList.add(wn);
            }
        }
        wp.setNodes(wnList);
        wp.setBuildingElements(parseBuildingAttributeWallElement(way));

//        String parseFacadeName = BuildingAttributeParser.parseFacadeMaterialName(w);
//        wp.setFacadeTextureData(BuildingAttributeParser.parseFacadeTexture(parseFacadeName, this.textureLibraryService));
//        wp.setColour(BuildingAttributeParser.parseFacadeColour(w));

        wp.setFacadeMaterialType(BuildingAttributeParser.parseFacadeMaterialName(way));
        wp.setFacadeColour(BuildingAttributeParser.parseFacadeColour(way));

        wp.setRoofMaterialType(BuildingAttributeParser.parseRoofMaterialName(way));
        wp.setRoofColour(BuildingAttributeParser.parseRoofColour(way));

        return wp;
    }

    private List<BuildingWallElement> parseBuildingAttributeWallElement(Way w) {

        List<BuildingWallElement> ret = new ArrayList<BuildingWallElement>();

        WindowGridBuildingElement wgbe = BuildingAttributeParser.parseWallWindowsColumns(w);
        if (wgbe != null) {
            ret.add(wgbe);
        }

        return ret;
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

                    EntranceBuildingElement beClone = (EntranceBuildingElement) clone(entrance);

                    beClone.setMinHeight(beClone.getMinHeight() + cloner);
                    buildingElements.add(beClone);
                }
            }

        }



        if (isAttribute(node, OsmAttributeKeys.BUILDING, OsmAttributeValues.WINDOW)) {

            WindowBuildingElement entrance = new WindowBuildingElement();
            entrance.setHeight(ModelUtil.getHeight(node,  entrance.getHeight()));
            entrance.setMinHeight(ModelUtil.getMinHeight(node,  entrance.getMinHeight()));

            entrance.setWidth(parseWidth(node, entrance.getWidth()));

            buildingElements.add(entrance);


            for (RelationCloneHeight rch : buildHeightClone) {
                for (Double cloner : rch) {


                    WindowBuildingElement beClone = (WindowBuildingElement) clone(entrance);

                    beClone.setMinHeight(beClone.getMinHeight() + cloner);
                    buildingElements.add(beClone);
                }

            }

        }


        return wn;
    }

    boolean isAttribute(OsmPrimitive prim, OsmAttributeKeys key, OsmAttributeValues val) {
        return val.getValue().equals(prim.get(key.getKey()));
    }

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

            if (!type.equals(member.getDisplayType())) {
                continue;
            }

            if (StringUtil.isBlankOrNull(member.getRole()) && StringUtil.isBlankOrNull(role)) {
                ret.add(member.getMember());
                continue;
            }

            if (StringUtil.equalsOrNulls(role, member.getRole())) {
                ret.add(member.getMember());
            }
        }
        return ret;
    }

    boolean isClosedWay(RelationMember member) {
        if (OsmPrimitiveType.WAY.equals(member.getType())
                && (member.getWay().isClosed())) {
            return true;
        }
        return false;
    }


    private List<BuildingPart> parseBuildingPart(Way primitive, Perspective3D pPerspective) {

        if (!primitive.isClosed()) {
            throw new RuntimeException("Way is not closed: " + primitive);
        }

        OsmPrimitive p = primitive;

        BuildingPart bp = parseBuildingPartAttributes(p);

        bp.setWall(parseWall((Way) p, pPerspective));
        bp.setInlineWalls(null);

        bp.setRoof(RoofParser.parse(primitive, pPerspective));

        List<BuildingPart> bpList = new ArrayList<BuildingPart>();
        bpList.add(bp);

        bpList.addAll(parseRelationClone(primitive, bp));

        return bpList;
    }

    private List<BuildingPart> parseRelationClone(Way primitive, BuildingPart bp) {
        List<RelationCloneHeight> buildHeightClone = RelationCloneHeight.buildHeightClone(primitive);
        List<BuildingPart> bpList = new ArrayList<BuildingPart>();

        for (RelationCloneHeight relationCloneHeight : buildHeightClone) {
            for (Double height : relationCloneHeight) {

                bpList.add(cloneBuildingPart(bp, height));
            }
        }
        return bpList;
    }

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

        if (this.modelRender.isDebugging() && this.debug != null) {
            for (NewBuildingDebug d : this.debug) {
                d.drawDebugRoof(pGl);
            }
        }
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
