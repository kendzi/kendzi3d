/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry;

import org.joml.Vector3dc;

public class Mesh {

    public Face[] face;

    /** An array of vertex points (point) */
    public Vector3dc[] vertices;

    /** An array of vertex normals (vector) */
    public Vector3dc[] normals;

    public TextCoord[] texCoords;

    public String name;

    public int materialID;

    public boolean hasTexture;

}
