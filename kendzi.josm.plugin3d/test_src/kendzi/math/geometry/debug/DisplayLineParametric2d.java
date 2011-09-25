package kendzi.math.geometry.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import kendzi.math.geometry.line.LineLinear2d;
import kendzi.math.geometry.line.LineParametric2d;
import kendzi.math.geometry.line.LineSegment2d;
import kendzi.swing.ui.panel.equation.EquationDisplay;

/**
 *
 * @author Tomasz Kêdziroa (kendzi)
 */
public class DisplayLineParametric2d extends DisplayObject {

    private LineParametric2d lineParametric2d;

    /**
     * @param polygon
     */
    public DisplayLineParametric2d(LineParametric2d lineParametric2d) {
        super();
        this.lineParametric2d = lineParametric2d;
    }

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.lineParametric2d == null) {
            return;
        }

        Point2d a = this.lineParametric2d.A;
        Vector2d u = this.lineParametric2d.U;

        int x1 = (int) disp.xPositionToPixel(a.x);
        int y1 = (int) disp.yPositionToPixel(a.y);

        int x2 = (int) disp.xPositionToPixel(a.x + u.x);
        int y2 = (int) disp.yPositionToPixel(a.y + u.y);

        if (selected) {
            Stroke stroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(Color.GREEN.brighter());
            g2d.drawLine(x1, y1, x2, y2); // thick
            g2d.setStroke(stroke);
        }

        g2d.setColor(Color.BLUE.brighter());
        g2d.drawLine(x1, y1, x2, y2);
    }

    private void drawProsta(Graphics2D g2d, EquationDisplay disp, LineLinear2d pLine, double alfa, double dist) {

        double minX = disp.getMinX();
        double minY = disp.getMinY();
        double maxX = disp.getMaxX();
        double maxY = disp.getMaxY();

        List<Point2d> prosta = new ArrayList<Point2d>();

        Point2d p = LineSegment2d.collide(minX, minY, maxX, minY, pLine.A, pLine.B, pLine.C);
        if (p != null) {
            prosta.add(p);
        }

        p = LineSegment2d.collide(maxX, minY, maxX, maxY, pLine.A, pLine.B, pLine.C);
        if (p != null) {
            prosta.add(p);
        }

        p = LineSegment2d.collide(maxX, maxY, minX, maxY, pLine.A, pLine.B, pLine.C);
        if (p != null) {
            prosta.add(p);
        }

        p = LineSegment2d.collide(minX, maxY, minX, minY, pLine.A, pLine.B, pLine.C);
        if (p != null) {
            prosta.add(p);
        }

        if (prosta.size() == 2 ) {
            Point2d p1 = prosta.get(0);
            Point2d p2 = prosta.get(1);

            int x1 = (int) disp.xPositionToPixel(p1.x);
            int y1 = (int) disp.yPositionToPixel(p1.y);
            int x2 = (int) disp.xPositionToPixel(p2.x);
            int y2 = (int) disp.yPositionToPixel(p2.y);

            g2d.drawLine(
                    x1,
                    y1,
                    x2,
                    y2);

        } else if (prosta.size() > 2) {
            System.err.println("something is wrong: " + prosta.size());
        }
    }


    @Override
    public Object drawObject() {
        return this.lineParametric2d;
    }

    @Override
    public DisplayRectBounds getBounds() {

        return null;
    }
}
