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
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.geometry.material.Material.MatType;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingModel;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingUtil;
import kendzi.josm.kendzi3d.jogl.model.building.model.Wall;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallNode;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.BuildingNodeElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.EntranceBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.SquareHoleElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.WindowBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.texture.BuildingElementsTextureMenager;
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
        rtd.setFacadeTextrure(takeFacadeTextureData(buildingModel, bp, w, firstWallPart));
        rtd.setRoofTexture(takeRoofTextureData(buildingModel, bp, w));

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

//        MeshFactory meshWind = mf.addMesh("Windows");
//        meshWind.hasTexture = true;
//        FaceFactory faceWindows = meshWind.addFace(FaceType.QUADS);

        Map<String, MeshFactory> buildingNodesMesh = new HashMap<String, MeshFactory>();
        int wpCount = -1;
        for (WallPart wp : w.getWallParts()) {
            wpCount++;

            TextureData facadeTD = takeFacadeTextureData(buildingModel, bp, w, wp);
            Material mat = MaterialFactory.createTextureMaterial(facadeTD.getFile());

            //XXX
            MatType mt = MatType.COLOR_TEXTURE0;
            int windowsCols = hasWindowsCloumns(wp);
            windowsCols = 0;
            boolean isWindows = windowsCols > 0 ;
            if (isWindows){
                mt =  MatType.COLOR_MultT0_MultT1;
                TextureData windowsTD = takeWindowsColumnsTextureData(buildingModel, bp, w, wp);
            }
            mat.matType = mt;
            mat.matType = MatType.TEXTURE0;

            MeshFactory mesh = mf.addMesh("WallPart: " + wpCount);
            mesh.hasTexture = true;
            mesh.materialID = mf.cacheMaterial(mat);

            FaceFactory face = mesh.addFace(FaceType.QUADS);


            List<WallNode> nodes = wp.getNodes();
            int size = nodes.size();

            double wallDistance = 0;

            for (int i = 0; i < size; i++) {
                WallNode n1 = nodes.get(i);
                WallNode n2 = nodes.get((i+1) % size);

                //List<WallHole> holesEnd = createEndHoles(n2);

                Point2d start = n1.getPoint();
                double segmentDistance = n1.getPoint().distance(n2.getPoint());
                Vector2d direction = new Vector2d(n2.getPoint());
                direction.sub(n1.getPoint());
                direction.normalize();

               // List<WallHole> holes = createHoles(n1);


                Point2d vbl = new Point2d(0, bp.getDefaultMinHeight());
                Point2d vbr = new Point2d(segmentDistance, bp.getDefaultMinHeight());

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

                        face.addVert(
                                segmentPointToVertex3dIndex(p, start, direction, mesh),
                                segmentPointToTextureDataIndex(p, mesh, facadeTexture),
                                iN);
                    }
                }
                //FaceFactory face = pMeshBorder.addFace(FaceType.QUADS);
                wallDistance += segmentDistance;
            }
        }
    }




    private static int hasWindowsCloumns(WallPart wp) {
        // TODO Auto-generated method stub
        return 3;
    }



    private static TextureData takeWindowsColumnsTextureData(BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp) {
        TextureData td = null;
//        if (wp.getWindowsColumnsTextureData() != null) {
//            return wp.getWindowsColumnsTextureData();
//        }
        if (buildingModel.getWindowsColumnsTextureData() != null) {
            td = buildingModel.getWindowsColumnsTextureData();
        }
        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    private static TextureData takeFacadeTextureData(BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp) {
        TextureData td = null;
        if (wp != null && wp.getFacadeTextureData() != null) {
            td = wp.getFacadeTextureData();
        } else  if (w.getFacadeTextureData() != null) {
            td = w.getFacadeTextureData();
        } else  if (bp.getFacadeTextureData() != null) {
            td = bp.getFacadeTextureData();
        } else  if (buildingModel.getFacadeTextureData() != null) {
            td = buildingModel.getFacadeTextureData();
        }

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    private static TextureData takeRoofTextureData(BuildingModel buildingModel, BuildingPart bp, Wall w) {
        TextureData td = null;

        if (w.getRoofTextureData() != null) {
            td = w.getRoofTextureData();
        } else  if (bp.getRoofTextureData() != null) {
            td = bp.getRoofTextureData();
        } else  if (buildingModel.getRoofTextureData() != null) {
            td = buildingModel.getRoofTextureData();
        }

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    private static TextureData getTextureData() {
        // TODO Auto-generated method stub
        return null;
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

//            addPointToMesh(mbp, segmentStart, segmentDirection, mesh, face, iN, td);
//            addPointToMesh(rbp, segmentStart, segmentDirection, mesh, face, iN, td);
//            addPointToMesh(rtp, segmentStart, segmentDirection, mesh, face, iN, td);
//            addPointToMesh(mtp, segmentStart, segmentDirection, mesh, face, iN, td);

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

//            addPointToMesh(lbp, segmentStart, segmentDirection, mesh, face, iN, td);
//            addPointToMesh(mbp, segmentStart, segmentDirection, mesh, face, iN, td);
//            addPointToMesh(mtp, segmentStart, segmentDirection, mesh, face, iN, td);
//            addPointToMesh(ltp, segmentStart, segmentDirection, mesh, face, iN, td);

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
                    new TextureFindCriteria(BuildingElementsTextureMenager.Type.WINDOW, wbe.getWindowType(), null, wbe.getWidth(), wbe.getHeight()));
        } else if (be instanceof EntranceBuildingElement) {
            EntranceBuildingElement wbe = (EntranceBuildingElement) be;

            td = pTextureMenager.findTexture(
                    new TextureFindCriteria(BuildingElementsTextureMenager.Type.ENTERENCE, wbe.getEntranceType(), null, wbe.getWidth(), wbe.getHeight()));
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

    /**
     * @param point
     * @param start
     * @param direction
     * @param mesh
     * @param face
     * @param iN
     * @param td
     */
    public static void addPointToMesh(Point2d point, Point2d start, Vector2d direction, MeshFactory mesh,
            FaceFactory face, int iN, TextureData td) {

        face.addVert(
                segmentPointToVertex3dIndex(point, start, direction, mesh),
                segmentPointToTextureDataIndex(point, mesh, td),
                iN);
    }

    /**
     * @param point
     * @param mesh
     * @param td
     * @return
     */
    public static int segmentPointToTextureDataIndex(Point2d point, MeshFactory mesh, TextureData td) {
        int iTc = mesh.addTextCoord(
                new TextCoord(
                        point.x / td.getLenght(),
                        point.y / td.getHeight()));
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
