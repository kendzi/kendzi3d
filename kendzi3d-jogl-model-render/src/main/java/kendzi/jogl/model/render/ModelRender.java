/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.model.render;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import java.util.List;

import kendzi.jogl.model.geometry.Face;
import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.geometry.material.AmbientDiffuseComponent;
import kendzi.jogl.model.geometry.material.Material;
import kendzi.jogl.model.geometry.material.OtherComponent;
import kendzi.jogl.texture.TextureCacheService;
import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * Renderer for models. Main interaction with opengl backend.
 */
public class ModelRender {

    /** Log. */
    private static final Logger log = Logger.getLogger(ModelRender.class);

    private static final int[] GL_TEXTURE = { GL13.GL_TEXTURE0, GL13.GL_TEXTURE1, GL13.GL_TEXTURE2, GL13.GL_TEXTURE3 };

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

    private final Material defaultMaterial = new Material();

    /**
     *
     */
    public ModelRender() {

    }

    /**
     * Face counter.
     *
     * @return face counter.
     */
    public int getFaceCount() {
        return faceCount;
    }

    /**
     * Resets face counter.
     */
    public void resetFaceCount() {
        faceCount = 0;
    }

    /**
     * Renders model.
     *
     * @param gl
     *            gl context
     * @param model
     *            model to render
     */
    public void render(GL2 gl, Model model) {

        if (model.useLight) {
            GL11.glEnable(GL11.GL_LIGHTING);
        }

        draw(gl, model);

        if (model.useLight) {
            GL11.glDisable(GL11.GL_LIGHTING);
        }

        // GL11.glDisable(GL11.GL_CULL_FACE);

        if (drawEdges || model.drawEdges) {
            DebugModelRendererUtil.drawEdges(model);
        }
        if (drawNormals || model.drawNormals) {
            DebugModelRendererUtil.drawNormals(model);
        }

        GL11.glColor3f(1.0f, 1.0f, 1.0f);
    }

    public void renderRaw(GL2 gl, Model model) {
        draw(gl, model);
    }

    private void draw(GL2 gl, Model model) {

        int mi = 0;
        int fi = 0;

        try {

            if (model.useCullFaces) {
                GL11.glEnable(GL11.GL_CULL_FACE);
            }

            GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, model.useTwoSided || drawTwoSided ? GL11.GL_TRUE : GL11.GL_FALSE);

            for (mi = 0; mi < model.mesh.length; mi++) {
                Mesh mesh = model.mesh[mi];

                Material material = model.getMaterial(mesh.materialID);

                setupMaterial2(material, model.useTwoSided || drawTwoSided ? GL11.GL_FRONT_AND_BACK : GL11.GL_FRONT);

                if (drawTextures) {
                    if (model.useTextureAlpha) {
                        enableTransparentText();
                    }
                    setupTextures(gl, material, mesh.hasTexture);
                }

                faceCount += mesh.face.length;

                for (fi = 0; fi < mesh.face.length; fi++) {
                    Face face = mesh.face[fi];

                    int numOfTextureLayers = Math.min(MAX_TEXTURES_LAYERS, face.coordIndexLayers.length);
                    if (!drawTextures || !mesh.hasTexture) {
                        numOfTextureLayers = 0;
                    }

                    GL11.glBegin(face.type);

                    for (int i = 0; i < face.vertIndex.length; i++) {
                        int vetexIndex = face.vertIndex[i];
                        // if (face.normalIndex != null &&
                        // face.normalIndex.length > i) {
                        int normalIndex = face.normalIndex[i];

                        GL11.glNormal3d(mesh.normals[normalIndex].x, mesh.normals[normalIndex].y, mesh.normals[normalIndex].z);
                        // }

                        for (int tl = 0; tl < numOfTextureLayers; tl++) {
                            int textureIndex = face.coordIndexLayers[tl][i];
                            GL13.glMultiTexCoord2d(GL_TEXTURE[tl], mesh.texCoords[textureIndex].u,
                                    mesh.texCoords[textureIndex].v);
                        }

                        GL11.glVertex3d(mesh.vertices[vetexIndex].x, mesh.vertices[vetexIndex].y, mesh.vertices[vetexIndex].z);
                    }

                    GL11.glEnd();
                }

                if (drawTextures) {
                    if (model.useTextureAlpha) {
                        disableTransparentText();
                    }
                    unsetupTextures(material, mesh.hasTexture);
                }
            }

            GL11.glColor3f(1.0f, 1.0f, 1.0f);

        } catch (RuntimeException e) {
            throw new RuntimeException("error model: " + model.getSource() + " mesh: " + mi + " ("
                    + (model.mesh[mi] != null ? model.mesh[mi].name : "") + ")" + " face: " + fi, e);
        } finally {

            GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_FALSE);

            GL11.glDisable(GL11.GL_CULL_FACE);
        }

    }

    private void unsetupTextures(Material material, boolean useTextures) {

        List<String> texturesComponent = material.getTexturesComponent();
        boolean colored = material.getTexture0Color() != null;

        int curLayer = texturesComponent.size();

        if (colored && 0 < curLayer && curLayer < MAX_TEXTURES_LAYERS) {
            GL13.glActiveTexture(GL_TEXTURE[curLayer]);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            unbindTexture();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        curLayer = useTextures ? curLayer : 0;

        while (curLayer > 0) {
            curLayer--;
            GL13.glActiveTexture(GL_TEXTURE[curLayer]);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            unbindTexture();
            // disableTransparentText(gl);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            if (curLayer == 0) {
                if (colored) {
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
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
            GL13.glActiveTexture(GL_TEXTURE[curLayer]);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        curLayer = useTextures ? 0 : texturesComponent.size();

        for (; curLayer < MAX_TEXTURES_LAYERS && curLayer < texturesComponent.size(); curLayer++) {
            GL13.glActiveTexture(GL_TEXTURE[curLayer]);
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            Texture texture = getTexture(gl, texturesComponent.get(curLayer));
            // enableTransparentText(gl);
            bindTexture(gl, texture);

            if (curLayer == 0) {
                if (colored) {
                    // For colored textures
                    // material.setAmbientDiffuse(new
                    // AmbientDiffuseComponent(Color.WHITE, Color.WHITE));
                    float[] rgbComponents = material.getTexture0Color().getRGBComponents(new float[4]);
                    rgbComponents[3] = 0.7f;

                    GL11.glTexEnvfv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, rgbComponents);

                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL13.GL_INTERPOLATE);

                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB, GL13.GL_CONSTANT);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);

                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB, GL11.GL_TEXTURE);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);

                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_RGB, GL13.GL_CONSTANT);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);
                } else {
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                }
            } else {
                /*
                 * Calculates texture color by choosing color value between previous texture and
                 * current one. As switch key use alpha channel from previous texture.
                 */
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL13.GL_INTERPOLATE);

                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB, GL13.GL_TEXTURE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL13.GL_SRC_COLOR);

                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB, GL13.GL_PREVIOUS);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, GL13.GL_SRC_COLOR);

                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_RGB, GL13.GL_TEXTURE);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);

                /*
                 * The final alpha should be 1. Sum both alpha channels from previous texture
                 * and current one.
                 */
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_ADD);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL13.GL_PREVIOUS);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA, GL13.GL_PREVIOUS);
                GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_ALPHA, GL11.GL_TEXTURE);
            }
        }

        if (colored && 0 < curLayer && curLayer < MAX_TEXTURES_LAYERS) {
            GL13.glActiveTexture(GL_TEXTURE[curLayer]);
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            Texture texture = getTexture(gl, texturesComponent.get(curLayer - 1));
            bindTexture(gl, texture);

            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL11.GL_MODULATE);

            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB, GL13.GL_PRIMARY_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);

            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB, GL13.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);

            /* Replete alpha with value from previous pass. */
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL13.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA, GL13.GL_PREVIOUS);

            /* Replete alpha with value from previous pass. */
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL13.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_ALPHA, GL13.GL_PREVIOUS);
            curLayer++;
        }
    }

    /**
     * Unbind texture.
     *
     */
    public void unbindTexture() {

        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPopMatrix();

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }

    /**
     * Bind texture.
     *
     * @param gl
     *            context
     * @param texture
     *            the texture
     */
    public void bindTexture(GL2 gl, Texture texture) {
        // switch to texture mode and push a new matrix on the stack
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();

        // check to see if the texture needs flipping
        if (texture.getMustFlipVertically()) {
            GL11.glScaled(1, -1, 1);
            GL11.glTranslated(0, -1, 0);
        }

        // switch to modelview matrix and push a new matrix on the stack
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();

        // This is required to repeat textures
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        // enable, bind
        texture.enable(gl);
        texture.bind(gl);
    }

    /**
     */
    public static void enableTransparentText() {
        // do not draw the transparent parts of the texture
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // don't show source alpha parts in the destination

        // determine which areas of the polygon are to be rendered
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0); // only render if alpha > 0
    }

    /**
     */
    public static void disableTransparentText() {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);

    }

    private void setupMaterialOtherComponent(OtherComponent material, int sides) {

        float[] rgba = new float[4];

        GL11.glMaterialfv(sides, GL11.GL_SPECULAR, material.getSpecularColor().getRGBComponents(rgba));

        GL11.glMaterialf(sides, GL11.GL_SHININESS, material.getShininess());

        GL11.glMaterialfv(sides, GL11.GL_EMISSION, material.getEmissive().getRGBComponents(rgba));

        lastOtherComponent = material;
    }

    private void setupMaterialAmbientDiffuseComponent(AmbientDiffuseComponent material, int sides) {

        float[] rgba = new float[4];

        GL11.glMaterialfv(sides, GL11.GL_DIFFUSE, material.getDiffuseColor().getRGBComponents(rgba));

        GL11.glMaterialfv(sides, GL11.GL_AMBIENT, material.getAmbientColor().getRGBComponents(rgba));

        lastAmbientDiffuseComponent = material;
    }

    /**
     * Resets cached materials settings.
     */
    public void resetMaterials() {
        lastSides = 0;
        lastOtherComponent = null;
        lastAmbientDiffuseComponent = null;
    }

    /**
     * Setups default material for opengl context. It allow to rested opengl state
     * to its default when next rendering loop starts.
     *
     */
    public void setupDefaultMaterial() {
        setupMaterial2(defaultMaterial, drawTwoSided ? GL11.GL_FRONT_AND_BACK : GL11.GL_FRONT);
    }

    private void setupMaterial2(Material material, int sides) {

        if (isAmbientDiffuseChanged(material.getAmbientDiffuse()) || isSidesChanged(sides)) {
            setupMaterialAmbientDiffuseComponent(material.getAmbientDiffuse(), sides);
        }

        if (isOtherComponentChanged(material.getOther()) || isSidesChanged(sides)) {
            setupMaterialOtherComponent(material.getOther(), sides);
        }

        lastSides = sides;
    }

    private boolean isSidesChanged(int sides) {
        return lastSides == 0 || lastSides == GL11.GL_FRONT && sides == GL11.GL_FRONT_AND_BACK;
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
