/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.buildings.builder.dto;

import java.util.List;

import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.output.RoofDebugOutput;
import kendzi.math.geometry.line.LineSegment3d;

/**
 * Roof builder output.
 * 
 * @author Tomasz Kedziora (kendzi)
 * 
 */
public class RoofOutput {

    /**
     * Debug information.
     */
    private RoofDebugOutput debug;

    private List<LineSegment3d> edges;

    /**
     * Heights of wall parts under roof.
     */
    private HeightCalculator heightCalculator;

    /**
     * Roof height.
     */
    private double height;

    /**
     * @return the height
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the debug
     */
    public RoofDebugOutput getDebug() {
        return this.debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(RoofDebugOutput debug) {
        this.debug = debug;
    }

    /**
     * @return the heightCalculator
     */
    public HeightCalculator getHeightCalculator() {
        return this.heightCalculator;
    }

    /**
     * @param heightCalculator the heightCalculator to set
     */
    public void setHeightCalculator(HeightCalculator heightCalculator) {
        this.heightCalculator = heightCalculator;
    }

    public List<LineSegment3d> getEdges() {
        return edges;
    }

    public void setEdges(List<LineSegment3d> edges) {
        this.edges = edges;
    }
}
