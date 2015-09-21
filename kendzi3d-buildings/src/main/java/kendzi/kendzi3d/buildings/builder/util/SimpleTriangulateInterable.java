package kendzi.kendzi3d.buildings.builder.util;

import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;

/**
 * Simple triangulation interator. To change rectangle polygons into triangles.
 * This is naive implementation which support only 3 or 4 vertex polygons. It
 * allows to use foreach and receive next triangle vertexes.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class SimpleTriangulateInterable implements Iterable<Point2d> {

    private final List<Point2d> points;
    private int count;
    private int size;
    private static int[] indexes = { 0, 1, 2, 2, 3, 0 };

    /**
     * Constructor.
     *
     * @param points
     *            the list of polygon points
     */
    public SimpleTriangulateInterable(List<Point2d> points) {
        this.points = points;
        if (points.size() == 4) {
            size = 6;
        } else if (points.size() == 3) {
            size = 3;
        } else {
            throw new IllegalArgumentException("Unsuported size of polygon: " + points.size());
        }
    }

    @Override
    public Iterator<Point2d> iterator() {

        return new Iterator<Point2d>() {

            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public Point2d next() {
                Point2d point2d = points.get(indexes[count]);
                count++;
                return point2d;
            }

            @Override
            public void remove() {
                throw new IllegalStateException();
            }
        };
    }

}