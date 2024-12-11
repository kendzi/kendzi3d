package kendzi.kendzi3d.buildings.builder.util;

import java.util.Iterator;
import java.util.List;

import org.joml.Vector2dc;

/**
 * Simple triangulation interator. To change rectangle polygons into triangles.
 * This is naive implementation which support only 3 or 4 vertex polygons. It
 * allows to use foreach and receive next triangle vertexes.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class SimpleTriangulateInterable implements Iterable<Vector2dc> {

    private final List<Vector2dc> points;
    private int count;
    private final int size;
    private static final int[] indexes = { 0, 1, 2, 2, 3, 0 };

    /**
     * Constructor.
     *
     * @param points
     *            the list of polygon points
     */
    public SimpleTriangulateInterable(List<Vector2dc> points) {
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
    public Iterator<Vector2dc> iterator() {

        return new Iterator<Vector2dc>() {

            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public Vector2dc next() {
                Vector2dc point2d = points.get(indexes[count]);
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