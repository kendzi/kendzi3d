/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry;
// Graham.java

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import kendzi.math.geometry.rectangle.RectanglePointVector2d;

public class GrahamTestUi extends Applet {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	final static int NMAX = 64;
	Dimension siz;
	Button reset;
	Button recalc;
	List<Point2d> p;
	int n;
	Polygon ch;
	boolean chok;
	private Polygon ch2;

	@Override
    public void init() {
		siz = getSize();
		add(reset = new Button("Reset"));
		add(recalc = new Button("recalc"));
		setBackground(new Color(0, 0, 0x80));
		setForeground(Color.white);
		n = 0;
		chok = false;
		p = new ArrayList<Point2d>();
//		for (int i = 0; i < NMAX; i++)
//			p[i] = new Point2d(0, 0);
	}

	@Override
    public boolean action(Event ev, Object obj) {
		if (ev.target == reset) {
			clearAll();
			repaint();
		}
		if (ev.target == recalc) {
			// p.add(new Point2d(78.0, 114.0);
//			 p.add(new Point2d(118.0, 121.0);
//			 p.add(new Point2d(129.0, 63.0));
//			 p.add(new Point2d(145.0, 84.0));

			GrahamScanApp();
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

	@Override
    public void paint(Graphics g) {
		g.clearRect(0, 0, siz.width, siz.height);
		g.setColor(Color.white);
		for (int i = 0; i < n; i++) {
			g.fillOval((int) p.get(i).x - 3, (int) p.get(i).y - 3, 7, 7);
		}
		g.drawString("<" + n + ">", 0, siz.height - 5);
		if (chok) {
			g.setColor(Color.yellow);
			g.drawPolygon(ch);
			if (ch2 != null) {
				g.setColor(Color.green);
				g.drawPolygon(ch2);
			}
		}
	}

	void addPoint(int x, int y) {
//		if (n == NMAX)
//			return;
		for (int i = 0; i < n; i++) {
			if (x == p.get(i).x && y == p.get(i).y)
				return;
		}
		p.add(new Point2d(x, y));
		n++;
		if (n >= 3) {
			chok = GrahamScanApp();
		}
	}

	void clearAll() {
		p = new ArrayList<Point2d>();
		n = 0;
		chok = false;
		ch = null;
		ch2 = null;
	}



	boolean GrahamScanApp() {
		List<Point2d> ret = Graham.grahamScan(p);

		ch = new Polygon();
		for (Point2d p : ret) {
			ch.addPoint((int) p.x, (int) p.y);
		}

		ch2 = new Polygon();
		Point2d[] cont = rectToList(RectangleUtil.findRectangleContur(Collections.unmodifiableList(ret)));
		for (Point2d p : cont) {
			ch2.addPoint((int) p.x, (int) p.y);
		}

		return true;
	}
	/**
     * @param contur
     * @return
     */
    public Point2d[] rectToList(RectanglePointVector2d contur) {
        Point2d p1 = contur.getPoint();
        Point2d p2 = new Point2d(contur.getVector());
        p2.scaleAdd(contur.getWidth(), contur.getPoint());

        Vector2d ort = new Vector2d(-contur.getVector().y * contur.getHeight(), contur.getVector().x * contur.getHeight());

        Point2d p3 = new Point2d(p2);
        p3.add(ort);

        Point2d p4 = new Point2d(p1);
        p4.add(ort);


        Point2d[] ret = new Point2d[4];
        ret[0] = p1;
        ret[1] = p2;
        ret[2] = p3;
        ret[3] = p4;
        return ret;
    }

}