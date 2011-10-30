package kendzi.math.geometry.skeleton.debug;

import java.awt.Color;
import java.awt.Graphics2D;

import kendzi.math.geometry.debug.DisplayObject;
import kendzi.math.geometry.debug.DisplayRectBounds;
import kendzi.math.geometry.debug.DrawUtil;
import kendzi.math.geometry.skeleton.Skeleton;
import kendzi.math.geometry.skeleton.Skeleton.EdgeEvent;
import kendzi.math.geometry.skeleton.Skeleton.IntersectEntry;
import kendzi.math.geometry.skeleton.Skeleton.SplitEvent;
import kendzi.swing.ui.panel.equation.EquationDisplay;

/**
 *
 * @author Tomasz Kędziora (kendzi)
 */
public class DisplayIntersectEntry extends DisplayObject {

    private  IntersectEntry intersect;

    private Color color;



    public DisplayIntersectEntry( IntersectEntry f , Color pColor) {
        super();
        this.intersect = f;
        this.color = pColor;
    }



    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected) {

        if (this.intersect == null) {
            return;
        }


//        Point2d last = this.points.get(this.points.size() - 1);




        if (intersect instanceof Skeleton.SplitEvent) {
            g2d.setColor(DisplayEventQueue.SPLIT_COLOR);
        } else {
            g2d.setColor(DisplayEventQueue.EDGE_COLOR);
        }

        DrawUtil.drawPoint(intersect.v, selected, g2d, disp);


        if (intersect instanceof Skeleton.SplitEvent) {
            SplitEvent split = (SplitEvent) this.intersect;

            if (split.V.processed) {
                g2d.setColor(Color.GRAY.brighter());
            } else {
                g2d.setColor(Color.GRAY.darker());
            }

            DrawUtil.drawPoint(split.V.v, selected, g2d, disp);

            DrawUtil.drawLine(intersect.v, split.V.v, selected, g2d, disp);

            g2d.setColor(Color.GRAY);
            DrawUtil.drawLine(split.opositeEdge.p1, split.opositeEdge.p2, selected, g2d, disp);
        }

        if (intersect instanceof EdgeEvent) {
            EdgeEvent split = (EdgeEvent) this.intersect;

            if (split.Va.processed) {
                g2d.setColor(Color.GRAY.brighter());
            } else {
                g2d.setColor(Color.GRAY.darker());
            }

            DrawUtil.drawPoint(split.Va.v, selected, g2d, disp);

            DrawUtil.drawLine(intersect.v, split.Va.v, selected, g2d, disp);

            if (split.Vb.processed) {
                g2d.setColor(Color.GRAY.brighter());
            } else {
                g2d.setColor(Color.GRAY.darker());
            }

            DrawUtil.drawPoint(split.Vb.v, selected, g2d, disp);

            DrawUtil.drawLine(intersect.v, split.Vb.v, selected, g2d, disp);
        }






    }


    @Override
    public Object drawObject() {
        return this.intersect;
    }

    @Override
    public DisplayRectBounds getBounds() {
        return null;
    }
}
