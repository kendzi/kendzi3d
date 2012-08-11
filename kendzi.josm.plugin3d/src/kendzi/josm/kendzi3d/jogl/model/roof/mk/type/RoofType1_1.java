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
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.factory.TextCordFactory;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RectangleRoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RectangleRoofHooksSpaces;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;

import org.apache.log4j.Logger;

/**
 * Roof type 1.1.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType1_1 extends RectangleRoofTypeBuilder{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType1_1.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE1_1;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(
            List<Point2d> border,
            Point2d[] rectangleContur,
            double pScaleA,
            double pScaleB,
            double pRecHeight,
            double pRecWidth,
            Integer prefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            ModelFactory model,
            RoofMaterials pRoofTextureData
            ) {

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 0d);
        Double h2 = getHeightDegreesMeters(pMeasurements, MeasurementKey.HEIGHT_2, 0, pRecHeight, 30d);
        Double h3 = getHeightDegreesMeters(pMeasurements, MeasurementKey.HEIGHT_3, 0, pRecWidth, 30d);
//        Double h3 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_3, h2);


        return build(border, pScaleA, pScaleB, pRecHeight, pRecWidth, rectangleContur, h1, h2, h3, model, pRoofTextureData);

    }

    @Override
    protected boolean normalizeAB() {
        return false;
    }

    /**
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param h2
     * @param h3
     * @param pRoofTextureData
     * @return
     */
    protected RoofTypeOutput build(
            List<Point2d> pBorderList,
            double pScaleA,
            double pScaleB,
            double pRecHeight,
            double pRecWidth,
            Point2d[] pRectangleContur,
            double h1,
            double h2,
            double h3,
            ModelFactory model,
            RoofMaterials pRoofTextureData) {




        MeshFactory meshBorder = createFacadeMesh(model, pRoofTextureData);
        MeshFactory meshRoof = createRoofMesh(model, pRoofTextureData);

        TextureData facadeTexture = pRoofTextureData.getFacade().getTextureData();
        TextureData roofTexture = pRoofTextureData.getRoof().getTextureData();



        // FIXME
        boolean isCounterClockwise = false;
        if (0.0f < Triangulate.area(pBorderList)) {
              isCounterClockwise = true;
        }


        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);
        MultiPolygonList2d topMP = new MultiPolygonList2d(borderPolygon);


//        Point3d planeRightTopPoint =  new Point3d(
//                rightTopPoint.x ,
//                h2,
//                -rightTopPoint.y);
//
//        Plane3d planeTop = new Plane3d(
//                planeRightTopPoint,
//                nt);

        Vector3d roofTopLineVector = new Vector3d(
                -pRecWidth,
                0,
                0);


        Plane3d planeTop = createRoofPlane(h1, h2, h3, pRecWidth, pRecHeight);

        RoofTypeUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);


        List<Point2d> borderSplit = RoofTypeUtil.splitBorder(borderPolygon);

        List<Double> borderHeights = calcHeightList(
                borderSplit,
                planeTop);



        ////******************

        RoofTypeUtil.makeRoofBorderMesh(

               borderSplit,
               borderHeights,

               meshBorder,
               facadeTexture
               );



        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(Math.max(h1, h2), h3));

        rto.setModel(model);

        RectangleRoofHooksSpaces rhs =
            buildRectRoofHooksSpace(
                    pRectangleContur,
                    new PolygonPlane(topMP, planeTop),
                    new PolygonPlane(topMP, planeTop),
                    new PolygonPlane(topMP, planeTop),
                    new PolygonPlane(topMP, planeTop)
                  );

        rto.setRoofHooksSpaces(rhs);

        return rto;








//
//
//
//        Point3d [] topPoints = new Point3d[cleanPointList.size()];
//        double [] heights = new double[cleanPointList.size()];
//
//        for (int i = 0; i < cleanPointList.size(); i++) {
//            Point2d borderPoint = cleanPointList.get(i);
//
//            double height = getHeight(borderPoint, roofPlane);
//
//            Point3d topPoint = new Point3d(borderPoint.x, height, -borderPoint.y);
//
//            topPoints[i] = topPoint;
//            heights[i] = height;
//        }
//
//
//        Point2d beginPoint = cleanPointList.get(0);
//
//        for (int i = 1; i < cleanPointList.size() + 1; i++) {
//            int index = i % cleanPointList.size();
//
//            Point2d endPoint = cleanPointList.get(index);
//
//            double beginHeight = heights[((i - 1)  % cleanPointList.size())];
//            double endHeight = heights[index];
//
//            double vBegin = (beginHeight / facadeTexture.getHeight());
//            double vEnd = (endHeight / facadeTexture.getHeight());
//
////            Vector3d norm = Normal.calcNormalNorm2(
////                    beginPoint.getX(), 0.0f, beginPoint.getY(),
////                    endPoint.getX(), 0.0f, endPoint.getY(),
////                    beginPoint.getX(), 1.0, beginPoint.getY());
//
//            Vector3d norm = new Vector3d(
//                    -(endPoint.y - beginPoint.y), 0, -(endPoint.x - beginPoint.x));
//            norm.normalize();
//
//            if (isCounterClockwise) {
//                norm.negate();
//            }
//
//            int n = meshBorder.addNormal(norm);
//
//            double distance = calcScaledDistance(beginPoint, endPoint, pScaleA, pScaleB);
//            double uEnd = (int) (distance / facadeTexture.getLenght());
//
//            int tc1 = meshBorder.addTextCoord(new TextCoord(0, 0));
//            int tc2 = meshBorder.addTextCoord(new TextCoord(0, vBegin));
//            int tc3 = meshBorder.addTextCoord(new TextCoord(uEnd, vEnd));
//            int tc4 = meshBorder.addTextCoord(new TextCoord(uEnd, 0));
//
//            double minHeight = 0;
//            int w1 = meshBorder.addVertex(new Point3d(beginPoint.getX(),  minHeight, -beginPoint.getY()));
//            int w2 = meshBorder.addVertex(new Point3d(beginPoint.getX(), beginHeight, -beginPoint.getY()));
//            int w3 = meshBorder.addVertex(new Point3d(endPoint.getX(), endHeight, -endPoint.getY()));
//            int w4 = meshBorder.addVertex(new Point3d(endPoint.getX(), minHeight, -endPoint.getY()));
//
//
//            FaceFactory face = meshBorder.addFace(FaceType.QUADS);
//            face.addVert(w1, tc1, n);
//            face.addVert(w2, tc2, n);
//            face.addVert(w3, tc3, n);
//            face.addVert(w4, tc4, n);
//
//            beginPoint = endPoint;
//        }
//
//
//        meshRoof.materialID = roofMaterialIndex;
//        meshRoof.hasTexture = true;
//
//        FaceFactory top1 = meshRoof.addFace(FaceType.TRIANGLES);
//
//        Vector3d roofLineVector = new Vector3d(pRectangleContur[1].x - pRectangleContur[0].x,
//                h3 - h1,
//                pRectangleContur[1].y - pRectangleContur[0].y);
//        roofLineVector.negate();
//
//        int topVerticesIndex = meshRoof.vertices.size();
//        int topTextCoordsIndex = meshRoof.textCoords.size();
//
//        for (int i = 0; i < topPoints.length; i++) {
//            meshRoof.addVertex(topPoints[i]);
//
//
//            TextCoord calcUV = calcUV(topPoints[i], roofPlane.getNormal(), roofLineVector,  roofPlane.getPoint(), roofTexture);
//
//            meshRoof.addTextCoord(calcUV);
//        }
//
//        int topNormalIndex = meshRoof.addNormal(roofPlane.getNormal());
//
//        for (int i = 0; i < triangles.size(); i++) {
//
//            top1.addVertIndex(topVerticesIndex + triangles.get(i));
//            top1.addNormalIndex(topNormalIndex);
//
//            top1.addCoordIndex(topTextCoordsIndex + triangles.get(i));
//        }
//
//
//        // Model model = modelBuilder.toModel();
//        // model.useLigth = true;
//        // model.drawVertex = true;
//        // model.drawEdges = true;
//        // model.drawNormals = true;
//        // model.useTexture = false;
//
//        RoofTypeOutput rto = new RoofTypeOutput();
//        rto.setHeight(Math.max(Math.max(h1, h2), h3));
//
//
//        rto.setModel(model);
//
//        //FIXME
//        //TODO
//        //XXX
//        // !!!
//        rto.setRoofHooksSpaces(null);
//////
////        RoofHooksSpace [] rhs = buildRoofHooksSpace(pRectangleContur, roofPlane);
////
////        rto.setRoofHooksSpaces(rhs);
//
//
//        return rto;
    }



    /**
     * @see kendzi.jogl.model.factory.TextCordFactory#calcFlatSurfaceUV(Point3d, Vector3d, Vector3d, Point3d, TextureData)
     */
    private TextCoord calcUV(Point3d point3d, Vector3d pPlaneNormal, Vector3d pRoofLineVector, Point3d pRoofLinePoint,
            TextureData roofTexture) {
        return TextCordFactory.calcFlatSurfaceUV(point3d, pPlaneNormal, pRoofLineVector, pRoofLinePoint, roofTexture);
    }

    @Deprecated //FIXME
    private RoofHooksSpace [] buildRoofHooksSpace(Point2d[] pRectangleContur, Plane3d roofPlane) {

        Vector2d v = new Vector2d(pRectangleContur[1]);
        v.sub(pRectangleContur[0]);

        double d =  pRectangleContur[1].distance(pRectangleContur[2]);

        return new RoofHooksSpace [] {
                new RectangleRoofHooksSpace(
                        pRectangleContur[0],
                        v,
                        d,
                        roofPlane
                     )

            };

    }

    /**
     * Calculate distance between points before scaling.
     *
     * @param pBeginPoint begin point
     * @param pEndPoint end point
     * @param pScaleA scale A
     * @param pScaleB scale B
     * @return distance between points before scaling
     */
    @Deprecated
    private double calcScaledDistance(Point2d pBeginPoint, Point2d pEndPoint, double pScaleA, double pScaleB) {

        double dx = (pBeginPoint.x - pEndPoint.x) * pScaleA;
        double dy = (pBeginPoint.y - pEndPoint.y) * pScaleB;

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Create roof plane.
     *
     * @param h1 height 1
     * @param h2 height 2
     * @param h3 height 3
     * @param pRecHeight
     * @param pRecWidth
     * @return roof plane
     */
    private Plane3d createRoofPlane(double h1, double h2, double h3, double pRecWidth, double pRecHeight) {

        Point3d p1 = new Point3d(0, h1, 0);

        Vector3d v2 = new Vector3d(0, h2, -pRecHeight);
        v2.sub(p1);

        Vector3d v3 = new Vector3d(pRecWidth, h3, 0);
        v3.sub(p1);

        Vector3d normal = new Vector3d();
        normal.cross(v3, v2);

        return new Plane3d(p1, normal);
    }

//    /**
//     * Point height.
//     *
//     * @param pPoint point
//     * @param pRoofPlane roof plane
//     * @return point height
//     */
//    private double getHeight(Point2d pPoint, Plane3d pRoofPlane) {
////        return RectangleUtil.calcYOfPlane(pPoint.x, pPoint.y, pRoofPlane.getPoint(), pRoofPlane.getNormal());
//        return pRoofPlane.calcYOfPlane(pPoint.x, pPoint.y);
//    }

    private List<Double> calcHeightList(
            List<Point2d> pSplitBorder,
            Plane3d planeTop) {



        List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
        for (Point2d point : pSplitBorder) {

           double height = calcHeight(point,
                   planeTop);

           borderHeights.add(height);

        }

        return borderHeights;
    }

    /** Calc height of point in border.
     * @param point
     * @param planeTop
     * @return
     */
    private double calcHeight(Point2d point,
            Plane3d planeTop) {

        double x = point.x;
        double z = -point.y;

        return planeTop.calcYOfPlane(x, z);

    }
}
