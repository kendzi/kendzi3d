/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.skeleton;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.vecmath.Point2d;

import kendzi.math.geometry.skeleton.Skeleton.Output;
import kendzi.swing.ui.panel.equation.EquationDisplay;
import kendzi.swing.ui.panel.equation.EquationLayer;
import kendzi.swing.ui.panel.equation.MapComponent;

import org.junit.Test;

public class SkeletonTest {

    @Test
    public void circularAddTest() {
        List<Point2d> polygon = new ArrayList<Point2d>();


        polygon.add(new Point2d(50, 50));
        polygon.add(new Point2d(100, 50));
        polygon.add(new Point2d(100, 100));
        polygon.add(new Point2d(50, 100));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);

        showResult(polygon, sk);

    }
    public void circularAddTest2() {
        List<Point2d> polygon = new ArrayList<Point2d>();

//        polygon.add(new Point2d(65, 77));
//        polygon.add(new Point2d(139, 77));
//        polygon.add(new Point2d(134, 117));
//        polygon.add(new Point2d(53, 120));
        polygon.add(new Point2d(50, 50));
        polygon.add(new Point2d(150, 50));
        polygon.add(new Point2d(150, 100));
        polygon.add(new Point2d(50, 100));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);

        showResult(polygon, sk);

    }

    @Test
    public void circularAddTest3() {
        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(32, 67));
        polygon.add(new Point2d(184, 60));
        polygon.add(new Point2d(122, 142));
        polygon.add(new Point2d(84, 152));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);

        showResult(polygon, sk);

    }

    /**
     * @param polygon
     * @param sk
     */
    public void showResult(List<Point2d> polygon, Output sk) {
        SkeletonTestUi ui = new SkeletonTestUi();
        ui.init();
        ui.start();
        ui.points = polygon;
        ui.setupResults(sk);

        JDialog frame = new JDialog();
        frame.add(ui);
        frame.pack();
        frame.setSize(300, 300);


        ui.repaint();

        frame.setModal(true);
        frame.setVisible(true);
    }
    /**
     * @param polygon
     * @param sk
     */
    public void showResult2(final List<Point2d> polygon, Output sk) {

        MapComponent mc = new MapComponent();
        mc.addLayer(new EquationLayer() {

            @Override
            public void draw(Graphics2D g2d, EquationDisplay disp) {
                if ( polygon == null) {
                    return;
                }

                for (Point2d p : polygon) {

                    g2d.setColor(Color.RED.brighter());

                    int x = (int) disp.xPositionToPixel(p.getX());
                    int y = (int) disp.yPositionToPixel(p.getY());
//                    g2d.translate(x, y);
                    g2d.fillOval(-10 + x, -10 + y, 20, 20);
                }

//                // Graphics2D g2d = (Graphics2D)g.create();
//
//                // g2d.setColor(Color.WHITE);
//                // g2d.fillRect(0, 0, 30, 30);
//                String str = "x: " + RoboUtil.ff(lokalizacja.getX()) + " y: "
//                        + RoboUtil.ff(lokalizacja.getY()) + " th: "
//                        + RoboUtil.ff(lokalizacja.getTh());
//
//                g2d.drawString(str, 100, 20);
//
//                g2d.setColor(Color.RED.brighter());
//
//                int x = (int) disp.xPositionToPixel(lokalizacja.getX());
//                int y = (int) disp.yPositionToPixel(lokalizacja.getY());
//                g2d.translate(x, y);
//                g2d.rotate(-lokalizacja.getTh());
//                g2d.fillOval(-10, -10, 20, 20);
//
//                g2d.setColor(Color.BLUE.brighter());
//                g2d.fillRect(0, -3, 15, 6);

            }


        });


//        mc.createAndShowGUI();



//        MapComponent eq = new MapComponent();
//
//
//        eq.l
//
////        SkeletonTestUi ui = new SkeletonTestUi();
////        ui.init();
////        ui.start();
////        ui.points = polygon;
////        ui.setupResults(sk);
//
        JDialog frame = new JDialog();
        frame.add(mc);
        frame.pack();
        frame.setSize(600, 600);
//
//
////        ui.repaint();
//
        frame.setModal(true);
        frame.setVisible(true);
    }

}
