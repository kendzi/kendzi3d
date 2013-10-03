package kendzi.josm.kendzi3d.jogl.model.roof.mk.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Point2d;

import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.TextureCacheServiceImpl;
import kendzi.jogl.texture.library.BuildingElementsTextureManager;
import kendzi.jogl.texture.library.OsmBuildingElementsTextureMenager;
import kendzi.jogl.texture.library.TextureLibraryService;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.model.building.builder.BuildingBuilder;
import kendzi.josm.kendzi3d.jogl.model.building.builder.BuildingOutput;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingModel;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.Wall;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallNode;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.BuildingNodeElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.EntranceBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.WindowBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofDebugOut;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.josm.kendzi3d.service.UrlReciverService;

import org.apache.log4j.Logger;

public class ExampleBuildingBuilderFrame extends BaseJoglFrame {
    /** Log. */
    private static final Logger log = Logger.getLogger(ExampleBuildingBuilderFrame.class);
    /**
     * Renderer of model.
     */
    private ModelRender modelRender;

    /**
     * Model of roof.
     */
    private Model model;

    private RoofDebugOut debug;

    private Object minHeight;
    private TextureLibraryStorageService textureLibraryStorageService;

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.model.roof.mk.ui.BaseJoglFrame#init(javax.media.opengl.GLAutoDrawable)
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);

        // this is required only for textures file finding. It is used in renderer do load files.
        // if textures are not used but only colored materials it is not required!
        // service to take files url's
        UrlReciverService fileUrlReciverService = new LocalResourceReciver();
        //FileUrlReciverService(locationOfResources);
        // cache for textures files.
        TextureCacheServiceImpl textureCacheService = new TextureCacheServiceImpl();
        textureCacheService.setFilter(true);

        // manual injection
        textureCacheService.setFileUrlReciverService(fileUrlReciverService);

        this.textureLibraryStorageService = new TextureLibraryService(fileUrlReciverService);

        // Set up of my model renderer to display my model
        this.modelRender = new ModelRender();
        // manual injection
        this.modelRender.setTextureCacheService(textureCacheService);

        // hire we create model mesh
        createRoofModel();
    }

    private void createRoofModel() {

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

        BuildingElementsTextureManager tm = new OsmBuildingElementsTextureMenager(this.textureLibraryStorageService);
        BuildingOutput buildModel = BuildingBuilder.buildModel(buildingModel, tm);

        this.model = buildModel.getModel();


        //        If you don't want use textures here replace texture key with material color like this:
        //          (XXX my renderer portably crash with materials);
        //
        //        for (Material material : this.model.materials) {
        //            if ("MY_MATERIAL_NAME_1".equals(material.strFile) {
        //                material.strFile = null;
        //                material.ambientColor = XXX;
        //                material.YYY = XXX;
        //            }
        //        }
        //        this.model.useTexture = false;


    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.model.roof.mk.ui.BaseJoglFrame#display(javax.media.opengl.GLAutoDrawable)
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        super.display(drawable);
        
        GL2 gl = drawable.getGL().getGL2();
        this.modelRender.render(gl, this.model);
    }



    public static void main(String[] args) {

        // WARNING: To run you need to set up native JOGL library for jogl.all.jar !!!

        ExampleBuildingBuilderFrame sj = new ExampleBuildingBuilderFrame();

        sj.initUi();

    }

}
