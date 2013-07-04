/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.clone.RelationCloneHeight;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractWayModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.TextureLibraryService;
import kendzi.math.geometry.Bool.CSG;
import kendzi.math.geometry.Bool.CSG.Polygon;
import kendzi.math.geometry.Bool.CSG.Vector;
import kendzi.math.geometry.Bool.CSG.Vertex;
import kendzi.math.geometry.line.LineLinear2d;
import kendzi.math.geometry.line.LineParametric2d;
import kendzi.math.geometry.point.Vector2dUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Fence for shapes defined as way.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class Wall extends AbstractWayModel {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Wall.class);

    private static final java.lang.Double FENCE_HEIGHT = 1d;

    /**
     * Hight.
     */
    private double hight;

    /**
     * Min height.
     */
    private double minHeight;

    /**
     * Model of building.
     */
    private Model model;

    /**
     * Height cloner.
     */
    private List<RelationCloneHeight> heightClone;

    /**
     * Renderer of model.
     */
    private ModelRender modelRender;

    /**
     * Metadata cache service.
     */
    private MetadataCacheService metadataCacheService;

    /**
     * Texture library service.
     */
    private TextureLibraryService textureLibraryService;

    /**
     * Fence constructor.
     *
     * @param pWay way
     * @param pPerspective3D perspective
     * @param pModelRender model render
     * @param pMetadataCacheService metadata cache service
     */
    public Wall(Way pWay, Perspective3D pPerspective3D,
            ModelRender pModelRender, MetadataCacheService pMetadataCacheService,
            TextureLibraryService pTextureLibraryService) {

        super(pWay, pPerspective3D);

        this.modelRender = pModelRender;
        this.metadataCacheService = pMetadataCacheService;
        this.textureLibraryService = pTextureLibraryService;
    }


    @Override
    public void buildModel() {

        if (!(this.points.size() > 1)) {
            return;
        }

        String fenceType = FenceRelation.getFenceType(this.way);

        double fenceHeight = this.metadataCacheService.getPropertitesDouble(
                "barrier.fence_{0}.height", FENCE_HEIGHT, fenceType);

        this.hight = ModelUtil.getHeight(this.way, fenceHeight);

        this.minHeight = ModelUtil.getMinHeight(this.way, 0d);


        ModelFactory modelBuilder = ModelFactory.modelBuilder();
        MeshFactory meshBorder = modelBuilder.addMesh("fence_border");
        MeshFactory testMesh = modelBuilder.addMesh("test");
        //MeshFactory meshBorder = modelBuilder.addMesh("box");

        TextureData facadeTexture = FenceRelation.getFenceTexture(fenceType, this.way, this.textureLibraryService);
        Material fenceMaterial = MaterialFactory.createTextureMaterial(facadeTexture.getTex0());

        int facadeMaterialIndex = modelBuilder.addMaterial(fenceMaterial);

        meshBorder.materialID = facadeMaterialIndex;
        meshBorder.hasTexture = true;


//        FenceRelation.buildWallModel(this.points, null, this.minHeight, this.hight, 0, meshBorder, facadeTexture);





//        this.heightClone = RelationCloneHeight.buildHeightClone(this.way);
     //   CSG cube = CSG.cube();
//        CSG sphere2 = CSG.sphere(null, 1.2d, null, null);
//        CSG sphere = CSG.cylinder(null, null, null, 4d);
        MeshFactory cubeMesh2 = MeshFactoryUtil.cubeMesh(new Point3d(), new Vector3d(2d, 2d, 2d));
        CSG sphere = meshToSolid(cubeMesh2);
        MeshFactory cubeMesh3 = MeshFactoryUtil.cubeMesh(new Point3d(1,1,1), new Vector3d(2d, 2d, 2d));
        CSG sphere2  = meshToSolid(cubeMesh3);

//        toModel(testMesh, sphere.toPolygons());
//        toModel(testMesh, sphere2.toPolygons());


//      var polygons = cube.subtract(sphere).toPolygons();
        ArrayList<Polygon> polygons = sphere.subtract(sphere2).toPolygons();
        solidToModel(testMesh, polygons);

        this.way.getNodes();

        double height = ModelUtil.getHeight(this.way, 2d);
        double min_height = ModelUtil.getHeight(this.way, 0d);

        double width = ModelUtil.parseHeight(this.way.get("width"), 1d);

        List<WallHole> holeList = new ArrayList<Wall.WallHole>();

        for (int i = 0; i < this.way.getNodes().size(); i++) {
            Node node = this.way.getNode(i);

            if ("box".equals(node.get("hole"))) {
                WallHole wh = new WallHole();
                wh.setWallHoleType(WallHoleType.box);
                wh.setHeight(ModelUtil.getHeight(node, 2d));
                wh.setWidth(ModelUtil.parseHeight(node.get("width"), 1d));
                wh.setDepth(width * 2d);
                wh.setX(this.points.get(i).x);
                wh.setY(this.points.get(i).y);

                holeList.add(wh);
            }

        }


        meshBorder = buildWallModelWithHoles(this.points, holeList, min_height, height, width);
        meshBorder.materialID = facadeMaterialIndex;
        meshBorder.hasTexture = true;

        modelBuilder.addMesh(meshBorder);


        if (this.points.size() > 0) {
            Point2d start = this.points.get(0);
            MeshFactory cubeMesh = MeshFactoryUtil.cubeMesh(new Point3d(
                    start.x, 3, -start.y));

            modelBuilder.addMesh(cubeMesh);
        }

        this.model = modelBuilder.toModel();
        this.model.setUseLight(true);
        this.model.setUseTexture(true);

        this.buildModel = true;
    }

    enum WallHoleType {
        box;
    };

    class WallHole {
        WallHoleType wallHoleType;
        double height;
        double width;
        double depth;
        double x;
        double y;
        /**
         * @return the wallHoleType
         */
        public WallHoleType getWallHoleType() {
            return wallHoleType;
        }
        /**
         * @param wallHoleType the wallHoleType to set
         */
        public void setWallHoleType(WallHoleType wallHoleType) {
            this.wallHoleType = wallHoleType;
        }
        /**
         * @return the height
         */
        public double getHeight() {
            return height;
        }
        /**
         * @param height the height to set
         */
        public void setHeight(double height) {
            this.height = height;
        }
        /**
         * @return the width
         */
        public double getWidth() {
            return width;
        }
        /**
         * @param width the width to set
         */
        public void setWidth(double width) {
            this.width = width;
        }
        /**
         * @return the depth
         */
        public double getDepth() {
            return depth;
        }
        /**
         * @param depth the depth to set
         */
        public void setDepth(double depth) {
            this.depth = depth;
        }
        /**
         * @return the x
         */
        public double getX() {
            return x;
        }
        /**
         * @param x the x to set
         */
        public void setX(double x) {
            this.x = x;
        }
        /**
         * @return the y
         */
        public double getY() {
            return y;
        }
        /**
         * @param y the y to set
         */
        public void setY(double y) {
            this.y = y;
        }

    }
    private MeshFactory buildWallModelWithHoles(
            List<Point2d> points, List<WallHole> holeList,
            double min_height, double height, double width
            ) {

        MeshFactory wallMesh = new MeshFactory();

        buildWallModel(this.points, width, wallMesh);

        List<MeshFactory> holes = new ArrayList<MeshFactory>();
        for (WallHole wh : holeList) {
            MeshFactory cubeMesh = MeshFactoryUtil.cubeMesh(new Point3d(wh.x, wh.height, -wh.y));
            holes.add(cubeMesh);
        }

        return applayHolesToModel(wallMesh, holes);

//        for (MeshFactory mf : holes) {
//
//        }

    }


    private MeshFactory applayHolesToModel(MeshFactory solidModel, List<MeshFactory> holesModel) {
        CSG solid = meshToSolid(solidModel);

        for (MeshFactory holeModel : holesModel) {
            CSG hole = meshToSolid(holeModel);
            solid = solid.subtract(hole);
        }

        ArrayList<Polygon> polygons = solid.toPolygons();

        MeshFactory mf = new MeshFactory();
        solidToModel(mf, polygons);

        return mf;
//        for (MeshFactory holeModel : holesModel) {
//            ArrayList<Polygon> hole = meshToSolid(holeModel).toPolygons();
//            solidToModel(solidModel, hole);
//        }
    }





    private void buildWallModel(List<Point2d> points, double width, MeshFactory meshBorder) {

        if (points == null || points.size() < 2) {
            return;
        }

        double min_height = 1;
        double max_height = 3;

        Point2d[] simpleOutLine = simpleOutLine(points, width);

        FaceFactory face = meshBorder.addFace(FaceType.QUADS);

        int niTop = meshBorder.addNormal(new Vector3d(0,1,0));
        int niBottom  = meshBorder.addNormal(new Vector3d(0,-1,0));
        meshBorder.addTextCoord(new TextCoord(0.5,0.5));

        int size = points.size();

        Point2d segmentStart = points.get(0);
        Point2d segmentEnd = points.get(1);
        Vector2d segmentVec = new Vector2d(segmentEnd);
        segmentVec.sub(segmentStart);
        segmentVec.normalize();


        Point2d ps1 = points.get(0);
        Point2d ps2 = simpleOutLine[0];


        int niStart  = meshBorder.addNormal(new Vector3d(-segmentVec.x,0, segmentVec.y));

        int ps1it = meshBorder.addVertex(new Point3d(ps1.x, max_height, -ps1.y));
        int ps2it = meshBorder.addVertex(new Point3d(ps2.x, max_height, -ps2.y));
        int ps1ib = meshBorder.addVertex(new Point3d(ps1.x, min_height, -ps1.y));
        int ps2ib = meshBorder.addVertex(new Point3d(ps2.x, min_height, -ps2.y));


//        int pf1it = meshBorder.addVertex(new Point3d(pe1.x, max_height, pe1.y));
//        int pf2it = meshBorder.addVertex(new Point3d(pe2.x, max_height, pe2.y));
//        int pf1ib = meshBorder.addVertex(new Point3d(pe1.x, min_height, pe1.y));
//        int pf2ib = meshBorder.addVertex(new Point3d(pe2.x, min_height, pe2.y));




        face.addVert(ps1it, 0, niStart);
        face.addVert(ps2it, 0, niStart);
        face.addVert(ps2ib, 0, niStart);
        face.addVert(ps1ib, 0, niStart);


        for (int i = 1; i < size ; i++){

            segmentEnd = points.get(i);
            segmentVec = new Vector2d(segmentEnd);
            segmentVec.sub(segmentStart);
            segmentVec.normalize();

            Point2d pe1 = points.get(i);
            Point2d pe2 = simpleOutLine[i];

            int pe1it = meshBorder.addVertex(new Point3d(pe1.x, max_height, -pe1.y));
            int pe2it = meshBorder.addVertex(new Point3d(pe2.x, max_height, -pe2.y));
            int pe1ib = meshBorder.addVertex(new Point3d(pe1.x, min_height, -pe1.y));
            int pe2ib = meshBorder.addVertex(new Point3d(pe2.x, min_height, -pe2.y));

            int niLeft = meshBorder.addNormal(new Vector3d(-segmentVec.y, 0, -segmentVec.x));
            int niRight = meshBorder.addNormal(new Vector3d(segmentVec.y, 0, segmentVec.x));


            face.addVert(pe1it, 0, niTop);
            face.addVert(pe2it, 0, niTop);
            face.addVert(ps2it, 0, niTop);
            face.addVert(ps1it, 0, niTop);





            face.addVert(ps1ib, 0, niBottom);
            face.addVert(ps2ib, 0, niBottom);
            face.addVert(pe2ib, 0, niBottom);
            face.addVert(pe1ib, 0, niBottom);




            face.addVert(ps1ib, 0, niRight);
            face.addVert(pe1ib, 0, niRight);
            face.addVert(pe1it, 0, niRight);
            face.addVert(ps1it, 0, niRight);


            face.addVert(ps2it, 0, niLeft);
            face.addVert(pe2it, 0, niLeft);
            face.addVert(pe2ib, 0, niLeft);
            face.addVert(ps2ib, 0, niLeft);


            ps1 = pe1;
            ps2 = pe2;

            ps1it = pe1it;
            ps2it = pe2it;
            ps1ib = pe1ib;
            ps2ib = pe2ib;

            segmentStart = segmentEnd;
        }

        int niEnd  = meshBorder.addNormal(new Vector3d(segmentVec.x, 0, -segmentVec.y));

        face.addVert(ps2it, 0, niEnd);
        face.addVert(ps1it, 0, niEnd);
        face.addVert(ps1ib, 0, niEnd);
        face.addVert(ps2ib, 0, niEnd);

//        face.addVert(ps1ib, 0, niBottom);
//        face.addVert(ps2ib, 0, niBottom);
//        face.addVert(ps2it, 0, niBottom);
//        face.addVert(ps1it, 0, niBottom);
    }

    public static void main(String[] args) {

        List<Point2d> points = new ArrayList<Point2d>();
        points.add(new Point2d(0,0));
        points.add(new Point2d(1,0));
        points.add(new Point2d(1,1));
        Point2d[] simpleOutLine = simpleOutLine(points, 2d);

        for (Point2d p : simpleOutLine) {
            System.out.println(p);
        }
    }

    private static Point2d[] simpleOutLine(List<Point2d> points, double width) {

        int size = points.size();

        Point2d [] ret = new Point2d[size];
        if (size < 2) {
            return new Point2d[0];
        }
        {
            Point2d p1 = points.get(0);
            Point2d p2 = points.get(1);

            Vector2d v1 = new Vector2d(p2);
            v1.sub(p1);
            Vector2d n1 = new Vector2d(-v1.y, v1.x);

            n1.normalize();
            n1.scale(width);

            Point2d start = new Point2d(p1);
            start.add(n1);
            ret[0] = start;
            p1 = points.get(size - 2);
            p2 = points.get(size - 1);

            v1 = new Vector2d(p2);
            v1.sub(p1);
            n1 = new Vector2d(-v1.y, v1.x);

            n1.normalize();
            n1.scale(width);

            Point2d end = new Point2d(p2);
            end.add(n1);

            ret[size -1] = end;
        }
        for(int i = 1; i < size-1; i++) {
            int pi1 = i - 1;
            int pi2 = i;
            int pi3 = i + 1;

            Point2d p1 = points.get(pi1);
            Point2d p2 = points.get(pi2);
            Point2d p3 = points.get(pi3);

            Vector2d v1 = new Vector2d(p2);
            v1.sub(p1);

            Vector2d v2 = new Vector2d(p3);
            v2.sub(p2);

            v1.normalize();
            v2.normalize();

            Vector2d n1 = new Vector2d(-v1.y, v1.x);
            Vector2d n2 = new Vector2d(-v2.y, v2.x);


            Vector2d bisectorNormalized = Vector2dUtil.bisectorNormalized(v1, v2);

            n1.scale(width);
            n2.scale(width);

            Point2d p1n = new Point2d(p1);
            p1n.add(n1);
            Point2d p2n = new Point2d(p2);
            p2n.add(n2);

            LineLinear2d l1 = new LineParametric2d(p1n, v1).getLinearForm();
//            LineLinear2d l2 = new LineParametric2d(p2n, v2).getLinearForm();
            LineLinear2d l3 = new LineParametric2d(p2, bisectorNormalized).getLinearForm();


            Point2d collide = l1.collide(l3);

            if (collide == null) {
                ret[i] = p2;
            } else {
                ret[i] = collide;
            }
        }

        return ret;

    }

    private CSG meshToSolid(MeshFactory solidModel) {
        ArrayList<Polygon> polygons = new ArrayList<Polygon>();
        for (FaceFactory ff : solidModel.faceFactory) {

            if (FaceType.QUADS.equals(ff.type)) {
                int size = ff.vertIndex.size();

                for (int j = 0; j < size / 4; j++) {

                    int polygonSize = 4;
                    Vertex [] vertices = new Vertex[polygonSize];
                    for (int c = 0; c < polygonSize; c++) {
                        int index = j * polygonSize + c;
                        int vi = ff.vertIndex.get(index);
                        int ti = ff.coordIndexLayers.get(0).get(index);
                        int ni = ff.normalIndex.get(index);

                        Point3d v = solidModel.vertices.get(vi);
                        TextCoord t = solidModel.textCoords.get(ti);
                        Vector3d n = solidModel.normals.get(ni);

                        Vector point = new Vector(v.x,v.y,v.z);
                        Vector normal = new Vector(n.x,n.y,n.z);

                        Vertex vertex = new Vertex(point, normal);

                        vertices[c] = vertex;

                    }
                    Polygon polygon = new CSG.Polygon(vertices, false);
                    polygons.add(polygon);
                }


            } else {
                throw new RuntimeException("not supported face type: " + ff.type);
            }

        }

        CSG solid = CSG.fromPolygons(polygons);

        return solid;
    }

    /**
     * @param meshBorder
     * @param polygons
     */
    public void solidToModel(MeshFactory meshBorder, ArrayList<Polygon> polygons) {
        for (Polygon polygon : polygons) {
            Vertex[] vertices = polygon.getVertices();

            FaceFactory faceRight = meshBorder.addFace(FaceType.TRIANGLE_FAN);
            meshBorder.addTextCoord(new TextCoord(0.5, 0.5));

            for (Vertex vertex : vertices) {
                kendzi.math.geometry.Bool.CSG.Vector pos = vertex.getPos();
                Vector normal = vertex.getNormal();
                int vi = meshBorder.addVertex(new Point3d(pos.x, pos.y, pos.z));
                int ni = meshBorder.addNormal(new Vector3d(normal.x, normal.y, normal.z));


                faceRight.addVert(vi, 0, ni);
            }
        }
    }



    @Override
    public void draw(GL2 pGl, Camera pCamera) {
//        if (true) {
//            return;
//        }

        // do not draw the transparent parts of the texture
        pGl.glEnable(GL2.GL_BLEND);
        pGl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        // don't show source alpha parts in the destination

        // determine which areas of the polygon are to be rendered
        pGl.glEnable(GL2.GL_ALPHA_TEST);
        pGl.glAlphaFunc(GL2.GL_GREATER, 0); // only render if alpha > 0

        // replace the quad colors with the texture
        //      gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
        pGl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);




        pGl.glEnable(GL2.GL_CULL_FACE);

        pGl.glPushMatrix();
        pGl.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());

        //pGl.glColor3f((float) 188 / 255, (float) 169 / 255, (float) 169 / 255);

        try {
            this.modelRender.render(pGl, this.model);

//            for (RelationCloneHeight cloner : this.heightClone) {
//                for (Double height : cloner) {
//
//                    pGl.glPushMatrix();
//                    pGl.glTranslated(0, height, 0);
//
//                    this.modelRender.render(pGl, this.model);
//                    pGl.glPopMatrix();
//
//                }
//            }

        } finally {

            pGl.glPopMatrix();

            pGl.glDisable(GL2.GL_CULL_FACE);
        }
    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (this.model == null) {
            buildModel();
        }

        return Collections.singletonList(new ExportItem(this.model, new Point3d(this.getGlobalX(), 0, -this.getGlobalY()), new Vector3d(1,1,1)));
    }
}
