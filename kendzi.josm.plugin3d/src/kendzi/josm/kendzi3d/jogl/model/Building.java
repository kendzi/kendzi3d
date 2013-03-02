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
import kendzi.josm.kendzi3d.jogl.model.roof.DormerRoof;
import kendzi.josm.kendzi3d.jogl.model.roof.Roof;
import kendzi.josm.kendzi3d.jogl.model.roof.ShapeRoof;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractWayModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.TextureLibraryService;
import kendzi.josm.kendzi3d.service.TextureLibraryService.TextureLibraryKey;
import kendzi.josm.kendzi3d.util.StringUtil;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.point.Vector2dUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Representing building model.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public class Building extends AbstractWayModel {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Building.class);

    /**
     * Default height of building level.
     */
    private static final double BUILDING_LEVEL_HEIGHT = 3.5;


    /**
     * Default height of building.
     */
    private static final double BUILDING_HEIGHT_DEFAULT = 5;


    /**
     * Default number of building levels.
     */
    private static final double BUILDING_LEVELS_DEFAULT = 1;

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

    boolean isCounterClockwise;

    private Roof roof;

    private double buildingHeight;

    /**
     * Height of building.
     */
    private double height;

    /**
     * Min height of building.
     */
    private Double minHeight;

    /**
     * Number of building levels.
     */
    private double levels;

    /**
     * Min number of building levels.
     */
    private double minLevels;

    /**
     * Model of building.
     */
    private Model model;

    /**
     * Height cloner.
     */
    private List<RelationCloneHeight> cloneHeight;

    /**
     * Windows and entrances.
     */
    private List<WindowEntrances> windowEntrances;

    private List<WindowEntrancesModel> windowsEnterencsModels;

    private TextureData facadeTexture;

    /**
     * Constructor for building.
     *
     * @param pWay way describing building
     * @param pPerspective perspective3
     * @param pModelRender model render
     * @param pMetadataCacheService metadata cache service
     * @param pTextureLibraryService texture library service
     */
    public Building(Way pWay, Perspective3D pPerspective,
            ModelRender pModelRender, MetadataCacheService pMetadataCacheService,
            TextureLibraryService pTextureLibraryService) {
        super(pWay, pPerspective);

        this.modelRender = pModelRender;
        this.metadataCacheService = pMetadataCacheService;
        this.textureLibraryService = pTextureLibraryService;

        this.facadeTexture = getFacadeTexture();


        if (0.0f < Triangulate.area(this.points)) {
            this.isCounterClockwise = true;
        }


        //        this.height =
        //        this.minHeight = (double) ModelUtil.getMinHeight(way, null);
        //        this.levels = (double) ModelUtil.getNumberAttribute(way, "building:levels", 1);
        //        this.minlevels = (double) ModelUtil.getNumberAttribute(way, "building:level", 0);

        Double heightAttr = ModelUtil.getHeight(pWay, null);
        Double minHeightAttr = ModelUtil.getMinHeight(pWay, null);
        Double levelsAttr = ModelUtil.getNumberAttribute(pWay, "building:levels", null);
        Double minlevelsAttr = ModelUtil.getNumberAttribute(pWay, "building:min_level", null);

        if (heightAttr == null && levelsAttr != null) {
            heightAttr = levelsAttr * BUILDING_LEVEL_HEIGHT;
        }
        if (minHeightAttr == null && minlevelsAttr != null) {
            minHeightAttr = minlevelsAttr * BUILDING_LEVEL_HEIGHT;
        }

        if (heightAttr == null) {
            heightAttr = BUILDING_HEIGHT_DEFAULT;
        }
        if (minHeightAttr == null) {
            minHeightAttr = 0d;
        }

        if (levelsAttr == null) {
            levelsAttr = BUILDING_LEVELS_DEFAULT;
        }
        if (minlevelsAttr == null) {
            minlevelsAttr = 0d;
        }

        this.height = heightAttr;
        this.minHeight = minHeightAttr;
        this.levels = levelsAttr;
        this.minLevels = minlevelsAttr;


        String tag3dr = pWay.get("3dr:type");
        if (!StringUtil.isBlankOrNull(tag3dr)) {
            this.roof = new DormerRoof(this, this.facadeTexture, pWay, pPerspective, pModelRender, pMetadataCacheService, pTextureLibraryService);
        } else {
            this.roof = new ShapeRoof(this, this.facadeTexture, pWay, pPerspective, pModelRender, pMetadataCacheService, pTextureLibraryService);
        }


    }

    /** Gets facade texture.
     * @return facade texture
     */
    public TextureData getFacadeTexture() {

        String facadeMaterial = this.way.get("building:material");
        if (StringUtil.isBlankOrNull(facadeMaterial)) {
            facadeMaterial = this.way.get("building:facade:material");
        }
        if (StringUtil.isBlankOrNull(facadeMaterial)) {
            facadeMaterial = this.way.get("facade:material");
        }

        String facadeColor = this.way.get("building:facade:color");
        if (StringUtil.isBlankOrNull(facadeColor)) {
            facadeColor = this.way.get("building:facade:colour");
        }
        if (StringUtil.isBlankOrNull(facadeColor)) {
            facadeColor = this.way.get("building:color");
        }
        if (StringUtil.isBlankOrNull(facadeColor)) {
            facadeColor = this.way.get("building:colour");
        }

        if (!StringUtil.isBlankOrNull(facadeMaterial) || StringUtil.isBlankOrNull(facadeColor)) {

            String textureKey = this.textureLibraryService.getKey(TextureLibraryKey.BUILDING_FACADE, facadeMaterial);
            return this.textureLibraryService.getTextureDefault(textureKey);

//            String facadeTextureFile = metadataCacheService.getPropertites(
//                    "buildings.building_facade_material_{0}.texture.file", null, facadeMaterial);
//
//            double facadeTextureLenght = metadataCacheService.getPropertitesDouble(
//                    "buildings.building_facade_material_{0}.texture.lenght", 1d, facadeMaterial);
//            double facadeTextureHeight = metadataCacheService.getPropertitesDouble(
//                    "buildings.building_facade_material_{0}.texture.height", 1d, facadeMaterial);
//
//            return new TextureData(facadeTextureFile, facadeTextureLenght, facadeTextureHeight);

        } else {
            //XXX this will be change in future ...

            String facadeColorFile = "#c=" + facadeColor;
//            String facadeColorFile = MetadataCacheService.getPropertites(
//                    "buildings.building_facade_color_{0}.texture.file", null, facadeColor);
//
//            if (StringUtil.isBlankOrNull(facadeColorFile)) {
//                facadeColorFile = MetadataCacheService.getPropertites(
//                        "buildings.building_facade_color_unknown.texture.file", null);
//            }
//
//            double facadeColorLenght = MetadataCacheService.getPropertitesDouble(
//                    "buildings.building_facade_color_{0}.texture.lenght", 1d, facadeColor);
//            double facadeColorHeight = MetadataCacheService.getPropertitesDouble(
//                    "buildings.building_facade_color_{0}.texture.height", 1d, facadeColor);
//
//            return new TextureData(facadeColorFile, facadeColorLenght, facadeColorHeight);
            return new TextureData(facadeColorFile, 1d, 1d);

        }
    }


    @Override
    public void buildModel() {

        this.windowEntrances = findWindowsEnterencs();

        this.windowsEnterencsModels = buildWindowsEnterencs(this.windowEntrances);



        Vector3d [] normals = new Vector3d[this.points.size()];

        if (this.points.size() > 0) {

            Point2d beginPoint = this.points.get(0);
            for (int i = 1; i < this.points.size(); i++) {

                // Point2d start = border.get(i);
                // Point2d stop = border.get((i + 1) % size);

                Point2d endPoint = this.points.get(i);

//                Vector3d norm = Normal.calcNormalNorm2(
//                        beginPoint.x, 0.0f, beginPoint.y,
//                        endPoint.x, 0.0f, endPoint.y,
//                        beginPoint.x, 1.0, beginPoint.y);


                Vector3d norm = new Vector3d(
                        -(endPoint.y - beginPoint.y), 0, -(endPoint.x - beginPoint.x));
                norm.normalize();


//                int size = border.size();
//                Vector3d[] ret = new Vector3d[size];
//                for (int i = 0; i < border.size(); i++) {
//                    Point2d start = border.get(i);
//                    Point2d stop = border.get((i + 1) % size);
//
//                    Vector3d n = new Vector3d((stop.y - start.y), 0, -(stop.x - start.x));
//                    n.normalize();
//
//                    ret[i] = n;
//
//                }
//                return ret;



                if (this.isCounterClockwise) {
                    norm.negate();
                }
                normals[i - 1] = norm;

                beginPoint = endPoint;
            }
        }

        this.roof.buildModel();

        this.buildingHeight = this.roof.getMinHeight();


        Material facadeMaterial = MaterialFactory.createTextureMaterial(this.facadeTexture.getTex0());


        ModelFactory modelBuilder = ModelFactory.modelBuilder();

        int mat = modelBuilder.addMaterial(facadeMaterial);


        MeshFactory meshWalls = modelBuilder.addMesh("walls");

        meshWalls.materialID = mat;
        meshWalls.hasTexture = true;

        for (Vector3d n : normals) {
            meshWalls.addNormal(n);
        }

        if (this.points.size() > 0) {

            double vEnd = (int) (this.buildingHeight / this.facadeTexture.getHeight());

            Point2d beginPoint = this.points.get(0);

            for (int i = 1; i < this.points.size(); i++) {

                Point2d endPoint = this.points.get(i);

                int n = meshWalls.addNormal(normals[i - 1]);

                double distance = beginPoint.distance(endPoint);
                double uEnd = (int) (distance / this.facadeTexture.getWidth());

                int tc1 = meshWalls.addTextCoord(new TextCoord(0, 0));
                int tc2 = meshWalls.addTextCoord(new TextCoord(0, vEnd));
                int tc3 = meshWalls.addTextCoord(new TextCoord(uEnd, vEnd));
                int tc4 = meshWalls.addTextCoord(new TextCoord(uEnd, 0));

                int w1 = meshWalls.addVertex(new Point3d(beginPoint.x, this.minHeight, -beginPoint.y));
                int w2 = meshWalls.addVertex(new Point3d(beginPoint.x, this.buildingHeight, -beginPoint.y));
                int w3 = meshWalls.addVertex(new Point3d(endPoint.x, this.buildingHeight, -endPoint.y));
                int w4 = meshWalls.addVertex(new Point3d(endPoint.x, this.minHeight, -endPoint.y));


                FaceFactory face = meshWalls.addFace(FaceType.QUADS);
                face.addVert(w1, tc1, n);
                face.addVert(w2, tc2, n);
                face.addVert(w3, tc3, n);
                face.addVert(w4, tc4, n);

                beginPoint = endPoint;
            }
        }

        this.model = modelBuilder.toModel();
        this.model.setUseLight(true);
        this.model.setUseTexture(true);

        this.buildModel = true;

        this.cloneHeight = RelationCloneHeight.buildHeightClone(this.way);
    }

    private  List<WindowEntrances> findWindowsEnterencs() {

        List<WindowEntrances> windowEntrances = new ArrayList<WindowEntrances>();


        for (int i = 0; i < this.way.getNodesCount(); i++) {
            Node node = this.way.getNode(i);
            if ("entrance".equals(node.get("building"))) {
                Entrances entrance = new Entrances();

                Point2d p = this.points.get(i);
                Vector2d direction = findWindowDirection(this.points, i);

                entrance.setPoint(p);
                entrance.setDirection(direction);
                entrance.setCloneHeight(RelationCloneHeight.buildHeightClone(node));

                entrance.setHeight(ModelUtil.getHeight(node,  entrance.getHeight()));
                entrance.setMinHeight(ModelUtil.getMinHeight(node,  entrance.getMinHeight()));

                windowEntrances.add(entrance);
            }

            if ("window".equals(node.get("building"))) {
                Window entrance = new Window();
                Point2d p = this.points.get(i);
                Vector2d direction = findWindowDirection(this.points, i);


                entrance.setPoint(p);
                entrance.setDirection(direction);
                entrance.setCloneHeight(RelationCloneHeight.buildHeightClone(node));

                entrance.setHeight(ModelUtil.getHeight(node,  entrance.getHeight()));
                entrance.setMinHeight(ModelUtil.getMinHeight(node,  entrance.getMinHeight()));

                entrance.setWidth(ModelUtil.parseHeight(node.get("width"),  entrance.getWidth()));

                windowEntrances.add(entrance);
            }

        }

        return windowEntrances;
    }

    /**
     * Only Mock for windows. They are build as boxes on top of building.
     * In future windows should be cut out from building model!
     *
     *  This method should change in future!
     *
     * @param pWindowEntrancesList
     *
     * @return
     */
    private  List<WindowEntrancesModel> buildWindowsEnterencs(List<WindowEntrances> pWindowEntrancesList) {

        List<WindowEntrancesModel> ret = new ArrayList<WindowEntrancesModel>();
        for (WindowEntrances we : pWindowEntrancesList) {

            ModelFactory modelBuilder = ModelFactory.modelBuilder();
            MeshFactory meshBorder = modelBuilder.addMesh("fence_border");

            TextureData facadeTexture = getWindowsTexture(this.way);
            Material fenceMaterial = MaterialFactory.createTextureMaterial(facadeTexture.getTex0());

            int facadeMaterialIndex = modelBuilder.addMaterial(fenceMaterial);

            meshBorder.materialID = facadeMaterialIndex;
            meshBorder.hasTexture = true;



            buildBoxModel(we.getPoint(), we.getDirection(), we.getHeight(), we.getMinHeight(),
                    we.getWidth(), 0.2d, meshBorder, facadeTexture);


            Model model = modelBuilder.toModel();
            model.setUseLight(true);
            model.setUseTexture(true);

            WindowEntrancesModel wem = new WindowEntrancesModel();
            wem.setModel(model);
            wem.setCloneHeight(we.getCloneHeight());


            ret.add(wem);
        }
        return ret;



    }

    private TextureData getWindowsTexture(Way way2) {
        // TODO
//        TextureData facadeTexture = new  TextureData("#c=#303030", 1d, 1d);
//        TextureData facadeTexture = new  TextureData("#c=#303030", 1d, 1d);
//        return facadeTexture;

        String textureKey = this.textureLibraryService.getKey(TextureLibraryKey.BUILDING_WINDOW);
        return this.textureLibraryService.getTextureDefault(textureKey);
    }

    private void buildBoxModel(Point2d point, Vector2d direction,
            double height2, double minHeight2, double width, double depth,
            MeshFactory pMeshBorder, TextureData facadeTexture) {

        Vector2d widthVector = new Vector2d(-direction.y, direction.x);
        widthVector.scale(width);

        Vector2d depthVector = new Vector2d(direction);
        depthVector.scale(depth);


        // left down front
        Point3d ldf = new Point3d(
                point.x - widthVector.x + depthVector.x, minHeight2, -(point.y - widthVector.y + depthVector.y));
        // right down front
        Point3d rdf = new Point3d(
                point.x + widthVector.x + depthVector.x, minHeight2, -(point.y + widthVector.y + depthVector.y));
        // left up front
        Point3d luf = new Point3d(
                point.x - widthVector.x + depthVector.x, height2, -(point.y - widthVector.y + depthVector.y));
        // right up front
        Point3d ruf = new Point3d(
                point.x + widthVector.x + depthVector.x, height2, -(point.y + widthVector.y + depthVector.y));

        // left down back
        Point3d ldb = new Point3d(
                point.x - widthVector.x - depthVector.x, minHeight2, -(point.y - widthVector.y - depthVector.y));
        // right down back
        Point3d rdb = new Point3d(
                point.x + widthVector.x - depthVector.x, minHeight2, -(point.y + widthVector.y - depthVector.y));
        // left up back
        Point3d lub = new Point3d(
                point.x - widthVector.x - depthVector.x, height2, -(point.y - widthVector.y - depthVector.y));
        // right up back
        Point3d rub = new Point3d(
                point.x + widthVector.x - depthVector.x, height2, -(point.y + widthVector.y - depthVector.y));


        int ldfi = pMeshBorder.addVertex(ldf);
        int rdfi = pMeshBorder.addVertex(rdf);
        int lufi = pMeshBorder.addVertex(luf);
        int rufi = pMeshBorder.addVertex(ruf);
        int ldbi = pMeshBorder.addVertex(ldb);
        int rdbi = pMeshBorder.addVertex(rdb);
        int lubi = pMeshBorder.addVertex(lub);
        int rubi = pMeshBorder.addVertex(rub);

        // front normal
        Vector3d fn = new Vector3d(direction.x, 0, -direction.y);
        // back normal
        Vector3d bn = new Vector3d(-direction.x, 0, direction.y);

        // left normal
        Vector3d ln = new Vector3d(direction.y, 0, -direction.x);
        // right normal
        Vector3d rn = new Vector3d( -direction.y, 0, direction.x);

        // down normal
        Vector3d dn = new Vector3d(0, -1d, 0);
        // up normal
        Vector3d un = new Vector3d(0, 1d, 0);

        int fni = pMeshBorder.addNormal(fn);
        int bni = pMeshBorder.addNormal(bn);
        int lni = pMeshBorder.addNormal(ln);
        int rni = pMeshBorder.addNormal(rn);
        int dni = pMeshBorder.addNormal(dn);
        int uni = pMeshBorder.addNormal(un);

        // left down
        TextCoord ld = new TextCoord(0d, 0d);
        TextCoord lu = new TextCoord(0d, 1d);
        TextCoord rd = new TextCoord(1d, 0d);
        TextCoord ru = new TextCoord(1d, 1d);

        int ldi = pMeshBorder.addTextCoord(ld);
        int lui = pMeshBorder.addTextCoord(lu);
        int rdi = pMeshBorder.addTextCoord(rd);
        int rui = pMeshBorder.addTextCoord(ru);


        FaceFactory faceFront = pMeshBorder.addFace(FaceType.QUADS);
        faceFront.addVert(ldfi, ldi, fni);
        faceFront.addVert(rdfi, rdi, fni);
        faceFront.addVert(rufi, rui, fni);
        faceFront.addVert(lufi, lui, fni);

        FaceFactory faceBack = pMeshBorder.addFace(FaceType.QUADS);
        faceBack.addVert(ldbi, ldi, bni);
        faceBack.addVert(rdbi, rdi, bni);
        faceBack.addVert(rubi, rui, bni);
        faceBack.addVert(lubi, lui, bni);

        FaceFactory faceLeft = pMeshBorder.addFace(FaceType.QUADS);
        faceLeft.addVert(ldbi, ldi, lni);
        faceLeft.addVert(ldfi, rdi, lni);
        faceLeft.addVert(lufi, rui, lni);
        faceLeft.addVert(lubi, lui, lni);

        FaceFactory faceRight = pMeshBorder.addFace(FaceType.QUADS);
        faceRight.addVert(rdfi, ldi, rni);
        faceRight.addVert(rdbi, rdi, rni);
        faceRight.addVert(rubi, rui, rni);
        faceRight.addVert(rufi, lui, rni);

        FaceFactory faceUp = pMeshBorder.addFace(FaceType.QUADS);
        faceUp.addVert(lufi, ldi, uni);
        faceUp.addVert(rufi, rdi, uni);
        faceUp.addVert(rubi, rui, uni);
        faceUp.addVert(lubi, lui, uni);

        FaceFactory faceDown = pMeshBorder.addFace(FaceType.QUADS);
        faceDown.addVert(ldbi, ldi, dni);
        faceDown.addVert(rdbi, rdi, dni);
        faceDown.addVert(rdfi, rui, dni);
        faceDown.addVert(ldfi, lui, dni);







    }

    /**
     * Only Mock for windows. They are build as boxes on top of building.
     * In future windows should be cut out from building model!
     *
     * @author Tomasz Kędziora (Kendzi)
     */
    class WindowEntrancesModel {
        /**
         * Model.
         */
        Model model;

        /**
         * Height cloner.
         */
        private List<RelationCloneHeight> cloneHeight;

        /**
         * @return the model
         */
        public Model getModel() {
            return this.model;
        }

        /**
         * @param model the model to set
         */
        public void setModel(Model model) {
            this.model = model;
        }

        /**
         * @return the cloneHeight
         */
        public List<RelationCloneHeight> getCloneHeight() {
            return this.cloneHeight;
        }

        /**
         * @param cloneHeight the cloneHeight to set
         */
        public void setCloneHeight(List<RelationCloneHeight> cloneHeight) {
            this.cloneHeight = cloneHeight;
        }

    }


    private Vector2d findWindowDirection(List<Point2d> list, int i) {

        Point2d p = list.get(i);
        Point2d pm1 = list.get((i + 1) % list.size());
        Point2d pp1 = list.get((i - 1 + list.size()) % list.size());

        Vector2d v1 = new Vector2d(p);
        v1.sub(pm1);
        Vector2d v2 = new Vector2d(pp1);
        v2.sub(p);

        Vector2d bisector = Vector2dUtil.bisector(v1, v2);
        bisector.normalize();

        return bisector;
    }

    class Window extends WindowEntrances {
        public Window() {
            super();
            this.minHeight = 1;

            double height = 1.5;
            this.height = this.minHeight + height;

            this.width = 1.8;
            //this.
        }

    }

    class Entrances extends WindowEntrances {

    }

    class WindowEntrances {
        Point2d point;

        double minHeight = 0;

        double height = 2;

        double width = 1d;

        Vector2d direction;

        /**
         * Height cloner.
         */
        private List<RelationCloneHeight> cloneHeight;

        /**
         * @return the point
         */
        public Point2d getPoint() {
            return this.point;
        }
        /**
         * @param point the point to set
         */
        public void setPoint(Point2d point) {
            this.point = point;
        }
        /**
         * @return the cloneHeight
         */
        public List<RelationCloneHeight> getCloneHeight() {
            return this.cloneHeight;
        }
        /**
         * @param cloneHeight the cloneHeight to set
         */
        public void setCloneHeight(List<RelationCloneHeight> cloneHeight) {
            this.cloneHeight = cloneHeight;
        }
        /**
         * @return the minHeight
         */
        public double getMinHeight() {
            return this.minHeight;
        }
        /**
         * @param minHeight the minHeight to set
         */
        public void setMinHeight(double minHeight) {
            this.minHeight = minHeight;
        }
        /**
         * @return the height
         */
        public double getHeight() {
            return this.height;
        }
        /**
         * @param height the height to set
         */
        public void setHeight(double height) {
            this.height = height;
        }
        /**
         * @return the direction
         */
        public Vector2d getDirection() {
            return this.direction;
        }
        /**
         * @param direction the direction to set
         */
        public void setDirection(Vector2d direction) {
            this.direction = direction;
        }
        /**
         * @return the width
         */
        public double getWidth() {
            return this.width;
        }
        /**
         * @param width the width to set
         */
        public void setWidth(double width) {
            this.width = width;
        }
    }



    @Override
    public void draw(GL2 pGl, Camera pCamera) {

        pGl.glPushMatrix();

        pGl.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());


        pGl.glColor3f((float) 188 / 255, (float) 169 / 255, (float) 169 / 255);

        this.modelRender.render(pGl, this.model);

        this.roof.draw(pGl, pCamera);


        for (RelationCloneHeight cloner : this.cloneHeight) {
            for (Double height : cloner) {

                pGl.glPushMatrix();
                pGl.glTranslated(0, height, 0);

                this.modelRender.render(pGl, this.model);

                this.roof.draw(pGl, pCamera);

                pGl.glPopMatrix();

            }
        }

        for (WindowEntrancesModel wem : this.windowsEnterencsModels) {

            List<RelationCloneHeight> cloneHeight2 = wem.getCloneHeight();


            this.modelRender.render(pGl, wem.getModel());

            for (RelationCloneHeight cloner : cloneHeight2) {
                for (Double height : cloner) {

                    pGl.glPushMatrix();
                    pGl.glTranslated(0, height, 0);

                    this.modelRender.render(pGl, wem.getModel());

                    pGl.glPopMatrix();

                }
            }
        }

        pGl.glPopMatrix();
    }


    /** Building height.
     * @return height
     */
    public double getHeight() {
        return this.height;
    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (this.model == null) {
            buildModel();
        }

        return Collections.singletonList(new ExportItem(this.model, new Point3d(this.getGlobalX(), 0, -this.getGlobalY()), new Vector3d(1,1,1)));
    }
}
