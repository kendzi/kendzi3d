/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk;

import java.util.List;

import javax.vecmath.Point3d;

public class RoofDebugOut {

    private Point3d startPoint;

    private List<Point3d> bbox;

    class TextPoint {
        String text;
        Point3d point;

        /**
         * @return the text
         */
        public String getText() {
            return this.text;
        }
        /**
         * @param text the text to set
         */
        public void setText(String text) {
            this.text = text;
        }
        /**
         * @return the point
         */
        public Point3d getPoint() {
            return this.point;
        }
        /**
         * @param point the point to set
         */
        public void setPoint(Point3d point) {
            this.point = point;
        }
    }


    /**
     * @return the startPoint
     */
    public Point3d getStartPoint() {
        return this.startPoint;
    }

    /**
     * @param startPoint the startPoint to set
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
     * @param bbox the bbox to set
     */
    public void setBbox(List<Point3d> bbox) {
        this.bbox = bbox;
    }
}
