package kendzi.josm.kendzi3d.jogl.model.building.builder.roof;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingUtil;
import kendzi.josm.kendzi3d.jogl.model.building.model.roof.RoofLinesModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofTypeUtil;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.SegmentHeight;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.wall.HeightCalculator;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangle2d;
import kendzi.math.geometry.line.LineSegment2d;
import kendzi.math.geometry.point.Vector3dUtil;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.triangulate.Poly2TriUtil2;

import org.apache.log4j.Logger;

public class RoofLinesBuildier {
    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofLinesBuildier.class);

    public static RoofOutput build(BuildingPart bp, double maxHeight, ModelFactory mf, TextureData roofTextureData,
            Color roofColor) {

        if (!(bp.getRoof() instanceof RoofLinesModel)) {
            throw new RuntimeException("wrong type of roof model, should be RoofLinesModel");
        }


        RoofLinesModel roof =  (RoofLinesModel) bp.getRoof();
        PolygonWithHolesList2d polygon = BuildingUtil.buildingPartToPolygonWithHoles(bp);

        final Map<Point2d, Double> heights = normalizeRoofHeights(maxHeight, roof.getRoofHeight(), roof.getHeights());


        PolygonList2d outer = polygon.getOuter();
        Collection<PolygonList2d> holes = polygon.getInner();

        Collection<LineSegment2d> segments = roof.getInnerSegments();

        MeshFactory roofMesh = createRoofMesh(mf, roofTextureData, roofColor);
        //XXX
        MeshFactory outlineMesh = createRoofMesh(mf, roofTextureData, roofColor);

        List<Triangle2d> triangles = Poly2TriUtil2.triangulate(outer, holes, segments,
                Collections.<Point2d> emptyList());

        Vector3d up = new Vector3d(0d, 1d, 0d);
        for (Triangle2d triangle : triangles) {
            Point2d p1 = triangle.getP1();
            Point2d p2 = triangle.getP2();
            Point2d p3 = triangle.getP3();

            double h1 = heights.get(p1);
            double h2 = heights.get(p2);
            double h3 = heights.get(p3);

            Point3d pp1 = new Point3d(p1.x, h1, -p1.y);
            Point3d pp2 = new Point3d(p2.x, h2, -p2.y);
            Point3d pp3 = new Point3d(p3.x, h3, -p3.y);

            Vector3d n = Vector3dUtil.fromTo(pp1, pp2);
            n.cross(n, Vector3dUtil.fromTo(pp1, pp3));
            //Vector3d n = new Vector3d(p2.x - p1.x, h2 - h1, -(p2.y - p1.y));
            //n.cross(n, new Vector3d(p3.x - p1.x, h3 - h1, -(p3.y - p1.y)));
            n.normalize();

            Vector3d rl = new Vector3d();
            //XXX
            rl.cross(up, n);



            MultiPolygonList2d topMP = new MultiPolygonList2d(new PolygonList2d(p1, p2, p3));
            Plane3d planeTop = new Plane3d(pp1, n);

            //FIXME there is no need in converting to Multipolygon, it should be done in different way
            MeshFactoryUtil.addPolygonToRoofMesh(roofMesh, topMP, planeTop, rl, roofTextureData);

        }

        HeightCalculator hc = new HeightCalculator() {

            @Override
            public SegmentHeight[] height(Point2d p1, Point2d p2) {

                return new SegmentHeight[] { new SegmentHeight(p1, heights.get(p1), p2, heights.get(p2)) };
            }
        };

        double minHeight = maxHeight - roof.getRoofHeight();

        RoofTypeUtil.makeWallsFromHeightCalculator(outer.getPoints(), hc, minHeight,  outlineMesh, roofTextureData);

        RoofOutput ro = new RoofOutput();
        ro.setHeight(roof.getRoofHeight());
        ro.setHeightCalculator(hc);

        return ro;
    }

    private static Map<Point2d, Double> normalizeRoofHeights(double maxHeight, double roofHeight, Map<Point2d, Double> heights) {
        Map<Point2d, Double> ret = new HashMap<Point2d, Double>();

        double cullisHeight = maxHeight - roofHeight;
        for (Entry<Point2d, Double> entry : heights.entrySet()) {

            Double value = entry.getValue();
            if (value < 0) {
                log.error("roof height map can't have value less then 0");
                value = 0d;
            }
            if (value > roofHeight) {
                log.error("roof height map can't have value greter then roof height");
                value = roofHeight;
            }

            ret.put(entry.getKey(), cullisHeight + value);
        }

        return ret;
    }

    protected static MeshFactory createRoofMesh( ModelFactory mf, TextureData td, Color color) {

        Material mat = MaterialFactory.createTextureColorMaterial(td.getTex0(), color);

        int materialIndex = mf.addMaterial(mat);

        MeshFactory meshRoof = mf.addMesh("roof_top");

        //        TextureData roofTexture = pRoofTextureData.getRoofTexture();
        //        Material roofMaterial = MaterialFactory.createTextureMaterial(roofTexture.getFile());
        //        int roofMaterialIndex = model.addMaterial(roofMaterial);

        meshRoof.materialID = materialIndex;
        meshRoof.hasTexture = true;

        return meshRoof;
    }

}
