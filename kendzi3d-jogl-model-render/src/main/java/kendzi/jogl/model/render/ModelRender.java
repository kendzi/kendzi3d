/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.model.render;

import java.util.List;

import org.apache.log4j.Logger;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.util.texture.Texture;

import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.material.AmbientDiffuseComponent;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.geometry.material.OtherComponent;
import kendzi.jogl.texture.TextureCacheService;

public class ModelRender {

    /** Log. */
    private static final Logger log = Logger.getLogger(ModelRender.class);

    private static final int[] GL_TEXTURE = { GL2.GL_TEXTURE0, GL2.GL_TEXTURE1, GL2.GL_TEXTURE2, GL2.GL_TEXTURE3 };

    private static final int MAX_TEXTURES_LAYERS = GL_TEXTURE.length;

    private boolean debugging = true;

    private boolean drawEdges;

    private boolean drawNormals;

    private boolean drawTextures;

    private boolean drawTwoSided;

    private int faceCount;

    /**
     * Texture cache service.
     */
    private TextureCacheService textureCacheService;

    private int lastSides;

    private OtherComponent lastOtherComponent;

    private AmbientDiffuseComponent lastAmbientDiffuseComponent;

    /**
     *
     */
    public ModelRender() {

    }

    public int getFaceCount() {
        return faceCount;
    }

    public void resetFaceCount() {
        faceCount = 0;
    }

    public void render(GL2 gl, Model model) {
        // XXX get list from cache
        // if (true) {
        // return;
        // gl.glEnable(GL.GL_CULL_FACE);
        // gl.glCullFace(GL.GL_FRONT);
        // }

        if (model.useLight) {
            gl.glEnable(GLLightingFunc.GL_LIGHTING);
        }

        draw(gl, model);

        if (model.useLight) {
            gl.glDisable(GLLightingFunc.GL_LIGHTING);
        }

        // gl.glDisable(GL.GL_CULL_FACE);

        if (drawEdges || model.drawEdges) {
            DebugModelRendererUtil.drawEdges(gl, model);
        }
        if (drawNormals || model.drawNormals) {
            DebugModelRendererUtil.drawNormals(gl, model);
        }

        gl.glColor3f(1.0f, 1.0f, 1.0f);
    }

    public void renderRaw(GL2 gl, Model model) {
        draw(gl, model);
    }

    private void draw(GL2 gl, Model model) {

        int mi = 0;
        int fi = 0;

        try {

            if (model.useCullFaces) {
                gl.glEnable(GL.GL_CULL_FACE);
            }

            gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, (model.useTwoSided || drawTwoSided) ? GL2.GL_TRUE : GL2.GL_FALSE);

            for (mi = 0; mi < model.mesh.length; mi++) {
                Mesh mesh = model.mesh[mi];

                Material material = model.getMaterial(mesh.materialID);

                setupMaterial2(gl, material, (model.useTwoSided || drawTwoSided) ? GL.GL_FRONT_AND_BACK : GL.GL_FRONT);

                if (drawTextures) {
                    if (model.useTextureAlpha)
                        enableTransparentText(gl);
                    setupTextures(gl, material, mesh.hasTexture);
                }

                faceCount += mesh.face.length;

                for (fi = 0; fi < mesh.face.length; fi++) {
                    Face face = mesh.face[fi];

                    int numOfTextureLayers = Math.min(MAX_TEXTURES_LAYERS, face.coordIndexLayers.length);
                    if (!drawTextures || !mesh.hasTexture) {
                        numOfTextureLayers = 0;
                    }

                    gl.glBegin(face.type);

                    for (int i = 0; i < face.vertIndex.length; i++) {
                        int vetexIndex = face.vertIndex[i];
                        // if (face.normalIndex != null &&
                        // face.normalIndex.length > i) {
                        int normalIndex = face.normalIndex[i];

                        gl.glNormal3d(mesh.normals[normalIndex].x, mesh.normals[normalIndex].y, mesh.normals[normalIndex].z);
                        // }

                        for (int tl = 0; tl < numOfTextureLayers; tl++) {
                            int textureIndex = face.coordIndexLayers[tl][i];
                            gl.glMultiTexCoord2d(GL_TEXTURE[tl], mesh.texCoords[textureIndex].u, mesh.texCoords[textureIndex].v);
                        }

                        gl.glVertex3d(mesh.vertices[vetexIndex].x, mesh.vertices[vetexIndex].y, mesh.vertices[vetexIndex].z);
                    }

                    gl.glEnd();
                }

                if (drawTextures) {
                    if (model.useTextureAlpha)
                        disableTransparentText(gl);
                    unsetupTextures(gl, material, mesh.hasTexture);
                }
            }

            gl.glColor3f(1.0f, 1.0f, 1.0f);

        } catch (RuntimeException e) {
            throw new RuntimeException("error model: " + model.getSource() + " mesh: " + mi + " ("
                    + (model.mesh[mi] != null ? model.mesh[mi].name : "") + ")" + " face: " + fi, e);
        } finally {

            gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_FALSE);

            gl.glDisable(GL.GL_CULL_FACE);
        }

    }

    private void unsetupTextures(GL2 gl, Material material, boolean useTextures) {

        List<String> texturesComponent = material.getTexturesComponent();
        boolean colored = material.getTexture0Color() != null;

        int curLayer = texturesComponent.size();

        if (colored && 0 < curLayer && curLayer < MAX_TEXTURES_LAYERS) {
            gl.glActiveTexture(GL_TEXTURE[curLayer]);
            gl.glEnable(GL.GL_TEXTURE_2D);
            unbindTexture(gl);
            gl.glDisable(GL.GL_TEXTURE_2D);
        }

        curLayer = useTextures ? curLayer : 0;

        while (curLayer > 0) {
            curLayer--;
            gl.glActiveTexture(GL_TEXTURE[curLayer]);
            gl.glEnable(GL.GL_TEXTURE_2D);
            unbindTexture(gl);
            //disableTransparentText(gl);
            gl.glDisable(GL.GL_TEXTURE_2D);
            if (curLayer == 0) {
                if (colored) {
                    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);
                }
            }
        }
    }

    private void setupTextures(GL2 gl, Material material, boolean useTextures) {

        List<String> texturesComponent = material.getTexturesComponent();
        boolean colored = material.getTexture0Color() != null;

        int curLayer = MAX_TEXTURES_LAYERS;

        while (curLayer > 0) {
            curLayer--;
            gl.glActiveTexture(GL_TEXTURE[curLayer]);
            gl.glDisable(GL.GL_TEXTURE_2D);
        }

        curLayer = useTextures ? 0 : texturesComponent.size();

        for (; curLayer < MAX_TEXTURES_LAYERS && curLayer < texturesComponent.size(); curLayer++) {
            gl.glActiveTexture(GL_TEXTURE[curLayer]);
            gl.glEnable(GL.GL_TEXTURE_2D);

            Texture texture = getTexture(gl, texturesComponent.get(curLayer));
            //enableTransparentText(gl);
            bindTexture(gl, texture);

            if (curLayer == 0) {
                if (colored) {
                    // For colored textures
                    // material.setAmbientDiffuse(new
                    // AmbientDiffuseComponent(Color.WHITE, Color.WHITE));
                    float[] rgbComponents = material.getTexture0Color().getRGBComponents(new float[4]);
                    rgbComponents[3] = 0.7f;

                    gl.glTexEnvfv(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_COLOR, rgbComponents, 0);

                    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_COMBINE);
                    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_RGB, GL2ES1.GL_INTERPOLATE);

                    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2ES1.GL_CONSTANT);
                    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND0_RGB, GL.GL_SRC_COLOR);

                    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL.GL_TEXTURE);
                    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND1_RGB, GL.GL_SRC_COLOR);

                    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE2_RGB, GL2ES1.GL_CONSTANT);
                    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND2_RGB, GL.GL_SRC_ALPHA);
                } else {
                    gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);
                }
            } else {
                /*
                 * Calculates texture color by choosing color value between
                 * previous texture and current one. As switch key use alpha
                 * channel from previous texture.
                 */
                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_COMBINE);
                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_RGB, GL2ES1.GL_INTERPOLATE);

                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL.GL_TEXTURE);
                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND0_RGB, GL.GL_SRC_COLOR);

                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2ES1.GL_PREVIOUS);
                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND1_RGB, GL.GL_SRC_COLOR);

                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE2_RGB, GL.GL_TEXTURE);
                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND2_RGB, GL.GL_SRC_ALPHA);

                /*
                 * The final alpha should be 1. Sum both alpha channels from
                 * previous texture and current one.
                 */
                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_ALPHA, GL2ES1.GL_ADD);
                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND0_ALPHA, GL2ES1.GL_PREVIOUS);
                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND1_ALPHA, GL.GL_SRC_ALPHA);
                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_ALPHA, GL2ES1.GL_PREVIOUS);
                gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE1_ALPHA, GL.GL_TEXTURE);
            }
        }

        if (colored && 0 < curLayer && curLayer < MAX_TEXTURES_LAYERS) {
            gl.glActiveTexture(GL_TEXTURE[curLayer]);
            gl.glEnable(GL.GL_TEXTURE_2D);

            Texture texture = getTexture(gl, texturesComponent.get(curLayer-1));
            bindTexture(gl, texture);

            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_COMBINE);
            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_RGB, GL2ES1.GL_MODULATE);

            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2ES1.GL_PRIMARY_COLOR);
            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND0_RGB, GL.GL_SRC_COLOR);

            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2ES1.GL_PREVIOUS);
            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND1_RGB, GL.GL_SRC_COLOR);

            /* Replete alpha with value from previous pass. */
            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_ALPHA, GL.GL_REPLACE);
            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND0_ALPHA, GL2ES1.GL_PREVIOUS);
            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_ALPHA, GL2ES1.GL_PREVIOUS);

            /* Replete alpha with value from previous pass. */
            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_ALPHA, GL.GL_REPLACE);
            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_OPERAND0_ALPHA, GL2ES1.GL_PREVIOUS);
            gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_ALPHA, GL2ES1.GL_PREVIOUS);
            curLayer++;
        }
    }

    public void unbindTexture(GL2 gl) {

        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glPopMatrix();

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glPopMatrix();
    }

    /**
     * @param gl
     * @param texture
     */
    public void bindTexture(GL2 gl, Texture texture) {
        // switch to texture mode and push a new matrix on the stack
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glPushMatrix();

        // check to see if the texture needs flipping
        if (texture.getMustFlipVertically()) {
            gl.glScaled(1, -1, 1);
            gl.glTranslated(0, -1, 0);
        }

        // switch to modelview matrix and push a new matrix on the stack
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glPushMatrix();

        // This is required to repeat textures
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);

        // enable, bind
        texture.enable(gl);
        texture.bind(gl);
    }

    /**
     * @param pGl
     *            FIXME move to util!
     */
    public static void enableTransparentText(GL2 pGl) {
        // do not draw the transparent parts of the texture
        pGl.glEnable(GL.GL_BLEND);
        pGl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        // don't show source alpha parts in the destination

        // determine which areas of the polygon are to be rendered
        pGl.glEnable(GL2ES1.GL_ALPHA_TEST);
        pGl.glAlphaFunc(GL.GL_GREATER, 0); // only render if alpha > 0
    }

    /**
     * @param pGl
     *            FIXME move to util!
     */
    public static void disableTransparentText(GL2 pGl) {
        pGl.glDisable(GL2ES1.GL_ALPHA_TEST);
        pGl.glDisable(GL.GL_BLEND);

    }

    public void setDefaultMaterial(GL2 gl) {

        int sides = drawTwoSided ? GL.GL_FRONT_AND_BACK : GL.GL_FRONT;

        gl.glMaterialfv(sides, GLLightingFunc.GL_DIFFUSE, new float[] { 0.8f, 0.8f, 0.8f, 1.0f }, 0);

        gl.glMaterialfv(sides, GLLightingFunc.GL_AMBIENT, new float[] { 0.5f, 0.5f, 0.5f, 1.0f }, 0);
        // pGl.glMaterialfv(sides, GL2.GL_AMBIENT, new float[] {0.2f,
        // 0.2f, 0.2f, 1.0f}, 0);
        gl.glMaterialfv(sides, GLLightingFunc.GL_SPECULAR, new float[] { 0.0f, 0.0f, 0.0f, 1.0f }, 0);
        gl.glMaterialf(sides, GLLightingFunc.GL_SHININESS, 0.0f);
        gl.glMaterialfv(sides, GLLightingFunc.GL_EMISSION, new float[] { 0.0f, 0.0f, 0.0f, 1.0f }, 0);
    }

    private void setupMaterialOtherComponent(GL2 pGl, OtherComponent material, int sides) {

        float[] rgba = new float[4];

        pGl.glMaterialfv(sides, GLLightingFunc.GL_SPECULAR, material.getSpecularColor().getRGBComponents(rgba), 0);

        pGl.glMaterialf(sides, GLLightingFunc.GL_SHININESS, material.getShininess());

        pGl.glMaterialfv(sides, GLLightingFunc.GL_EMISSION, material.getEmissive().getRGBComponents(rgba), 0);

        lastOtherComponent = material;
    }

    private void setupMaterialAmbientDiffuseComponent(GL2 pGl, AmbientDiffuseComponent material, int sides) {

        float[] rgba = new float[4];

        pGl.glMaterialfv(sides, GLLightingFunc.GL_DIFFUSE, material.getDiffuseColor().getRGBComponents(rgba), 0);

        pGl.glMaterialfv(sides, GLLightingFunc.GL_AMBIENT, material.getAmbientColor().getRGBComponents(rgba), 0);

        lastAmbientDiffuseComponent = material;
    }

    public void resetMaterials() {
        lastSides = 0;
        lastOtherComponent = null;
        lastAmbientDiffuseComponent = null;
    }

    Material defaultMaterial = new Material();

    public void setupDefaultMaterial(GL2 pGL) {
        setupMaterial2(pGL, defaultMaterial, drawTwoSided ? GL.GL_FRONT_AND_BACK : GL.GL_FRONT);
    }

    private void setupMaterial2(GL2 pGl, Material material, int sides) {

        if (isAmbientDiffuseChanged(material.getAmbientDiffuse()) || isSidesChanged(sides)) {
            setupMaterialAmbientDiffuseComponent(pGl, material.getAmbientDiffuse(), sides);
        }

        if (isOtherComponentChanged(material.getOther()) || isSidesChanged(sides)) {
            setupMaterialOtherComponent(pGl, material.getOther(), sides);
        }

        lastSides = sides;
    }

    private boolean isSidesChanged(int sides) {
        return lastSides == 0 || lastSides != sides;
    }

    private boolean isOtherComponentChanged(OtherComponent other) {
        return lastOtherComponent == null || !lastOtherComponent.equals(other);
    }

    private boolean isAmbientDiffuseChanged(AmbientDiffuseComponent ambientDiffuse) {
        return lastAmbientDiffuseComponent == null || !lastAmbientDiffuseComponent.equals(ambientDiffuse);
    }

    private Texture getTexture(GL gl, String file) {

        if (file != null) {
            return textureCacheService.getTexture(gl, file);
        }
        return textureCacheService.getTexture(gl, "/textures/undefined.png");
    }

    /**
     * @return the drawEdges
     */
    public boolean isDrawEdges() {
        return drawEdges;
    }

    /**
     * @param drawEdges
     *            the drawEdges to set
     */
    public void setDrawEdges(boolean drawEdges) {
        this.drawEdges = drawEdges;
    }

    /**
     * @return the drawNormals
     */
    public boolean isDrawNormals() {
        return drawNormals;
    }

    /**
     * @param drawTextures
     *            the drawTextures to set
     */
    public void setDrawTextures(boolean drawTextures) {
        this.drawTextures = drawTextures;
    }

    /**
     * @return the drawTextures
     */
    public boolean isDrawTextures() {
        return drawTextures;
    }

    /**
     * @param drawTwoSided
     *            the drawTwoSided attribute to set
     */
    public void setDrawTwoSided(boolean drawTwoSided) {
        this.drawTwoSided = drawTwoSided;
    }

    /**
     * @return the drawTwoSided attribute
     */
    public boolean isDrawTwoSided() {
        return drawTwoSided;
    }

    /**
     * @param drawNormals
     *            the drawNormals to set
     */
    public void setDrawNormals(boolean drawNormals) {
        this.drawNormals = drawNormals;
    }

    /**
     * @return the debugging
     */
    public boolean isDebugging() {
        return debugging;
    }

    /**
     * @param debugging
     *            the debugging to set
     */
    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    /**
     * @return the textureCacheService
     */
    public TextureCacheService getTextureCacheService() {
        return textureCacheService;
    }

    /**
     * @param textureCacheService
     *            the textureCacheService to set
     */
    public void setTextureCacheService(TextureCacheService textureCacheService) {
        this.textureCacheService = textureCacheService;
    }
}
