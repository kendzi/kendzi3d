/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import kendzi.jogl.util.VertexArrayObject;
import org.joml.Vector3dc;

public class Mesh implements AutoCloseable {
    private List<VertexArrayObject> bufferObject;

    private Face[] face;

    /** An array of vertex points (point) */
    private Vector3dc[] vertices;

    /** An array of vertex normals (vector) */
    private Vector3dc[] normals;

    private TextCoord[] texCoords;

    private String name;

    private int materialID;

    private boolean hasTexture;

    public List<VertexArrayObject> getArrayObject() {
        if (this.bufferObject != null) {
            return this.bufferObject;
        }
        this.generateBufferObject();
        return this.bufferObject;
    }

    private synchronized void generateBufferObject() {
        if (this.bufferObject != null) {
            return;
        }
        List<VertexArrayObject> newBufferObject = new ArrayList<>(this.face.length);
        for (Face currentFace : this.face) {
            VertexArrayObject vao = VertexArrayObject.createVertexArrayObject(
                    IntStream.of(0, currentFace.vertIndex.length / 3).toArray(), currentFace.vertIndex, currentFace.normalIndex,
                    null, null);
            newBufferObject.add(vao);
        }
        this.bufferObject = newBufferObject;
    }

    public TextCoord[] getTexCoords() {
        return this.texCoords;
    }

    public void setTexCoords(final TextCoord[] texCoords) {
        this.texCoords = texCoords;
        this.resetBufferObject();
    }

    public Vector3dc[] getNormals() {
        return this.normals;
    }

    public void setNormals(final Vector3dc[] normals) {
        this.normals = normals;
        this.resetBufferObject();
    }

    public Vector3dc[] getVertices() {
        return this.vertices;
    }

    public void setVertices(final Vector3dc[] vertices) {
        this.vertices = vertices;
        this.resetBufferObject();
    }

    public Face[] getFaces() {
        return this.face;
    }

    public void setFaces(final Face[] face) {
        this.face = face;
        this.resetBufferObject();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getMaterialID() {
        return this.materialID;
    }

    public void setMaterialID(final int materialID) {
        this.materialID = materialID;
    }

    public boolean hasTexture() {
        return this.hasTexture;
    }

    public void setHasTexture(final boolean hasTexture) {
        this.hasTexture = hasTexture;
    }

    private synchronized void resetBufferObject() {
        if (this.bufferObject != null) {
            this.bufferObject.forEach(VertexArrayObject::close);
            this.bufferObject = null;
        }
    }

    @Override
    public void close() {
        this.resetBufferObject();
    }
}
