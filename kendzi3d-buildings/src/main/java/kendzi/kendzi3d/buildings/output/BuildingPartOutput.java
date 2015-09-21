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

import kendzi.math.geometry.line.LineSegment3d;

/**
 * Additional debug information for building part.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class BuildingPartOutput {

    /**
     * Debug output for building part roof.
     */
    private RoofDebugOutput roofDebug;

    /**
     * Debug edged for building part.
     */
    private List<LineSegment3d> edges;

    /**
     * @return the roofDebugOut
     */
    public RoofDebugOutput getRoofDebug() {
        return roofDebug;
    }

    /**
     * @param roofDebug
     *            the roofDebugOut to set
     */
    public void setRoofDebug(RoofDebugOutput roofDebug) {
        this.roofDebug = roofDebug;
    }

    /**
     * @return the edges
     */
    public List<LineSegment3d> getEdges() {
        return edges;
    }

    /**
     * @param edges
     *            the edges to set
     */
    public void setEdges(List<LineSegment3d> edges) {
        this.edges = edges;
    }
}
