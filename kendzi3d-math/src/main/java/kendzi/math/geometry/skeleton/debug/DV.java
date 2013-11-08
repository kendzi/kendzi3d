package kendzi.math.geometry.skeleton.debug;

import java.awt.Color;
import java.util.List;
import java.util.PriorityQueue;

import javax.vecmath.Point2d;

import kendzi.math.geometry.debug.DebugDisplay;
import kendzi.math.geometry.debug.DebugLayer;
import kendzi.math.geometry.debug.DisplayLineParametric2d;
import kendzi.math.geometry.debug.DisplayLineSegment2d;
import kendzi.math.geometry.debug.DisplayObject;
import kendzi.math.geometry.debug.DisplayPoints;
import kendzi.math.geometry.debug.DisplayPolygon;
import kendzi.math.geometry.debug.DisplaySkeletonOut;
import kendzi.math.geometry.line.LineParametric2d;
import kendzi.math.geometry.line.LineSegment2d;
import kendzi.math.geometry.skeleton.CircularList;
import kendzi.math.geometry.skeleton.Skeleton.FaceNode;
import kendzi.math.geometry.skeleton.Skeleton.FaceQueue;
import kendzi.math.geometry.skeleton.Skeleton.IntersectEntry;
import kendzi.math.geometry.skeleton.Skeleton.Output;
import kendzi.math.geometry.skeleton.Skeleton.VertexEntry2;

public class DV {

    public static boolean debug;

    static DebugLayer dv = DebugDisplay.getDebugDisplay().getDebugLayer();

    public static void debug(FaceNode fn) {
        if (debug) {
            dv.addDebug(new DisplayFaceNode(fn, Color.red));
        }
    }

    public static void debug(FaceQueue f) {
        if (debug) {
            dv.addDebug(new DisplayFaceNode(f, Color.pink.darker()));
        }
    }

    public static void debug(IntersectEntry I) {
        if (debug) {
            dv.addDebug(new DisplayIntersectEntry(I, Color.red));
        }
    }

    public static void debug(PriorityQueue<IntersectEntry> queue) {
        if (debug) {
            dv.addDebug(new DisplayEventQueue(queue));
        }
    }

    public static void debug(CircularList<VertexEntry2> l) {
        if (debug) {
            dv.addDebug(new DisplayLav2(l, Color.ORANGE.darker().darker()));
        }
    }

    public static void debug(Output pOutput) {
        if (debug) {
            dv.addDebug(new DisplaySkeletonOut(pOutput));
        }
    }

    public static void debug(LineSegment2d pLineSegment2d) {
        if (debug) {
            dv.addDebug(new DisplayLineSegment2d(pLineSegment2d.getBegin(), pLineSegment2d.getEnd(), Color.GRAY));
        }
    }

    public static void debug(Point2d pPoint2d) {
        if (debug) {
            dv.addDebug(new DisplayPoints(pPoint2d));
        }
    }

    public static void debug(LineParametric2d pLineParametric2d) {
        if (debug) {
            dv.addDebug(new DisplayLineParametric2d(pLineParametric2d));
        }
    }

    public static void debug(List<Point2d> pPolygon) {
        if (debug) {
            dv.addDebug(new DisplayPolygon(pPolygon));
        }
    }

    public static void debug(DisplayObject displayObject) {
        if (debug) {
            dv.addDebug(displayObject);
        }
    }

    public static void clear() {
        if (debug) {
            dv.clear();
        }
    }

    public static void block() {
        if (debug) {
            DebugDisplay.getDebugDisplay().block();
        }
    }
}
