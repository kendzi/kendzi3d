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

public class SplitPolygons {
    List<SplitPolygon> splitPolygons = new ArrayList<>();

    @Deprecated
    public List<List<Vector2dc>> getLeftPolygons() {
        List<List<Vector2dc>> ret = new ArrayList<>();

        for (SplitPolygon s : this.splitPolygons) {
            ret.addAll(s.getLeftPolygons());
        }
        return ret;
    }

    public void add(SplitPolygon partPolygon) {
        this.splitPolygons.add(partPolygon);
    }

    @Deprecated
    public List<List<Vector2dc>> getRightPolygons() {
        List<List<Vector2dc>> ret = new ArrayList<>();

        for (SplitPolygon s : this.splitPolygons) {
            ret.addAll(s.getRightPolygons());
        }
        return ret;
    }

    public MultiPolygonList2d getTopMultiPolygons() {

        MultiPolygonList2d mp = new MultiPolygonList2d();
        Set<PolygonList2d> polygons = mp.getPolygons();

        for (SplitPolygon s : this.splitPolygons) {

            polygons.addAll(s.getTopMultiPolygons().getPolygons());
        }
        return mp;
    }

    public MultiPolygonList2d getBottomMultiPolygons() {

        MultiPolygonList2d mp = new MultiPolygonList2d();
        Set<PolygonList2d> polygons = mp.getPolygons();

        for (SplitPolygon s : this.splitPolygons) {

            polygons.addAll(s.getBottomMultiPolygons().getPolygons());
        }
        return mp;
    }

}
