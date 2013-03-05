/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.render;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.material.AmbientDiffuseComponent;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.geometry.material.OtherComponent;
import kendzi.josm.kendzi3d.service.TextureCacheService;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.jogamp.opengl.util.texture.Texture;

public class ModelRender {

    /** Log. */
    private static final Logger log = Logger.getLogger(ModelRender.class);

    private static final int[] GL_TEXTURE = {GL2.GL_TEXTURE0, GL2.GL_TEXTURE1, GL2.GL_TEXTURE2, GL2.GL_TEXTURE3};

    private static final int MAX_TEXTURES_LAYERS = 4;

    private boolean debugging = true;

    private boolean drawEdges;

    private boolean drawNormals;

    private int faceCount;

    /**
     * Texture cache service.
     */
    @Inject
    private TextureCacheService textureCacheService;

    private OtherComponent lastOtherComponent;

    private AmbientDiffuseComponent lastAmbientDiffuseComponent;

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

            for (mi = 0; mi < model.mesh.length; mi++) {
                Mesh mesh = model.mesh[mi];

                Material material = model.getMaterial(mesh.materialID);

                setupMaterial2(gl, material);

                boolean useTextures = mesh.hasTexture;

                setupTextures(gl, material, useTextures);

                this.faceCount += mesh.face.length;

                for (fi = 0; fi < mesh.face.length; fi++) {
                    Face face = mesh.face[fi];

                    int numOfTextureLayers = Math.min(MAX_TEXTURES_LAYERS, face.coordIndexLayers.length);
                    if (!mesh.hasTexture) {
                        numOfTextureLayers = 0;
                    }

                    gl.glBegin(face.type);

                    for (int i = 0; i < face.vertIndex.length; i++) {
                        int vetexIndex = face.vertIndex[i];
//                        if (face.normalIndex != null && face.normalIndex.length > i) {
                        int normalIndex = face.normalIndex[i];

                        gl.glNormal3d(
                                mesh.normals[normalIndex].x,
                                mesh.normals[normalIndex].y,
                                mesh.normals[normalIndex].z);
//                        }

                        for (int tl = 0; tl < numOfTextureLayers; tl++) {
                            int textureIndex = face.coordIndexLayers[tl][i];
                            gl.glMultiTexCoord2d(
                                    GL_TEXTURE[tl],
                                    mesh.texCoords[textureIndex].u,
                                    mesh.texCoords[textureIndex].v);
                        }

                        gl.glVertex3d(
                                mesh.vertices[vetexIndex].x,
                                mesh.vertices[vetexIndex].y,
                                mesh.vertices[vetexIndex].z);
                    }

                    gl.glEnd();
                }


                unsetupTextures(gl, material, useTextures);

            }

            gl.glColor3f(1.0f, 1.0f, 1.0f);


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
       // setDefaultMaterial(gl);
    }

    private void unsetupTextures(GL2 gl, Material material, boolean useTextures) {
        List<String> texturesComponent = material.getTexturesComponent();
        boolean colored = material.getTexture0Color() != null;

        int texSize = texturesComponent.size();

        int texColored = (colored && texSize > 0? texSize : -1);

        for (int i = MAX_TEXTURES_LAYERS -1; i >=  0; i--) {

            boolean textLayerEnabled = i < texSize && useTextures;

            gl.glActiveTexture(GL_TEXTURE[i]);

            if (i == 0 && colored) {
                gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
            }

            if (textLayerEnabled || texColored == i) {

                gl.glDisable(GL2.GL_TEXTURE_2D);

                unbindTexture(gl);
            }
        }
    }

    private void setupTextures(GL2 gl, Material material, boolean useTextures) {

        List<String> texturesComponent = material.getTexturesComponent();
        boolean colored = material.getTexture0Color() != null;

        int texSize = texturesComponent.size();
        int texColored = (colored && texSize > 0? texSize : -1);
        for (int i = 0; i< MAX_TEXTURES_LAYERS; i++) {

            boolean layerEnabled = i < texSize && useTextures;
            boolean lightLayerEnabled = i == texColored;

            gl.glActiveTexture(GL_TEXTURE[i]);

            if (lightLayerEnabled) {
                String textureKey = texturesComponent.get(texSize - 1);

                Texture texture = getTexture(gl, textureKey);

                gl.glEnable(GL2.GL_TEXTURE_2D);

                bindTexture(gl, texture);

                gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE );
                gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_MODULATE );

                gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2.GL_PRIMARY_COLOR );
                gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND0_RGB, GL2.GL_SRC_COLOR );

                gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2.GL_PREVIOUS );
                gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND1_RGB, GL2.GL_SRC_COLOR );



            } else if (layerEnabled) {

                String textureKey = texturesComponent.get(i);

                Texture texture = getTexture(gl, textureKey);

                gl.glEnable(GL2.GL_TEXTURE_2D);

                bindTexture(gl, texture);

                if (i == 0) {
                    if (colored) {
                        // For colored textures
                        // material.setAmbientDiffuse(new AmbientDiffuseComponent(Color.WHITE, Color.WHITE));
                        float[] rgbComponents = material.getTexture0Color().getRGBComponents(new float [4]);
                        rgbComponents[3] = 0.7f;

                        gl.glTexEnvfv(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_COLOR, rgbComponents, 0);

                        gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE );
                        gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_INTERPOLATE );

                        gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2.GL_CONSTANT );
                        gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND0_RGB, GL2.GL_SRC_COLOR );

                        gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2.GL_TEXTURE);
                        gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND1_RGB, GL2.GL_SRC_COLOR );

                        gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE2_RGB, GL2.GL_CONSTANT );
                        gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND2_RGB, GL2.GL_SRC_ALPHA );

                    } else {
                        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
                    }

                } else {
                    gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE );
                    gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_INTERPOLATE );

                    gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2.GL_TEXTURE );
                    gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND0_RGB, GL2.GL_SRC_COLOR );

                    gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2.GL_PREVIOUS );
                    gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND1_RGB, GL2.GL_SRC_COLOR );

                    gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE2_RGB, GL2.GL_TEXTURE );
                    gl.glTexEnvi( GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND2_RGB, GL2.GL_SRC_ALPHA );
                }

            } else {
                gl.glDisable(GL2.GL_TEXTURE_2D);
            }
        }
    }


    private void unbindTexture(GL2 gl) {

        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glPopMatrix();

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();
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

    public static void setDefaultMaterial(GL2 pGl) {
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, new float[] {0.8f, 0.8f, 0.8f, 1.0f}, 0);

        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, new float[] {0.5f, 0.5f, 0.5f, 1.0f}, 0);
//        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, new float[] {0.2f, 0.2f, 0.2f, 1.0f}, 0);
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, new float[] {0.0f, 0.0f, 0.0f, 1.0f}, 0);
        pGl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.0f);
        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, new float[] {0.0f, 0.0f, 0.0f, 1.0f}, 0);
    }

    private void setupMaterialOtherComponent(GL2 pGl, OtherComponent material) {

        float[] rgba = new float[4];

        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, material.getSpecularColor().getRGBComponents(rgba), 0);

        pGl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, material.getShininess());

        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, material.getEmissive().getRGBComponents(rgba), 0);

        this.lastOtherComponent = material;
    }

    private void setupMaterialAmbientDiffuseComponent(GL2 pGl, AmbientDiffuseComponent material) {

        float[] rgba = new float[4];

        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, material.getDiffuseColor().getRGBComponents(rgba), 0);

        pGl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, material.getAmbientColor().getRGBComponents(rgba), 0);

        this.lastAmbientDiffuseComponent = material;
    }

    public void resetMaterials() {
        this.lastOtherComponent = null;
        this.lastAmbientDiffuseComponent = null;
    }

    Material defaultMaterial = new Material();
    public void setupDefaultMaterial(GL2 pGL) {
        setupMaterial2(pGL, this.defaultMaterial);
    }

    private void setupMaterial2(GL2 pGl, Material material) {
        if (isAmbientDiffuseChanged(material.getAmbientDiffuse())) {
            setupMaterialAmbientDiffuseComponent(pGl, material.getAmbientDiffuse());
        }

        if (isOtherComponentChanged(material.getOther())) {
            setupMaterialOtherComponent(pGl, material.getOther());
        }
    }

    private boolean isOtherComponentChanged(OtherComponent other) {
        return this.lastOtherComponent == null || !this.lastOtherComponent.equals(other);
    }

    private boolean isAmbientDiffuseChanged(AmbientDiffuseComponent ambientDiffuse) {
        return this.lastAmbientDiffuseComponent == null || !this.lastAmbientDiffuseComponent.equals(ambientDiffuse);
    }

    private Texture getTexture(GL gl, String file) {

        if (file != null) {
            return this.textureCacheService.getTexture(gl, file);
        }
        return this.textureCacheService.getTexture(gl, "/textures/undefined.png");
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
