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

import java.nio.Buffer;
import java.nio.ByteBuffer;

import kendzi.jogl.util.Buffers;
import kendzi.jogl.util.GLPixelBuffer;

/**
 * Represents the data for an OpenGL texture. This is separated from the notion
 * of a Texture to support things like streaming in of textures in a background
 * thread without requiring an OpenGL context to be current on that thread.
 *
 * @author Chris Campbell
 * @author Kenneth Russell
 * @author Sven Gothel
 */

@Deprecated
public class TextureData {
    /** ColorSpace of pixel data. */
    public enum ColorSpace {
    }

    protected int width;
    protected int height;
    private int border;
    protected GLPixelBuffer.GLPixelAttributes pixelAttributes;
    protected int internalFormat; // perhaps inferred from pixelFormat?
    protected boolean mipmap; // indicates whether mipmaps should be generated
    // (ignored if mipmaps are supplied from the file)
    private boolean dataIsCompressed;
    protected boolean mustFlipVertically; // Must flip texture coordinates
    // vertically to get OpenGL output
    // to look correct
    protected ByteBuffer buffer; // the actual data...
    private ByteBuffer[] mipmapData; // ...or a series of mipmaps
    private Flusher flusher;
    protected int rowLength;
    protected int alignment; // 1, 2, or 4 bytes
    protected int estimatedMemorySize;

    // These booleans are a concession to the AWTTextureData subclass
    protected boolean haveEXTABGR;
    protected boolean haveGL12;

    // TODO: final, and set via ctor for 2.4.X
    /* pp */ ImageType srcImageType;

    /**
     * Constructs a new TextureData object with the specified parameters and data
     * contained in the given Buffer. The optional Flusher can be used to clean up
     * native resources associated with this TextureData when processing is
     * complete; for example, closing of memory-mapped files that might otherwise
     * require a garbage collection to reclaim and close.
     *
     * @param internalFormat
     *            the OpenGL internal format for the resulting texture; must be
     *            specified, may not be 0
     * @param width
     *            the width in pixels of the texture
     * @param height
     *            the height in pixels of the texture
     * @param border
     *            the number of pixels of border this texture data has (0 or 1)
     * @param pixelFormat
     *            the OpenGL pixel format for the resulting texture; must be
     *            specified, may not be 0
     * @param pixelType
     *            the OpenGL type of the pixels of the texture
     * @param mipmap
     *            indicates whether mipmaps should be autogenerated (using GLU) for
     *            the resulting texture. Currently if mipmap is true then
     *            dataIsCompressed may not be true.
     * @param dataIsCompressed
     *            indicates whether the texture data is in compressed form (e.g.
     *            GL_COMPRESSED_RGB_S3TC_DXT1_EXT)
     * @param mustFlipVertically
     *            indicates whether the texture coordinates must be flipped
     *            vertically in order to properly display the texture
     * @param buffer
     *            the buffer containing the texture data
     * @param flusher
     *            optional flusher to perform cleanup tasks upon call to flush()
     *
     * @throws IllegalArgumentException
     *             if any parameters of the texture data were invalid, such as
     *             requesting mipmap generation for a compressed texture
     */
    public TextureData(final int internalFormat, final int width, final int height, final int border, final int pixelFormat,
            final int pixelType, final boolean mipmap, final boolean dataIsCompressed, final boolean mustFlipVertically,
            final ByteBuffer buffer, final Flusher flusher) throws IllegalArgumentException {
        this(internalFormat, width, height, border, new GLPixelBuffer.GLPixelAttributes(pixelFormat, pixelType), mipmap,
                dataIsCompressed, mustFlipVertically, buffer, flusher);
    }

    /**
     * Constructs a new TextureData object with the specified parameters and data
     * contained in the given Buffer. The optional Flusher can be used to clean up
     * native resources associated with this TextureData when processing is
     * complete; for example, closing of memory-mapped files that might otherwise
     * require a garbage collection to reclaim and close.
     *
     * @param internalFormat
     *            the OpenGL internal format for the resulting texture; must be
     *            specified, may not be 0
     * @param width
     *            the width in pixels of the texture
     * @param height
     *            the height in pixels of the texture
     * @param border
     *            the number of pixels of border this texture data has (0 or 1)
     * @param pixelAttributes
     *            the OpenGL pixel format and type for the resulting texture; must
     *            be specified, may not be 0
     * @param mipmap
     *            indicates whether mipmaps should be autogenerated (using GLU) for
     *            the resulting texture. Currently if mipmap is true then
     *            dataIsCompressed may not be true.
     * @param dataIsCompressed
     *            indicates whether the texture data is in compressed form (e.g.
     *            GL_COMPRESSED_RGB_S3TC_DXT1_EXT)
     * @param mustFlipVertically
     *            indicates whether the texture coordinates must be flipped
     *            vertically in order to properly display the texture
     * @param buffer
     *            the buffer containing the texture data
     * @param flusher
     *            optional flusher to perform cleanup tasks upon call to flush()
     *
     * @throws IllegalArgumentException
     *             if any parameters of the texture data were invalid, such as
     *             requesting mipmap generation for a compressed texture
     */
    public TextureData(final int internalFormat, final int width, final int height, final int border,
            final GLPixelBuffer.GLPixelAttributes pixelAttributes, final boolean mipmap, final boolean dataIsCompressed,
            final boolean mustFlipVertically, final ByteBuffer buffer, final Flusher flusher) throws IllegalArgumentException {
        if (mipmap && dataIsCompressed) {
            throw new IllegalArgumentException("Can not generate mipmaps for compressed textures");
        }
        this.width = width;
        this.height = height;
        this.border = border;
        this.pixelAttributes = pixelAttributes;
        this.internalFormat = internalFormat;
        this.mipmap = mipmap;
        this.dataIsCompressed = dataIsCompressed;
        this.mustFlipVertically = mustFlipVertically;
        this.buffer = buffer;
        this.flusher = flusher;
        alignment = 1; // FIXME: is this correct enough in all situations?
        estimatedMemorySize = estimatedMemorySize(buffer);
    }

    /**
     * Constructs a new TextureData object with the specified parameters and data
     * for multiple mipmap levels contained in the given array of Buffers. The
     * optional Flusher can be used to clean up native resources associated with
     * this TextureData when processing is complete; for example, closing of
     * memory-mapped files that might otherwise require a garbage collection to
     * reclaim and close.
     *
     * @param internalFormat
     *            the OpenGL internal format for the resulting texture; must be
     *            specified, may not be 0
     * @param width
     *            the width in pixels of the topmost mipmap level of the texture
     * @param height
     *            the height in pixels of the topmost mipmap level of the texture
     * @param border
     *            the number of pixels of border this texture data has (0 or 1)
     * @param pixelFormat
     *            the OpenGL pixel format for the resulting texture; must be
     *            specified, may not be 0
     * @param pixelType
     *            the OpenGL type of the pixels of the texture
     * @param dataIsCompressed
     *            indicates whether the texture data is in compressed form (e.g.
     *            GL_COMPRESSED_RGB_S3TC_DXT1_EXT)
     * @param mustFlipVertically
     *            indicates whether the texture coordinates must be flipped
     *            vertically in order to properly display the texture
     * @param mipmapData
     *            the buffers containing all mipmap levels of the texture's data
     * @param flusher
     *            optional flusher to perform cleanup tasks upon call to flush()
     *
     * @throws IllegalArgumentException
     *             if any parameters of the texture data were invalid, such as
     *             requesting mipmap generation for a compressed texture
     */
    public TextureData(final int internalFormat, final int width, final int height, final int border, final int pixelFormat,
            final int pixelType, final boolean dataIsCompressed, final boolean mustFlipVertically, final ByteBuffer[] mipmapData,
            final Flusher flusher) throws IllegalArgumentException {
        this(internalFormat, width, height, border, new GLPixelBuffer.GLPixelAttributes(pixelFormat, pixelType), dataIsCompressed,
                mustFlipVertically, mipmapData, flusher);
    }

    /**
     * Constructs a new TextureData object with the specified parameters and data
     * for multiple mipmap levels contained in the given array of Buffers. The
     * optional Flusher can be used to clean up native resources associated with
     * this TextureData when processing is complete; for example, closing of
     * memory-mapped files that might otherwise require a garbage collection to
     * reclaim and close.
     *
     * @param internalFormat
     *            the OpenGL internal format for the resulting texture; must be
     *            specified, may not be 0
     * @param width
     *            the width in pixels of the topmost mipmap level of the texture
     * @param height
     *            the height in pixels of the topmost mipmap level of the texture
     * @param border
     *            the number of pixels of border this texture data has (0 or 1)
     * @param pixelAttributes
     *            the OpenGL pixel format and type for the resulting texture; must
     *            be specified, may not be 0
     * @param dataIsCompressed
     *            indicates whether the texture data is in compressed form (e.g.
     *            GL_COMPRESSED_RGB_S3TC_DXT1_EXT)
     * @param mustFlipVertically
     *            indicates whether the texture coordinates must be flipped
     *            vertically in order to properly display the texture
     * @param mipmapData
     *            the buffers containing all mipmap levels of the texture's data
     * @param flusher
     *            optional flusher to perform cleanup tasks upon call to flush()
     *
     * @throws IllegalArgumentException
     *             if any parameters of the texture data were invalid, such as
     *             requesting mipmap generation for a compressed texture
     */
    public TextureData(final int internalFormat, final int width, final int height, final int border,
            final GLPixelBuffer.GLPixelAttributes pixelAttributes, final boolean dataIsCompressed,
            final boolean mustFlipVertically, final ByteBuffer[] mipmapData, final Flusher flusher)
            throws IllegalArgumentException {
        this.width = width;
        this.height = height;
        this.border = border;
        this.pixelAttributes = pixelAttributes;
        this.internalFormat = internalFormat;
        this.dataIsCompressed = dataIsCompressed;
        this.mustFlipVertically = mustFlipVertically;
        this.mipmapData = mipmapData.clone();
        this.flusher = flusher;
        alignment = 1; // FIXME: is this correct enough in all situations?
        for (int i = 0; i < mipmapData.length; i++) {
            estimatedMemorySize += estimatedMemorySize(mipmapData[i]);
        }
    }

    /** Used only by subclasses */
    protected TextureData() {
        this.pixelAttributes = GLPixelBuffer.GLPixelAttributes.UNDEF;
    }

    /** Returns the width in pixels of the texture data. */
    public int getWidth() {
        return width;
    }

    /** Returns the height in pixels of the texture data. */
    public int getHeight() {
        return height;
    }

    /** Returns the border in pixels of the texture data. */
    public int getBorder() {
        return border;
    }

    /**
     * Returns the intended OpenGL pixel format of the texture data using
     * {@link #getPixelAttributes()}.
     */
    public int getPixelFormat() {
        return pixelAttributes.format;
    }

    /**
     * Returns the intended OpenGL pixel type of the texture data using
     * {@link #getPixelAttributes()}.
     */
    public int getPixelType() {
        return pixelAttributes.type;
    }

    /** Returns the intended OpenGL internal format of the texture data. */
    public int getInternalFormat() {
        return internalFormat;
    }

    /** Returns whether mipmaps should be generated for the texture data. */
    public boolean getMipmap() {
        return mipmap;
    }

    /** Indicates whether the texture data is in compressed form. */
    public boolean isDataCompressed() {
        return dataIsCompressed;
    }

    /**
     * Indicates whether the texture coordinates must be flipped vertically for
     * proper display.
     */
    public boolean getMustFlipVertically() {
        return mustFlipVertically;
    }

    /** Returns the texture data, or null if it is specified as a set of mipmaps. */
    public ByteBuffer getBuffer() {
        return buffer;
    }

    /**
     * Returns all mipmap levels for the texture data, or null if it is specified as
     * a single image.
     */
    public ByteBuffer[] getMipmapData() {
        return mipmapData;
    }

    /** Returns the required byte alignment for the texture data. */
    public int getAlignment() {
        return alignment;
    }

    /**
     * Returns the row length needed for correct GL_UNPACK_ROW_LENGTH specification.
     * This is currently only supported for non-mipmapped, non-compressed textures.
     */
    public int getRowLength() {
        return rowLength;
    }

    /** Sets whether mipmaps should be generated for the texture data. */
    public void setMipmap(final boolean mipmap) {
        this.mipmap = mipmap;
    }

    /**
     * Indicates to this TextureData whether the GL_EXT_abgr extension is available.
     * Used for optimization along some code paths to avoid data copies.
     */
    public void setHaveEXTABGR(final boolean haveEXTABGR) {
        this.haveEXTABGR = haveEXTABGR;
    }

    /**
     * Indicates to this TextureData whether OpenGL version 1.2 is available. If
     * not, falls back to relatively inefficient code paths for several input data
     * types (several kinds of packed pixel formats, in particular).
     */
    public void setHaveGL12(final boolean haveGL12) {
        this.haveGL12 = haveGL12;
    }

    /**
     * Returns an estimate of the amount of memory in bytes this TextureData will
     * consume once uploaded to the graphics card. It should only be treated as an
     * estimate; most applications should not need to query this but instead let the
     * OpenGL implementation page textures in and out as necessary.
     */
    public int getEstimatedMemorySize() {
        return estimatedMemorySize;
    }

    /**
     * Flushes resources associated with this TextureData by calling
     * Flusher.flush().
     */
    public void flush() {
        if (flusher != null) {
            flusher.flush();
            flusher = null;
        }
    }

    /**
     * Defines a callback mechanism to allow the user to explicitly deallocate
     * native resources (memory-mapped files, etc.) associated with a particular
     * TextureData.
     */
    public interface Flusher {
        /**
         * Flushes any native resources associated with this TextureData.
         */
        void flush();
    }

    @Override
    public String toString() {
        final String optImageType = null != srcImageType ? ", " + srcImageType : "";
        return "TextureData[" + width + "x" + height + ", y-flip " + mustFlipVertically + ", internFormat 0x"
                + Integer.toHexString(internalFormat) + ", " + pixelAttributes + ", border " + border + ", estSize "
                + estimatedMemorySize + ", alignment " + alignment + ", rowlen " + rowLength + optImageType;
    }

    // ----------------------------------------------------------------------
    // Internals only below this point
    //

    protected static int estimatedMemorySize(final Buffer buffer) {
        if (buffer == null) {
            return 0;
        }
        return buffer.capacity() * Buffers.sizeOfBufferElem(buffer);
    }
}
