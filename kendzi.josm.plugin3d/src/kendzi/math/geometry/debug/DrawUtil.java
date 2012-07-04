package kendzi.math.geometry.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.vecmath.Point2d;

import kendzi.swing.ui.panel.equation.EquationDisplay;

public class DrawUtil {


    /**
     * @param p
     * @param selected
     * @param g2d
     * @param disp
     */
    public static void drawPoint(Point2d p, boolean selected, Graphics2D g2d, EquationDisplay disp) {
        int x = (int) disp.xPositionToPixel(p.x);
        int y = (int) disp.yPositionToPixel(p.y);
        // g2d.translate(x, y);
        Color color = g2d.getColor();
        if (selected) {
            g2d.setColor(Color.GREEN.brighter());
            g2d.fillOval(-11 + x, -11 + y, 22, 22);
        }
        g2d.setColor(color);
        g2d.fillOval(-10 + x, -10 + y, 20, 20);
    }


    /**
     * @param current
     * @param previous
     * @param selected
     * @param g2d
     * @param disp
     */
    public static void drawLine(Point2d current, Point2d previous, boolean selected, Graphics2D g2d, EquationDisplay disp) {

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

}
