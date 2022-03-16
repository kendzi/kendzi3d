/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.dto;

import java.util.List;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.kendzi3d.buildings.builder.height.HeightCalculator;
import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space.RoofHooksSpaces;
import org.ejml.simple.SimpleMatrix;
import org.joml.Vector3dc;

public class RoofTypeOutput {

    /**
     * XXX this should by changed!
     */
    List<Vector3dc> rectangle;

    // ModelFactory model;
    List<MeshFactory> mesh;

    RoofHooksSpaces roofHooksSpaces;

    double height;

    SimpleMatrix transformationMatrix;

    /**
     * Heights of wall parts under roof.
     */
    private HeightCalculator heightCalculator;

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the transformationMatrix
     */
    public SimpleMatrix getTransformationMatrix() {
        return transformationMatrix;
    }

    /**
     * @param transformationMatrix
     *            the transformationMatrix to set
     */
    public void setTransformationMatrix(SimpleMatrix transformationMatrix) {
        this.transformationMatrix = transformationMatrix;
    }

    /**
     * @return the rectangle
     */
    public List<Vector3dc> getRectangle() {
        return rectangle;
    }

    /**
     * @param rectangle
     *            the rectangle to set
     */
    public void setRectangle(List<Vector3dc> rectangle) {
        this.rectangle = rectangle;
    }

    /**
     * @return the roofHooksSpaces
     */
    public RoofHooksSpaces getRoofHooksSpaces() {
        return roofHooksSpaces;
    }

    /**
     * @param roofHooksSpaces
     *            the roofHooksSpaces to set
     */
    public void setRoofHooksSpaces(RoofHooksSpaces roofHooksSpaces) {
        this.roofHooksSpaces = roofHooksSpaces;
    }

    /**
     * @return the mesh
     */
    public List<MeshFactory> getMesh() {
        return mesh;
    }

    /**
     * @param mesh
     *            the mesh to set
     */
    public void setMesh(List<MeshFactory> mesh) {
        this.mesh = mesh;
    }

    /**
     * @return the heightCalculator
     */
    public HeightCalculator getHeightCalculator() {
        return heightCalculator;
    }

    /**
     * @param heightCalculator
     *            the heightCalculator to set
     */
    public void setHeightCalculator(HeightCalculator heightCalculator) {
        this.heightCalculator = heightCalculator;
    }
}
