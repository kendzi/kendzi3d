/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.DrawUtil;
import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.factory.FaceFactory;
import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.material.AmbientDiffuseComponent;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.model.DrawableModel;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.lod.DLODSuport;
import kendzi.josm.kendzi3d.jogl.model.lod.LOD;
import kendzi.josm.kendzi3d.jogl.selection.Selectable;
import kendzi.josm.kendzi3d.jogl.selection.Selection;
import kendzi.kendzi3d.world.WorldObject;
import kendzi.kendzi3d.world.quad.ModelLayerBuilder;
import kendzi.kendzi3d.world.quad.layer.Layer;
import kendzi.math.geometry.ray.Ray3d;
import kendzi.math.geometry.ray.Ray3dUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.event.AbstractDatasetChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataSetListenerAdapter;
import org.openstreetmap.josm.data.osm.event.DatasetEventManager;
import org.openstreetmap.josm.data.osm.event.DatasetEventManager.FireMode;
import org.openstreetmap.josm.data.osm.event.TagsChangedEvent;
import org.openstreetmap.josm.data.projection.Projection;

public class RenderJOSM implements DataSetListenerAdapter.Listener {

    /** Log. */
    private static final Logger log = Logger.getLogger(RenderJOSM.class);

    DataSet dataSet = null;

    public static double lod1 = 20 * 20;

    private static double lod2 = 100 * 100;

    private static double lod3 = 500 * 500;

    private static double lod4 = 1000 * 1000;

    /**
     * Request for models rebuild.
     */
    private boolean datasetChanged = true;

    /**
     * Perspective used to transform coordinates from geolocation (lat/lon) to
     * local, drawable world.
     */
    private Perspective3D perspective;

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
    private List<Layer> layers = new ArrayList<Layer>();

    private Selection lastSelection;

    private GLUquadric quadratic; // Storage For Our Quadratic Objects
    private GLU glu = new GLU();

    private List<WorldObject> models = new ArrayList<WorldObject>(500);

    public Perspective3D getPerspective() {
        return this.perspective;
    }

    public void setPerspective(Perspective3D pers) {
        this.perspective = pers;
    }

    public RenderJOSM() {

        // FIXME TODO XXX
        DatasetEventManager.getInstance().addDatasetListener(new DataSetListenerAdapter(this), FireMode.IMMEDIATELY);

        this.errorModel = createErrorModel();

        this.center = new EastNorth(0, 0);

        this.perspective = new Perspective3D(1, 0, 0);

    }

    public void init(GL2 gl) {

        this.quadratic = this.glu.gluNewQuadric();
        // Create Smooth Normals
        this.glu.gluQuadricNormals(this.quadratic, GLU.GLU_SMOOTH);

    }

    public void draw(GL2 gl, Camera camera) {

        if (this.datasetChanged) {
            this.datasetChanged = false;
            rebuildData();
        }
        for (WorldObject r : models) {

            drawModel((DrawableModel) r, gl, camera);

        }

        if (this.modelRender.isDebugging()) {
            drawSelectable(gl);

        }

        this.modelRender.setupDefaultMaterial(gl);
    }

    /**
     * @param model
     * @param gl
     * @param camera
     */
    public void drawModel(DrawableModel model, GL2 gl, Camera camera) {
        if (model.isError()) {
            drawError(model, gl, camera);
            return;
        }

        // r.isInCamraRange(pCamraX, pCamraY, pCamraRange)
        try {
            if (!model.isWorldObjectBuild()) {
                model.buildWorldObject();
            }

            if (model instanceof DLODSuport) {
                DLODSuport lodModel = (DLODSuport) model;
                LOD pLod = getLods(lodModel, camera);

                lodModel.draw(gl, camera, pLod);

            } else {

                model.draw(gl, camera);

            }

        } catch (Exception e) {
            log.error("error rendering", e);
            model.setError(true);
            drawError(model, gl, camera);
        }
    }

    private void drawError(DrawableModel r, GL2 pGl, Camera camera) {
        double x = r.getX();
        double y = r.getY();

        pGl.glPushMatrix();
        pGl.glTranslated(x, 0, -y);

        this.modelRender.render(pGl, this.errorModel);

        pGl.glPopMatrix();
    }

    kendzi.jogl.model.geometry.Model createErrorModel() {
        ModelFactory modelBuilder = ModelFactory.modelBuilder();

        Material material = new Material(new AmbientDiffuseComponent(Color.RED, Color.RED));

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

        // XXX recalculate normals!

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
        return getLods(r.getPoint(), camera.getPoint());
    }

    public static LOD getLods(Point3d point, Point3d camera) {

        // double lod5 = 1000 * 1000;

        // //XXX temporary
        // Point3d point = r.getPoint();
        // Point3d c = camera.getPoint();
        double dx = camera.x - point.x;
        double dy = camera.y - point.y;
        double dz = camera.z - point.z;

        double distance = dx * dx + dy * dy + dz * dz;

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

    public DataSet getDataSet() {
        return this.dataSet;
        // return Main.main.getCurrentDataSet();
    }

    public void rebuildData() {
        DataSet dataset = getDataSet();

        models.clear();

        if (dataset == null) {
            return;
        }

        models.addAll(ModelLayerBuilder.bulid(layers, dataset, perspective));

        // for (Layer layer : layers) {
        // layer.clear();
        // }
        //
        // for (Node node : dataset.getNodes()) {
        //
        // if (node.isDeleted()) {
        // continue;
        // }
        //
        // // layers
        // for (Layer layer : this.layers) {
        // if (layer.getNodeMatcher() != null &&
        // layer.getNodeMatcher().match(node)) {
        // layer.addModel(node, this.pers);
        // }
        // }
        // }
        //
        // for (Way way : dataset.getWays()) {
        //
        // if (way.isDeleted()) {
        // continue;
        // }
        //
        // // layers
        // for (Layer layer : this.layers) {
        // if (layer.getWayMatcher() != null &&
        // layer.getWayMatcher().match(way)) {
        // layer.addModel(way, this.pers);
        // }
        // }
        // }
        //
        // for (org.openstreetmap.josm.data.osm.Relation relation :
        // dataset.getRelations()) {
        //
        // if (relation.isDeleted()) {
        // continue;
        // }
        //
        // // layers
        // for (Layer layer : this.layers) {
        // if (layer.getRelationMatcher() != null &&
        // layer.getRelationMatcher().match(relation)) {
        // layer.addModel(relation, this.pers);
        // }
        // }
        // }
    }

    private void setupPerspective3D(EastNorth center) {
        Projection proj = Main.getProjection();

        LatLon l1 = proj.eastNorth2latlon(center.add(1, 0));
        LatLon l2 = proj.eastNorth2latlon(center.add(-1, 0));

        double dist = l1.greatCircleDistance(l2);

        double scale = dist / 2d;

        this.perspective = new Perspective3D(scale, center.getX(), center.getY());
    }

    @Override
    public void processDatasetEvent(AbstractDatasetChangedEvent pEvent) {
        if (Main.map == null) {

            return;
        }

        // if (pEvent != null && pEvent.getType() ==
        // DatasetEventType.TAGS_CHANGED){
        if (pEvent instanceof TagsChangedEvent) {
            updatePrimitive(((TagsChangedEvent) pEvent).getPrimitive());
        }

        if (this.center == null || pEvent == null) {
            // for tests we change center only on menu button action
            this.center = Main.map.mapView.getCenter();
        }

        if (pEvent != null) {
            this.dataSet = pEvent.getDataset();
        }

        if (pEvent instanceof DataChangedEvent) {

            if (this.dataSet != null) {

                Projection proj = Main.getProjection();

                this.center = centerFromDataSet(this.dataSet, proj);

            } else {
                this.center = new EastNorth(0, 0);
            }
        }

        setupPerspective3D(this.center);

        this.datasetChanged = true;
    }

    private void updatePrimitive(OsmPrimitive primitive) {
        // TODO Auto-generated method stub

    }

    public static Bounds boundsFromDataSet(DataSet dataset) {
        // XXX move to util

        Bounds bounds = null;
        for (Bounds b : dataset.getDataSourceBounds()) {
            if (bounds == null) {
                bounds = new Bounds(b);
            } else {
                bounds.extend(b.getMax());
                bounds.extend(b.getMin());
            }
        }

        return bounds;

    }

    private EastNorth centerFromDataSet(DataSet dataset, Projection proj) {

        double maxX = -Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;

        double maxY = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

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

        return proj.latlon2eastNorth(new LatLon((maxY + minY) / 2d, (maxX + minX) / 2d));
    }

    /**
     * @return the modelRender
     */
    public ModelRender getModelRender() {
        return this.modelRender;
    }

    /**
     * @param modelRender
     *            the modelRender to set
     */
    public void setModelRender(ModelRender modelRender) {
        this.modelRender = modelRender;
    }

    /**
     * @return the layerList
     */
    public List<Layer> getLayerList() {
        return this.layers;
    }

    /**
     * @param layerList
     *            the layerList to set
     */
    public void setLayerList(List<Layer> layerList) {
        this.layers = layerList;
    }

    public void drawSelectable(GL2 gl) {

        gl.glColor3fv(Color.ORANGE.darker().getRGBComponents(new float[4]), 0);

        for (WorldObject r : models) {
            if (r instanceof Selectable) {
                for (Selection s : ((Selectable) r).getSelection()) {

                    gl.glPushMatrix();

                    Point3d p = s.getCenter();

                    double dx = p.x;
                    double dy = p.y;
                    double dz = p.z;

                    gl.glLineWidth(1);
                    gl.glTranslated(dx, dy, dz);

                    DrawUtil.drawDotOuterY(gl, s.getRadius(), 24);

                    gl.glRotated(90d, 1d, 0, 0);
                    DrawUtil.drawDotOuterY(gl, s.getRadius(), 24);

                    gl.glRotated(90d, 0, 0, 1d);
                    DrawUtil.drawDotOuterY(gl, s.getRadius(), 24);

                    gl.glPopMatrix();
                }
            }

        }

    }

    public Selection select(Ray3d selectRay) {
        Selection selection = null;
        double min = Double.MAX_VALUE;

        for (WorldObject r : models) {
            if (r instanceof Selectable) {
                for (Selection s : ((Selectable) r).getSelection()) {
                    Double intersect = Ray3dUtil.intersect(selectRay, s.getCenter(), s.getRadius());
                    if (intersect == null) {
                        continue;
                    }
                    if (intersect < min) {
                        selection = s;
                        min = intersect;
                    }
                }
            }
        }

        Selection last = this.lastSelection;

        if (selection != null) {
            log.info("selected object: " + selection);
            // FIXME
            if (last != selection) {
                // FIXME
                selection.select(true);
                if (last != null) {
                    last.select(false);
                }
            }

        } else {
            log.info("can't find selection");
            // FIXME
            if (last != null) {
                last.select(false);
            }
        }

        this.lastSelection = selection;

        return selection;
    }

    /**
     * @return the models
     */
    public List<WorldObject> getModels() {
        return models;
    }

}
