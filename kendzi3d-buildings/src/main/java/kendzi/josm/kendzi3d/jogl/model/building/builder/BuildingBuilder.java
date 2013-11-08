/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.building.builder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.BuildingElementsTextureManager;
import kendzi.jogl.texture.library.TextureFindCriteria;
import kendzi.jogl.texture.library.TextureFindCriteria.Type;
import kendzi.josm.kendzi3d.jogl.model.building.builder.roof.DormerRoofBuilder;
import kendzi.josm.kendzi3d.jogl.model.building.builder.roof.RoofLinesBuildier;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingModel;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingUtil;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingWallElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.NodeBuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.SphereNodeBuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.Wall;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallNode;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.WindowGridBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.BuildingNodeElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.EntranceBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.SquareHoleElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.WindowBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.roof.RoofLinesModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType5v6;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitUtil;

/**
 * Builder for 3d model of building.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class BuildingBuilder {

    /**
     * Build 3d Model of building.
     *
     * @param buildingModel building model
     * @param tm texture manager
     * @return building model and debug informations
     */
    public static BuildingOutput buildModel(BuildingModel buildingModel, BuildingElementsTextureManager tm) {

        List<BuildingPartOutput> partsOut = new ArrayList<BuildingPartOutput>();

        ModelFactory mf = ModelFactory.modelBuilder();

        if (buildingModel.getParts() != null) {

            for (BuildingPart bp : buildingModel.getParts()) {

                partsOut.add(buildPart(bp, buildingModel, mf, tm));
            }
        }

        if (buildingModel.getNodeParts() != null) {

            for (NodeBuildingPart bp : buildingModel.getNodeParts()) {
                partsOut.add(builNodePart(bp, buildingModel, mf, tm));
            }
        }

        BuildingOutput out = new BuildingOutput();
        out.setModel(mf.toModel());
        out.setBuildingPartOutput(partsOut);
        return out;
    }

    private static boolean isWallCounterClockwise(Wall wall) {
        PolygonList2d wallToPolygon = BuildingUtil.wallToOuterPolygon(wall);

        if (0.0f < Triangulate.area(wallToPolygon.getPoints())) {
            return true;
        }
        return false;
    }

    private static BuildingPartOutput builNodePart(NodeBuildingPart bp, BuildingModel buildingModel, ModelFactory mf,
            BuildingElementsTextureManager tm) {

        if (bp instanceof SphereNodeBuildingPart) {
            Color floorColor = takeFacadeColor(buildingModel, bp, tm);
            TextureData floorTD = takeFacadeTextureData(buildingModel, bp, tm, floorColor != null);

            String tex0Key = floorTD.getTex0();
            Material mat = MaterialFactory.createTextureColorMaterial(tex0Key, floorColor);


            MeshFactory mesh = mf.addMesh("NodePart");
            mesh.hasTexture = true;
            mesh.materialID = mf.cacheMaterial(mat);

            SphereNodeBuildingPart sphere = (SphereNodeBuildingPart) bp;
            int pIcross = 12;
            int icross = pIcross  + 1;

            double height = sphere.getHeight();
            double radius = sphere.getRadius();
            Point2d point = sphere.getPoint();

            // create cross section
            Point2d [] crossSection = new Point2d[icross];
            for (int i = 0; i < icross; i++) {
                double a = Math.toRadians(180) / (icross - 1) * i - Math.toRadians(90);

                crossSection[i] = new Point2d(Math.cos(a) * radius, Math.sin(a) * radius + height);
            }

            int pIsection = 12;
            RoofType5v6.buildRotaryShape(mesh, point, pIsection, crossSection, true);

        }
        return new BuildingPartOutput();
    }

    private static BuildingPartOutput buildPart(BuildingPart bp, BuildingModel buildingModel, ModelFactory mf,
            BuildingElementsTextureManager tm) {

        BuildingPartOutput partOutput = new BuildingPartOutput();

        CatchFaceFactory catchFaceFactory = new CatchFaceFactory(mf);

        Wall w = bp.getWall();

        boolean cc = isWallCounterClockwise(w);

        double maxHeight = bp.getDefaultMaxHeight();


        double minHeight = bp.getDefaultMinHeight();

        WallPart firstWallPart = getFirstWallPart(w);
        Color facadeColor = takeFacadeColor(buildingModel, bp, w, firstWallPart, tm);
        Color roofColor = takeRoofColor(buildingModel, bp, w, firstWallPart, tm);
        TextureData roofTextureData = takeRoofTextureData(buildingModel, bp, w, tm, roofColor != null);
        //XXX
        TextureData facadeTextureData = takeFacadeTextureData(buildingModel, bp, w, firstWallPart, tm, facadeColor != null);


        RoofOutput roofOutput = buildRoof(bp, mf, maxHeight, facadeColor, roofColor, facadeTextureData, roofTextureData);

        double wallHeight = maxHeight - roofOutput.getHeight();

        buildWall(w, cc, minHeight, wallHeight, bp, buildingModel, mf, catchFaceFactory, tm);

        buildFloor(bp, buildingModel, mf, tm, roofTextureData, facadeColor, minHeight);

        if (bp.getInlineWalls() != null) {
            for (Wall in : bp.getInlineWalls()) {
                boolean ccc = isWallCounterClockwise(w);

                buildWall(in, ccc, minHeight, wallHeight, bp, buildingModel, mf, catchFaceFactory, tm);
            }
        }

        partOutput.setRoofDebugOut(roofOutput.getDebug());
        partOutput.setEdges(roofOutput.getEdges());
        // partOutput.setFirstPoint(getFirstWallPoint(w));

        return partOutput;
    }

    /**
     * @param bp
     * @param mf
     * @param maxHeight
     * @param facadeColor
     * @param roofColor
     * @param facadeTextureData
     * @param roofTextureData
     * @return
     */
    private static RoofOutput buildRoof(BuildingPart bp, ModelFactory mf, double maxHeight, Color facadeColor, Color roofColor,
            TextureData facadeTextureData, TextureData roofTextureData) {
        RoofTextureData rtd = new RoofTextureData();
        // XXX fix, currently roof builder support only one texture, fix before roof builder is changed.

        rtd.setFacadeColor(facadeColor);
        rtd.setFacadeTexture(facadeTextureData);
        rtd.setRoofColor(roofColor);
        rtd.setRoofTexture(roofTextureData);
        RoofOutput roofOutput = null;
        if (bp.getRoof() instanceof DormerRoofModel) {
            roofOutput = DormerRoofBuilder.build(bp, maxHeight, mf, rtd);

        } else if (bp.getRoof() instanceof RoofLinesModel) {
            roofOutput = RoofLinesBuildier.build(bp, maxHeight, mf, roofTextureData, roofColor);
        } else {
            throw new RuntimeException();
        }
        return roofOutput;
    }

    private static void buildFloor(BuildingPart bp, BuildingModel buildingModel, ModelFactory mf,
            BuildingElementsTextureManager tm, TextureData roofTextureData, Color facadeColor, double minHeight) {
        Color floorColor = takeFloorColor(buildingModel, bp, tm);
        TextureData floorTD = takeFloorTextureData(buildingModel, bp, tm, floorColor != null);

        String tex0Key = floorTD.getTex0();
        Material mat = MaterialFactory.createTextureColorMaterial(tex0Key, floorColor);


        MeshFactory mesh = mf.addMesh("FloorPart");
        mesh.hasTexture = true;
        mesh.materialID = mf.cacheMaterial(mat);

        PolygonWithHolesList2d buildingPolygon = BuildingUtil.buildingPartToPolygonWithHoles(bp);
        MeshFactoryUtil.addPolygonWithHolesInYRevert(buildingPolygon, minHeight, mesh, roofTextureData, 0, 0, new Vector3d(1, 0, 0));
    }

    private static Point2d getFirstWallPoint(Wall w) {
        WallPart firstWallPart = getFirstWallPart(w);

        if (firstWallPart != null
                && firstWallPart.getNodes() != null
                && firstWallPart.getNodes().size() > 0) {
            return firstWallPart.getNodes().get(0).getPoint();
        }

        return null;
    }

    private static WallPart getFirstWallPart(Wall w) {

        if (w == null) {
            return null;
        }

        if (w.getWallParts() != null && w.getWallParts().size() > 0) {
            return w.getWallParts().get(0);
        }

        return null;
    }

    private static void buildWall(Wall w, boolean counterClockwise, double minHeight, double wallHeight, BuildingPart bp,
            BuildingModel buildingModel, ModelFactory mf, CatchFaceFactory catchFaceFactory, BuildingElementsTextureManager tm) {

        int wpCount = -1;
        for (WallPart wp : w.getWallParts()) {
            wpCount++;


            double wallLength = calcWallPartLength(wp);

            List<WallNode> nodes = wp.getNodes();
            int size = nodes.size();

            Color facadeColor = takeFacadeColor(buildingModel, bp, w, wp, tm);
            TextureData facadeTD = takeFacadeTextureData(buildingModel, bp, w, wp, tm, facadeColor != null);

            String tex0Key = facadeTD.getTex0();
            Material mat = MaterialFactory.createTextureColorMaterial(tex0Key, facadeColor);

            if (facadeTD.getTex1() != null) {
                mat.getTexturesComponent().add(facadeTD.getTex1());
            }

            Integer windowsCols = hasWindowsCloumns(wp.getBuildingElements());
            //windowsCols = 0;
            boolean isWindows = windowsCols != null && windowsCols != 0;
            TextureData windowsTexture = null;
            if (isWindows) {

                TextureData windowsTD = takeWindowsColumnsTextureData(buildingModel, bp, w, wp, tm);
                if (windowsTD.getTex0() != null) {
                    mat.getTexturesComponent().add(windowsTD.getTex0());
                }


                double windowsSegmetLength = wallLength / (double)windowsCols;

                int facadeLevels = getFacadeLevels(w, bp);
                double windowsSegmentHeight = (wallHeight - minHeight) / facadeLevels;


                windowsTexture = new TextureData(null, windowsSegmetLength, windowsSegmentHeight);


            }

            MeshFactory mesh = mf.addMesh("WallPart: " + wpCount);
            mesh.hasTexture = true;
            mesh.materialID = mf.cacheMaterial(mat);

            boolean isOverLayer = facadeTD.getTex1() != null;

            int texNum = 1 + (isOverLayer ? 1 : 0) + (isWindows ? 1 : 0);

            FaceFactory face = mesh.addFace(FaceType.QUADS, texNum);

            double wallDistance = 0;

            for (int i = 0; i < size - 1; i++) {
                WallNode n1 = nodes.get(i);
                WallNode n2 = nodes.get((i + 1) % size);

                //List<WallHole> holesEnd = createEndHoles(n2);

                Point2d start = n1.getPoint();
                double segmentDistance = n1.getPoint().distance(n2.getPoint());
                Vector2d direction = new Vector2d(n2.getPoint());
                direction.sub(n1.getPoint());
                direction.normalize();

                // List<WallHole> holes = createHoles(n1);


                Point2d vbl = new Point2d(0, minHeight);
                Point2d vbr = new Point2d(segmentDistance, minHeight);

                Point2d vtr = new Point2d(segmentDistance, wallHeight);
                Point2d vtl = new Point2d(0, wallHeight);

                PolygonList2d poly = new PolygonList2d(vbl, vbr, vtr, vtl);
                MultiPolygonList2d mPoly = new MultiPolygonList2d(poly);

                //List<List<Point2d>> holes = new ArrayList<List<Point2d>>();
                //
                //                mPoly = applyBuildingElements(mPoly, n1, 0, false );
                //                mPoly = cutHoles(mPoly, n1, 0, false);
                //                mPoly = cutHoles(mPoly, n2, segmentDistance, true);

                //                MultiPolygonList2d mPoly,
                //                WallNode n1,
                //                Vector2d direction,
                //                double displacement,
                //                boolean beginEnd,
                //                CatchFaceFactory pCatchFaceFactory,
                //                BuildingElementsTextureMenager pTextureMenager


                //                mPoly = cutHoles(mPoly, holes);

                // build mesh


                Vector3d normal = new Vector3d(-direction.y, 0, -direction.x);
                if (counterClockwise) {
                    normal.negate();
                }

                int iN = mesh.addNormal(normal);

                mPoly = applyWindows(mPoly, n1.getBuildingNodeElements(), start, direction, 0, false, catchFaceFactory, tm, counterClockwise);
                mPoly = applyWindows(mPoly, n2.getBuildingNodeElements(), start, direction, segmentDistance, true, catchFaceFactory, tm, counterClockwise);

                for (PolygonList2d polygon : mPoly.getPolygons()) {
                    List<Point2d> points = polygon.getPoints();
                    if (points.size() > 4) {
                        throw new RuntimeException("more then 4 points in quate!" + points);
                    }


                    for (Point2d p : points) {
                        //addPointToMesh(p, start, direction, mesh, face, iN, facadeTexture);
                        int vi = segmentPointToVertex3dIndex(p, start, direction, mesh);

                        int [] tex = new int[texNum];

                        tex[0] = segmentPointToTextureDataIndex(p, 0d, 0d,  mesh, facadeTD);

                        if (isOverLayer) {
                            tex[1] = tex[0];
                        }

                        if (isWindows) {
                            tex[texNum - 1] = segmentPointToTextureDataIndex(p, wallDistance, 0d, mesh, windowsTexture);
                        }

                        face.addVert(vi, iN, tex);
                    }
                }
                //FaceFactory face = pMeshBorder.addFace(FaceType.QUADS);
                wallDistance += segmentDistance;
            }
        }
    }

    private static double calcWallPartLength(WallPart wp) {
        double wallLength = 0;
        List<WallNode> nodes = wp.getNodes();
        int size = nodes.size();
        WallNode n1 = nodes.get(0);
        for (int i = 1; i < size; i++) {
            WallNode n2 = nodes.get(i);

            wallLength += n1.getPoint().distance(n2.getPoint());

            n1 = n2;
        }
        return wallLength;
    }

    private static int getFacadeLevels(Wall w, BuildingPart bp) {

        return bp.getDefaultMaxLevel() - bp.getDefaultMinLevel() - bp.getDefaultRoofLevels();
    }

    private static Integer hasWindowsCloumns(List<BuildingWallElement> list) {
        if (list == null) {
            return null;
        }

        for (BuildingWallElement buildingWallElement : list) {
            if (buildingWallElement instanceof WindowGridBuildingElement) {
                WindowGridBuildingElement w = (WindowGridBuildingElement) buildingWallElement;

                int numOfCols = w.getNumOfCols();
                if (numOfCols > 0) {
                    return numOfCols;
                }
                return null;
            }
        }

        return null;
    }

    private static TextureData takeWindowsColumnsTextureData(
            BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp, BuildingElementsTextureManager tm) {


        String mt = null;

        TextureData td = tm.findTexture(new TextureFindCriteria(Type.WINDOWS, mt, null, null, null, false));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    private static TextureData takeFacadeTextureData(BuildingModel buildingModel, NodeBuildingPart bp,
            BuildingElementsTextureManager tm, boolean colorable) {

        String mt = null;

        if (bp.getFacadeMaterialType() != null) {
            mt = bp.getFacadeMaterialType();
        } else if (buildingModel.getFacadeMaterialType() != null) {
            mt = buildingModel.getFacadeMaterialType();
        }

        TextureData td = tm.findTexture(new TextureFindCriteria(Type.FACADE, mt, null, null, null, colorable));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    private static TextureData takeFacadeTextureData(BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp,
            BuildingElementsTextureManager tm, boolean colorable) {

        String mt = null;

        if (wp != null && wp.getFacadeMaterialType() != null) {
            mt = wp.getFacadeMaterialType();
        } else  if (w.getFacadeMaterialType() != null) {
            mt = w.getFacadeMaterialType();
        } else  if (bp.getFacadeMaterialType() != null) {
            mt = bp.getFacadeMaterialType();
        } else  if (buildingModel.getFacadeMaterialType() != null) {
            mt = buildingModel.getFacadeMaterialType();
        }

        TextureData td = tm.findTexture(new TextureFindCriteria(Type.FACADE, mt, null, null, null, colorable));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    private static TextureData takeFloorTextureData(BuildingModel buildingModel, BuildingPart bp,
            BuildingElementsTextureManager tm, boolean colorable) {

        String mt = null;

        if (bp.getFloorMaterialType() != null) {
            mt = bp.getFloorMaterialType();
        } else  if (buildingModel.getFloorMaterialType() != null) {
            mt = buildingModel.getFloorMaterialType();
        }

        TextureData td = tm.findTexture(new TextureFindCriteria(Type.FLOOR, mt, null, null, null, colorable));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    private static Color takeFacadeColor(BuildingModel buildingModel, NodeBuildingPart bp, BuildingElementsTextureManager tm) {

        Color c = null;

        if (bp.getFacadeColor() != null) {
            c = bp.getFacadeColor();
        } else if (buildingModel.getFacadeColor() != null) {
            c = buildingModel.getFacadeColor();
        }

        return c;
    }

    private static Color takeFacadeColor(BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp,
            BuildingElementsTextureManager tm) {

        Color c = null;

        if (wp != null && wp.getFacadeColor() != null) {
            c = wp.getFacadeColor();
        } else if (w.getFacadeColor() != null) {
            c = w.getFacadeColor();
        } else if (bp.getFacadeColor() != null) {
            c = bp.getFacadeColor();
        } else if (buildingModel.getFacadeColor() != null) {
            c = buildingModel.getFacadeColor();
        }

        return c;
    }

    private static Color takeFloorColor(BuildingModel buildingModel, BuildingPart bp, BuildingElementsTextureManager tm) {

        Color c = null;

        if (bp.getFloorColor() != null) {
            c = bp.getFloorColor();
        } else if (buildingModel.getFloorColor() != null) {
            c = buildingModel.getFloorColor();
        }

        return c;
    }

    private static Color takeRoofColor(BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp,
            BuildingElementsTextureManager tm) {

        Color c = null;

        if (wp != null && wp.getRoofColor() != null) {
            c = wp.getRoofColor();
        } else if (w.getRoofColor() != null) {
            c = w.getRoofColor();
        } else if (bp.getRoofColor() != null) {
            c = bp.getRoofColor();
        } else if (buildingModel.getRoofColor() != null) {
            c = buildingModel.getRoofColor();
        }

        return c;
    }

    private static TextureData takeRoofTextureData(BuildingModel buildingModel, BuildingPart bp, Wall w,
            BuildingElementsTextureManager tm, boolean colorable) {

        String mt = null;

        if (w.getRoofMaterialType() != null) {
            mt = w.getRoofMaterialType();
        } else  if (bp.getRoofMaterialType() != null) {
            mt = bp.getRoofMaterialType();
        } else  if (buildingModel.getRoofMaterialType() != null) {
            mt = buildingModel.getRoofMaterialType();
        }

        TextureData td = tm.findTexture(new TextureFindCriteria(Type.ROOF, mt, null, null, null, colorable));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    private static MultiPolygonList2d applyWindows(MultiPolygonList2d mPoly, List<BuildingNodeElement> pBuildingNodeElements,
            Point2d segmentStart, Vector2d segmentDirection, double displacementOnSegment, boolean beginEnd,
            CatchFaceFactory pCatchFaceFactory, BuildingElementsTextureManager pTextureMenager, boolean counterClockwise) {

        if (pBuildingNodeElements != null) {
            for (BuildingNodeElement be : pBuildingNodeElements) {

                if (beginEnd) {
                    mPoly = applyWindowsEnd(mPoly, be, segmentStart, segmentDirection, displacementOnSegment, pCatchFaceFactory, pTextureMenager, counterClockwise);
                } else {
                    mPoly = applyWindowsBegin(mPoly, be, segmentStart, segmentDirection, displacementOnSegment, pCatchFaceFactory, pTextureMenager, counterClockwise);
                }
            }
        }
        return mPoly;
    }

    private static MultiPolygonList2d applyWindowsBegin(MultiPolygonList2d mPoly, BuildingNodeElement be, Point2d segmentStart,
            Vector2d segmentDirection, double nodeDisplacement, CatchFaceFactory pCatchFaceFactory,
            BuildingElementsTextureManager pTextureMenager, boolean counterClockwise) {

        if (be instanceof SquareHoleElement) {
            SquareHoleElement she = (SquareHoleElement) be;

            Point2d mbp = new Point2d(0, she.getMinHeight());
            Point2d rbp = new Point2d(she.getWidth() / 2.0, she.getMinHeight());
            Point2d rtp = new Point2d(she.getWidth() / 2.0, she.getMaxHeight());
            Point2d mtp = new Point2d(0 , she.getMaxHeight());

            mPoly = PolygonSplitUtil.unionOfFrontPart(mPoly,
                    new LinePoints2d(rtp, rbp),
                    new LinePoints2d(mtp, rtp),
                    new LinePoints2d(rbp, mbp)
                    );

            TextureData td = findWindowTextureData(be, pTextureMenager);

            MeshFactory mesh = pCatchFaceFactory.createOrGetMeshFactory(td.getTex0());

            FaceFactory face = mesh.addFace(FaceType.QUADS);

            Vector3d n = new Vector3d(-segmentDirection.y, 0, -segmentDirection.x);
            if (counterClockwise) {
                n.negate();
            }

            int iN = mesh.addNormal(n);

            int imbTc = mesh.addTextCoord(new TextCoord(0.5, 0));
            int imtTc = mesh.addTextCoord(new TextCoord(0.5, 1));
            int irbTc = mesh.addTextCoord(new TextCoord(1, 0));
            int irtTc = mesh.addTextCoord(new TextCoord(1, 1));

            face.addVert(
                    segmentPointToVertex3dIndex(mbp, segmentStart, segmentDirection, mesh),
                    imbTc,
                    iN);
            face.addVert(
                    segmentPointToVertex3dIndex(rbp, segmentStart, segmentDirection, mesh),
                    irbTc,
                    iN);
            face.addVert(
                    segmentPointToVertex3dIndex(rtp, segmentStart, segmentDirection, mesh),
                    irtTc,
                    iN);
            face.addVert(
                    segmentPointToVertex3dIndex(mtp, segmentStart, segmentDirection, mesh),
                    imtTc,
                    iN);

        }

        return mPoly;
    }

    private static MultiPolygonList2d applyWindowsEnd(MultiPolygonList2d mPoly, BuildingNodeElement be, Point2d segmentStart,
            Vector2d segmentDirection, double nodeDisplacement, CatchFaceFactory pCatchFaceFactory,
            BuildingElementsTextureManager pTextureMenager, boolean counterClockwise) {

        if (be instanceof SquareHoleElement) {
            SquareHoleElement she = (SquareHoleElement) be;


            Point2d lbp = new Point2d(nodeDisplacement - she.getWidth() / 2.0, she.getMinHeight());
            Point2d mbp = new Point2d(nodeDisplacement, she.getMinHeight());
            Point2d mtp = new Point2d(nodeDisplacement, she.getMaxHeight());
            Point2d ltp = new Point2d(nodeDisplacement - she.getWidth() / 2.0, she.getMaxHeight());


            mPoly = PolygonSplitUtil.unionOfFrontPart(mPoly,
                    new LinePoints2d(lbp, ltp),
                    new LinePoints2d(ltp, mtp),
                    new LinePoints2d(mbp, lbp)
                    );


            TextureData td = findWindowTextureData(be, pTextureMenager);

            MeshFactory mesh = pCatchFaceFactory.createOrGetMeshFactory(td.getTex0());

            FaceFactory face = mesh.addFace(FaceType.QUADS);

            Vector3d n = new Vector3d(-segmentDirection.y, 0, -segmentDirection.x);
            if (counterClockwise) {
                n.negate();
            }

            int iN = mesh.addNormal(n);

            int imbTc = mesh.addTextCoord(new TextCoord(0.5, 0));
            int imtTc = mesh.addTextCoord(new TextCoord(0.5, 1));
            int ilbTc = mesh.addTextCoord(new TextCoord(0, 0));
            int iltTc = mesh.addTextCoord(new TextCoord(0, 1));

            face.addVert(
                    segmentPointToVertex3dIndex(lbp, segmentStart, segmentDirection, mesh),
                    ilbTc,
                    iN);
            face.addVert(
                    segmentPointToVertex3dIndex(mbp, segmentStart, segmentDirection, mesh),
                    imbTc,
                    iN);
            face.addVert(
                    segmentPointToVertex3dIndex(mtp, segmentStart, segmentDirection, mesh),
                    imtTc,
                    iN);
            face.addVert(
                    segmentPointToVertex3dIndex(ltp, segmentStart, segmentDirection, mesh),
                    iltTc,
                    iN);

        }

        return mPoly;
    }

    private static TextureData findWindowTextureData(BuildingNodeElement be, BuildingElementsTextureManager pTextureMenager) {

        TextureData td = null;
        if (be instanceof WindowBuildingElement) {
            WindowBuildingElement wbe = (WindowBuildingElement) be;

            td = pTextureMenager.findTexture(
                    new TextureFindCriteria(Type.WINDOW, wbe.getWindowType(), null, wbe.getWidth(), wbe.getHeight(), false));
        } else if (be instanceof EntranceBuildingElement) {
            EntranceBuildingElement wbe = (EntranceBuildingElement) be;

            td = pTextureMenager.findTexture(
                    new TextureFindCriteria(Type.ENTERENCE, wbe.getEntranceType(), null, wbe.getWidth(), wbe.getHeight(), false));
        } else{
            throw new RuntimeException("unsuported buidlding element " + be);
        }

        if (td == null) {
            td = createEmptyTextureData();
        }
        return td;
    }


    static class CatchFaceFactory {
        private Map<String, MeshFactory> catchMesh = new HashMap<String, MeshFactory>();
        private ModelFactory mf;

        public CatchFaceFactory(ModelFactory mf) {
            this.mf = mf;
        }

        MeshFactory createOrGetMeshFactory(String texture0Key, String texture1Key, Color color) {
            throw new RuntimeException("TODO");
        }

        MeshFactory createOrGetMeshFactory(String textureKey) {

            MeshFactory meshFactory = this.catchMesh.get(textureKey);
            if (meshFactory == null) {
                meshFactory = this.mf.addMesh(textureKey);

                Material mat = MaterialFactory.createTextureMaterial(textureKey);
                int iMat = this.mf.addMaterial(mat);

                meshFactory.materialID = iMat;
                meshFactory.hasTexture = true;

                this.catchMesh.put(textureKey, meshFactory);
            }

            return meshFactory;
        }
    }

    private static TextureData createEmptyTextureData() {
        return new TextureData(null, 1, 1);
    }

    private static int segmentPointToTextureDataIndex(Point2d point, double offsetX, double offsetY, MeshFactory mesh,
            TextureData td) {

        return mesh.addTextCoord(new TextCoord((point.x + offsetX) / td.getWidth(), (point.y + offsetY) / td.getHeight()));
    }

    private static int segmentPointToVertex3dIndex(Point2d point, Point2d start, Vector2d direction, MeshFactory mesh) {
        Point3d vertex = new Point3d(direction.x, 0, -direction.y);

        vertex.scale(point.x);
        vertex.x += start.x;
        vertex.z -= start.y;

        vertex.y = point.y;

        int iV = mesh.addVertex(vertex);
        return iV;
    }


    static class WallHole {
        private Point2d ldp;
        private Point2d rdp;
        private Point2d rtp;
        private Point2d ltp;

        /**
         * @return the ldp
         */
        public Point2d getLdp() {
            return this.ldp;
        }

        /**
         * @param ldp the ldp to set
         */
        public void setLdp(Point2d ldp) {
            this.ldp = ldp;
        }

        /**
         * @return the rdp
         */
        public Point2d getRdp() {
            return this.rdp;
        }

        /**
         * @param rdp the rdp to set
         */
        public void setRdp(Point2d rdp) {
            this.rdp = rdp;
        }

        /**
         * @return the rtp
         */
        public Point2d getRtp() {
            return this.rtp;
        }

        /**
         * @param rtp the rtp to set
         */
        public void setRtp(Point2d rtp) {
            this.rtp = rtp;
        }

        /**
         * @return the ltp
         */
        public Point2d getLtp() {
            return this.ltp;
        }

        /**
         * @param ltp the ltp to set
         */
        public void setLtp(Point2d ltp) {
            this.ltp = ltp;
        }
    }

}
