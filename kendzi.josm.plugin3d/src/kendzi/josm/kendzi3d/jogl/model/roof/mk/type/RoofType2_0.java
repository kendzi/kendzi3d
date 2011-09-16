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

import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.factory.TextCordFactory;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.josm.kendzi3d.jogl.model.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.GableRoof;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.PolygonRoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.PolygonSplitUtil;

import org.apache.log4j.Logger;

/**
 * Roof type 2.0.
 *
 * @author Tomasz Kêdziora (Kendzi)
 *
 */
public class RoofType2_0 extends RectangleRoofType{

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType2_0.class);

    @Override
    public String getPrefixKey() {
        return "2.0";
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(
            List<Point2d> pBorder,
            Point2d[] pRectangleContur,
            double pScaleA,
            double pScaleB,
            double pSizeA,
            double pSizeB,
            Integer pPrefixParameter,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData
            ) {

//        Double h1 = getSize(0, pHeights, 1d);
//
//        Double b1 = getSize(0, pSizesB, 0.5d);

        Double b1 = getLenghtMetersPersent(pMeasurements, MeasurementKey.LENGTH_1, pSizeA, pSizeA / 2d);

        Double h1 = getHeightDegreesMeters(pMeasurements, MeasurementKey.HEIGHT_1, 0, b1, 30);


        return build(pBorder, pScaleA, pScaleB, pSizeA, pSizeB, pRectangleContur, h1, b1, pRoofTextureData);

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
     * @param h3
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
            double h1, double b1,
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

        double minHeight = 0;

        double roofLineDistance1 = b1;
        double roofLineDistance2 = pSizeA - roofLineDistance1;

        LinePoints2d roofLine = new LinePoints2d(new Point2d(0, roofLineDistance1), new Point2d(pSizeB, roofLineDistance1));

        Vector3d n1 = new Vector3d(0, roofLineDistance1, h1);
        n1.normalize();

        Vector3d n2 = new Vector3d(0, roofLineDistance2, -h1);
        n2.normalize();

        // Vector3d n1 = PointUtil.rotateY3d(rotateC3d, -roofLineAngle + Math.toRadians(90));
        // Vector3d n2 = PointUtil.rotateY3d(rotateC3d, -roofLineAngle - Math.toRadians(180) + Math.toRadians(90));

        Point3d planePoint =  new Point3d(
                (roofLine.getP1().x) ,
                h1,
                -(roofLine.getP1().y));


        Plane3d plane1 = new Plane3d(planePoint, n1);
        Plane3d plane2 = new Plane3d(planePoint, n2);

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
            double height = GableRoof.calcHeight(p, planePoint, planeNorm1, planeNorm2);
            heightList.add(height);
        }

        minHeight = GableRoof.findRoofMinHeight(heightList);

        GableRoof.addVertexToRoofMesh(meshRoof, borderExtanded, heightList);

        Vector3d roofLineVector = new Vector3d(
                roofLine.getP2().x - roofLine.getP1().x,
                0,
                -(roofLine.getP2().y - roofLine.getP1().y)
        );

        GableRoof.addPolygonToRoofMesh(meshRoof, borderExtanded, polygonsRight, plane2, roofLineVector, roofTexture);
        GableRoof.addPolygonToRoofMesh(meshRoof, borderExtanded, polygonsLeft, plane1, roofLineVector, roofTexture);


        ////******************

        Vector3d [] wallNormals = calcNormals(border);

        GableRoof.makeRoofBorderMesh(
                border,
                borderExtanded,
                meshBorder,
                minHeight,
                heightList,
                wallNormals,
                facadeTexture);




        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);

        rto.setModel(model);

        List<List<Point2d>> polygonTop = RoofType2_1.indexesToList(borderExtanded, polygonsRight);
        List<List<Point2d>> polygonBottom = RoofType2_1.indexesToList(borderExtanded, polygonsLeft);

        RoofHooksSpace [] rhs = buildRoofHooksSpace(polygonTop, polygonBottom, roofLineDistance1, roofLineDistance2, pRectangleContur, plane1, plane2);

        rto.setRoofHooksSpaces(rhs);

        return rto;
    }



    private Vector3d[] calcNormals(List<Point2d> border) {

        boolean isCounterClockwise = false;
        if (0.0f < Triangulate.area(border)) {
            isCounterClockwise = true;
        }

        int size = border.size();
        Vector3d[] ret = new Vector3d[size];
        for (int i = 0; i < border.size(); i++) {
            Point2d start = border.get(i);
            Point2d stop = border.get((i + 1) % size);

            Vector3d n = new Vector3d(
                    -(stop.y - start.y), 0, -(stop.x - start.x));
            n.normalize();

            if (isCounterClockwise) {
                n.negate();
            }

            ret[i] = n;

        }
        return ret;
    }

    /**
     * @see kendzi.jogl.model.factory.TextCordFactory#calcFlatSurfaceUV(Point3d, Vector3d, Vector3d, Point3d, TextureData)
     */
    private TextCoord calcUV(Point3d point3d, Vector3d pPlaneNormal, Vector3d pRoofLineVector, Point3d pRoofLinePoint,
            TextureData roofTexture) {
        return TextCordFactory.calcFlatSurfaceUV(point3d, pPlaneNormal, pRoofLineVector, pRoofLinePoint, roofTexture);
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
