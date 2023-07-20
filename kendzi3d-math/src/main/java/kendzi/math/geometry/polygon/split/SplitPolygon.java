/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.polygon.split;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;
import org.joml.Vector2dc;

public class SplitPolygon {

    /** Polygon points */
    List<Vector2dc> polygonExtanded = new ArrayList<>();
    List<List<Integer>> polygonsLeft = new ArrayList<>();
    List<List<Integer>> polygonsRight = new ArrayList<>();

    @Deprecated
    public List<List<Vector2dc>> getLeftPolygons() {

        List<List<Vector2dc>> ret = new ArrayList<>();

        for (List<Integer> p : this.polygonsLeft) {
            List<Vector2dc> polygon = makeListFromIndex(this.polygonExtanded, p);
            ret.add(polygon);
        }
        return ret;

    }

    public MultiPolygonList2d getBottomMultiPolygons() {

        MultiPolygonList2d mp = new MultiPolygonList2d();
        Set<PolygonList2d> polygons = mp.getPolygons();

        for (List<Integer> p : this.polygonsLeft) {
            List<Vector2dc> polygon = makeListFromIndex(this.polygonExtanded, p);

            PolygonList2d polygonList = new PolygonList2d(polygon);
            polygons.add(polygonList);
        }
        return mp;
    }

    @Deprecated
    public List<List<Vector2dc>> getRightPolygons() {

        List<List<Vector2dc>> ret = new ArrayList<>();

        for (List<Integer> p : this.polygonsRight) {
            List<Vector2dc> polygon = makeListFromIndex(this.polygonExtanded, p);
            ret.add(polygon);
        }
        return ret;

    }

    public MultiPolygonList2d getTopMultiPolygons() {

        MultiPolygonList2d mp = new MultiPolygonList2d();
        Set<PolygonList2d> polygons = mp.getPolygons();

        for (List<Integer> p : this.polygonsRight) {
            List<Vector2dc> polygon = makeListFromIndex(this.polygonExtanded, p);

            PolygonList2d polygonList = new PolygonList2d(polygon);
            polygons.add(polygonList);
        }
        return mp;
    }

    private List<Vector2dc> makeListFromIndex(List<Vector2dc> borderExtanded, List<Integer> polyIndex) {

        List<Vector2dc> ret = new ArrayList<>(polyIndex.size());
        for (Integer i : polyIndex) {
            ret.add(borderExtanded.get(i));
        }
        return ret;
    }

    /**
     * @return the polygonExtanded
     */
    public List<Vector2dc> getPolygonExtanded() {
        return this.polygonExtanded;
    }

    /**
     * @param polygonExtanded
     *            the polygonExtanded to set
     */
    public void setPolygonExtanded(List<Vector2dc> polygonExtanded) {
        this.polygonExtanded = polygonExtanded;
    }

    /**
     * @return the polygonsLeft
     */
    public List<List<Integer>> getPolygonsLeft() {
        return this.polygonsLeft;
    }

    /**
     * @param polygonsLeft
     *            the polygonsLeft to set
     */
    public void setPolygonsLeft(List<List<Integer>> polygonsLeft) {
        this.polygonsLeft = polygonsLeft;
    }

    /**
     * @return the polygonsRight
     */
    public List<List<Integer>> getPolygonsRight() {
        return this.polygonsRight;
    }

    /**
     * @param polygonsRight
     *            the polygonsRight to set
     */
    public void setPolygonsRight(List<List<Integer>> polygonsRight) {
        this.polygonsRight = polygonsRight;
    }

}