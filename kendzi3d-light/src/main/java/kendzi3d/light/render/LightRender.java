package kendzi3d.light.render;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

import javax.inject.Inject;

import kendzi.jogl.Gl2Draw;
import kendzi3d.light.service.LightRenderService;

public class LightRender implements Gl2Draw {

    @Inject
    private LightRenderService lightRenderService;

    /**
     * Set up a light position and color.
     *
     * @param gl
     *            gl
     */
    @Override
    public void draw(GL2 gl) {

        // gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        //
        // // enable a single light source
        // gl.glEnable(GLLightingFunc.GL_LIGHTING);
        // gl.glEnable(GLLightingFunc.GL_LIGHT0);

        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, lightRenderService.getAmbientLightColor(), 0);

        float[] diffuseLightColor = lightRenderService.getDiffuseLightColor();
        // bright white diffuse
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, diffuseLightColor, 0);
        // and specular
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, diffuseLightColor, 0);

        // float lightPos[] = { 1.0f, 1.0f, 1.0f, 0.0f }; // top right front

        // direction_
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightRenderService.getLightPosition(), 0);
    }

    public void init(GL2 gl) {

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

        // enable a single light source
        gl.glEnable(GLLightingFunc.GL_LIGHTING);
        gl.glEnable(GLLightingFunc.GL_LIGHT0);
    }

}
