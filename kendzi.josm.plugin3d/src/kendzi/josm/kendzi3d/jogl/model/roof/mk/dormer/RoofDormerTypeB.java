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
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RoofHooksSpace;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerType;
import kendzi.math.geometry.point.TransformationMatrix3d;

import org.apache.log4j.Logger;
import org.ejml.data.SimpleMatrix;

/**
 * Dormer type B.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class RoofDormerTypeB extends AbstractRoofDormerType {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofDormerTypeB.class);

    @Override
    public DormerType getType() {
        return DormerType.B;
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

        double width1 = getWidth(MeasurementKey.DORMER_WIDTH_1, pMeasurements, 1.5d);
        double height1 = getHeight(MeasurementKey.DORMER_HEIGHT_1, pMeasurements, 1.5d);

        // XXX
        double depth = pRoofHookPoint.getDepth();

        return buildMesh(pRoofHookPoint, space, out, facadeTexture, roofTexture, width1, height1, depth);
    }

    private RoofDormerTypeOutput buildMesh(RoofHookPoint pRoofHookPoint, RoofHooksSpace space,
            RoofDormerTypeOutput out, TextureData facadeTexture, TextureData roofTexture, double width1, double h1,
            double d) {

        int facadeMaterialIndex = RoofTextureData.FACADE_TEXTRURE_INDEX;
        int topMaterialIndex = RoofTextureData.ROOF_TEXTRURE_INDEX;

        double v1 = (h1 / facadeTexture.getHeight());
        double u1 = (width1 / facadeTexture.getLenght());

        MeshFactory border = MeshFactory.meshBuilder("border");
        border.materialID = facadeMaterialIndex;
        border.hasTexture = true;

        int b01 = border.addVertex(new Point3d(-0.5 * width1, 0, 0));
        int b12 = border.addVertex(new Point3d(0, h1, 0));
        int b03 = border.addVertex(new Point3d(0.5 * width1, 0, 0));

        int b04 = border.addVertex(new Point3d(0.5 * width1, 0, -d));
        int b15 = border.addVertex(new Point3d(0, h1, -d));
        int b06 = border.addVertex(new Point3d(-0.5 * width1, 0, -d));


        int nb1 = border.addNormal(new Vector3d(0, 0, 1));
        int nb2 = border.addNormal(new Vector3d(0, 0, -1));

        int t_0_0 = border.addTextCoord(new TextCoord(0, 0));
        int t_u15_v1 = border.addTextCoord(new TextCoord(u1/2, v1));
        int t_u1_0 = border.addTextCoord(new TextCoord(u1, 0));


        FaceFactory face = border.addFace(FaceType.TRIANGLES);
        // wall 1
        face.addVert(b01, t_0_0, nb1);
        face.addVert(b03, t_u1_0, nb1);
        face.addVert(b12, t_u15_v1, nb1);

        // wall 2
        face.addVert(b04, t_0_0, nb2);
        face.addVert(b06, t_u1_0, nb2);
        face.addVert(b15, t_u15_v1, nb2);

        // top
        MeshFactory top = MeshFactory.meshBuilder("top");
        top.materialID = topMaterialIndex;
        top.hasTexture = true;

        Point3d p1 = new Point3d(-0.5 * width1, 0, 0);
        Point3d p25 = new Point3d(0, h1, 0);
        Point3d p38 = new Point3d(0, h1, -d);
        Point3d p4 = new Point3d(-0.5 * width1, 0, -d);

        Point3d p6 = new Point3d(0.5 * width1, 0, 0);
        Point3d p7 = new Point3d(0.5 * width1, 0, -d);

        int t1 = top.addVertex(p1);
        int t25 = top.addVertex(p25);
        int t38 = top.addVertex(p38);
        int t4 = top.addVertex(p4);
        int t6 = top.addVertex(p6);
        int t7 = top.addVertex(p7);

        Vector3d tn1 = new Vector3d(-h1, 0.5 * width1, 0);
        tn1.normalize();
        int tn1i = top.addNormal(tn1);

        Vector3d tn2 = new Vector3d(h1, 0.5 * width1, 0);
        tn2.normalize();
        int tn2i = top.addNormal(tn2);

        Vector3d roofLineVector1 = new Vector3d(0, 0, 1);
        Vector3d roofLineVector2 = new Vector3d(0, 0, -1);

        int tc1 = top.addTextCoord(calcUV(p1, tn1, roofLineVector1, p1, roofTexture));
        int tc2 = top.addTextCoord(calcUV(p25, tn1, roofLineVector1, p1, roofTexture));
        int tc3 = top.addTextCoord(calcUV(p38, tn1, roofLineVector1, p1, roofTexture));
        int tc4 = top.addTextCoord(calcUV(p4, tn1, roofLineVector1, p1, roofTexture));

        int tc5 = top.addTextCoord(calcUV(p25, tn2, roofLineVector2, p7, roofTexture));
        int tc6 = top.addTextCoord(calcUV(p6, tn2, roofLineVector2, p7, roofTexture));
        int tc7 = top.addTextCoord(calcUV(p7, tn2, roofLineVector2, p7, roofTexture));
        int tc8 = top.addTextCoord(calcUV(p38, tn2, roofLineVector2, p7, roofTexture));

        FaceFactory topFace = top.addFace(FaceType.QUADS);
        // roof surface 1
        topFace.addVert(t1, tc1, tn1i);
        topFace.addVert(t25, tc2, tn1i);
        topFace.addVert(t38, tc3, tn1i);
        topFace.addVert(t4, tc4, tn1i);

        // roof surface 2
        topFace.addVert(t25, tc5, tn2i);
        topFace.addVert(t6, tc6, tn2i);
        topFace.addVert(t7, tc7, tn2i);
        topFace.addVert(t38, tc8, tn2i);

        List<MeshFactory> ret = new ArrayList<MeshFactory>();
        ret.add(border);
        ret.add(top);

        out.setMesh(ret);

        Point3d extPoint = pRoofHookPoint.getPoint();
        SimpleMatrix tranA = TransformationMatrix3d.tranA(extPoint.x, extPoint.y, extPoint.z);

        out.setTransformationMatrix(space.getTransformationMatrix().mult(tranA));

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
