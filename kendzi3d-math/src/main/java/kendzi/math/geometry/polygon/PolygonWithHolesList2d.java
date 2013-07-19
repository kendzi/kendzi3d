/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.polygon;

import java.util.List;

/**
 * Polygon with holes described by list of points.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class PolygonWithHolesList2d {

    private PolygonList2d outer;

    private List<PolygonList2d> inner;

    public PolygonWithHolesList2d(PolygonList2d outer, List<PolygonList2d> inner) {
        super();
        this.outer = outer;
        this.inner = inner;
    }

    /**
     * @return the outer
     */
    public PolygonList2d getOuter() {
        return this.outer;
    }

    /**
     * @param outer the outer to set
     */
    public void setOuter(PolygonList2d outer) {
        this.outer = outer;
    }

    /**
     * @return the inner
     */
    public List<PolygonList2d> getInner() {
        return this.inner;
    }

    /**
     * @param inner the inner to set
     */
    public void setInner(List<PolygonList2d> inner) {
        this.inner = inner;
    }



}
