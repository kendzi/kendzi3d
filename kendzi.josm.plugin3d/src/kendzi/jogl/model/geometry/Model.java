/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry;

import java.util.Vector;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.geometry.material.Material;


public class Model {

    /**
     * Model meshs.
     */
    public Mesh [] mesh;

    //FIXME
    public Vector<Material> materials = new Vector<Material>();

    /**
     * Model use textures.
     */
    public boolean useTexture;

    /**
     * Model use light.
     */
    public boolean useLight;

    /**
     * Model use scale.
     */
    public boolean useScale;

    /**
     * Model name/source.
     */
    protected String source;

    /** FIXME Bounds of the model */
    protected Bounds bounds = new Bounds();
    /** FIXME Center point of the model */
    private Point3d centerPoint = new Point3d (0.0f, 0.0f, 0.0f);

    public Vector3d scale = new Vector3d(1d, 1d, 1d);

    //FIXME
    int numberOfMaterials;

    /**
     * Draw normal vectors.
     */
    public boolean drawNormals;

    /**
     * Draw model vertex.
     */
    public boolean drawVertex;

    /**
     * Draw mesh edges.
     */
    public boolean drawEdges;

    //TODO drawObjectBounds
    //TODO drawModelBounds

    /**
     * Default constructor.
     */
    public Model() {
        this("");
    }

    /**
     * Constructor.
     *
     * @param pName model name
     */
    public Model(String pName) {
        // FIXME !
        this.source = pName;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * @param pSource the source to set
     */
    public void setSource(String pSource) {
        this.source = pSource;
    }

    /**
     * @return the bounds
     */
    public Bounds getBounds() {
        return this.bounds;
    }

    /**
     * @param pBounds the bounds to set
     */
    public void setBounds(Bounds pBounds) {
        this.bounds = pBounds;
    }

    /**
     * @return the centerPoint
     */
    public Point3d getCenterPoint() {
        return this.centerPoint;
    }

    /**
     * @param pCenterPoint the centerPoint to set
     */
    public void setCenterPoint(Point3d pCenterPoint) {
        this.centerPoint = pCenterPoint;
    }

    // FIXME Add material
    public void addMaterial(Material pMaterial) {
        this.materials.add(pMaterial);
    }
    // FIXME Get material
    public Material getMaterial(int pIndex) {
        return this.materials.get(pIndex);
    }

    /**
     * @return the numberOfMaterials
     */
    public int getNumberOfMaterials() {
        return this.materials.size(); //this.numberOfMaterials;
    }

    /**
     * @return the useTexture
     */
    public boolean isUseTexture() {
        return this.useTexture;
    }

    /**
     * @param pUseTexture the useTexture to set
     */
    public void setUseTexture(boolean pUseTexture) {
        this.useTexture = pUseTexture;
    }

    /**
     * @return the useLight
     */
    public boolean isUseLight() {
        return this.useLight;
    }

    /**
     * @param pUseLight the useLight to set
     */
    public void setUseLight(boolean pUseLight) {
        this.useLight = pUseLight;
    }

    /**
     * @return the useScale
     */
    public boolean isUseScale() {
        return this.useScale;
    }

    /**
     * @param pUseScale the useScale to set
     */
    public void setUseScale(boolean pUseScale) {
        this.useScale = pUseScale;
    }

    /**
     * @return the drawNormals
     */
    public boolean isDrawNormals() {
        return this.drawNormals;
    }

    /**
     * @param pDrawNormals the drawNormals to set
     */
    public void setDrawNormals(boolean pDrawNormals) {
        this.drawNormals = pDrawNormals;
    }

    /**
     * @return the drawVertex
     */
    public boolean isDrawVertex() {
        return this.drawVertex;
    }

    /**
     * @param pDrawVertex the drawVertex to set
     */
    public void setDrawVertex(boolean pDrawVertex) {
        this.drawVertex = pDrawVertex;
    }

    /**
     * @return the drawEdges
     */
    public boolean isDrawEdges() {
        return this.drawEdges;
    }

    /**
     * @param pDrawEdges the drawEdges to set
     */
    public void setDrawEdges(boolean pDrawEdges) {
        this.drawEdges = pDrawEdges;
    }




}
