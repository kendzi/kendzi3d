/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.building;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.model.factory.BoundsFactory;
import kendzi.jogl.model.geometry.Bounds;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.library.BuildingElementsTextureManager;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.jogl.util.ColorUtil;
import kendzi.josm.kendzi3d.data.OsmPrimitiveWorldObject;
import kendzi.josm.kendzi3d.data.RebuildableWorldObject;
import kendzi.josm.kendzi3d.jogl.model.AbstractModel;
import kendzi.josm.kendzi3d.jogl.model.WorldObjectDebugDrawable;
import kendzi.josm.kendzi3d.jogl.model.building.editor.PartValueEditor;
import kendzi.josm.kendzi3d.jogl.model.building.parser.BuildingParser;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.kendzi3d.buildings.builder.BuildingBuilder;
import kendzi.kendzi3d.buildings.model.BuildingModel;
import kendzi.kendzi3d.buildings.model.BuildingPart;
import kendzi.kendzi3d.buildings.model.WallNode;
import kendzi.kendzi3d.buildings.model.WallPart;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRoofModel;
import kendzi.kendzi3d.buildings.model.roof.shape.RoofTypeAliasEnum;
import kendzi.kendzi3d.buildings.output.BuildingOutput;
import kendzi.kendzi3d.buildings.output.BuildingPartOutput;
import kendzi.kendzi3d.editor.drawer.SelectionDrawUtil;
import kendzi.kendzi3d.editor.selection.ModelSelection;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.editor.selection.editor.CachePoint3dProvider;
import kendzi.kendzi3d.editor.selection.editor.Editor;
import kendzi.kendzi3d.editor.selection.editor.EditorType;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.math.geometry.bbox.Bbox2d;
import kendzi.math.geometry.line.LineSegment3d;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.PrimitiveId;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Representing building model.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public class Building extends AbstractModel implements RebuildableWorldObject, WorldObjectDebugDrawable, OsmPrimitiveWorldObject {

    /** Log. */
    private static final Logger log = Logger.getLogger(Building.class);

    /**
     * Renderer of model.
     */
    private final ModelRender modelRender;

    /**
     * Texture library service.
     */
    private final TextureLibraryStorageService textureLibraryStorageService;

    /**
     * Model of building.
     */
    private Model model;

    private OsmPrimitive primitive;

    private List<Selection> selection = Collections.<Selection> emptyList();

    private Bounds bounds;

    protected boolean preview;

    private BuildingModel bm;

    private BuildingDebugData debug;

    private final static float[] ROOF_EDGES_COLOR = ColorUtil.colorToArray(Color.RED.darker());

    private List<Editor> editors;

    /**
     * Constructor for building.
     *
     * @param primitive
     *            primitive describing building
     * @param perspective
     *            perspective3
     * @param modelRender
     *            model render
     * @param metadataCacheService
     *            metadata cache service
     * @param textureLibraryStorageService
     *            texture library service
     */
    public Building(OsmPrimitive primitive, Perspective perspective, ModelRender modelRender,
            MetadataCacheService metadataCacheService, TextureLibraryStorageService textureLibraryStorageService) {
        super(perspective);

        this.modelRender = modelRender;
        this.textureLibraryStorageService = textureLibraryStorageService;

        this.primitive = primitive;
    }

    @Override
    public void rebuildWorldObject(OsmPrimitive primitive, Perspective perspective) {
        // clean up everything
        this.primitive = primitive;
        this.perspective = perspective;

        buildWorldObject();
    }

    @Override
    public void buildWorldObject() {

        BuildingModel bm = this.bm;

        if (!preview || bm == null) {

            bm = BuildingParser.parseBuilding(primitive, perspective);

            if (primitive instanceof Node || primitive instanceof Way) {

                selection = parseSelection(primitive.getUniqueId(), bm);
            }

            this.bm = bm;
        }
        preview = false;

        if (bm != null) {

            BuildingElementsTextureManager tm = new CacheOsmBuildingElementsTextureMenager(textureLibraryStorageService);

            BuildingOutput buildModel = BuildingBuilder.buildModel(bm, tm);
            Model model = buildModel.getModel();
            model.useLight = true;
            model.useTexture = true;

            this.model = model;
            this.buildModel = true;

            debug = prepareDebugInformation(buildModel);
        }
    }

    private BuildingDebugData prepareDebugInformation(BuildingOutput buildModel) {
        BuildingDebugData d = new BuildingDebugData();

        List<LineSegment3d> edges = new ArrayList<LineSegment3d>();
        List<BuildingDebugDrawer> debugPart = new ArrayList<BuildingDebugDrawer>();

        if (buildModel.getBuildingPartOutput() != null) {
            for (BuildingPartOutput bo : buildModel.getBuildingPartOutput()) {
                debugPart.add(new BuildingDebugDrawer(bo.getRoofDebug()));
                if (bo.getEdges() != null) {
                    edges.addAll(bo.getEdges());
                }
            }
        }
        d.setEdges(edges);
        d.setDebugParts(debugPart);
        return d;

    }

    private void generatePreview() {
        preview = true;
        buildModel = false;

        buildWorldObject();
    }

    private List<Selection> parseSelection(long wayId, final BuildingModel bm) {

        {
            List<Editor> updatedEditors = new ArrayList<Editor>();
            List<BuildingPart> parts = bm.getParts();
            if (parts != null) {
                for (final BuildingPart bp : parts) {

                    PrimitiveId primitive = (PrimitiveId) bp.getContext();
                    {
                        /*
                         * Check if given editor don't exist already, if so we
                         * need to update it.
                         */
                        PartValueEditor editorHeight = findEditor(primitive, editors, "height");
                        if (editorHeight == null) {
                            // don't exist we create fresh one
                            editorHeight = new PartValueEditor(primitive, "height") {
                                @Override
                                public void preview(double value) {
                                    // sets height to current building part
                                    getBuildingPart().setMaxHeight(value);

                                    generatePreview();
                                };

                            };
                            editorHeight.setOffset(0.1);
                        }
                        /*
                         * We need to re-setup building part. It can change when
                         * building is change after change made in JOSM dataset.
                         * It is not important when preview is generated.
                         */
                        editorHeight.setBuildingPart(bp);

                        Bbox2d bounds = calcBounds(bp);

                        double minHeight = bp.getDefaultMinHeight();
                        double maxHeight = bp.getDefaultMaxHeight();
                        Point3d partCenter = new Point3d( //
                                (bounds.getxMax() + bounds.getxMin()) / 2d,//
                                minHeight, //
                                -(bounds.getyMax() + bounds.getyMin()) / 2d);

                        editorHeight.setEditorOrigin(partCenter);

                        editorHeight.setLength(maxHeight);

                        updatedEditors.add(editorHeight);
                    }

                    /*
                     * Check if given editor don't exist already, if so we need
                     * to update it.
                     */
                    PartValueEditor editorRoofHeight = findEditor(primitive, editors, "roof:height");
                    if (editorRoofHeight == null) {
                        // don't exist we create fresh one
                        editorRoofHeight = new PartValueEditor(primitive, "roof:height") {
                            boolean changeRoofShape = false;

                            @Override
                            public void preview(double value) {
                                // sets height to current building part
                                getBuildingPart().getRoof().setRoofHeight(value);

                                if (getBuildingPart().getRoof() instanceof DormerRoofModel) {
                                    DormerRoofModel model = (DormerRoofModel) getBuildingPart().getRoof();
                                    if (model.getRoofType() == null || RoofTypeAliasEnum.FLAT.equals(model.getRoofType())) {

                                        changeRoofShape = true;
                                        model.setRoofType(RoofTypeAliasEnum.GABLED);
                                    }
                                }

                                generatePreview();
                            };

                            @Override
                            protected void updateTags(AbstractMap<String, String> tags) {
                                if (changeRoofShape) {
                                    tags.put("roof:shape", "gabled");
                                }
                            }

                        };
                        editorRoofHeight.setEditorType(EditorType.BOX_SMALL);
                        editorRoofHeight.setVector(new Vector3d(0, -1, 0));
                    }
                    /*
                     * We need to re-setupeditorRoofHeight building part. It can
                     * change when building is change after change made in JOSM
                     * dataset. It is not important when preview is generated.
                     */
                    editorRoofHeight.setBuildingPart(bp);

                    final Bbox2d bounds = calcBounds(bp);

                    double roofHeight = bp.getRoof().getRoofHeight();

                    final CachePoint3dProvider roofHeightCenter = new CachePoint3dProvider() {

                        @Override
                        public void beforeProvide(Point3d point) {
                            point.x = (bounds.getxMax() + bounds.getxMin()) / 2d;
                            point.y = bp.getDefaultMaxHeight();
                            point.z = -(bounds.getyMax() + bounds.getyMin()) / 2d;

                        }
                    };

                    editorRoofHeight.setEditorOrigin(roofHeightCenter);

                    editorRoofHeight.setLength(roofHeight);

                    updatedEditors.add(editorRoofHeight);

                }
            }
            editors = updatedEditors;
        }

        BoundsFactory bf = new BoundsFactory();

        List<BuildingPart> parts = bm.getParts();
        if (parts != null) {
            for (BuildingPart bp : parts) {
                List<WallPart> wallParts = bp.getWall().getWallParts();
                for (WallPart wp : wallParts) {
                    for (WallNode wn : wp.getNodes()) {

                        Point2d p = wn.getPoint();

                        bf.addPoint(p.x, bp.getDefaultMinHeight(), -p.y);
                        bf.addPoint(p.x, bp.getDefaultMaxHeight(), -p.y);
                    }
                }
            }
        }

        final Bounds bounds = bf.toBounds();
        this.bounds = bounds;

        return Arrays.asList((Selection) new ModelSelection(bounds.getCenter(), bounds.getRadius()) {

            @Override
            public List<Editor> getEditors() {
                return editors;
            }

            @Override
            public Object getSource() {
                return Building.this;
            }

            @Override
            public double getRadius() {
                return bounds.getRadius();
            }

            @Override
            public Model getModel() {
                return Building.this.getModel();
            }
        });

    }

    private PartValueEditor findEditor(PrimitiveId primitive2, List<Editor> editors2, String string) {

        if (editors2 == null) {
            return null;
        }
        for (Editor editor : editors2) {
            if (editor instanceof PartValueEditor) {
                PartValueEditor e = (PartValueEditor) editor;

                if (primitive2.equals(e.getPrimitiveId()) && string.equals(e.getFildName())) {
                    return e;
                }
            }
        }
        return null;
    }

    private Bbox2d calcBounds(BuildingPart bp) {

        Bbox2d bbox = new Bbox2d();

        List<WallPart> wallParts = bp.getWall().getWallParts();
        for (WallPart wp : wallParts) {
            for (WallNode wn : wp.getNodes()) {

                Point2d p = wn.getPoint();

                bbox.addPoint(p);
            }
        }

        return bbox;
    }

    @Override
    public void draw(GL2 gl, Camera camera) {
        draw(gl, camera, false);
    }

    @Override
    public void draw(GL2 gl, Camera pCamera, boolean selected) {
        // XXX move draw debug do new method
        gl.glPushMatrix();

        Point3d position = getPosition();

        gl.glTranslated(position.x, position.y, position.z);

        modelRender.render(gl, model);

        if (debug != null && debug.getEdges() != null) {
            drawEdges(gl, debug.getEdges());
        }

        gl.glPopMatrix();
    }

    private void drawEdges(GL2 gl, List<LineSegment3d> edges) {

        // Lift up a little to avoid z-buffer problems
        gl.glTranslated(0, 0.1, 0);

        gl.glLineWidth(6);
        gl.glColor3fv(ROOF_EDGES_COLOR, 0);

        for (LineSegment3d line : edges) {

            gl.glBegin(GL.GL_LINES);

            Point3d begin = line.getBegin();
            Point3d end = line.getEnd();

            gl.glVertex3d(begin.x, begin.y, begin.z);
            gl.glVertex3d(end.x, end.y, end.z);

            gl.glEnd();
        }
    }

    @Override
    public List<ExportItem> export(ExportModelConf conf) {
        if (model == null) {
            buildWorldObject();
        }

        return Collections
                .singletonList(new ExportItem(model, new Point3d(getGlobalX(), 0, -getGlobalY()), new Vector3d(1, 1, 1)));
    }

    @Override
    public Model getModel() {
        return model;
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.model.AbstractModel#getSelection()
     */
    @Override
    public List<Selection> getSelection() {
        return selection;
    }

    @Override
    public Point3d getPosition() {
        return getPoint();
    }

    private static class BuildingDebugData {

        private List<BuildingDebugDrawer> debugParts = new ArrayList<BuildingDebugDrawer>();
        private List<LineSegment3d> edges;

        /**
         * @return the edges
         */
        public List<LineSegment3d> getEdges() {
            return edges;
        }

        /**
         * @param edges
         *            the edges to set
         */
        public void setEdges(List<LineSegment3d> edges) {
            this.edges = edges;
        }

        /**
         * @return the debugParts
         */
        public List<BuildingDebugDrawer> getDebugParts() {
            return debugParts;
        }

        /**
         * @param debugParts
         *            the debugParts to set
         */
        public void setDebugParts(List<BuildingDebugDrawer> debugParts) {
            this.debugParts = debugParts;
        }

    }

    @Override
    public void drawDebug(GL2 gl, Camera camera) {

        if (!modelRender.isDebugging()) {
            return;
        }

        gl.glPushMatrix();

        Point3d position = getPosition();

        gl.glTranslated(position.x, position.y, position.z);

        if (debug != null && debug.getEdges() != null) {
            drawEdges(gl, debug.getEdges());
        }

        gl.glPopMatrix();

        SelectionDrawUtil.drawSphereSelection(gl, this);
    }

    @Override
    public PrimitiveId getPrimitiveId() {
        return primitive;
    }
}
