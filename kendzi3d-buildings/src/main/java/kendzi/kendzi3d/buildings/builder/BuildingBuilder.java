/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder;

import static kendzi.kendzi3d.buildings.builder.util.BuildingTextureUtil.findWindowTextureData;
import static kendzi.kendzi3d.buildings.builder.util.BuildingTextureUtil.takeFacadeTextureData;
import static kendzi.kendzi3d.buildings.builder.util.BuildingTextureUtil.takeFloorTextureData;
import static kendzi.kendzi3d.buildings.builder.util.BuildingTextureUtil.takeWindowsColumnsTextureData;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import kendzi.kendzi3d.buildings.builder.dto.RoofOutput;
import kendzi.kendzi3d.buildings.builder.dto.RoofTextureData;
import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.height.SegmentHeight;
import kendzi.kendzi3d.buildings.builder.roof.lines.RoofLinesBuildier;
import kendzi.kendzi3d.buildings.builder.roof.shape.ShapeRoofBuilder;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofType5v6;
import kendzi.kendzi3d.buildings.builder.util.SimpleTriangulateInterable;
import kendzi.kendzi3d.buildings.model.BuildingModel;
import kendzi.kendzi3d.buildings.model.BuildingPart;
import kendzi.kendzi3d.buildings.model.BuildingUtil;
import kendzi.kendzi3d.buildings.model.BuildingWallElement;
import kendzi.kendzi3d.buildings.model.NodeBuildingPart;
import kendzi.kendzi3d.buildings.model.SphereNodeBuildingPart;
import kendzi.kendzi3d.buildings.model.Wall;
import kendzi.kendzi3d.buildings.model.WallNode;
import kendzi.kendzi3d.buildings.model.WallPart;
import kendzi.kendzi3d.buildings.model.WindowGridBuildingElement;
import kendzi.kendzi3d.buildings.model.element.BuildingNodeElement;
import kendzi.kendzi3d.buildings.model.element.SquareHoleElement;
import kendzi.kendzi3d.buildings.model.roof.lines.RoofLinesModel;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRoofModel;
import kendzi.kendzi3d.buildings.output.BuildingOutput;
import kendzi.kendzi3d.buildings.output.BuildingPartOutput;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * Builder for 3d model of building.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class BuildingBuilder {

    private static final double EPSILON = 1e-10;

    /**
     * Build 3d Model of building.
     *
     * @param buildingModel
     *            building model
     * @param tm
     *            texture manager
     * @return building model and debug informations
     */
    public static BuildingOutput buildModel(BuildingModel buildingModel, BuildingElementsTextureManager tm) {

        List<BuildingPartOutput> partsOut = new ArrayList<>();

        ModelFactory mf = ModelFactory.modelBuilder();

        if (buildingModel.getParts() != null) {

            for (BuildingPart bp : buildingModel.getParts()) {

                partsOut.add(buildPart(bp, buildingModel, mf, tm));
            }
        }

        if (buildingModel.getNodeParts() != null) {

            for (NodeBuildingPart bp : buildingModel.getNodeParts()) {
                partsOut.add(buildNodePart(bp, buildingModel, mf, tm));
            }
        }

        BuildingOutput out = new BuildingOutput();
        out.setModel(mf.toModel());
        out.setBuildingPartOutput(partsOut);
        return out;
    }

    private static boolean isWallCounterClockwise(Wall wall) {
        PolygonList2d wallToPolygon = BuildingUtil.wallToOuterPolygon(wall);

        return 0.0f < Triangulate.area(wallToPolygon.getPoints());
    }

    private static BuildingPartOutput buildNodePart(NodeBuildingPart bp, BuildingModel buildingModel, ModelFactory mf,
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
            int icross = pIcross + 1;

            double height = sphere.getHeight();
            double radius = sphere.getRadius();
            Vector2dc point = sphere.getPoint();

            // create cross section
            Vector2dc[] crossSection = new Vector2dc[icross];
            for (int i = 0; i < icross; i++) {
                double a = Math.toRadians(180) / (icross - 1) * i - Math.toRadians(90);

                crossSection[i] = new Vector2d(Math.cos(a) * radius, Math.sin(a) * radius + height);
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

        double maxHeight = bp.getDefaultMaxHeight();
        double minHeight = bp.getDefaultMinHeight();

        WallPart firstWallPart = getFirstWallPart(w);
        Color facadeColor = takeFacadeColor(buildingModel, bp, w, firstWallPart, tm);
        Color roofColor = takeRoofColor(buildingModel, bp, w, firstWallPart, tm);
        TextureData roofTextureData = takeRoofTextureData(buildingModel, bp, w, tm, roofColor != null);
        // XXX
        TextureData facadeTextureData = takeFacadeTextureData(buildingModel, bp, w, firstWallPart, tm, facadeColor != null);

        RoofOutput roofOutput = buildRoof(bp, mf, maxHeight, facadeColor, roofColor, facadeTextureData, roofTextureData);

        double wallHeight = maxHeight - roofOutput.getHeight();

        buildWall(w, minHeight, wallHeight, bp, buildingModel, mf, catchFaceFactory, tm, roofOutput.getHeightCalculator());

        buildFloor(bp, buildingModel, mf, tm, roofTextureData, facadeColor, minHeight);

        if (bp.getInlineWalls() != null) {
            for (Wall in : bp.getInlineWalls()) {
                buildWall(in, minHeight, wallHeight, bp, buildingModel, mf, catchFaceFactory, tm,
                        roofOutput.getHeightCalculator());
            }
        }

        partOutput.setRoofDebug(roofOutput.getDebug());
        partOutput.setEdges(roofOutput.getEdges());

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
        // XXX fix, currently roof builder support only one texture, fix before
        // roof builder is changed.

        rtd.setFacadeColor(facadeColor);
        rtd.setFacadeTexture(facadeTextureData);
        rtd.setRoofColor(roofColor);
        rtd.setRoofTexture(roofTextureData);
        RoofOutput roofOutput = null;
        if (bp.getRoof() instanceof DormerRoofModel) {
            roofOutput = ShapeRoofBuilder.build(bp, maxHeight, mf, rtd);

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
        MeshFactoryUtil.addPolygonWithHolesInYRevert(buildingPolygon, minHeight, mesh, roofTextureData, 0, 0,
                new Vector3d(1, 0, 0));
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

    private static void buildWall(Wall w, double minHeight, double wallHeight, BuildingPart bp, BuildingModel buildingModel,
            ModelFactory mf, CatchFaceFactory catchFaceFactory, BuildingElementsTextureManager tm,
            HeightCalculator roofHeightCalculator) {

        boolean counterClockwise = isWallCounterClockwise(w);

        int numberOfParts = w.getWallParts().size();
        // always iterate counter clockwise
        for (int iPart = 0; iPart < numberOfParts; iPart++) {

            int partNumber = iPart;
            if (!counterClockwise) {
                partNumber = numberOfParts - partNumber - 1;
            }

            WallPart wp = w.getWallParts().get(partNumber);

            double wallLength = calcWallPartLength(wp);

            List<WallNode> nodes = wp.getNodes();
            int size = nodes.size();

            Color facadeColor = takeFacadeColor(buildingModel, bp, w, wp, tm);
            TextureData facadeTextureData = takeFacadeTextureData(buildingModel, bp, w, wp, tm, facadeColor != null);

            TextureData adjustedWindowsTextureData = generateWindowsOverlayTextureData(w, minHeight, wallHeight, bp,
                    buildingModel, tm, wp, wallLength);

            Material mat = createWallMaterial(facadeColor, facadeTextureData, adjustedWindowsTextureData);

            MeshFactory mesh = createWallMesh(mf, partNumber, mat);

            boolean isOverLayer = facadeTextureData.getTex1() != null;
            boolean isWindows = adjustedWindowsTextureData != null;

            int texNum = 1 + (isOverLayer ? 1 : 0) + (isWindows ? 1 : 0);

            FaceFactory face = mesh.addFace(FaceType.TRIANGLES, texNum);

            double wallDistance = 0;

            for (int i = 0; i < size - 1; i++) {

                int beginWallNodeIndex = i;
                int endWallNodeIndex = (beginWallNodeIndex + 1) % size;

                if (!counterClockwise) {
                    beginWallNodeIndex = size - beginWallNodeIndex - 1;
                    endWallNodeIndex = size - endWallNodeIndex - 1;
                }

                WallNode n1 = nodes.get(beginWallNodeIndex);
                WallNode n2 = nodes.get(endWallNodeIndex);

                Vector2dc startPoint = n1.getPoint();
                Vector2dc endPoint = n2.getPoint();
                double segmentDistance = startPoint.distance(endPoint);
                Vector2dc direction = new Vector2d(endPoint).sub(startPoint).normalize();

                Vector2dc vbl = new Vector2d(0, minHeight);
                Vector2dc vbr = new Vector2d(segmentDistance, minHeight);

                Vector2dc vtr = new Vector2d(segmentDistance, wallHeight);
                Vector2dc vtl = new Vector2d(0, wallHeight);

                Vector3dc normal = new Vector3d(-direction.y(), 0, -direction.x()).negate();

                int iN = mesh.addNormal(normal);

                PolygonList2d poly = new PolygonList2d(vbl, vbr, vtr, vtl);

                MultiPolygonList2d mPoly = new MultiPolygonList2d(poly);

                if (roofHeightCalculator != null) {
                    mPoly.getPolygons().addAll(createUnderRoofPolygons(startPoint, endPoint, wallHeight, roofHeightCalculator));
                }

                // build mesh
                mPoly = applyWindows(mPoly, n1.getBuildingNodeElements(), startPoint, direction, 0, false, catchFaceFactory, tm,
                        counterClockwise);
                mPoly = applyWindows(mPoly, n2.getBuildingNodeElements(), startPoint, direction, segmentDistance, true,
                        catchFaceFactory, tm, counterClockwise);

                for (PolygonList2d polygon : mPoly.getPolygons()) {

                    Iterable<Vector2dc> trianglePoint = new SimpleTriangulateInterable(polygon.getPoints());

                    for (Vector2dc p : trianglePoint) {

                        int vi = segmentPointToVertex3dIndex(p, startPoint, direction, mesh);

                        int[] tex = new int[texNum];

                        /* Wall material texture coordinates. */
                        tex[0] = segmentPointToTextureDataIndex(p, wallDistance, 0d, mesh, facadeTextureData);

                        /*
                         * First over layer have always the same coordinates as base layer. It is used
                         * to create two color wall texture.
                         */
                        if (isOverLayer) {
                            tex[1] = tex[0];
                        }

                        /*
                         * Windows over layer have its own texture coordinates.
                         */
                        if (isWindows) {
                            tex[texNum - 1] = segmentPointToTextureDataIndex(p, wallDistance, 0d, mesh,
                                    adjustedWindowsTextureData);
                        }

                        face.addVert(vi, iN, tex);
                    }
                }
                wallDistance += segmentDistance;
            }
        }
    }

    private static Collection<? extends PolygonList2d> createUnderRoofPolygons(Vector2dc startPoint, Vector2dc endPoint,
            double wallHeight2, HeightCalculator roofHeightCalculator) {

        List<PolygonList2d> ret = new ArrayList<>();

        List<SegmentHeight> heightSegments = roofHeightCalculator.height(startPoint, endPoint);

        double distance = 0;
        for (SegmentHeight segmentHeight : heightSegments) {

            double segmentDistance = segmentHeight.getBegin().distance(segmentHeight.getEnd());

            double leftHeight = segmentHeight.getBeginHeight();
            double rightHeight = segmentHeight.getEndHeight();

            boolean shrinkLeft = equal(wallHeight2, leftHeight);
            boolean shrinkRight = equal(wallHeight2, rightHeight);

            if (shrinkLeft && shrinkRight) {
                /*
                 * Same height of begin end and desired height so no point to generate empty
                 * size polygon.
                 */
                continue;
            }

            Vector2dc bottomLeft = new Vector2d(distance, wallHeight2);
            Vector2dc bottomRight = new Vector2d(distance + segmentDistance, wallHeight2);

            Vector2dc topRight = new Vector2d(distance + segmentDistance, rightHeight);
            Vector2dc topLeft = new Vector2d(distance, leftHeight);

            ret.add(new PolygonList2d(bottomLeft, bottomRight, topRight, topLeft));

            distance += segmentDistance;
        }

        return ret;
    }

    private static boolean equal(double number1, double number2) {

        return Math.abs(number1 - number2) < EPSILON;
    }

    private static TextureData generateWindowsOverlayTextureData(Wall wall, double minHeight, double wallHeight,
            BuildingPart buildingPart, BuildingModel buildingModel, BuildingElementsTextureManager tm, WallPart wallPart,
            double wallLength) {

        Integer windowsCols = hasWindowsCloumns(wallPart.getBuildingElements());

        if (windowsCols != null && windowsCols != 0) {
            /* Check if wall has any windows column defined. */
            TextureData windowsTextureData = takeWindowsColumnsTextureData(buildingModel, buildingPart, wall, wallPart, tm);

            double windowsSegmetLength = wallLength / (double) windowsCols;

            int facadeLevels = getFacadeLevels(wall, buildingPart);

            /*
             * Adjust texture data size to match wall size multiply by number of window
             * columns and levels.
             */
            double windowsSegmentHeight = (wallHeight - minHeight) / facadeLevels;

            return new TextureData(windowsTextureData.getTex0(), windowsSegmetLength, windowsSegmentHeight);
        }
        return null;
    }

    private static MeshFactory createWallMesh(ModelFactory mf, int partNumber, Material mat) {
        MeshFactory mesh = mf.addMesh("WallPart: " + partNumber);
        mesh.hasTexture = true;
        mesh.materialID = mf.cacheMaterial(mat);
        return mesh;
    }

    /**
     * Wall material can few four components like wall color (mixed with base
     * texture), base texture (mixed with color), optional overlayer texture for
     * wall material (like bricks) and optional overlayer for wall windows.
     *
     * @param facadeColor
     *            facade color
     * @param facadeTextureData
     *            facade texture data with required base texture, and optional
     *            overlay
     * @param windowsOverlayerTextureData
     *            optional texture with window overlayer
     * @return material for facade
     */
    private static Material createWallMaterial(Color facadeColor, TextureData facadeTextureData,
            TextureData windowsOverlayerTextureData) {
        Material mat = MaterialFactory.createTextureColorMaterial(facadeTextureData.getTex0(), facadeColor);

        if (facadeTextureData.getTex1() != null) {

            mat.getTexturesComponent().add(facadeTextureData.getTex1());
        }
        if (windowsOverlayerTextureData != null) {
            mat.getTexturesComponent().add(windowsOverlayerTextureData.getTex0());
        }
        return mat;
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
        } else if (bp.getRoofMaterialType() != null) {
            mt = bp.getRoofMaterialType();
        } else if (buildingModel.getRoofMaterialType() != null) {
            mt = buildingModel.getRoofMaterialType();
        }

        TextureData td = tm.findTexture(new TextureFindCriteria(Type.ROOF, mt, null, null, null, colorable));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    private static MultiPolygonList2d applyWindows(MultiPolygonList2d mPoly, List<BuildingNodeElement> buildingNodeElements,
            Vector2dc segmentStart, Vector2dc segmentDirection, double displacementOnSegment, boolean beginEnd,
            CatchFaceFactory catchFaceFactory, BuildingElementsTextureManager textureMenager, boolean counterClockwise) {

        if (buildingNodeElements != null) {
            for (BuildingNodeElement be : buildingNodeElements) {

                if (beginEnd) {
                    mPoly = applyWindowsEnd(mPoly, be, segmentStart, segmentDirection, displacementOnSegment, catchFaceFactory,
                            textureMenager, counterClockwise);
                } else {
                    mPoly = applyWindowsBegin(mPoly, be, segmentStart, segmentDirection, displacementOnSegment, catchFaceFactory,
                            textureMenager, counterClockwise);
                }
            }
        }
        return mPoly;
    }

    private static MultiPolygonList2d applyWindowsBegin(MultiPolygonList2d mPoly, BuildingNodeElement be, Vector2dc segmentStart,
            Vector2dc segmentDirection, double nodeDisplacement, CatchFaceFactory pCatchFaceFactory,
            BuildingElementsTextureManager pTextureMenager, boolean counterClockwise) {

        if (be instanceof SquareHoleElement) {
            SquareHoleElement she = (SquareHoleElement) be;

            Vector2dc mbp = new Vector2d(0, she.getMinHeight());
            Vector2dc rbp = new Vector2d(she.getWidth() / 2.0, she.getMinHeight());
            Vector2dc rtp = new Vector2d(she.getWidth() / 2.0, she.getMaxHeight());
            Vector2dc mtp = new Vector2d(0, she.getMaxHeight());

            mPoly = PolygonSplitHelper.unionOfLeftSideOfMultipleCuts(mPoly, new LinePoints2d(rtp, rbp),
                    new LinePoints2d(mtp, rtp), new LinePoints2d(rbp, mbp));

            TextureData td = findWindowTextureData(be, pTextureMenager);

            MeshFactory mesh = pCatchFaceFactory.createOrGetMeshFactory(td.getTex0());

            FaceFactory face = mesh.addFace(FaceType.QUADS);

            Vector3d n = new Vector3d(-segmentDirection.y(), 0, -segmentDirection.x());
            // if (counterClockwise) {
            n.negate();
            // }

            int iN = mesh.addNormal(n);

            int imbTc = mesh.addTextCoord(new TextCoord(0.5, 0));
            int imtTc = mesh.addTextCoord(new TextCoord(0.5, 1));
            int irbTc = mesh.addTextCoord(new TextCoord(1, 0));
            int irtTc = mesh.addTextCoord(new TextCoord(1, 1));

            face.addVert(segmentPointToVertex3dIndex(mbp, segmentStart, segmentDirection, mesh), imbTc, iN);
            face.addVert(segmentPointToVertex3dIndex(rbp, segmentStart, segmentDirection, mesh), irbTc, iN);
            face.addVert(segmentPointToVertex3dIndex(rtp, segmentStart, segmentDirection, mesh), irtTc, iN);
            face.addVert(segmentPointToVertex3dIndex(mtp, segmentStart, segmentDirection, mesh), imtTc, iN);

        }

        return mPoly;
    }

    private static MultiPolygonList2d applyWindowsEnd(MultiPolygonList2d mPoly, BuildingNodeElement be, Vector2dc segmentStart,
            Vector2dc segmentDirection, double nodeDisplacement, CatchFaceFactory catchFaceFactory,
            BuildingElementsTextureManager textureMenager, boolean counterClockwise) {

        if (be instanceof SquareHoleElement) {
            SquareHoleElement she = (SquareHoleElement) be;

            Vector2dc lbp = new Vector2d(nodeDisplacement - she.getWidth() / 2.0, she.getMinHeight());
            Vector2dc mbp = new Vector2d(nodeDisplacement, she.getMinHeight());
            Vector2dc mtp = new Vector2d(nodeDisplacement, she.getMaxHeight());
            Vector2dc ltp = new Vector2d(nodeDisplacement - she.getWidth() / 2.0, she.getMaxHeight());

            mPoly = PolygonSplitHelper.unionOfLeftSideOfMultipleCuts(mPoly, new LinePoints2d(lbp, ltp),
                    new LinePoints2d(ltp, mtp), new LinePoints2d(mbp, lbp));

            TextureData td = findWindowTextureData(be, textureMenager);

            MeshFactory mesh = catchFaceFactory.createOrGetMeshFactory(td.getTex0());

            FaceFactory face = mesh.addFace(FaceType.QUADS);

            Vector3dc n = new Vector3d(-segmentDirection.y(), 0, -segmentDirection.x()).negate();

            int iN = mesh.addNormal(n);

            int imbTc = mesh.addTextCoord(new TextCoord(0.5, 0));
            int imtTc = mesh.addTextCoord(new TextCoord(0.5, 1));
            int ilbTc = mesh.addTextCoord(new TextCoord(0, 0));
            int iltTc = mesh.addTextCoord(new TextCoord(0, 1));

            face.addVert(segmentPointToVertex3dIndex(lbp, segmentStart, segmentDirection, mesh), ilbTc, iN);
            face.addVert(segmentPointToVertex3dIndex(mbp, segmentStart, segmentDirection, mesh), imbTc, iN);
            face.addVert(segmentPointToVertex3dIndex(mtp, segmentStart, segmentDirection, mesh), imtTc, iN);
            face.addVert(segmentPointToVertex3dIndex(ltp, segmentStart, segmentDirection, mesh), iltTc, iN);

        }

        return mPoly;
    }

    static class CatchFaceFactory {
        private final Map<String, MeshFactory> catchMesh = new HashMap<>();
        private final ModelFactory mf;

        public CatchFaceFactory(ModelFactory mf) {
            this.mf = mf;
        }

        MeshFactory createOrGetMeshFactory(String textureKey) {

            MeshFactory meshFactory = catchMesh.get(textureKey);
            if (meshFactory == null) {
                meshFactory = mf.addMesh(textureKey);

                Material mat = MaterialFactory.createTextureMaterial(textureKey);
                int iMat = mf.addMaterial(mat);

                meshFactory.materialID = iMat;
                meshFactory.hasTexture = true;

                catchMesh.put(textureKey, meshFactory);
            }

            return meshFactory;
        }
    }

    private static int segmentPointToTextureDataIndex(Vector2dc point, double offsetX, double offsetY, MeshFactory mesh,
            TextureData td) {

        return mesh.addTextCoord(new TextCoord((point.x() + offsetX) / td.getWidth(), (point.y() + offsetY) / td.getHeight()));
    }

    private static int segmentPointToVertex3dIndex(Vector2dc point, Vector2dc start, Vector2dc direction, MeshFactory mesh) {
        Vector3d vertex = new Vector3d(direction.x(), 0, -direction.y()).mul(point.x());

        vertex.x += start.x();
        vertex.z -= start.y();

        vertex.y = point.y();

        int iV = mesh.addVertex(vertex);
        return iV;
    }

}
