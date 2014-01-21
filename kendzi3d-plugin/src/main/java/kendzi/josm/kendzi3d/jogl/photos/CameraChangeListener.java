/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.photos;



public interface CameraChangeListener {
    // event dispatch methods
//    somethingHappened(PhotoChangeEvent e);
//
//    somethingElseHappened(PhotoChangeEvent e);
    public abstract void dispatchCameraChange(CameraChangeEvent cameraChangeEvent);


}
