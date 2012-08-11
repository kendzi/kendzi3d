/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry;


public class Face {
	public int [] vertIndex;
    public int [] coordIndex;
    public int [] coordIndex1;
    public int [] coordIndex2;
    public int [] normalIndex;

    public int type;


    /**
     * Id of material used by face.
     */
    public int materialID;




    public Face() {
        //
    }

    public Face(int pType, int pLength) {

        this.vertIndex = new int[pLength];
        this.coordIndex = new int[pLength];
        this.normalIndex = new int[pLength];

        this.type = pType;
    }



    //FIXME
    enum Type {
    	triangle_list;
    }

}
