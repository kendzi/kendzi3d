/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.TextCordFactory;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.josm.kendzi3d.jogl.model.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementParserUtil;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;
import kendzi.math.geometry.point.TransformationMatrix3d;

import org.apache.log4j.Logger;
import org.ejml.data.SimpleMatrix;

public class RoofDormerTypeA implements RoofDormerType {

    /** Log. */
    private static final Logger log = Logger.getLogger(RoofDormerTypeA.class);

    @Override
    public Character getPrefixKey() {
        return 'a';
    }

    @Override
    public RoofDormerTypeOutput buildRoof(
            RoofHookPoint pRoofHookPoint,
            RoofHooksSpace space,
            Map<MeasurementKey, Measurement> pMeasurements,
            RoofTextureData pRoofTextureData) {

        RoofDormerTypeOutput out = new RoofDormerTypeOutput();


        TextureData facadeTexture = pRoofTextureData.getFacadeTextrure();
        TextureData roofTexture = pRoofTextureData.getRoofTexture();

        double width1 = getWidth1(pMeasurements);
        double height1 = getHeight1(pMeasurements);

        // XXX
        double depth = pRoofHookPoint.getDepth();

        double height2 = getHeight2(pMeasurements, height1, depth);

        return buildMesh(pRoofHookPoint, space, out, facadeTexture, roofTexture, width1, height1, depth, height2);
    }

    private double getWidth1(Map<MeasurementKey, Measurement> pMeasurements) {


        Measurement measurement = getMeasurement(MeasurementKey.DORMER_WIDTH_1, pMeasurements);

        if (measurement == null) {
            return 1.5;
        }

        if (isUnit(measurement, MeasurementUnit.METERS)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.UNKNOWN)) {
            return measurement.getValue();
        } else {
            log.error(MeasurementParserUtil.getErrorMessage(MeasurementKey.DORMER_WIDTH_1, measurement));
            return 1.5;
        }
    }

    private Measurement getMeasurement(MeasurementKey pDormerWidth1, Map<MeasurementKey, Measurement> pMeasurements) {
        if (pMeasurements == null) {
            return null;
        }
        return pMeasurements.get(pDormerWidth1);
    }

    private double getHeight1(Map<MeasurementKey, Measurement> pMeasurements) {

        Measurement measurement = getMeasurement(MeasurementKey.DORMER_HEIGHT_1, pMeasurements);

        if (measurement == null) {
            return 1.5;
        }
        if (isUnit(measurement, MeasurementUnit.METERS)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.UNKNOWN)) {
            return measurement.getValue();
        } else {
            log.error(MeasurementParserUtil.getErrorMessage(MeasurementKey.DORMER_WIDTH_1, measurement));
            return 1.5;
        }
    }

    private double getHeight2(Map<MeasurementKey, Measurement> pMeasurements, double height1, double depth) {

        Measurement measurement = getMeasurement(MeasurementKey.DORMER_HEIGHT_2, pMeasurements);

        if (measurement == null) {
            return height1 + depth * Math.tan(Math.toRadians(10));
        }
        if (isUnit(measurement, MeasurementUnit.METERS)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.UNKNOWN)) {
            return measurement.getValue();
        } else if (isUnit(measurement, MeasurementUnit.DEGREES)) {
            return height1 + depth * Math.tan(Math.toRadians(measurement.getValue()));
        } else {
            log.error(MeasurementParserUtil.getErrorMessage(MeasurementKey.DORMER_HEIGHT_2, measurement));
            return 1.5;
        }
    }

    boolean isUnit(Measurement pMeasurement, MeasurementUnit pMeasurementUnit) {
        if (pMeasurement == null) {
            return false;
        }
        if (pMeasurementUnit.equals(pMeasurement.getUnit())) {
            return true;
        }
        return false;
    }



    private RoofDormerTypeOutput buildMesh(RoofHookPoint pRoofHookPoint, RoofHooksSpace space,
            RoofDormerTypeOutput out, TextureData facadeTexture, TextureData roofTexture, double s1, double h1,
            double d, double h2) {

        int facadeMaterialIndex = RoofTextureData.FACADE_TEXTRURE_INDEX;
        int topMaterialIndex = RoofTextureData.ROOF_TEXTRURE_INDEX;

        double v1 = (h1 / facadeTexture.getHeight());
        double v2 = (h2 / facadeTexture.getHeight());
        double u1 = (s1 / facadeTexture.getLenght());
        double u2 = (d / facadeTexture.getLenght());

        MeshFactory border = MeshFactory.meshBuilder("border");
        border.materialID = facadeMaterialIndex;
        border.hasTexture = true;

        int b01 = border.addVertex(new Point3d(-0.5 * s1, 0, 0));
        int b11 = border.addVertex(new Point3d(-0.5 * s1, h1, 0));

        int b02 = border.addVertex(new Point3d(0.5 * s1, 0, 0));
        int b12 = border.addVertex(new Point3d(0.5 * s1, h1, 0));

        int b03 = border.addVertex(new Point3d(0.5 * s1, 0, -d));
        int b13 = border.addVertex(new Point3d(0.5 * s1, h2, -d));

        int b04 = border.addVertex(new Point3d(-0.5 * s1, 0, -d));
        int b14 = border.addVertex(new Point3d(-0.5 * s1, h2, -d));

        int nb1 = border.addNormal(new Vector3d(0, 0, 1));
        int nb2 = border.addNormal(new Vector3d(1, 0, 0));
        int nb3 = border.addNormal(new Vector3d(0, 0, -1));
        int nb4 = border.addNormal(new Vector3d(-1, 0, 0));


        int t_0_0 = border.addTextCoord(new TextCoord(0, 0));
        int t_0_v1 = border.addTextCoord(new TextCoord(0, v1));
        int t_u1_0 = border.addTextCoord(new TextCoord(u1, 0));
        int t_u1_v1 = border.addTextCoord(new TextCoord(u1, v1));

        int t_u2_0 = border.addTextCoord(new TextCoord(u2, 0));
        int t_u2_v2 = border.addTextCoord(new TextCoord(u2, v2));

        int t_0_v2 = border.addTextCoord(new TextCoord(0, v2));
        int t_u1_v2 = border.addTextCoord(new TextCoord(u1, v2));


//        int n = border.addNormal(norm);
//
//        double distance = calcScaledDistance(beginPoint, endPoint, pScaleA, pScaleB);
//        double uEnd = (int) (distance / facadeTexture.getLenght());
//
//        int tc1 = border.addTextCoord(new TextCoord(0, 0));
//        int tc2 = border.addTextCoord(new TextCoord(0, vBegin));
//        int tc3 = border.addTextCoord(new TextCoord(uEnd, vEnd));
//        int tc4 = border.addTextCoord(new TextCoord(uEnd, 0));
//
//        double minHeight = 0;
//        int w1 = border.addVertex(new Point3d(beginPoint.getX(),  minHeight, beginPoint.getY()));
//        int w2 = border.addVertex(new Point3d(beginPoint.getX(), beginHeight, beginPoint.getY()));
//        int w3 = border.addVertex(new Point3d(endPoint.getX(), endHeight, endPoint.getY()));
//        int w4 = border.addVertex(new Point3d(endPoint.getX(), minHeight, endPoint.getY()));


        FaceFactory face = border.addFace(FaceType.QUADS);
        // wall 1
        face.addVert(b01, t_0_0, nb1);
        face.addVert(b11, t_0_v1, nb1);
        face.addVert(b12, t_u1_v1, nb1);
        face.addVert(b02, t_u1_0, nb1);
        // wall 2
        face.addVert(b02, t_0_0, nb2);
        face.addVert(b12, t_0_v1, nb2);
        face.addVert(b13, t_u2_v2, nb2);
        face.addVert(b03, t_u2_0, nb2);
        // wall 3
        face.addVert(b03, t_0_0, nb3);
        face.addVert(b13, t_0_v2, nb3);
        face.addVert(b14, t_u1_v2, nb3);
        face.addVert(b04, t_u1_0, nb3);
        // wall 4
        face.addVert(b04, t_u2_0, nb4);
        face.addVert(b14, t_u2_v2, nb4);
        face.addVert(b11, t_0_v1, nb4);
        face.addVert(b01, t_0_0, nb4);


//        top
        MeshFactory top = MeshFactory.meshBuilder("border");
        top.materialID = topMaterialIndex;
        top.hasTexture = true;

        Point3d p1 = new Point3d(-0.5 * s1, h1, 0);
        Point3d p2 = new Point3d(0.5 * s1, h1, 0);
        Point3d p3 = new Point3d(0.5 * s1, h2, -d);
        Point3d p4 = new Point3d(-0.5 * s1, h2, -d);

        int t1 = top.addVertex(p1);
        int t2 = top.addVertex(p2);
        int t3 = top.addVertex(p3);
        int t4 = top.addVertex(p4);

        Vector3d tn = new Vector3d(0, d, (h2 - h1));
        tn.normalize();
        int tni = top.addNormal(tn);

        Vector3d roofLineVector = new Vector3d(1, 0, 0);

        int tc1 = top.addTextCoord(calcUV(p1, tn, roofLineVector, p1, roofTexture));
        int tc2 = top.addTextCoord(calcUV(p2, tn, roofLineVector, p1, roofTexture));
        int tc3 = top.addTextCoord(calcUV(p3, tn, roofLineVector, p1, roofTexture));
        int tc4 = top.addTextCoord(calcUV(p4, tn, roofLineVector, p1, roofTexture));

        FaceFactory topFace = top.addFace(FaceType.QUADS);
        // wall 1
        topFace.addVert(t1, tc1, tni);
        topFace.addVert(t2, tc2, tni);
        topFace.addVert(t3, tc3, tni);
        topFace.addVert(t4, tc4, tni);

        List<MeshFactory> ret = new ArrayList<MeshFactory>();
        ret.add(border);
        ret.add(top);

        out.setMesh(ret);

        Point3d extPoint = pRoofHookPoint.getPoint();
        SimpleMatrix tranA = TransformationMatrix3d.tranA(extPoint.x, extPoint.y, extPoint.z);

        out.setTransformationMatrix(space.getTransformationMatrix().mult(tranA));
//        out.setTransformationMatrix(tranA.mult(space.getTransformationMatrix()));

        return out;
    }


    /** XXX
     * @param pPointToCalc to calculates texture coordinates
     * @param pPlaneNormal normal vector of surface plane
     * @param pLineVector vector laying on the plane (texture is parallel to this vector)
     * @param pStartPoint point when texture starts, laying on surface
     * @param pTexture texture
     * @return uv cordinates for texture
     */
    private TextCoord calcUV(Point3d pPointToCalc, Vector3d pPlaneNormal, Vector3d pLineVector, Point3d pStartPoint,
            TextureData pTexture) {

        return TextCordFactory.calcFlatSurfaceUV(pPointToCalc, pPlaneNormal, pLineVector, pStartPoint, pTexture);
    }

}
