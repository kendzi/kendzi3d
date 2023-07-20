/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.buildings.builder.roof.lines;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofOutput;
import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.height.SegmentHeight;
import kendzi.kendzi3d.buildings.builder.roof.shape.type.RoofTypeUtil;
import kendzi.kendzi3d.buildings.model.BuildingPart;
import kendzi.kendzi3d.buildings.model.BuildingUtil;
import kendzi.kendzi3d.buildings.model.roof.lines.RoofLinesModel;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.Triangle2d;
import kendzi.math.geometry.line.LineSegment2d;
import kendzi.math.geometry.line.LineSegment3d;
import kendzi.math.geometry.point.Vector3dUtil;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.triangulate.Poly2TriUtil;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Roof builder for roof described using RoofLines tagging schema.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class RoofLinesBuildier {
    /** Log. */
    private static final Logger log = LoggerFactory.getLogger(RoofLinesBuildier.class);

    /**
     * Builds roof. Roof is build into given model factory.
     *
     * @param bp
     *            building part
     * @param maxHeight
     *            maximal height
     * @param mf
     *            model factory
     * @param roofTextureData
     *            roof texture data
     * @param roofColor
     *            roof color
     * @return debbug infromation about builded roof
     */
    public static RoofOutput build(BuildingPart bp, double maxHeight, ModelFactory mf, TextureData roofTextureData,
            Color roofColor) {

        if (!(bp.getRoof() instanceof RoofLinesModel)) {
            throw new RuntimeException("wrong type of roof model, should be RoofLinesModel");
        }

        RoofLinesModel roof = (RoofLinesModel) bp.getRoof();
        PolygonWithHolesList2d polygon = BuildingUtil.buildingPartToPolygonWithHoles(bp);

        final Map<Vector2dc, Double> heights = normalizeRoofHeights(maxHeight, roof.getRoofHeight(), roof.getHeights());

        PolygonList2d outer = polygon.getOuter();
        Collection<PolygonList2d> holes = polygon.getInner();

        Collection<LineSegment2d> segments = roof.getInnerSegments();

        MeshFactory roofMesh = createRoofMesh(mf, roofTextureData, roofColor);
        // XXX
        MeshFactory outlineMesh = createRoofMesh(mf, roofTextureData, roofColor);

        List<Triangle2d> triangles = Poly2TriUtil.triangulate(outer, holes, segments, Collections.emptyList());

        Vector3dc up = new Vector3d(0d, 1d, 0d);
        for (Triangle2d triangle : triangles) {
            Vector2dc p1 = triangle.getP1();
            Vector2dc p2 = triangle.getP2();
            Vector2dc p3 = triangle.getP3();

            double h1 = getHeight(heights, p1);
            double h2 = getHeight(heights, p2);
            double h3 = getHeight(heights, p3);

            Vector3dc pp1 = new Vector3d(p1.x(), h1, -p1.y());
            Vector3dc pp2 = new Vector3d(p2.x(), h2, -p2.y());
            Vector3dc pp3 = new Vector3d(p3.x(), h3, -p3.y());

            Vector3dc n = Vector3dUtil.fromTo(pp1, pp2).cross(Vector3dUtil.fromTo(pp1, pp3)).normalize();

            Vector3dc rl = up.cross(n, new Vector3d());
            // XXX

            MultiPolygonList2d topMP = new MultiPolygonList2d(new PolygonList2d(p1, p2, p3));
            Plane3d planeTop = new Plane3d(pp1, n);

            // FIXME there is no need in converting to Multipolygon, it should
            // be done in different way
            MeshFactoryUtil.addPolygonToRoofMesh(roofMesh, topMP, planeTop, rl, roofTextureData);

        }

        HeightCalculator hc = (p1, p2) -> Collections
                .singletonList(new SegmentHeight(p1, getHeight(heights, p1), p2, getHeight(heights, p2)));

        double minHeight = maxHeight - roof.getRoofHeight();

        RoofTypeUtil.makeWallsFromHeightCalculator(outer.getPoints(), hc, minHeight, outlineMesh, roofTextureData);

        RoofOutput ro = new RoofOutput();
        ro.setHeight(roof.getRoofHeight());
        ro.setHeightCalculator(hc);
        ro.setEdges(createEdgesDebug(segments, heights));

        return ro;
    }

    /**
     * @param heights
     * @param p1
     * @return
     */
    public static double getHeight(final Map<Vector2dc, Double> heights, Vector2dc p1) {
        Double height = heights.get(p1);
        if (height == null) {
            log.error("unmaped height for point: " + p1);
            return -1;
        }
        return height;
    }

    private static Map<Vector2dc, Double> normalizeRoofHeights(double maxHeight, double roofHeight,
            Map<Vector2dc, Double> heights) {
        Map<Vector2dc, Double> ret = new HashMap<>();

        double cullisHeight = maxHeight - roofHeight;
        for (Entry<Vector2dc, Double> entry : heights.entrySet()) {

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

    private static MeshFactory createRoofMesh(ModelFactory mf, TextureData td, Color color) {

        Material mat = MaterialFactory.createTextureColorMaterial(td.getTex0(), color);

        int materialIndex = mf.addMaterial(mat);

        MeshFactory meshRoof = mf.addMesh("roof_top");

        meshRoof.materialID = materialIndex;
        meshRoof.hasTexture = true;

        return meshRoof;
    }

    private static List<LineSegment3d> createEdgesDebug(Collection<LineSegment2d> segments, Map<Vector2dc, Double> heights) {

        List<LineSegment3d> ret = new ArrayList<>();

        if (segments == null) {
            return ret;
        }

        for (LineSegment2d line : segments) {

            double heightBegin = RoofLinesBuildier.getHeight(heights, line.getBegin());
            double heightEnd = RoofLinesBuildier.getHeight(heights, line.getEnd());
            Vector3dc begin = new Vector3d(line.getBegin().x(), heightBegin, -line.getBegin().y());
            Vector3dc end = new Vector3d(line.getEnd().x(), heightEnd, -line.getEnd().y());

            ret.add(new LineSegment3d(begin, end));

        }
        return ret;
    }

}
