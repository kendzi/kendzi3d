/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.point;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.ejml.data.SimpleMatrix;

/**
 *
 * @see http://en.wikipedia.org/wiki/Transformation_matrix
 * @see http://pl.wikipedia.org/wiki/Elementarne_macierze_transformacji
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class TransformationMatrix2d {

    /**
     * @param alpha
     * @return
     */
    public static SimpleMatrix rotX(double alpha) {
        double sinX = Math.sin(alpha);
        double cosX = Math.cos(alpha);

        return new SimpleMatrix(
                new double [][] {
                {1, 0 },
                {0, cosX}
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
                        {1, 0, 0},
                        {0, cosX, 0},
                        {0, 0, 1}
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
                        {cosX, 0 },
                        {0, 1 },
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
                        {cosX, 0, 0},
                        {0, 1, 0},
                        {0, 0, 1}
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
                        {cosX, -sinX },
                        {sinX, cosX }
                });
    }
    /**
     * XXX sign of alpha may change !!!
     * @param alpha
     * @return
     */
    public static SimpleMatrix rotZA(double alpha) {
        double sinX = Math.sin(alpha);
        double cosX = Math.cos(alpha);

        return new SimpleMatrix(
                new double [][] {
                        {cosX, -sinX, 0},
                        {sinX, cosX, 0},
                        {0, 0, 1}
                });
    }


    /**
     * @param alpha
     * @return
     */
    public static SimpleMatrix tranA(double x, double y) {

        return new SimpleMatrix(
                new double [][] {
                        {1, 0, x},
                        {0, 1, y},
                        {0, 0, 1}
                });
    }

    /**
     *
     * @return
     */
    public static SimpleMatrix scaleA(double scaleX, double scaleY) {


        return new SimpleMatrix(
                new double [][] {
                        {scaleX, 0, 0},
                        {0, scaleY, 0},
                        {0, 0, 1}
                });
    }

    public static Point2d transform(Point2d pPoint, SimpleMatrix pSimpleMatrix) {
        SimpleMatrix sm = new SimpleMatrix(
                new double [][] {
                        {pPoint.getX()},
                        {pPoint.getY()},
                        {1}
                });

        SimpleMatrix mult = pSimpleMatrix.mult(sm);

        return new Point2d(mult.get(0), mult.get(1));
    }

    public static Vector2d transform(Vector2d pVector, SimpleMatrix pSimpleMatrix) {
        SimpleMatrix sm = new SimpleMatrix(
                new double [][] {
                        {pVector.getX()},
                        {pVector.getY()},
                        {0}
                });

        SimpleMatrix mult = pSimpleMatrix.mult(sm);

        return new Vector2d(mult.get(0), mult.get(1));
    }
}
