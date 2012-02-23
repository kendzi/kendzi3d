/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.render;

import java.awt.Color;
import java.net.URL;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;

import kendzi.jogl.model.factory.FaceFactory.FaceType;
import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.josm.kendzi3d.service.TextureCacheService;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class ModelRender {

    /** Log. */
    private static final Logger log = Logger.getLogger(ModelRender.class);

    private static final Material TEXTURE_MATERIAL = getDefaultMaterial();

//    private static ModelRender instance = new ModelRender();
    private HashMap<Integer, Texture> textures;
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

    public ModelRender() {
    }

//    public static ModelRender getInstance() {
//        return instance;
//    }

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

//        boolean materialChanged = true;

//        if (this.textures == null) {
//            //FIXME
//            initTextures(model);
//        }
        int mi = 0;
        int fi = 0;

//        gl.glPushMatrix();

        try {
            boolean useTextureDisabled = false;

            for (mi = 0; mi < model.mesh.length; mi++) {
                Mesh mesh = model.mesh[mi];

                boolean meshUseTexture = mesh.hasTexture && getTexture(mesh.materialID, model) != null;
                if (meshUseTexture) {
                    if (model.useTexture && useTextureDisabled) {
                        // if model is using textures only in some mesh
                        gl.glEnable(GL2.GL_TEXTURE_2D);
                        useTextureDisabled = false;
                    }
                    //FIXME
//                    Texture texture = this.textures.get(model.getMaterial(mesh.materialID));
                    Texture texture = getTexture(mesh.materialID, model);

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
                    texture.enable();
                    texture.bind();
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
                //        setupMaterial(gl, faceMaterial);
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

                                gl.glTexCoord2d(mesh.texCoords[textureIndex].u, mesh.texCoords[textureIndex].v);
                            }
                        }

                        gl.glVertex3d(mesh.vertices[vetexIndex].x, mesh.vertices[vetexIndex].y, mesh.vertices[vetexIndex].z);

                    }

                    gl.glEnd();
                }



                if (meshUseTexture) {
                    Texture t = getTexture(mesh.materialID, model);
                    //this.textures.get(mesh.materialID);// .get(mesh.materialID);
                    if (t != null) {
                        t.disable();
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
//        gl.glPopMatrix();

        gl.glDisable(GL2.GL_LIGHTING);

        if (this.drawEdges || model.drawEdges) {
            drawEdges(gl, model);
        }
        if (this.drawNormals || model.drawNormals) {
            drawNormals(gl, model);
        }

        gl.glColor3f(1.0f, 1.0f, 1.0f);

    }

    /** Draws normals.
     * @param pGl gl
     * @param pModel model
     */
    private void drawNormals(GL2 pGl, Model pModel) {

        for (Mesh mesh : pModel.mesh) {
            //blue
            pGl.glColor3f(0.5f, 0.5f, 1.0f);

            // Set line width
            pGl.glLineWidth(2);
            // Repeat count, repeat pattern
            pGl.glLineStipple(1, (short) 0xf0f0);

            pGl.glBegin(GL2.GL_LINES);

            for (Face face : mesh.face) {
                int vertLength = face.vertIndex.length;

                if (face.normalIndex != null && face.normalIndex.length > 0) {
                    for (int i = 0; i < vertLength; i++) {

                        int normalIndex = face.normalIndex[i];
                        if (mesh.normals.length > normalIndex) {

                            int vetexIndex = face.vertIndex[i];
                            pGl.glVertex3d(
                                    mesh.vertices[vetexIndex].x,
                                    mesh.vertices[vetexIndex].y,
                                    mesh.vertices[vetexIndex].z);

                            double normScale = 0.5;
                            pGl.glVertex3d(
                            mesh.vertices[vetexIndex].x + normScale * mesh.normals[normalIndex].x,
                            mesh.vertices[vetexIndex].y + normScale * mesh.normals[normalIndex].y,
                            mesh.vertices[vetexIndex].z + normScale * mesh.normals[normalIndex].z);
                        }

                    }
                }
            }

            pGl.glEnd();
        }
    }

    /** Draws edges.
     * @param pGl gl
     * @param pModel model
     */
    private void drawEdges(GL2 pGl, Model pModel) {
        for (Mesh mesh : pModel.mesh) {
            //green
            pGl.glColor3f(0.5f, 1.0f, 0.5f);

            // Set line width
            pGl.glLineWidth(4);
            // Repeat count, repeat pattern
            pGl.glLineStipple(1, (short) 0xf0f0);


            for (Face face : mesh.face) {
                int vertLength = face.vertIndex.length;

                if (face.type == FaceType.TRIANGLE_STRIP.getType()) {
                    pGl.glBegin(GL2.GL_LINE_STRIP);
                    for (int i = 0; i < vertLength; i++) {

                        int vetexIndex = face.vertIndex[i];
                        pGl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }
                    pGl.glEnd();
                    if (face.vertIndex.length > 2) {
                        pGl.glBegin(GL2.GL_LINE_STRIP);
                        for (int i = 0; i < vertLength; i = i + 2) {

                            int vetexIndex = face.vertIndex[i];
                            pGl.glVertex3d(
                                    mesh.vertices[vetexIndex].x,
                                    mesh.vertices[vetexIndex].y,
                                    mesh.vertices[vetexIndex].z);
                        }
                        pGl.glEnd();
                        pGl.glBegin(GL2.GL_LINE_STRIP);
                        for (int i = 1; i < vertLength; i = i + 2) {

                            int vetexIndex = face.vertIndex[i];
                            pGl.glVertex3d(
                                    mesh.vertices[vetexIndex].x,
                                    mesh.vertices[vetexIndex].y,
                                    mesh.vertices[vetexIndex].z);
                        }
                        pGl.glEnd();
                    }
                } else if (face.type == FaceType.TRIANGLES.getType()) {
                    int i = 0;
                    while (i < vertLength) {
                        pGl.glBegin(GL2.GL_LINE_LOOP);
                        int triangleCount = 0;
                        while (i + triangleCount < vertLength && triangleCount < 3) {

                            int vetexIndex = face.vertIndex[i + triangleCount];
                            pGl.glVertex3d(
                                    mesh.vertices[vetexIndex].x,
                                    mesh.vertices[vetexIndex].y,
                                    mesh.vertices[vetexIndex].z);
                            triangleCount++;
                        }
                        i = i + 3;
                        pGl.glEnd();
                    }
                } else if (face.type == FaceType.TRIANGLE_FAN.getType()) {
                    pGl.glBegin(GL2.GL_LINE_LOOP);
                    for (int i = 0; i < vertLength; i++) {

                        int vetexIndex = face.vertIndex[i];
                        pGl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }
                    pGl.glEnd();

                    if (vertLength > 1) {

                        Point3d begin = mesh.vertices[face.vertIndex[0]];

                        pGl.glBegin(GL2.GL_LINES);
                        for (int i = 2; i < vertLength; i++) {

                            pGl.glVertex3d(begin.x, begin.y, begin.z);

                            int endIndex = face.vertIndex[i];
                            pGl.glVertex3d(
                                    mesh.vertices[endIndex].x,
                                    mesh.vertices[endIndex].y,
                                    mesh.vertices[endIndex].z);
                        }
                        pGl.glEnd();
                    }

                } else if (face.type == FaceType.QUADS.getType()) {
                    int q = 0;

                    while (q < vertLength) {
                        pGl.glBegin(GL2.GL_LINE_LOOP);
                        int i = 0;
                        while (i < 4 && i + q < vertLength) {
//                        for (int i = 0; i < 4; i++) {

                            int vetexIndex = face.vertIndex[i + q];
                            pGl.glVertex3d(
                                    mesh.vertices[vetexIndex].x,
                                    mesh.vertices[vetexIndex].y,
                                    mesh.vertices[vetexIndex].z);

                            i++;
                        }
                        q = q + 4;
                        pGl.glEnd();
                    }
                } else if (face.type == FaceType.QUAD_STRIP.getType()) {
                    pGl.glBegin(GL2.GL_LINES);
                    for (int i = 0; i < vertLength; i++) {

                        int vetexIndex = face.vertIndex[i];
                        pGl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }
                    pGl.glEnd();

                    pGl.glBegin(GL2.GL_LINE_STRIP);
                    for (int i = 0; i < vertLength; i = i + 2) {

                        int vetexIndex = face.vertIndex[i];
                        pGl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }
                    pGl.glEnd();

                    pGl.glBegin(GL2.GL_LINE_STRIP);
                    for (int i = 1; i < vertLength; i = i + 2) {

                        int vetexIndex = face.vertIndex[i];
                        pGl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }
                    pGl.glEnd();
                }
            }
        }
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
        return material == null || material.equals(lastMaterial);
    }

    private void setupMaterials(Material material, GL2 pGl) {
//        if (TEXTURE_MATERIAL.equals(material)) {
//            setDefaultMaterial(pGl);
//            return;
//        }

        setupMaterial(pGl, material);
    }

    private boolean setupMaterials(boolean materialChanged, GL2 pGl, Model pModel, Mesh mesh, Face face) {


        // If the object has a texture, then do nothing till later...else
        // apply the material property to it.
       if (mesh.hasTexture) {
                // nothing
           if (materialChanged) {
               setDefaultMaterial(pGl);
           }

            // Has no texture but has a material instead and this material is
            // the FACES material, and not the OBJECTS material ID as being used
            // incorrectly below...by specification, the materialID is associated
            // with a FACE and not an OBJECT
        } else {
            if (face.materialID < pModel.getNumberOfMaterials()) {



                float [] f = new float[6];

                pGl.glGetMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, f, 0);
                pGl.glGetMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, f, 0);
                pGl.glGetMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, f, 0);
                pGl.glGetMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, f, 0);
                pGl.glGetMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, f, 0);


                Material material = pModel.getMaterial(face.materialID);

                setupMaterial(pGl, material);

                return true;
            }
        }
       return false;

    }

    public static void setDefaultMaterial(GL2 pGl) {
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, new float[] {0.8f, 0.8f, 0.8f, 1.0f}, 0);
        //FIXME don't setup materials after each rendered model !!!
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, new float[] {0.5f, 0.5f, 0.5f, 1.0f}, 0);
//        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, new float[] {0.2f, 0.2f, 0.2f, 1.0f}, 0);
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, new float[] {0.0f, 0.0f, 0.0f, 1.0f}, 0);
        pGl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.0f);
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, new float[] {0.0f, 0.0f, 0.0f, 1.0f}, 0);
    }

    public static Material getDefaultMaterial() {

        Material m = new Material();
        m.diffuseColor = new Color(0.8f, 0.8f, 0.8f, 1.0f);
        m.ambientColor = new Color(0.5f, 0.5f, 0.5f, 1.0f);
        //FIXME don't setup materials after each rendered model !!!
        m.specularColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);

        m.shininess = 0.0f;
        m.emissive = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        return m;
    }

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

    private Texture getTexture(int materialId, Model model) {

        if (model.getMaterial(materialId).strFile != null) {
//            if (this.isDebugging) {
//                log.info("        Material:  " + model.getMaterial(materialId).strFile);
//            }

            return this.textureCacheService.getTexture(model.getMaterial(materialId).strFile);
        }
        return this.textureCacheService.getTexture("/textures/undefined.png");
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


    /** Loads texture file.
     * @param url
     * @param id
     * @return
     */
    private Texture loadTexture(URL url, int id) {
        if (url != null ) {
            try {
                Texture tex = AWTTextureIO.newTexture(url, true, null);
                this.textures.put(id,tex);

            } catch (Exception e) {
                log.error("faild to load texture from url: " + url);
                e.printStackTrace();
            }
        }
        return null;
    }

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
