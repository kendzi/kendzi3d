/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import javax.media.opengl.GL2;

import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.selection.Selectable;

/** Model of OSM objects. Base interface for all objects. Support building and drawing them.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public interface Model extends Selectable {


    /** test if model is in camera visible range.
     * @param pCamraX camera x coordinate
     * @param pCamraY camera y coordinate
     * @param pCamraRange camera see range
     * @return is in camera range
     */
    boolean isInCamraRange(double pCamraX, double pCamraY, double pCamraRange);

    /** TODO .
     * @param pX x coordinate
     * @param pY y coordinate
     * @return distance
     */
    double distance(double pX, double pY);

    /** Object gravity center.
     * @return x middle of object
     */
    double getX();
    /** Object gravity center.
     * @return y middle of object
     */
    double getY();

    /** Size of object.
     * @return max distance from all vertex to middle point of object
     */
    double getRadius();

    /**
     *  Create model vertex and assign textures.
     */
    void buildModel();
    /**
     * Test if model was created.
     * @return is model created
     */
    boolean isBuildModel();

    /** Draw using openGl.
     * @param pGl openGl
     * @param pCamera camera
     */
    void draw(GL2 pGl, Camera pCamera);

    /**
     * When error occurred in time of model building/rendering.
     * @return is error
     */
    boolean isError();

    /**
     * Set if error occurred.
     * @param pError error
     */
    void setError(boolean pError);

//    /** Macher for way.
//     * @return Matcher for way
//     */
//    static Match getWayMatcher();


}
