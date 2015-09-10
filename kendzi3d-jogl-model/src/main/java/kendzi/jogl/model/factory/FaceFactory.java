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

import com.jogamp.opengl.GL2;

public class FaceFactory {

	public List<Integer> vertIndex = new ArrayList<Integer>();

	public List<List<Integer>> coordIndexLayers = new ArrayList<List<Integer>>();
//	public List<Integer> coordIndex = new ArrayList<Integer>();
//	public List<Integer> coordIndex1 = new ArrayList<Integer>();
//	public List<Integer> coordIndex2 = new ArrayList<Integer>();
	public List<Integer> normalIndex = new ArrayList<Integer>();

	public int count;

	int textIndex ;
	public FaceType type;

//	public int [] vertIndex;
//    public int [] coordIndex;
//    public int [] normalIndex;

//    public Type type;


    protected FaceFactory(FaceType pFaceType) {
        this(pFaceType, 1);
    }

    protected FaceFactory(FaceType pFaceType, int numOfLayers) {
	    this.type = pFaceType;
	    this.coordIndexLayers = new ArrayList<List<Integer>>();

	    for (int i = 0; i < numOfLayers; i++) {
	        coordIndexLayers.add(new ArrayList<Integer>());
	    }
	}

    public enum FaceType {
    	/**
    	 * Single triangles.
    	 */
    	TRIANGLES(GL2.GL_TRIANGLES),
    	/**
    	 * Triangle fan. One comon point.
    	 */
    	TRIANGLE_FAN(GL2.GL_TRIANGLE_FAN),
    	/**
    	 * Strip of triangles.
    	 */
    	TRIANGLE_STRIP(GL2.GL_TRIANGLE_STRIP),

    	/**
    	 * QUADS.
    	 */
    	QUADS(GL2.GL_QUADS),

    	/**
    	 * QUAD_STRIP.
    	 */
    	QUAD_STRIP(GL2.GL_QUAD_STRIP);

    	private int type;

		FaceType(int pType) {
    		this.type = pType;
    	}

		public int getType() {
			return this.type;
		}
    }


	public void addVertIndex(int i) {
		this.vertIndex.add(i);
	}

	public void addCoordIndex(int i) {
		this.coordIndexLayers.get(0).add(i);
	}

	public void addNormalIndex(int i) {
		this.normalIndex.add(i);
	}

	public void addVert(int vertIndex, int coordIndex, int normalIndex) {

		this.vertIndex.add(vertIndex);

		validateAddedTextureCoordinates(1);

		this.coordIndexLayers.get(0).add(coordIndex);

		this.normalIndex.add(normalIndex);

		this.count++;
	}

	private void validateAddedTextureCoordinates(int numOfAddedTexturesCoordinates) {
	    if (this.coordIndexLayers.size() != numOfAddedTexturesCoordinates) {
            throw new RuntimeException("face have setup: " + this.coordIndexLayers.size() + " textures layers but added vertex has only coordinate for: "+ numOfAddedTexturesCoordinates);
        }
	}

	public void addVert(int vertIndex, int coordIndex0, int coordIndex1, int normalIndex) {

	    this.vertIndex.add(vertIndex);

	    validateAddedTextureCoordinates(2);

	    this.coordIndexLayers.get(0).add(coordIndex0);
	    this.coordIndexLayers.get(1).add(coordIndex1);

	    this.normalIndex.add(normalIndex);

	    this.count++;
	}

	public void addVert(int vertIndex, int normalIndex, int...cords) {

	    this.vertIndex.add(vertIndex);

	    validateAddedTextureCoordinates(cords.length);

	    for (int i = 0; i < cords.length; i++) {
	        this.coordIndexLayers.get(i).add(cords[i]);
	    }

	    this.normalIndex.add(normalIndex);

	    this.count++;
	}

	public void setTextIndex(int textIndex) {
		this.textIndex = textIndex;
	}

	public int numOfTexturesLayers() {
	    return this.coordIndexLayers.size();
	}

//	public void setType(Type type) {
//		this.type = type;
//	}


}
