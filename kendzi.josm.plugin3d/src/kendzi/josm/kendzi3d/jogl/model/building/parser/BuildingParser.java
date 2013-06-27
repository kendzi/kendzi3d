/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.model.building.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeKeys;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeValues;
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
import kendzi.josm.kendzi3d.jogl.model.clone.RelationCloneHeight;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.josm.PolygonWithHolesUtil;
import kendzi.josm.kendzi3d.josm.PolygonWithHolesUtil.AreaWithHoles;
import kendzi.josm.kendzi3d.josm.ReversableWay;
import kendzi.josm.kendzi3d.util.StringUtil;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;

public class BuildingParser {

    /**
     * Parse building model from multipolygon.
     *
     * @param pRelation
     * @param pers
     * @return
     */
    public static BuildingModel parseBuildingMultiPolygon(Relation pRelation, Perspective3D pers) {
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

    /**
     * Parse building model from multipolygon.
     *
     * @param pRelation
     * @param pPerspective
     * @return
     */
    static List<BuildingPart> parseBuildingMultiPolygonPart(Relation pRelation, Perspective3D pPerspective) {

        List<AreaWithHoles> waysPolygon = PolygonWithHolesUtil.findAreaWithHoles(pRelation);

        List<BuildingPart> ret = new ArrayList<BuildingPart>();

        for (AreaWithHoles waysPolygon2 : waysPolygon) {
            BuildingPart bp = parseBuildingPartAttributes(pRelation);

            DormerRoofModel roof = RoofParser.parse(pRelation, pPerspective);
            // DormerRoofModel roofClosedWay = RoofParser.parse(p,
            // pPerspective);
            // bp.setRoof(marge(roof, roofClosedWay));
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

    /**
     * @param pOsmPrimitive
     * @return
     */
    public static BuildingPart parseBuildingPartAttributes(OsmPrimitive pOsmPrimitive) {
        BuildingPart bp = new BuildingPart();
        bp.setMaxHeight(BuildingAttributeParser.parseMaxHeight(pOsmPrimitive));
        bp.setMinHeight(BuildingAttributeParser.parseMinHeight(pOsmPrimitive));
        bp.setMaxLevel(BuildingAttributeParser.parseMaxLevel(pOsmPrimitive));
        bp.setRoofLevels(BuildingAttributeParser.parseRoofLevels(pOsmPrimitive));
        bp.setMinLevel(BuildingAttributeParser.parseMinLevel(pOsmPrimitive));

        bp.setFacadeMaterialType(BuildingAttributeParser.parseFacadeMaterialName(pOsmPrimitive));
        bp.setFacadeColour(BuildingAttributeParser.parseFacadeColour(pOsmPrimitive));

        bp.setFloorMaterialType(BuildingAttributeParser.parseFloorMaterialName(pOsmPrimitive));
        bp.setFloorColour(BuildingAttributeParser.parseFloorColour(pOsmPrimitive));

        bp.setRoofMaterialType(BuildingAttributeParser.parseRoofMaterialName(pOsmPrimitive));
        bp.setRoofColour(BuildingAttributeParser.parseRoofColour(pOsmPrimitive));
        return bp;
    }

    /**
     * Parse building model from way.
     *
     * @param way
     * @param perspective
     * @return
     */
    public static BuildingModel parseBuildingWay(Way way, Perspective3D perspective) {
        BuildingModel bm = new BuildingModel();

        List<BuildingPart> bpList = parseBuildingPart(way, perspective);
        bm.setParts(bpList);
        return bm;
    }

    /**
     * Parse building model from relation.
     *
     * @param pRelation
     * @param pers
     * @return
     */
    public static BuildingModel parseBuildingRelation(Relation pRelation, Perspective3D pers) {
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

            if (BuildingType.OUTLINE.equals(bt) && !OsmAttributeKeys.BUILDING.primitiveKeyHaveAnyValue(member.getMember())) {
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

    private static BuildingPart cloneBuildingPart(BuildingPart bp, Double height) {
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

        ret.setFloorColour(bp.getFloorColour());
        ret.setFloorMaterialType(bp.getFloorMaterialType());

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

    enum BuildingType {
        OUTLINE, PARTS
    }

    private static boolean isRelationHaveBuildingParts(Relation pRelation) {

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

    private static Wall parseWall(Way way, Perspective3D pPerspective) {
        Wall wall = new Wall();

        WallPart wp = parseWallPart(new ReversableWay(way, false), pPerspective);

        wall.setWallParts(Arrays.asList(wp));

        return wall;
    }

    static Double parseWidth(OsmPrimitive primitive, Double defaultValue) {
        return ModelUtil.parseHeight(primitive.get("width"), defaultValue);
    }

    static boolean isClosedWay(RelationMember member) {
        if (OsmPrimitiveType.WAY.equals(member.getType()) && (member.getWay().isClosed())) {
            return true;
        }
        return false;
    }

    private static List<BuildingPart> parseBuildingPart(Way primitive, Perspective3D pPerspective) {

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

    private static List<BuildingPart> parseRelationClone(Way primitive, BuildingPart bp) {
        List<RelationCloneHeight> buildHeightClone = RelationCloneHeight.buildHeightClone(primitive);
        List<BuildingPart> bpList = new ArrayList<BuildingPart>();

        for (RelationCloneHeight relationCloneHeight : buildHeightClone) {
            for (Double height : relationCloneHeight) {

                bpList.add(cloneBuildingPart(bp, height));
            }
        }
        return bpList;
    }

    private static Wall parseWall(List<ReversableWay> rwList, Perspective3D pPerspective) {
        Wall wall = new Wall();
        List<WallPart> wp = new ArrayList<WallPart>();
        for (ReversableWay rw : rwList) {
            wp.add(parseWallPart(rw, pPerspective));
        }

        wall.setWallParts(wp);

        return wall;
    }

    private static WallPart parseWallPart(ReversableWay rw, Perspective3D pPerspective) {

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

        // String parseFacadeName =
        // BuildingAttributeParser.parseFacadeMaterialName(w);
        // wp.setFacadeTextureData(BuildingAttributeParser.parseFacadeTexture(parseFacadeName,
        // this.textureLibraryService));
        // wp.setColour(BuildingAttributeParser.parseFacadeColour(w));

        wp.setFacadeMaterialType(BuildingAttributeParser.parseFacadeMaterialName(way));
        wp.setFacadeColour(BuildingAttributeParser.parseFacadeColour(way));

        wp.setRoofMaterialType(BuildingAttributeParser.parseRoofMaterialName(way));
        wp.setRoofColour(BuildingAttributeParser.parseRoofColour(way));

        return wp;
    }

    private static List<BuildingWallElement> parseBuildingAttributeWallElement(Way w) {

        List<BuildingWallElement> ret = new ArrayList<BuildingWallElement>();

        WindowGridBuildingElement wgbe = BuildingAttributeParser.parseWallWindowsColumns(w);
        if (wgbe != null) {
            ret.add(wgbe);
        }

        return ret;
    }

    private static WallNode parseWallNode(Node node, Perspective3D pPerspective) {
        WallNode wn = new WallNode();
        wn.setPoint(pPerspective.calcPoint(node));

        List<BuildingNodeElement> buildingElements = new ArrayList<BuildingNodeElement>();
        wn.setBuildingNodeElements(buildingElements);

        List<RelationCloneHeight> buildHeightClone = RelationCloneHeight.buildHeightClone(node);

        if (isAttribute(node, OsmAttributeKeys.BUILDING, OsmAttributeValues.ENTRANCE)
                || isAnyAttribute(node, OsmAttributeKeys.ENTRANCE)) {

            EntranceBuildingElement entrance = new EntranceBuildingElement();
            entrance.setHeight(ModelUtil.getHeight(node, entrance.getHeight()));
            entrance.setMinHeight(ModelUtil.getMinHeight(node, entrance.getMinHeight()));
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
            entrance.setHeight(ModelUtil.getHeight(node, entrance.getHeight()));
            entrance.setMinHeight(ModelUtil.getMinHeight(node, entrance.getMinHeight()));

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

    private static BuildingNodeElement clone(BuildingNodeElement entrance) {
        try {
            return (WindowBuildingElement) entrance.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("error cloning WindowBuildingElement", e);
        }
    }

    private static boolean isAttribute(OsmPrimitive prim, OsmAttributeKeys key, OsmAttributeValues val) {
        return val.getValue().equals(prim.get(key.getKey()));
    }

    private static boolean isAnyAttribute(OsmPrimitive prim, OsmAttributeKeys key) {
        return !StringUtil.isBlankOrNull(prim.get(key.getKey()));
    }
}
