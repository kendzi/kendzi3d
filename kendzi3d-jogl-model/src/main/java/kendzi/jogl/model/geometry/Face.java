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

	public int [][] coordIndexLayers;

//    public int [] coordIndex;
//    public int [] coordIndex1;
//    public int [] coordIndex2;
    public int [] normalIndex;

    public int type;


//    /**
//     * Id of material used by face.
//     */
//    public int materialID;




    public Face() {
        //
    }

    public Face(int pType, int pLength) {
        this(pType, pLength, 1);
    }

    public Face(int pType, int pLength, int numOfTexturesLayers) {

        this.vertIndex = new int[pLength];
        this.coordIndexLayers = new int [numOfTexturesLayers][];//int[pLength];
        for (int i = 0; i < numOfTexturesLayers; i++) {
            this.coordIndexLayers[i] = new int[pLength];
        }

        this.normalIndex = new int[pLength];

        this.type = pType;
    }



    //FIXME
    enum Type {
    	triangle_list;
    }

}
