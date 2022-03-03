/*
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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
 *
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */
package kendzi.jogl.util.awt;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import kendzi.jogl.glu.GLException;
import kendzi.jogl.glu.GLU;
import kendzi.jogl.util.texture.Texture;
import kendzi.jogl.util.texture.TextureIO;
import kendzi.jogl.util.texture.awt.AWTTextureData;
import org.lwjgl.opengl.GL11;

/**
 * Provides the ability to render into an OpenGL
 * {@link com.jogamp.opengl.util.texture.Texture Texture} using the Java 2D
 * APIs. This renderer class uses an internal Java 2D image (of unspecified
 * type) for its backing store and flushes portions of that image to an OpenGL
 * texture on demand. The resulting OpenGL texture can then be mapped on to a
 * polygon for display.
 */

@Deprecated
public class TextureRenderer {
    // For now, we supply only a BufferedImage back-end for this
    // renderer. In theory we could use the Java 2D/JOGL bridge to fully
    // accelerate the rendering paths, but there are restrictions on
    // what work can be done where; for example, Graphics2D-related work
    // must not be done on the Queue Flusher Thread, but JOGL's
    // OpenGL-related work must be. This implies that the user's code
    // would need to be split up into multiple callbacks run from the
    // appropriate threads, which would be somewhat unfortunate.

    // Whether we have an alpha channel in the (RGB/A) backing store
    private final boolean alpha;

    // Whether we're using only a GL_INTENSITY backing store
    private final boolean intensity;

    // Whether we're attempting to use automatic mipmap generation support
    private boolean mipmap;

    // Whether smoothing is enabled for the OpenGL texture (switching
    // between GL_LINEAR and GL_NEAREST filtering)
    private boolean smoothing = true;
    private boolean smoothingChanged;

    // The backing store itself
    private BufferedImage image;

    private Texture texture;
    private AWTTextureData textureData;
    private boolean mustReallocateTexture;
    private Rectangle dirtyRegion;

    // Current color
    private float r = 1.0f;
    private float g = 1.0f;
    private float b = 1.0f;
    private float a = 1.0f;

    /**
     * Creates a new renderer with backing store of the specified width and height.
     * If <CODE>alpha</CODE> is true, allocates an alpha channel in the backing
     * store image. If <CODE>mipmap</CODE> is true, attempts to use OpenGL's
     * automatic mipmap generation for better smoothing when rendering the
     * TextureRenderer's contents at a distance.
     * 
     * @param width
     *            the width of the texture to render into
     * @param height
     *            the height of the texture to render into
     * @param alpha
     *            whether to allocate an alpha channel for the texture
     * @param mipmap
     *            whether to attempt use of automatic mipmap generation
     */
    public TextureRenderer(final int width, final int height, final boolean alpha, final boolean mipmap) {
        this(width, height, alpha, false, mipmap);
    }

    // Internal constructor to avoid confusion since alpha only makes
    // sense when intensity is not set
    private TextureRenderer(final int width, final int height, final boolean alpha, final boolean intensity,
            final boolean mipmap) {
        this.alpha = alpha;
        this.intensity = intensity;
        this.mipmap = mipmap;
        init(width, height);
    }

    /**
     * Creates a {@link java.awt.Graphics2D Graphics2D} instance for rendering to
     * the backing store of this renderer. The returned object should be disposed of
     * using the normal {@link java.awt.Graphics#dispose() Graphics.dispose()}
     * method once it is no longer being used.
     * 
     * @return a new {@link java.awt.Graphics2D Graphics2D} object for rendering
     *         into the backing store of this renderer
     */
    public Graphics2D createGraphics() {
        return image.createGraphics();
    }

    /**
     * Returns the underlying Java 2D {@link java.awt.Image Image} being rendered
     * into.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Returns the underlying OpenGL Texture object associated with this renderer,
     * synchronizing any dirty regions of the TextureRenderer with the underlying
     * OpenGL texture.
     * 
     * @throws GLException
     *             If an OpenGL context is not current when this method is called
     */
    public Texture getTexture() throws GLException {
        if (dirtyRegion != null) {
            sync(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
            dirtyRegion = null;
        }

        ensureTexture();
        return texture;
    }

    /**
     * Convenience method which assists in rendering portions of the OpenGL texture
     * to the screen as 2D quads in 3D space. Pushes OpenGL state (GL_ENABLE_BIT);
     * disables lighting; and enables the texture in this renderer. Unlike
     * {@link #beginOrthoRendering beginOrthoRendering}, does not modify the depth
     * test, back-face culling, lighting, or the modelview or projection matrices.
     * The user is responsible for setting up the view matrices for correct results
     * of {@link #draw3DRect draw3DRect}. {@link #end3DRendering} must be used in
     * conjunction with this method to restore all OpenGL states.
     * 
     * @throws GLException
     *             If an OpenGL context is not current when this method is called
     */
    public void begin3DRendering() throws GLException {
        beginRendering(false, 0, 0, false);
    }

    // ----------------------------------------------------------------------
    // Internals only below this point
    //

    private void beginRendering(final boolean ortho, final int width, final int height, final boolean disableDepthTestForOrtho) {
        final int attribBits = GL11.GL_ENABLE_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | (ortho ? (GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_TRANSFORM_BIT) : 0);
        GL11.glPushAttrib(attribBits);
        GL11.glDisable(GL11.GL_LIGHTING);
        if (ortho) {
            if (disableDepthTestForOrtho) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GLU.gluOrtho2D(0, width, 0, height);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        final Texture texture = getTexture();
        texture.enable();
        texture.bind();
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        // Change polygon color to last saved
        GL11.glColor4f(r, g, b, a);
        if (smoothingChanged) {
            smoothingChanged = false;
            if (smoothing) {
                texture.setTexParameteri(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                if (mipmap) {
                    texture.setTexParameteri(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
                } else {
                    texture.setTexParameteri(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                }
            } else {
                texture.setTexParameteri(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                texture.setTexParameteri(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            }
        }
    }

    private void init(final int width, final int height) {
        // Discard previous BufferedImage if any
        if (image != null) {
            image.flush();
            image = null;
        }

        // Infer the internal format if not an intensity texture
        final int internalFormat = (intensity ? GL11.GL_INTENSITY : 0);
        final int imageType = (intensity ? BufferedImage.TYPE_BYTE_GRAY
                : (alpha ? BufferedImage.TYPE_INT_ARGB_PRE : BufferedImage.TYPE_INT_RGB));
        image = new BufferedImage(width, height, imageType);
        // Always realllocate the TextureData associated with this
        // BufferedImage; it's just a reference to the contents but we
        // need it in order to update sub-regions of the underlying
        // texture
        textureData = new AWTTextureData(internalFormat, 0, mipmap, image);
        // For now, always reallocate the underlying OpenGL texture when
        // the backing store size changes
        mustReallocateTexture = true;
    }

    /**
     * Synchronizes the specified region of the backing store down to the underlying
     * OpenGL texture. If {@link #markDirty markDirty} is used instead to indicate
     * the regions that are out of sync, this method does not need to be called.
     * 
     * @param x
     *            the x coordinate (in Java 2D coordinates -- relative to upper
     *            left) of the region to update
     * @param y
     *            the y coordinate (in Java 2D coordinates -- relative to upper
     *            left) of the region to update
     * @param width
     *            the width of the region to update
     * @param height
     *            the height of the region to update
     * 
     * @throws GLException
     *             If an OpenGL context is not current when this method is called
     */
    private void sync(final int x, final int y, final int width, final int height) throws GLException {
        // Force allocation if necessary
        final boolean canSkipUpdate = ensureTexture();

        if (!canSkipUpdate) {
            // Update specified region.
            // NOTE that because BufferedImage-based TextureDatas now don't
            // do anything to their contents, the coordinate systems for
            // OpenGL and Java 2D actually line up correctly for
            // updateSubImage calls, so we don't need to do any argument
            // conversion here (i.e., flipping the Y coordinate).
            texture.updateSubImage(textureData, 0, x, y, x, y, width, height);
        }
    }

    // Returns true if the texture was newly allocated, false if not
    private boolean ensureTexture() {
        if (mustReallocateTexture) {
            if (texture != null) {
                texture.destroy();
                texture = null;
            }
            mustReallocateTexture = false;
        }

        if (texture == null) {
            texture = TextureIO.newTexture(textureData);
            if (mipmap && !texture.isUsingAutoMipmapGeneration()) {
                // Only try this once
                texture.destroy();
                mipmap = false;
                textureData.setMipmap(false);
                texture = TextureIO.newTexture(textureData);
            }

            if (!smoothing) {
                // The TextureIO classes default to GL_LINEAR filtering
                texture.setTexParameteri(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                texture.setTexParameteri(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            }
            return true;
        }

        return false;
    }
}