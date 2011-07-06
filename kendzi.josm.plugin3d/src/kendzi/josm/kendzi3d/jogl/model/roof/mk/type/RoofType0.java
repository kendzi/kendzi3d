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
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;

import org.apache.log4j.Logger;

/**
 * Roof type 0.X.
 *
 * @author Tomasz Kêdziora (Kendzi)
 *
 */
public abstract class RoofType0 extends RectangleRoofType {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofType0.class);


    /**
     * @return roof type.
     */
    abstract int getType();

    @Override
    public boolean isPrefixParameter() {
        return false;
    }

    @Override
    public RoofTypeOutput buildRectangleRoof(List<Point2d> borderList,
            Point2d[] rectangleContur,
            double scaleA ,
            double scaleB,
            double sizeA,
            double sizeB,
            Integer prefixParameter, List<Double> heights, List<Double> sizesB,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData
            ) {


        int type = getType();

        // %
        Double B1 = getSize(0, sizesB, null);
        Double B2 = getSize(1, sizesB, null);
        Double B3 = getSize(2, sizesB, null);
        Double B4 = getSize(3, sizesB, null);

        Double H1 = getSize(0, heights, 0d);
        Double H2 = getSize(1, heights, 1d);

        if (type == 0) {
            //
            B1 = 0d;
            B2 = 0d;
            B3 = 0d;
            B4 = 0d;

            return build(scaleA, scaleB, rectangleContur, B1, B2, B3, B4, H1, H2, pRoofTextureData);

        } else if (type == 1) {
            B1 = getSize(0, sizesB, 0.3d);
            B2 = 0d;
            B3 = 0d;
            B4 = 0d;

//            if (B1 == null) {
//                B1 = 0.3d;
//            }
            return build(scaleA, scaleB, rectangleContur, B1, B2, B3, B4, H1, H2, pRoofTextureData);
        } else if (type == 2) {
            B1 = getSize(0, sizesB, 0.3d);
            B2 = getSize(1, sizesB, B1);

            B3 = 0d;
            B4 = 0d;

            return build(scaleA, scaleB, rectangleContur, B1, B2, B3, B4, H1, H2, pRoofTextureData);

        } else if (type == 3) {
            B1 = getSize(0, sizesB, 0.3d);
            B2 = getSize(1, sizesB, B1);
            B3 = getSize(2, sizesB, B2);

            B4 = 0d;

            return build(scaleA, scaleB, rectangleContur, B1, B2, B3, B4, H1, H2, pRoofTextureData);

        } else if (type == 4) {
            B1 = getSize(0, sizesB, 0.3d);
            B2 = getSize(1, sizesB, B1);
            B3 = getSize(2, sizesB, B2);
            B4 = getSize(3, sizesB, B3);
            return build(scaleA, scaleB, rectangleContur, B1, B2, B3, B4, H1, H2, pRoofTextureData);
        }
        return null;
    }

    private RoofTypeOutput build(double scaleA, double scaleB, Point2d[] rectangleContur, double b1, double b2, double b3, double b4, double h1, double h2, RoofTextureData pRoofTextureData) {

        ModelFactory model = ModelFactory.modelBuilder();
        MeshFactory border = model.addMesh("border1");
        MeshFactory top = model.addMesh("top1");
//        MeshFactory mf = new MeshFactory();
        int ph01 = border.addVertex(new Point3d(0, 0, -1));
        int ph02 = border.addVertex(new Point3d(0, 0, 0));
        int ph03 = border.addVertex(new Point3d(1, 0, 0));
        int ph04 = border.addVertex(new Point3d(1, 0, -1));

        int n0 = border.addNormal(new Vector3d(0, 1, 0));

        int n1 = border.addNormal(new Vector3d(-1, 0, 0));
        int n2 = border.addNormal(new Vector3d(0, 0, -1));
        int n3 = border.addNormal(new Vector3d(1, 0, 0));
        int n4 = border.addNormal(new Vector3d(0, 0, 1));

        int ph11 = border.addVertex(new Point3d(0, h1, -1));
        int ph12 = border.addVertex(new Point3d(0, h1, 0));
        int ph13 = border.addVertex(new Point3d(1, h1, 0));
        int ph14 = border.addVertex(new Point3d(1, h1, -1));

        FaceFactory border1 = border.addFace(FaceType.QUAD_STRIP);
        border1.addVertIndex(ph01);
        border1.addVertIndex(ph11);
        border1.addVertIndex(ph02);
        border1.addVertIndex(ph12);

        border1.addNormalIndex(n1);
        border1.addNormalIndex(n1);
        border1.addNormalIndex(n1);
        border1.addNormalIndex(n1);


        border1.addVertIndex(ph02);
        border1.addVertIndex(ph12);
        border1.addVertIndex(ph03);
        border1.addVertIndex(ph13);

        border1.addNormalIndex(n2);
        border1.addNormalIndex(n2);
        border1.addNormalIndex(n2);
        border1.addNormalIndex(n2);

        border1.addVertIndex(ph03);
        border1.addVertIndex(ph13);
        border1.addVertIndex(ph04);
        border1.addVertIndex(ph14);

        border1.addNormalIndex(n3);
        border1.addNormalIndex(n3);
        border1.addNormalIndex(n3);
        border1.addNormalIndex(n3);


        border1.addVertIndex(ph04);
        border1.addVertIndex(ph14);
        border1.addVertIndex(ph01);
        border1.addVertIndex(ph11);

        border1.addNormalIndex(n4);
        border1.addNormalIndex(n4);
        border1.addNormalIndex(n4);
        border1.addNormalIndex(n4);

        // XXX
        top.normals = border.normals;
        //XXX
        top.vertices = border.vertices;

        FaceFactory top1 = top.addFace(FaceType.QUADS);
        top1.addVertIndex(ph11);
        top1.addVertIndex(ph12);
        top1.addVertIndex(ph13);
        top1.addVertIndex(ph14);

        top1.addNormalIndex(n0);
        top1.addNormalIndex(n0);
        top1.addNormalIndex(n0);
        top1.addNormalIndex(n0);

        if (h2 != 0d) {

            MeshFactory border2 = model.addMesh("border2");
            MeshFactory top2 = model.addMesh("top2");
            // MeshFactory mf = new MeshFactory();
            int ph21 = border2.addVertex(new Point3d(b1, h1, -(1 - b4)));
            int ph22 = border2.addVertex(new Point3d(b1, h1, -(b2)));
            int ph23 = border2.addVertex(new Point3d(1 - b3, h1, -(b2)));
            int ph24 = border2.addVertex(new Point3d(1 - b3, h1, -(1 - b4)));

             int n20 = border2.addNormal(new Vector3d(0, 1, 0));
//
            int n21 = border2.addNormal(new Vector3d(-1, 0, 0));
            int n22 = border2.addNormal(new Vector3d(0, 0, -1));
            int n23 = border2.addNormal(new Vector3d(1, 0, 0));
            int n24 = border2.addNormal(new Vector3d(0, 0, 1));

            int ph31 = border2.addVertex(new Point3d(b1, h1 + h2, -(1 - b4)));
            int ph32 = border2.addVertex(new Point3d(b1, h1 + h2, -(b2)));
            int ph33 = border2.addVertex(new Point3d(1 - b3, h1 + h2, -(b2)));
            int ph34 = border2.addVertex(new Point3d(1 - b3, h1 + h2, -(1 - b4)));

            FaceFactory border1Face = border2.addFace(FaceType.QUAD_STRIP);
            border1Face.addVertIndex(ph21);
            border1Face.addVertIndex(ph31);
            border1Face.addVertIndex(ph22);
            border1Face.addVertIndex(ph32);

            border1Face.addNormalIndex(n21);
            border1Face.addNormalIndex(n21);
            border1Face.addNormalIndex(n21);
            border1Face.addNormalIndex(n21);


            border1Face.addVertIndex(ph22);
            border1Face.addVertIndex(ph32);
            border1Face.addVertIndex(ph23);
            border1Face.addVertIndex(ph33);

            border1Face.addNormalIndex(n22);
            border1Face.addNormalIndex(n22);
            border1Face.addNormalIndex(n22);
            border1Face.addNormalIndex(n22);

            border1Face.addVertIndex(ph23);
            border1Face.addVertIndex(ph33);
            border1Face.addVertIndex(ph24);
            border1Face.addVertIndex(ph34);

            border1Face.addNormalIndex(n23);
            border1Face.addNormalIndex(n23);
            border1Face.addNormalIndex(n23);
            border1Face.addNormalIndex(n23);


            border1Face.addVertIndex(ph24);
            border1Face.addVertIndex(ph34);
            border1Face.addVertIndex(ph21);
            border1Face.addVertIndex(ph31);

            border1Face.addNormalIndex(n24);
            border1Face.addNormalIndex(n24);
            border1Face.addNormalIndex(n24);
            border1Face.addNormalIndex(n24);

            // XXX
            top2.normals = border2.normals;
            //XXX
            top2.vertices = border2.vertices;

            FaceFactory top1Face = top2.addFace(FaceType.QUADS);
            top1Face.addVertIndex(ph31);
            top1Face.addVertIndex(ph32);
            top1Face.addVertIndex(ph33);
            top1Face.addVertIndex(ph34);

            top1Face.addNormalIndex(n20);
            top1Face.addNormalIndex(n20);
            top1Face.addNormalIndex(n20);
            top1Face.addNormalIndex(n20);



        }

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1 + h2);

        rto.setModel(model);

        return rto;
    }
}
