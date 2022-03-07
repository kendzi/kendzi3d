/**
 * Copyright 2013 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */
package kendzi.jogl.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import kendzi.jogl.glu.GLException;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengles.GLES;

/**
 * OpenGL pixel data buffer, allowing user to provide buffers via their
 * {@link GLPixelBuffer.GLPixelBufferProvider} implementation.
 * <p>
 * {@link GLPixelBuffer.GLPixelBufferProvider} produces a {@link GLPixelBuffer}.
 * </p>
 * <p>
 * You may use {@link #defaultProviderNoRowStride}.
 * </p>
 */
@Deprecated
public class GLPixelBuffer {

    /** Pixel attributes. */
    public static class GLPixelAttributes {
        /**
         * Undefined instance of {@link GLPixelBuffer.GLPixelAttributes}, having
         * componentCount:=0, format:=0 and type:= 0.
         */
        public static final GLPixelBuffer.GLPixelAttributes UNDEF = new GLPixelBuffer.GLPixelAttributes(PixelFormat.LUMINANCE, 0,
                0, true, false);

        /**
         * Returns the matching {@link PixelFormat} for the given GL format and type if
         * exists, otherwise returns <code>null</code>.
         */
        public static final PixelFormat getPixelFormat(final int glFormat, final int glDataType) {
            PixelFormat pixFmt = null;

            switch (glFormat) {
            case GL11.GL_ALPHA:
            case GL11.GL_LUMINANCE:
            case GL12.GL_RED:
                pixFmt = PixelFormat.LUMINANCE;
                break;
            case GL11.GL_RGB:
                switch (glDataType) {
                case GL41.GL_UNSIGNED_SHORT_5_6_5_REV:
                    pixFmt = PixelFormat.RGB565;
                    break;
                case GL41.GL_UNSIGNED_SHORT_5_6_5:
                    pixFmt = PixelFormat.BGR565;
                    break;
                case GL11.GL_UNSIGNED_BYTE:
                    pixFmt = PixelFormat.RGB888;
                    break;
                }
                break;
            case GL11.GL_RGBA:
                switch (glDataType) {
                case GL41.GL_UNSIGNED_SHORT_1_5_5_5_REV:
                    pixFmt = PixelFormat.RGBA5551;
                    break;
                case GL41.GL_UNSIGNED_SHORT_5_5_5_1:
                    pixFmt = PixelFormat.ABGR1555;
                    break;
                case GL41.GL_UNSIGNED_INT_8_8_8_8_REV:
                    // fall through intended
                case GL11.GL_UNSIGNED_BYTE:
                    pixFmt = PixelFormat.RGBA8888;
                    break;
                case GL41.GL_UNSIGNED_INT_8_8_8_8:
                    pixFmt = PixelFormat.ABGR8888;
                    break;
                }
                break;
            case GL41.GL_BGR:
                if (GL11.GL_UNSIGNED_BYTE == glDataType) {
                    pixFmt = PixelFormat.BGR888;
                }
                break;
            case GL41.GL_BGRA:
                switch (glDataType) {
                case GL41.GL_UNSIGNED_INT_8_8_8_8:
                    pixFmt = PixelFormat.ARGB8888;
                    break;
                case GL41.GL_UNSIGNED_INT_8_8_8_8_REV:
                    // fall through intended
                case GL11.GL_UNSIGNED_BYTE:
                    pixFmt = PixelFormat.BGRA8888;
                    break;
                }
                break;
            }
            return pixFmt;
        }

        private static final int convert(final PixelFormat pixFmt, final boolean pack, final int[] dfRes, final int[] dtRes) {
            final GLCapabilities cap = GL.getCapabilities();
            final boolean glesReadMode = pack && GLES.getCapabilities().GLES20;
            int df = 0; // format
            int dt = GL11.GL_UNSIGNED_BYTE; // data type
            switch (pixFmt) {
            case LUMINANCE:
                if (!glesReadMode) {
                    if (cap.OpenGL30) {
                        // RED is supported on ES3 and >= GL3 [core]; ALPHA/LUMINANCE is deprecated on
                        // core
                        df = GL20.GL_RED;
                    } else {
                        // ALPHA/LUMINANCE is supported on ES2 and GL2, i.e. <= GL3 [core] or
                        // compatibility
                        df = GL11.GL_LUMINANCE;
                    }
                }
                break;
            case RGB565:
                if (cap.OpenGL20) {
                    df = GL11.GL_RGB;
                    dt = GL12.GL_UNSIGNED_SHORT_5_6_5_REV;
                }
                break;
            case BGR565:
                if (cap.OpenGL20) {
                    df = GL11.GL_RGB;
                    dt = GL12.GL_UNSIGNED_SHORT_5_6_5;
                }
                break;
            case RGBA5551:
                if (cap.OpenGL20) {
                    df = GL11.GL_RGBA;
                    dt = GL12.GL_UNSIGNED_SHORT_1_5_5_5_REV;
                }
                break;
            case ABGR1555:
                if (cap.OpenGL20) {
                    df = GL11.GL_RGBA;
                    dt = GL12.GL_UNSIGNED_SHORT_5_5_5_1;
                }
                break;
            case RGB888:
                if (!glesReadMode) {
                    df = GL11.GL_RGB;
                }
                break;
            case BGR888:
                if (cap.OpenGL20) {
                    df = GL13.GL_BGR;
                }
                break;
            case RGBx8888:
            case RGBA8888:
                df = GL13.GL_RGBA;
                break;
            case ABGR8888:
                if (cap.OpenGL20) {
                    df = GL11.GL_RGBA;
                    dt = GL13.GL_UNSIGNED_INT_8_8_8_8;
                }
                break;
            case ARGB8888:
                if (cap.OpenGL20) {
                    df = GL13.GL_BGRA;
                    dt = GL13.GL_UNSIGNED_INT_8_8_8_8;
                }
                break;
            case BGRx8888:
            case BGRA8888:
                if (cap.OpenGL20) { // FIXME: or if( !glesReadMode ) ? BGRA n/a on GLES
                    df = GL13.GL_BGRA;
                }
                break;
            }
            dfRes[0] = df;
            dtRes[0] = dt;
            return df;
        }

        /** The OpenGL pixel data format */
        public final int format;
        /** The OpenGL pixel data type */
        public final int type;

        /**
         * {@link PixelFormat} describing the {@link PixelFormat.Composition component}
         * layout
         */
        public final PixelFormat pfmt;

        @Override
        public final int hashCode() {
            // 31 * x == (x << 5) - x
            int hash = pfmt.hashCode();
            hash = ((hash << 5) - hash) + format;
            return ((hash << 5) - hash) + type;
        }

        @Override
        public final boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof GLPixelBuffer.GLPixelAttributes) {
                final GLPixelBuffer.GLPixelAttributes other = (GLPixelBuffer.GLPixelAttributes) obj;
                return format == other.format && type == other.type && pfmt.equals(other.pfmt);
            } else {
                return false;
            }
        }

        /**
         * Create a new {@link GLPixelBuffer.GLPixelAttributes} instance based on GL
         * format and type.
         * 
         * @param dataFormat
         *            GL data format
         * @param dataType
         *            GL data type
         * @throws GLException
         *             if {@link PixelFormat} could not be determined, see
         *             {@link #getPixelFormat(int, int)}.
         */
        public GLPixelAttributes(final int dataFormat, final int dataType) throws GLException {
            this(null, dataFormat, dataType, true /* not used */, true);
        }

        /**
         * Create a new {@link GLPixelBuffer.GLPixelAttributes} instance based on
         * {@link GLProfile}, {@link PixelFormat} and {@code pack}.
         * 
         * @param pixFmt
         *            the to be matched {@link PixelFormat pixel format}
         * @param pack
         *            {@code true} for read mode GPU -> CPU, e.g.
         *            {@link GL#glReadPixels(int, int, int, int, int, int, Buffer)
         *            glReadPixels}. {@code false} for write mode CPU -> GPU, e.g.
         *            {@link GL#glTexImage2D(int, int, int, int, int, int, int, int, Buffer)
         *            glTexImage2D}.
         * @throws GLException
         *             if GL format or type could not be determined, see
         *             {@link #convert(PixelFormat, boolean)}.
         */
        public GLPixelAttributes(final PixelFormat pixFmt, final boolean pack) throws GLException {
            this(pixFmt, 0, 0, pack, true);
        }

        private GLPixelAttributes(final PixelFormat pixFmt, final int dataFormat, final int dataType, final boolean pack,
                final boolean checkArgs) throws GLException {
            if (checkArgs && (0 == dataFormat || 0 == dataType)) {
                if (null == pixFmt) {
                    throw new GLException("Zero format and/or type w/o pixFmt or glp: " + this);
                }
                final int[] df = new int[1];
                final int[] dt = new int[1];
                if (0 == convert(pixFmt, pack, df, dt)) {
                    throw new GLException(
                            "Could not find format and type for " + pixFmt + " and " + GL.getCapabilities() + ", " + this);
                }
                this.format = df[0];
                this.type = dt[0];
                this.pfmt = pixFmt;
            } else {
                this.format = dataFormat;
                this.type = dataType;
                this.pfmt = null != pixFmt ? pixFmt : getPixelFormat(dataFormat, dataType);
                if (null == this.pfmt) {
                    throw new GLException("Could not find PixelFormat for format and/or type: " + this);
                }
            }
            if (checkArgs) {
                final int bytesPerPixel = GLBuffers.bytesPerPixel(this.format, this.type);
                if (0 == bytesPerPixel) {
                    throw new GLException("Zero bytesPerPixel: " + this);
                }
            }
        }

        @Override
        public String toString() {
            return "PixelAttributes[fmt 0x" + Integer.toHexString(format) + ", type 0x" + Integer.toHexString(type) + ", " + pfmt
                    + "]";
        }
    }

    /** The {@link GLPixelBuffer.GLPixelAttributes}. */
    public final GLPixelBuffer.GLPixelAttributes pixelAttributes;
    /**
     * Width in pixels, representing {@link #buffer}'s {@link #byteSize}.
     * <p>
     * May not represent actual image width as user may re-use buffer for different
     * dimensions, see {@link #requiresNewBuffer(int, int, int)}.
     * </p>
     */
    public final int width;
    /**
     * Height in pixels, representing {@link #buffer}'s {@link #byteSize}.
     * <p>
     * May not represent actual image height as user may re-use buffer for different
     * dimensions, see {@link #requiresNewBuffer(int, int, int)}.
     * </p>
     */
    public final int height;
    /** Depth in pixels. */
    public final int depth;
    /**
     * Data packing direction.
     * <p>
     * {@code true} for read mode GPU -> CPU, e.g.
     * {@link GL#glReadPixels(int, int, int, int, int, int, Buffer) glReadPixels}.
     * </p>
     * <p>
     * {@code false} for write mode CPU -> GPU, e.g.
     * {@link GL#glTexImage2D(int, int, int, int, int, int, int, int, Buffer)
     * glTexImage2D}.
     * </p>
     */
    public final boolean pack;
    /**
     * Byte size of the buffer. Actually the number of {@link Buffer#remaining()}
     * bytes when passed in ctor.
     */
    public final int byteSize;
    /**
     * Buffer holding the pixel data. If {@link #rewind()}, it holds
     * <code>byteSize</code> {@link Buffer#remaining()} bytes.
     * <p>
     * By default the {@link Buffer} is a {@link ByteBuffer}, due to
     * {@link DefProvider#allocate(GL, PixelFormat.Composition, GLPixelBuffer.GLPixelAttributes, boolean, int, int, int, int)}.
     * However, other {@link GLPixelBuffer.GLPixelBufferProvider} may utilize
     * different {@link Buffer} types.
     * </p>
     */
    public final Buffer buffer;
    /** Buffer element size in bytes. */
    public final int bufferElemSize;

    private final boolean disposed = false;

    public StringBuilder toString(StringBuilder sb) {
        if (null == sb) {
            sb = new StringBuilder();
        }
        sb.append(pixelAttributes).append(", dim ").append(width).append("x").append(height).append("x").append(depth)
                .append(", pack ").append(pack).append(", disposed ").append(disposed).append(", valid ").append(isValid())
                .append(", buffer[bytes ").append(byteSize).append(", elemSize ").append(bufferElemSize).append(", ")
                .append(buffer).append("]");
        return sb;
    }

    @Override
    public String toString() {
        return "GLPixelBuffer[" + toString(null).toString() + "]";
    }

    /**
     * @param pixelAttributes
     *            the desired {@link GLPixelBuffer.GLPixelAttributes}
     * @param pack
     *            {@code true} for read mode GPU -> CPU, e.g.
     *            {@link GL#glReadPixels(int, int, int, int, int, int, Buffer)
     *            glReadPixels}. {@code false} for write mode CPU -> GPU, e.g.
     *            {@link GL#glTexImage2D(int, int, int, int, int, int, int, int, Buffer)
     *            glTexImage2D}.
     * @param width
     *            in pixels
     * @param height
     *            in pixels
     * @param depth
     *            in pixels
     * @param buffer
     *            the backing array
     * @param allowRowStride
     *            If <code>true</code>, allow row-stride, otherwise not. See
     *            {@link #requiresNewBuffer(int, int, int)}.
     * @param hostPixelComp
     *            the host {@link PixelFormat.Composition}
     */
    public GLPixelBuffer(final GLPixelBuffer.GLPixelAttributes pixelAttributes, final boolean pack, final int width,
            final int height, final int depth, final Buffer buffer, final boolean allowRowStride) {
        this.pixelAttributes = pixelAttributes;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.pack = pack;
        this.buffer = buffer;
        this.bufferElemSize = Buffers.sizeOfBufferElem(buffer);
        this.byteSize = buffer.remaining() * this.bufferElemSize;
    }

    /** Is not {@link #dispose() disposed} and has {@link #byteSize} &gt; 0. */
    public boolean isValid() {
        return !disposed && 0 < byteSize;
    }

}