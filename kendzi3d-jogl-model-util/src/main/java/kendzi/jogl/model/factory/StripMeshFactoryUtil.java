package kendzi.jogl.model.factory;

import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.texture.dto.TextureData;

/**
 * Factory utility for creating strip mesh.
 */
public class StripMeshFactoryUtil {

    /**
     * Make roof border mesh. It is wall under roof.
     *
     * @param stripPoints
     *            the list of strip points in 2d
     * @param minHeights
     *            the provider for minimal point height
     * @param maxHeights
     *            the provider for maximal point height
     * @param mesh
     *            the output mesh
     * @param textureData
     *            the texture data
     * @param closed
     *            if strip should be closed
     * @param isCounterClockwise
     *            the direction of normals
     */
    public static void verticalStripMesh(List<Point2d> stripPoints, HeightProvider minHeights,
            HeightProvider maxHeights, MeshFactory mesh, TextureData textureData, boolean closed,
            boolean isCounterClockwise) {

        int size = stripPoints.size();
        if (!closed) {
            // Strip should not be closed.
            size--;
        }

        Integer[] bottomPointsIndex = new Integer[size];
        Integer[] topPointsIndex = new Integer[size];

        FaceFactory face = mesh.addFace(FaceType.TRIANGLES);

        double uLast = 0;

        for (int i = 0; i < size; i++) {
            int index1 = i;
            int index2 = (i + 1) % size;

            Point2d point1 = stripPoints.get(index1);
            Point2d point2 = stripPoints.get(index2);

            double height1 = maxHeights.getHeight(index1);
            double height2 = maxHeights.getHeight(index2);

            double minHeight1 = minHeights.getHeight(index1);
            double minHeight2 = minHeights.getHeight(index2);

            int point1HightIndex = cachePointIndex(point1, index1, height1, topPointsIndex, mesh);
            int point2HightIndex = cachePointIndex(point2, index2, height2, topPointsIndex, mesh);

            int point1BottomIndex = cachePointIndex(point1, index1, minHeight1, bottomPointsIndex, mesh);
            int point2BottomIndex = cachePointIndex(point2, index2, minHeight2, bottomPointsIndex, mesh);

            Vector3d n = new Vector3d(-(point2.y - point1.y), 0, -(point2.x - point1.x));
            n.normalize();

            if (isCounterClockwise) {
                n.negate();
            }

            int normalIndex = mesh.addNormal(n);

            double uBegin = uLast;
            double uEnd = uLast + point1.distance(point2) / textureData.getWidth();
            uLast = uEnd;

            int tc_0_0 = mesh.addTextCoord(new TextCoord(uBegin, minHeight1 / textureData.getHeight()));
            int tc_0_v = mesh.addTextCoord(new TextCoord(uBegin, height1 / textureData.getHeight()));
            int tc_u_0 = mesh.addTextCoord(new TextCoord(uEnd, minHeight2 / textureData.getHeight()));
            int tc_u_v = mesh.addTextCoord(new TextCoord(uEnd, height2 / textureData.getHeight()));

            if (height1 > 0) {
                face.addVertIndex(point1HightIndex);
                face.addVertIndex(point1BottomIndex);
                face.addVertIndex(point2BottomIndex);

                face.addNormalIndex(normalIndex);
                face.addNormalIndex(normalIndex);
                face.addNormalIndex(normalIndex);

                face.addCoordIndex(tc_0_v);
                face.addCoordIndex(tc_0_0);
                face.addCoordIndex(tc_u_0);
            }

            if (height2 > 0) {
                face.addVertIndex(point1HightIndex);
                face.addVertIndex(point2BottomIndex);
                face.addVertIndex(point2HightIndex);

                face.addNormalIndex(normalIndex);
                face.addNormalIndex(normalIndex);
                face.addNormalIndex(normalIndex);

                face.addCoordIndex(tc_0_v);
                face.addCoordIndex(tc_u_0);
                face.addCoordIndex(tc_u_v);
            }
        }
    }

    private static int cachePointIndex(Point2d point, int pointIndex, double height, Integer[] pointsIndexCache,
            MeshFactory meshBorder) {

        if (pointsIndexCache[pointIndex] == null) {
            pointsIndexCache[pointIndex] = meshBorder.addVertex(new Point3d(point.x, height, -point.y));
        }

        return pointsIndexCache[pointIndex];
    }

    /**
     * Provides height for points.
     */
    interface HeightProvider {
        /**
         * Gets height for given point.
         * 
         * @param i
         *            point index
         * @return heigth for given point
         */
        double getHeight(int i);
    }

    /**
     * Provides constant height for point.
     */
    public static class ConstHeightProvider implements HeightProvider {
        private final double height;

        /**
         * Const.
         * 
         * @param height
         *            constant height
         */
        public ConstHeightProvider(double height) {
            this.height = height;
        }

        @Override
        public double getHeight(int i) {
            return height;
        }
    }

    /**
     * Provides height from list sources for the point.
     */
    public static class ListHeightProvider implements HeightProvider {
        private final List<Double> heights;

        /**
         * Const.
         * 
         * @param heights
         *            list source of heights
         */
        public ListHeightProvider(List<Double> heights) {
            this.heights = heights;
        }

        @Override
        public double getHeight(int i) {
            return heights.get(i);
        }
    }

}
