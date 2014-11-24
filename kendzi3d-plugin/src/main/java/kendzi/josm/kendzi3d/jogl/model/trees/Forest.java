/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.trees;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.geometry.Bounds;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.util.DrawUtil;
import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.lod.LOD;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractWayModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.MultiPointWorldObject;
import kendzi.math.geometry.Triangulate;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Representing trees in row model.
 * 
 * @author Tomasz Kedziora (Kendzi)
 */
public class Forest extends AbstractWayModel implements MultiPointWorldObject {

    /** Log. */
    private static final Logger log = Logger.getLogger(Forest.class);

    ModelCacheService modelCacheService;
    MetadataCacheService metadataCacheService;

    private static final double EPSILON = 0.001;

    /**
     * Renderer of model.
     */
    private final ModelRender modelRender;

    private final EnumMap<LOD, Model> modelLod;

    private String type;
    private String genus;
    private String species;

    Vector3d scale;

    private List<Point2d> hookPoints;

    private Integer numOfTrees;

    private List<HeightCluster> clusterHook;

    /**
     * @param pWay
     *            way
     * @param perspective
     *            perspective
     */
    public Forest(Way pWay, Perspective perspective, ModelRender pModelRender, ModelCacheService modelCacheService,
            MetadataCacheService metadataCacheService) {
        super(pWay, perspective);

        modelLod = new EnumMap<LOD, Model>(LOD.class);

        scale = new Vector3d(1d, 1d, 1d);

        modelRender = pModelRender;
        this.modelCacheService = modelCacheService;
        this.metadataCacheService = metadataCacheService;
    }

    @Override
    public void buildWorldObject() {

        buildModel(LOD.LOD1);
        buildModel(LOD.LOD2);
        buildModel(LOD.LOD3);
        buildModel(LOD.LOD4);
        buildModel(LOD.LOD5);

        buildModel = true;
    }

    public void buildModel(LOD pLod) {

        type = way.get("type");
        if (type == null) {
            type = "unknown";
        }
        genus = way.get("genus");
        species = way.get("species");

        double height = Tree.getHeight(way, species, genus, type, metadataCacheService);

        Model model = null;

        model = Tree.findSimpleModel(species, genus, type, pLod, metadataCacheService, modelCacheService);

        setupScale(model, height);

        modelLod.put(pLod, model);

        numOfTrees = ModelUtil.parseInteger(way.get("tree"), null);

        if (hookPoints == null) {
            hookPoints = calsHookPoints(points, numOfTrees);

            log.info("***** num of tree: " + hookPoints.size());

            double CLUSTER_SIZE = 50;

            Point2d minBound = minBound(points);

            clusterHook = calcClusterHooks(hookPoints, minBound, CLUSTER_SIZE, HeightCluster.class);

            calcHeight(clusterHook);

        }

    }

    private void calcHeight(List<HeightCluster> clusterHooks) {

        for (HeightCluster heightCluster : clusterHooks) {
            List<Point2d> hook = heightCluster.getHook();

            double[] heights = new double[hook.size()];
            for (int i = 0; i < hook.size(); i++) {
                heights[i] = randHeight();
            }
            heightCluster.setHeight(heights);
        }
    }

    private double randHeight() {
        Random randomNumberGenerator = new Random();

        return randomNumberGenerator.nextDouble() / 2d + 0.5;
    }

    private <T extends Cluster> ArrayList<T> calcClusterHooks(List<Point2d> pHookPoints2, Point2d pMinBound,
            double pClusterSize, Class<T> clazz) {

        Point2d minBound = pMinBound;

        if (minBound == null) {
            minBound = minBound(pHookPoints2);
        }
        Point2d maxBound = maxBound(pHookPoints2);

        double minX = minBound.x;
        double minY = minBound.y;

        double width = maxBound.x - minBound.x;
        double height = maxBound.y - minBound.y;

        int clusterXMax = (int) Math.ceil(width / pClusterSize);
        int clusterYMax = (int) Math.ceil(height / pClusterSize);

        T[] clusters = (T[]) Array.newInstance(clazz, clusterYMax * clusterXMax);

        for (int y = 0; y < clusterYMax; y++) {
            for (int x = 0; x < clusterXMax; x++) {

                T c = null;
                try {
                    c = clazz.newInstance();
                } catch (InstantiationException e) {
                    log.error(e, e);
                } catch (IllegalAccessException e) {
                    log.error(e, e);
                }

                c.setCenter(new Point3d(minX + pClusterSize / 2 + pClusterSize * x, 0,
                        -(minY + pClusterSize / 2 + pClusterSize * y)));

                clusters[clusterXMax * y + x] = c;
            }
        }

        for (Point2d p : pHookPoints2) {
            int clusterX = (int) Math.floor((p.x - minX) / pClusterSize);
            int clusterY = (int) Math.floor((p.y - minY) / pClusterSize);

            clusters[clusterXMax * clusterY + clusterX].getHook().add(p);

        }

        ArrayList<T> ret = new ArrayList<T>();
        for (int y = 0; y < clusterYMax; y++) {
            for (int x = 0; x < clusterXMax; x++) {
                ret.add(clusters[clusterXMax * y + x]);
            }
        }

        return ret;
    }

    static class HeightCluster extends Cluster {
        double[] height;

        /**
         * @return the height
         */
        public double[] getHeight() {
            return height;
        }

        /**
         * @param height
         *            the height to set
         */
        public void setHeight(double[] height) {
            this.height = height;
        }

    }

    static class Cluster {
        List<Point2d> hook;
        Point3d center;

        public Cluster() {
            super();
            hook = new ArrayList<Point2d>();
            center = new Point3d();
        }

        /**
         * @return the hook
         */
        public List<Point2d> getHook() {
            return hook;
        }

        /**
         * @param hook
         *            the hook to set
         */
        public void setHook(List<Point2d> hook) {
            this.hook = hook;
        }

        /**
         * @return the center
         */
        public Point3d getCenter() {
            return center;
        }

        /**
         * @param center
         *            the center to set
         */
        public void setCenter(Point3d center) {
            this.center = center;
        }
    }

    private List<Point2d> calsHookPoints(List<Point2d> points, Integer numOfTrees) {

        double area = Math.abs(Triangulate.area(points));

        if (numOfTrees == null) {
            // 1 tree on 100 square meters
            numOfTrees = (int) Math.round(area / 100d);
        }

        if (numOfTrees > 1000) {
            // XXX
            numOfTrees = 1000;
        }

        PolygonList2d polygon = new PolygonList2d(points);

        return monteCarloHookGenerator(polygon, numOfTrees);

    }

    /**
     * Minimal values in polygon. Minimal coordinates of bounding box.
     * 
     * @param pPolygon
     *            polygon
     * @return minimal values
     */
    public static Point2d minBound(List<Point2d> points) {

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (Point2d p : points) {
            if (p.x < minX) {
                minX = p.x;
            }
            if (p.y < minY) {
                minY = p.y;
            }
        }

        return new Point2d(minX, minY);
    }

    /**
     * Maximal values in polygon. Maximal coordinates of bounding box.
     * 
     * @param pPolygon
     *            polygon
     * @return maximal values
     */
    public static Point2d maxBound(List<Point2d> points) {

        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (Point2d p : points) {
            if (p.x > maxX) {
                maxX = p.x;
            }
            if (p.y > maxY) {
                maxY = p.y;
            }
        }

        return new Point2d(maxX, maxY);
    }

    /**
     * Generate hook for trees using monte carlo method.
     * 
     * @param polygon
     *            polygon
     * @param numOfTrees
     *            num of trees
     * @return hooks for trees
     */
    private List<Point2d> monteCarloHookGenerator(PolygonList2d polygon, Integer numOfTrees) {

        List<Point2d> ret = new ArrayList<Point2d>(numOfTrees);

        Point2d minBound = PolygonUtil.minBound(polygon);
        Point2d maxBound = PolygonUtil.maxBound(polygon);

        double minX = minBound.x;
        double minY = minBound.y;

        double width = maxBound.x - minBound.x;
        double height = maxBound.y - minBound.y;

        Random randomNumberGenerator = new Random();

        for (int i = 0; i < numOfTrees * 10; i++) {

            double x = minX + randomNumberGenerator.nextDouble() * width;
            double y = minY + randomNumberGenerator.nextDouble() * height;

            Point2d hook = new Point2d(x, y);

            if (PolygonUtil.isPointInsidePolygon(hook, polygon)) {
                ret.add(hook);
            }

            if (ret.size() >= numOfTrees) {
                return ret;
            }
        }

        return ret;
    }

    private void setupScale(Model model2, double height) {

        Bounds bounds = model2.getBounds();

        double modelHeight = bounds.max.y;

        double modelScaleHeight = height / modelHeight;

        double modelScaleWidht = modelScaleHeight;

        scale.x = modelScaleWidht;
        scale.y = modelScaleHeight;
        scale.z = modelScaleWidht;

    }

    public boolean isModelBuild(LOD pLod) {

        if (modelLod.get(pLod) != null) {
            return true;
        }
        return false;
    }

    public void draw(GL2 gl, Camera camera, LOD pLod) {
        Model model2 = modelLod.get(pLod);

        if (model2 != null) {

            Integer dl = getDisplayList(model2);

            if (dl == null) {
                dl = createDisplayList(gl, model2);
            }

            gl.glEnable(GLLightingFunc.GL_NORMALIZE);

            for (Point2d hook : hookPoints) {

                gl.glPushMatrix();

                gl.glTranslated(getGlobalX() + hook.x, 0, -(getGlobalY() + hook.y));

                gl.glScaled(scale.x, scale.y, scale.z);

                gl.glCallList(dl);

                gl.glPopMatrix();
            }

            gl.glDisable(GLLightingFunc.GL_NORMALIZE);
        }
    }

    private int createDisplayList(GL2 gl, Model model2) {

        // create one display list
        int index = gl.glGenLists(1);

        // XXX for texture download
        modelRender.render(gl, model2);

        // compile the display list, store a triangle in it
        gl.glNewList(index, GL2.GL_COMPILE);

        modelRender.resetFaceCount();
        modelRender.render(gl, model2);
        log.info("***> face count: " + modelRender.getFaceCount());

        gl.glEndList();

        displayList.put(model2, index == 0 ? null : index);

        return index;
    }

    Map<Model, Integer> displayList = new HashMap<Model, Integer>();

    private Integer getDisplayList(Model model2) {
        return displayList.get(model2);
    }

    @Override
    public void draw(GL2 gl, Camera camera, boolean selected) {
        draw(gl, camera);
    }

    @Override
    public void draw(GL2 gl, Camera camera) {

        Point3d localCamera = new Point3d(camera.getPoint().x - getGlobalX(), camera.getPoint().y, camera.getPoint().z
                + getGlobalY());

        for (HeightCluster c : clusterHook) {

            if (modelRender.isDebugging()) {
                gl.glPushMatrix();

                gl.glTranslated(c.getCenter().x + getGlobalX(), 2, c.getCenter().z - getGlobalY());

                DrawUtil.drawDotY(gl, 6d, 6);

                gl.glPopMatrix();
            }

            LOD lod = RenderJOSM.getLods(c.getCenter(), localCamera);
            List<Point2d> hookPoints = c.getHook();
            double[] heights = c.getHeight();

            Model model2 = modelLod.get(lod);

            if (model2 != null) {

                Integer dl = getDisplayList(model2);

                if (dl == null) {
                    dl = createDisplayList(gl, model2);
                }

                gl.glEnable(GLLightingFunc.GL_NORMALIZE);

                int i = 0;
                for (Point2d hook : hookPoints) {

                    double height = heights[i];

                    gl.glPushMatrix();

                    gl.glTranslated(getGlobalX() + hook.x, 0, -(getGlobalY() + hook.y));

                    gl.glScaled(scale.x * height, scale.y * height, scale.z * height);

                    gl.glCallList(dl);

                    gl.glPopMatrix();
                    i++;
                }

                gl.glDisable(GLLightingFunc.GL_NORMALIZE);
            }

        }

    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Model getModel() {
        return modelLod.get(LOD.LOD1);
    }

    @Override
    public List<Point3d> getPoints() {
        List<Point3d> ret = new ArrayList<Point3d>();
        for (HeightCluster cluster : clusterHook) {

            List<Point2d> hookPoints = cluster.getHook();

            for (Point2d hook : hookPoints) {
                ret.add(new Point3d(hook.x, 0, -hook.y));
            }
        }
        return ret;
    }

    @Override
    public Point3d getPosition() {
        return getPoint();
    }
}
