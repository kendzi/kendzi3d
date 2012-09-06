package kendzi.josm.kendzi3d.jogl.model.building.builder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
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
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingModel;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingUtil;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingWallElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.Wall;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallNode;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.WindowGridBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.BuildingNodeElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.EntranceBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.SquareHoleElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.WindowBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.texture.BuildingElementsTextureMenager;
import kendzi.josm.kendzi3d.jogl.model.building.texture.BuildingElementsTextureMenager.Type;
import kendzi.josm.kendzi3d.jogl.model.building.texture.TextureFindCriteria;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.DormerRoofBuilder;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.RoofTextureData;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitUtil;

public class BuildingBuilder {

    public static Model buildModel(BuildingModel buildingModel, BuildingElementsTextureMenager tm) {

//        ModelFactory modelFactory = ModelFactory.modelBuilder();

        ModelFactory mf = ModelFactory.modelBuilder();

        if (buildingModel.getParts() != null) {

            for (BuildingPart bp : buildingModel.getParts()) {
                buildPart(bp, buildingModel, mf, tm);
             }
        }
//        else {
//            buildPart(buildingModel.getOutline(), buildingModel, mf, tm);
//        }
        return mf.toModel();
    }

    ModelFactory CreateModelFactory() {

        ModelFactory model = ModelFactory.modelBuilder();
//        MeshFactory meshBorder = model.addMesh("wals");
//
////
////        //XXX move it
////        TextureData facadeTexture = pRoofTextureData.getFacadeTextrure();
////        TextureData roofTexture = pRoofTextureData.getRoofTexture();
////        Material facadeMaterial = MaterialFactory.createTextureMaterial(facadeTexture.getFile());
//////        Material roofMaterial = MaterialFactory.createTextureMaterial(roofTexture.getFile());
////        // XXX move material
////        int facadeMaterialIndex = model.addMaterial(facadeMaterial);
//
//        meshBorder.materialID = facadeMaterialIndex;
//        meshBorder.hasTexture = true;

        return model;
    }


    static boolean isWallCounterClockwise(Wall wall) {
        PolygonList2d wallToPolygon = BuildingUtil.wallToPolygon(wall);

        if (0.0f < Triangulate.area(wallToPolygon.getPoints())) {
            return true;
        }
        return false;
    }

    private static void buildPart(BuildingPart bp, BuildingModel buildingModel, ModelFactory mf, BuildingElementsTextureMenager tm) {

        CatchFaceFactory catchFaceFactory = new CatchFaceFactory(mf);

        Wall w = bp.getWall();

        boolean cc = isWallCounterClockwise(w);

        RoofTextureData rtd = new RoofTextureData();
        // XXX fix, currently roof builder support only one texture, fix before roof builder is changed.
        WallPart firstWallPart = getFirstWallPart(w);
        Color facadeColor = takeFacadeColor(buildingModel, bp, w, firstWallPart, tm);
        rtd.setFacadeCoror(facadeColor);
        rtd.setFacadeTextrure(takeFacadeTextureData(buildingModel, bp, w, firstWallPart, tm, facadeColor!=null));
        Color roofColor = takeRoofColor(buildingModel, bp, w, firstWallPart, tm);
        rtd.setRoofCoror(roofColor);
        rtd.setRoofTexture(takeRoofTextureData(buildingModel, bp, w, tm, roofColor!=null));
        double maxHeight = bp.getDefaultMaxHeight();

        RoofOutput roofOutput = DormerRoofBuilder.build(bp, maxHeight, mf, rtd);

        double wallHeight = maxHeight - roofOutput.getHeight();


        buildWall(w, cc, wallHeight, bp, buildingModel, mf, catchFaceFactory, tm);

//        for (WallPart wp : w.getWallParts()) {
//            buildWallPart(wp, bp, mf);
//        }

        if (bp.getInlineWalls() != null) {
            for (Wall in : bp.getInlineWalls()) {
                boolean ccc = isWallCounterClockwise(w);

                buildWall(in, ccc, wallHeight, bp, buildingModel, mf, catchFaceFactory, tm);
    //            for (WallPart wp : in.getWallParts()) {
    //                buildWallPart(wp, bp, mf);
    //            }
            }
        }
//        BuildingModelUtil.WallPartToOutline(wallParts)

    }

    private static WallPart getFirstWallPart(Wall w) {

        if (w == null) {
            return null;
        }

        if(w.getWallParts() != null && w.getWallParts().size() > 0) {
            return w.getWallParts().get(0);
        }

        return null;
    }

    private static void buildWall(Wall w, boolean counterClockwise, double wallHeight, BuildingPart bp, BuildingModel buildingModel, ModelFactory mf,  CatchFaceFactory catchFaceFactory, BuildingElementsTextureMenager tm) {

        double minHeight = bp.getDefaultMinHeight();

//        MeshFactory meshWind = mf.addMesh("Windows");
//        meshWind.hasTexture = true;
//        FaceFactory faceWindows = meshWind.addFace(FaceType.QUADS);

        Map<String, MeshFactory> buildingNodesMesh = new HashMap<String, MeshFactory>();
        int wpCount = -1;
        for (WallPart wp : w.getWallParts()) {
            wpCount++;


            double wallLength = calcWallPartLength(wp);

            List<WallNode> nodes = wp.getNodes();
            int size = nodes.size();

            Color facadeColor = takeFacadeColor(buildingModel, bp, w, wp, tm);
            TextureData facadeTD = takeFacadeTextureData(buildingModel, bp, w, wp, tm, facadeColor != null);

            String tex0Key = facadeTD.getFile();
            Material mat = MaterialFactory.createTextureColorMaterial(tex0Key, facadeColor);

            //XXX

            Integer windowsCols = hasWindowsCloumns(wp.getBuildingElements());
            //windowsCols = 0;
            boolean isWindows = windowsCols != null && windowsCols != 0;
            TextureData windowsTexture = null;
            if (isWindows){

                TextureData windowsTD = takeWindowsColumnsTextureData(buildingModel, bp, w, wp, tm);
                String tex1Key = windowsTD.getFile();
                mat.setTexturesComponent(Arrays.asList(tex0Key, tex1Key));



                double windowsSegmetLength = wallLength / ((double)windowsCols);

                int facadeLevels = getFacadeLevels(w, bp);
                double windowsSegmentHeight = (wallHeight - minHeight) / (facadeLevels);


                windowsTexture = new TextureData(null, windowsSegmetLength, windowsSegmentHeight);


            }
//            mat.matType = MatType.TEXTURE0;
//            mat.matType = mt;

            MeshFactory mesh = mf.addMesh("WallPart: " + wpCount);
            mesh.hasTexture = true;
            mesh.materialID = mf.cacheMaterial(mat);

            FaceFactory face = mesh.addFace(FaceType.QUADS, isWindows ? 2 : 1);








            double wallDistance = 0;

            for (int i = 0; i < size - 1; i++) {
                WallNode n1 = nodes.get(i);
                WallNode n2 = nodes.get((i+1) % size);

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

                TextureData facadeTexture = facadeTD;
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

                    for(Point2d p: points) {
                        //addPointToMesh(p, start, direction, mesh, face, iN, facadeTexture);

                        if (isWindows) {
                            face.addVert(
                                    segmentPointToVertex3dIndex(p, start, direction, mesh),
                                    segmentPointToTextureDataIndex(p, 0d, 0d,  mesh, facadeTexture),
                                    segmentPointToTextureDataIndex(p, wallDistance, 0d,  mesh, windowsTexture),
                                    iN);
                        } else {
                            face.addVert(
                                    segmentPointToVertex3dIndex(p, start, direction, mesh),
                                    segmentPointToTextureDataIndex(p, 0d, 0d,  mesh, facadeTexture),
                                    iN);
                        }
                    }
                }
                //FaceFactory face = pMeshBorder.addFace(FaceType.QUADS);
                wallDistance += segmentDistance;
            }
        }
    }

    /**
     * @param wp
     * @param wallLength
     * @return
     */
    public static double calcWallPartLength(WallPart wp) {
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
        // FIXME TODO XXX
        // take levels of roof
        return bp.getDefaultMaxLevel() - bp.getDefaultMinLevel();
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
            BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp, BuildingElementsTextureMenager tm) {


        String mt = null;


//        TextureData td = null;
//        if (wp.getWindowsColumnsTextureData() != null) {
//            return wp.getWindowsColumnsTextureData();
//        }
//        if (buildingModel.getWindowsColumnsTextureData() != null) {
//            td = buildingModel.getWindowsColumnsTextureData();
//        }


        TextureData td = tm.findTexture(new TextureFindCriteria(Type.WINDOWS, mt, null, null, null, false));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    private static TextureData takeFacadeTextureData(
            BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp, BuildingElementsTextureMenager tm, boolean colorable) {

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

    private static Color takeFacadeColor(
            BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp, BuildingElementsTextureMenager tm) {

        Color c = null;

        if (wp != null && wp.getFacadeColour() != null) {
            c = wp.getFacadeColour();
        } else  if (w.getFacadeColour() != null) {
            c = w.getFacadeColour();
        } else  if (bp.getFacadeColour() != null) {
            c = bp.getFacadeColour();
        } else  if (buildingModel.getFacadeColour() != null) {
            c = buildingModel.getFacadeColour();
        }

        return c;
    }

    private static Color takeRoofColor(
            BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp, BuildingElementsTextureMenager tm) {

        Color c = null;

        if (wp != null && wp.getRoofColour() != null) {
            c = wp.getRoofColour();
        } else  if (w.getRoofColour() != null) {
            c = w.getRoofColour();
        } else  if (bp.getRoofColour() != null) {
            c = bp.getRoofColour();
        } else  if (buildingModel.getRoofColour() != null) {
            c = buildingModel.getRoofColour();
        }

        return c;
    }

    private static TextureData takeRoofTextureData(
            BuildingModel buildingModel, BuildingPart bp, Wall w, BuildingElementsTextureMenager tm, boolean colorable) {

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

    private static MultiPolygonList2d applyBuildingElements(MultiPolygonList2d mPoly, WallNode n1, int i, boolean b) {
        if (n1.getBuildingNodeElements() != null) {
            for (BuildingNodeElement be : n1.getBuildingNodeElements()) {
                if (be instanceof SquareHoleElement) {
//                    WindowBuildingElement wbe = (WindowBuildingElement) be;


                }
            }
        }

        return null;
    }

    private static MultiPolygonList2d applyWindows(
            MultiPolygonList2d mPoly,
            List<BuildingNodeElement> pBuildingNodeElements,
            Point2d segmentStart,
            Vector2d segmentDirection,
            double displacementOnSegment,
            boolean beginEnd,
            CatchFaceFactory pCatchFaceFactory,
            BuildingElementsTextureMenager pTextureMenager, boolean counterClockwise) {

        if (pBuildingNodeElements != null) {
            for (BuildingNodeElement be : pBuildingNodeElements) {
//                if (be instanceof WindowBuildingElement) {
//                    WindowBuildingElement wbe = (WindowBuildingElement) be;
//                }
                if (beginEnd) {
                    mPoly = applyWindowsEnd(mPoly, be, segmentStart, segmentDirection, displacementOnSegment, pCatchFaceFactory, pTextureMenager, counterClockwise);
                } else {
                    mPoly = applyWindowsBegin(mPoly, be, segmentStart, segmentDirection, displacementOnSegment, pCatchFaceFactory, pTextureMenager, counterClockwise);
                }
            }
        }
        return mPoly;
    }

    private static MultiPolygonList2d applyWindowsBegin(
            MultiPolygonList2d mPoly,
            BuildingNodeElement be,
            Point2d segmentStart,
            Vector2d segmentDirection,
            double nodeDisplacement,
            //Map<String, MeshFactory> buildingNodesMesh,
            CatchFaceFactory pCatchFaceFactory,
            BuildingElementsTextureMenager pTextureMenager, boolean counterClockwise) {

        if (be instanceof SquareHoleElement) {
            SquareHoleElement she = (SquareHoleElement) be;

            Point2d mbp = new Point2d(0 , she.getMinHeight());
            Point2d rbp = new Point2d(she.getWidth()/2.0 , she.getMinHeight());;
            Point2d rtp = new Point2d(she.getWidth()/2.0 , she.getMaxHeight());;
            Point2d mtp = new Point2d(0 , she.getMaxHeight());;

            mPoly = PolygonSplitUtil.unionOfFrontPart(mPoly,
                    new LinePoints2d(rtp, rbp),
                    new LinePoints2d(mtp, rtp),
                    new LinePoints2d(rbp, mbp)
                    );

//            Point2d lbp = new Point2d(nodeDisplacement - she.getWidth()/2.0 , she.getMinHeight());
//            Point2d mbp = new Point2d(nodeDisplacement , she.getMinHeight());;
//            Point2d mtp = new Point2d(nodeDisplacement  , she.getMaxHeight());;
//            Point2d ltp = new Point2d(nodeDisplacement  - she.getWidth()/2.0 , she.getMaxHeight());;
//
//            mPoly = PolygonSplitUtil.unionOfFrontPart(mPoly,
//                    new LinePoints2d(lbp, ltp),
//                    new LinePoints2d(ltp, mtp),
//                    new LinePoints2d(mbp, lbp)
//                    );



            TextureData td = findWindowTextureData(be, pTextureMenager);

            MeshFactory mesh = pCatchFaceFactory.createOrGetMeshFactory(td.getFile());

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
    private static MultiPolygonList2d applyWindowsEnd(
            MultiPolygonList2d mPoly,
            BuildingNodeElement be,
            Point2d segmentStart,
            Vector2d segmentDirection,
            double nodeDisplacement,
            //Map<String, MeshFactory> buildingNodesMesh,
            CatchFaceFactory pCatchFaceFactory,
            BuildingElementsTextureMenager pTextureMenager, boolean counterClockwise) {

        if (be instanceof SquareHoleElement) {
            SquareHoleElement she = (SquareHoleElement) be;


            Point2d lbp = new Point2d(nodeDisplacement - she.getWidth()/2.0 , she.getMinHeight());
            Point2d mbp = new Point2d(nodeDisplacement , she.getMinHeight());;
            Point2d mtp = new Point2d(nodeDisplacement  , she.getMaxHeight());;
            Point2d ltp = new Point2d(nodeDisplacement  - she.getWidth()/2.0 , she.getMaxHeight());;

            mPoly = PolygonSplitUtil.unionOfFrontPart(mPoly,
                    new LinePoints2d(lbp, ltp),
                    new LinePoints2d(ltp, mtp),
                    new LinePoints2d(mbp, lbp)
                );


            TextureData td = findWindowTextureData(be, pTextureMenager);

            MeshFactory mesh = pCatchFaceFactory.createOrGetMeshFactory(td.getFile());

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

    /**
     * @param be
     * @param pTextureMenager
     * @return
     */
    public static TextureData findWindowTextureData(BuildingNodeElement be,
            BuildingElementsTextureMenager pTextureMenager) {
        TextureData td = null;
        if (be instanceof WindowBuildingElement) {
            WindowBuildingElement wbe = (WindowBuildingElement) be;

            td = pTextureMenager.findTexture(
                    new TextureFindCriteria(BuildingElementsTextureMenager.Type.WINDOW, wbe.getWindowType(), null, wbe.getWidth(), wbe.getHeight(), false));
        } else if (be instanceof EntranceBuildingElement) {
            EntranceBuildingElement wbe = (EntranceBuildingElement) be;

            td = pTextureMenager.findTexture(
                    new TextureFindCriteria(BuildingElementsTextureMenager.Type.ENTERENCE, wbe.getEntranceType(), null, wbe.getWidth(), wbe.getHeight(), false));
        } else{
            //
        }

        if (td == null) {
            td = createEmptyTextureData();
        }
        return td;
    }






    static class CatchFaceFactory {
        Map<String, MeshFactory> catchMesh = new HashMap<String, MeshFactory>();
        ModelFactory mf;

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
                //meshFactory = meshFactory.addFace(FaceType.QUADS);

                this.catchMesh.put(textureKey, meshFactory);
            }

            return meshFactory;
        }
    }

    private static TextureData createEmptyTextureData() {
        return new TextureData(null, 1, 1);
    }

    private static TextureData takeWindowsTextureData(BuildingNodeElement be) {
        // TODO Auto-generated method stub
        return null;
    }

//    /**
//     * @param point
//     * @param start
//     * @param direction
//     * @param mesh
//     * @param face
//     * @param iN
//     * @param td
//     */
//    public static void addPointToMesh(Point2d point, Point2d start, Vector2d direction, MeshFactory mesh,
//            FaceFactory face, int iN, TextureData td, TextureData td2) {
//
//        if (td2 == null) {
//            face.addVert(
//                    segmentPointToVertex3dIndex(point, start, direction, mesh),
//                    segmentPointToTextureDataIndex(point, 0d, 0d, mesh, td),
//                    iN);
//        } else {
//            face.addVert(
//                    segmentPointToVertex3dIndex(point, start, direction, mesh),
//                    segmentPointToTextureDataIndex(point, 0d, 0d, mesh, td),
//                    segmentPointToTextureDataIndex(point,  0d, 0d, mesh, td2),
//                    iN);
//        }
//    }

    /**
     * @param point
     * @param offsetX
     * @param offsetY
     * @param mesh
     * @param td
     * @return
     */
    public static int segmentPointToTextureDataIndex(Point2d point, double offsetX, double offsetY, MeshFactory mesh, TextureData td) {
        int iTc = mesh.addTextCoord(
                new TextCoord(
                        (point.x + offsetX)/ td.getLenght() ,
                        (point.y  + offsetY)/ td.getHeight()));
        return iTc;
    }

    /**
     * @param point
     * @param start
     * @param direction
     * @param mesh
     * @return
     */
    public static int segmentPointToVertex3dIndex(Point2d point, Point2d start, Vector2d direction, MeshFactory mesh) {
        Point3d vertex = new Point3d(direction.x, 0, -direction.y);

        vertex.scale(point.x);
        vertex.x += start.x;
        vertex.z -= start.y;

        vertex.y = point.y;

        int iV = mesh.addVertex(vertex);
        return iV;
    }


    private static List<WallHole> createHoles(WallNode n1) {
        List<WallHole> ret = new ArrayList<BuildingBuilder.WallHole>();
        for (BuildingNodeElement be : n1.getBuildingNodeElements()) {
            if (be instanceof SquareHoleElement) {
                SquareHoleElement she = (SquareHoleElement) be;



            }
        }
        return null;
    }

    private static MultiPolygonList2d cutHoles(
            MultiPolygonList2d mPoly,
            WallNode n1,
            double displacement,
            boolean beginEnd) {

        if (n1.getBuildingNodeElements() != null) {
            for (BuildingNodeElement be : n1.getBuildingNodeElements()) {
                if (be instanceof SquareHoleElement) {
                    SquareHoleElement she = (SquareHoleElement) be;

//                    List<LinePoints2d> cutLines = new ArrayList<LinePoints2d>();

                    Point2d lbp;
                    Point2d rbp;
                    Point2d rtp;
                    Point2d ltp;

                    if (!beginEnd    ) {
                        // begin
                        lbp = new Point2d(0 , she.getMinHeight());
                        rbp = new Point2d(she.getWidth()/2.0 , she.getMinHeight());;
                        rtp = new Point2d(she.getWidth()/2.0 , she.getMaxHeight());;
                        ltp = new Point2d(0 , she.getMaxHeight());;

                        mPoly = PolygonSplitUtil.unionOfFrontPart(mPoly,
                                    new LinePoints2d(rtp, rbp),
                                    new LinePoints2d(ltp, rtp),
                                    new LinePoints2d(rbp, lbp)
                                );
    //                    cutLines.add(new LinePoints2d(p1, p2))
                    } else {
                        // end
                        lbp = new Point2d(displacement - she.getWidth()/2.0 , she.getMinHeight());
                        rbp = new Point2d(displacement , she.getMinHeight());;
                        rtp = new Point2d(displacement  , she.getMaxHeight());;
                        ltp = new Point2d(displacement  - she.getWidth()/2.0 , she.getMaxHeight());;

                        mPoly = PolygonSplitUtil.unionOfFrontPart(mPoly,
                                new LinePoints2d(lbp, ltp),
                                new LinePoints2d(ltp, rtp),
                                new LinePoints2d(rbp, lbp)
                            );
                    }


    //                LinePoints2d


                }
            }
        }
        return mPoly;
    }



    class WallHole {
        Point2d ldp;
        Point2d rdp;
        Point2d rtp;
        Point2d ltp;
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




//    private void buildWallPart(WallPart wp, BuildingPart bp, MeshFactory mf) {
//
//
//    }
}
