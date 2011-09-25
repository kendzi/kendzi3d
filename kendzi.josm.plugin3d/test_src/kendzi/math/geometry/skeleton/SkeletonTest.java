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

import kendzi.math.geometry.debug.DebugDisplay;
import kendzi.math.geometry.debug.DebugLayer;
import kendzi.math.geometry.debug.DisplayPolygon;
import kendzi.math.geometry.debug.DisplaySkeletonOut;
import kendzi.math.geometry.skeleton.Skeleton.Output;
import kendzi.swing.ui.panel.equation.EquationDisplay;
import kendzi.swing.ui.panel.equation.EquationLayer;
import kendzi.swing.ui.panel.equation.MapComponent;

import org.junit.Test;

public class SkeletonTest {

    DebugLayer dv = DebugDisplay.getDebugDisplay().getDebugLayer();


    @Test
    public void skeletonTest5() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();


        polygon.add(new Point2d(-2,0));
        polygon.add(new Point2d(-1, -1));
        polygon.add(new Point2d(0, 0));
        polygon.add(new Point2d(1, -1));
        polygon.add(new Point2d(2, 0));
        polygon.add(new Point2d(1, 1));
        polygon.add(new Point2d(-1, 1));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();


//        showResult2(polygon, sk);

    }


    @Test
    public void skeletonTest6() {

        dv.clear();

        List<Point2d> inner = new ArrayList<Point2d>();


        inner.add(new Point2d(-1,1));
        inner.add(new Point2d(1, 1));
        inner.add(new Point2d(1, -1));
        inner.add(new Point2d(0, -1));
        inner.add(new Point2d(0, 0));

        List<Point2d> outer = new ArrayList<Point2d>();
        outer.add(new Point2d(-2, -2));
        outer.add(new Point2d(2, -2));
        outer.add(new Point2d(2, 2));
        outer.add(new Point2d(-2, 2));


        List<List<Point2d>> innerList = new ArrayList<List<Point2d>>();
        innerList.add(inner);

//        polygon.add(new Point2d(1, 1));
//        polygon.add(new Point2d(-1, 1));

        dv.addDebug(new DisplayPolygon(outer));
        dv.addDebug(new DisplayPolygon(inner));

        Output sk = Skeleton.sk(outer, innerList);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(inner));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();


//        showResult2(polygon, sk);

    }
    @Test
    public void skeletonTest6_1() {

        dv.clear();

        List<Point2d> inner = new ArrayList<Point2d>();


        inner.add(new Point2d(-1,1));
        inner.add(new Point2d(1, 1));
        inner.add(new Point2d(1, -1));
        inner.add(new Point2d(0, -1));
        inner.add(new Point2d(0, 0));

        List<Point2d> outer = new ArrayList<Point2d>();
        outer.add(new Point2d(-2, -2));
        outer.add(new Point2d(2, -2));
        outer.add(new Point2d(2, 2));
        outer.add(new Point2d(-2, 2));


        List<List<Point2d>> innerList = new ArrayList<List<Point2d>>();
        innerList.add(inner);

//        polygon.add(new Point2d(1, 1));
//        polygon.add(new Point2d(-1, 1));

        dv.addDebug(new DisplayPolygon(outer));
        dv.addDebug(new DisplayPolygon(inner));

        Output sk = Skeleton.sk(outer, innerList);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(inner));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();


//        showResult2(polygon, sk);

    }

    @Test
    public void skeletonTest6_9() {

        dv.clear();

        List<Point2d> inner = new ArrayList<Point2d>();

//
//        inner.add(new Point2d(-1,1));
//        inner.add(new Point2d(1, 1));
//        inner.add(new Point2d(1, -1));
//        inner.add(new Point2d(-1, -1));
//        inner.add(new Point2d(-2, 0));

        inner.add(new Point2d(119,158));
        inner.add(new Point2d(259, 159));
        inner.add(new Point2d(248, 63));
        inner.add(new Point2d(126, 60));
        inner.add(new Point2d(90, 106));

//        List<Point2d> outer = new ArrayList<Point2d>();
//        outer.add(new Point2d(-2, -2));
//        outer.add(new Point2d(2, -2));
//        outer.add(new Point2d(2, 2));
//        outer.add(new Point2d(-2, 2));
//
//
//        List<List<Point2d>> innerList = new ArrayList<List<Point2d>>();
//        innerList.add(inner);

//        polygon.add(new Point2d(1, 1));
//        polygon.add(new Point2d(-1, 1));

//        dv.addDebug(new DisplayPolygon(outer));
        dv.addDebug(new DisplayPolygon(inner));

        Output sk = Skeleton.sk(inner);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(inner));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();


//        showResult2(polygon, sk);

    }

    @Test
    public void skeletonTest7() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0, 0));
        polygon.add(new Point2d(0, -1));
        polygon.add(new Point2d(1, -1));
        polygon.add(new Point2d(1, 1));
        polygon.add(new Point2d(-1,1));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();


//        showResult2(polygon, sk);

    }

    @Test
    public void circularAddTest() {

//        DebugLayer dv = createDebugView();

        DebugLayer dv = DebugDisplay.getDebugDisplay().getDebugLayer();
        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();


        polygon.add(new Point2d(50, 50));
        polygon.add(new Point2d(100, 50));
        polygon.add(new Point2d(100, 100));
        polygon.add(new Point2d(50, 100));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);

        dv.addDebug("border", new DisplayPolygon(polygon));

        dv.addDebug(new DisplaySkeletonOut(sk));

        DebugDisplay.getDebugDisplay().block();

       // showResult2(polygon, sk);

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

    DebugLayer createDebugView() {
//        DebugLayer ret1 = null;
        DebugLayer ret = new DebugLayer();

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
//                new UiFrame().setVisible(true);
            DebugLayer ret = new DebugLayer();

            MapComponent mc = new MapComponent();
            mc.addLayer(ret);

            JDialog frame = new JDialog();
            frame.add(mc);
            frame.pack();
            frame.setSize(600, 600);
    //
    //
    ////        ui.repaint();
    //
            frame.setModal(false);
            frame.setVisible(true);

//             ret1 = ret;
            }
        });

        return ret;
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
