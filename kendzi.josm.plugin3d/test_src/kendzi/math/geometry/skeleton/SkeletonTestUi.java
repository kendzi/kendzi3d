/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.skeleton;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Polygon;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.polygon.PolygonList2d;

public class SkeletonTestUi extends Applet {
    /**
	 *
	 */
    private static final long serialVersionUID = 1L;
    // final static int NMAX = 64;
    Dimension siz;
    Button reset;
    Button recalc;
    List<Point2d> points;
    // int n;
    List<Polygon> drawableObjects = new ArrayList<Polygon>();

    private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp

    @Override
    public void init() {
        this.siz = getSize();
        add(this.reset = new Button("Reset"));
        add(this.recalc = new Button("Recalc"));
        setBackground(Color.LIGHT_GRAY);// new Color(0, 0, 0x80));
        setForeground(Color.white);

        this.points = new ArrayList<Point2d>();

    }

    @Override
    public boolean action(Event ev, Object obj) {
        if (ev.target == this.reset) {
            clearAll();
            repaint();
        }
        if (ev.target == this.recalc) {

            SkeletonScanApp();
            repaint();
        }
        return true;
    }

    @Override
    public boolean mouseDown(Event ev, int x, int y) {
        addPoint(x, y);
        repaint();
        return true;
    }

    private String format(Point2d pPoint) {
        return "[" + this.df.format(pPoint.x) + "," + this.df.format(pPoint.y) + "]";
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, this.siz.width, this.siz.height);
        for (int i = 0; i < this.points.size(); i++) {
            Point2d point = this.points.get(i);
            g.setColor(Color.ORANGE);
            g.fillOval((int) point.x - 3, (int) point.y - 3, 7, 7);

            g.setColor(Color.BLACK);
            g.drawString("" + format(point), (int) point.x + 3, (int) point.y - 3);

        }
        g.drawString("<" + this.points.size() + ">", 0, this.siz.height - 5);
        if (this.drawableObjects != null) {

            for (Polygon polygon : this.drawableObjects) {

                g.setColor(Color.yellow);
                g.drawPolygon(polygon);

            }
        }
    }

    void addPoint(int x, int y) {

        Point2d point = new Point2d(x, y);
        if (this.points.contains(point)) {
            return;
        }

        this.points.add(point);

        if (this.points.size() >= 3) {
            SkeletonScanApp();
        }
    }

    void clearAll() {
        this.points = new ArrayList<Point2d>();

        this.drawableObjects.clear();

    }

    boolean SkeletonScanApp() {
        Skeleton.Output ret = Skeleton.sk(this.points);

        setupResults(ret);

        return true;
    }

    public void setupResults(Skeleton.Output ret) {
        this.drawableObjects.clear();

//        for (List<Point2d> list : ret.faces) {
//
//            Polygon polygon = new Polygon();
//            for (Point2d point : list) {
//                polygon.addPoint((int) point.x, (int) point.y);
//            }
//
//            this.drawableObjects.add(polygon);
//        }

        for (PolygonList2d list : ret.faces2) {

            Polygon polygon = new Polygon();
            for (Point2d point : list.getPoints()) {
                polygon.addPoint((int) point.x, (int) point.y);
            }

            this.drawableObjects.add(polygon);
        }
    }

}
