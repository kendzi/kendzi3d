/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.render;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.geometry.material.Material.MatType;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.josm.kendzi3d.service.TextureCacheService;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.jogamp.opengl.util.texture.Texture;

public class ModelRender {

    /** Log. */
    private static final Logger log = Logger.getLogger(ModelRender.class);

    private static final Material TEXTURE_MATERIAL = MaterialFactory.getDefaultMaterial();

    private boolean debugging = true;

    private boolean drawEdges;

    private boolean drawNormals;

    private Material lastSetMaterial;

    private int vertexCount;

    private int faceCount;

    /**
     * Texture cache service.
     */
    @Inject
    private TextureCacheService textureCacheService;

    /**
     *
     */
    public ModelRender() {

    }

    public int getFaceCount() {
        return this.faceCount;
    }

    public void resetFaceCount() {
       this.faceCount = 0;
    }

    public void render(GL2 gl, Model model) {
        // XXX get list from cash

        if (model.useLight) {
            gl.glEnable(GL2.GL_LIGHTING);
        }
        if (model.useTexture) {
            gl.glEnable(GL2.GL_TEXTURE_2D);
        } else {
            gl.glDisable(GL2.GL_TEXTURE_2D);
        }

        draw(gl, model);
    }

    private void draw(GL2 gl, Model model) {

        int mi = 0;
        int fi = 0;

        try {

            boolean useTextureDisabled = false;

            for (mi = 0; mi < model.mesh.length; mi++) {
                Mesh mesh = model.mesh[mi];

                Material material = model.getMaterial(mesh.materialID);

                boolean meshUseTexture = mesh.hasTexture && getTexture(gl, mesh.materialID, model) != null;
                if (meshUseTexture) {
                    if (model.useTexture && useTextureDisabled) {
                        // if model is using textures only in some mesh
                        gl.glEnable(GL2.GL_TEXTURE_2D);
                        useTextureDisabled = false;
                    }


                    if (MatType.TEXTURE0.equals(material.matType)) {
                        Material mat =  model.getMaterial(mesh.materialID);
                        Texture texture0 = getTexture(gl, mat.strFile);

                        bindTexture(gl, texture0);
                    } else if (MatType.COLOR_MultT0_MultT1.equals(material.matType)) {
                        Material mat =  model.getMaterial(mesh.materialID);
                        Texture texture0 = getTexture(gl, mat.strFile);
                        Texture texture1 = getTexture(gl, mat.texture1);

                        gl.glActiveTexture(GL2.GL_TEXTURE0);
                        gl.glEnable(GL2.GL_TEXTURE_2D);
                        bindTexture(gl, texture0);
                        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

                        gl.glActiveTexture(GL2.GL_TEXTURE1);
                        gl.glEnable(GL2.GL_TEXTURE_2D);
                        bindTexture(gl, texture1);

                    }

                } else {

                    // if model is using textures only in some mesh
                    gl.glDisable(GL2.GL_TEXTURE_2D);
                    useTextureDisabled = true;
                }

                this.faceCount += mesh.face.length;

                for (fi = 0; fi < mesh.face.length; fi++) {
                    Face face = mesh.face[fi];

                    Material faceMaterial = getFaceMaterial(model, mesh, face);
//                    materialchanged = isMaterialChanged(faceMaterial, lastMaterial)
                    if (isMaterialChanged(faceMaterial, this.lastSetMaterial)) {
                        setupMaterial(gl, faceMaterial);
                    }

//                    boolean setupMaterials = setupMaterials(materialChanged, gl, model, mesh, face);

//                    materialChanged = materialChanged || setupMaterials;

                    gl.glBegin(face.type);

                    for (int i = 0; i < face.vertIndex.length; i++) {
                        int vetexIndex = face.vertIndex[i];
                        if (face.normalIndex != null && face.normalIndex.length > i) {
                            int normalIndex = face.normalIndex[i];

//                            int normalIndexMod = normalIndex % mesh.normals.length;
//                            if (normalIndex != normalIndexMod) {
//                                log.error("model is bad! bad normals");
//                                normalIndex = normalIndexMod;
//                            }

                            gl.glNormal3d(mesh.normals[normalIndex].x, mesh.normals[normalIndex].y, mesh.normals[normalIndex].z);
                        }

                        int textureIndex = 0; //?
                        if (mesh.hasTexture) {
                            if (mesh.texCoords != null) {
                                textureIndex = face.coordIndex[i];

                                if (MatType.COLOR_MultT0_MultT1.equals(material.matType)) {
                                    gl.glMultiTexCoord2d(GL2.GL_TEXTURE0, mesh.texCoords[textureIndex].u, mesh.texCoords[textureIndex].v);

                                    textureIndex = face.coordIndex1[i];
                                    gl.glMultiTexCoord2d(GL2.GL_TEXTURE1, mesh.texCoords[textureIndex].u, mesh.texCoords[textureIndex].v);

                                    textureIndex = face.coordIndex2[i];
                                    gl.glMultiTexCoord2d(GL2.GL_TEXTURE2, mesh.texCoords[textureIndex].u, mesh.texCoords[textureIndex].v);

                                } else {
                                    gl.glTexCoord2d(mesh.texCoords[textureIndex].u, mesh.texCoords[textureIndex].v);
                                }
                            }
                        }

                        gl.glVertex3d(mesh.vertices[vetexIndex].x, mesh.vertices[vetexIndex].y, mesh.vertices[vetexIndex].z);

                    }

                    gl.glEnd();
                }



                if (meshUseTexture) {
                    Texture t = getTexture(gl, mesh.materialID, model);
                    //this.textures.get(mesh.materialID);// .get(mesh.materialID);

                    if (MatType.TEXTURE0.equals(material.matType)) {
                        if (t != null) {
                            t.disable(gl);
                        }

                    } else if (MatType.COLOR_MultT0_MultT1.equals(material.matType)) {
                        gl.glActiveTexture(GL2.GL_TEXTURE1);
                        gl.glDisable(GL2.GL_TEXTURE_2D);
                        gl.glActiveTexture(GL2.GL_TEXTURE2);
                        gl.glDisable(GL2.GL_TEXTURE_2D);

                        gl.glActiveTexture(GL2.GL_TEXTURE0);
                    }


                    gl.glMatrixMode(GL2.GL_TEXTURE);
                    gl.glPopMatrix();

                    gl.glMatrixMode(GL2.GL_MODELVIEW);
                    gl.glPopMatrix();
                }
            }

            gl.glColor3f(1.0f, 1.0f, 1.0f);

//            if (materialChanged) {
//                setDefaultMaterial(gl);
//            }

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "error model: " + model.getSource()
                    + " mesh: " + mi + " (" + (model.mesh[mi] != null ? model.mesh[mi].name : "" ) + ")"
                    + " face: " + fi, e);
        }

        gl.glDisable(GL2.GL_LIGHTING);

        if (this.drawEdges || model.drawEdges) {
            DebugModelRendererUtil.drawEdges(gl, model);
        }
        if (this.drawNormals || model.drawNormals) {
            DebugModelRendererUtil.drawNormals(gl, model);
        }

        gl.glColor3f(1.0f, 1.0f, 1.0f);

    }

    /**
     * @param gl
     * @param texture
     */
    public void bindTexture(GL2 gl, Texture texture) {
        // switch to texture mode and push a new matrix on the stack
        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glPushMatrix();

        // check to see if the texture needs flipping
        if (texture.getMustFlipVertically()) {
            gl.glScaled(1, -1, 1);
            gl.glTranslated(0, -1, 0);
        }

        // switch to modelview matrix and push a new matrix on the stack
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();

        // This is required to repeat textures
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

        // enable, bind
        texture.enable(gl);
        texture.bind(gl);
    }






    private Material getFaceMaterial(Model pModel, Mesh mesh, Face face) {
        if (mesh.hasTexture) {
            return TEXTURE_MATERIAL;
        }
        if (face.materialID < pModel.getNumberOfMaterials()) {
            return pModel.getMaterial(face.materialID);
        }
        return null;
    }

    private boolean isMaterialChanged(Material material, Material lastMaterial) {
        return material == null || !material.equals(lastMaterial);
    }

    private void setupMaterials(Material material, GL2 pGl) {
//        if (TEXTURE_MATERIAL.equals(material)) {
//            setDefaultMaterial(pGl);
//            return;
//        }

        setupMaterial(pGl, material);
    }

//    private boolean setupMaterials(boolean materialChanged, GL2 pGl, Model pModel, Mesh mesh, Face face) {
//
//
//        // If the object has a texture, then do nothing till later...else
//        // apply the material property to it.
//       if (mesh.hasTexture) {
//                // nothing
//           if (materialChanged) {
//               setDefaultMaterial(pGl);
//           }
//
//            // Has no texture but has a material instead and this material is
//            // the FACES material, and not the OBJECTS material ID as being used
//            // incorrectly below...by specification, the materialID is associated
//            // with a FACE and not an OBJECT
//        } else {
//            if (face.materialID < pModel.getNumberOfMaterials()) {
//
//
//
//                float [] f = new float[6];
//
//                pGl.glGetMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, f, 0);
//                pGl.glGetMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, f, 0);
//                pGl.glGetMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, f, 0);
//                pGl.glGetMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, f, 0);
//                pGl.glGetMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, f, 0);
//
//
//                Material material = pModel.getMaterial(face.materialID);
//
//                setupMaterial(pGl, material);
//
//                return true;
//            }
//        }
//       return false;
//
//    }

    public static void setDefaultMaterial(GL2 pGl) {
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, new float[] {0.8f, 0.8f, 0.8f, 1.0f}, 0);
        //FIXME don't setup materials after each rendered model !!!
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, new float[] {0.5f, 0.5f, 0.5f, 1.0f}, 0);
//        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, new float[] {0.2f, 0.2f, 0.2f, 1.0f}, 0);
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, new float[] {0.0f, 0.0f, 0.0f, 1.0f}, 0);
        pGl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.0f);
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, new float[] {0.0f, 0.0f, 0.0f, 1.0f}, 0);
    }

//    public static Material getDefaultMaterial() {
//
//        Material m = new Material();
//        m.diffuseColor = new Color(0.8f, 0.8f, 0.8f, 1.0f);
//        m.ambientColor = new Color(0.5f, 0.5f, 0.5f, 1.0f);
//        //FIXME don't setup materials after each rendered model !!!
//        m.specularColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
//
//        m.shininess = 0.0f;
//        m.emissive = new Color(0.0f, 0.0f, 0.0f, 1.0f);
//        return m;
//    }

    private void setupMaterial(GL2 pGl, Material material) {
        float[] rgba = new float[4];

        if (material.diffuseColor != null) {
            pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, material.diffuseColor.getRGBComponents(rgba), 0);
        }
        if (material.ambientColor != null) {
            pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, material.ambientColor.getRGBComponents(rgba), 0);
        }
        if (material.specularColor != null) {
            pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, material.specularColor.getRGBComponents(rgba), 0);
        }

        pGl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, material.shininess);

        if (material.emissive != null) {
            pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, material.emissive.getRGBComponents(rgba), 0);
        }

        this.lastSetMaterial = material;
    }

    private Texture getTexture(GL gl, int materialId, Model model) {
        return getTexture(gl, model.getMaterial(materialId).strFile);
    }

    private Texture getTexture(GL gl, String file) {

        if (file != null) {
            return this.textureCacheService.getTexture(gl, file);
        }
        return this.textureCacheService.getTexture(gl, "/textures/undefined.png");
    }



//    private void initTextures(Model model) {
//        //FIXME
//        String file = model.getSource();
//
//        this.textures = new HashMap<Integer, Texture>();
//        for (int i = 0; i < model.getNumberOfMaterials(); i++) {
//            // TODO:DELETE THIS OLD LINE loadTexture(materials.get(i).strFile, i);
//            // TODO:DELETE THIS OLD LINE materials.get(i).texureId = i;
//
//            String subFileName = getFullTexturePath(file);
//
//            if (model.getMaterial(i).strFile != null) {
//                if (this.isDebugging) {
//                    log.info("        Material:  " + subFileName + model.getMaterial(i).strFile);
//                }
//
//
//
//                Texture texture = TextureCacheService.getTexture(subFileName + model.getMaterial(i).strFile);
//                this.textures.put(i, texture);
//
//                //                URL result;
//                //                try {
//                //                    result = ResourceRetriever.getResourceAsUrl(subFileName + model.getMaterial(i).strFile);
//                //                    FileReciver.reciveFileUrl(subFileName + model.getMaterial(i).strFile);
//                //                } catch(IOException e) {
//                //                    if (this.isDebugging)
//                //                        log.error(" ... failed");
//                //                    continue;
//                //                }
//                //
//                //                if (result != null && !result.getPath().endsWith("/") && !result.getPath().endsWith("\\")) {
//                //                    loadTexture(result, i);
//                //                    model.getMaterial(i).textureId = i;
//                //                    if (this.isDebugging)
//                //                        log.info(" ... done. Texture ID: " + i);
//                //                } else if (this.isDebugging) {
//                //                    log.info(" ... failed (no result for material)");
//                //                }
//            }
//        }
//
//
//
//
//
//    }




    /**
     * @return the drawEdges
     */
    public boolean isDrawEdges() {
        return this.drawEdges;
    }

    /**
     * @param drawEdges the drawEdges to set
     */
    public void setDrawEdges(boolean drawEdges) {
        this.drawEdges = drawEdges;
    }

    /**
     * @return the drawNormals
     */
    public boolean isDrawNormals() {
        return this.drawNormals;
    }

    /**
     * @param drawNormals the drawNormals to set
     */
    public void setDrawNormals(boolean drawNormals) {
        this.drawNormals = drawNormals;
    }

    /**
     * @return the debugging
     */
    public boolean isDebugging() {
        return this.debugging;
    }

    /**
     * @param debugging the debugging to set
     */
    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    /**
     * @return the textureCacheService
     */
    public TextureCacheService getTextureCacheService() {
        return this.textureCacheService;
    }

    /**
     * @param textureCacheService the textureCacheService to set
     */
    public void setTextureCacheService(TextureCacheService textureCacheService) {
        this.textureCacheService = textureCacheService;
    }
}
