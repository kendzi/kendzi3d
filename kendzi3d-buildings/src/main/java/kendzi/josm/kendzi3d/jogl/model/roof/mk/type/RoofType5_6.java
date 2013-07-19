/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LineSegment2d;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import kendzi.math.geometry.point.Vector2dUtil;
import kendzi.math.geometry.polygon.CircleInsidePolygon;
import kendzi.math.geometry.polygon.CircleInsidePolygon.Circle;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

/**
 * Roof type 5.6
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class RoofType5_6 extends AbstractRoofTypeBuilder {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType5_6.class);

    @Override
    public RoofTypeAliasEnum getPrefixKey() {
        return RoofTypeAliasEnum.ROOF_TYPE5_6;
    }

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRoof(
            Point2d pStartPoint,
            PolygonWithHolesList2d buildingPolygon,
            DormerRoofModel pRoof,
            double height,
            RoofMaterials pRoofTextureData) {

//            Point2d pStartPoint, List<Point2d> border, Integer prefixParameter, double height,
//            Map<MeasurementKey, Measurement> pMeasurements, RoofTextureData pRoofTextureData) {

        SimpleMatrix transformLocal = TransformationMatrix2d.tranA(-pStartPoint.x, -pStartPoint.y);

        List<Point2d> pPolygon = buildingPolygon.getOuter().getPoints();

        pPolygon = TransformationMatrix2d.transformList(pPolygon, transformLocal);

        // rectangleContur = TransformationMatrix2d.transformArray(rectangleContur, transformLocal);

        Double h1 = null;
        Double angle = null;
        Measurement measurement = pRoof.getMeasurements().get(MeasurementKey.HEIGHT_1);
        if (isUnit(measurement, MeasurementUnit.DEGREES)) {
//            return pAngleHeight + pAngleDepth * Math.tan(Math.toRadians(measurement.getValue()));
            angle = measurement.getValue();
        } else {
            h1 = getHeightMeters(pRoof.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);
        }



        RoofTypeOutput rto = build(pPolygon, h1, angle, 0, 0, pRoofTextureData);

        SimpleMatrix transformGlobal = TransformationMatrix3d.tranA(pStartPoint.x, height - rto.getHeight(),
                -pStartPoint.y);
        rto.setTransformationMatrix(transformGlobal);

        return rto;

    }

    protected RoofTypeOutput build(List<Point2d> pBorderList,

        Double h1, Double angle, double l1, double l2,
        RoofMaterials pRoofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(pRoofTextureData);
        MeshFactory meshRoof = createRoofMesh(pRoofTextureData);

        TextureData facadeTexture = pRoofTextureData.getFacade().getTextureData();
        TextureData roofTexture = pRoofTextureData.getRoof().getTextureData();


        PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

        MultiPolygonList2d topMP = new MultiPolygonList2d(borderPolygon);
       // build flat
        Point3d planeRightTopPoint =  new Point3d(
              0 ,
              0,
              0);

        Vector3d nt = new Vector3d(0, 1  , 0);

        Plane3d planeTop = new Plane3d(
              planeRightTopPoint,
              nt);

        Vector3d roofTopLineVector = new Vector3d(
                -1d,
                0,
                0);

        MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);

        //build circle
        Circle circle = CircleInsidePolygon.iterativeNonConvex(borderPolygon, 0.01);
        int pIcross = 5;
        int pIsection = 9;
        buildRotaryShape(meshBorder, circle, pIcross, pIsection, true);



        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(circle.getRadius());

        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        rto.setRoofHooksSpaces(null);

        rto.setRectangle(RoofType9_0.findRectangle(pBorderList, 0));

        return rto;
    }


    private void buildRotaryShape(
            MeshFactory meshFactory,
            Circle circle,
            int pIcross,
            int pIsection,
            boolean soft
            ) {


        int icross = pIcross + 1;


        // create cross section
        Point2d [] crossSection = new Point2d[icross];
        for (int i = 0; i < icross; i++) {
            double a = Math.toRadians(90) / (icross - 1) * i;

            crossSection[i] = new Point2d(Math.cos(a) * circle.getRadius(), Math.sin(a) * circle.getRadius());
        }

        buildRotaryShape(meshFactory, circle.getPoint(), pIsection, crossSection, soft);
    }

    public static void buildRotaryShape(
            MeshFactory meshFactory,
//            Circle circle,
            Point2d center,
            int pIsection,
            Point2d [] crossSection,
            boolean soft
            ) {

        int icross = crossSection.length;

        int isection = pIsection;//= 9;

        Vector2d [] crossSectionSoftNormals = null;
        if (soft) {
            crossSectionSoftNormals = calsSoftNormals(crossSection);
        }


        // create points
        Point3d [][] mesh = new Point3d[isection][];
        for (int i = 0; i< isection; i++) {
            double a = Math.toRadians(360) / isection * i;

            SimpleMatrix tranA = TransformationMatrix3d.tranA(center.x, 0, -center.y);
            SimpleMatrix rotY = TransformationMatrix3d.rotYA(a);

            SimpleMatrix trans = tranA.mult(rotY);

            Point3d [] crossMesh = new Point3d[icross];

            for (int j = 0; j < crossSection.length; j++) {
                // point
                Point2d cross = crossSection[j];
                Point3d p = new Point3d(cross.x, cross.y, 0);

                Point3d transform = TransformationMatrix3d.transform(p, trans);

                crossMesh[j] = transform;

            }
            mesh[i] = crossMesh;
        }

        TextQuadsIndex[] tc = buildRotaryShapeTextureMapping(meshFactory, icross, isection, mesh);

        FaceFactory face = meshFactory.addFace(FaceType.QUADS);

        // add vertex to mesh
        int [][] pointsIntex = new int[isection][];
        for (int i = 0; i < isection; i++) {
            pointsIntex[i] = new int[icross];
            for (int j = 0; j < icross; j++) {
                Point3d p =  mesh[i][j];
                int ip = meshFactory.addVertex(p);
                pointsIntex[i][j] = ip;
            }
        }

        // add soft normals vectors
        int [][] softNormalsIntex = new int[isection][];
        if (soft) {
//            double circleX = circle.getPoint().x;
//            double circleY = circle.getPoint().y;
//            for (int i = 0; i < isection; i++) {
//                softNormalsIntex[i] = new int[icross];
//                for (int j = 0; j < icross; j++) {
//                    Point3d p =  mesh[i][j];
//
//                    Vector3d n = new Vector3d(p.x - circleX, p.y,  p.z  + circleY);
//
//                    int in = meshFactory.addNormal(n);
//                    softNormalsIntex[i][j] = in;
//                }
//            }

            for (int i = 0; i< isection; i++) {
                double a = Math.toRadians(360) / isection * i;

                SimpleMatrix rotY = TransformationMatrix3d.rotYA(a);
                softNormalsIntex[i] = new int[icross];

                for (int j = 0; j < crossSection.length; j++) {
                    // point
                    Vector2d n2d = crossSectionSoftNormals[j];
                    Vector3d n = new Vector3d(n2d.x, n2d.y, 0);

                    Vector3d transform = TransformationMatrix3d.transform(n, rotY);

                    int in = meshFactory.addNormal(transform);
                    softNormalsIntex[i][j] = in;
                }
            }
        }




        // add faces to mesh
        for (int i = 0; i < isection; i++) {
            Point3d [] c1 = mesh[i];
            Point3d [] c2 = mesh[(i + 1) % isection];

            int i2 = (i + 1) % isection;

            for (int j = 0; j < icross - 1; j++) {

                int ic1p1 = pointsIntex[i][j];
                int ic2p1 = pointsIntex[i2][j];
                int ic1p2 = pointsIntex[i][j+1];
                int ic2p2 = pointsIntex[i2][j+1];
//                int ic1p1 = meshFactory.addVertex(c1p1);
//                int ic2p1 = meshFactory.addVertex(c2p1);
//                int ic1p2 = meshFactory.addVertex(c1p2);
//                int ic2p2 = meshFactory.addVertex(c2p2);

                int ic1p1n;
                int ic2p1n;
                int ic1p2n;
                int ic2p2n;

                if (!soft) {
                    // hard

                    Point3d c1p1 = c1[j];
                    Point3d c2p1 = c2[j];
                    Point3d c1p2 = c1[j + 1];

                    Vector3d n = calcNormal(c1p1, c2p1, c1p2);
                    int in = meshFactory.addNormal(n);

                    ic1p1n = in;
                    ic2p1n = in;
                    ic1p2n = in;
                    ic2p2n = in;

                } else { // soft
                    ic1p1n = softNormalsIntex[i][j];
                    ic2p1n = softNormalsIntex[i2][j];
                    ic1p2n = softNormalsIntex[i][j+1];
                    ic2p2n = softNormalsIntex[i2][j+1];
//                 // XXX doubled normals
//                    Vector3d c1p1n = new Vector3d(c1p1.x - circle.getPoint().x, c1p1.y,  c1p1.z  + circle.getPoint().y);
//                    Vector3d c2p1n = new Vector3d(c2p1.x - circle.getPoint().x, c2p1.y,  c2p1.z  + circle.getPoint().y);
//                    Vector3d c1p2n = new Vector3d(c1p2.x - circle.getPoint().x, c1p2.y,  c1p2.z  + circle.getPoint().y);
//                    Vector3d c2p2n = new Vector3d(c2p2.x - circle.getPoint().x, c2p2.y,  c2p2.z  + circle.getPoint().y);
//
//                    ic1p1n = meshFactory.addNormal(c1p1n);
//                    ic2p1n = meshFactory.addNormal(c2p1n);
//                    ic1p2n = meshFactory.addNormal(c1p2n);
//                    ic2p2n = meshFactory.addNormal(c2p2n);
                }

                TextQuadsIndex tq = tc[j];

                face.addVert(ic1p1, tq.getLd(), ic1p1n);
                face.addVert(ic2p1, tq.getRd(), ic2p1n);
                face.addVert(ic2p2, tq.getRt(), ic2p2n);
                face.addVert(ic1p2, tq.getLt(), ic1p2n);
            }

        }
    }

    private static Vector2d[] calsSoftNormals(Point2d[] crossSection) {

        Vector2d[] ret =  new Vector2d[crossSection.length];

        Vector2d [] normals = new Vector2d[crossSection.length - 1];
        for (int i = 0; i < crossSection.length - 1; i++) {
            Vector2d n = new Vector2d(crossSection[i+1]);
            n.sub(crossSection[i]);
            n.normalize();
            normals[i] = n;
        }

        for (int i = 1; i < crossSection.length - 1; i++) {
            Vector2d n1 = normals[i-1];
            Vector2d n2 = normals[i];

            Vector2d n = Vector2dUtil.bisectorNormalized(n1, n2);
            n.normalize();

            ret[i] = n;
        }

        ret[0] = Vector2dUtil.orthogonal(normals[0]);
        //ret[crossSection.length-1] = Vector2dUtil.orthogonal(normals[0]);
        ret[crossSection.length-1] = Vector2dUtil.orthogonal(normals[normals.length-1]);

        for (int i = 0; i < ret.length; i++) {
            ret[i].negate();
        }

        return ret;
    }

    private static TextQuadsIndex[] buildRotaryShapeTextureMapping(MeshFactory meshFactory, int icross, int isection,
            Point3d[][] mesh) {
        TextQuadsIndex [] tc = new TextQuadsIndex[icross];
        // texture mapping
        double textHeightD = 0;
//        for (int i = 0; i< isection; i++) {
        {
            int i = 0;
            Point3d [] c1 = mesh[i];
            Point3d [] c2 = mesh[(i + 1) % isection];


            Point3d middleD = new Point3d(
                    (c1[0].x + c2[0].x) / 2d,
                    (c1[0].y + c2[0].y) / 2d,
                    (c1[0].z + c2[0].z) / 2d);

            double widthD = middleD.distance(c2[0]) / 2d;

            for (int j = 1; j < icross; j++) {

                Point3d middleT = new Point3d(
                        (c1[j].x + c2[j].x) / 2d,
                        (c1[j].y + c2[j].y) / 2d,
                        (c1[j].z + c2[j].z) / 2d);

                double widthT = middleT.distance(c2[j]) / 2d;
                double height = middleD.distance(middleT);
                double textHeightT = textHeightD + height;
                TextQuadsIndex tq = new TextQuadsIndex();

                tq.setLd(meshFactory.addTextCoord(new TextCoord(-widthD, textHeightD)));
                tq.setRd(meshFactory.addTextCoord(new TextCoord(widthD, textHeightD)));

                tq.setRt(meshFactory.addTextCoord(new TextCoord(widthT, textHeightT)));
                tq.setLt(meshFactory.addTextCoord(new TextCoord(-widthT, textHeightT)));


                tc[j-1] = tq;

                middleD = middleT;
                widthD = widthT;
                textHeightD = textHeightT;
            }
        }
        return tc;
    }

    private static Vector3d calcNormal(Point3d p1, Point3d p2, Point3d p3) {
        Vector3d n = new Vector3d(p2);
        n.sub(p1);

        Vector3d n2 = new Vector3d(p3);
        n2.sub(p2);

        n.cross(n, n2);
        n.normalize();
        return n;
    }

    static class TextQuadsIndex {
        int ld;
        int rd;
        int rt;
        int lt;
        /**
         * @return the ld
         */
        public int getLd() {
            return ld;
        }
        /**
         * @param ld the ld to set
         */
        public void setLd(int ld) {
            this.ld = ld;
        }
        /**
         * @return the rd
         */
        public int getRd() {
            return rd;
        }
        /**
         * @param rd the rd to set
         */
        public void setRd(int rd) {
            this.rd = rd;
        }
        /**
         * @return the rt
         */
        public int getRt() {
            return rt;
        }
        /**
         * @param rt the rt to set
         */
        public void setRt(int rt) {
            this.rt = rt;
        }
        /**
         * @return the lt
         */
        public int getLt() {
            return lt;
        }
        /**
         * @param lt the lt to set
         */
        public void setLt(int lt) {
            this.lt = lt;
        }


    }
    static class TextQuat {
        TextCoord ld;
        TextCoord rd;
        TextCoord rt;
        TextCoord lt;
        /**
         * @return the ld
         */
        public TextCoord getLd() {
            return ld;
        }
        /**
         * @param ld the ld to set
         */
        public void setLd(TextCoord ld) {
            this.ld = ld;
        }
        /**
         * @return the rd
         */
        public TextCoord getRd() {
            return rd;
        }
        /**
         * @param rd the rd to set
         */
        public void setRd(TextCoord rd) {
            this.rd = rd;
        }
        /**
         * @return the rt
         */
        public TextCoord getRt() {
            return rt;
        }
        /**
         * @param rt the rt to set
         */
        public void setRt(TextCoord rt) {
            this.rt = rt;
        }
        /**
         * @return the lt
         */
        public TextCoord getLt() {
            return lt;
        }
        /**
         * @param lt the lt to set
         */
        public void setLt(TextCoord lt) {
            this.lt = lt;
        }
    }


    private Vector3d calcNormal(LineSegment2d edge, List<Point2d> points, Map<Point2d, Double> distance) {

        Point2d p1 = edge.getBegin();
        Point2d p2 = edge.getEnd();
        Point2d p3 = null;

        double d3 = -Double.MAX_VALUE;
        for (Point2d p : points) {
            Double d = distance.get(p);

            if (d3 < d) {
                d3 = d;
                p3 = p;
            }

//            if (d3 > 1) {
//                break;
//            }
        }

        Double d1 = distance.get(p1);
        Double d2 = distance.get(p2);

        // Due Bug in skeleton algorithm recalculate distance
        // XXX
//        d3 = calcDistance(p3, edge);


        Vector3d v1 = new Vector3d(p2.x - p1.x, d2 - d1, -p2.y + p1.y);
        Vector3d v2 = new Vector3d(p3.x - p2.x, d3 - d2, -p3.y + p2.y);

        v1.cross(v1, v2);
        v1.normalize();
        return v1;
    }


    /**
     * Computes the distance between this point and point p1.
     * @param p0
     *
     * @param p1
     *            the other point
     * @return
     */
    private static double distance(Tuple2d p0, Tuple2d p1) {
        double dx, dy;

        dx = p0.x - p1.x;
        dy = p0.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
