package kendzi.kendzi3d.editor.drawer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

public class SimpleOutlineDrawUtil {

    public static void endSimpleOutline(GL2 gl) {
        gl.glEnable(GLLightingFunc.GL_LIGHTING);
        gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
        gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_POINT);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

        gl.glDisable(GL.GL_CULL_FACE);
    }

    public static void beginSimpleOutlineLine(GL2 gl) {
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_FRONT);

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
        gl.glLineWidth(9);

        gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
        // offset polygons to back
        gl.glPolygonOffset(1.0f, 1.0f);
        // bold line

        gl.glDisable(GLLightingFunc.GL_LIGHTING);
    }

    public static void beginSimpleOutlinePoint(GL2 gl) {
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_FRONT);

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_POINT);

        gl.glPointSize(6);
        gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_POINT);

        // offset polygons to back
        gl.glPolygonOffset(2.0f, 2.0f);

        gl.glDisable(GLLightingFunc.GL_LIGHTING);
    }

}
