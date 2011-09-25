package kendzi.math.geometry.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.swing.ui.panel.equation.EquationDisplay;

/**
 *
 * @author Tomasz Kêdziroa (kendzi)
 */
public class DisplayPolygon extends DisplayObject {

    private List<Point2d> polygon;

    /**
     * @param polygon
     */
    public DisplayPolygon(List<Point2d> polygon) {
        super();
        this.polygon = polygon;
    }

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.polygon == null || this.polygon.size() == 0) {
            return;
        }

        Point2d last = this.polygon.get(this.polygon.size() - 1);
        for (Point2d p : this.polygon) {

            g2d.setColor(Color.RED.brighter());

            // int x = (int) disp.xPositionToPixel(p.getX());
            // int y = (int) disp.yPositionToPixel(p.getY());
            // // g2d.translate(x, y);
            // if (selected) {
            // g2d.setColor(Color.GREEN.brighter());
            // g2d.fillOval(-11 + x, -11 + y, 22, 22);
            // }
            // g2d.setColor(Color.RED.brighter());
            // g2d.fillOval(-10 + x, -10 + y, 20, 20);

            int x1 = (int) disp.xPositionToPixel(last.x);
            int y1 = (int) disp.yPositionToPixel(last.y);
            int x2 = (int) disp.xPositionToPixel(p.x);
            int y2 = (int) disp.yPositionToPixel(p.y);

            if (selected) {
                Stroke stroke = g2d.getStroke();
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(Color.GREEN.brighter());
                g2d.drawLine(x1, y1, x2, y2); // thick
                g2d.setStroke(stroke);
            }

            g2d.setColor(Color.YELLOW.brighter());
            g2d.drawLine(x1, y1, x2, y2);

            if (selected) {
                g2d.setColor(Color.GREEN.brighter());
                g2d.fillOval(-6 + x2, -6 + y2, 12, 12);
            }
            g2d.setColor(Color.RED.brighter());
            g2d.fillOval(-5 + x2, -5 + y2, 10, 10);

            last = p;
        }
    }

    @Override
    public Object drawObject() {
        return polygon;
    }

    @Override
    public DisplayRectBounds getBounds() {
        DisplayRectBounds b = new DisplayRectBounds();
        b.addList(polygon);
        return b.toBount();
    }
}
