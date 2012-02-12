/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.TextCordFactory;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.josm.kendzi3d.jogl.model.TextureData;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.RectangleUtil;
import kendzi.math.geometry.Triangulate;

import org.apache.log4j.Logger;

/**
 * Represent gable roof.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class GableRoof {

    /** Log. */
    private static final Logger log = Logger.getLogger(GableRoof.class);










    /** Find minimum height.
     * @param pHeight height list
     * @return minimum height
     */
    public static double findRoofMinHeight(List<java.lang.Double> pHeight) {

        double minHeight = java.lang.Double.MAX_VALUE;

        for (double height1 : pHeight) {

            if (minHeight > height1) {
                minHeight = height1;
            }
            //			if (maxHeight < height1) {
            //				maxHeight = height1;
            //			}
        }
        return minHeight;
    }

    /** Add all points from polygon to mesh. Add height to them.
     * @param pMeshRoof mesh of roof
     * @param pPloygon polygon
     * @param pHeight height list of points in polygon
     */
    public static void addVertexToRoofMesh(MeshFactory pMeshRoof,
            List<Point2d> pPloygon, List<java.lang.Double> pHeight) {

        // Convert 2d > 3d. Calculate height for all vertex;
        for (int i = 0; i < pPloygon.size(); i++) {

            Point2d p = pPloygon.get(i);
            double height1 = pHeight.get(i);

            Point3d p3 = new Point3d();
            p3.x = p.x;
            p3.y = height1;
            p3.z = -p.y;

            pMeshRoof.addVertex(p3);
        }
    }

    /** Add polygons to roof mesh.
     * @param pMeshRoof roof mesh
     * @param pPolygonsPoints point of polygons
     * @param pPolygons list of polygons
     * @param plane2
     * @param pRoofLineVector
     * @param roofTexture
     */
    public static void addPolygonToRoofMesh(MeshFactory pMeshRoof, List<Point2d> pPolygonsPoints,
            List<List<Integer>> pPolygons, Plane3d plane2, Vector3d pRoofLineVector, TextureData roofTexture) {

        int normalIndex = pMeshRoof.addNormal(plane2.getNormal());

        Integer [] pointsIndex = new Integer[pPolygonsPoints.size()];

        //at last we create model
        // connect all polygon in mesh
        for (List<Integer> polyIndex : pPolygons) {

            List<Point2d> poly = makeListFromIndex(pPolygonsPoints, polyIndex);

            if (poly.size() < 3) {
                log.error("blad za malo wiezcholkow !!!!!!");
                continue;
            }
            int s1 = poly.size();
            Triangulate t = new Triangulate();
            poly = t.removeClosePoints1(poly);

            if (s1 != poly.size()) {
                log.error("error polygon have dublet points!! it require to fix");
            }

            List<Integer> trianglePoly = t.processIndex(poly);

            if (trianglePoly == null) {
                log.error("trianglePoly: == null");
                continue;
            }

            FaceFactory face = pMeshRoof.addFace(FaceType.TRIANGLES);
            for (Integer i : trianglePoly) {
                // index magic

                Integer pointIndex = polyIndex.get(i);
                if (pointsIndex[pointIndex] == null) {
                    // don't calc points twice.

                    Point2d point2d = pPolygonsPoints.get(polyIndex.get(i));

                    double h = plane2.calcYOfPlane(point2d.x, -point2d.y);

                    int vi = pMeshRoof.addVertex(new Point3d(point2d.x, h, -point2d.y));

                    pointsIndex[pointIndex] = vi;
                }

                int vi = pointsIndex[pointIndex];

                Point3d point3d = pMeshRoof.vertices.get(vi);


                face.addVertIndex(vi);

                face.addNormalIndex(normalIndex);

                TextCoord calcUV = calcUV(point3d, plane2.getNormal(), pRoofLineVector, plane2.getPoint(), roofTexture);

                int tci = pMeshRoof.addTextCoord(calcUV);

                face.addCoordIndex(tci);
            }
        }
    }

    /**
     * @see kendzi.jogl.model.factory.TextCordFactory#calcFlatSurfaceUV(Point3d, Vector3d, Vector3d, Point3d, TextureData)
     */
    public static TextCoord calcUV(Point3d point3d, Vector3d pPlaneNormal, Vector3d pRoofLineVector, Point3d pRoofLinePoint,
            TextureData roofTexture) {
        return TextCordFactory.calcFlatSurfaceUV(point3d, pPlaneNormal, pRoofLineVector, pRoofLinePoint, roofTexture);
    }


    /** Make roof border mesh. It is wall under roof.
     * @param pBorder walls polygon
     * @param pBorderExtanded wall polygon with extra points where top roof line divide walls polygon
     * @param pMeshBorder border mesh
     * @param pMinHeght minimal height of roof
     * @param pHight maximal height of roof
     * @param pWallNormals normal vectors of walls
     * @param facadeTexture
     */
    public static void makeRoofBorderMesh(List<Point2d> pBorder,
            List<Point2d> pBorderExtanded, MeshFactory pMeshBorder,
            double pMinHeght, List<java.lang.Double> pHight, Vector3d [] pWallNormals, TextureData facadeTexture) {
        int b = 0;
        int be = 0;

        while (b < pBorder.size() && be < pBorderExtanded.size()) {
            Vector3d normal = pWallNormals[b];

            Point2d pl1 = pBorder.get(b);
            Point2d ph1 = pBorderExtanded.get(be);
            double h1 = pHight.get(be);

            b++;
            be++;
            Point2d pl2 = pBorder.get((b) % pBorder.size());
            Point2d ph2 = pBorderExtanded.get((be) % pBorderExtanded.size());

            int normalIndex = pMeshBorder.addNormal(normal);

            int pl1i = pMeshBorder.addVertex(new Point3d(pl1.x, pMinHeght, -pl1.y));
            int ph1i = pMeshBorder.addVertex(new Point3d(ph1.x, h1, -ph1.y));
            FaceFactory face = pMeshBorder.addFace(FaceType.TRIANGLE_FAN);

            if (!pl2.equals(ph2)) {
                Point2d phm = ph2;
                double hm = pHight.get((be) % pBorderExtanded.size());
                int plm = pMeshBorder.addVertex(new Point3d(phm.x, hm, -phm.y));

                double u = pl1.distance(phm) / facadeTexture.getLenght();
                int tc = pMeshBorder.addTextCoord(new TextCoord(u  , hm / facadeTexture.getHeight()));

                face.addVertIndex(plm);
                face.addNormalIndex(normalIndex);
                face.addCoordIndex(tc);

                be++;
                ph2 = pBorderExtanded.get((be) % pBorderExtanded.size());



            }

            double h2 = pHight.get((be) % pBorderExtanded.size());

            int pl2i = pMeshBorder.addVertex(new Point3d(pl2.x, pMinHeght, -pl2.y));
            int ph2i = pMeshBorder.addVertex(new Point3d(ph2.x, h2, -ph2.y));


            double u = pl1.distance(pl2) / facadeTexture.getLenght();

            int tc_0_0 = pMeshBorder.addTextCoord(new TextCoord(0  , 0));
            int tc_0_v = pMeshBorder.addTextCoord(new TextCoord(0  , h1 / facadeTexture.getHeight()));
            int tc_u_0 = pMeshBorder.addTextCoord(new TextCoord(u  , 0));
            int tc_u_v = pMeshBorder.addTextCoord(new TextCoord(u  , h2 / facadeTexture.getHeight()));

            face.addVertIndex(ph1i);
            face.addVertIndex(pl1i);
            face.addVertIndex(pl2i);
            face.addVertIndex(ph2i);

            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);
            face.addNormalIndex(normalIndex);

            face.addCoordIndex(tc_0_0);
            face.addCoordIndex(tc_0_v);
            face.addCoordIndex(tc_u_0);
            face.addCoordIndex(tc_u_v);


        }
    }





    private static List<Point2d> makeListFromIndex(List<Point2d> borderExtanded,
            List<Integer> polyIndex) {

        List<Point2d> ret = new ArrayList<Point2d>(polyIndex.size());
        for (Integer i : polyIndex) {
            ret.add(borderExtanded.get(i));
        }
        return ret;
    }




    public static java.lang.Double calcHeight(Point2d p, Point3d planePoint, Vector3d planeNormal1, Vector3d planeNormal2) {

        return Math.min(
                RectangleUtil.calcYOfPlane(p.x, -p.y, planePoint, planeNormal1),
                RectangleUtil.calcYOfPlane(p.x, -p.y, planePoint, planeNormal2)
        );

    }





}
