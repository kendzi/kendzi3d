/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class NormalUtil {

    /**
     * 
     * <code>
     * <pre>
     *  N
     *  ^
     *  |   C
     *  |  /
     *  | /
     *  |/
     *  A--------->B
     * </pre>
     * </code>
     * 
     * @param a
     * @param b
     * @param c
     * @return normal vector
     */
    public static Vector3d normal(Point3d a, Point3d b, Point3d c) {
        return normal(a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z);
    }

    /**
     * @param a_x
     * @param a_y
     * @param a_z
     * @param b_x
     * @param b_y
     * @param b_z
     * @param c_x
     * @param c_y
     * @param c_z
     * @return normal
     * 
     * @see kendzi.math.geometry.NormalUtil#normal(Point3d, Point3d, Point3d)
     */
    public static Vector3d normal(
            double a_x, double a_y, double a_z,
            double b_x, double b_y, double b_z,
            double c_x, double c_y, double c_z) {

        double v1_x = b_x - a_x;
        double v1_y = b_y - a_y;
        double v1_z = b_z - a_z;

        double v2_x = c_x - a_x;
        double v2_y = c_y - a_y;
        double v2_z = c_z - a_z;

        double normal_x = (v1_y * v2_z) - (v1_z * v2_y);
        double normal_y = -((v2_z * v1_x) - (v2_x * v1_z));
        double normal_z = (v1_x * v2_y) - (v1_y * v2_x);

        double normalisationFactor = Math.sqrt(
                (normal_x * normal_x) +
                (normal_y * normal_y) +
                (normal_z * normal_z));

        normal_x = normal_x / normalisationFactor;
        normal_y = normal_y / normalisationFactor;
        normal_z = normal_z / normalisationFactor;

        //TODO use function from vecmath.jar

        return new Vector3d(normal_x, normal_y, normal_z);
    }
}
