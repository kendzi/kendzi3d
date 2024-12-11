/**
 * Copyright (c) 2014 JogAmp Community. All rights reserved.
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

import java.util.Arrays;

/**
 * Basic pixel formats
 * <p>
 * Notation follows OpenGL notation, i.e. name consist of all it's component
 * names followed by their bit size.
 * </p>
 * <p>
 * Order of component names is from lowest-bit to highest-bit.
 * </p>
 * <p>
 * In case component-size is 1 byte (e.g. OpenGL data-type GL_UNSIGNED_BYTE),
 * component names are ordered from lowest-byte to highest-byte. Note that
 * OpenGL applies special interpretation if data-type is e.g.
 * GL_UNSIGNED_8_8_8_8_REV or GL_UNSIGNED_8_8_8_8_REV.
 * </p>
 * <p>
 * PixelFormat can be converted to OpenGL GLPixelAttributes via
 * 
 * <pre>
 *  GLPixelAttributes glpa = GLPixelAttributes.convert(PixelFormat pixFmt, GLProfile glp);
 * </pre>
 * </p>
 * <p>
 * See OpenGL Specification 4.3 - February 14, 2013, Core Profile, Section 8.4.4
 * Transfer of Pixel Rectangles, p. 161-174.
 * </ul>
 *
 * </p>
 */
enum PixelFormat {
    /**
     * Stride is 8 bits, 8 bits per pixel, 1 component of 8 bits. Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_ALPHA (< GL3), GL_RED (>= GL3), data-type
     * GL_UNSIGNED_BYTE</li>
     * <li>AWT: <i>none</i></li>
     * </ul>
     * </p>
     */
    LUMINANCE(new CType[] { CType.Y }, 1, 8, 8),

    /**
     * Stride is 16 bits, 16 bits per pixel, 3 {@link PackedComposition#isUniform()
     * discrete} components.
     * <p>
     * The {@link PackedComposition#isUniform() discrete}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>R: 0x1F << 0</li>
     * <li>G: 0x3F << 5</li>
     * <li>B: 0x1F << 11</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_RGB, data-type GL_UNSIGNED_SHORT_5_6_5_REV</li>
     * <li>AWT: <i>None</i></li>
     * </ul>
     * </p>
     */
    RGB565(new CType[] { CType.R, CType.G, CType.B }, new int[] { 0x1F, 0x3F, 0x1F }, new int[] { 0, 5, 5 + 6 }, 16),

    /**
     * Stride is 16 bits, 16 bits per pixel, 3 {@link PackedComposition#isUniform()
     * discrete} components.
     * <p>
     * The {@link PackedComposition#isUniform() discrete}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>B: 0x1F << 0</li>
     * <li>G: 0x3F << 5</li>
     * <li>R: 0x1F << 11</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_RGB, data-type GL_UNSIGNED_SHORT_5_6_5</li>
     * <li>AWT: <i>None</i></li>
     * </ul>
     * </p>
     */
    BGR565(new CType[] { CType.B, CType.G, CType.R }, new int[] { 0x1F, 0x3F, 0x1F }, new int[] { 0, 5, 5 + 6 }, 16),

    /**
     * Stride is 16 bits, 16 bits per pixel, 4 {@link PackedComposition#isUniform()
     * discrete} components.
     * <p>
     * The {@link PackedComposition#isUniform() discrete}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>R: 0x1F << 0</li>
     * <li>G: 0x1F << 5</li>
     * <li>B: 0x1F << 10</li>
     * <li>A: 0x01 << 15</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_RGBA, data-type GL_UNSIGNED_SHORT_1_5_5_5_REV</li>
     * <li>AWT: <i>None</i></li>
     * </ul>
     * </p>
     */
    RGBA5551(new CType[] { CType.R, CType.G, CType.B, CType.A }, new int[] { 0x1F, 0x1F, 0x1F, 0x01 },
            new int[] { 0, 5, 5 + 5, 5 + 5 + 5 }, 16),

    /**
     * Stride is 16 bits, 16 bits per pixel, 4 {@link PackedComposition#isUniform()
     * discrete} components.
     * <p>
     * The {@link PackedComposition#isUniform() discrete}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>A: 0x01 << 0</li>
     * <li>B: 0x1F << 1</li>
     * <li>G: 0x1F << 6</li>
     * <li>R: 0x1F << 11</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_RGBA, data-type GL_UNSIGNED_SHORT_5_5_5_1</li>
     * <li>AWT: <i>None</i></li>
     * </ul>
     * </p>
     */
    ABGR1555(new CType[] { CType.A, CType.B, CType.G, CType.R }, new int[] { 0x01, 0x1F, 0x1F, 0x1F },
            new int[] { 0, 1, 1 + 5, 1 + 5 + 5 }, 16),

    /**
     * Stride 24 bits, 24 bits per pixel, 3 {@link PackedComposition#isUniform()
     * uniform} components of 8 bits.
     * <p>
     * The {@link PackedComposition#isUniform() uniform}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>R: 0xFF << 0</li>
     * <li>G: 0xFF << 8</li>
     * <li>B: 0xFF << 16</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_RGB, data-type GL_UNSIGNED_BYTE</li>
     * <li>AWT: <i>None</i></li>
     * </ul>
     * </p>
     */
    RGB888(new CType[] { CType.R, CType.G, CType.B }, 3, 8, 24),

    /**
     * Stride is 24 bits, 24 bits per pixel, 3 {@link PackedComposition#isUniform()
     * uniform} components of of 8 bits.
     * <p>
     * The {@link PackedComposition#isUniform() uniform}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>B: 0xFF << 0</li>
     * <li>G: 0xFF << 8</li>
     * <li>R: 0xFF << 16</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_BGR (>= GL2), data-type GL_UNSIGNED_BYTE</li>
     * <li>AWT: {@link java.awt.image.BufferedImage#TYPE_3BYTE_BGR
     * TYPE_3BYTE_BGR}</li>
     * </ul>
     * </p>
     */
    BGR888(new CType[] { CType.B, CType.G, CType.R }, 3, 8, 24),

    /**
     * Stride is 32 bits, 24 bits per pixel, 3 {@link PackedComposition#isUniform()
     * uniform} components of 8 bits.
     * <p>
     * The {@link PackedComposition#isUniform() uniform}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>R: 0xFF << 0</li>
     * <li>G: 0xFF << 8</li>
     * <li>B: 0xFF << 16</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_RGBA, data-type GL_UNSIGNED_BYTE, with alpha
     * discarded!</li>
     * <li>AWT: {@link java.awt.image.BufferedImage#TYPE_INT_BGR TYPE_INT_BGR}</li>
     * </ul>
     * </p>
     */
    RGBx8888(new CType[] { CType.R, CType.G, CType.B }, 3, 8, 32),

    /**
     * Stride is 32 bits, 24 bits per pixel, 3 {@link PackedComposition#isUniform()
     * uniform} components of 8 bits.
     * <p>
     * The {@link PackedComposition#isUniform() uniform}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>B: 0xFF << 0</li>
     * <li>G: 0xFF << 8</li>
     * <li>R: 0xFF << 16</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_BGRA, data-type GL_UNSIGNED_BYTE - with alpha
     * discarded!</li>
     * <li>AWT: {@link java.awt.image.BufferedImage#TYPE_INT_RGB TYPE_INT_RGB}</li>
     * </ul>
     * </p>
     */
    BGRx8888(new CType[] { CType.B, CType.G, CType.R }, 3, 8, 32),

    /**
     * Stride is 32 bits, 32 bits per pixel, 4 {@link PackedComposition#isUniform()
     * uniform} components of 8 bits.
     * <p>
     * The {@link PackedComposition#isUniform() uniform}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>R: 0xFF << 0</li>
     * <li>G: 0xFF << 8</li>
     * <li>B: 0xFF << 16</li>
     * <li>A: 0xFF << 24</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_RGBA, data-type GL_UNSIGNED_BYTE</li>
     * <li>AWT: <i>None</i></li>
     * <li>PointerIcon: OSX (NSBitmapImageRep)</li>
     * <li>Window Icon: OSX (NSBitmapImageRep)</li>
     * <li>PNGJ: Scanlines</li>
     * </ul>
     * </p>
     */
    RGBA8888(new CType[] { CType.R, CType.G, CType.B, CType.A }, 4, 8, 32),

    /**
     * Stride is 32 bits, 32 bits per pixel, 4 {@link PackedComposition#isUniform()
     * uniform} components of 8 bits.
     * <p>
     * The {@link PackedComposition#isUniform() uniform}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>A: 0xFF << 0</li>
     * <li>B: 0xFF << 8</li>
     * <li>G: 0xFF << 16</li>
     * <li>R: 0xFF << 24</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_RGBA, data-type GL_UNSIGNED_INT_8_8_8_8</li>
     * <li>AWT: {@link java.awt.image.BufferedImage#TYPE_4BYTE_ABGR
     * TYPE_4BYTE_ABGR}</li>
     * </ul>
     * </p>
     */
    ABGR8888(new CType[] { CType.A, CType.B, CType.G, CType.R }, 4, 8, 32),

    /**
     * Stride is 32 bits, 32 bits per pixel, 4 {@link PackedComposition#isUniform()
     * uniform} components of 8 bits.
     * <p>
     * The {@link PackedComposition#isUniform() uniform}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>A: 0xFF << 0</li>
     * <li>R: 0xFF << 8</li>
     * <li>G: 0xFF << 16</li>
     * <li>B: 0xFF << 24</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_BGRA, data-type GL_UNSIGNED_INT_8_8_8_8</li>
     * <li>AWT: <i>None</i></li>
     * </ul>
     * </p>
     */
    ARGB8888(new CType[] { CType.A, CType.R, CType.G, CType.B }, 4, 8, 32),

    /**
     * Stride is 32 bits, 32 bits per pixel, 4 {@link PackedComposition#isUniform()
     * uniform} components of 8 bits.
     * <p>
     * The {@link PackedComposition#isUniform() uniform}
     * {@link PixelFormat#composition components} are interleaved in the order Low
     * to High:
     * <ol>
     * <li>B: 0xFF << 0</li>
     * <li>G: 0xFF << 8</li>
     * <li>R: 0xFF << 16</li>
     * <li>A: 0xFF << 24</li>
     * </ol>
     * </p>
     * <p>
     * Compatible with:
     * <ul>
     * <li>OpenGL: data-format GL_BGRA, data-type GL_UNSIGNED_BYTE</li>
     * <li>AWT: {@link java.awt.image.BufferedImage#TYPE_INT_ARGB
     * TYPE_INT_ARGB}</li>
     * <li>PointerIcon: X11 (XCURSOR), Win32, AWT</li>
     * <li>Window Icon: X11, Win32</li>
     * </ul>
     * </p>
     */
    BGRA8888(new CType[] { CType.B, CType.G, CType.R, CType.A }, 4, 8, 32);

    /**
     * Unique {@link Composition Pixel Composition}, i.e. layout of its components.
     */
    public final Composition comp;

    /**
     * @param componentOrder
     *            {@link CType Component type} order of all components, see
     *            {@link Composition#componentBitMask()}.
     * @param componentCount
     *            number of components
     * @param bpc
     *            bits per component
     * @param bitStride
     *            stride bits to next pixel
     */
    PixelFormat(final CType[] componentOrder, final int componentCount, final int bpc, final int bitStride) {
        this.comp = new PackedComposition(componentOrder, componentCount, bpc, bitStride);
    }

    /**
     * @param componentOrder
     *            {@link CType Component type} order of all components, see
     *            {@link Composition#componentBitMask()}.
     * @param componentMask
     *            bit-mask of of all components, see
     *            {@link Composition##componentBitMask()}.
     * @param componentBitShift
     *            bit-shift of all components, see
     *            {@link Composition##componentBitMask()}.
     * @param bitStride
     *            stride bits to next pixel
     */
    PixelFormat(final CType[] componentOrder, final int[] componentMask, final int[] componentBitShift, final int bitStride) {
        this.comp = new PackedComposition(componentOrder, componentMask, componentBitShift, bitStride);
    }

    /** Component types */
    enum CType {
        /** Red component */
        R,
        /** Green component */
        G,
        /** Blue component */
        B,
        /** Alpha component */
        A,
        /** Luminance component, e.g. grayscale or Y of YUV */
        Y,
        /** U component of YUV */
        U,
        /** V component of YUV */
        V
    }

    /**
     * Pixel composition, i.e. layout of its components.
     */
    public interface Composition {
        /**
         * Returns the index of given {@link CType} within {@link #componentOrder()}, -1
         * if not exists.
         */
        int find(final PixelFormat.CType s);

        /**
         * Returns cached immutable hash value, see {@link Object#hashCode()}.
         */
        int hashCode();

        /**
         * Returns {@link Object#equals(Object)}
         */
        boolean equals(final Object o);

        /**
         * Returns {@link Object#toString()}.
         */
        String toString();
    }

    /**
     * Packed pixel composition, see {@link Composition}.
     * <p>
     * Components are interleaved, i.e. packed.
     * </p>
     */
    public static class PackedComposition implements Composition {
        private final CType[] compOrder;
        private final int[] compMask;
        private final int[] compBitCount;
        private final int[] compBitShift;
        private final int bitsPerPixel;
        private final int bitStride;
        private final boolean uniform;
        private final int hashCode;

        public final String toString() {
            return String.format("PackedComp[order %s, stride %d, bpp %d, uni %b, comp %d: %s]", Arrays.toString(compOrder),
                    bitStride, bitsPerPixel, uniform, compMask.length, toHexString(compBitCount, compMask, compBitShift));
        }

        /**
         * @param componentOrder
         *            {@link CType Component type} order of all components, see
         *            {@link #componentBitMask()}.
         * @param componentCount
         *            number of components
         * @param bpc
         *            bits per component
         * @param bitStride
         *            stride bits to next pixel
         */
        public PackedComposition(final CType[] componentOrder, final int componentCount, final int bpc, final int bitStride) {
            this.compOrder = componentOrder;
            this.compMask = new int[componentCount];
            this.compBitShift = new int[componentCount];
            this.compBitCount = new int[componentCount];
            final int compMask = (1 << bpc) - 1;
            for (int i = 0; i < componentCount; i++) {
                this.compMask[i] = compMask;
                this.compBitShift[i] = bpc * i;
                this.compBitCount[i] = bpc;
            }
            this.uniform = true;
            this.bitsPerPixel = bpc * componentCount;
            this.bitStride = bitStride;
            if (this.bitStride < this.bitsPerPixel) {
                throw new IllegalArgumentException(
                        String.format("bit-stride %d < bitsPerPixel %d", this.bitStride, this.bitsPerPixel));
            }
            this.hashCode = hashCodeImpl();
        }

        /**
         * @param componentOrder
         *            {@link CType Component type} order of all components, see
         *            {@link #componentBitMask()}.
         * @param componentMask
         *            bit-mask of of all components, see {@link #componentBitMask()}.
         * @param componentBitShift
         *            bit-shift of all components, see {@link #componentBitMask()}.
         * @param bitStride
         *            stride bits to next pixel
         */
        public PackedComposition(final CType[] componentOrder, final int[] componentMask, final int[] componentBitShift,
                final int bitStride) {
            this.compOrder = componentOrder;
            this.compMask = componentMask;
            this.compBitShift = componentBitShift;
            this.compBitCount = new int[componentMask.length];
            int bpp = 0;
            boolean uniform = true;
            for (int i = componentMask.length - 1; i >= 0; i--) {
                final int cmask = componentMask[i];
                final int bitCount = bitCount(cmask);
                bpp += bitCount;
                this.compBitCount[i] = bitCount;
                if (i > 0 && uniform) {
                    uniform = componentMask[i - 1] == cmask;
                }
            }
            this.uniform = uniform;
            this.bitsPerPixel = bpp;
            this.bitStride = bitStride;
            if (this.bitStride < this.bitsPerPixel) {
                throw new IllegalArgumentException(
                        String.format("bit-stride %d < bitsPerPixel %d", this.bitStride, this.bitsPerPixel));
            }
            this.hashCode = hashCodeImpl();
        }

        private static int bitCount(int n) {
            // Note: Original used 'unsigned int',
            // hence we use the unsigned right-shift '>>>'
            /**
             * Original does not work due to lack of 'unsigned' right-shift and modulo, we
             * need 2-complementary solution, i.e. 'signed'. int c = n; c -= (n >>> 1) &
             * 033333333333; c -= (n >>> 2) & 011111111111; return ( (c + ( c >>> 3 ) ) &
             * 030707070707 ) & 0x3f; // % 63
             */
            // Hackers Delight, Figure 5-2, pop1 of pop.c.txt
            n = n - ((n >>> 1) & 0x55555555);
            n = (n & 0x33333333) + ((n >>> 2) & 0x33333333);
            n = (n + (n >>> 4)) & 0x0f0f0f0f;
            n = n + (n >>> 8);
            n = n + (n >>> 16);
            return n & 0x3f;
        }

        public final int find(final PixelFormat.CType s) {
            return find(s, compOrder, false /* mapRGB2Y */);
        }

        private static int find(final PixelFormat.CType s, final PixelFormat.CType[] pool, final boolean mapRGB2Y) {
            int i = pool.length - 1;
            while (i >= 0 && pool[i] != s) {
                i--;
            }

            if (0 > i && mapRGB2Y && 1 == pool.length && pool[0] == PixelFormat.CType.Y
                    && (PixelFormat.CType.R == s || PixelFormat.CType.G == s || PixelFormat.CType.B == s)) {
                // Special case, fallback for RGB mapping -> LUMINANCE/Y
                return 0;
            } else {
                return i;
            }
        }

        @Override
        public final int hashCode() {
            return hashCode;
        }

        private final int hashCodeImpl() {
            // 31 * x == (x << 5) - x
            int hash = 31 + bitStride;
            hash = ((hash << 5) - hash) + bitsPerPixel;
            hash = ((hash << 5) - hash) + compMask.length;
            for (int i = compOrder.length - 1; i >= 0; i--) {
                hash = ((hash << 5) - hash) + compOrder[i].ordinal();
            }
            for (int i = compMask.length - 1; i >= 0; i--) {
                hash = ((hash << 5) - hash) + compMask[i];
            }
            for (int i = compBitShift.length - 1; i >= 0; i--) {
                hash = ((hash << 5) - hash) + compBitShift[i];
            }
            return hash;
        }

        @Override
        public final boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof PackedComposition) {
                final PackedComposition other = (PackedComposition) obj;
                return bitStride == other.bitStride && bitsPerPixel == other.bitsPerPixel
                        && Arrays.equals(compOrder, other.compOrder) && Arrays.equals(compMask, other.compMask)
                        && Arrays.equals(compBitShift, other.compBitShift);
            } else {
                return false;
            }
        }
    }

    private static String toHexString(final int[] bitCount, final int[] mask, final int[] shift) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        final int l = mask.length;
        for (int i = 0; i < l; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(bitCount[i]).append(": ").append("0x").append(Integer.toHexString(mask[i])).append(" << ").append(shift[i]);
        }
        return sb.append("]").toString();
    }
}
