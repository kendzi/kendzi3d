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
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.roof.DormerRoof;
import kendzi.josm.kendzi3d.jogl.model.roof.Roof;
import kendzi.josm.kendzi3d.jogl.model.roof.ShapeRoof;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.math.geometry.Triangulate;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Representing building model.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public class Building extends AbstractModel {

    /** Log. */
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



    List<Point2d> list = new ArrayList<Point2d>();

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
     * Renderer of model.
     */
    private ModelRender modelRender;


    /**
     * Way.
     */
    private Way way;

    /** Constructor for building.
     * @param pWay way describing building
     * @param pPerspective perspective3
     */
    public Building(Way pWay, Perspective3D pPerspective) {
        super(pWay, pPerspective);
        this.way = pWay;

        this.list = new ArrayList<Point2d>();

        for (int i = 0; i < pWay.getNodesCount(); i++) {
            Node node = pWay.getNode(i);

            double x = pPerspective.calcX(node.getEastNorth().getX());
            double y = pPerspective.calcY(node.getEastNorth().getY());

            this.list.add(new Point2d(x, y));
            //log.info("d x: " + x + " y: " + y);
        }

        if (0.0f < Triangulate.area(this.list)) {
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
//
//        String shape = pWay.get("building:roof:shape");
//        if ("flat".equals(shape)) {
//            this.roof = new FlatRoof(this, this.list, pWay, pPerspective);
//        } else if ("pitched".equals(shape)) {
//            this.roof = new GableRoof(this, this.list, pWay, pPerspective, true);
//        } else if ("gable".equals(shape)) {
//            this.roof = new GableRoof(this, this.list, pWay, pPerspective, false);
//        } else if ("hip".equals(shape)) {
//            this.roof = new HipRoof(this, this.list, pWay, pPerspective);
//        } else if ("3dr".equals(shape)) {
//            this.roof = new DormerRoof(this, this.list, pWay, pPerspective);
//        } else {
//            this.roof = new FlatRoof(this, this.list, pWay, pPerspective);
//        }

        String tag3dr = pWay.get("3dr:type");
        if (!isBlankOrNull(tag3dr)) {
            this.roof = new DormerRoof(this, this.list, pWay, pPerspective);
        } else {
            this.roof = new ShapeRoof(this, this.list, pWay, pPerspective);
        }

        this.modelRender = ModelRender.getInstance();
    }

    public TextureData getFacadeTexture() {

        String facadeMaterial = this.way.get("building:facade:material");
        String facadeColor = this.way.get("building:facade:color");
        if (isBlankOrNull(facadeColor)) {
            facadeColor = this.way.get("building:color");
        }

        if (!isBlankOrNull(facadeMaterial) || isBlankOrNull(facadeColor)) {

            String facadeTextureFile = MetadataCacheService.getPropertites(
                    "buildings.building_facade_material_{0}.texture.file", null, facadeMaterial);

            double facadeTextureLenght = MetadataCacheService.getPropertitesDouble(
                    "buildings.building_facade_material_{0}.texture.lenght", 1d, facadeMaterial);
            double facadeTextureHeight = MetadataCacheService.getPropertitesDouble(
                    "buildings.building_facade_material_{0}.texture.height", 1d, facadeMaterial);

            return new TextureData(facadeTextureFile, facadeTextureLenght, facadeTextureHeight);

        } else {

            String facadeColorFile = "#c=" + facadeColor;
//            String facadeColorFile = MetadataCacheService.getPropertites(
//                    "buildings.building_facade_color_{0}.texture.file", null, facadeColor);
//
//            if (isBlankOrNull(facadeColorFile)) {
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

    public static boolean isBlankOrNull(String pString) {
        if (pString == null) {
            return true;
        }

        if (!"".equals(pString.trim())) {
            return false;
        }

        return true;
    }



    @Override
    public void buildModel() {

        TextureData facadeTexture = getFacadeTexture();
        Material facadeMaterial =  MaterialFactory.createTextureMaterial(facadeTexture.getFile());


        Vector3d [] normals = new Vector3d[this.list.size()];

        if (this.list.size() > 0) {

            Point2d beginPoint = this.list.get(0);
            for (int i = 1; i < this.list.size(); i++) {

                // Point2d start = border.get(i);
                // Point2d stop = border.get((i + 1) % size);

                Point2d endPoint = this.list.get(i);

//                Vector3d norm = Normal.calcNormalNorm2(
//                        beginPoint.getX(), 0.0f, beginPoint.getY(),
//                        endPoint.getX(), 0.0f, endPoint.getY(),
//                        beginPoint.getX(), 1.0, beginPoint.getY());


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

        this.roof.setWallNormals(normals);
        this.roof.buildModel();

        //        this.buildingHeight = this.height - this.roof.getRoofHeight();
        this.buildingHeight = this.roof.getMinHeight();


        ModelFactory modelBuilder = ModelFactory.modelBuilder();

        int mat = modelBuilder.addMaterial(facadeMaterial);


        MeshFactory meshWalls = modelBuilder.addMesh("walls");

        meshWalls.materialID = mat;
        meshWalls.hasTexture = true;

        for (Vector3d n : normals) {
            meshWalls.addNormal(n);
        }

        if (this.list.size() > 0) {

            double vEnd = (int) (this.buildingHeight / facadeTexture.getHeight());

            Point2d beginPoint = this.list.get(0);

            for (int i = 1; i < this.list.size(); i++) {

                Point2d endPoint = this.list.get(i);

                int n = meshWalls.addNormal(normals[i - 1]);

                double distance = beginPoint.distance(endPoint);
                double uEnd = (int) (distance / facadeTexture.getLenght());

                int tc1 = meshWalls.addTextCoord(new TextCoord(0, 0));
                int tc2 = meshWalls.addTextCoord(new TextCoord(0, vEnd));
                int tc3 = meshWalls.addTextCoord(new TextCoord(uEnd, vEnd));
                int tc4 = meshWalls.addTextCoord(new TextCoord(uEnd, 0));

                int w1 = meshWalls.addVertex(new Point3d(beginPoint.getX(), this.minHeight, -beginPoint.getY()));
                int w2 = meshWalls.addVertex(new Point3d(beginPoint.getX(), this.buildingHeight, -beginPoint.getY()));
                int w3 = meshWalls.addVertex(new Point3d(endPoint.getX(), this.buildingHeight, -endPoint.getY()));
                int w4 = meshWalls.addVertex(new Point3d(endPoint.getX(), this.minHeight, -endPoint.getY()));


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
    }

    @Override
    public void draw(GL2 pGl, Camera pCamera) {

        pGl.glColor3f((float) 188 / 255, (float) 169 / 255, (float) 169 / 255);

        this.modelRender.render(pGl, this.model);

        this.roof.draw(pGl, pCamera);
    }


    public double getHeight() {
        return this.height;
    }
}
