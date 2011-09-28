package kendzi.math.geometry.skeleton.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.PriorityQueue;

import javax.vecmath.Point2d;

import kendzi.math.geometry.debug.DisplayObject;
import kendzi.math.geometry.debug.DisplayRectBounds;
import kendzi.math.geometry.skeleton.Skeleton;
import kendzi.math.geometry.skeleton.Skeleton.IntersectEntry;
import kendzi.swing.ui.panel.equation.EquationDisplay;

/**
 *
 * @author Tomasz Kêdziroa (kendzi)
 */
public class DisplayEventQueue extends DisplayObject {

    private PriorityQueue<IntersectEntry> points;

    public final static Color EDGE_COLOR = Color.PINK;
    public final static Color SPLIT_COLOR = new Color(127, 0, 255);


    /**
     * @param polygon
     * @param pColor
     */
    public DisplayEventQueue(PriorityQueue<IntersectEntry> polygon) {
        super();
        this.points = polygon;

    }

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.points == null) {
            return;
        }


//        Point2d last = this.points.get(this.points.size() - 1);


//        g2d.setColor(color.darker());
//
//        Iterator<IntersectEntry> iterator = this.points.iterator();
//
//        if (iterator.hasNext()) {
//
//            IntersectEntry next = iterator.next();
//
//            Point2d first = next.v;
//            Point2d previous = first;
//            Point2d current = null;
//
//            while (iterator.hasNext()) {
//                IntersectEntry next2 = iterator.next();
//                current = next2.v;
//                drawLine(current, previous, selected, g2d, disp);
//                previous = current;
//            }
//            drawLine(previous, first, selected, g2d, disp);
//        }


        for (IntersectEntry e : this.points ) {

            Point2d p = e.v;

            int x = (int) disp.xPositionToPixel(p.getX());
            int y = (int) disp.yPositionToPixel(p.getY());
            // g2d.translate(x, y);
            if (selected) {
                g2d.setColor(Color.GREEN.brighter());
                g2d.fillOval(-11 + x, -11 + y, 22, 22);
            }

            if (e instanceof Skeleton.SplitEvent) {

                g2d.setColor(SPLIT_COLOR);
            } else {
                g2d.setColor(EDGE_COLOR);
            }
            g2d.fillOval(-10 + x, -10 + y, 20, 20);
        }

//        for (IntersectEntry e : this.points) {
//
//            Point2d p = e.v;
//
//            g2d.setColor(color);
//
//            int x = (int) disp.xPositionToPixel(p.getX());
//            int y = (int) disp.yPositionToPixel(p.getY());
//            // g2d.translate(x, y);
//            if (selected) {
//                g2d.setColor(Color.GREEN.brighter());
//                g2d.fillOval(-11 + x, -11 + y, 22, 22);
//            }
//            g2d.setColor(color);
//            g2d.fillOval(-10 + x, -10 + y, 20, 20);
//        }
    }

    private void drawLine(Point2d current, Point2d previous, boolean selected, Graphics2D g2d, EquationDisplay disp) {

        int x1 = (int) disp.xPositionToPixel(previous.x);
        int y1 = (int) disp.yPositionToPixel(previous.y);
        int x2 = (int) disp.xPositionToPixel(current.x);
        int y2 = (int) disp.yPositionToPixel(current.y);

        if (selected) {
            Stroke stroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(Color.GREEN.brighter());
            g2d.drawLine(x1, y1, x2, y2); // thick
            g2d.setStroke(stroke);
        }

    }

    @Override
    public Object drawObject() {
        return this.points;
    }

    @Override
    public DisplayRectBounds getBounds() {
        DisplayRectBounds b = new DisplayRectBounds();
        for (IntersectEntry e : this.points) {

            Point2d p = e.v;

            b.addPoint(p);
        }
        return b.toBount();
    }
}
