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

import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.TextCoord;
import kendzi.jogl.model.geometry.material.Material;

public class ModelFactory {

    protected List<Material> materials = new ArrayList<Material>();

    public static ModelFactory modelBuilder() {
        return new ModelFactory();
    }


    private List<MeshFactory> mashFactory = new ArrayList<MeshFactory>();;

    protected ModelFactory() {
        //
    }


    //	public void addMesh(MashFactory mf) {
    //		mashFactory.add(mf);
    //	}

    /** Add mesh to model.
     * @return added mesh
     */
    public MeshFactory addMesh() {
        return addMesh((String) null);
    }

    /** Add mesh to model.
     * @param pName name of mesh
     * @return added mesh
     */
    public MeshFactory addMesh(String pName) {
        MeshFactory mf = new MeshFactory();
        mf.name = pName;
        this.mashFactory.add(mf);
        return mf;
    }

    /** Add mesh to model.
     * @param pMesh mesh
     * @return added mesh
     */
    public MeshFactory addMesh(MeshFactory pMesh) {
        this.mashFactory.add(pMesh);
        return pMesh;
    }








    /** Convert Factory object into model.
     * @return model generated from ModelFactory
     */
    public Model toModel() {
        BoundsFactory bf = new BoundsFactory();

        Model m = new Model();

        for (Material mat : materials) {
            m.addMaterial(mat);
        }
        m.mesh = new Mesh[this.mashFactory.size()];

        int meshCount = 0;
        for (MeshFactory mf : this.mashFactory) {
            Mesh mesh = new Mesh();

            mesh.materialID = mf.materialID;
            mesh.hasTexture = mf.hasTexture;
            mesh.name = mf.name;

            mesh.vertices = mf.vertices.toArray(new Point3d[0]);
            mesh.normals = mf.normals.toArray(new Vector3d[0]);
            mesh.texCoords = mf.textCoords.toArray(new TextCoord[0]);
            //            mesh.texture = mf.getTexture();
            //            //XXX
            //            mesh.hasTexture = mf.getTexture() != null;

            for (Point3d v : mesh.vertices) {
                bf.addPoint(v);
            }


            List<FaceFactory> faceFactory = mf.faceFactory;


            m.mesh[meshCount] = mesh;

            Face [] faces = new Face[faceFactory.size()];
            int j = 0;
            for (FaceFactory ff : faceFactory) {
                Face f = new Face(ff.type.getType(), ff.vertIndex.size(), ff.numOfTexturesLayers());
//                f.type = ff.type.getType();
//                f.coordIndex = new int[ff.coordIndex.size()];
                for (int l = 0; l < ff.numOfTexturesLayers(); l++) {
                    List<Integer> coordIndex = ff.coordIndexLayers.get(l);

                    for (int i = 0; i < coordIndex.size(); i++) {
                        f.coordIndexLayers[l][i] = coordIndex.get(i);
                    }
                }

//                if (ff.coordIndex1 != null) {
//                    f.coordIndex1 = new int[ff.coordIndex1.size()];
//                    for (int i = 0; i < ff.coordIndex1.size(); i++) {
//                        f.coordIndex1[i] = ff.coordIndex1.get(i);
//                    }
//                }
//
//                if (ff.coordIndex2 != null) {
//                    f.coordIndex2 = new int[ff.coordIndex2.size()];
//                    for (int i = 0; i < ff.coordIndex2.size(); i++) {
//                        f.coordIndex2[i] = ff.coordIndex2.get(i);
//                    }
//                }

//                f.normalIndex = new int[ff.normalIndex.size()];
                for (int i = 0; i < ff.normalIndex.size(); i++) {
                    f.normalIndex[i] = ff.normalIndex.get(i);
                }
//                f.vertIndex = new int[ff.vertIndex.size()];
                for (int i = 0; i < ff.vertIndex.size(); i++) {
                    f.vertIndex[i] = ff.vertIndex.get(i);
                }
//                if (no face material)
//                f.materialID = mesh.materialID;

                faces[j] = f;

                j++;
            }

            mesh.face = faces;

            meshCount++;
        }

        m.setBounds(bf.toBounds());

        return m;
    }


    /** Add material used in model.
     * @param mat material
     * @return id of material in model
     */
    public int addMaterial(Material mat) {
        this.materials.add(mat);
        // XXX
        return this.materials.size() - 1;
    }

    public int cacheMaterial(Material mat) {

        int i = this.materials.indexOf(mat);
        if ( i >= 0 ) {
            return i;
        }

        return addMaterial(mat);
    }


    /**
     * @return the mashFactory
     */
    public List<MeshFactory> getMashFactory() {
        return mashFactory;
    }

}
