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

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.factory.TextCordFactory;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.Building;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.TextureData;
import kendzi.math.geometry.Graham;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.RectangleUtil;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.point.PointUtil;
import kendzi.math.geometry.polygon.PolygonSplitUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Represent gable roof.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class GableRoof extends Roof {

    /** Log. */
    private static final Logger log = Logger.getLogger(GableRoof.class);


    private static final String BUILDING_ROOF_RIDGE = "building:roof:ridge";
    private static final String BUILDING_ROOF_ORIENTATION_ACROSS = "across";
    @SuppressWarnings("unused")
    private static final String BUILDING_ROOF_ORIENTATION_ALONG = "along";
    private static final String BUILDING_ROOF_ORIENTATION = "building:roof:orientation";


    private Point2d[] contur;

    ModelRender modelRender;
    kendzi.jogl.model.geometry.Model model;
    private boolean pitched;

    //    private Vector3d [] wallNormals;


    /** Create model of gable roof.
     * @param pBuilding building of roof
     * @param pList list of wall points
     * @param pWay way which define building
     * @param pPers 3d perspective
     * @param pPitched if pitched roof
     */
    public GableRoof(Building pBuilding, List<Point2d> pList, Way pWay,
            Perspective3D pPers, boolean pPitched) {
        super(pBuilding, pList, pWay, pPers);

        log.info("test log4j");

        this.modelRender = ModelRender.getInstance();

        this.pitched = pPitched;
    }

    /**
     * Build model of the roof.
     */
    @Override
    public void buildModel() {
        List<Point2d> graham = Graham.grahamScan(this.list);

        this.contur = RectangleUtil.longerSideFirst(RectangleUtil.findRectangleContur(graham));

        ModelFactory modelBuilder = ModelFactory.modelBuilder();

        TextureData roofTexture = getRoofTexture();
        TextureData fasadeTexture = getFasadeTexture();

        int mi = modelBuilder.addMaterial(MaterialFactory.createTextureMaterial(roofTexture.getFile()));
        int mi2 = modelBuilder.addMaterial(MaterialFactory.emptyMaterial());

        MeshFactory meshBorder = modelBuilder.addMesh("roof_border");
//        MeshFactory meshBorder = new MeshFactory();
        MeshFactory meshRoof = modelBuilder.addMesh("roof_top");

        if (BUILDING_ROOF_ORIENTATION_ACROSS.equals(this.way.get(BUILDING_ROOF_ORIENTATION))) {
            this.contur = RectangleUtil.swapOnePoint(this.contur);
        }

        LinePoints2d roofLine = findTopRoofLine(this.pitched);

        boolean isCounterClockwise = false;
        if (0.0f < Triangulate.area(this.list)) {
            isCounterClockwise = true;
        }

        double angle = Math.toRadians(30);

        try {

            angle = Math.toRadians(java.lang.Double.parseDouble(this.way.get("building:roof:angle")));
        } catch (Exception e) {
            e.printStackTrace();
        }


        double roofLineAngle = Math.atan2(
                roofLine.getP2().y - roofLine.getP1().y,
                roofLine.getP2().x - roofLine.getP1().x);

System.out.println(Math.toDegrees(roofLineAngle));

        Vector3d roofLineVector3 = new Vector3d(1, 0, 0);

        Vector3d rotateC3d = PointUtil.rotateZ3d(roofLineVector3, -angle + Math.toRadians(90));

        Vector3d n1 = PointUtil.rotateY3d(rotateC3d, -(-roofLineAngle + Math.toRadians(90)));

        Vector3d n2 = PointUtil.rotateY3d(rotateC3d, -(-roofLineAngle - Math.toRadians(180) + Math.toRadians(90)));

        Point3d planePoint =  new Point3d(
                (roofLine.getP1().x) ,
                this.height,
                -(roofLine.getP1().y));

        Plane3d plane1 = new Plane3d(planePoint, n1);
        Plane3d plane2 = new Plane3d(planePoint, n2);

        Vector3d planeNorm1 = n1;
        Vector3d planeNorm2 = n2;

        List<Point2d> border = new ArrayList<Point2d>();

        List<java.lang.Double> heightList = new ArrayList<java.lang.Double>();



        ////******************
        List<Point2d> borderExtanded = new ArrayList<Point2d>();


        {

            for (Point2d ppp : this.list) {
                border.add(new Point2d(ppp.x, ppp.y));
            }
            if (border.get(border.size() - 1).equals(border.get(0))) {
                border.remove(border.size() - 1);
            }

            List<List<Integer>> polygonsLeft = new ArrayList<List<Integer>>();
            List<List<Integer>> polygonsRight = new ArrayList<List<Integer>>();

            PolygonSplitUtil.splitPolygonByLine(roofLine, border, borderExtanded, polygonsLeft, polygonsRight);

            for (Point2d p : borderExtanded) {
                double height = calcHeight(p, planePoint, planeNorm1, planeNorm2);
                heightList.add(height);
            }

            this.minHeight = findRoofMinHeight(heightList);

            addVertexToRoofMesh(meshRoof, borderExtanded, heightList);

            Vector3d roofLineVector = new Vector3d(
                    roofLine.getP2().x - roofLine.getP1().x,
                    0,
                    -(roofLine.getP2().y - roofLine.getP1().y)
            );


            addPolygonToRoofMesh(meshRoof, borderExtanded, polygonsRight, plane2, roofLineVector, roofTexture);
            addPolygonToRoofMesh(meshRoof, borderExtanded, polygonsLeft, plane1, roofLineVector, roofTexture);

            meshRoof.materialID = mi;
            meshRoof.hasTexture = true;


        }
        ////******************


        makeRoofBorderMesh(
                border,
                borderExtanded,
                meshBorder,
                this.minHeight,
                heightList,
                this.wallNormals,
                fasadeTexture);

        meshBorder.materialID = mi2;
        meshBorder.hasTexture = false;



        this.model = modelBuilder.toModel();
        this.model.setUseLight(true);
        this.model.setUseTexture(true);
    }








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

    /** Finds top roof line.
     * @param pPitched
     * @return top roof line
     */
    private LinePoints2d findTopRoofLine(boolean pPitched) {

        Way ridgeWay = null;

        ref: for (OsmPrimitive op : this.way.getReferrers()) {
            if (op instanceof Relation) {
                Relation r = (Relation) op;
                // XXX this relation is only proposal!
                if ("building".equals(op.get("type"))) {
                    for (RelationMember rm : r.getMembers()) {
                        if ("building:roof:ridge".equals(rm.getRole())
                                && rm.isWay()) {
                            ridgeWay = rm.getWay();
                            break ref;
                        }
                    }
                }
            }
        }

        if (ridgeWay != null &&  ridgeWay.getNodesCount() > 1) {
            // roof line defined in relation

            return new LinePoints2d(
                    this.perspective.calcPoint(ridgeWay.getNode(0)),
                    this.perspective.calcPoint(ridgeWay.getNode(1)));
        }

        int point = 0;
        Point2d p1 = null;
        Point2d p2 = null;
        for (Node node : this.way.getNodes()) {
            // old way
            if ("yes".equals(node.get(BUILDING_ROOF_RIDGE))) {
                if (point == 0) {
                    p1 = this.perspective.calcPoint(node);
                } else if (point == 1) {
                    p2 = this.perspective.calcPoint(node);
                } else {
                    log.error("too mach points");
                }
                point++;
            }
        }
        if (point > 1) {
            return new LinePoints2d(p1, p2);
        }
        if (pPitched) {
            Point2d l1 = new Point2d(
                    (this.contur[1].x),
                    (this.contur[1].y));

            Point2d l2 = new Point2d(
                    (this.contur[0].x) ,
                    (this.contur[0].y));

            return new LinePoints2d(l1, l2);


        }

        // gable
        Point2d l1 = new Point2d(
                (this.contur[3].x + this.contur[0].x) / 2.0,
                (this.contur[3].y + this.contur[0].y) / 2.0);

        Point2d l2 = new Point2d(
                (this.contur[1].x + this.contur[2].x) / 2.0,
                (this.contur[1].y + this.contur[2].y) / 2.0);

        return new LinePoints2d(l1, l2);


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




    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.model.Model#draw(javax.media.opengl.GL2, kendzi.josm.kendzi3d.jogl.Camera)
     */
    @Override
    public void draw(GL2 pGl, Camera pCamera) {
        this.modelRender.render(pGl, this.model);
    }

}
