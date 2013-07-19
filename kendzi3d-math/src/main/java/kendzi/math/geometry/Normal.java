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



public class Normal {

	/** From 0 to Pi
	 *
	 * @param ax x coordinate of vector a
	 * @param ay y coordinate of vector a
	 * @param bx x coordinate of vector b
	 * @param by y coordinate of vector b
	 * @return angle between vectors
	 */
	public static double angleBetwenVector(double ax, double ay, double bx, double by) {
		// cos %alfa = (a o b) / |a| * |b|;
		// XXX test sqrt
		double cosAlfa = (ax * bx + ay * by) / Math.sqrt( (ax*ax + ay*ay) * (bx*bx + by*by) );

		return Math.acos(cosAlfa);
	}

	/**
	 *
	 * <code>
	 * <pre>
	 *  N
	 * 	^
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
	 * @return
	 */
	public static double [] calcNormal(
			double a_x, double a_y, double a_z,
			double b_x, double b_y, double b_z,
			double c_x, double c_y, double c_z) {

		double [] ret = new double [3];
//
//		double v1_x = b.x - a.x;
//		double v1_y = b.y - a.y;
//		double v2_z = b.z - a.z;
//
//		double v2_x = c.x - a.x;
//		double v2_y = c.y - a.y;
//		double v2_z = c.z - a.z;


		double v1_x = b_x - a_x;
		double v1_y = b_y - a_y;
		double v1_z = b_z - a_z;

		double v2_x = c_x - a_x;
		double v2_y = c_y - a_y;
		double v2_z = c_z - a_z;

		double normal_x = (v1_y * v2_z) - (v1_z * v2_y);
		double normal_y = -((v2_z * v1_x) - (v2_x * v1_z));
		double normal_z = (v1_x * v2_y) - (v1_y * v2_x);

		ret[0] = normal_x;
		ret[1] = normal_y;
		ret[2] = normal_z;

		return ret;
	}

	public static double [] calcNormalNorm(
			double a_x, double a_y, double a_z,
			double b_x, double b_y, double b_z,
			double c_x, double c_y, double c_z) {

		double [] ret = calcNormal(a_x, a_y, a_z, b_x, b_y, b_z, c_x, c_y, c_z);

		double normal_x = ret[0];
		double normal_y = ret[1];
		double normal_z = ret[2];

		double normalisationFactor = Math.sqrt(
				(normal_x * normal_x) +
				(normal_y * normal_y) +
				(normal_z * normal_z));

		ret[0] = normal_x / normalisationFactor;
		ret[1] = normal_y / normalisationFactor;
		ret[2] = normal_z / normalisationFactor;

		return ret;
	}

	public static Vector3d calcNormalNorm(
			Point3d A,
			Point3d B,
			Point3d C) {
		//FIXME
		//TODO
		//CLEAN UP
		return calcNormalNorm2(A.x, A.y, A.z, B.x, B.y, B.z, C.x, C.y, C.z);
	}

	public static Vector3d calcNormalNorm2(

			double a_x, double a_y, double a_z,
			double b_x, double b_y, double b_z,
			double c_x, double c_y, double c_z) {

		//FIXME
		//TODO
		//CLEAN UP

		double [] ret = calcNormal(a_x, a_y, a_z, b_x, b_y, b_z, c_x, c_y, c_z);

		double normal_x = ret[0];
		double normal_y = ret[1];
		double normal_z = ret[2];

		double normalisationFactor = Math.sqrt(
				(normal_x * normal_x) +
				(normal_y * normal_y) +
				(normal_z * normal_z));

		ret[0] = normal_x / normalisationFactor;
		ret[1] = normal_y / normalisationFactor;
		ret[2] = normal_z / normalisationFactor;

		//TODO use function from vecmath.jar

		return new Vector3d(ret[0], ret[1], ret[2]);
	}
}
