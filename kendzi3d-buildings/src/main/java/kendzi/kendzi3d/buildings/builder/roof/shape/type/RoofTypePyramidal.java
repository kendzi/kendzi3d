/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import com.dreizak.miniball.highdim.Miniball;
import com.dreizak.miniball.model.ArrayPointSet;

import java.util.Arrays;
import java.util.List;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.MeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.builder.height.BetweenLinesHeightCalculator;
import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.math.geometry.Graham;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonUtil;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.polygon.split.PolygonSplitHelper;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * Roof type Pyramidal.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofTypePyramidal extends RectangleRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRectangleRoof(RectangleRoofTypeConf conf) {

        double h1 = getHeightMeters(conf.getMeasurements(), MeasurementKey.HEIGHT_1, 2.5d);

        return build(conf.getBuildingPolygon(), conf.getRecHeight(), conf.getRecWidth(), conf.getRectangleContur(), h1,
                conf.getRoofTextureData());

    }

    /**
     * @param buildingPolygon
     *            the building polygon
     * @param recHeight
     *            the bonding rectangle height
     * @param recWidth
     *            the bonding rectangle width
     * @param rectangleContur
     *            the bonding rectangle
     * @param h1
     *            the roof height
     * @param roofTextureData
     *            the roof texture data
     * @return the roof output
     */
    protected RoofTypeOutput build(PolygonWithHolesList2d buildingPolygon, double recHeight, double recWidth,
            Vector2dc[] rectangleContur, double h1, RoofMaterials roofTextureData) {

        MeshFactory meshBorder = createFacadeMesh(roofTextureData);
        MeshFactory meshRoof = createRoofMesh(roofTextureData);

        TextureData roofTexture = roofTextureData.getRoof().getTextureData();

        List<Vector2dc> outlineList = buildingPolygon.getOuter().getPoints();

        if (PolygonUtil.isClockwisePolygon(outlineList)) {
            outlineList = PolygonList2d.reverse(outlineList);
        }

        List<Vector2dc> outlineConvexHull = Graham.grahamScan(outlineList);

        Vector2dc middlePoint = findMiddlePoint(outlineConvexHull);

        LinePoints2d[] lines = createLines(outlineConvexHull, middlePoint);
        MultiPolygonList2d[] mp = createMP(outlineConvexHull, outlineList, middlePoint);
        Plane3d[] planes = createPlanes(outlineConvexHull, h1, middlePoint);

        Vector3dc[] roofLine = createRoofLines(outlineConvexHull);

        double[] textureOffset = createTextureOffset(outlineConvexHull);

        for (int i = 0; i < mp.length; i++) {

            MeshFactoryUtil.addPolygonToRoofMesh(meshRoof, mp[i], planes[i], roofLine[i], roofTexture, textureOffset[i], 0);
        }

        HeightCalculator hc = new BetweenLinesHeightCalculator(lines, planes);

        RoofTypeOutput rto = new RoofTypeOutput();
        rto.setHeight(h1);
        rto.setHeightCalculator(hc);
        rto.setMesh(Arrays.asList(meshBorder, meshRoof));

        return rto;
    }

    /**
     * Calculates center of given polygon.
     * 
     * @param points
     *            the list of points to calculate center
     * @return the center point of outline
     */
    public static Vector2d findMiddlePoint(List<Vector2dc> points) {

        final ArrayPointSet pts = new ArrayPointSet(2, points.size());

        for (int i = 0; i < points.size(); i++) {
            Vector2dc point2d = points.get(i);
            pts.set(i, 0, point2d.x());
            pts.set(i, 1, point2d.y());
        }

        Miniball mb = new Miniball(pts);

        double[] center = mb.center();

        return new Vector2d(center);
    }

    /**
     * Creates array of texture offsets for each point in polygon.
     * 
     * @param polygon
     *            the list of points in polygon
     * @return the array of offsets for texture in given point
     */
    public static double[] createTextureOffset(List<Vector2dc> polygon) {
        int size = polygon.size();
        double[] ret = new double[size];

        double distance = 0;

        for (int i = 0; i < size; i++) {
            ret[i] = distance;

            Vector2dc p1 = polygon.get(i);
            Vector2dc p2 = polygon.get((i + 1) % size);

            distance += p1.distance(p2);
        }

        return ret;
    }

    /**
     * Creates vector for each edge in polygon.
     * 
     * @param polygon
     *            the polygon
     * @return the array of vectors for each edge in polygon
     */
    public static Vector3dc[] createRoofLines(List<Vector2dc> polygon) {

        int size = polygon.size();
        Vector3dc[] ret = new Vector3d[size];
        for (int i = 0; i < size; i++) {
            Vector2dc p1 = polygon.get(i);
            Vector2dc p2 = polygon.get((i + 1) % size);

            ret[i] = new Vector3d(p2.x() - p1.x(), 0, -(p2.y() - p1.y()));
        }

        return ret;
    }

    private LinePoints2d[] createLines(List<Vector2dc> polygon, Vector2dc middlePoint) {
        int size = polygon.size();
        LinePoints2d[] ret = new LinePoints2d[size];
        for (int i = 0; i < size; i++) {
            Vector2dc p1 = polygon.get(i);

            LinePoints2d l = new LinePoints2d(middlePoint, p1);

            ret[i] = l;
        }

        return ret;
    }

    /**
     * Creates planes for each edge in polygon.
     * 
     * @param polygon
     *            the polygon
     * @param height
     *            the height of roof
     * @param m
     *            the middle point
     * @return the planes for each polygon edge
     */
    public static Plane3d[] createPlanes(List<Vector2dc> polygon, double height, Vector2dc m) {

        int size = polygon.size();
        Plane3d[] ret = new Plane3d[size];
        // Point3d center = new Point3d(m.x(), height, -m.y());
        for (int i = 0; i < size; i++) {
            Vector2dc p1 = polygon.get(i);
            Vector2dc p2 = polygon.get((i + 1) % size);

            Vector3dc v2 = new Vector3d(p2.x() - m.x(), -height, -(p2.y() - m.y()));
            Vector3dc v1 = new Vector3d(p1.x() - m.x(), -height, -(p1.y() - m.y())).cross(v2).normalize();

            Vector3dc point = new Vector3d(p1.x(), 0, -p1.y());
            // Point3d point = new Point3d(p2.x(), 0, -p2.y());

            Plane3d p = new Plane3d(point, v1);

            ret[i] = p;
        }

        return ret;
    }

    /**
     * Create roof surface polygons.
     * 
     * @param outlineConvexHull
     * @param outlineList
     * @param outlinePolygon
     * @param middlePoint
     * @return
     */
    private MultiPolygonList2d[] createMP(List<Vector2dc> outlineConvexHull, List<Vector2dc> outlineList, Vector2dc middlePoint) {

        MultiPolygonList2d outlineMultiPolygon = new MultiPolygonList2d(new PolygonList2d(outlineList));

        int size = outlineConvexHull.size();
        MultiPolygonList2d[] ret = new MultiPolygonList2d[size];

        for (int i = 0; i < size; i++) {
            Vector2dc p1 = outlineConvexHull.get(i);
            Vector2dc p2 = outlineConvexHull.get((i + 1) % size);

            ret[i] = intersectionOfLeftSideOfMultipleCuts(outlineMultiPolygon, p2, middlePoint, p1);
        }

        return ret;
    }

    private static MultiPolygonList2d intersectionOfLeftSideOfMultipleCuts(MultiPolygonList2d polygons, Vector2dc... lines) {
        return PolygonSplitHelper.intersectionOfLeftSideOfMultipleCuts(polygons,
                PolygonSplitHelper.polygonalChaniToLineArray(lines));
    }
}
