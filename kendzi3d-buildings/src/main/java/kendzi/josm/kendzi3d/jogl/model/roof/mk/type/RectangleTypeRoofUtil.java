/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import kendzi.math.geometry.point.Vector2dUtil;
import kendzi.math.geometry.polygon.PolygonList2d;

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
     * @return direction vector snaps to best matching orthogonally of polygon
     *         edges
     */
    public static Vector2d snapsDirectionToOutline(Vector2d frontDirection, PolygonList2d polygon) {

        Vector2d direction = Vector2dUtil.orthogonal(frontDirection);
        List<Point2d> points = polygon.getPoints();

        double maxD = -Double.MAX_VALUE;
        Vector2d maxV = null;

        Point2d end = points.get(points.size() - 1);
        for (Point2d begin : points) {
            Vector2d v = new Vector2d(end);
            v.sub(begin);
            v.normalize();

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

        maxV = Vector2dUtil.orthogonal(maxV);

        if (maxV.dot(frontDirection) < 0) {
            maxV.negate();
            return maxV;
        }

        return maxV;
    }
}
