/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.model.Building;
import kendzi.josm.kendzi3d.jogl.model.DLODSuport;
import kendzi.josm.kendzi3d.jogl.model.Fence;
import kendzi.josm.kendzi3d.jogl.model.LOD;
import kendzi.josm.kendzi3d.jogl.model.Model;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.Road;
import kendzi.josm.kendzi3d.jogl.model.Tree;
import kendzi.josm.kendzi3d.jogl.model.Water;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.search.SearchCompiler;
import org.openstreetmap.josm.actions.search.SearchCompiler.Match;
import org.openstreetmap.josm.actions.search.SearchCompiler.ParseError;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.event.AbstractDatasetChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataSetListenerAdapter;
import org.openstreetmap.josm.data.osm.event.DatasetEventManager;
import org.openstreetmap.josm.data.osm.event.DatasetEventManager.FireMode;
import org.openstreetmap.josm.data.projection.Projection;

public class RenderJOSM implements DataSetListenerAdapter.Listener {

    private static final double NOWE_KRAMSKO_CENTER_X = 0.275119242;
    private static final double NOWE_KRAMSKO_CENTER_Y = 1.070152217;

    /**
     * List of models. XXX move to layers.
     */
    private List<Model> modelList = new ArrayList<Model>();

    /**
     * Matcher for building layer. XXX move to layers.
     */
    private Match buildings;

    /**
     * Matcher for roads layer. XXX move to layers.
     */
    private Match roadsMatcher;

    /**
     * Matcher for trees layer. XXX move to layers.
     */
    private Match treesMatcher;

    /**
     * Matcher for water layer. XXX move to layers.
     */
    private Match waterMatcher;

    /**
     * Matcher for fence layer. XXX move to layers.
     */
    private Match fenceMatcher;

    /**
     * Request for rebuildin models.
     */
    private boolean datasetChanged = true;

    /**
     * Perspective used by OpenGl. Transforming coordinates from EastNorth to OpenGl .
     */
    private Perspective3D pers;

//    /**
//     * X Offset of coordinate system used by openGl.
//     */
//    private double centerX = 0;
//
//    /**
//     * Y Offset of coordinate system used by openGl.
//     */
//    private double centerY = 0;


    /**
     * Position of sun. XXX
     */
    private float lightPos[] = new float[] { 0.0f, 1.0f, 1.0f, 0f };

    /**
     * Model displayed when error happen.
     */
    private kendzi.jogl.model.geometry.Model errorModel;


    /**
     * Renderer of model.
     */
    private ModelRender modelRender;

    /**
     * Center point of world.
     */
    private EastNorth center;


    public Perspective3D getPerspective() {
        return this.pers;
    }
    public void setPerspective(Perspective3D pers) {
        this.pers = pers;
    }

    public RenderJOSM() {
        try {
            // XXX move to layer builder
            this.buildings = SearchCompiler.compile("(building=*) | (building\\:part=yes)", false, false);
            this.roadsMatcher = SearchCompiler.compile("(highway=*)", false, false);
            this.treesMatcher = SearchCompiler.compile("(natural=tree)", false, false);
            this.waterMatcher = SearchCompiler.compile("(natural=water) | (landuse=reservoir)", false,
                    false);
            this.fenceMatcher = SearchCompiler.compile("barrier=fence", false,
                    false);
        } catch (ParseError e) {
            this.buildings = new SearchCompiler.Never();
            e.printStackTrace();
        }

        DatasetEventManager.getInstance().addDatasetListener(new DataSetListenerAdapter(this),
                FireMode.IMMEDIATELY);

        this.errorModel = createErrorModel();

        this.center = new EastNorth(0, 0);

        this.pers = new Perspective3D(1, 0, 0);

    }

    public void init(GL2 gl) {

//        gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);

        this.modelRender = ModelRender.getInstance();

    }

    public void draw(GL2 gl, Camera camera) {

        // _direction_
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, this.lightPos, 0);

        if (this.datasetChanged) {
            this.datasetChanged = false;
            rebuildData();
        }

        for (Model r : this.modelList) {
            if (r.isError()) {
                drawError(r, gl, camera);
                continue;
            }

//            r.isInCamraRange(pCamraX, pCamraY, pCamraRange)
            try {

                if (r instanceof DLODSuport) {
                    DLODSuport lodModel = ((DLODSuport) r);
                    LOD pLod = getLods(lodModel, camera);
                    if (!lodModel.isModelBuild(pLod)) {
                        lodModel.buildModel(pLod);
                    }

                    lodModel.draw(gl, camera, pLod);

                } else {
                        if (!r.isBuildModel()) {
                            r.buildModel();
                        }

                        r.draw(gl, camera);

                }

            } catch (Exception e) {
                e.printStackTrace();
                r.setError(true);
                drawError(r, gl, camera);
            }
        }
    }

    private void drawError(Model r, GL2 pGl, Camera camera) {
        double x = r.getX();
        double y = r.getY();

        pGl.glPushMatrix();
        pGl.glTranslated(x, 0, -y);

        this.modelRender.render(pGl, this.errorModel);

        pGl.glPopMatrix();
    }

    kendzi.jogl.model.geometry.Model createErrorModel() {
        ModelFactory modelBuilder = ModelFactory.modelBuilder();

        Material material = new Material();
        material.ambientColor = Color.RED;
        material.diffuseColor = Color.RED;
        material.specularColor = Color.RED;

        material.emissive = Color.RED;

        material.shininess = (float) 0.5;

//        material.shininess2 = (float) 0.5;

        int mat = modelBuilder.addMaterial(material);

        MeshFactory mesh = modelBuilder.addMesh("walls");

        mesh.materialID = mat;
        mesh.hasTexture = false;

        int v1 = mesh.addVertex(new Point3d(0, 0, 0));
        int v2 = mesh.addVertex(new Point3d(0, 1, 1));
        int v3 = mesh.addVertex(new Point3d(1, 1, 0));
        int v4 = mesh.addVertex(new Point3d(0, 1, -1));
        int v5 = mesh.addVertex(new Point3d(-1, 1, 0));
        int v6 = mesh.addVertex(new Point3d(0, 2, 0));

        //XXX recalculate normals!

        int n1 = mesh.addNormal(new Vector3d(0, 1, 0));

        FaceFactory face = mesh.addFace(FaceType.TRIANGLES);

        face.addVertIndex(v1);
        face.addVertIndex(v2);
        face.addVertIndex(v3);

        face.addVertIndex(v1);
        face.addVertIndex(v3);
        face.addVertIndex(v4);

        face.addVertIndex(v1);
        face.addVertIndex(v4);
        face.addVertIndex(v5);

        face.addVertIndex(v1);
        face.addVertIndex(v5);
        face.addVertIndex(v2);

        face.addVertIndex(v6);
        face.addVertIndex(v3);
        face.addVertIndex(v2);

        face.addVertIndex(v6);
        face.addVertIndex(v4);
        face.addVertIndex(v3);

        face.addVertIndex(v6);
        face.addVertIndex(v5);
        face.addVertIndex(v4);

        face.addVertIndex(v6);
        face.addVertIndex(v2);
        face.addVertIndex(v5);


        kendzi.jogl.model.geometry.Model model = modelBuilder.toModel();
        model.setUseLight(true);
        model.setUseTexture(true);

        return model;

    }


    private LOD getLods(DLODSuport r, Camera camera) {
        // XXX only temporary !
        double lod1 = 20 * 20;
        double lod2 = 100 * 100;
        double lod3 = 500 * 500;
        double lod4 = 1000 * 1000;
//        double lod5 = 1000 * 1000;

        //XXX temporary
        Point3d point = r.getPoint();
        Point3d c = camera.getPoint();
        double dx = c.x - point.x;
        double dy = c.y - point.y;
        double dz = c.z - point.z;

        double distance = dx * dx  + dy * dy + dz * dz;

        if (distance < lod1) {
            return LOD.LOD1;
        } else if (distance < lod2) {
            return LOD.LOD2;
        } else if (distance < lod3) {
            return LOD.LOD3;
        } else if (distance < lod4) {
            return LOD.LOD4;
        }
        return LOD.LOD5;

    }

    public void rebuildData() {
        DataSet dataset = Main.main.getCurrentDataSet();

        if (dataset == null) {
            return;
        }

        this.modelList.clear();

//        System.out.println(this.pers.calcX(this.centerX));
//        System.out.println(this.pers.calcX(0.2751230166333761));
//        System.out.println(this.pers.calcX(0.27512260124501414));
//        System.out.println(this.pers.calcX(0.2751235175428715));


        for (Node node : dataset.getNodes()) {

            if (this.treesMatcher.match(node)) {

                this.modelList.add(new Tree(node, this.pers));
            }
        }
        for (Way way : dataset.getWays()) {
            if (this.buildings.match(way)) {

                this.modelList.add(new Building(way, this.pers));
            }
            if (this.roadsMatcher.match(way)) {

                this.modelList.add(new Road(way, this.pers));
            }

            if (this.waterMatcher.match(way)) {

                this.modelList.add(new Water(way, this.pers));
            }
            if (this.fenceMatcher.match(way)) {

                this.modelList.add(new Fence(way, this.pers, this.lightPos));
            }

        }
    }

    private void setupPerspective3D(EastNorth center) {
        Projection proj = Main.getProjection();

        LatLon l1 = proj.eastNorth2latlon(center.add(1, 0));
        LatLon l2 = proj.eastNorth2latlon(center.add(-1, 0));

        double dist = l1.greatCircleDistance(l2);

        double scale = dist / 2d;

        this.pers = new Perspective3D(scale, center.getX(), center.getY());
    }

    @Override
    public void processDatasetEvent(AbstractDatasetChangedEvent pEvent) {


        if (this.center == null || pEvent == null) {
        // for tests we change center only on menu button action
            this.center = Main.map.mapView.getCenter();
        }

        if (pEvent instanceof DataChangedEvent) {
            AbstractDatasetChangedEvent dce = pEvent;

            double maxX = -Double.MAX_VALUE;
            double minX = Double.MAX_VALUE;

            double maxY = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;

            DataSet dataset = dce.getDataset();

            if (dataset != null) {
                if (dataset.dataSources.size() > 0) {
                    // it is dataset connected with OSM. Get bounds.
                    for (DataSource source : dataset.dataSources) {
                        // create area from data bounds
                        LatLon min = source.bounds.getMin();
                        LatLon max = source.bounds.getMax();

                        if (minX > min.getX()) {
                            minX = min.getX();
                        }
                        if (minY > min.getY()) {
                            minY = min.getY();
                        }
                        if (maxX < max.getX()) {
                            maxX = max.getX();
                        }
                        if (maxY < max.getY()) {
                            maxY = max.getY();
                        }
                    }
                } else {
                    // it is newly created dataset not connected with OSM
                    for (Node n : dataset.getNodes()) {
                     // create area from data bounds
                        LatLon cord = n.getCoor();

                        if (cord == null) {
                            continue;
                        }

                        if (minX > cord.getX()) {
                            minX = cord.getX();
                        }
                        if (minY > cord.getY()) {
                            minY = cord.getY();
                        }
                        if (maxX < cord.getX()) {
                            maxX = cord.getX();
                        }
                        if (maxY < cord.getY()) {
                            maxY = cord.getY();
                        }
                    }

                }

                Projection proj = Main.getProjection();

                this.center = proj.latlon2eastNorth(new LatLon(
                        (maxY + minY) / 2d,
                        (maxX + minX) / 2d
                    ));

            } else {
                this.center = new EastNorth(0, 0);
            }
        }

        setupPerspective3D(this.center);

        this.datasetChanged = true;
    }


}
