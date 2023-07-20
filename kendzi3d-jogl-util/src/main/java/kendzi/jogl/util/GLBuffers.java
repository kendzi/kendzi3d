/*
 * Copyright (c) 2008 Sun Microsystems, Inc. All Rights Reserved.
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
package kendzi.jogl.util;

import kendzi.jogl.glu.GLException;
import org.lwjgl.opengl.EXTABGR;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.NVTextureShader;
import org.lwjgl.opengles.APPLERGB422;
import org.lwjgl.opengles.OESTextureHalfFloat;

/**
 * Utility routines for dealing with direct buffers.
 *
 * @author Kenneth Russel, et.al.
 */
@Deprecated
public class GLBuffers extends Buffers {

    private static final int glGetInteger(final int pname, final int[] tmp) {
        GL11.glGetIntegerv(pname, tmp);
        return tmp[0];
    }

    /**
     * Returns the number of bytes required for one pixel with the the given OpenGL
     * format and type.
     *
     * <p>
     * This method is security critical, hence it throws an exception (fail-fast) in
     * case either the format, type or alignment is unhandled. In case we forgot to
     * handle proper values, please contact the maintainer.
     * </p>
     *
     * <p>
     * See {@link #componentCount(int)}.
     * </p>
     *
     * @param format
     *            must be one of (27) <br/>
     *            GL_COLOR_INDEX GL_STENCIL_INDEX <br/>
     *            GL_DEPTH_COMPONENT GL_DEPTH_STENCIL <br/>
     *            GL_RED GL_RED_INTEGER <br/>
     *            GL_GREEN GL_GREEN_INTEGER <br/>
     *            GL_BLUE GL_BLUE_INTEGER <br/>
     *            GL_ALPHA GL_LUMINANCE (12) <br/>
     *            <br/>
     *            GL_LUMINANCE_ALPHA GL_RG <br/>
     *            GL_RG_INTEGER GL_HILO_NV <br/>
     *            GL_SIGNED_HILO_NV (5) <br/>
     *            <br/>
     *            GL_YCBCR_422_APPLE <br/>
     *            <br/>
     *            GL_RGB GL_RGB_INTEGER <br/>
     *            GL_BGR GL_BGR_INTEGER (4)<br/>
     *            <br/>
     *            GL_RGBA GL_RGBA_INTEGER <br/>
     *            GL_BGRA GL_BGRA_INTEGER <br/>
     *            GL_ABGR_EXT (5)<br/>
     *
     * @param type
     *            must be one of (32) <br/>
     *            GL_BITMAP, <br/>
     *            GL_BYTE, GL_UNSIGNED_BYTE, <br/>
     *            GL_UNSIGNED_BYTE_3_3_2, GL_UNSIGNED_BYTE_2_3_3_REV, <br/>
     *            <br/>
     *            GL_SHORT, GL_UNSIGNED_SHORT, <br/>
     *            GL_UNSIGNED_SHORT_5_6_5, GL_UNSIGNED_SHORT_5_6_5_REV, <br/>
     *            GL_UNSIGNED_SHORT_4_4_4_4, GL_UNSIGNED_SHORT_4_4_4_4_REV, <br/>
     *            GL_UNSIGNED_SHORT_5_5_5_1, GL_UNSIGNED_SHORT_1_5_5_5_REV, <br/>
     *            GL_UNSIGNED_SHORT_8_8_APPLE, GL_UNSIGNED_SHORT_8_8_REV_APPLE,
     *            <br/>
     *            GL_HALF_FLOAT, GL_HALF_FLOAT_OES <br/>
     *            <br/>
     *            GL_FIXED, GL_INT <br/>
     *            GL_UNSIGNED_INT, GL_UNSIGNED_INT_8_8_8_8, <br/>
     *            GL_UNSIGNED_INT_8_8_8_8_REV, GL_UNSIGNED_INT_10_10_10_2, <br/>
     *            GL_UNSIGNED_INT_2_10_10_10_REV, GL_UNSIGNED_INT_24_8, <br/>
     *            GL_UNSIGNED_INT_10F_11F_11F_REV, GL_UNSIGNED_INT_5_9_9_9_REV <br/>
     *            GL_HILO16_NV, GL_SIGNED_HILO16_NV <br/>
     *            <br/>
     *            GL_FLOAT_32_UNSIGNED_INT_24_8_REV <br/>
     *            <br/>
     *            GL_FLOAT, GL_DOUBLE <br/>
     *
     * @return required size of one pixel in bytes
     * @throws GLException
     *             if format or type alignment is not handled. Please contact the
     *             maintainer if this is our bug.
     */
    public static final int bytesPerPixel(final int format, final int type) throws GLException {
        int compSize = 0;

        int compCount = componentCount(format);

        switch (type) /* 30 */ {
        case GL11.GL_BITMAP:
            if (GL11.GL_COLOR_INDEX == format || GL11.GL_STENCIL_INDEX == format) {
                compSize = 1;
            } else {
                throw new GLException("BITMAP type only supported for format COLOR_INDEX and STENCIL_INDEX, not 0x"
                        + Integer.toHexString(format));
            }
            break;
        case GL11.GL_BYTE:
        case GL11.GL_UNSIGNED_BYTE:
            compSize = 1;
            break;
        case GL11.GL_SHORT:
        case GL11.GL_UNSIGNED_SHORT:
        case GL41.GL_HALF_FLOAT:
        case OESTextureHalfFloat.GL_HALF_FLOAT_OES:
            compSize = 2;
            break;
        case GL41.GL_FIXED:
        case GL11.GL_INT:
        case GL11.GL_UNSIGNED_INT:
        case GL11.GL_FLOAT:
            compSize = 4;
            break;
        case GL11.GL_DOUBLE:
            compSize = 8;
            break;

        case GL41.GL_UNSIGNED_BYTE_3_3_2:
        case GL41.GL_UNSIGNED_BYTE_2_3_3_REV:
            compSize = 1;
            compCount = 1;
            break;
        case GL41.GL_UNSIGNED_SHORT_5_6_5:
        case GL41.GL_UNSIGNED_SHORT_5_6_5_REV:
        case GL41.GL_UNSIGNED_SHORT_4_4_4_4:
        case GL41.GL_UNSIGNED_SHORT_4_4_4_4_REV:
        case GL41.GL_UNSIGNED_SHORT_5_5_5_1:
        case GL41.GL_UNSIGNED_SHORT_1_5_5_5_REV:
        case APPLERGB422.GL_UNSIGNED_SHORT_8_8_APPLE:
        case APPLERGB422.GL_UNSIGNED_SHORT_8_8_REV_APPLE:
            compSize = 2;
            compCount = 1;
            break;
        case NVTextureShader.GL_HILO16_NV:
        case NVTextureShader.GL_SIGNED_HILO16_NV:
            compSize = 2;
            compCount = 2;
            break;
        case GL41.GL_UNSIGNED_INT_8_8_8_8:
        case GL41.GL_UNSIGNED_INT_8_8_8_8_REV:
        case GL41.GL_UNSIGNED_INT_10_10_10_2:
        case GL41.GL_UNSIGNED_INT_2_10_10_10_REV:
        case GL41.GL_UNSIGNED_INT_24_8:
        case GL41.GL_UNSIGNED_INT_10F_11F_11F_REV:
        case GL41.GL_UNSIGNED_INT_5_9_9_9_REV:
            compSize = 4;
            compCount = 1;
            break;
        case GL41.GL_FLOAT_32_UNSIGNED_INT_24_8_REV:
            compSize = 8;
            compCount = 1;
            break;

        default:
            throw new GLException("type 0x" + Integer.toHexString(type) + "/" + "format 0x" + Integer.toHexString(format)
                    + " not supported [yet], pls notify the maintainer in case this is our bug.");
        }
        return compCount * compSize;
    }

    /**
     * Returns the number of components required for the given OpenGL format.
     *
     * <p>
     * This method is security critical, hence it throws an exception (fail-fast) in
     * case either the format, type or alignment is unhandled. In case we forgot to
     * handle proper values, please contact the maintainer.
     * </p>
     *
     * @param format
     *            must be one of (27) <br/>
     *            GL_COLOR_INDEX GL_STENCIL_INDEX <br/>
     *            GL_DEPTH_COMPONENT GL_DEPTH_STENCIL <br/>
     *            GL_RED GL_RED_INTEGER <br/>
     *            GL_GREEN GL_GREEN_INTEGER <br/>
     *            GL_BLUE GL_BLUE_INTEGER <br/>
     *            GL_ALPHA GL_LUMINANCE (12) <br/>
     *            <br/>
     *            GL_LUMINANCE_ALPHA GL_RG <br/>
     *            GL_RG_INTEGER GL_HILO_NV <br/>
     *            GL_SIGNED_HILO_NV (5) <br/>
     *            <br/>
     *            GL_YCBCR_422_APPLE <br/>
     *            <br/>
     *            GL_RGB GL_RGB_INTEGER <br/>
     *            GL_BGR GL_BGR_INTEGER (4)<br/>
     *            <br/>
     *            GL_RGBA GL_RGBA_INTEGER <br/>
     *            GL_BGRA GL_BGRA_INTEGER <br/>
     *            GL_ABGR_EXT (5)<br/>
     *
     * @return number of components required for the given OpenGL format
     * @throws GLException
     *             if format is not handled. Please contact the maintainer if this
     *             is our bug.
     */
    public static final int componentCount(final int format) throws GLException {
        final int compCount;

        switch (format) /* 26 */ {
        case GL11.GL_COLOR_INDEX:
        case GL11.GL_STENCIL_INDEX:
        case GL11.GL_DEPTH_COMPONENT:
        case GL30.GL_DEPTH_STENCIL:
        case GL11.GL_RED:
        case GL30.GL_RED_INTEGER:
        case GL11.GL_GREEN:
        case GL30.GL_GREEN_INTEGER:
        case GL11.GL_BLUE:
        case GL30.GL_BLUE_INTEGER:
        case GL11.GL_ALPHA:
        case GL11.GL_LUMINANCE:
            compCount = 1;
            break;
        case GL11.GL_LUMINANCE_ALPHA:
        case GL30.GL_RG:
        case GL30.GL_RG_INTEGER:
        case NVTextureShader.GL_HILO_NV:
        case NVTextureShader.GL_SIGNED_HILO_NV:
            compCount = 2;
            break;
        case GL11.GL_RGB:
        case GL30.GL_RGB_INTEGER:
        case GL20.GL_BGR:
        case GL30.GL_BGR_INTEGER:
            compCount = 3;
            break;
        case GL11.GL_RGBA:
        case GL41.GL_RGBA_INTEGER:
        case GL41.GL_BGRA:
        case GL41.GL_BGRA_INTEGER:
        case EXTABGR.GL_ABGR_EXT:
            compCount = 4;
            break;
        /*
         * FIXME ?? case GL11.GL_HILO_NV: elements = 2; break;
         */
        default:
            throw new GLException("format 0x" + Integer.toHexString(format)
                    + " not supported [yet], pls notify the maintainer in case this is our bug.");
        }
        return compCount;
    }

}
