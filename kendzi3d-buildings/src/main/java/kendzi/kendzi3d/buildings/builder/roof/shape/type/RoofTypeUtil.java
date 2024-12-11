/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.ArrayList;
import java.util.List;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.StripMeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.height.SegmentHeight;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonUtil;
import kendzi.math.geometry.polygon.split.EnrichPolygonalChainUtil;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoofTypeUtil {
    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(RoofTypeUtil.class);

    /**
     * Make mesh of vertical wall e.g. It is wall under the roof. The wall base is
     * on zero height.
     * 
     * 
     * @param stripPoints
     *            the strip points
     * @param stripPointHeights
     *            the strip points heights
     * @param mesh
     *            the mesh
     * @param textureData
     *            the texture data
     */
    public static void makeRoofBorderMesh(List<Vector2dc> stripPoints, List<Double> stripPointHeights, MeshFactory mesh,
            TextureData textureData) {

        makeRoofBorderMesh(stripPoints, 0, stripPointHeights, mesh, textureData);
    }

    /**
     * Make mesh of vertical wall e.g. It is wall under the roof.
     *
     * @param stripPoints
     *            the strip points
     * @param minHeight
     *            the strip minimal height
     * @param stripPointHeights
     *            the strip points heights
     * @param mesh
     *            the mesh
     * @param textureData
     *            the texture data
     */
    public static void makeRoofBorderMesh(List<Vector2dc> stripPoints, double minHeight, List<Double> stripPointHeights,
            MeshFactory mesh, TextureData textureData) {

        StripMeshFactoryUtil.verticalStripMesh(stripPoints, //
                new StripMeshFactoryUtil.ConstHeightProvider(minHeight), //
                new StripMeshFactoryUtil.ListHeightProvider(stripPointHeights), //
                mesh, //
                textureData, //
                true, //
                !PolygonUtil.isClockwisePolygon(stripPoints));
    }

    /**
     * Splits polygon by lines. Adds extra points in crossing places.
     *
     * @param polygon
     *            polygon to split by lines
     * @param lines
     *            lines to split polygon
     * @return polygon with extra points on crossing places
     */
    public static List<Vector2dc> splitBorder(PolygonList2d polygon, LinePoints2d... lines) {

        List<Vector2dc> splitPolygon = new ArrayList<>(polygon.getPoints());

        for (LinePoints2d line : lines) {
            splitPolygon = EnrichPolygonalChainUtil.enrichClosedPolygonalChainByLineCrossing(splitPolygon, line);
        }

        return splitPolygon;
    }

    /**
     * This is temporary method. It group code for wall generation under roof
     * gutters. Walls are generated from building_max_height - roof_height to
     * building_max_height. This code will be removed in future.
     *
     * @param wallPoints
     *            the wall points
     * @param heightCalculator
     *            the maximal height of wall points
     * @param minHeight
     *            the minimal height of wall points
     * @param mesh
     *            the mesh
     * @param textureData
     *            the texture data
     */
    public static void makeWallsFromHeightCalculator(List<Vector2dc> wallPoints, HeightCalculator heightCalculator,
            double minHeight, MeshFactory mesh, TextureData textureData) {

        boolean isClockwisePolygon = !PolygonUtil.isClockwisePolygon(wallPoints);

        for (int i = 0; i < wallPoints.size(); i++) {
            Vector2dc p1 = wallPoints.get(i);
            Vector2dc p2 = wallPoints.get((i + 1) % wallPoints.size());

            List<SegmentHeight> height2 = heightCalculator.height(p1, p2);

            for (SegmentHeight height : height2) {

                List<Vector2dc> segment = new ArrayList<>(2);
                List<Double> segmentHeights = new ArrayList<>(2);

                SegmentHeight segmentHeight = height;

                segment.add(segmentHeight.getBegin());
                segment.add(segmentHeight.getEnd());

                segmentHeights.add(segmentHeight.getBeginHeight());
                segmentHeights.add(segmentHeight.getEndHeight());

                StripMeshFactoryUtil.verticalStripMesh(segment, //
                        new StripMeshFactoryUtil.ConstHeightProvider(minHeight), //
                        new StripMeshFactoryUtil.ListHeightProvider(segmentHeights), //
                        mesh, //
                        textureData, //
                        false, //
                        isClockwisePolygon);
            }
        }
    }

    /**
     * Find minimal rectangle containing list of points. Save as list of 3d points
     * to display.
     *
     * XXX this should by changed!
     *
     * @param polygon
     *            list of points
     * @param height
     *            height
     * @return minimal rectangle
     */
    public static List<Vector3dc> findRectangle(List<Vector2dc> polygon, double height) {

        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;

        for (Vector2dc p : polygon) {
            if (minx > p.x()) {
                minx = p.x();
            }
            if (miny > p.y()) {
                miny = p.y();
            }
            if (maxx < p.x()) {
                maxx = p.x();
            }
            if (maxy < p.y()) {
                maxy = p.y();
            }
        }

        List<Vector3dc> rect = new ArrayList<>();
        rect.add(new Vector3d(minx, height, -miny));
        rect.add(new Vector3d(minx, height, -maxy));
        rect.add(new Vector3d(maxx, height, -maxy));
        rect.add(new Vector3d(maxx, height, -miny));

        return rect;
    }
}
