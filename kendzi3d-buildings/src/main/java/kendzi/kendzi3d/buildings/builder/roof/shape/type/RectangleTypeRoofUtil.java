/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import java.util.List;

import kendzi.math.geometry.point.Vector2dUtil;
import kendzi.math.geometry.polygon.PolygonList2d;
import org.joml.Vector2d;
import org.joml.Vector2dc;

/**
 * Rectangle type roof util.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public class RectangleTypeRoofUtil {

    /**
     * Snaps direction vector to best matching orthogonally of polygon edges.
     * 
     * @param frontDirection
     *            direction vector
     * @param polygon
     *            polygon
     * @return direction vector snaps to best matching orthogonally of polygon edges
     */
    public static Vector2dc snapsDirectionToOutline(Vector2dc frontDirection, PolygonList2d polygon) {

        Vector2dc direction = Vector2dUtil.orthogonalLeft(frontDirection);
        List<Vector2dc> points = polygon.getPoints();

        double maxD = -Double.MAX_VALUE;
        Vector2d maxV = null;

        Vector2dc end = points.get(points.size() - 1);
        for (Vector2dc begin : points) {
            Vector2d v = new Vector2d(end).sub(begin).normalize();

            double d = v.dot(direction);
            if (d > maxD) {
                maxD = d;
                maxV = v;
            }

            v.negate();
            d = v.dot(direction);
            if (d > maxD) {
                maxD = d;
                v.negate();
                maxV = v;
            }

            end = begin;
        }

        if (maxV == null) {
            return null;
        }

        maxV = Vector2dUtil.orthogonalLeft(maxV);

        if (maxV.dot(frontDirection) < 0) {
            maxV.negate();
            return maxV;
        }

        return maxV;
    }
}
