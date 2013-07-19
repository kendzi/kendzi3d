package kendzi.math.geometry.skeleton.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.vecmath.Point2d;

import kendzi.math.geometry.debug.DisplayObject;
import kendzi.math.geometry.debug.DisplayRectBounds;
import kendzi.math.geometry.skeleton.CircularList;
import kendzi.math.geometry.skeleton.Skeleton.VertexEntry2;
import kendzi.swing.ui.panel.equation.EquationDisplay;

/**
 *
 * @author Tomasz Kędziora (kendzi)
 */
public class DisplayLav2 extends DisplayObject {

    private  CircularList<VertexEntry2> lav;

    private Color color;


    /**
     * @param LAV
     * @param pColor
     */
    public DisplayLav2( CircularList<VertexEntry2> lav , Color pColor) {
        super();
        this.lav = lav;
        this.color = pColor;
    }

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.lav == null) {
            return;
        }


//        Point2d last = this.points.get(this.points.size() - 1);


        g2d.setColor(color.darker());





        for (VertexEntry2 v2 : lav) {
//            VertexEntry v2 = (VertexEntry) LAVi.next();
            VertexEntry2 v1 = (VertexEntry2) v2.previous();

            Point2d p1 = v1.v;
            Point2d p2 = v2.v;

            g2d.setColor(color);
            drawLine(p1, p2, selected, g2d, disp);
        }

        for (VertexEntry2 v2 : lav) {

            g2d.setColor(color);

            drawPoint(v2.v, selected, g2d, disp);
        }
    }

    /**
     * @param p
     * @param selected
     * @param g2d
     * @param disp
     */
    public void drawPoint(Point2d p, boolean selected, Graphics2D g2d, EquationDisplay disp) {
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
        return this.lav;
    }

    @Override
    public DisplayRectBounds getBounds() {
        return null;
    }
}
