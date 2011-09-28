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
import kendzi.math.geometry.skeleton.debug.DV;
import kendzi.swing.ui.panel.equation.EquationDisplay;
import kendzi.swing.ui.panel.equation.EquationLayer;
import kendzi.swing.ui.panel.equation.MapComponent;

import org.junit.Before;
import org.junit.Test;

public class SkeletonTest {

    DebugLayer dv = DebugDisplay.getDebugDisplay().getDebugLayer();

    @Before
    public void init() {
        DV.debug = true;
    }

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


    //@Test
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
   // @Test
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

   // @Test
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
    public void skeletonTest8() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(-1,  0));
        polygon.add(new Point2d(-1.2, -2));
        polygon.add(new Point2d( 1.2, -2));
        polygon.add(new Point2d( 1,  0.5));
        polygon.add(new Point2d( 2, -0.2));
        polygon.add(new Point2d( 2,  1));
        polygon.add(new Point2d(-2,  1.2));
        polygon.add(new Point2d(-2,  -0.2));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }

    @Test
    public void skeletonTestB1() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(0.7904833761575505,  8.520486967634694));
        polygon.add(new Point2d(5.978418789681697,  8.712497973454056));
        polygon.add(new Point2d(5.95269105167549,  -2.6355979260267777));
        polygon.add(new Point2d(4.566910029680516,  -2.6324561649763485));
        polygon.add(new Point2d(4.5603585630377115,  -5.522203838861205));
        polygon.add(new Point2d(6.043569207647302,  -5.525566487736131));
        polygon.add(new Point2d(6.038049999411376,  -7.960001358506733));
        polygon.add(new Point2d(9.886846028372108,  -7.968727126586532));
        polygon.add(new Point2d(9.902081573281308,  -1.248570683335708));
        polygon.add(new Point2d(13.742215004880482,  -1.2572768087753285));
        polygon.add(new Point2d(13.75400717659087,  3.9440624000165103));
        polygon.add(new Point2d(9.194585721152315,  3.9543992526769878));
        polygon.add(new Point2d(5.823717342947504,  17.30434988614582));
        polygon.add(new Point2d(5.808494957384097,  10.589997844496661));
        polygon.add(new Point2d(-0.13214359029800526,  10.603466113057067));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }
    @Test
    public void skeletonTestB2() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(0.7904833761549828,  8.520486967607015));
        polygon.add(new Point2d(5.9784187896622765,  8.712497973425755));
        polygon.add(new Point2d(5.952691051656153,  -2.6355979260182156));
        polygon.add(new Point2d(4.56691002966568,  -2.632456164967797));
        polygon.add(new Point2d(4.560358563022897,  -5.522203838843264));
        polygon.add(new Point2d(6.0435692076276695,  -5.525566487718182));
        polygon.add(new Point2d(6.038049999391761,  -7.960001358480875));
        polygon.add(new Point2d(9.886846028339992,  -7.968727126560646));
        polygon.add(new Point2d(9.902081573249141,  -1.2485706833316517));
        polygon.add(new Point2d(13.74221500483584,  -1.2572768087712447));
        polygon.add(new Point2d(13.754007176546189,  3.944062400003698));
        polygon.add(new Point2d(9.194585721122445,  3.9543992526641416));
        polygon.add(new Point2d(9.840828592998651,  10.391220834155359));
        polygon.add(new Point2d(-0.24573045314637643,  10.433085818392197));


        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }
    @Test
    public void skeletonTestB3__() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(0.0853589477356087,  -5.32440343246266));
        polygon.add(new Point2d(3.934154976683839,  -5.33312920054243));
        polygon.add(new Point2d(3.9493905215929885,  1.387027242686564));
        polygon.add(new Point2d(7.789523953179687,  1.378321117246971));
        polygon.add(new Point2d(3.2418946694662925,  6.589997178682357));
        polygon.add(new Point2d(-0.4480081827933864,  6.565094698194268));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }
    @Test
    public void skeletonTestB4__() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(-1.192493260706565,  -5.6367673060470285));
        polygon.add(new Point2d(2.656302768241665,  -5.645493074126799));
        polygon.add(new Point2d(6.511671744737513,  1.0659572436626021));
        polygon.add(new Point2d(-1.7258603912355601,  6.252730824609899));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }
    @Test
    public void skeletonTestB5__() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(-1.192493260706565,  -5.6367673060470285));
        polygon.add(new Point2d(2.656302768241665,  -5.645493074126799));
        polygon.add(new Point2d(7.051209343876594,  2.9401404828825903));
        polygon.add(new Point2d(-1.7258603912355601,  6.252730824609899));


        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }
    @Test
    public void skeletonTestB6__() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(-1.192493260706565,  -5.636767306047028));
        polygon.add(new Point2d(2.656302768241665,  -5.645493074126798));
        polygon.add(new Point2d(5.716563703938576,  6.120572646649897));
        polygon.add(new Point2d(-5.985367752852362,  6.423111118668768));
        polygon.add(new Point2d(-6.297731626436729,  -3.6293262553813097));
        polygon.add(new Point2d(-3.4580600517873807,  1.3968924313579514));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }

    @Test
    public void skeletonTestB7__() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(-1.1889778921584675,  -7.356451670462243));
        polygon.add(new Point2d(5.7257149714503175,  -12.035132476438635));
        polygon.add(new Point2d(11.739705976732338,  -17.194940549920428));
        polygon.add(new Point2d(0.8357970425329011,  -1.0288592710693223));
        polygon.add(new Point2d(7.360455718922119,  -6.229013606285628));


        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }
    @Test
    public void skeletonTestB8__() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(-1.1889778921584675,  -7.356451670462243));
        polygon.add(new Point2d(5.7257149714503175,  -12.035132476438635));
        polygon.add(new Point2d(11.739705976732338,  -17.194940549920428));
        polygon.add(new Point2d(0.8357970425329011,  -1.0288592710693223));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }

    @Test
    public void skeletonTest9() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(77,  85));
        polygon.add(new Point2d(198, 85));
        polygon.add(new Point2d(196, 139));
        polygon.add(new Point2d(150,  119));
        polygon.add(new Point2d(157, 177));
        polygon.add(new Point2d(112,  179));
        polygon.add(new Point2d(125,  130));
        polygon.add(new Point2d(68,  118));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }
    @Test
    public void skeletonTestB10() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(23.542862199718826,  -1.0957017437087124));
        polygon.add(new Point2d(12.89581137652037,  1.5573908447103584));
        polygon.add(new Point2d(13.68678342709616,  5.195862274901293));
        polygon.add(new Point2d(30.92997412599037,  6.619611963708646));
        polygon.add(new Point2d(16.53428280871175,  7.568778425199767));
        polygon.add(new Point2d(13.05400578686415,  8.676139297892002));
        polygon.add(new Point2d(-4.188927083681472,  7.336703572978552));
        polygon.add(new Point2d(10.196014852102863,  4.475707108744242));
        polygon.add(new Point2d(8.782756714583655,  1.5573908412810287));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }
    @Test
    public void skeletonTestB11() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(-0.2885918221241157,  14.001053106358517));
        polygon.add(new Point2d(4.899343591400031,  14.19306411217788));
        polygon.add(new Point2d(4.873615853393824,  2.8449682126970464));
        polygon.add(new Point2d(3.4878348313988496,  2.8481099737474747));
        polygon.add(new Point2d(3.4812833647560453,  -0.04163770013738066));
        polygon.add(new Point2d(4.964494009365636,  -0.04500034901230876));
        polygon.add(new Point2d(4.95897480112971,  -2.4794352197829106));
        polygon.add(new Point2d(8.807770830090442,  -2.4881609878627096));
        polygon.add(new Point2d(8.823006374999641,  4.231995455388115));
        polygon.add(new Point2d(12.663139806598815,  4.223289329948495));
        polygon.add(new Point2d(12.674931978309203,  9.424628538740333));
        polygon.add(new Point2d(8.115510522870647,  9.43496539140081));
        polygon.add(new Point2d(4.744642144665839,  22.784916024869645));
        polygon.add(new Point2d(4.729419759102431,  16.070563983220485));
        polygon.add(new Point2d(-1.2112187885796715,  16.08403225178089));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }
    @Test
    public void skeletonTestB12() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(1.6082838074612242,  15.395815413439262));
        polygon.add(new Point2d(6.796219518140479,  15.587826427398873));
        polygon.add(new Point2d(6.7704917786606345,  4.239729879063727));
        polygon.add(new Point2d(5.384710677004972,  4.2428716408656655));
        polygon.add(new Point2d(5.37815921027269,  1.3531237986037645));
        polygon.add(new Point2d(6.861369940123552,  1.3497611512508971));
        polygon.add(new Point2d(6.855850731428608,  -1.084673859531076));
        polygon.add(new Point2d(10.704646980698193,  -1.093399628682226));
        polygon.add(new Point2d(10.719882526622944,  5.626757200629533));
        polygon.add(new Point2d(14.560016178034793,  5.6180510758343525));
        polygon.add(new Point2d(14.571808350563504,  10.819390581977487));
        polygon.add(new Point2d(10.01238663382704,  10.829727434086928));
        polygon.add(new Point2d(6.64151806240239,  24.179678832787182));
        polygon.add(new Point2d(6.626295676252851,  17.465326408838887));
        polygon.add(new Point2d(0.6856567883022331,  17.478794675312955));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

    }
    @Test
    public void skeletonTestB13() {

        dv.clear();

        List<Point2d> polygon = new ArrayList<Point2d>();

        polygon.add(new Point2d(0.0,  0.0));
        polygon.add(new Point2d(-0.03697835689094475,  17.903291653889664));
        polygon.add(new Point2d(9.36122931562474,  17.922703185404146));
        polygon.add(new Point2d(9.399539490923859,  -0.6253964219022965));
        polygon.add(new Point2d(6.897780217346079,  -0.6305636811510293));
        polygon.add(new Point2d(6.907305814387495,  -5.242438102429183));
        polygon.add(new Point2d(9.496043768204736,  -5.2367356072030695));
        polygon.add(new Point2d(9.673537498409361,  -7.819464124646299));
        polygon.add(new Point2d(19.728934851080233,  -7.7986952031890375));
        polygon.add(new Point2d(19.715280237589244,  -1.1877328304801722));
        polygon.add(new Point2d(23.581205989632387,  -1.1797479507986637));
        polygon.add(new Point2d(23.570459756724986,  4.023104657038741));
        polygon.add(new Point2d(19.065027189523686,  4.01379891209519));
        polygon.add(new Point2d(19.009685241927738,  30.807932065847332));
        polygon.add(new Point2d(9.439383865135643,  30.78816508512935));
        polygon.add(new Point2d(9.453189359125524,  24.10415305431124));
        polygon.add(new Point2d(-0.01730198014624129,  24.08459222736407));
        polygon.add(new Point2d(-0.030597953439544412,  30.521916694234474));
        polygon.add(new Point2d(-10.417861267451112,  30.500462317733504));
        polygon.add(new Point2d(-10.354819907553885,  -0.021387367337700525));

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);


        dv.addDebug(new DisplayPolygon(polygon));
        dv.addDebug(new DisplaySkeletonOut(sk));


        DebugDisplay.getDebugDisplay().block();

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

        dv.addDebug(new DisplayPolygon(polygon));

        Output sk = Skeleton.sk(polygon);

        System.out.println(sk);

        dv.addDebug(new DisplayPolygon(polygon));
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
