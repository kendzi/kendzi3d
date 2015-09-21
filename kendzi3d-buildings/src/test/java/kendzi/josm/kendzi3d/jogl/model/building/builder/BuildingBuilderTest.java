package kendzi.josm.kendzi3d.jogl.model.building.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.util.MeshTriangleUtil;
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
import kendzi.math.geometry.Triangle3d;
import kendzi.math.geometry.triangle.Triangle3dUtil;

import org.junit.Test;

public class BuildingBuilderTest {

    private static final String FLOOR_PART = "FloorPart";
    private static final String ROOF_TOP = "roof_top";
    private static final String ROOF_FACADE = "roof_facade";
    private static final String WALL_PART_0 = "WallPart: 0";
    private static final double EPSILON = 1e-10;

    @SuppressWarnings("javadoc")
    @Test
    public void buildSquareBuilding() {

        Wall wall = createSquareWall();

        DormerRoofModel roof = new DormerRoofModel();
        roof.setRoofType(RoofTypeAliasEnum.FLAT);
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());

        BuildingPart part = new BuildingPart();
        part.setWall(wall);
        part.setRoof(roof);

        BuildingModel buildingModel = new BuildingModel();
        buildingModel.setParts(Arrays.asList(part));

        BuildingOutput model = BuildingBuilder.buildModel(buildingModel, createMockTextureManager());

        assertNotNull(model);

        assertNotNull(model.getModel());

        assertEquals(set(WALL_PART_0, ROOF_FACADE, ROOF_TOP, FLOOR_PART), colectNames(model));
        assertEquals(32, sumAreaForName(model, WALL_PART_0), EPSILON);
        assertEquals(0, sumAreaForName(model, ROOF_FACADE), EPSILON);
        assertEquals(1, sumAreaForName(model, ROOF_TOP), EPSILON);
        assertEquals(1, sumAreaForName(model, FLOOR_PART), EPSILON);
    }

    private Wall createSquareWall() {
        WallNode n1 = new WallNode(new Point2d(1, 1), null);
        WallNode n2 = new WallNode(new Point2d(2, 1), null);
        WallNode n3 = new WallNode(new Point2d(2, 2), null);
        WallNode n4 = new WallNode(new Point2d(1, 2), null);

        List<WallNode> nodes = new ArrayList<WallNode>();
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        nodes.add(n1);

        WallPart wallPart = new WallPart();
        wallPart.setNodes(nodes);

        Wall wall = new Wall();
        wall.setWallParts(Arrays.asList(wallPart));
        return wall;
    }

    @SuppressWarnings("javadoc")
    @Test
    public void buildRecBuilding() {

        Wall wall = createRectWall();

        DormerRoofModel roof = new DormerRoofModel();
        roof.setRoofType(RoofTypeAliasEnum.FLAT);
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());

        BuildingPart part = new BuildingPart();
        part.setWall(wall);
        part.setRoof(roof);

        BuildingModel buildingModel = new BuildingModel();
        buildingModel.setParts(Arrays.asList(part));

        BuildingOutput model = BuildingBuilder.buildModel(buildingModel, createMockTextureManager());

        assertNotNull(model);

        assertNotNull(model.getModel());

        assertEquals(set(WALL_PART_0, ROOF_FACADE, ROOF_TOP, FLOOR_PART), colectNames(model));
        assertEquals(48, sumAreaForName(model, WALL_PART_0), EPSILON);
        assertEquals(0, sumAreaForName(model, ROOF_FACADE), EPSILON);
        assertEquals(2, sumAreaForName(model, ROOF_TOP), EPSILON);
        assertEquals(2, sumAreaForName(model, FLOOR_PART), EPSILON);
    }

    @SuppressWarnings("javadoc")
    @Test
    public void buildRecBuilding2v1() {

        Wall wall = createRectWall();

        DormerRoofModel roof = new DormerRoofModel();
        roof.setRoofType(RoofTypeAliasEnum.ROOF_TYPE2_1);
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());

        BuildingPart part = new BuildingPart();
        part.setWall(wall);
        part.setRoof(roof);

        BuildingModel buildingModel = new BuildingModel();
        buildingModel.setParts(Arrays.asList(part));

        BuildingOutput model = BuildingBuilder.buildModel(buildingModel, createMockTextureManager());

        assertNotNull(model);

        assertNotNull(model.getModel());

        assertEquals(set(WALL_PART_0, ROOF_FACADE, ROOF_TOP, FLOOR_PART), colectNames(model));
        assertEquals(35, sumAreaForName(model, WALL_PART_0), EPSILON);
        // FIXME this is bug!
        // assertEquals(0, sumAreaForName(model, "roof_facade"), EPSILON);
        assertEquals(8.261297173761164, sumAreaForName(model, ROOF_TOP), EPSILON);
        assertEquals(2, sumAreaForName(model, FLOOR_PART), EPSILON);
    }

    @SuppressWarnings("javadoc")
    @Test
    public void buildRecBuildingGabled() {
        // DEFAULT_BUILDING_HEIGHT = 8

        Wall wall = createRectWall();

        DormerRoofModel roof = new DormerRoofModel();
        roof.setRoofType(RoofTypeAliasEnum.GABLED);
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());

        BuildingPart part = new BuildingPart();
        part.setWall(wall);
        part.setRoof(roof);

        BuildingModel buildingModel = new BuildingModel();
        buildingModel.setParts(Arrays.asList(part));

        BuildingOutput model = BuildingBuilder.buildModel(buildingModel, createMockTextureManager());

        assertNotNull(model);
        assertNotNull(model.getModel());

        assertEquals(set(WALL_PART_0, ROOF_FACADE, ROOF_TOP, FLOOR_PART), colectNames(model));
        assertEquals(46.55662432702594, sumAreaForName(model, WALL_PART_0), EPSILON);
        assertEquals(0, sumAreaForName(model, ROOF_FACADE), EPSILON);
        assertEquals(2.3094010767585034, sumAreaForName(model, ROOF_TOP), EPSILON);
        assertEquals(2, sumAreaForName(model, FLOOR_PART), EPSILON);
    }

    @SuppressWarnings("javadoc")
    @Test
    public void buildRecBuilding3v0() {

        Wall wall = createRectWall();

        DormerRoofModel roof = new DormerRoofModel();
        roof.setRoofType(RoofTypeAliasEnum.ROOF_TYPE3_0);
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());

        BuildingPart part = new BuildingPart();
        part.setWall(wall);
        part.setRoof(roof);

        BuildingModel buildingModel = new BuildingModel();
        buildingModel.setParts(Arrays.asList(part));

        BuildingOutput model = BuildingBuilder.buildModel(buildingModel, createMockTextureManager());

        assertNotNull(model);
        assertNotNull(model.getModel());

        assertEquals(set(WALL_PART_0, ROOF_FACADE, ROOF_TOP, FLOOR_PART), colectNames(model));
        assertEquals(46.842925207882736, sumAreaForName(model, WALL_PART_0), EPSILON);
        assertEquals(0, sumAreaForName(model, ROOF_FACADE), EPSILON);
        assertEquals(2.424682579017192, sumAreaForName(model, ROOF_TOP), EPSILON);
        assertEquals(2, sumAreaForName(model, FLOOR_PART), EPSILON);
    }

    @SuppressWarnings("javadoc")
    @Test
    public void buildRecBuilding4v0() {

        Wall wall = createRectWall();

        DormerRoofModel roof = new DormerRoofModel();
        roof.setRoofType(RoofTypeAliasEnum.ROOF_TYPE4_0);
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());

        BuildingPart part = new BuildingPart();
        part.setWall(wall);
        part.setRoof(roof);

        BuildingModel buildingModel = new BuildingModel();
        buildingModel.setParts(Arrays.asList(part));

        BuildingOutput model = BuildingBuilder.buildModel(buildingModel, createMockTextureManager());

        assertNotNull(model);
        assertNotNull(model.getModel());

        assertEquals(set(WALL_PART_0, ROOF_FACADE, ROOF_TOP, FLOOR_PART), colectNames(model));
        assertEquals(36.333333333333336, sumAreaForName(model, WALL_PART_0), EPSILON);
        assertEquals(0, sumAreaForName(model, ROOF_FACADE), EPSILON);
        assertEquals(10.290026952170264, sumAreaForName(model, ROOF_TOP), EPSILON);
        assertEquals(2, sumAreaForName(model, FLOOR_PART), EPSILON);
    }

    @SuppressWarnings("javadoc")
    @Test
    public void buildRecBuilding4v2() {

        Wall wall = createRectWall();

        DormerRoofModel roof = new DormerRoofModel();
        roof.setRoofType(RoofTypeAliasEnum.ROOF_TYPE4_2);
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());

        BuildingPart part = new BuildingPart();
        part.setWall(wall);
        part.setRoof(roof);

        BuildingModel buildingModel = new BuildingModel();
        buildingModel.setParts(Arrays.asList(part));

        BuildingOutput model = BuildingBuilder.buildModel(buildingModel, createMockTextureManager());

        assertNotNull(model);
        assertNotNull(model.getModel());

        assertEquals(set(WALL_PART_0, ROOF_FACADE, ROOF_TOP, FLOOR_PART), colectNames(model));
        assertEquals(33.0, sumAreaForName(model, WALL_PART_0), EPSILON);
        assertEquals(0, sumAreaForName(model, ROOF_FACADE), EPSILON);
        assertEquals(11.924981000515517, sumAreaForName(model, ROOF_TOP), EPSILON);
        assertEquals(2, sumAreaForName(model, FLOOR_PART), EPSILON);
    }

    @SuppressWarnings("javadoc")
    @Test
    public void buildRecBuilding5v0() {

        Wall wall = createRectWall();

        DormerRoofModel roof = new DormerRoofModel();
        roof.setRoofType(RoofTypeAliasEnum.ROOF_TYPE5_0);
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());

        BuildingPart part = new BuildingPart();
        part.setWall(wall);
        part.setRoof(roof);

        BuildingModel buildingModel = new BuildingModel();
        buildingModel.setParts(Arrays.asList(part));

        BuildingOutput model = BuildingBuilder.buildModel(buildingModel, createMockTextureManager());

        assertNotNull(model);
        assertNotNull(model.getModel());

        assertEquals(set(WALL_PART_0, ROOF_FACADE, ROOF_TOP, FLOOR_PART), colectNames(model));
        assertEquals(45.78036128806451, sumAreaForName(model, WALL_PART_0), EPSILON);
        assertEquals(0, sumAreaForName(model, ROOF_FACADE), EPSILON);
        assertEquals(3.1365484905459398, sumAreaForName(model, ROOF_TOP), EPSILON);
        assertEquals(2, sumAreaForName(model, FLOOR_PART), EPSILON);
    }

    @SuppressWarnings("javadoc")
    @Test
    public void buildRecBuilding5v2() {

        Wall wall = createRectWall();

        DormerRoofModel roof = new DormerRoofModel();
        roof.setRoofType(RoofTypeAliasEnum.ROOF_TYPE5_2);
        roof.setMeasurements(new HashMap<MeasurementKey, Measurement>());

        BuildingPart part = new BuildingPart();
        part.setWall(wall);
        part.setRoof(roof);

        BuildingModel buildingModel = new BuildingModel();
        buildingModel.setParts(Arrays.asList(part));

        BuildingOutput model = BuildingBuilder.buildModel(buildingModel, createMockTextureManager());

        assertNotNull(model);
        assertNotNull(model.getModel());

        assertEquals(set(WALL_PART_0, ROOF_FACADE, ROOF_TOP, FLOOR_PART), colectNames(model));
        assertEquals(45.55291427061512, sumAreaForName(model, WALL_PART_0), EPSILON);
        assertEquals(0, sumAreaForName(model, ROOF_FACADE), EPSILON);
        assertEquals(3.132628613281237, sumAreaForName(model, ROOF_TOP), EPSILON);
        assertEquals(2, sumAreaForName(model, FLOOR_PART), EPSILON);
    }

    private Wall createRectWall() {
        WallNode n1 = new WallNode(new Point2d(1, 1), null);
        WallNode n2 = new WallNode(new Point2d(3, 1), null);
        WallNode n3 = new WallNode(new Point2d(3, 2), null);
        WallNode n4 = new WallNode(new Point2d(1, 2), null);

        List<WallNode> nodes = new ArrayList<WallNode>();
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        nodes.add(n1);

        WallPart wallPart = new WallPart();
        wallPart.setNodes(nodes);

        Wall wall = new Wall();
        wall.setWallParts(Arrays.asList(wallPart));
        return wall;
    }

    Set<String> set(String... names) {
        return new HashSet<String>(Arrays.asList(names));
    }

    private Set<String> colectNames(BuildingOutput model) {
        Set<String> ret = new HashSet<String>();
        for (Mesh mesh : model.getModel().mesh) {
            ret.add(mesh.name);
        }
        return ret;
    }

    private double sumAreaForName(BuildingOutput model, String name) {
        return area(collectNamedTriangles(name, model.getModel()));
    }

    private double area(List<Triangle3d> roofTriangles) {
        double area = 0;
        for (Triangle3d triangle : roofTriangles) {
            area += Triangle3dUtil.area(triangle.getPoint0(), triangle.getPoint1(), triangle.getPoint2());

        }
        return area;
    }

    private List<Triangle3d> collectNamedTriangles(String name, Model model) {

        List<Triangle3d> ret = new ArrayList<Triangle3d>();
        for (Mesh mesh : model.mesh) {

            if (!name.equals(mesh.name)) {
                continue;
            }

            List<Point3d> triangles = MeshTriangleUtil.toTriangles(mesh);

            ret.addAll(toTriangles(triangles));
        }
        return ret;
    }

    private Collection<? extends Triangle3d> toTriangles(List<Point3d> triangles) {

        List<Triangle3d> ret = new ArrayList<Triangle3d>();

        for (int i = 0; i < triangles.size(); i = i + 3) {
            ret.add(new Triangle3d(triangles.get(i), triangles.get(i + 1), triangles.get(i + 2)));
        }
        return ret;
    }

    private BuildingElementsTextureManager createMockTextureManager() {
        BuildingElementsTextureManager tm = new BuildingElementsTextureManager() {

            @Override
            public TextureData findTexture(TextureFindCriteria textureFindCriteria) {
                return new TextureData("test.jpg", 2, 2);
            }
        };
        return tm;
    }
}
