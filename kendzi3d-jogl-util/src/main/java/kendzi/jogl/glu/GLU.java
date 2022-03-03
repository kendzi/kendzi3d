/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package kendzi.jogl.glu;

import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_STACK_OVERFLOW;
import static org.lwjgl.opengl.GL11.GL_STACK_UNDERFLOW;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBImaging;
import org.lwjgl.opengl.EXTFramebufferObject;

/**
 * GLU.java
 *
 *
 * Created 23-dec-2003
 * 
 * @author Erik Duijs
 */
@Deprecated
public class GLU {
    private static final Disk DISK = new Disk();
    private static final Cylinder CYLINDER = new Cylinder();
    private static final Sphere SPHERE = new Sphere();

    static final float PI = (float) Math.PI;

    /* Errors: (return value 0 = no error) */
    public static final int GLU_INVALID_ENUM = 100900;
    public static final int GLU_INVALID_VALUE = 100901;
    public static final int GLU_OUT_OF_MEMORY = 100902;

    /**** Quadric constants ****/

    /* QuadricNormal */
    public static final int GLU_SMOOTH = 100000;
    public static final int GLU_NONE = 100002;

    /* QuadricDrawStyle */
    public static final int GLU_POINT = 100010;
    public static final int GLU_LINE = 100011;
    public static final int GLU_FILL = 100012;
    public static final int GLU_SILHOUETTE = 100013;

    /* QuadricOrientation */
    public static final int GLU_OUTSIDE = 100020;
    public static final int GLU_INSIDE = 100021;

    /* Callback types: */
    /* ERROR = 100103 */

    /* NurbsCallback */
    /* ERROR = 100103 */

    /**
     * Method gluLookAt
     * 
     * @param eyex
     * @param eyey
     * @param eyez
     * @param centerx
     * @param centery
     * @param centerz
     * @param upx
     * @param upy
     * @param upz
     */
    public static void gluLookAt(float eyex, float eyey, float eyez, float centerx, float centery, float centerz, float upx,
            float upy, float upz) {

        Project.gluLookAt(eyex, eyey, eyez, centerx, centery, centerz, upx, upy, upz);
    }

    /**
     * Method gluOrtho2D
     * 
     * @param left
     * @param right
     * @param bottom
     * @param top
     */
    public static void gluOrtho2D(float left, float right, float bottom, float top) {

        glOrtho(left, right, bottom, top, -1.0, 1.0);
    }

    /**
     * Method gluPerspective
     * 
     * @param fovy
     * @param aspect
     * @param zNear
     * @param zFar
     */
    public static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {

        Project.gluPerspective(fovy, aspect, zNear, zFar);
    }

    /**
     * Method gluProject
     * 
     * @param objx
     * @param objy
     * @param objz
     * @param modelMatrix
     * @param projMatrix
     * @param viewport
     * @param win_pos
     */
    public static boolean gluProject(float objx, float objy, float objz, FloatBuffer modelMatrix, FloatBuffer projMatrix,
            IntBuffer viewport, FloatBuffer win_pos) {
        return Project.gluProject(objx, objy, objz, modelMatrix, projMatrix, viewport, win_pos);
    }

    /**
     * Method gluBuild2DMipmaps
     * 
     * @param target
     * @param components
     * @param width
     * @param height
     * @param format
     * @param type
     * @param data
     * @return int
     */
    public static int gluBuild2DMipmaps(int target, int components, int width, int height, int format, int type,
            ByteBuffer data) {

        return MipMap.gluBuild2DMipmaps(target, components, width, height, format, type, data);
    }

    public static String gluErrorString(int error_code) {
        switch (error_code) {
        case GLU_INVALID_ENUM:
            return "Invalid enum (glu)";
        case GLU_INVALID_VALUE:
            return "Invalid value (glu)";
        case GLU_OUT_OF_MEMORY:
            return "Out of memory (glu)";
        default:
            return translateGLErrorString(error_code);
        }
    }

    private static String translateGLErrorString(int error_code) {
        switch (error_code) {
        case GL_NO_ERROR:
            return "No error";
        case GL_INVALID_ENUM:
            return "Invalid enum";
        case GL_INVALID_VALUE:
            return "Invalid value";
        case GL_INVALID_OPERATION:
            return "Invalid operation";
        case GL_STACK_OVERFLOW:
            return "Stack overflow";
        case GL_STACK_UNDERFLOW:
            return "Stack underflow";
        case GL_OUT_OF_MEMORY:
            return "Out of memory";
        case ARBImaging.GL_TABLE_TOO_LARGE:
            return "Table too large";
        case EXTFramebufferObject.GL_INVALID_FRAMEBUFFER_OPERATION_EXT:
            return "Invalid framebuffer operation";
        default:
            return null;
        }
    }

    public static void gluDisk(float innerRadius, float outerRadius, int slices, int loops) {
        DISK.draw(innerRadius, outerRadius, slices, loops);
    }

    public static void gluCylinder(float baseRadius, float topRadius, float height, int slice, int stacks) {
        CYLINDER.draw(baseRadius, topRadius, height, slice, stacks);
    }

    public static void gluSphere(float radius, int slices, int stacks) {
        SPHERE.draw(radius, slices, stacks);
    }
}
