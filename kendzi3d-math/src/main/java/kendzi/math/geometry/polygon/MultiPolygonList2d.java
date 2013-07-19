/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.polygon;

import java.util.HashSet;
import java.util.Set;

public class MultiPolygonList2d {

    private Set<PolygonList2d> polygons;

    public MultiPolygonList2d(PolygonList2d pPolygon) {
        this();
        this.polygons.add(pPolygon);
    }

    public MultiPolygonList2d(Set<PolygonList2d> pPolygons) {
        this.polygons = pPolygons;
    }

    public MultiPolygonList2d() {
        this(new HashSet<PolygonList2d>());
    }

    /**
     * @return the polygons
     */
    public Set<PolygonList2d> getPolygons() {
        return this.polygons;
    }

    /**
     * @param polygons the polygons to set
     */
    public void setPolygons(Set<PolygonList2d> polygons) {
        this.polygons = polygons;
    }
}

