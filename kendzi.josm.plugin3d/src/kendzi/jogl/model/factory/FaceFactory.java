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

import javax.media.opengl.GL2;

public class FaceFactory {

	public List<Integer> vertIndex = new ArrayList<Integer>();
	public List<Integer> coordIndex = new ArrayList<Integer>();
	public List<Integer> normalIndex = new ArrayList<Integer>();

	int textIndex ;
	public FaceType type;

//	public int [] vertIndex;
//    public int [] coordIndex;
//    public int [] normalIndex;

//    public Type type;

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

	protected FaceFactory(FaceType type) {
		this.type = type;
	}

	public void addVertIndex(int i) {
		vertIndex.add(i);
	}

	public void addCoordIndex(int i) {
		coordIndex.add(i);
	}

	public void addNormalIndex(int i) {
		normalIndex.add(i);
	}

	public void addVert(int vertIndex, int coordIndex, int normalIndex) {

		this.vertIndex.add(vertIndex);

		this.coordIndex.add(coordIndex);

		this.normalIndex.add(normalIndex);

	}

	public void setTextIndex(int textIndex) {
		this.textIndex = textIndex;
	}

//	public void setType(Type type) {
//		this.type = type;
//	}


}
