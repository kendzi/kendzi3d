package kendzi.math.geometry.debug;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.swing.ui.panel.equation.EquationDisplay;

/**
 * 
 * @author Tomasz Kędziora (kendzi)
 */
public class DisplayPolygonNames extends DisplayObject {

    private List<Point2d> polygon;

    /**
     * @param polygon
     */
    public DisplayPolygonNames(List<Point2d> polygon) {
        super();
        this.polygon = polygon;
    }

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.polygon == null || this.polygon.size() == 0) {
            return;
        }

        for (Point2d p : this.polygon) {

            int x2 = (int) disp.xPositionToPixel(p.x);
            int y2 = (int) disp.yPositionToPixel(p.y);

            if (selected) {
                g2d.setColor(Color.BLACK);

            }
            g2d.setColor(Color.BLACK.brighter().brighter());

            g2d.drawString(p.toString(), x2, y2);
        }
    }

    @Override
    public Object drawObject() {
        return this;
    }

    @Override
    public DisplayRectBounds getBounds() {
        DisplayRectBounds b = new DisplayRectBounds();
        b.addList(polygon);
        return b.toBount();
    }
}
