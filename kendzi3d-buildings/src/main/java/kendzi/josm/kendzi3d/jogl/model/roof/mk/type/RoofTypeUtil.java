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

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.wall.HeightCalculator;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.split.PolygonSplit;

import org.apache.log4j.Logger;

public class RoofTypeUtil {
    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofTypeUtil.class);



    /** Make roof border mesh. It is wall under roof.
     * @param borderSplit
     * @param borderHeights
     * @param pMeshBorder border mesh
     * @param facadeTexture
     */
    public static void makeRoofBorderMesh(

            List<Point2d> borderSplit ,
            List<Double> borderHeights,
            MeshFactory pMeshBorder,
            TextureData facadeTexture) {

        makeRoofBorderMesh(borderSplit, 0, borderHeights, pMeshBorder, facadeTexture);
    }

    /** Make roof border mesh. It is wall under roof.
     * @param borderSplit
     * @param minHeight
     * @param borderHeights
     * @param pMeshBorder border mesh
     * @param facadeTexture
     */
    public static void makeRoofBorderMesh(

            List<Point2d> borderSplit ,
            double minHeight,
            List<Double> borderHeights,
            MeshFactory pMeshBorder,
            TextureData facadeTexture) {


        List<Double> borderMinHeights = new ArrayList<Double>(borderHeights.size());
        Double min = new Double(minHeight);
        for (int i =0; i< borderHeights.size(); i++) {
            // XXX do it without list!
            borderMinHeights.add(min);
        }
        makeRoofBorderMesh(borderSplit, borderMinHeights, borderHeights, pMeshBorder, facadeTexture);
    }

    /** Make roof border mesh. It is wall under roof.
     * @param borderSplit
     * @param borderHeights
     * @param pMeshBorder border mesh
     * @param facadeTexture
     */
    public static void makeRoofBorderMesh(

            List<Point2d> borderSplit ,
            List<Double> borderMinHeights,
            List<Double> borderHeights,
            MeshFactory pMeshBorder,
            TextureData facadeTexture) {



        boolean isCounterClockwise = false;
        if (0.0f < Triangulate.area(borderSplit)) {
            isCounterClockwise = true;
        }

        List<Double> heights = borderHeights;
        List<Double> minHeights = borderMinHeights;


        List<Point2d> pBorderExtanded = borderSplit;

        Integer [] bottomPointsIndex = new Integer[pBorderExtanded.size()];
        Integer [] topPointsIndex = new Integer[pBorderExtanded.size()];


        FaceFactory face = pMeshBorder.addFace(FaceType.QUADS);

        double uLast = 0;

        for (int i = 0; i < pBorderExtanded.size(); i++) {
            int index1 = i;
            int index2 = (i + 1) % pBorderExtanded.size();

            Point2d point1 = pBorderExtanded.get(index1);
            Point2d point2 = pBorderExtanded.get(index2);

            double height1 = heights.get(index1);
            double height2 = heights.get(index2);

            double minHeight1 = minHeights.get(index1);
            double minHeight2 = minHeights.get(index2);


            int point1HightIndex = cachePointIndex(point1, index1, height1, topPointsIndex, pMeshBorder);
            int point2HightIndex = cachePointIndex(point2, index2, height2, topPointsIndex, pMeshBorder);

            int point1BottomIndex = cachePointIndex(point1, index1, minHeight1, bottomPointsIndex, pMeshBorder);
            int point2BottomIndex = cachePointIndex(point2, index2, minHeight2, bottomPointsIndex, pMeshBorder);

            Vector3d n = new Vector3d(-(point2.y - point1.y), 0, -(point2.x - point1.x));
            n.normalize();

            if (isCounterClockwise) {
                n.negate();
            }

            int normalIndex = pMeshBorder.addNormal(n);

            double uBegin = uLast;
            double uEnd = uLast + point1.distance(point2) / facadeTexture.getWidth();
            uLast = uEnd;

            int tc_0_0 = pMeshBorder.addTextCoord(new TextCoord(uBegin  , minHeight1  / facadeTexture.getHeight()));
            int tc_0_v = pMeshBorder.addTextCoord(new TextCoord(uBegin  , height1 / facadeTexture.getHeight()));
            int tc_u_0 = pMeshBorder.addTextCoord(new TextCoord(uEnd  , minHeight2 / facadeTexture.getHeight()));
            int tc_u_v = pMeshBorder.addTextCoord(new TextCoord(uEnd  , height2 / facadeTexture.getHeight()));


            face.addVertIndex(point1HightIndex);
            face.addVertIndex(point1BottomIndex);
            face.addVertIndex(point2BottomIndex);
            face.addVertIndex(point2HightIndex);

            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);

            face.addCoordIndex(tc_0_v);
            face.addCoordIndex(tc_0_0);
            face.addCoordIndex(tc_u_0);
            face.addCoordIndex(tc_u_v);

        }
    }

    private static int cachePointIndex(Point2d pPoint, int pPointIndex, double pHeight, Integer[] pPointsIndexCache,
            MeshFactory pMeshBorder) {

        if (pPointsIndexCache[pPointIndex] == null) {
            int p1i = pMeshBorder.addVertex(new Point3d(pPoint.x, pHeight, -pPoint.y));
            pPointsIndexCache[pPointIndex] = p1i;
        }

        int point2BottomIndex = pPointsIndexCache[pPointIndex];
        return point2BottomIndex;
    }

    /** Splits polygon by lines. Adds extra points in crossing places.
     * @param pPolygon polygon to split by lines
     * @param pLines lines to split polygon
     * @return polygon with extra points on crossing places
     */
    public static List<Point2d> splitBorder(PolygonList2d pPolygon, LinePoints2d... pLines) {

        List<Point2d> splitPolygon = new ArrayList<Point2d>(pPolygon.getPoints());


        for (LinePoints2d line : pLines) {
            splitPolygon = PolygonSplit.splitLineSegmentsOnLine(line, splitPolygon);
        }

        return splitPolygon;
    }

    /**
     * This is temporary method. It group code for wall generation under roof gutters.
     * Walls are generated from building_max_height - roof_height to building_max_height.
     * This code will be removed in future.
     * 
     * @param pOutline
     * @param pHeightCalculator
     * @param minHeight
     * @param pWallMesh
     * @param pFacadeTextureData
     */
    @Deprecated
    public static void makeWallsFromHeightCalculator(List<Point2d> pOutline, HeightCalculator pHeightCalculator, double minHeight, MeshFactory pWallMesh,
            TextureData pFacadeTextureData) {
        List<Point2d> borderSplit = new ArrayList<Point2d>();
        List<Double> borderHeights = new ArrayList<Double>();
        {
            // This is only temporary, border generation code will be moved
            for (int i = 0; i < pOutline.size(); i++) {
                Point2d p1 = pOutline.get(i);
                Point2d p2 = pOutline.get((i + 1) % pOutline.size());

                SegmentHeight[] height2 = pHeightCalculator.height(p1, p2);

                for (int j = 0; j < height2.length; j++) {
                    borderSplit.add(height2[j].getBegin());
                    borderHeights.add(height2[j].getBeginHeight());
                }

            }
        }

        RoofTypeUtil.makeRoofBorderMesh(borderSplit, minHeight, borderHeights, pWallMesh, pFacadeTextureData);
    }

    /** Find minimal rectangle containing list of points.
     * Save as list of 3d points to display.
     *
     * XXX this should by changed!
     *
     * @param pPolygon list of points
     * @param height height
     * @return minimal rectangle
     */
    public static List<Point3d> findRectangle(List<Point2d> pPolygon, double height) {

        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;

        for (Point2d p : pPolygon) {
            if (minx > p.x) {
                minx = p.x;
            }
            if (miny > p.y) {
                miny = p.y;
            }
            if (maxx < p.x) {
                maxx = p.x;
            }
            if (maxy < p.y) {
                maxy = p.y;
            }
        }


        List<Point3d> rect = new ArrayList<Point3d>();
        rect.add(new Point3d(minx, height, -miny));
        rect.add(new Point3d(minx, height, -maxy));
        rect.add(new Point3d(maxx, height, -maxy));
        rect.add(new Point3d(maxx, height, -miny));

        return rect;
    }
}
