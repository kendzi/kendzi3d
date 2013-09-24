/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

/**
 * Util for calculate graham polygon for given list of point.
 * 
 */
public class Graham {

    /**
     * Calculate graham polygon for given list of point.
     * 
     * @param points
     * @return graham polygon
     */
    public static List<Point2d> grahamScan(List<Point2d> points) {
        int n = points.size();

        List<Point2d> ret = new ArrayList<Point2d>();

        Point2d p[] = new Point2d[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Point2d point = points.get(i);
            p[i] = point;
        }

        selectMin(p, n);
        sortPoints(p, n);

        int stk[] = new int[n];
        int top = 2;
        stk[0] = 0;
        stk[1] = 1;
        stk[2] = 2;
        for (int i = 3; i < n; i++) {
            while (area(p[stk[top - 1]], p[stk[top]], p[i]) < 0) {
                top--;
            }
            stk[++top] = i;
        }

        for (int i = 0; i <= top; i++) {
            ret.add(new Point2d(p[stk[i]].x, p[stk[i]].y));
        }
        ret.add(new Point2d(p[stk[0]].x, p[stk[0]].y));
        return ret;
    }

    private static double area(Point2d p0, Point2d p1,
            Point2d p2) {
        double dx1 = p1.x - p0.x, dy1 = p1.y - p0.y;
        double dx2 = p2.x - p0.x, dy2 = p2.y - p0.y;
        return dx1 * dy2 - dx2 * dy1;
    }

    private static void swapPoints(Point2d p[], int i, int j) {
        Point2d tmp;
        tmp = p[i];
        p[i] = p[j];
        p[j] = tmp;
    }

    private static void selectMin(Point2d p[], int n) {
        double ym = p[0].y;
        int m = 0;
        for (int i = 1; i < n; i++) {
            if (ym > p[i].y || (ym == p[i].y && p[m].x > p[i].x)) {
                ym = p[i].y;
                m = i;
            }
        }
        swapPoints(p, 0, m);
    }

    private static void sortPoints(Point2d p[], int n) {
        for (int i = 1; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (area(p[0], p[i], p[j]) < 0) {
                    swapPoints(p, i, j);
                }
            }
        }
    }
}