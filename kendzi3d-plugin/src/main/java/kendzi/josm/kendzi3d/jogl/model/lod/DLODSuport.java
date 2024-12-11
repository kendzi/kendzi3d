/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.lod;

import kendzi.jogl.camera.Camera;
import org.joml.Vector3dc;

public interface DLODSuport {
    // FIXME

    boolean isModelBuild(LOD pLod);

    void draw(Camera camera, LOD pLod);

    void buildModel(LOD pLod);

    Vector3dc getPoint();
}
