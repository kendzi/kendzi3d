/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.validation.ValidationUtil;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofDormerTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType0_0;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType0_1;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType0_2;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType0_3;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType0_4;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType1_0;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType1_1;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_0;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_1;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_2;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_3;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_4;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_5;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_6;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_7;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_8;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType2_9;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType4_0;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType9_0;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeFlat;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeGabled;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeGambrel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeHalfHipped;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeHipped;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypePitched;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypePyramidal;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeSkillion;
import kendzi.math.geometry.point.TransformationMatrix3d;

import org.apache.log4j.Logger;
import org.ejml.data.SimpleMatrix;


/**
 * Dormer roof builder.
 *
 * @author Tomasz Kędziora (kendzi)
 */
public class DormerRoofBuilder {
    /** Log. */
    private static final Logger log = Logger.getLogger(DormerRoofBuilder.class);

    static RoofType [] roofTypes = {
        // word alias for types
        new RoofTypeFlat(),
        new RoofTypePitched(),
        new RoofTypeSkillion(),
        new RoofTypeGabled(),
        new RoofTypeGambrel(),
        new RoofTypeHalfHipped(),
        new RoofTypeHipped(),
        new RoofTypePyramidal(),

        // normal types
        new RoofType0_0(),
        new RoofType0_1(),
        new RoofType0_2(),
        new RoofType0_3(),
        new RoofType0_4(),

        new RoofType1_0(),
        new RoofType1_1(),

        new RoofType2_0(),
        new RoofType2_1(),
        new RoofType2_2(),
        new RoofType2_3(),
        new RoofType2_4(),
        new RoofType2_5(),
        new RoofType2_6(),
        new RoofType2_7(),
        new RoofType2_9(),
        new RoofType2_8(),

        new RoofType4_0(),

        new RoofType9_0()
    };



    /**
     * Dormer roof builder.
     *
     * @param roof roof model
     * @param height roof maximal height. Taken from building
     * @param pRoofTextureData texture data
     * @return roof model
     */
    public static RoofOutput build(
            DormerRoofModel roof,

            double height,

            RoofTextureData pRoofTextureData
            ) {


        List<Point2d> polygon = cleanPolygon(roof.getBuilding().getPoints());

        Point2d startPoint = polygon.get(0);

        RoofType roofType = roof.getRoofType();

        RoofTypeOutput rto = roofType.buildRoof(startPoint, polygon, roof, height, pRoofTextureData);

        List<RoofDormerTypeOutput> roofExtensionsList =
                DormerTypeBuilder.build(
                        rto.getRoofHooksSpaces(),

                        roof,

                        roof.getMeasurements(),
                        pRoofTextureData);



        Model model = buildModel(rto, roofExtensionsList);

        RoofDebugOut debug = buildDebugInfo(rto, roofExtensionsList);


        RoofOutput out = new RoofOutput();
        out.setModel(model);
        out.setHeight(rto.getHeight());
        out.setDebug(debug);

        return out;
    }

    /**
     * Remove last point if it is the same as first.
     *
     * @param pRoofPolygon
     *            polygon
     * @return list of points
     */
    private static List<Point2d> cleanPolygon(List<Point2d> pRoofPolygon) {
        if (pRoofPolygon == null) {
            return null;
        }

        List<Point2d> ret = new ArrayList<Point2d>();
        for (Point2d point : pRoofPolygon) {
            ret.add(point);
        }

        int size = pRoofPolygon.size();

        if (size > 1 && pRoofPolygon.get(0).equals(pRoofPolygon.get(size - 1))) {
            ret.remove(size - 1);

        }

        return ret;

    }


    //    private static void transformModel(RoofTypeOutput rto) {
//        Model model = rto.getModel();
//
//        SimpleMatrix normalMatrix = rto.getTransformationMatrix().invert().transpose();
//
//        Set<Vector3d> procesed = new HashSet<Vector3d>();
//        for (Mesh mesh : model.mesh) {
//            for (int i = 0; i < mesh.vertices.length; i++) {
//                Point3d p = mesh.vertices[i];
//                mesh.vertices[i] = TransformationMatrix3d.transform(p, rto.getTransformationMatrix());
//            }
//
//            for (int i = 0; i < mesh.normals.length; i++) {
//                Vector3d v = mesh.normals[i];
////                if (procesed.contains(v)) {
////                    continue;
////                }
//                procesed.add(v);
//
//
//
//                v = TransformationMatrix3d.transform(v, normalMatrix);
//                // XXX !!!;
//                v.normalize();
//
//                mesh.normals[i] = v;
//            }
//        }
//
//    }
    private static void transformMeshFactory(MeshFactory pMeshFactory,
            SimpleMatrix pTransformationMatrix
//            SimpleMatrix pNormalTransformationMatrix
            ) {
        MeshFactory mesh = pMeshFactory;

        SimpleMatrix normalMatrix = pTransformationMatrix.invert().transpose();

        Set<Vector3d> procesed = new HashSet<Vector3d>();

            List<Point3d> vertices = new ArrayList<Point3d>();
            for (int i = 0; i < mesh.vertices.size(); i++) {
                Point3d p = mesh.vertices.get(i);

                    vertices.add(TransformationMatrix3d.transform(p, pTransformationMatrix));
            }
            mesh.vertices = vertices;

            List<Vector3d> normals = new ArrayList<Vector3d>();
            for (int i = 0; i < mesh.normals.size(); i++) {
                Vector3d v = mesh.normals.get(i);
//                if (procesed.contains(v)) {
//                    continue;
//                }
                procesed.add(v);



                v = TransformationMatrix3d.transform(v, normalMatrix);
                // XXX !!!;
                v.normalize();

                normals.add(v);
            }
            mesh.normals = normals;
    }

    private static RoofDebugOut buildDebugInfo(RoofTypeOutput rto, List<RoofDormerTypeOutput> roofExtensionsList) {

        List<Point3d> rectangleTransf = new ArrayList<Point3d>();

        List<Point3d> rectangle = rto.getRectangle();
        for (int i = 0; i < rectangle.size(); i++) {
            Point3d p = rectangle.get(i);

            rectangleTransf.add(TransformationMatrix3d.transform(p, rto.getTransformationMatrix()));
        }
        rto.setRectangle(rectangleTransf);
        RoofDebugOut out = new RoofDebugOut();
        out.setRectangle(rectangleTransf);

        return out;
    }

    private static Model buildModel(RoofTypeOutput rto, List<RoofDormerTypeOutput> roofExtensionsList) {
        // TODO Auto-generated method stub
//        Model model = new Model();
//        model.mesh = new Mesh[1 + (roofExtensionsList != null ? roofExtensionsList.size() : 0)];
//        model.mesh[0] = mesh;

        ModelFactory modelFactory = rto.getModel();

        for (MeshFactory mf : rto.getModel().getMashFactory()) {
            transformMeshFactory(mf, rto.getTransformationMatrix());
        }

        for (RoofDormerTypeOutput e : roofExtensionsList) {
            if (e == null) {
                continue;
            }
//            SimpleMatrix roofMatrix = e.getTransformationMatrix().mult(rto.getTransformationMatrix());
            SimpleMatrix roofMatrix = rto.getTransformationMatrix().mult(e.getTransformationMatrix());
//            SimpleMatrix roofMatrix = e.getTransformationMatrix();
            for (MeshFactory mf : e.getMesh()) {

                transformMeshFactory(mf, roofMatrix);

                modelFactory.addMesh(mf);
            }
        }

        Model model = modelFactory.toModel();
        model.useLight = true;

        List<String> validate = ValidationUtil.validate(model);
        log.info(ValidationUtil.errorToString(validate));

        return model;
    }
}
