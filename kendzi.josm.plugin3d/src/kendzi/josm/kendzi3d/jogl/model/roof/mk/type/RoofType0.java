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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Material;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.space.RoofHooksSpace;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonSplitUtil;
import kendzi.math.geometry.polygon.split.SplitPolygon;
import kendzi.math.geometry.polygon.split.SplitPolygons;

import org.apache.log4j.Logger;

/**
 * Roof type 0.X.
 *
 * @author Tomasz Kędziora (Kendzi)
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

    /**
     * @param pBorderList
     * @param pScaleA
     * @param pScaleB
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
           double l1,
           double l2,
           double l3,
           double l4,
           int type,
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

       // first calc BASEE

       PolygonList2d borderPolygon = new PolygonList2d(pBorderList);

       MultiPolygonList2d topMP = new MultiPolygonList2d(borderPolygon);
       Point3d planeRightTopPoint =  new Point3d(
             0 ,
             h1,
             0);


       Vector3d nt = new Vector3d(0, 1  , 0);

       Plane3d planeTop = new Plane3d(
             planeRightTopPoint,
             nt);

       Vector3d roofTopLineVector = new Vector3d(
             -pRecWidth,
             0,
             0);


       Point3d planeRightCenterPoint =  new Point3d(
               0 ,
               h1 + h2,
               0);

       Plane3d planeCenter = new Plane3d(
               planeRightCenterPoint,
               nt);



       RoofTypeUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);

       List<Double> borderHeights = calcHeightList(pBorderList, h1);


       ////******************

       RoofTypeUtil.makeRoofBorderMesh(
               pBorderList,
               borderHeights,
               meshBorder,
               facadeTexture
               );


       /// NOW UPPER PART

       if (type >= 1) {

           LinePoints2d bLine = new LinePoints2d(new Point2d(0, l1), new Point2d(pRecWidth, l1));


           SplitPolygon middleSplit = PolygonSplitUtil.splitPolygon(borderPolygon, bLine);


           MultiPolygonList2d centerMP = middleSplit.getTopMultiPolygons();


           if (type >= 2) {
               LinePoints2d rLine = new LinePoints2d(new Point2d(pRecWidth - l2, 0), new Point2d(pRecWidth - l2, pRecHeight));


               SplitPolygons topSplit = PolygonSplitUtil.splitMultiPolygon(centerMP, rLine);

               centerMP = topSplit.getTopMultiPolygons();

           }

           if (type >= 3) {

               LinePoints2d tLine = new LinePoints2d(new Point2d(pRecWidth, pRecHeight - l3), new Point2d(0, pRecHeight - l3));


               SplitPolygons topSplit = PolygonSplitUtil.splitMultiPolygon(centerMP, tLine);

               centerMP = topSplit.getTopMultiPolygons();

           }

           if (type >= 4) {

               LinePoints2d tLine = new LinePoints2d(new Point2d(l4, pRecHeight), new Point2d(l4, 0));


               SplitPolygons topSplit = PolygonSplitUtil.splitMultiPolygon(centerMP, tLine);

               centerMP = topSplit.getTopMultiPolygons();

           }

           RoofTypeUtil.addPolygonToRoofMesh(meshRoof, centerMP, planeCenter, roofTopLineVector, roofTexture);

           for (PolygonList2d polygon : centerMP.getPolygons()) {

               List<Point2d> centerPolygonPoints = polygon.getPoints();

               List<Double> centerHeights = calcHeightList(centerPolygonPoints, h1 + h2);


               RoofTypeUtil.makeRoofBorderMesh(
                       centerPolygonPoints,
                       h1,
                       centerHeights,
                       meshBorder,
                       facadeTexture
                       );

           }

       }





       RoofTypeOutput rto = new RoofTypeOutput();
       rto.setHeight(h1 + h2);

       rto.setModel(model);

       RoofHooksSpace [] rhs = null;
//           buildRectRoofHooksSpace(
//                   pRectangleContur,
//                   new PolygonPlane(bottomMP, planeBottom),
//                   null,
//                   new PolygonPlane(topMP, planeTop),
//                   null
//                 );

       rto.setRoofHooksSpaces(null);

       return rto;
   }

   private List<Double> calcHeightList(
           List<Point2d> pSplitBorder, double height) {

       List<Double> borderHeights = new ArrayList<Double>(pSplitBorder.size());
       for (Point2d point : pSplitBorder) {
          borderHeights.add(height);
       }

       return borderHeights;
   }
}
