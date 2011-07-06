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
    List<Point3d> rectangle;

    class TextPoint {
        String text;
        Point3d point;

        /**
         * @return the text
         */
        public String getText() {
            return text;
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
            return point;
        }
        /**
         * @param point the point to set
         */
        public void setPoint(Point3d point) {
            this.point = point;
        }
    }
    /**
     * @return the rectangle
     */
    public List<Point3d> getRectangle() {
        return rectangle;
    }

    /**
     * @param rectangle the rectangle to set
     */
    public void setRectangle(List<Point3d> rectangle) {
        this.rectangle = rectangle;
    }
}
