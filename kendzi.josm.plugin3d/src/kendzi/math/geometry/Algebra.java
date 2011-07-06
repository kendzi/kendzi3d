/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry;

import java.awt.geom.Point2D.Double;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public class Algebra {

    @Deprecated
	public static Double projectPointPerpendicularToLine(Double lineA, Double lineB, Double point) {

		double sqr1 = lineA.x - lineB.x;
		double sqr2 = lineA.y - lineB.y;
		double u = ((point.x - lineA.x) * (lineB.x - lineA.x) + (point.y - lineA.y) * (lineB.y - lineA.y)) / (sqr1 * sqr1 + sqr2 * sqr2);

		return new Double(
				(lineA.x + (lineB.x - lineA.x) * u),
				(lineA.y + (lineB.y - lineA.y) * u));
	}

    @Deprecated
	public static Point2d projectPointPerpendicularToLine(Point2d lineA, Point2d lineB, Point2d point) {

	    double sqr1 = lineA.x - lineB.x;
	    double sqr2 = lineA.y - lineB.y;
	    double u = ((point.x - lineA.x) * (lineB.x - lineA.x) + (point.y - lineA.y) * (lineB.y - lineA.y)) / (sqr1 * sqr1 + sqr2 * sqr2);

	    return new Point2d(
	            (lineA.x + (lineB.x - lineA.x) * u),
	            (lineA.y + (lineB.y - lineA.y) * u));
	}

    /**
     * <code>
     * <pre>
     *
     *  unitVector
     *    ^
     *    |
     *    |
     *    + -- ^ vectorToProject
     *    |   /
     *    |  /
     *    | /
     *    |/
     *    0
     * </pre>
     * </code>
     *
     *
     * @see http://en.wikipedia.org/wiki/Vector_projection
     * @param unitVector
     * @param vectorToProject
     * @return
     */
   public static Vector2d orthogonalProjection(Vector2d unitVector, Tuple2d vectorToProject) {
       Vector2d n = new Vector2d(unitVector);
       n.normalize();

       double px = vectorToProject.x;
       double py = vectorToProject.y;

       double ax = n.x;
       double ay = n.y;

       return new Vector2d(
               px * ax * ax   + py * ax * ay,
               px * ax * ay   + py * ay * ay
            );
   }


	/**
	 *
	 * @see http://en.wikipedia.org/wiki/Vector_projection
	 * @param unitVector
	 * @param vectorToProject
	 * @return
	 */
	public static Vector3d orthogonalProjection(Vector3d unitVector, Vector3d vectorToProject) {
	    Vector3d n = new Vector3d(unitVector);
	    n.normalize();

	    double px = vectorToProject.x;
	    double py = vectorToProject.y;
	    double pz = vectorToProject.z;

	    double ax = n.x;
	    double ay = n.y;
	    double az = n.z;

	    return new Vector3d(
	            px * ax * ax   + py * ax * ay  + pz * ax * az,
	            px * ax * ay   + py * ay * ay  + pz * ay * az,
	            px * ax * az   + py * ay * az  + pz * az * az
	            );

	}

}
