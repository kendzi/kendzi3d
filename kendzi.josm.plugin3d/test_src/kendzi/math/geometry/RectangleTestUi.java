/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

public class RectangleTestUi {

	static double calcMom(List<Point2D.Double> points, double p, double q) {

		double[] x = new double[points.size()];
		double[] y = new double[points.size()];

		double midX = 0;
		double midY = 0;
		for (int i = 0; i < points.size(); i++) {
			Double point = points.get(i);

			x[i] = point.x;
			y[i] = point.y;

			midX += point.x;
			midY += point.y;
		}
		midX = midX / points.size();
		midY = midY / points.size();

		double sumX = 0;
		double sumY = 0;
		for (int i = 0; i < points.size(); i++) {
			for (int j = 0; j < points.size(); j++) {

				sumX += (Math.pow((x[i] - midX), p) * Math.pow((y[j] - midY), q));
			}
		}

		return sumX;

	}

	static double calcTeta(List<Point2D.Double> points) {

		double u11 = calcMom(points, 1, 1);
		double u20 = calcMom(points, 2, 0);
		double u02 = calcMom(points, 0, 2);

		double tan = Math.tan((2.0*u11) / (u20 - u02));
		return 0.5 * Math.pow(tan, -1);

	}

	public static void main(String[] args) {
		List<Point2D.Double> points = new ArrayList<Point2D.Double>();
		points.add(new Double(0,0));
		points.add(new Double(1,1));
		points.add(new Double(2,2));
		points.add(new Double(3,3));
		points.add(new Double(0,2));


		System.out.println(Math.toDegrees(calcTeta(points)));
	}

}
