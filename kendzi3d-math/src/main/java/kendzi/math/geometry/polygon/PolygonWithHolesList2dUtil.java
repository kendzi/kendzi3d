package kendzi.math.geometry.polygon;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.point.TransformationMatrix2d;

import org.ejml.simple.SimpleMatrix;

public class PolygonWithHolesList2dUtil {

    public static PolygonWithHolesList2d transform(PolygonWithHolesList2d polygon, SimpleMatrix transformMatrix) {

        PolygonList2d outer = polygon.getOuter();

        List<Point2d> outerList = TransformationMatrix2d.transformList(outer.getPoints(), transformMatrix);

        List<PolygonList2d> inner = polygon.getInner();

        List<PolygonList2d> innerLists = null;

        if (inner != null) {
            innerLists = new ArrayList<PolygonList2d>();

            for (PolygonList2d i : inner) {
                innerLists.add(new PolygonList2d(TransformationMatrix2d.transformList(i.getPoints(), transformMatrix)));
            }
        }
        return new PolygonWithHolesList2d(new PolygonList2d(outerList), innerLists);
    }

    public static List<List<Point2d>> getListOfHolePoints(PolygonWithHolesList2d polygon) {
        List<List<Point2d>> ret = new ArrayList<List<Point2d>>();
        List<PolygonList2d> inner = polygon.getInner();
        if (inner != null) {
            for (PolygonList2d p : inner) {
                ret.add(p.getPoints());
            }
        }
        return ret;
    }
}
