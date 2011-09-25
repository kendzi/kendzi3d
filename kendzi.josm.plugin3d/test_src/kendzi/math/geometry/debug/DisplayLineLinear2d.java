package kendzi.math.geometry.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.line.LineLinear2d;
import kendzi.math.geometry.line.LineSegment2d;
import kendzi.swing.ui.panel.equation.EquationDisplay;

/**
 *
 * @author Tomasz Kêdziroa (kendzi)
 */
public class DisplayLineLinear2d extends DisplayObject {

    private LineLinear2d lineLinear2d;

    /**
     * @param polygon
     */
    public DisplayLineLinear2d(LineLinear2d lineLinear2d) {
        super();
        this.lineLinear2d = lineLinear2d;
    }

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.lineLinear2d == null) {
            return;
        }

        if (selected) {
            Stroke stroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(Color.GREEN.brighter());
            // thick
            drawLineLinear(g2d, disp, this.lineLinear2d);
            g2d.setStroke(stroke);
        }

        g2d.setColor(Color.BLUE.brighter());
        drawLineLinear(g2d, disp, this.lineLinear2d);

    }

    private void drawLineLinear(Graphics2D g2d, EquationDisplay disp, LineLinear2d pLine) {
        // XXX test it !
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

        if (prosta.size() == 2) {
            Point2d p1 = prosta.get(0);
            Point2d p2 = prosta.get(1);

            int x1 = (int) disp.xPositionToPixel(p1.x);
            int y1 = (int) disp.yPositionToPixel(p1.y);
            int x2 = (int) disp.xPositionToPixel(p2.x);
            int y2 = (int) disp.yPositionToPixel(p2.y);

            g2d.drawLine(x1, y1, x2, y2);

        } else if (prosta.size() > 2) {
            System.err.println("something is wrong: " + prosta.size());
        }
    }

    @Override
    public Object drawObject() {
        return this.lineLinear2d;
    }

    @Override
    public DisplayRectBounds getBounds() {

        return null;
    }
}
