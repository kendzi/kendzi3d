/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.buildings.output;

import java.util.List;

import javax.vecmath.Point3d;

/**
 * Debug information for roof.
 *
 */
public class RoofDebugOutput {

    /**
     * Starting point of roof outline polygon.
     */
    private Point3d startPoint;

    /**
     * Bounding box for roof.
     */
    private List<Point3d> bbox;

    /**
     * @return the startPoint
     */
    public Point3d getStartPoint() {
        return this.startPoint;
    }

    /**
     * @param startPoint
     *            the startPoint to set
     */
    public void setStartPoint(Point3d startPoint) {
        this.startPoint = startPoint;
    }

    /**
     * @return the bbox
     */
    public List<Point3d> getBbox() {
        return this.bbox;
    }

    /**
     * @param bbox
     *            the bbox to set
     */
    public void setBbox(List<Point3d> bbox) {
        this.bbox = bbox;
    }
}
