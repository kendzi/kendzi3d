package kendzi.math.geometry.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import javax.vecmath.Point2d;

import kendzi.swing.ui.panel.equation.EquationDisplay;

/**
 *
 * @author Tomasz Kędziora (kendzi)
 */
public class DisplayCollectionOfPoints extends DisplayObject {

    private Collection<Point2d> points;

    private Color color;

    /**
     * @param points
     */
    public DisplayCollectionOfPoints(Collection<Point2d> points) {
        this(points, Color.RED.brighter());
    }

    /**
     * @param polygon
     * @param pColor
     */
    public DisplayCollectionOfPoints(Collection<Point2d> polygon, Color pColor) {
        super();
        this.points = polygon;
        this.color = pColor;
    }

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.points == null) {
            return;
        }

        for (Point2d p : this.points) {

            g2d.setColor(color);

            int x = (int) disp.xPositionToPixel(p.x);
            int y = (int) disp.yPositionToPixel(p.y);
            // g2d.translate(x, y);
            if (selected) {
                g2d.setColor(Color.GREEN.brighter());
                g2d.fillOval(-11 + x, -11 + y, 22, 22);
            }
            g2d.setColor(color);
            g2d.fillOval(-10 + x, -10 + y, 20, 20);
        }
    }

    @Override
    public Object drawObject() {
        return this.points;
    }

    @Override
    public DisplayRectBounds getBounds() {
        DisplayRectBounds b = new DisplayRectBounds();
        for (Point2d p : this.points) {
            b.addPoint(p);
        }
        return b.toBount();
    }
}
