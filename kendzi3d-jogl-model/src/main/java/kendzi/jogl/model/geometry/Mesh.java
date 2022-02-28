/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Mesh {

    public Face[] face;

    public Point3d[] vertices;

    public Vector3d[] normals;

    public TextCoord[] texCoords;

    public String name;

    public int materialID;

    public boolean hasTexture;

}
