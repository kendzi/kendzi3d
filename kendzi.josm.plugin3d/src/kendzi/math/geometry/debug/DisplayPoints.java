package kendzi.math.geometry.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.swing.ui.panel.equation.EquationDisplay;

/**
 *
 * @author Tomasz Kędziora (kendzi)
 */
public class DisplayPoints extends DisplayObject {

    private List<Point2d> points;
    private Point2d point;

    /**
     * @param polygon
     */
    public DisplayPoints(Point2d point) {
        super();
        this.points = new ArrayList<Point2d>();
        this.points.add(point);

        this.point = point;
    }
    /**
     * @param polygon
     */
    public DisplayPoints(List<Point2d> polygon) {
        super();
        this.points = polygon;
    }

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.points == null) {
            return;
        }

        for (Point2d p : this.points) {

            g2d.setColor(Color.RED.brighter());

            int x = (int) disp.xPositionToPixel(p.getX());
            int y = (int) disp.yPositionToPixel(p.getY());
            // g2d.translate(x, y);
            if (selected) {
                g2d.setColor(Color.GREEN.brighter());
                g2d.fillOval(-11 + x, -11 + y, 22, 22);
            }
            g2d.setColor(Color.RED.brighter());
            g2d.fillOval(-10 + x, -10 + y, 20, 20);
        }
    }

    @Override
    public Object drawObject() {
        if (this.point != null) {
            return this.point;
        }
        return this.points;
    }

    @Override
    public DisplayRectBounds getBounds() {
        DisplayRectBounds b = new DisplayRectBounds();
        b.addList(this.points);
        return b.toBount();
    }
}
