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

import org.joml.Vector3dc;

/**
 * Debug information for roof.
 *
 */
public class RoofDebugOutput {

    /**
     * Starting point of roof outline polygon.
     */
    private Vector3dc startPoint;

    /**
     * Bounding box for roof.
     */
    private List<Vector3dc> bbox;

    /**
     * @return the startPoint
     */
    public Vector3dc getStartPoint() {
        return this.startPoint;
    }

    /**
     * @param startPoint
     *            the startPoint to set
     */
    public void setStartPoint(Vector3dc startPoint) {
        this.startPoint = startPoint;
    }

    /**
     * @return the bbox
     */
    public List<Vector3dc> getBbox() {
        return this.bbox;
    }

    /**
     * @param bbox
     *            the bbox to set
     */
    public void setBbox(List<Vector3dc> bbox) {
        this.bbox = bbox;
    }
}
