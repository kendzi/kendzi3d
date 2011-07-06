/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

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
import kendzi.jogl.model.factory.TextCordFactory;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.josm.kendzi3d.jogl.model.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RectangleRoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangulate;

import org.apache.log4j.Logger;

/**
 * Roof type 1.1.
 *
 * @author Tomasz Kêdziora (Kendzi)
 *
 */
public class RoofType1_1 extends RectangleRoofType{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType1_1.class);

    @Override
    public String getPrefixKey() {
        return "1.1";
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
            List<Double> heights,
            List<Double> sizeB,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData
            ) {

//        Double h1 = getSize(0, heights, 0d);
//        Double h2 = getSize(1, heights, h1);
//        Double h3 = getSize(2, heights, h1);

        Double h1 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_1, 0d);
        Double h2 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_2, 2.5d);
        Double h3 = getHeightMeters(pMeasurements, MeasurementKey.HEIGHT_3, h2);


        return build(border, scaleA, scaleB, pSizeA, pSizeB, rectangleContur, h1, h2, h3, pRoofTextureData);

    }

    @Override
    protected boolean normalizeAB() {
        return false;
    }

    /**
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
     * @param pRectangleContur
     * @param h1
     * @param h2
     * @param h3
     * @return
     */
    protected RoofTypeOutput build(
            List<Point2d> pBorderList,
            double pScaleA,
            double pScaleB,
            double pSizeA,
            double pSizeB,
            Point2d[] pRectangleContur,
            double h1, double h2, double h3, RoofTextureData pRoofTextureData) {

        Plane3d roofPlane = createRoofPlane(h1, h2, h3, pRectangleContur);


        ModelFactory model = ModelFactory.modelBuilder();
        MeshFactory border = model.addMesh("border1");
        MeshFactory top = model.addMesh("top1");

        Triangulate t = new Triangulate();
        List<Point2d> cleanPointList = t.removeClosePoints(pBorderList);

        List<Integer> triangles = t.processIndex(cleanPointList);

        TextureData facadeTexture = pRoofTextureData.getFacadeTextrure();
        TextureData roofTexture = pRoofTextureData.getRoofTexture();
//        TextureData facadeTexture = new TextureData("", 1, 1);
//        TextureData roofTexture = new TextureData("", 1.5, 1.5);
//        double facadeTextureLenght = 4;
//        double facadeTextureHeight = 2;


        // FIXME
        boolean isCounterClockwise = false;
        if (0.0f < Triangulate.area(pBorderList)) {
              isCounterClockwise = true;
        }

        Material facadeMaterial =  MaterialFactory.createTextureMaterial(facadeTexture.getFile());
        Material roofMaterial =  MaterialFactory.createTextureMaterial(roofTexture.getFile());
        // XXX move material
        int facadeMaterialIndex = model.addMaterial(facadeMaterial);
        int roofMaterialIndex = model.addMaterial(roofMaterial);


        border.materialID = facadeMaterialIndex;
        border.hasTexture = true;

        Point3d [] topPoints = new Point3d[cleanPointList.size()];
        double [] heights = new double[cleanPointList.size()];

        for (int i = 0; i < cleanPointList.size(); i++) {
            Point2d borderPoint = cleanPointList.get(i);

            double height = getHeight(borderPoint, roofPlane);

            Point3d topPoint = new Point3d(borderPoint.x, height, -borderPoint.y);

            topPoints[i] = topPoint;
            heights[i] = height;
        }


        Point2d beginPoint = cleanPointList.get(0);

        for (int i = 1; i < cleanPointList.size() + 1; i++) {
            int index = i % cleanPointList.size();

            Point2d endPoint = cleanPointList.get(index);

            double beginHeight = heights[((i - 1)  % cleanPointList.size())];
            double endHeight = heights[index];

            double vBegin = (beginHeight / facadeTexture.getHeight());
            double vEnd = (endHeight / facadeTexture.getHeight());

//            Vector3d norm = Normal.calcNormalNorm2(
//                    beginPoint.getX(), 0.0f, beginPoint.getY(),
//                    endPoint.getX(), 0.0f, endPoint.getY(),
//                    beginPoint.getX(), 1.0, beginPoint.getY());

            Vector3d norm = new Vector3d(
                    -(endPoint.y - beginPoint.y), 0, -(endPoint.x - beginPoint.x));
            norm.normalize();

            if (isCounterClockwise) {
                norm.negate();
            }

            int n = border.addNormal(norm);

            double distance = calcScaledDistance(beginPoint, endPoint, pScaleA, pScaleB);
            double uEnd = (int) (distance / facadeTexture.getLenght());

            int tc1 = border.addTextCoord(new TextCoord(0, 0));
            int tc2 = border.addTextCoord(new TextCoord(0, vBegin));
            int tc3 = border.addTextCoord(new TextCoord(uEnd, vEnd));
            int tc4 = border.addTextCoord(new TextCoord(uEnd, 0));

            double minHeight = 0;
            int w1 = border.addVertex(new Point3d(beginPoint.getX(),  minHeight, -beginPoint.getY()));
            int w2 = border.addVertex(new Point3d(beginPoint.getX(), beginHeight, -beginPoint.getY()));
            int w3 = border.addVertex(new Point3d(endPoint.getX(), endHeight, -endPoint.getY()));
            int w4 = border.addVertex(new Point3d(endPoint.getX(), minHeight, -endPoint.getY()));


            FaceFactory face = border.addFace(FaceType.QUADS);
            face.addVert(w1, tc1, n);
            face.addVert(w2, tc2, n);
            face.addVert(w3, tc3, n);
            face.addVert(w4, tc4, n);

            beginPoint = endPoint;
        }


        top.materialID = roofMaterialIndex;
        top.hasTexture = true;

        FaceFactory top1 = top.addFace(FaceType.TRIANGLES);

        Vector3d roofLineVector = new Vector3d(pRectangleContur[1].x - pRectangleContur[0].x,
                h3 - h1,
                pRectangleContur[1].y - pRectangleContur[0].y);
        roofLineVector.negate();

        int topVerticesIndex = top.vertices.size();
        int topTextCoordsIndex = top.textCoords.size();

        for (int i = 0; i < topPoints.length; i++) {
            top.addVertex(topPoints[i]);


            TextCoord calcUV = calcUV(topPoints[i], roofPlane.getNormal(), roofLineVector,  roofPlane.getPoint(), roofTexture);

            top.addTextCoord(calcUV);
        }

        int topNormalIndex = top.addNormal(roofPlane.getNormal());

        for (int i = 0; i < triangles.size(); i++) {

            top1.addVertIndex(topVerticesIndex + triangles.get(i));
            top1.addNormalIndex(topNormalIndex);

            top1.addCoordIndex(topTextCoordsIndex + triangles.get(i));
        }


        // Model model = modelBuilder.toModel();
        // model.useLigth = true;
        // model.drawVertex = true;
        // model.drawEdges = true;
        // model.drawNormals = true;
        // model.useTexture = false;

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(Math.max(Math.max(h1, h2), h3));


        rto.setModel(model);


//
        RoofHooksSpace [] rhs = buildRoofHooksSpace(pRectangleContur, roofPlane);

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }



    /**
     * @see kendzi.jogl.model.factory.TextCordFactory#calcFlatSurfaceUV(Point3d, Vector3d, Vector3d, Point3d, TextureData)
     */
    private TextCoord calcUV(Point3d point3d, Vector3d pPlaneNormal, Vector3d pRoofLineVector, Point3d pRoofLinePoint,
            TextureData roofTexture) {
        return TextCordFactory.calcFlatSurfaceUV(point3d, pPlaneNormal, pRoofLineVector, pRoofLinePoint, roofTexture);
    }


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
     * @param pRectangleContur
     * @return roof plane
     */
    private Plane3d createRoofPlane(double h1, double h2, double h3, Point2d[] pRectangleContur) {

        Point2d c1 = pRectangleContur[0];
        Point2d c2 = pRectangleContur[3];
        Point2d c3 = pRectangleContur[1];

        Point3d p1 = new Point3d(c1.x, h1, c1.y);

        Vector3d v2 = new Vector3d(c2.x, h2, c2.y);
        v2.sub(p1);

        Vector3d v3 = new Vector3d(c3.x, h3, c3.y);
        v3.sub(p1);

        Vector3d normal = new Vector3d();
        normal.cross(v2, v3);

        return new Plane3d(p1, normal);
    }

    /**
     * Point height.
     *
     * @param pPoint point
     * @param pRoofPlane roof plane
     * @return point height
     */
    private double getHeight(Point2d pPoint, Plane3d pRoofPlane) {
//        return RectangleUtil.calcYOfPlane(pPoint.x, pPoint.y, pRoofPlane.getPoint(), pRoofPlane.getNormal());
        return pRoofPlane.calcYOfPlane(pPoint.x, pPoint.y);
    }
}
