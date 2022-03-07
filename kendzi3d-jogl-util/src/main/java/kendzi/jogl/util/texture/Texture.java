/*
 * Copyright (c) 2005 Sun Microsystems, Inc. All Rights Reserved.
 * Copyright (c) 2010 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

package kendzi.jogl.util.texture;

import java.nio.ByteBuffer;
import java.util.Optional;

import kendzi.jogl.glu.GLException;
import kendzi.jogl.glu.GLU;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureCompressionS3TC;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengles.GLES32;

/**
 * Represents an OpenGL texture object. Contains convenience routines for
 * enabling/disabling OpenGL texture state, binding this texture, and computing
 * texture coordinates for both the entire image as well as a sub-image.
 *
 * <a name="textureCallOrder">
 * <h5>Order of Texture Commands</h5></a>
 * <p>
 * Due to many confusions w/ texture usage, following list described the order
 * and semantics of texture unit selection, binding and enabling.
 * <ul>
 * <li><i>Optional:</i> Set active textureUnit via
 * <code>gl.glActiveTexture(GL11.GL_TEXTURE0 + textureUnit)</code>,
 * <code>0</code> is default.</li>
 * <li>Bind <code>textureId</code> -> active <code>textureUnit</code>'s
 * <code>textureTarget</code> via
 * <code>gl.glBindTexture(textureTarget, textureId)</code></li>
 * <li><i>Compatible Context Only:</i> Enable active <code>textureUnit</code>'s
 * <code>textureTarget</code> via <code>glEnable(textureTarget)</code>.
 * <li><i>Optional:</i> Fiddle with the texture parameters and/or environment
 * settings.</li>
 * <li>GLSL: Use <code>textureUnit</code> in your shader program, enable shader
 * program.</li>
 * <li>Issue draw commands</li>
 * </ul>
 * </p>
 *
 * <p>
 * <a name="nonpow2"><b>Non-power-of-two restrictions</b></a> <br>
 * When creating an OpenGL texture object, the Texture class will attempt to use
 * <i>non-power-of-two textures</i> (NPOT) if available, see
 * {@link GL#isNPOTTextureAvailable()}. Further more, <a href=
 * "http://www.opengl.org/registry/specs/ARB/texture_rectangle.txt">GL_ARB_texture_rectangle</a>
 * (RECT) will be attempted on OSX w/ ATI drivers. If NPOT is not available or
 * RECT not chosen, the Texture class will simply upload a non-pow2-sized image
 * into a standard pow2-sized texture (without any special scaling). Since the
 * choice of extension (or whether one is used at all) depends on the user's
 * machine configuration, developers are recommended to use
 * {@link #getImageTexCoords} and {@link #getSubImageTexCoords}, as those
 * methods will calculate the appropriate texture coordinates for the situation.
 *
 * <p>
 * One caveat in this approach is that certain texture wrap modes (e.g.
 * <code>GL_REPEAT</code>) are not legal when the GL_ARB_texture_rectangle
 * extension is in use. Another issue to be aware of is that in the default pow2
 * scenario, if the original image does not have pow2 dimensions, then wrapping
 * may not work as one might expect since the image does not extend to the edges
 * of the pow2 texture. If texture wrapping is important, it is recommended to
 * use only pow2-sized images with the Texture class.
 *
 * <p>
 * <a name="perftips"><b>Performance Tips</b></a> <br>
 * For best performance, try to avoid calling {@link #enable} / {@link #bind} /
 * {@link #disable} any more than necessary. For example, applications using
 * many Texture objects in the same scene may want to reduce the number of calls
 * to both {@link #enable} and {@link #disable}. To do this it is necessary to
 * call {@link #getTarget} to make sure the OpenGL texture target is the same
 * for all of the Texture objects in use; non-power-of-two textures using the
 * GL_ARB_texture_rectangle extension use a different target than power-of-two
 * textures using the GL_TEXTURE_2D target. Note that when switching between
 * textures it is necessary to call {@link #bind}, but when drawing many
 * triangles all using the same texture, for best performance only one call to
 * {@link #bind} should be made. User may also utilize multiple texture units,
 * see <a href="#textureCallOrder"> order of texture commands above</a>.
 *
 * <p>
 * <a name="premult"><b>Alpha premultiplication and blending</b></a>
 * <p>
 * <i>Disclaimer: Consider performing alpha premultiplication in shader code, if
 * really desired! Otherwise use RGBA.</i><br/>
 * </p>
 * <p>
 * The Texture class does not convert RGBA image data into premultiplied data
 * when storing it into an OpenGL texture.
 * </p>
 * <p>
 * The mathematically correct way to perform blending in OpenGL with the SrcOver
 * "source over destination" mode, or any other Porter-Duff rule, is to use
 * <i>premultiplied color components</i>, which means the R/G/ B color
 * components must have been multiplied by the alpha value. If using
 * <i>premultiplied color components</i> it is important to use the correct
 * blending function; for example, the SrcOver rule is expressed as:
 * 
 * <pre>
 * gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
 * </pre>
 * 
 * Also, when using a texture function like <code>GL_MODULATE</code> where the
 * current color plays a role, it is important to remember to make sure that the
 * color is specified in a premultiplied form, for example:
 * 
 * <pre>
 float a = ...;
 float r = r * a;
 float g = g * a;
 float b = b * a;
 gl.glColor4f(r, g, b, a);
 * </pre>
 *
 * For reference, here is a list of the Porter-Duff compositing rules and the
 * associated OpenGL blend functions (source and destination factors) to use in
 * the face of premultiplied alpha:
 *
 * <CENTER>
 * <TABLE WIDTH="75%">
 * <TR>
 * <TD>Rule
 * <TD>Source
 * <TD>Dest
 * <TR>
 * <TD>Clear
 * <TD>GL_ZERO
 * <TD>GL_ZERO
 * <TR>
 * <TD>Src
 * <TD>GL_ONE
 * <TD>GL_ZERO
 * <TR>
 * <TD>SrcOver
 * <TD>GL_ONE
 * <TD>GL_ONE_MINUS_SRC_ALPHA
 * <TR>
 * <TD>DstOver
 * <TD>GL_ONE_MINUS_DST_ALPHA
 * <TD>GL_ONE
 * <TR>
 * <TD>SrcIn
 * <TD>GL_DST_ALPHA
 * <TD>GL_ZERO
 * <TR>
 * <TD>DstIn
 * <TD>GL_ZERO
 * <TD>GL_SRC_ALPHA
 * <TR>
 * <TD>SrcOut
 * <TD>GL_ONE_MINUS_DST_ALPHA
 * <TD>GL_ZERO
 * <TR>
 * <TD>DstOut
 * <TD>GL_ZERO
 * <TD>GL_ONE_MINUS_SRC_ALPHA
 * <TR>
 * <TD>Dst
 * <TD>GL_ZERO
 * <TD>GL_ONE
 * <TR>
 * <TD>SrcAtop
 * <TD>GL_DST_ALPHA
 * <TD>GL_ONE_MINUS_SRC_ALPHA
 * <TR>
 * <TD>DstAtop
 * <TD>GL_ONE_MINUS_DST_ALPHA
 * <TD>GL_SRC_ALPHA
 * <TR>
 * <TD>AlphaXor
 * <TD>GL_ONE_MINUS_DST_ALPHA
 * <TD>GL_ONE_MINUS_SRC_ALPHA
 * </TABLE>
 * </CENTER>
 * 
 * @author Chris Campbell, Kenneth Russell, et.al.
 */
@Deprecated
public class Texture {
    /** The GL target type for this texture. */
    private int target;
    /**
     * The image GL target type for this texture, or its sub-components if cubemap.
     */
    private int imageTarget;
    /** The GL texture ID. */
    private int texID;
    /** The width of the texture. */
    private int texWidth;
    /** The height of the texture. */
    private int texHeight;
    /** The width of the image. */
    private int imgWidth;
    /** The height of the image. */
    private int imgHeight;
    /**
     * Indicates whether the TextureData requires a vertical flip of the texture
     * coords.
     */
    private boolean mustFlipVertically;
    /**
     * Indicates whether we're using automatic mipmap generation support
     * (GL_GENERATE_MIPMAP).
     */
    private boolean usingAutoMipmapGeneration;

    /** The texture coordinates corresponding to the entire image. */
    private TextureCoords coords;

    @Override
    public String toString() {
        final String targetS = target == imageTarget ? Integer.toHexString(target)
                : Integer.toHexString(target) + " - image " + Integer.toHexString(imageTarget);
        return "Texture[target " + targetS + ", name " + texID + ", " + imgWidth + "/" + texWidth + " x " + imgHeight + "/"
                + texHeight + ", y-flip " + mustFlipVertically + ", " + estimatedMemorySize + " bytes]";
    }

    /** An estimate of the amount of texture memory this texture consumes. */
    private int estimatedMemorySize;

    public Texture(final TextureData data) throws GLException {
        this.texID = 0;
        this.target = 0;
        this.imageTarget = 0;
        updateImage(data);
    }

    /**
     * Constructor for use when creating e.g. cube maps, where there is no initial
     * texture data
     * 
     * @param target
     *            the OpenGL texture target, eg GL11.GL_TEXTURE_2D,
     *            GL11.GL_TEXTURE_RECTANGLE
     */
    public Texture(final int target) {
        this.texID = 0;
        this.target = target;
        this.imageTarget = target;
    }

    /**
     * Constructor to wrap an OpenGL texture ID from an external library and allows
     * some of the base methods from the Texture class, such as binding and querying
     * of texture coordinates, to be used with it. Attempts to update such textures'
     * contents will yield undefined results.
     *
     * @param textureID
     *            the OpenGL texture object to wrap
     * @param target
     *            the OpenGL texture target, eg GL11.GL_TEXTURE_2D,
     *            GL11.GL_TEXTURE_RECTANGLE
     * @param texWidth
     *            the width of the texture in pixels
     * @param texHeight
     *            the height of the texture in pixels
     * @param imgWidth
     *            the width of the image within the texture in pixels (if the
     *            content is a sub-rectangle in the upper left corner); otherwise,
     *            pass in texWidth
     * @param imgHeight
     *            the height of the image within the texture in pixels (if the
     *            content is a sub-rectangle in the upper left corner); otherwise,
     *            pass in texHeight
     * @param mustFlipVertically
     *            indicates whether the texture coordinates must be flipped
     *            vertically in order to properly display the texture
     */
    public Texture(final int textureID, final int target, final int texWidth, final int texHeight, final int imgWidth,
            final int imgHeight, final boolean mustFlipVertically) {
        this.texID = textureID;
        this.target = target;
        this.imageTarget = target;
        this.mustFlipVertically = mustFlipVertically;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.updateTexCoords();
    }

    /**
     * Enables this texture's target (e.g., GL_TEXTURE_2D) in the given GL context's
     * state. This method is a shorthand equivalent of the following OpenGL code:
     * 
     * <pre>
     * GL11.glEnable(texture.getTarget());
     * </pre>
     * <p>
     * Call is ignored if the {@link GL} object's context is using a core profile,
     * see {@link GL#isGLcore()}, or if {@link #getTarget()} is
     * {@link GLES2#GL_TEXTURE_EXTERNAL_OES}.
     * </p>
     * <p>
     * See the <a href="#perftips">performance tips</a> above for hints on how to
     * maximize performance when using many Texture objects.
     * </p>
     * 
     * @throws GLException
     *             if no OpenGL context was current or if any OpenGL-related errors
     *             occurred
     */
    public void enable() throws GLException {
        final GLCapabilities cap = GL.getCapabilities();
        if (!cap.forwardCompatible) {
            GL11.glEnable(target);
        }
    }

    /**
     * Disables this texture's target (e.g., GL_TEXTURE_2D) in the given GL state.
     * This method is a shorthand equivalent of the following OpenGL code:
     * 
     * <pre>
     * GL11.glDisable(texture.getTarget());
     * </pre>
     * <p>
     * Call is ignored if the {@link GL} object's context is using a core profile,
     * see {@link GL#isGLcore()}, or if {@link #getTarget()} is
     * {@link GLES2#GL_TEXTURE_EXTERNAL_OES}.
     * </p>
     * <p>
     * See the <a href="#perftips">performance tips</a> above for hints on how to
     * maximize performance when using many Texture objects.
     * </p>
     * 
     * @throws GLException
     *             if no OpenGL context was current or if any OpenGL-related errors
     *             occurred
     */
    public void disable() throws GLException {
        final GLCapabilities cap = GL.getCapabilities();
        if (!cap.forwardCompatible) {
            GL11.glDisable(target);
        }
    }

    /**
     * Binds this texture to the given GL context. This method is a shorthand
     * equivalent of the following OpenGL code:
     * 
     * <pre>
     * GL11.glBindTexture(texture.getTarget(), texture.getTextureObject());
     * </pre>
     *
     * See the <a href="#perftips">performance tips</a> above for hints on how to
     * maximize performance when using many Texture objects.
     *
     * @throws GLException
     *             if no OpenGL context was current or if any OpenGL-related errors
     *             occurred
     */
    public void bind() throws GLException {
        validateTexID(true);
        GL11.glBindTexture(target, texID);
    }

    /**
     * Destroys the native resources used by this texture object.
     *
     * @throws GLException
     *             if any OpenGL-related errors occurred
     */
    public void destroy() throws GLException {
        if (0 != texID) {
            GL11.glDeleteTextures(texID);
            texID = 0;
        }
    }

    /**
     * Returns the set of texture coordinates corresponding to the entire image. If
     * the TextureData indicated that the texture coordinates must be flipped
     * vertically, the returned TextureCoords will take that into account.
     *
     * @return the texture coordinates corresponding to the entire image
     */
    public TextureCoords getImageTexCoords() {
        return coords;
    }

    /**
     * Updates the entire content area incl. {@link TextureCoords} of this texture
     * using the data in the given image.
     *
     * @throws GLException
     *             if any OpenGL-related errors occurred
     */
    public void updateImage(final TextureData data) throws GLException {
        updateImage(data, 0);
    }

    /**
     * Indicates whether this texture's texture coordinates must be flipped
     * vertically in order to properly display the texture. This is handled
     * automatically by {@link #getImageTexCoords getImageTexCoords} and
     * {@link #getSubImageTexCoords getSubImageTexCoords}, but applications may
     * generate or otherwise produce texture coordinates which must be corrected.
     */
    public boolean getMustFlipVertically() {
        return mustFlipVertically;
    }

    /**
     * Updates the content area incl. {@link TextureCoords} of the specified target
     * of this texture using the data in the given image. In general this is
     * intended for construction of cube maps.
     *
     * @throws GLException
     *             if any OpenGL-related errors occurred
     */
    public void updateImage(final TextureData data, final int targetOverride) throws GLException {
        validateTexID(true);

        imgWidth = data.getWidth();
        imgHeight = data.getHeight();
        mustFlipVertically = data.getMustFlipVertically();

        int texTarget = 0;
        int texParamTarget = this.target;

        // See whether we have automatic mipmap generation support
        boolean haveAutoMipmapGeneration = GL.getCapabilities().OpenGL14;

        // Indicate to the TextureData what functionality is available
        data.setHaveEXTABGR(GL.getCapabilities().GL_EXT_abgr);
        data.setHaveGL12(GL.getCapabilities().OpenGL12);

        // Indicates whether both width and height are power of two
        final boolean isPOT = isPowerOfTwo(imgWidth) && isPowerOfTwo(imgHeight);

        // Note that automatic mipmap generation doesn't work for
        // GL_ARB_texture_rectangle
        if (!isPOT && !haveNPOT()) {
            haveAutoMipmapGeneration = false;
        }

        boolean expandingCompressedTexture = false;
        boolean done = false;
        if (data.getMipmap() && !haveAutoMipmapGeneration) {
            // GLU always scales the texture's dimensions to be powers of
            // two. It also doesn't really matter exactly what the texture
            // width and height are because the texture coords are always
            // between 0.0 and 1.0.
            imgWidth = nextPowerOfTwo(imgWidth);
            imgHeight = nextPowerOfTwo(imgHeight);
            texWidth = imgWidth;
            texHeight = imgHeight;
            texTarget = GL11.GL_TEXTURE_2D;
            done = true;
        }

        if (!done && preferTexRect() && !isPOT && haveTexRect() && !data.isDataCompressed() && !GL.getCapabilities().OpenGL30) {
            // GL_ARB_texture_rectangle does not work for compressed textures
            texWidth = imgWidth;
            texHeight = imgHeight;
            texTarget = GL31.GL_TEXTURE_RECTANGLE;
            done = true;
        }

        if (!done && (isPOT || haveNPOT())) {
            texWidth = imgWidth;
            texHeight = imgHeight;
            texTarget = GL11.GL_TEXTURE_2D;
            done = true;
        }

        if (!done && haveTexRect() && !data.isDataCompressed() && !GL.getCapabilities().OpenGL30) {
            // GL_ARB_texture_rectangle does not work for compressed textures

            texWidth = imgWidth;
            texHeight = imgHeight;
            texTarget = GL31.GL_TEXTURE_RECTANGLE;
            done = true;
        }

        if (!done) {
            // If we receive non-power-of-two compressed texture data and
            // don't have true hardware support for compressed textures, we
            // can fake this support by producing an empty "compressed"
            // texture image, using glCompressedTexImage2D with that to
            // allocate the texture, and glCompressedTexSubImage2D with the
            // incoming data.
            if (data.isDataCompressed()) {
                if (data.getMipmapData() != null) {

                    // We don't currently support expanding of compressed,
                    // mipmapped non-power-of-two textures to the nearest power
                    // of two; the obvious port of the non-mipmapped code didn't
                    // work
                    throw new GLException(
                            "Mipmapped non-power-of-two compressed textures only supported on OpenGL 2.0 hardware (GL_ARB_texture_non_power_of_two)");
                }

                expandingCompressedTexture = true;
            }

            if (data.getBorder() != 0) {
                throw new RuntimeException("Scaling up a non-power-of-two texture which has a border won't work");
            }
            texWidth = nextPowerOfTwo(imgWidth);
            texHeight = nextPowerOfTwo(imgHeight);
            texTarget = GL11.GL_TEXTURE_2D;
        }
        texParamTarget = texTarget;
        imageTarget = texTarget;
        updateTexCoords();

        if (targetOverride != 0) {
            // Allow user to override auto detection and skip bind step (for
            // cubemap construction)
            if (this.target == 0) {
                throw new GLException("Override of target failed; no target specified yet");
            }
            texTarget = targetOverride;
            texParamTarget = this.target;
            GL11.glBindTexture(texParamTarget, texID);
        } else {
            GL11.glBindTexture(texTarget, texID);
        }

        if (data.getMipmap() && !haveAutoMipmapGeneration) {
            final int[] align = new int[1];
            GL11.glGetIntegerv(GL11.GL_UNPACK_ALIGNMENT, align); // save alignment
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, data.getAlignment());

            if (data.isDataCompressed()) {
                throw new GLException("May not request mipmap generation for compressed textures");
            }

            try {
                // FIXME: may need check for GLUnsupportedException
                GLU.gluBuild2DMipmaps(texTarget, data.getInternalFormat(), data.getWidth(), data.getHeight(),
                        data.getPixelFormat(), data.getPixelType(), data.getBuffer());
            } finally {
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, align[0]); // restore alignment
            }
        } else {
            final ByteBuffer[] mipmapData = data.getMipmapData();
            if (mipmapData != null) {
                int width = texWidth;
                int height = texHeight;
                for (int i = 0; i < mipmapData.length; i++) {
                    if (data.isDataCompressed()) {
                        // Need to use glCompressedTexImage2D directly to allocate and fill this image
                        // Avoid spurious memory allocation when possible
                        GL20.glCompressedTexImage2D(texTarget, i, data.getInternalFormat(), width, height, data.getBorder(),
                                mipmapData[i]);
                    } else {
                        // Allocate texture image at this level
                        GL11.glTexImage2D(texTarget, i, data.getInternalFormat(), width, height, data.getBorder(),
                                data.getPixelFormat(), data.getPixelType(), data.getBuffer());
                        updateSubImageImpl(data, texTarget, i, 0, 0, 0, 0, data.getWidth(), data.getHeight());
                    }

                    width = Math.max(width / 2, 1);
                    height = Math.max(height / 2, 1);
                }
            } else {
                if (data.isDataCompressed()) {
                    if (!expandingCompressedTexture) {
                        // Need to use glCompressedTexImage2D directly to allocate and fill this image
                        // Avoid spurious memory allocation when possible
                        GL13.glCompressedTexImage2D(texTarget, 0, data.getInternalFormat(), texWidth, texHeight, data.getBorder(),
                                data.getBuffer());
                    } else {
                        final ByteBuffer buf = BufferUtils.createByteBuffer(texWidth * texHeight
                                / (data.getInternalFormat() == EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT
                                        || data.getInternalFormat() == EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT
                                                ? 2
                                                : 1));
                        GL13.glCompressedTexImage2D(texTarget, 0, data.getInternalFormat(), texWidth, texHeight, data.getBorder(),
                                buf);
                        updateSubImageImpl(data, texTarget, 0, 0, 0, 0, 0, data.getWidth(), data.getHeight());
                    }
                } else {
                    if (data.getMipmap() && haveAutoMipmapGeneration) {
                        // For now, only use hardware mipmapping for uncompressed 2D
                        // textures where the user hasn't explicitly specified
                        // mipmap data; don't know about interactions between
                        // GL_GENERATE_MIPMAP and glCompressedTexImage2D
                        GL11.glTexParameteri(texParamTarget, GL31.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
                        usingAutoMipmapGeneration = true;
                    }

                    GL11.glTexImage2D(texTarget, 0, data.getInternalFormat(), texWidth, texHeight, data.getBorder(),
                            data.getPixelFormat(), data.getPixelType(), (ByteBuffer) null);
                    updateSubImageImpl(data, texTarget, 0, 0, 0, 0, 0, data.getWidth(), data.getHeight());
                }
            }
        }

        final int minFilter = (data.getMipmap() ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR);
        final int magFilter = GL11.GL_LINEAR;
        final int wrapMode = GL.getCapabilities().OpenGL13 ? GL13.GL_CLAMP_TO_EDGE : GL11.GL_CLAMP;

        // REMIND: figure out what to do for GL_TEXTURE_RECTANGLE_ARB
        if (texTarget != GL31.GL_TEXTURE_RECTANGLE) {
            GL11.glTexParameteri(texParamTarget, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
            GL11.glTexParameteri(texParamTarget, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
            GL11.glTexParameteri(texParamTarget, GL11.GL_TEXTURE_WRAP_S, wrapMode);
            GL11.glTexParameteri(texParamTarget, GL11.GL_TEXTURE_WRAP_T, wrapMode);
            if (this.target == GL13.GL_TEXTURE_CUBE_MAP) {
                GL11.glTexParameteri(texParamTarget, GLES32.GL_TEXTURE_WRAP_R, wrapMode);
            }
        }

        // Don't overwrite target if we're loading e.g. faces of a cube
        // map
        if ((this.target == 0) || (this.target == GL11.GL_TEXTURE_2D) || (this.target == GL31.GL_TEXTURE_RECTANGLE)) {
            this.target = texTarget;
        }

        // This estimate will be wrong for cube maps
        estimatedMemorySize = data.getEstimatedMemorySize();
    }

    /**
     * Updates a subregion of the content area of this texture using the specified
     * sub-region of the given data. If automatic mipmap generation is in use (see
     * {@link #isUsingAutoMipmapGeneration isUsingAutoMipmapGeneration}), updates to
     * the base (level 0) mipmap will cause the lower-level mipmaps to be
     * regenerated, and updates to other mipmap levels will be ignored. Otherwise,
     * if automatic mipmap generation is not in use, only updates the specified
     * mipmap level and does not re-generate mipmaps if they were originally
     * produced or loaded. This method is only supported for uncompressed
     * TextureData sources.
     *
     * @param data
     *            the image data to be uploaded to this texture
     * @param mipmapLevel
     *            the mipmap level of the texture to set. If this is non-zero and
     *            the TextureData contains mipmap data, the appropriate mipmap level
     *            will be selected.
     * @param dstx
     *            the x offset (in pixels) relative to the lower-left corner of this
     *            texture where the update will be applied
     * @param dsty
     *            the y offset (in pixels) relative to the lower-left corner of this
     *            texture where the update will be applied
     * @param srcx
     *            the x offset (in pixels) relative to the lower-left corner of the
     *            supplied TextureData from which to fetch the update rectangle
     * @param srcy
     *            the y offset (in pixels) relative to the lower-left corner of the
     *            supplied TextureData from which to fetch the update rectangle
     * @param width
     *            the width (in pixels) of the rectangle to be updated
     * @param height
     *            the height (in pixels) of the rectangle to be updated
     *
     * @throws GLException
     *             if no OpenGL context was current or if any OpenGL-related errors
     *             occurred
     */
    public void updateSubImage(final TextureData data, final int mipmapLevel, final int dstx, final int dsty, final int srcx,
            final int srcy, final int width, final int height) throws GLException {
        if (data.isDataCompressed()) {
            throw new GLException("updateSubImage specifying a sub-rectangle is not supported for compressed TextureData");
        }
        if (usingAutoMipmapGeneration && mipmapLevel != 0) {
            // When we're using mipmap generation via GL_GENERATE_MIPMAP, we
            // don't need to update other mipmap levels
            return;
        }
        bind();
        updateSubImageImpl(data, target, mipmapLevel, dstx, dsty, srcx, srcy, width, height);
    }

    /**
     * Sets the OpenGL integer texture parameter for the texture's target. This
     * gives control over parameters such as GL_TEXTURE_WRAP_S and
     * GL_TEXTURE_WRAP_T, which by default are set to GL_CLAMP_TO_EDGE if OpenGL 1.2
     * is supported on the current platform and GL_CLAMP if not. Causes this texture
     * to be bound to the current texture state.
     *
     * @throws GLException
     *             if any OpenGL-related errors occurred
     */
    public void setTexParameteri(final int parameterName, final int value) {
        bind();
        GL11.glTexParameteri(target, parameterName, value);
    }

    /**
     * Indicates whether this Texture is using automatic mipmap generation (via the
     * OpenGL texture parameter GL_GENERATE_MIPMAP). This will automatically be used
     * when mipmapping is requested via the TextureData and either OpenGL 1.4 or the
     * GL_SGIS_generate_mipmap extension is available. If so, updates to the base
     * image (mipmap level 0) will automatically propagate down to the lower mipmap
     * levels. Manual updates of the mipmap data at these lower levels will be
     * ignored.
     */
    public boolean isUsingAutoMipmapGeneration() {
        return usingAutoMipmapGeneration;
    }

    // ----------------------------------------------------------------------
    // Internals only below this point
    //

    /**
     * Returns true if the given value is a power of two.
     *
     * @return true if the given value is a power of two, false otherwise
     */
    private static boolean isPowerOfTwo(final int val) {
        return ((val & (val - 1)) == 0);
    }

    /**
     * Returns the nearest power of two that is larger than the given value. If the
     * given value is already a power of two, this method will simply return that
     * value.
     *
     * @param val
     *            the value
     * @return the next power of two
     */
    private static int nextPowerOfTwo(final int val) {
        int ret = 1;
        while (ret < val) {
            ret <<= 1;
        }
        return ret;
    }

    private void updateTexCoords() {
        if (GL31.GL_TEXTURE_RECTANGLE == imageTarget) {
            if (mustFlipVertically) {
                coords = new TextureCoords(0, imgHeight, imgWidth, 0);
            } else {
                coords = new TextureCoords(0, 0, imgWidth, imgHeight);
            }
        } else {
            if (mustFlipVertically) {
                coords = new TextureCoords(0, // l
                        (float) imgHeight / (float) texHeight, // b
                        (float) imgWidth / (float) texWidth, // r
                        0 // t
                );
            } else {
                coords = new TextureCoords(0, // l
                        0, // b
                        (float) imgWidth / (float) texWidth, // r
                        (float) imgHeight / (float) texHeight // t
                );
            }
        }
    }

    private void updateSubImageImpl(final TextureData data, final int newTarget, final int mipmapLevel, int dstx, int dsty,
            int srcx, int srcy, int width, int height) throws GLException {
        data.setHaveEXTABGR(GL.getCapabilities().GL_EXT_abgr);
        data.setHaveGL12(GL.getCapabilities().OpenGL12);

        ByteBuffer buffer = data.getBuffer();
        if (buffer == null && data.getMipmapData() == null) {
            // Assume user just wanted to get the Texture object allocated
            return;
        }

        int rowlen = data.getRowLength();
        int dataWidth = data.getWidth();
        int dataHeight = data.getHeight();
        if (data.getMipmapData() != null) {
            // Compute the width, height and row length at the specified mipmap level
            // Note we do not support specification of the row length for
            // mipmapped textures at this point
            for (int i = 0; i < mipmapLevel; i++) {
                width = Math.max(width / 2, 1);
                height = Math.max(height / 2, 1);

                dataWidth = Math.max(dataWidth / 2, 1);
                dataHeight = Math.max(dataHeight / 2, 1);
            }
            rowlen = 0;
            buffer = data.getMipmapData()[mipmapLevel];
        }

        // Clip incoming rectangles to what is available both on this
        // texture and in the incoming TextureData
        if (srcx < 0) {
            width += srcx;
            srcx = 0;
        }
        if (srcy < 0) {
            height += srcy;
            srcy = 0;
        }
        // NOTE: not sure whether the following two are the correct thing to do
        if (dstx < 0) {
            width += dstx;
            dstx = 0;
        }
        if (dsty < 0) {
            height += dsty;
            dsty = 0;
        }

        if (srcx + width > dataWidth) {
            width = dataWidth - srcx;
        }
        if (srcy + height > dataHeight) {
            height = dataHeight - srcy;
        }
        if (dstx + width > texWidth) {
            width = texWidth - dstx;
        }
        if (dsty + height > texHeight) {
            height = texHeight - dsty;
        }

        if (data.isDataCompressed()) {
            GL13.glCompressedTexSubImage2D(newTarget, mipmapLevel, dstx, dsty, width, height, data.getInternalFormat(), buffer);
        } else {
            final int[] align = { 0 };
            final int[] rowLength = { 0 };
            final int[] skipRows = { 0 };
            final int[] skipPixels = { 0 };
            GL11.glGetIntegerv(GL11.GL_UNPACK_ALIGNMENT, align); // save alignment
            final boolean isGl2GL3 = !GL.getCapabilities().forwardCompatible;
            if (isGl2GL3) {
                GL11.glGetIntegerv(GLES32.GL_UNPACK_ROW_LENGTH, rowLength); // save row length
                GL11.glGetIntegerv(GLES32.GL_UNPACK_SKIP_ROWS, skipRows); // save skipped rows
                GL11.glGetIntegerv(GLES32.GL_UNPACK_SKIP_PIXELS, skipPixels); // save skipped pixels
            }
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, data.getAlignment());
            if (isGl2GL3) {
                GL11.glPixelStorei(GLES32.GL_UNPACK_ROW_LENGTH, rowlen);
                GL11.glPixelStorei(GLES32.GL_UNPACK_SKIP_ROWS, srcy);
                GL11.glPixelStorei(GLES32.GL_UNPACK_SKIP_PIXELS, srcx);
            } else {
                if (rowlen != 0 && rowlen != width && srcy != 0 && srcx != 0) {
                    throw new GLException("rowlen and/or x/y offset only available for GL2");
                }
            }

            GL11.glTexSubImage2D(newTarget, mipmapLevel, dstx, dsty, width, height, data.getPixelFormat(), data.getPixelType(),
                    buffer);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, align[0]); // restore alignment
            if (isGl2GL3) {
                GL11.glPixelStorei(GLES32.GL_UNPACK_ROW_LENGTH, rowLength[0]); // restore row length
                GL11.glPixelStorei(GLES32.GL_UNPACK_SKIP_ROWS, skipRows[0]); // restore skipped rows
                GL11.glPixelStorei(GLES32.GL_UNPACK_SKIP_PIXELS, skipPixels[0]); // restore skipped pixels
            }
        }
    }

    private boolean validateTexID(final boolean throwException) {
        if (0 == texID) {
            texID = GL11.glGenTextures();
            if (0 == texID && throwException) {
                throw new GLException(
                        "Create texture ID invalid: texID " + texID + ", glerr 0x" + Integer.toHexString(GL11.glGetError()));
            }
        }
        return 0 != texID;
    }

    // Helper routines for disabling certain codepaths
    private static boolean haveNPOT() {
        return GL.getCapabilities().GL_ARB_texture_non_power_of_two;
    }

    private static boolean haveTexRect() {
        return (TextureIO.isTexRectEnabled() && GL.getCapabilities().GL_ARB_texture_rectangle);
    }

    private static boolean preferTexRect() {
        // Prefer GL_ARB_texture_rectangle on ATI hardware on Mac OS X
        // due to software fallbacks

        if (Optional.ofNullable(System.getProperty("os.name")).filter(os -> os.contains("mac")).isPresent()) {
            final String vendor = GL11.glGetString(GL11.GL_VENDOR);
            return vendor != null && vendor.startsWith("ATI");
        }

        return false;
    }
}
