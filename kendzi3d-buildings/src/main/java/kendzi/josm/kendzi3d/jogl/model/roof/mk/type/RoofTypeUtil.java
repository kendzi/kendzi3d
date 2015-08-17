/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.StripMeshFactoryUtil;
import kendzi.jogl.texture.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.wall.HeightCalculator;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonUtil;
import kendzi.math.geometry.polygon.split.EnrichPolygonalChainUtil;

import org.apache.log4j.Logger;

public class RoofTypeUtil {
    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(RoofTypeUtil.class);

    /**
     * Make mesh of vertical wall e.g. It is wall under the roof. The wall base
     * is on zero height.
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
    public static void makeRoofBorderMesh(List<Point2d> stripPoints, List<Double> stripPointHeights, MeshFactory mesh,
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
    public static void makeRoofBorderMesh(List<Point2d> stripPoints, double minHeight, List<Double> stripPointHeights,
            MeshFactory mesh, TextureData textureData) {

        StripMeshFactoryUtil.verticalStripMesh(stripPoints, //
                new StripMeshFactoryUtil.ConstHeightProvider(minHeight),//
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
    public static List<Point2d> splitBorder(PolygonList2d polygon, LinePoints2d... lines) {

        List<Point2d> splitPolygon = new ArrayList<Point2d>(polygon.getPoints());

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
    public static void makeWallsFromHeightCalculator(List<Point2d> wallPoints, HeightCalculator heightCalculator,
            double minHeight, MeshFactory mesh, TextureData textureData) {

        boolean isClockwisePolygon = !PolygonUtil.isClockwisePolygon(wallPoints);

        for (int i = 0; i < wallPoints.size(); i++) {
            Point2d p1 = wallPoints.get(i);
            Point2d p2 = wallPoints.get((i + 1) % wallPoints.size());

            List<SegmentHeight> height2 = heightCalculator.height(p1, p2);

            for (int j = 0; j < height2.size(); j++) {

                List<Point2d> segment = new ArrayList<Point2d>(2);
                List<Double> segmentHeights = new ArrayList<Double>(2);

                SegmentHeight segmentHeight = height2.get(j);

                segment.add(segmentHeight.getBegin());
                segment.add(segmentHeight.getEnd());

                segmentHeights.add(segmentHeight.getBeginHeight());
                segmentHeights.add(segmentHeight.getEndHeight());

                StripMeshFactoryUtil.verticalStripMesh(segment, //
                        new StripMeshFactoryUtil.ConstHeightProvider(minHeight),//
                        new StripMeshFactoryUtil.ListHeightProvider(segmentHeights), //
                        mesh, //
                        textureData, //
                        false, //
                        isClockwisePolygon);
            }
        }
    }

    /**
     * Find minimal rectangle containing list of points. Save as list of 3d
     * points to display.
     *
     * XXX this should by changed!
     *
     * @param polygon
     *            list of points
     * @param height
     *            height
     * @return minimal rectangle
     */
    public static List<Point3d> findRectangle(List<Point2d> polygon, double height) {

        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;

        for (Point2d p : polygon) {
            if (minx > p.x) {
                minx = p.x;
            }
            if (miny > p.y) {
                miny = p.y;
            }
            if (maxx < p.x) {
                maxx = p.x;
            }
            if (maxy < p.y) {
                maxy = p.y;
            }
        }

        List<Point3d> rect = new ArrayList<Point3d>();
        rect.add(new Point3d(minx, height, -miny));
        rect.add(new Point3d(minx, height, -maxy));
        rect.add(new Point3d(maxx, height, -maxy));
        rect.add(new Point3d(maxx, height, -miny));

        return rect;
    }
}
