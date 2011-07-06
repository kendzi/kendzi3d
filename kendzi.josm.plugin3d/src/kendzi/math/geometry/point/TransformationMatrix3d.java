/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.point;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.ejml.data.SimpleMatrix;

/**
 *
 * @see http://en.wikipedia.org/wiki/Transformation_matrix
 * @see http://pl.wikipedia.org/wiki/Elementarne_macierze_transformacji
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class TransformationMatrix3d {

    /**
     * @param alpha
     * @return
     */
    public static SimpleMatrix rotX(double alpha) {
        double sinX = Math.sin(alpha);
        double cosX = Math.cos(alpha);

        return new SimpleMatrix(
                new double [][] {
                {1, 0, 0 },
                {0, cosX, -sinX},
                {0, sinX, cosX}
                });
    }
    /**
     * @param alpha
     * @return
     */
    public static SimpleMatrix rotXA(double alpha) {
        double sinX = Math.sin(alpha);
        double cosX = Math.cos(alpha);

        return new SimpleMatrix(
                new double [][] {
                        {1, 0, 0, 0},
                        {0, cosX, -sinX, 0},
                        {0, sinX, cosX, 0},
                        {0, 0, 0, 1}
                });
    }

    /**
     * @param alpha
     * @return
     */
    public static SimpleMatrix rotY(double alpha) {
        double sinX = Math.sin(alpha);
        double cosX = Math.cos(alpha);

        return new SimpleMatrix(
                new double [][] {
                        {cosX, 0, sinX },
                        {0, 1, 0 },
                        {-sinX, 0, cosX }
                });
    }
    /**
     * @param alpha
     * @return
     */
    public static SimpleMatrix rotYA(double alpha) {
        double sinX = Math.sin(alpha);
        double cosX = Math.cos(alpha);

        return new SimpleMatrix(
                new double [][] {
                        {cosX, 0, sinX, 0},
                        {0, 1, 0, 0},
                        {-sinX, 0, cosX, 0},
                        {0, 0, 0, 1}
                });
    }

    /**
     * @param alpha
     * @return
     */
    public static SimpleMatrix rotZ(double alpha) {
        double sinX = Math.sin(alpha);
        double cosX = Math.cos(alpha);

        return new SimpleMatrix(
                new double [][] {
                        {cosX, -sinX, 0 },
                        {sinX, cosX, 0 },
                        {0, 0, 1 }
                });
    }
    /**
     * @param alpha
     * @return
     */
    public static SimpleMatrix rotZA(double alpha) {
        double sinX = Math.sin(alpha);
        double cosX = Math.cos(alpha);

        return new SimpleMatrix(
                new double [][] {
                        {cosX, -sinX, 0, 0},
                        {sinX, cosX, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}
                });
    }

    /**
     * @param alpha
     * @return
     */
    public static SimpleMatrix tranA(double x, double y, double z) {

        return new SimpleMatrix(
                new double [][] {
                        {1, 0, 0, x},
                        {0, 1, 0, y},
                        {0, 0, 1, z},
                        {0, 0, 0, 1}
                });
    }

    /**
     *
     * @return
     */
    public static SimpleMatrix scaleA(double scaleX, double scaleY, double scaleZ) {


        return new SimpleMatrix(
                new double [][] {
                        {scaleX, 0, 0, 0},
                        {0, scaleY, 0, 0},
                        {0, 0, scaleZ, 0},
                        {0, 0, 0, 1}
                });
    }

    public static Point3d transform(Point3d pPoint, SimpleMatrix pSimpleMatrix) {
        SimpleMatrix sm = new SimpleMatrix(
                new double [][] {
                        {pPoint.getX()},
                        {pPoint.getY()},
                        {pPoint.getZ()},
                        {1}
                });

        SimpleMatrix mult = pSimpleMatrix.mult(sm);

        return new Point3d(mult.get(0), mult.get(1), mult.get(2));
    }

    public static Vector3d transform(Vector3d pVector, SimpleMatrix pSimpleMatrix) {
        SimpleMatrix sm = new SimpleMatrix(
                new double [][] {
                        {pVector.getX()},
                        {pVector.getY()},
                        {pVector.getZ()},
                        {0}
                });

        SimpleMatrix mult = pSimpleMatrix.mult(sm);

        return new Vector3d(mult.get(0), mult.get(1), mult.get(2));
    }
}
