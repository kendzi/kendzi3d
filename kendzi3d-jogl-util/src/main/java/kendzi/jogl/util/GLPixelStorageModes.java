/**
 * Copyright 2010 JogAmp Community. All rights reserved.
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

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLCapabilities;

/**
 * Utility to safely set and restore the PACK and UNPACK pixel storage mode,
 * regardless of the GLProfile.
 * <p>
 * PACK for GPU to CPU transfers, e.g.
 * {@link GL#glReadPixels(int, int, int, int, int, int, java.nio.Buffer)
 * ReadPixels}, etc.
 * </p>
 * <p>
 * UNPACK for CPU o GPU transfers, e.g.
 * {@link GL#glTexImage2D(int, int, int, int, int, int, int, int, long)
 * TexImage2D}, etc
 * </p>
 */
@Deprecated
public class GLPixelStorageModes {
    private final int[] cachePack = new int[8];
    private final int[] cacheUnpack = new int[8];
    private boolean savedPack = false;
    private boolean savedUnpack = false;

    /** Create instance w/ {@link #saveAll()} */
    public GLPixelStorageModes() {
        saveAll();
    }

    /**
     * Saves PACK and UNPACK pixel storage modes and {@link #resetAll() resets}
     * them, i.e. issues {@link #savePack()} and {@link #saveUnpack()}.
     * <p>
     * Operation is skipped, if the modes were already saved.
     * </p>
     * <p>
     * Restore via {@link #restore()}
     * </p>
     */
    public final void saveAll() {
        savePack();
        saveUnpack();
    }

    /**
     * Resets PACK pixel storage modes to their default value.
     */
    public final void resetPack() {
        // Compared w/ ES2, ES3 and GL3-core spec
        final GLCapabilities cap = GL.getCapabilities();
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 4); // es2, es3, gl3
        if (cap.OpenGL20) {
            GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH, 0); // es3, gl3
            GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS, 0); // es3, gl3
            GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, 0); // es3, gl3
            if (cap.OpenGL20 && cap.forwardCompatible) {
                GL11.glPixelStorei(GL11.GL_PACK_SWAP_BYTES, GL11.GL_FALSE); // gl3
                GL11.glPixelStorei(GL11.GL_PACK_LSB_FIRST, GL11.GL_FALSE); // gl3
                if (cap.OpenGL12) {
                    GL11.glPixelStorei(GL12.GL_PACK_IMAGE_HEIGHT, 0); // gl3, GL_VERSION_1_2
                    GL11.glPixelStorei(GL12.GL_PACK_SKIP_IMAGES, 0); // gl3, GL_VERSION_1_2
                }
            }
        }
    }

    /**
     * Saves PACK pixel storage modes and {@link #resetPack() resets} them.
     * <p>
     * Operation is skipped, if the modes were already saved.
     * </p>
     * <p>
     * Restore via {@link #restore()}
     * </p>
     */
    public final void savePack() {
        if (savedPack) {
            return;
        }
        final GLCapabilities cap = GL.getCapabilities();
        if (cap.OpenGL12) {
            // See GLStateTracker.pushAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT)
            GL11.glPushClientAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT);
        } else {
            // ES1 or ES2 deals with pack/unpack alignment only
            cachePack[0] = GL11.glGetInteger(GL11.GL_PACK_ALIGNMENT);
            if (cap.OpenGL20) {
                cachePack[1] = GL11.glGetInteger(GL11.GL_PACK_ROW_LENGTH);
                cachePack[2] = GL11.glGetInteger(GL11.GL_PACK_SKIP_ROWS);
                cachePack[3] = GL11.glGetInteger(GL11.GL_PACK_SKIP_PIXELS);
                if (cap.forwardCompatible) {
                    cachePack[4] = GL11.glGetInteger(GL11.GL_PACK_SWAP_BYTES);
                    cachePack[5] = GL11.glGetInteger(GL11.GL_PACK_LSB_FIRST);
                    cachePack[6] = GL11.glGetInteger(GL12.GL_PACK_IMAGE_HEIGHT);
                    cachePack[7] = GL11.glGetInteger(GL12.GL_PACK_SKIP_IMAGES);
                }
            }
        }
        savedPack = true;
        resetPack();
    }

    private final void restorePack() {
        GLCapabilities cap = GL.getCapabilities();
        if (cap.OpenGL12) {
            GL11.glPopClientAttrib();
        } else {
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, cachePack[0]);
            if (cap.OpenGL20) {
                GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH, cachePack[1]);
                GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS, cachePack[2]);
                GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, cachePack[3]);
                if (cap.forwardCompatible) {
                    GL11.glPixelStorei(GL11.GL_PACK_SWAP_BYTES, cachePack[4]);
                    GL11.glPixelStorei(GL11.GL_PACK_LSB_FIRST, cachePack[5]);
                    GL11.glPixelStorei(GL12.GL_PACK_IMAGE_HEIGHT, cachePack[6]);
                    GL11.glPixelStorei(GL12.GL_PACK_SKIP_IMAGES, cachePack[7]);
                }
            }
        }
    }

    /**
     * Resets UNPACK pixel storage modes to their default value.
     */
    public final void resetUnpack() {
        // Compared w/ ES2, ES3 and GL3-core spec
        final GLCapabilities cap = GL.getCapabilities();
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4); // es2, es3, gl3
        if (cap.OpenGL20) {
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0); // es3, gl3
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0); // es3, gl3
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0); // es3, gl3
            if (cap.forwardCompatible) {
                if (cap.OpenGL12) {
                    GL11.glPixelStorei(GL12.GL_UNPACK_IMAGE_HEIGHT, 0); // es3, gl3, GL_VERSION_1_2
                    GL11.glPixelStorei(GL12.GL_UNPACK_SKIP_IMAGES, 0); // es3, gl3, GL_VERSION_1_2
                }
                GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, GL11.GL_FALSE); // gl3
                GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, GL11.GL_FALSE); // gl3
            } else {
                GL11.glPixelStorei(GL12.GL_UNPACK_IMAGE_HEIGHT, 0); // es3, gl3, GL_VERSION_1_2
                GL11.glPixelStorei(GL12.GL_UNPACK_SKIP_IMAGES, 0); // es3, gl3, GL_VERSION_1_2
            }
        }
    }

    /**
     * Saves UNPACK pixel storage modes and {@link #resetUnpack() resets} them.
     * <p>
     * Operation is skipped, if the modes were already saved.
     * </p>
     * <p>
     * Restore via {@link #restore()}
     * </p>
     */
    public final void saveUnpack() {
        if (savedUnpack) {
            return;
        }
        final GLCapabilities cap = GL.getCapabilities();
        if (cap.OpenGL12) {
            // See GLStateTracker.pushAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT)
            GL11.glPushClientAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT);
        } else {
            // ES1 or ES2 deals with pack/unpack alignment only
            cacheUnpack[0] = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
            if (cap.OpenGL20) {
                cacheUnpack[1] = GL11.glGetInteger(GL11.GL_UNPACK_ROW_LENGTH);
                cacheUnpack[2] = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_ROWS);
                cacheUnpack[3] = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_PIXELS);
                cacheUnpack[4] = GL11.glGetInteger(GL12.GL_UNPACK_IMAGE_HEIGHT);
                cacheUnpack[5] = GL11.glGetInteger(GL12.GL_UNPACK_SKIP_IMAGES);
                if (cap.forwardCompatible) {
                    cacheUnpack[6] = GL11.glGetInteger(GL11.GL_UNPACK_SWAP_BYTES);
                    cacheUnpack[7] = GL11.glGetInteger(GL11.GL_UNPACK_LSB_FIRST);
                }
            }
        }
        savedUnpack = true;
        resetUnpack();
    }

    private final void restoreUnpack() {
        final GLCapabilities cap = GL.getCapabilities();
        if (cap.OpenGL12) {
            GL11.glPopClientAttrib();
        } else {
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, cacheUnpack[0]);
            if (cap.OpenGL20) {
                GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, cacheUnpack[1]);
                GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, cacheUnpack[2]);
                GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, cacheUnpack[3]);
                GL11.glPixelStorei(GL12.GL_UNPACK_IMAGE_HEIGHT, cacheUnpack[4]);
                GL11.glPixelStorei(GL12.GL_UNPACK_SKIP_IMAGES, cacheUnpack[5]);
                if (cap.forwardCompatible) {
                    GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, cacheUnpack[6]);
                    GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, cacheUnpack[7]);
                }
            }
        }
    }
}