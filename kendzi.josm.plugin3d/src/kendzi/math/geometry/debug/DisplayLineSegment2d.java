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
 * @author Tomasz Kędziora (kendzi)
 */
public class DisplayLineSegment2d extends DisplayObject {

    private LineSegment2d lineSegment2d;

    private boolean points;

    private Color color;

    /**
     * @param p1
     * @param p2
     * @param pColor
     */
    public DisplayLineSegment2d(Point2d p1, Point2d p2, Color pColor) {
        super();

        if (p1 == null || p2 == null) {
            throw new RuntimeException("p1 and p2 can't be null");
        }

        this.lineSegment2d = new LineSegment2d(p1, p2) {
            @Override
            public boolean equals(Object obj) {
                if (obj instanceof LineSegment2d) {
                    LineSegment2d line = (LineSegment2d) obj;

                    return (line.getBegin() == this.getBegin()) && (line.getEnd() == this.getEnd());

                }
                return false;
            }

            @Override
            public int hashCode() {

                return this.getBegin().hashCode() + 7 * this.getEnd().hashCode();
            }
        };

        this.points = true;
        this.color = Color.GRAY.darker();
    }

    /**
     * @param pLineSegment2d
     *
     */
    public DisplayLineSegment2d(LineSegment2d pLineSegment2d) {
        super();
        this.lineSegment2d = pLineSegment2d;
        this.color = Color.GRAY.darker();
    }

    /**
     * @param pLineSegment2d
     * @param pColor
     */
    public DisplayLineSegment2d(LineSegment2d pLineSegment2d, Color pColor) {
        super();
        this.lineSegment2d = pLineSegment2d;
        this.color = pColor;

    }

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.lineSegment2d == null) {
            return;
        }

        Point2d begin = this.lineSegment2d.getBegin();
        Point2d end = this.lineSegment2d.getEnd();

        int x1 = (int) disp.xPositionToPixel(begin.x);
        int y1 = (int) disp.yPositionToPixel(begin.y);

        int x2 = (int) disp.xPositionToPixel(end.x);
        int y2 = (int) disp.yPositionToPixel(end.y);

        if (selected) {
            Stroke stroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(Color.GREEN.brighter());
            g2d.drawLine(x1, y1, x2, y2); // thick
            g2d.setStroke(stroke);
        }

        g2d.setColor(color);
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
        return this.lineSegment2d;
    }

    @Override
    public DisplayRectBounds getBounds() {

        return null;
    }
}
