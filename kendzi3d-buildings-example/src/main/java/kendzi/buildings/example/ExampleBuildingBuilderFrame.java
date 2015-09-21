package kendzi.buildings.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Point2d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.TextureCacheServiceImpl;
import kendzi.jogl.texture.library.BuildingElementsTextureManager;
import kendzi.jogl.texture.library.OsmBuildingElementsTextureMenager;
import kendzi.jogl.texture.library.TextureLibraryService;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.jogl.ui.BaseJoglFrame;
import kendzi.kendzi3d.buildings.builder.BuildingBuilder;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.Measurement;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.kendzi3d.buildings.model.BuildingModel;
import kendzi.kendzi3d.buildings.model.BuildingPart;
import kendzi.kendzi3d.buildings.model.Wall;
import kendzi.kendzi3d.buildings.model.WallNode;
import kendzi.kendzi3d.buildings.model.WallPart;
import kendzi.kendzi3d.buildings.model.element.BuildingNodeElement;
import kendzi.kendzi3d.buildings.model.element.EntranceBuildingElement;
import kendzi.kendzi3d.buildings.model.element.WindowBuildingElement;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRoofModel;
import kendzi.kendzi3d.buildings.model.roof.shape.RoofTypeAliasEnum;
import kendzi.kendzi3d.buildings.output.BuildingOutput;
import kendzi.kendzi3d.resource.inter.LocalResourceReciver;
import kendzi.kendzi3d.resource.inter.ResourceService;

public class ExampleBuildingBuilderFrame extends BaseJoglFrame {

    /**
     * Renderer of model.
     */
    private ModelRender modelRender;

    /**
     * Model of roof.
     */
    private Model model;

    /**
     * Texture library storage service.
     */
    private TextureLibraryStorageService textureLibraryStorageService;

    /**
     * {@inheritDoc}
     *
     * @see kendzi.buildings.example.BaseJoglFrame#init(com.jogamp.opengl.GLAutoDrawable)
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);

        /*
         * This is required only for textures file finding. Renderer use it to
         * load files with textures. If textures are not used but only colored
         * materials it is not required.
         */
        ResourceService resourceService = new LocalResourceReciver();

        // cache for textures files.
        TextureCacheServiceImpl textureCacheService = new TextureCacheServiceImpl();
        textureCacheService.setFilter(true);

        // Manual injection of dependencies.
        textureCacheService.setFileUrlReciverService(resourceService);

        textureLibraryStorageService = new TextureLibraryService(resourceService);

        // Set up of my model renderer to display my model
        modelRender = new ModelRender();

        // Manual injection of dependencies.
        modelRender.setTextureCacheService(textureCacheService);

        // Create model mesh.
        model = createRoofModel();
    }

    private Model createRoofModel() {

        // Assume we have building size:
        // - height: 8m
        // - width: 10m
        // - depth: 5m
        // with is 10m from world center :)

        double height = 8;

        List<WallNode> nodes = new ArrayList<WallNode>();
        nodes.add(new WallNode(new Point2d(10, 0), null));
        nodes.add(new WallNode(new Point2d(15, 0), Arrays.asList((BuildingNodeElement) new EntranceBuildingElement())));
        nodes.add(new WallNode(new Point2d(18, 0), Arrays.asList((BuildingNodeElement) new WindowBuildingElement())));
        nodes.add(new WallNode(new Point2d(20, 0), null));
        nodes.add(new WallNode(new Point2d(20, 5), null));
        nodes.add(new WallNode(new Point2d(10, 5), null));
        nodes.add(nodes.get(0));

        WallPart wp = new WallPart();
        wp.setNodes(nodes);

        Wall w = new Wall();
        w.setWallParts(Arrays.asList(wp));

        // materials names from library
        w.setFacadeMaterialType("brick");
        w.setRoofMaterialType("roof_tiles");

        BuildingPart bp = new BuildingPart();
        bp.setWall(w);
        bp.setMaxHeight(height);

        DormerRoofModel roof = new DormerRoofModel();
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());
        bp.setRoof(roof);

        // Roof type
        roof.setRoofType(RoofTypeAliasEnum.PYRAMIDAL);

        BuildingModel buildingModel = new BuildingModel();
        buildingModel.setParts(Arrays.asList(bp));

        BuildingElementsTextureManager tm = new OsmBuildingElementsTextureMenager(textureLibraryStorageService);
        BuildingOutput buildModel = BuildingBuilder.buildModel(buildingModel, tm);

        return buildModel.getModel();

        /*-
         * If you don't want use textures here replace texture key with material color like this:
         *          (XXX my renderer portably crash with materials);
         *
         *        for (Material material : this.model.materials) {
         *            if ("MY_MATERIAL_NAME_1".equals(material.strFile) {
         *                material.strFile = null;
         *                material.ambientColor = XXX;
         *                material.YYY = XXX;
         *            }
         *        }
         *        this.model.useTexture = false;
         *        */
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.buildings.example.BaseJoglFrame#display(com.jogamp.opengl.GLAutoDrawable)
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        super.display(drawable);

        GL2 gl = drawable.getGL().getGL2();

        modelRender.render(gl, model);
    }

    public static void main(String[] args) {

        ExampleBuildingBuilderFrame sj = new ExampleBuildingBuilderFrame();

        sj.initUi();
    }
}
