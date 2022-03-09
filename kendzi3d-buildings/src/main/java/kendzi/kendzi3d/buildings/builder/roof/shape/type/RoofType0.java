/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space.RoofHooksSpace;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper.MultiPolygonSplitResult;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * Roof type 0.X.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public abstract class RoofType0 extends RectangleRoofTypeBuilder {

    /**
     * @return roof type.
     */
    protected abstract int getType();

    /**
     * @param pBorderList
     * @param pRecHeight
     * @param pRecWidth
     * @param pRectangleContur
     * @param h1
     * @param h2
     * @param l1
     * @param l2
     * @param l3
     * @param l4
     * @param type
     * @param model
     * @param roofTextureData
     * @return
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double pRecHeight, double pRecWidth,
            Vector2dc[] pRectangleContur, double h1, double h2, double l1, double l2, double l3, double l4, int type,
            // ModelFactory model,
            RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData facadeTexture = roofTextureData.getFacade().getTextureData();
        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        Vector3dc nt = new Vector3d(0, 1, 0);

        Vector3dc roofTopLineVector = new Vector3d(-pRecWidth, 0, 0);

        Vector3dc planeRightCenterPoint = new Vector3d(0, h1 + h2, 0);

        Plane3d planeCenter = new Plane3d(planeRightCenterPoint, nt);

        List<Vector2dc> borderList = buildingPolygon.getOuter().getPoints();
        List<List<Vector2dc>> innerLists = innerLists(buildingPolygon);

        MeshFactoryUtil.addPolygonWithHolesInY(buildingPolygon, h1, meshRoof, roofTexture, 0, 0, roofTopLineVector);

        List<Double> borderHeights = calcHeightList(borderList, h1);

        if (h1 > 0) {
            RoofTypeUtil.makeRoofBorderMesh(borderList, borderHeights, meshBorder, facadeTexture);

            for (List<Vector2dc> inner : innerLists) {
                // for inners
                List<Double> innerHeights = calcHeightList(inner, h1);

                RoofTypeUtil.makeRoofBorderMesh(inner, innerHeights, meshBorder, facadeTexture);
            }
        }

        // / NOW UPPER PART

        if (type >= 1) {

            LinePoints2d bLine = new LinePoints2d(new Vector2d(0, l1), new Vector2d(pRecWidth, l1));

            MultiPolygonSplitResult middleSplit = PolygonSplitHelper
                    .splitMultiPolygon(new MultiPolygonList2d(new PolygonList2d(borderList)), bLine);

            MultiPolygonList2d centerMP = middleSplit.getLeftMultiPolygon();

            if (type >= 2) {
                LinePoints2d rLine = new LinePoints2d(new Vector2d(pRecWidth - l2, 0), new Vector2d(pRecWidth - l2, pRecHeight));

                MultiPolygonSplitResult topSplit = PolygonSplitHelper.splitMultiPolygon(centerMP, rLine);

                centerMP = topSplit.getLeftMultiPolygon();

            }

            if (type >= 3) {

                LinePoints2d tLine = new LinePoints2d(new Vector2d(pRecWidth, pRecHeight - l3), new Vector2d(0, pRecHeight - l3));

                MultiPolygonSplitResult topSplit = PolygonSplitHelper.splitMultiPolygon(centerMP, tLine);

                centerMP = topSplit.getLeftMultiPolygon();

            }

            if (type >= 4) {

                LinePoints2d tLine = new LinePoints2d(new Vector2d(l4, pRecHeight), new Vector2d(l4, 0));

                MultiPolygonSplitResult topSplit = PolygonSplitHelper.splitMultiPolygon(centerMP, tLine);

                centerMP = topSplit.getLeftMultiPolygon();

            }

            MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, centerMP, planeCenter, roofTopLineVector, roofTexture);

            for (PolygonList2d polygon : centerMP.getPolygons()) {

                List<Vector2dc> centerPolygonPoints = polygon.getPoints();

                List<Double> centerHeights = calcHeightList(centerPolygonPoints, h1 + h2);

                RoofTypeUtil.makeRoofBorderMesh(centerPolygonPoints, h1, centerHeights, meshBorder, facadeTexture);

            }

        }

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1 + h2);

        // rto.setModel(model);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        RoofHooksSpace[] rhs = null;
        // buildRectRoofHooksSpace(
        // pRectangleContur,
        // new PolygonPlane(bottomMP, planeBottom),
        // null,
        // new PolygonPlane(topMP, planeTop),
        // null
        // );

        rto.setRoofHooksSpaces(null);

        return rto;
    }

    private List<List<Vector2dc>> innerLists(PolygonWithHolesList2d buildingPolygon) {
        List<List<Vector2dc>> ret = new ArrayList<>();

        if (buildingPolygon.getInner() == null) {
            return ret;
        }

        for (PolygonList2d p : buildingPolygon.getInner()) {
            ret.add(p.getPoints());
        }

        return ret;
    }

    private List<Double> calcHeightList(List<Vector2dc> pSplitBorder, double height) {

        List<Double> borderHeights = new ArrayList<>(pSplitBorder.size());
        for (Vector2dc point : pSplitBorder) {
            borderHeights.add(height);
        }

        return borderHeights;
    }
}
