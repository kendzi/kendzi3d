/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.factory;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.TextCoord;

public class MeshFactory {

    public List<FaceFactory> faceFactory = new ArrayList<FaceFactory>();
    public List<Point3d> vertices = new ArrayList<Point3d>();
    public List<Vector3d> normals = new ArrayList<Vector3d>();
    public List<TextCoord> textCoords = new ArrayList<TextCoord>();

    public int materialID;

    /**
     * If mesh use texture.
     */
    public boolean hasTexture;
    /**
     * Name of mesh.
     */
    public String name;

    public MeshFactory(String pName) {
        this.name = pName;
    }

    public MeshFactory() {
        this(null);
    }

    public static MeshFactory meshBuilder(String string) {
        return new MeshFactory(string);
    }




    public FaceFactory addFace(FaceType faceType, int numOfTextures) {
        FaceFactory ff = new FaceFactory(faceType, numOfTextures);
        this.faceFactory.add(ff);
        return ff;
    }

    public FaceFactory addFace(FaceType faceType) {
        FaceFactory ff = new FaceFactory(faceType);
        this.faceFactory.add(ff);
        return ff;
    }

    public int addVertex(Point3d p) {
        this.vertices.add(p);
        // XXX
        return this.vertices.size() - 1;
    }

    public int addNormal(Vector3d v) {
        this.normals.add(v);
        // XXX
        return this.normals.size() - 1;
    }

    public int addTextCoord(TextCoord tc) {
        this.textCoords.add(tc);
        // XXX
        return this.textCoords.size() - 1;
    }

    public Point3d getVertex(int i) {

        return this.vertices.get(i);
    }

}
