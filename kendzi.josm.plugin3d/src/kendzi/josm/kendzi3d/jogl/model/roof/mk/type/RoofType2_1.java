/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.GableRoof;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.PolygonRoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.PolygonSplitUtil;

import org.apache.log4j.Logger;

/**
 * Roof type 2.1.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType2_1 extends RectangleRoofTypeBuilder{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2_1.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE2_1;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(
            List<Point2d> border,
            Point2d[] rectangleContur,
            double scaleA,
            double scaleB,
            double pSizeA,
            double pSizeB,
            Integer prefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData
            ) {


        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 1.5d);

        Double h2 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_2, 2.5d);

        Double b1 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_1, pSizeA, pSizeA /2d);

        return build(border, scaleA, scaleB, pSizeA, pSizeB, rectangleContur, h1, h2, b1, pRoofTextureData);

    }

    @Override
    protected boolean normalizeAB() {
        return false;
    }

    /**
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
     * @param pSizeA
     * @param pSizeB
     * @param pRectangleContur
     * @param h1
     * @param h2
     * @param b1
     * @param pRoofTextureData
     * @return
     */
    protected RoofTypeOutput build(
            List<Point2d> pBorderList,
            double pScaleA,
            double pScaleB,
            double pSizeA,
            double pSizeB,
            Point2d[] pRectangleContur,
            double h1,
            double h2,
            double b1,
            RoofTextureData pRoofTextureData) {


        ModelFactory model = ModelFactory.modelBuilder();
        MeshFactory meshBorder = model.addMesh("roof_border");
        MeshFactory meshRoof = model.addMesh("roof_top");

        //XXX move it
        TextureData facadeTexture = pRoofTextureData.getFacadeTextrure();
        TextureData roofTexture = pRoofTextureData.getRoofTexture();
        Material facadeMaterial = MaterialFactory.createTextureMaterial(facadeTexture.getFile());
        Material roofMaterial = MaterialFactory.createTextureMaterial(roofTexture.getFile());
        // XXX move material
        int facadeMaterialIndex = model.addMaterial(facadeMaterial);
        int roofMaterialIndex = model.addMaterial(roofMaterial);

        meshBorder.materialID = facadeMaterialIndex;
        meshBorder.hasTexture = true;

        meshRoof.materialID = roofMaterialIndex;
        meshRoof.hasTexture = true;

//        double minHeight = 0;

        double roofLineDistance1 = b1;
        double roofLineDistance2 = pSizeA - roofLineDistance1;

        LinePoints2d roofLine = new LinePoints2d(new Point2d(0, roofLineDistance1), new Point2d(pSizeB, roofLineDistance1));

        Vector3d n1 = new Vector3d(0, roofLineDistance1, h1);
        n1.normalize();

        Vector3d n2 = new Vector3d(0, roofLineDistance2, -h2);
        n2.normalize();

        Point3d planePoint1 =  new Point3d(
                (roofLine.getP1().x) ,
                h1,
                -(roofLine.getP1().y));

        Point3d planePoint2 =  new Point3d(
                (roofLine.getP1().x) ,
                h2,
                -(roofLine.getP1().y));

        Plane3d plane1 = new Plane3d(planePoint1, n1);
        Plane3d plane2 = new Plane3d(planePoint2, n2);

        Vector3d planeNorm1 = n1;

        Vector3d planeNorm2 = n2;

        List<Point2d> border = new ArrayList<Point2d>();

        List<java.lang.Double> heightList = new ArrayList<java.lang.Double>();



        ////******************
        List<Point2d> borderExtanded = new ArrayList<Point2d>();




        for (Point2d ppp : pBorderList) {
            border.add(new Point2d(ppp.x, ppp.y));
        }
        if (border.get(border.size() - 1).equals(border.get(0))) {
            border.remove(border.size() - 1);
        }

        List<List<Integer>> polygonsLeft = new ArrayList<List<Integer>>();
        List<List<Integer>> polygonsRight = new ArrayList<List<Integer>>();

        PolygonSplitUtil.splitPolygonByLine(roofLine, border, borderExtanded, polygonsLeft, polygonsRight);

        for (Point2d p : borderExtanded) {
            //XXX
            double height = GableRoof.calcHeight(p, planePoint1, planeNorm1, planeNorm2);
            heightList.add(height);
        }

//        //XXX
//        minHeight = GableRoof.findRoofMinHeight(heightList);

        GableRoof.addVertexToRoofMesh(meshRoof, borderExtanded, heightList);

        Vector3d roofLineVector = new Vector3d(
                roofLine.getP2().x - roofLine.getP1().x,
                0,
                roofLine.getP2().y - roofLine.getP1().y
        );

        GableRoof.addPolygonToRoofMesh(meshRoof, borderExtanded, polygonsRight, plane2, roofLineVector, roofTexture);
        GableRoof.addPolygonToRoofMesh(meshRoof, borderExtanded, polygonsLeft, plane1, roofLineVector, roofTexture);

        ////******************

       makeRoofBorderMesh(
               h1,
               h2,
               border,
               borderExtanded,
               polygonsRight,
               plane2,
               polygonsLeft,
               plane1,
               meshBorder,
               facadeTexture);



        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(h1, h2));

        rto.setModel(model);


        List<List<Point2d>> polygonTop = indexesToList(borderExtanded, polygonsRight);
        List<List<Point2d>> polygonBottom = indexesToList(borderExtanded, polygonsLeft);

        //FIXME
        //TODO
        //XXX
        // !!!
        rto.setRoofHooksSpaces(null);
//        RectangleRoofHooksSpaces rhs =
//                buildRoofHooksSpace(
//                        polygonTop,
//                        polygonBottom,
//                        roofLineDistance1,
//                        roofLineDistance2,
//                        pRectangleContur,
//                        plane1,
//                        plane2);

//        RectangleRoofHooksSpaces rhs =
//                buildRectRoofHooksSpace(
//                        pRectangleContur,
//                        new PolygonPlane(mpb, planeBottom),
//                        null,
//                        new PolygonPlane(mpt, planeTop),
//                        null
//                      );


        return rto;
    }


    public static List<List<Point2d>> indexesToList(List<Point2d> borderExtanded, List<List<Integer>> polygonsIndexes) {

        List<List<Point2d>> ret = new ArrayList<List<Point2d>>();

        for (List<Integer> p : polygonsIndexes) {
            List<Point2d> polygon = makeListFromIndex(borderExtanded, p);
            ret.add(polygon);
        }
        return ret;

    }

    static List<Point2d> makeListFromIndex(List<Point2d> borderExtanded,
            List<Integer> polyIndex) {

        List<Point2d> ret = new ArrayList<Point2d>(polyIndex.size());
        for (Integer i : polyIndex) {
            ret.add(borderExtanded.get(i));
        }
        return ret;
    }



    /** Make roof border mesh. It is wall under roof.
     * @param h22
     * @param h12
     * @param pBorder walls polygon
     * @param pBorderExtanded wall polygon with extra points where top roof line divide walls polygon
     * @param pPolygonsRight
     * @param pPlaneRight
     * @param pPolygonsLeft
     * @param pPlaneLeft
     * @param pMeshBorder border mesh

     * @param facadeTexture
     */
    public static void makeRoofBorderMesh(double h12, double h22, List<Point2d> pBorder,
            List<Point2d> pBorderExtanded,
            List<List<Integer>> pPolygonsRight,
            Plane3d pPlaneRight,
            List<List<Integer>> pPolygonsLeft,
            Plane3d pPlaneLeft,
            MeshFactory pMeshBorder,
            TextureData facadeTexture) {

        boolean isCounterClockwise = false;
        if (0.0f < Triangulate.area(pBorder)) {
            isCounterClockwise = true;
        }

        List<Integer> middleWallBegins = new ArrayList<Integer>();
        List<Integer> middleWallEnds = new ArrayList<Integer>();

        Set<Integer> rightSet = new HashSet<Integer>();

        for (List<Integer> polygon : pPolygonsRight) {


            for (int i = 0; i < polygon.size(); i++) {

                Integer index1 = polygon.get(i);
                Integer index2 = polygon.get((i + 1) % polygon.size());

                int di = index2 - index1;
                if (di > 1 || di < 1) {
                    middleWallBegins.add(index1);
                    middleWallEnds.add(index2);
                }

                rightSet.add(index1);



            }
        }

        Set<Integer> leftSet = new HashSet<Integer>();

        for (List<Integer> polygon : pPolygonsLeft) {

            for (int i = 0; i < polygon.size(); i++) {

                Integer index = polygon.get(i);

                leftSet.add(index);
            }
        }

        Integer [] bottomPointsIndex = new Integer[pBorderExtanded.size()];
        Integer [] leftPointsIndex = new Integer[pBorderExtanded.size()];
        Integer [] rightPointsIndex = new Integer[pBorderExtanded.size()];

        FaceFactory face = pMeshBorder.addFace(FaceType.QUADS);

        double uLast = 0;

        for (int i = 0; i < pBorderExtanded.size(); i++) {
            int index1 = i;
            int index2 = (i + 1) % pBorderExtanded.size();

            boolean i1Left = leftSet.contains(index1);
            boolean i1Rigth = rightSet.contains(index1);

            boolean i2Left = leftSet.contains(index2);
            boolean i2Rigth = rightSet.contains(index2);

            Point2d point1 = pBorderExtanded.get(index1);
            Point2d point2 = pBorderExtanded.get(index2);


            boolean isLeftSegment = false;
            boolean isRightSegment = false;

            if (i1Left && i2Left) {
                isLeftSegment = true;
            }
            if (i1Rigth && i2Rigth) {
                isRightSegment = true;
            }

            int point1BottomIndex = -1;
            int point2BottomIndex = -1;
            int point1HightIndex = -1;
            int point2HightIndex = -1;
            double height1 = -1;
            double height2 = -1;


            if (isLeftSegment && isRightSegment) {
                log.error("ups. segment is in left and right polygon !");
                continue;
            } else if (isLeftSegment) {


                height1 = pPlaneLeft.calcYOfPlane(point1.x, -point1.y);
                point1HightIndex = cachePointIndex(point1, index1, height1, leftPointsIndex, pMeshBorder);

                height2 = pPlaneLeft.calcYOfPlane(point2.x, -point2.y);
                point2HightIndex = cachePointIndex(point2, index2, height2, leftPointsIndex, pMeshBorder);

            } else if (isRightSegment) {

                height1 = pPlaneRight.calcYOfPlane(point1.x, -point1.y);
                point1HightIndex = cachePointIndex(point1, index1, height1, rightPointsIndex, pMeshBorder);

                height2 = pPlaneRight.calcYOfPlane(point2.x, -point2.y);
                point2HightIndex = cachePointIndex(point2, index2, height2, rightPointsIndex, pMeshBorder);

            } else {
                log.error("ups. segment is not in left or right polygon !");
                continue;
            }

            point1BottomIndex = cachePointIndex(point1, index1, 0, bottomPointsIndex, pMeshBorder);
            point2BottomIndex = cachePointIndex(point2, index2, 0, bottomPointsIndex, pMeshBorder);

            Vector3d n = new Vector3d(-(point2.y - point1.y), 0, -(point2.x - point1.x));
            n.normalize();

            if (isCounterClockwise) {
                n.negate();
            }

            int normalIndex = pMeshBorder.addNormal(n);

            double uBegin = uLast;
            double uEnd = uBegin + point1.distance(point2) / facadeTexture.getLenght();
            uLast = uEnd;

            int tc_0_0 = pMeshBorder.addTextCoord(new TextCoord(uBegin  , 0));
            int tc_0_v = pMeshBorder.addTextCoord(new TextCoord(uBegin  , height1 / facadeTexture.getHeight()));
            int tc_u_0 = pMeshBorder.addTextCoord(new TextCoord(uEnd  , 0));
            int tc_u_v = pMeshBorder.addTextCoord(new TextCoord(uEnd  , height2 / facadeTexture.getHeight()));


            face.addVertIndex(point1HightIndex);
            face.addVertIndex(point1BottomIndex);
            face.addVertIndex(point2BottomIndex);
            face.addVertIndex(point2HightIndex);

            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);

            face.addCoordIndex(tc_0_v);
            face.addCoordIndex(tc_0_0);
            face.addCoordIndex(tc_u_0);
            face.addCoordIndex(tc_u_v);



        }


        // midle wall
        for (int i = 0; i < middleWallBegins.size(); i++) {
            int startIndex = middleWallBegins.get(i);
            int endIndex = middleWallEnds.get(i);

            Point2d start = pBorderExtanded.get(startIndex);
            Point2d end = pBorderExtanded.get(endIndex);

            double heightStartLeft = pPlaneLeft.calcYOfPlane(start.x, -start.y);
            double heightStartRight = pPlaneRight.calcYOfPlane(start.x, -start.y);

            double heightEndLeft = pPlaneLeft.calcYOfPlane(end.x, -end.y);
            double heightEndRight = pPlaneRight.calcYOfPlane(end.x, -end.y);


            int direction = 1;
            if (heightStartLeft > heightStartRight) {
                direction = -1;
            }

            int startLeftIndex = cachePointIndex(start, startIndex, heightStartLeft, leftPointsIndex, pMeshBorder);
            int startRightIndex = cachePointIndex(start, startIndex, heightStartRight, rightPointsIndex, pMeshBorder);

            int endLeftIndex = cachePointIndex(end, endIndex, heightEndLeft, leftPointsIndex, pMeshBorder);
            int ebdRightIndex = cachePointIndex(end, endIndex, heightEndRight, rightPointsIndex, pMeshBorder);


            Vector3d n = new Vector3d(-direction * (end.y - start.y), 0, -direction * (end.x - start.x));
            n.normalize();

            if (isCounterClockwise) {
                n.negate();
            }

            int normalIndex = pMeshBorder.addNormal(n);


            double u = start.distance(end) / facadeTexture.getLenght();

            int tc_0_0 = pMeshBorder.addTextCoord(new TextCoord(0  , heightStartLeft / facadeTexture.getHeight()));
            int tc_0_v = pMeshBorder.addTextCoord(new TextCoord(0  , heightStartRight / facadeTexture.getHeight()));
            int tc_u_0 = pMeshBorder.addTextCoord(new TextCoord(u  , heightEndLeft / facadeTexture.getHeight()));
            int tc_u_v = pMeshBorder.addTextCoord(new TextCoord(u  , heightEndRight / facadeTexture.getHeight()));


            face.addVertIndex(startLeftIndex);
            face.addVertIndex(startRightIndex);
            face.addVertIndex(ebdRightIndex);
            face.addVertIndex(endLeftIndex);

            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);

            face.addCoordIndex(tc_0_0);
            face.addCoordIndex(tc_0_v);
            face.addCoordIndex(tc_u_v);
            face.addCoordIndex(tc_u_0);

        }
    }


    private static int cachePointIndex(Point2d pPoint, int pPointIndex, double pHeight, Integer[] pPointsIndexCache,
            MeshFactory pMeshBorder) {

        if (pPointsIndexCache[pPointIndex] == null) {
            int p1i = pMeshBorder.addVertex(new Point3d(pPoint.x, pHeight, -pPoint.y));
            pPointsIndexCache[pPointIndex] = p1i;
        }

        int point2BottomIndex = pPointsIndexCache[pPointIndex];
        return point2BottomIndex;
    }



    private RoofHooksSpace [] buildRoofHooksSpace(
            List<List<Point2d>> topPolygon,
            List<List<Point2d>> bottomPolygon,
            double pRoofLineDistance1 , double pRoofLineDistance2, Point2d[] pRectangleContur,
            Plane3d pPlane1, Plane3d pPlane2) {

        Vector2d v1 = new Vector2d(pRectangleContur[1]);
        v1.sub(pRectangleContur[0]);

        double d1 =  pRoofLineDistance1;
        Plane3d plane1 = new Plane3d(pPlane1.getPoint(), pPlane1.getNormal());

        PolygonRoofHooksSpace rrhs1 = new PolygonRoofHooksSpace(
                        pRectangleContur[0],
                        v1,
                        bottomPolygon,
                        plane1);


        Vector2d v2 = new Vector2d(pRectangleContur[0]);
        v2.sub(pRectangleContur[1]);

        double d2 =  pRoofLineDistance2;

        Plane3d plane2 = new Plane3d(pPlane2.getPoint(), pPlane2.getNormal());

        PolygonRoofHooksSpace rrhs2 = new PolygonRoofHooksSpace(
                pRectangleContur[2],
                v2,
                topPolygon,
                plane2
                 );


        if (d1 <= 0 && d2 <= 0) {
            return new RoofHooksSpace [] {};

        } else if (d1 <= 0) {
            return new RoofHooksSpace [] {
                    rrhs2
                };
        } else if (d2 <= 0) {
            return new RoofHooksSpace [] {
                    rrhs1
                };
        }

        return new RoofHooksSpace [] {
                rrhs1,
                rrhs2
            };

    }

}
