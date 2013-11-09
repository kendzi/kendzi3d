package kendzi.math.geometry.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.skeleton.Skeleton;
import kendzi.swing.ui.panel.equation.EquationDisplay;

/**
 *
 * @author Tomasz Kędziora (kendzi)
 */
public class DisplaySkeletonOut extends DisplayObject {

    private Skeleton.SkeletonOutput skeletonOut;

    /**
     * @param polygon
     */
    public DisplaySkeletonOut(Skeleton.SkeletonOutput skeletonOut) {
        super();
        this.skeletonOut = skeletonOut;
    }

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.skeletonOut == null) {
            return;
        }

        //        if (this.drawableObjects == null) {
        //            this.drawableObjects = new ArrayList<Polygon>();

        for (List<Point2d> list : this.skeletonOut.getFaces()) {

            Polygon polygon = new Polygon();
            for (Point2d point : list) {
                int x = (int) disp.xPositionToPixel(point.x);
                int y = (int) disp.yPositionToPixel(point.y);

                polygon.addPoint(x, y);
            }

            g2d.setColor(Color.yellow.brighter());
            g2d.fillPolygon(polygon);

            if (selected) {
                Stroke stroke = g2d.getStroke();
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(Color.GREEN.brighter());
                g2d.drawPolygon(polygon);
                g2d.setStroke(stroke);
            }
            g2d.setColor(Color.yellow.darker());
            g2d.drawPolygon(polygon);

            //                this.drawableObjects.add(polygon);
        }



        for (PolygonList2d list : this.skeletonOut.getFaces2()) {

            Polygon polygon = new Polygon();
            for (Point2d point : list.getPoints()) {
                int x = (int) disp.xPositionToPixel(point.x);
                int y = (int) disp.yPositionToPixel(point.y);

                polygon.addPoint(x, y);
            }

            g2d.setColor(Color.blue.brighter());
            g2d.fillPolygon(polygon);

            if (selected) {
                Stroke stroke = g2d.getStroke();
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(Color.GREEN.brighter());
                g2d.drawPolygon(polygon);
                g2d.setStroke(stroke);
            }
            g2d.setColor(Color.yellow.darker());
            g2d.drawPolygon(polygon);

        }
    }

    @Override
    public Object drawObject() {
        return skeletonOut;
    }

    @Override
    public DisplayRectBounds getBounds() {
        return null;
    }
}
