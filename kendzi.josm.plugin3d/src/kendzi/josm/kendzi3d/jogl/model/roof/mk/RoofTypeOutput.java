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

import kendzi.jogl.model.factory.ModelFactory;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.dormer.RoofHooksSpace;

import org.ejml.data.SimpleMatrix;

public class RoofTypeOutput {

    List<Point3d> rectangle;

    ModelFactory model;

    RoofHooksSpace [] roofHooksSpaces;

    double height;

//    double [] transformationMatrix = new double [9];
    SimpleMatrix transformationMatrix;



    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the model
     */
    public ModelFactory getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(ModelFactory model) {
        this.model = model;
    }

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
     * @return the roofHooksSpaces
     */
    public RoofHooksSpace[] getRoofHooksSpaces() {
        return roofHooksSpaces;
    }

    /**
     * @param roofHooksSpaces the roofHooksSpaces to set
     */
    public void setRoofHooksSpaces(RoofHooksSpace[] roofHooksSpaces) {
        this.roofHooksSpaces = roofHooksSpaces;
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
