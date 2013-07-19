/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.point;

import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public class PointUtil {

    /**
     * @see http://en.wikipedia.org/wiki/Transformation_matrix
     * @param pTuple
     * @param pAngle
     * @return
     */
    public static Vector2d rotateCounterClockwise2d(Tuple2d pTuple, double pAngle) {
        double cosA = Math.cos(pAngle);
        double sinA = Math.sin(pAngle);
        return new Vector2d(
                pTuple.x * cosA - pTuple.y * sinA,
                pTuple.x * sinA + pTuple.y * cosA);
    }

    /**
     * @see http://en.wikipedia.org/wiki/Transformation_matrix
     * @param pTuple
     * @param pAngle
     * @return
     */
    public static Vector2d rotateClockwise2d(Tuple2d pTuple, double pAngle) {
        double cosA = Math.cos(pAngle);
        double sinA = Math.sin(pAngle);
        return new Vector2d(
                pTuple.x * cosA + pTuple.y * sinA,
                - pTuple.x * sinA + pTuple.y * cosA);
    }

    /**
     * @see http://pl.wikipedia.org/wiki/Elementarne_macierze_transformacji
     * @param pTuple3d
     * @param pAngle
     * @return
     */
    public static Vector3d rotateX3d(Tuple3d pTuple, double pAngle) {

        double x = pTuple.x;
        double y = pTuple.y;
        double z = pTuple.z;

        double cosA = Math.cos(pAngle);
        double sinA = Math.sin(pAngle);

        return new Vector3d(
                x,
                y * cosA - z * sinA,
                y * sinA + z * cosA);

    }

    /**
     * @see http://pl.wikipedia.org/wiki/Elementarne_macierze_transformacji
     * @param pTuple3d
     * @param pAngle
     * @return
     */
    public static Vector3d rotateY3d(Tuple3d pTuple3d, double pAngle) {

        double x = pTuple3d.x;
        double y = pTuple3d.y;
        double z = pTuple3d.z;

        double cosB = Math.cos(pAngle);
        double sinB = Math.sin(pAngle);

        return new Vector3d(
              x * cosB + z * sinB,
              y,
              -x * sinB + z * cosB);

    }

    /**
     * @see http://pl.wikipedia.org/wiki/Elementarne_macierze_transformacji
     * @param pTuple3d
     * @param pAngle
     * @return
     */
    public static Vector3d rotateZ3d(Vector3d pTuple3d, double pAngle) {

        double x = pTuple3d.x;
        double y = pTuple3d.y;
        double z = pTuple3d.z;

        double cosR = Math.cos(pAngle);
        double sinR = Math.sin(pAngle);

        return new Vector3d(
              x * cosR - y * sinR,
              x * sinR + y * cosR,
              z);

    }



}
