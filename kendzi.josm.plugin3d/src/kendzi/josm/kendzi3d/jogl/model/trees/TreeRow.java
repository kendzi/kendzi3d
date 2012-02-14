/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.trees;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.geometry.Bounds;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.lod.DLODSuport;
import kendzi.josm.kendzi3d.jogl.model.lod.LOD;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractWayModel;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.ModelCacheService;

import org.openstreetmap.josm.data.osm.Way;

/**
 * Representing trees in row model.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public class TreeRow extends AbstractWayModel implements DLODSuport {

    private static final double EPSILON = 0.001;

    /**
     * Renderer of model.
     */
    private ModelRender modelRender;

    /**
     * Model cache service
     */
    private ModelCacheService modelCacheService;

    /**
     * Metadata cache service.
     */
    private MetadataCacheService metadataCacheService;

    private EnumMap<LOD, Model> modelLod;

    private String type;
    private String genus;
    private String species;

    Vector3d scale;

    private List<Point2d> hookPoints;

    private Integer numOfTrees;

    /**
     * @param pWay way
     * @param pPerspective3D perspective
     */
    public TreeRow(Way pWay, Perspective3D pPerspective3D,
            ModelRender pModelRender,
            ModelCacheService modelCacheService,
            MetadataCacheService metadataCacheService) {
        super(pWay, pPerspective3D);

        this.modelLod = new EnumMap<LOD, Model>(LOD.class);

        this.scale = new Vector3d(1d, 1d, 1d);

        this.modelRender = pModelRender;
        this.modelCacheService = modelCacheService;
        this.metadataCacheService = metadataCacheService;
    }

    @Override
    public void buildModel() {

        buildModel(LOD.LOD1);

        this.buildModel = true;
    }

    @Override
    public void buildModel(LOD pLod) {

        this.type = this.way.get("type");
        if (this.type == null) {
            this.type = "unknown";
        }
        this.genus = this.way.get("genus");
        this.species = this.way.get("species");

        double height = Tree.getHeight(this.way, this.species, this.genus, this.type, metadataCacheService);

        Model model = null;

        model = Tree.findSimpleModel(this.species, this.genus, this.type, pLod, metadataCacheService, modelCacheService);

        setupScale(model, height);

        this.modelLod.put(pLod, model);

        this.numOfTrees = ModelUtil.parseInteger(this.way.get("tree"), null);

        if (this.hookPoints == null) {
            this.hookPoints = calsHookPoints(this.points, this.numOfTrees);
        }
    }

    private List<Point2d> calsHookPoints(List<Point2d> points, Integer numOfTrees) {

        double distance = calcDistance(points);

        if (numOfTrees == null) {
            numOfTrees = (int) Math.round(distance / 5d);
        }


        List<Point2d> ret = new ArrayList<Point2d>();

        double repeatEvery = distance / numOfTrees;

        double lastOffset = 0;

        Point2d b = points.get(0);
        for (int i = 1 ; i<points.size(); i ++) {
            Point2d e = points.get(i);
            distance = e.distance(b);

            lastOffset = splitVector(b, e, lastOffset, repeatEvery, ret);

            b = e;

        }


        return ret;
    }

    private double splitVector(Point2d b, Point2d e, double left, double every, List<Point2d> ret) {
        Vector2d v = new Vector2d(e);
        v.sub(b);
        double distance = v.length();
        if (distance + EPSILON < left) {
            return left - distance;
        }

        v.normalize();

        Vector2d beginVector = new Vector2d(v);
        beginVector.scale(left);

        Vector2d everyVector = new Vector2d(v);
        everyVector.scale(every);

        Vector2d repeat = beginVector;


        do {
            ret.add(new Point2d(b.x + repeat.x, b.y + repeat.y));

            repeat.add(everyVector);


        } while ( distance + EPSILON >= repeat.length());

        return repeat.length() - distance;
//        return distance - (repeat.length() - everyVector.length());

    }

    private double calcDistance(List<Point2d> points) {

        if (points == null || points.size() < 2) {
            return 0d;
        }
        double distance = 0;

        Point2d b = points.get(0);
        for (int i = 1 ; i<points.size(); i ++) {
            Point2d e = points.get(i);
            distance = distance + e.distance(b);

            b = e;
        }
        return distance;
    }

    private void setupScale(Model model2, double height) {

        Bounds bounds = model2.getBounds();

        double modelHeight = bounds.max.y;

        double modelWidht = Math.max(bounds.max.x - bounds.min.x, bounds.max.z - bounds.min.z);

        double modelScaleHeight = height / modelHeight;

        double modelScaleWidht = modelScaleHeight;

        this.scale.x = modelScaleWidht;
        this.scale.y = modelScaleHeight;
        this.scale.z = modelScaleWidht;

//        model2.useScale = true;
    }





    @Override
    public boolean isModelBuild(LOD pLod) {

        if (this.modelLod.get(pLod) != null) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(GL2 gl, Camera camera, LOD pLod) {
        Model model2 = this.modelLod.get(pLod);
        if (model2 != null) {

            gl.glEnable(GL2.GL_NORMALIZE);

            for (Point2d hook : this.hookPoints) {

                gl.glPushMatrix();

                gl.glTranslated(this.getGlobalX() + hook.x, 0, -(this.getGlobalY() + hook.y));

                gl.glScaled(this.scale.x, this.scale.y, this.scale.z);

                this.modelRender.render(gl, model2);

                gl.glPopMatrix();
            }

            gl.glDisable(GL2.GL_NORMALIZE);
        }
    }


    @Override
    public void draw(GL2 gl, Camera camera) {
        draw(gl, camera, LOD.LOD1);
    }

    @Override
    public Point3d getPoint() {
        return new Point3d(this.x, 0, -this.y);
    }


}
