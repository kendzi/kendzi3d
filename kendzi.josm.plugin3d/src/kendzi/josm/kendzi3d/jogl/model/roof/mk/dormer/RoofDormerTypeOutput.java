/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer;

import java.util.List;

import kendzi.jogl.model.factory.MeshFactory;

import org.ejml.data.SimpleMatrix;

public class RoofDormerTypeOutput {

    List<MeshFactory> mesh;

    SimpleMatrix transformationMatrix;



    /**
     * @return the transformationMatrix
     */
    public SimpleMatrix getTransformationMatrix() {
        return transformationMatrix;
    }

    /**
     * @param transformationMatrix the transformationMatrix to set
     */
    public void setTransformationMatrix(SimpleMatrix transformationMatrix) {
        this.transformationMatrix = transformationMatrix;
    }

    /**
     * @return the mesh
     */
    public List<MeshFactory> getMesh() {
        return mesh;
    }

    /**
     * @param mesh the mesh to set
     */
    public void setMesh(List<MeshFactory> mesh) {
        this.mesh = mesh;
    }

}
