package kendzi.kendzi3d.collada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.BuildingElementsTextureManager;
import kendzi.jogl.texture.library.TextureFindCriteria;
import kendzi.kendzi3d.buildings.builder.BuildingBuilder;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.Measurement;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.kendzi3d.buildings.model.BuildingModel;
import kendzi.kendzi3d.buildings.model.BuildingPart;
import kendzi.kendzi3d.buildings.model.Wall;
import kendzi.kendzi3d.buildings.model.WallNode;
import kendzi.kendzi3d.buildings.model.WallPart;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRoofModel;
import kendzi.kendzi3d.buildings.model.roof.shape.RoofTypeAliasEnum;
import kendzi.kendzi3d.buildings.output.BuildingOutput;

import org.junit.Test;

public class ColladaExportTest {

    @Test
    public void boxTest() throws Throwable {

        Model model = createBoxModel();

        ColladaExport exporter = new ColladaExport();

        exporter.addModel(model);

        exporter.marsall("test.dae");

    }

    @Test
    public void buildingTest() throws Throwable {

        Model model = createBuildingModel();

        ColladaExport exporter = new ColladaExport();

        exporter.addModel(model);

        exporter.marsall("test.dae");
        //        System.out.println(marshaller);
        //   exporter.saveFile("/multiText/test.dae", marshaller);
    }

    /**
     * @return
     */
    public static Model createBuildingModel() {

        WallPart wp = new WallPart();
        List<WallNode> nodes = new ArrayList<WallNode>();
        wp.setNodes(nodes);
        nodes.add(new WallNode(new Point2d(0,0), null));
        nodes.add(new WallNode(new Point2d(10,0), null));
        nodes.add(new WallNode(new Point2d(10.1,10), null));
        nodes.add(new WallNode(new Point2d(0,10), null));
        nodes.add(new WallNode(new Point2d(0,0), null));

        Wall wall = new Wall();
        List<WallPart> wallParts = new ArrayList<WallPart>();
        wall.setWallParts(wallParts);
        wallParts.add(wp);

        BuildingPart bp = new BuildingPart();
        bp.setWall(wall);
        DormerRoofModel roof = new DormerRoofModel();
        roof.setRoofType(RoofTypeAliasEnum.GABLED);
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());
        bp.setRoof(roof);

        BuildingModel bm = new BuildingModel();
        List<BuildingPart> parts = new ArrayList<BuildingPart>();
        bm.setParts(parts);
        parts.add(bp);

        BuildingElementsTextureManager tm = new BuildingElementsTextureManager() {

            @Override
            public TextureData findTexture(
                    TextureFindCriteria pTextureFindCriteria) {
                return new TextureData("test.jpg", 2, 2);
            }
        };

        BuildingOutput buildModel = BuildingBuilder.buildModel(bm, tm);
        Model ret = buildModel.getModel();
        //        ret.materialID = 0;
        //        ret.hasTexture = true;
        return ret;
    }

    /**
     * @return
     */
    public static Model createBoxModel() {
        ModelFactory mf = ModelFactory.modelBuilder();
        Material m = new Material();
        m.getTexturesComponent().add("test.jpg");
        mf.addMaterial(m);

        MeshFactory cubeMesh = MeshFactoryUtil.cubeMesh(new Point3d());
        cubeMesh.materialID = 0;
        cubeMesh.hasTexture = true;
        mf.addMesh(cubeMesh);


        Model model = mf.toModel();
        model.useLight = true;
        model.useTexture = true;
        return model;
    }
}
