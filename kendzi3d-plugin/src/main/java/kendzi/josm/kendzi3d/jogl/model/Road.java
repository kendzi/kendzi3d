/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractWayModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.kendzi3d.josm.model.perspective.Perspective;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Represent road.
 * 
 * This class require lot of clean up!
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
@Deprecated
public class Road extends AbstractWayModel {

    /** Log. */
    private static final Logger log = Logger.getLogger(Road.class);

    /**
     * Default width of road.
     */
    private static final double DEFAULT_ROAD_WIDTH = 6.0f;

    /**
     * Renderer of model.
     */
    private ModelRender modelRender;

    /**
     * Metadata cache service.
     */
    private MetadataCacheService metadataCacheService;

    /**
     * List of road points.
     */
    private List<Point2d> list = new ArrayList<Point2d>();

    /**
     * Width of road.
     */
    private double roadWidth = DEFAULT_ROAD_WIDTH;

    /**
     * Sin of 90.
     */
    private static double cos90 = Math.cos(Math.toRadians(90));

    /**
     * Cos of 90.
     */
    private static double sin90 = Math.sin(Math.toRadians(90));

    /**
     * Model of road.
     */
    private Model model;

    /**
     * Represent road.
     * 
     * @param way
     *            way
     * @param perspective
     *            perspective
     * @param modelRender
     *            model render
     * @param metadataCacheService
     *            metadata cache service
     */
    public Road(Way way, Perspective perspective, ModelRender modelRender, MetadataCacheService metadataCacheService) {
        super(way, perspective);

        this.modelRender = modelRender;
        this.metadataCacheService = metadataCacheService;
    }

    @Override
    public void buildWorldObject() {

        // FIXME object is not in local coordinates!
        setPoint(new Point3d());

        List<Point2d> pointList = new ArrayList<Point2d>();

        for (int i = 0; i < this.way.getNodesCount(); i++) {
            Node node = this.way.getNode(i);
            pointList.add(perspective.calcPoint(node));
        }

        this.list = pointList;

        this.roadWidth = (float) DEFAULT_ROAD_WIDTH;

        this.roadWidth = getRoadWidth();

        TextureData texture = getTexture();

        Material m = MaterialFactory.createTextureMaterial(texture.getFile());

        ModelFactory modelBuilder = ModelFactory.modelBuilder();

        int mi = modelBuilder.addMaterial(m);

        MeshFactory meshWalls = modelBuilder.addMesh("road");

        meshWalls.materialID = mi;
        meshWalls.hasTexture = true;

        if (this.list.size() > 1) {

            FaceFactory leftBorder = meshWalls.addFace(FaceType.QUAD_STRIP);
            FaceFactory leftPart = meshWalls.addFace(FaceType.QUAD_STRIP);
            FaceFactory rightBorder = meshWalls.addFace(FaceType.QUAD_STRIP);
            FaceFactory rightPart = meshWalls.addFace(FaceType.QUAD_STRIP);

            Vector3d flatSurface = new Vector3d(0, 1, 0);

            int flatNormalI = meshWalls.addNormal(flatSurface);

            Point2d beginPoint = this.list.get(0);
            for (int i = 1; i < this.list.size(); i++) {
                Point2d endPoint = this.list.get(i);

                double x = endPoint.x - beginPoint.x;
                double y = endPoint.y - beginPoint.y;
                // calc lenght of road segment
                double mod = Math.sqrt(x * x + y * y);

                double distance = beginPoint.distance(endPoint);

                // calc orthogonal for road segment
                double orthX = x * cos90 + y * sin90;
                double orthY = -x * sin90 + y * cos90;

                // calc vector for road width;
                double normX = this.roadWidth / 2 * orthX / mod;
                double normY = this.roadWidth / 2 * orthY / mod;
                // calc vector for border width;
                double borderX = normX + 0.2 * orthX / mod;
                double borderY = normY + 0.2 * orthY / mod;

                double uEnd = distance / texture.getLenght();

                // left border
                int tcb1 = meshWalls.addTextCoord(new TextCoord(0, 0.99999d));
                // left part of road
                int tcb2 = meshWalls.addTextCoord(new TextCoord(0, 1 - 0.10d));
                // Middle part of road
                int tcb3 = meshWalls.addTextCoord(new TextCoord(0, 0.00001d));
                // right part of road
                int tcb4 = meshWalls.addTextCoord(new TextCoord(0, 1 - 0.10d));
                // right border
                int tcb5 = meshWalls.addTextCoord(new TextCoord(0, 0.99999d));

                // left border
                int tce1 = meshWalls.addTextCoord(new TextCoord(uEnd, 0.99999d));
                // left part of road
                int tce2 = meshWalls.addTextCoord(new TextCoord(uEnd, 1 - 0.10d));
                // Middle part of road
                int tce3 = meshWalls.addTextCoord(new TextCoord(uEnd, 0.00001d));
                // right part of road
                int tce4 = meshWalls.addTextCoord(new TextCoord(uEnd, 1 - 0.10d));
                // right border
                int tce5 = meshWalls.addTextCoord(new TextCoord(uEnd, 0.99999d));

                // left border
                int wbi1 = meshWalls.addVertex(new Point3d(beginPoint.x + borderX, 0.0d, -(beginPoint.y + borderY)));
                // left part of road
                int wbi2 = meshWalls.addVertex(new Point3d(beginPoint.x + normX, 0.1d, -(beginPoint.y + normY)));
                // middle part of road
                int wbi3 = meshWalls.addVertex(new Point3d(beginPoint.x, 0.15d, -beginPoint.y));
                // right part of road
                int wbi4 = meshWalls.addVertex(new Point3d(beginPoint.x - normX, 0.1d, -(beginPoint.y - normY)));
                // right border
                int wbi5 = meshWalls.addVertex(new Point3d(beginPoint.x - borderX, 0.0d, -(beginPoint.y - borderY)));

                // left border
                int wei1 = meshWalls.addVertex(new Point3d(endPoint.x + borderX, 0.0d, -(endPoint.y + borderY)));
                // left part of road
                int wei2 = meshWalls.addVertex(new Point3d(endPoint.x + normX, 0.1d, -(endPoint.y + normY)));
                // middle part of road
                int wei3 = meshWalls.addVertex(new Point3d(endPoint.x, 0.15d, -endPoint.y));
                // right part of road
                int wei4 = meshWalls.addVertex(new Point3d(endPoint.x - normX, 0.1d, -(endPoint.y - normY)));
                // right border
                int wei5 = meshWalls.addVertex(new Point3d(endPoint.x - borderX, 0.0d, -(endPoint.y - borderY)));

                leftBorder.addVert(wbi1, tcb1, flatNormalI);
                leftBorder.addVert(wbi2, tcb2, flatNormalI);
                leftBorder.addVert(wei1, tce1, flatNormalI);
                leftBorder.addVert(wei2, tce2, flatNormalI);

                leftPart.addVert(wbi2, tcb2, flatNormalI);
                leftPart.addVert(wbi3, tcb3, flatNormalI);
                leftPart.addVert(wei2, tce2, flatNormalI);
                leftPart.addVert(wei3, tce3, flatNormalI);

                rightBorder.addVert(wbi3, tcb3, flatNormalI);
                rightBorder.addVert(wbi4, tcb4, flatNormalI);
                rightBorder.addVert(wei3, tce3, flatNormalI);
                rightBorder.addVert(wei4, tce4, flatNormalI);

                rightPart.addVert(wbi4, tcb4, flatNormalI);
                rightPart.addVert(wbi5, tcb5, flatNormalI);
                rightPart.addVert(wei4, tce4, flatNormalI);
                rightPart.addVert(wei5, tce5, flatNormalI);

                beginPoint = endPoint;
            }
        }

        this.model = modelBuilder.toModel();
        this.model.setUseLight(true);
        this.model.setUseTexture(true);

        this.buildModel = true;
    }

    /**
     * Finds texture data.
     * 
     * @return texture data
     */
    private TextureData getTexture() {

        String highway = this.way.get("highway");
        if (highway == null) {
            highway = "unknown";
        }

        String surface = this.way.get("surface");
        if (surface == null) {
            surface = "unknown";
        }

        String file = null;

        String highwayTexture = this.metadataCacheService.getPropertites("roads.highway_" + highway + ".texture.file", null);
        Double highwayTextureLenght = this.metadataCacheService.getPropertitesDouble("roads.highway_" + highway
                + ".texture.lenght", 1d);
        String surfaceTexture = this.metadataCacheService.getPropertites("roads.surface_" + surface + ".texture.file", null);
        Double surfaceTextureLenght = this.metadataCacheService.getPropertitesDouble("roads.surface_" + surface
                + ".texture.lenght", 1d);

        double lenght = 1;
        // finds known texture
        if (!"unknown".equals(surface) && surfaceTexture != null) {
            file = surfaceTexture;
            lenght = surfaceTextureLenght;
        } else if (!"unknown".equals(highway) && highwayTexture != null) {
            file = highwayTexture;
            lenght = highwayTextureLenght;
        }

        if (file == null) {
            // finds unknown texture
            if (surfaceTexture != null) {
                file = surfaceTexture;
                lenght = surfaceTextureLenght;
            } else if (highwayTexture != null) {
                file = highwayTexture;
                lenght = highwayTextureLenght;
            }
        }

        return new TextureData(file, lenght);

    }

    /**
     * Texture data.
     * 
     * @author kendzi
     * 
     */
    private class TextureData {
        String file;
        double lenght;

        private TextureData(String pFile, double pLenght) {
            super();
            this.file = pFile;
            this.lenght = pLenght;
        }

        /**
         * @return the file
         */
        public String getFile() {
            return this.file;
        }

        /**
         * @param pFile
         *            the file to set
         */
        public void setFile(String pFile) {
            this.file = pFile;
        }

        /**
         * @return the lenght
         */
        public double getLenght() {
            return this.lenght;
        }

        /**
         * @param pLenght
         *            the lenght to set
         */
        public void setLenght(double pLenght) {
            this.lenght = pLenght;
        }

    }

    /**
     * Finds road width.
     * 
     * @return road width
     */
    private double getRoadWidth() {

        String highway = this.way.get("highway");
        if (highway == null) {
            highway = "unknown";
        }

        String widthStr = this.way.get("width");
        if (widthStr != null) {
            try {
                return Long.parseLong(widthStr);
            } catch (Exception e) {
                log.error(e, e);
            }
        }

        Double paramWidth = this.metadataCacheService.getPropertitesDouble("roads.highway_" + highway + ".width",
                DEFAULT_ROAD_WIDTH);

        return paramWidth;

    }

    @Override
    public void draw(GL2 pGl, Camera pCamera) {
        // FIXME object is not in local coordinates!
        this.modelRender.render(pGl, this.model);

    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (this.model == null) {
            buildWorldObject();
        }

        return Collections.singletonList(new ExportItem(this.model, new Point3d(this.getGlobalX(), 0, -this.getGlobalY()),
                new Vector3d(1, 1, 1)));
    }

    @Override
    public Model getModel() {
        return model;
    }
}
