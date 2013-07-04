package kendzi.josm.kendzi3d.jogl.model.roof.mk.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Point2d;

import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.Wall;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallNode;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallPart;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.DormerRoofBuilder;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofDebugOut;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.josm.kendzi3d.service.TextureCacheService;
import kendzi.josm.kendzi3d.service.UrlReciverService;
import kendzi.josm.kendzi3d.service.impl.FileUrlReciverService;

public class ExampleRoofJoglFrame extends BaseJoglFrame {

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
        String locationOfResources = "c:/java/workspace/sun/sun/kendzi.josm.plugin3d";
        // service to take files url's
        UrlReciverService fileUrlReciverService = new FileUrlReciverService(locationOfResources);
        // cache for textures files.
        TextureCacheService textureCacheService = new TextureCacheService();
        textureCacheService.setFilter(true);

        // manual injection
        textureCacheService.setFileUrlReciverService(fileUrlReciverService);

        // Set up of my model renderer to display my model
        this.modelRender = new ModelRender();
        // manual injection
        this.modelRender.setTextureCacheService(textureCacheService);

        // hire we create model mesh
        createRoofModel();
    }

    private void createRoofModel() {

        // Assume we have building size:
        // - height: 15m
        // - width: 10m
        // - depth: 5m
        // with is 10m from world center :)

        double height = 15;

        List<WallNode> nodes = new ArrayList<WallNode>();
        nodes.add(new WallNode(new Point2d(10, 0), null));
        nodes.add(new WallNode(new Point2d(20, 0), null));
        nodes.add(new WallNode(new Point2d(20, 5), null));
        nodes.add(new WallNode(new Point2d(10, 5), null));

        WallPart wp = new WallPart();
        wp.setNodes(nodes);

//        List<Point2d> border = new ArrayList<Point2d>();
//
//        border.add(new Point2d(10, 0));
//        border.add(new Point2d(20, 0));
//        border.add(new Point2d(20, 5));
//        border.add(new Point2d(10, 5));
//
//        border.add(border.get(0));
        Wall w = new Wall();
        w.setWallParts(Arrays.asList(wp));


        BuildingPart bp = new BuildingPart();
        bp.setWall(w);

        DormerRoofModel roof = new DormerRoofModel();
        bp.setRoof(roof);

//        roof.setBuilding(new PolygonList2d(border));

        // Roof type
        roof.setRoofType(RoofTypeAliasEnum.PYRAMIDAL);

        RoofTextureData rtd = new RoofTextureData();
        // it can be material with color ! (XXX my renderer portably crash with materials);
        // simply create different keys for texture and replace it before rendering like this:
        // rtd.setFacadeTextrure(new TextureData("MY_MATERIAL_NAME_1", 1, 1));
        rtd.setFacadeTexture(new TextureData("/textures/building_facade_plaster.png", 4, 2));
        rtd.setRoofTexture(new TextureData("/textures/building_roof_material_roofTiles.png", 3, 3));

        ModelFactory mf = ModelFactory.modelBuilder();

        RoofOutput roofOutput = DormerRoofBuilder.build(bp, height, mf, rtd);

        this.debug = roofOutput.getDebug();

        this.minHeight = height - roofOutput.getHeight();
        this.model = roofOutput.getModel();

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

        ExampleRoofJoglFrame sj = new ExampleRoofJoglFrame();

        sj.initUi();

    }

}
