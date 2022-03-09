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

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.TextCoord;
import org.joml.Vector3dc;

public class MeshFactory {

    public List<FaceFactory> faceFactory = new ArrayList<>();
    /** A list of vertex points */
    public List<Vector3dc> vertices = new ArrayList<>();
    /** A list of normal vectors */
    public List<Vector3dc> normals = new ArrayList<>();
    public List<TextCoord> textCoords = new ArrayList<>();

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

    public int addVertex(Vector3dc p) {
        this.vertices.add(p);
        // XXX
        return this.vertices.size() - 1;
    }

    public int addNormal(Vector3dc v) {
        this.normals.add(v);
        // XXX
        return this.normals.size() - 1;
    }

    public int addTextCoord(TextCoord tc) {
        this.textCoords.add(tc);
        // XXX
        return this.textCoords.size() - 1;
    }

    public Vector3dc getVertex(int i) {

        return this.vertices.get(i);
    }

}
