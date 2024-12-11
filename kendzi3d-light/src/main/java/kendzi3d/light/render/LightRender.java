package kendzi3d.light.render;

import javax.inject.Inject;

import kendzi.jogl.Gl2Draw;
import kendzi3d.light.service.LightRenderService;
import org.lwjgl.opengl.GL11;

public class LightRender implements Gl2Draw {

    @Inject
    private LightRenderService lightRenderService;

    /**
     * Set up a light position and color.
     *
     */
    @Override
    public void draw() {

        // GL11.glMatrixMode(GL11.GL_MODELVIEW);
        //
        // // enable a single light source
        // GL11.glEnable(GL11.GL_LIGHTING);
        // GL11.glEnable(GL11.GL_LIGHT0);

        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, lightRenderService.getAmbientLightColor());

        float[] diffuseLightColor = lightRenderService.getDiffuseLightColor();
        // bright white diffuse
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, diffuseLightColor);
        // and specular
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, diffuseLightColor);

        // float lightPos[] = { 1.0f, 1.0f, 1.0f, 0.0f }; // top right front

        // direction_
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightRenderService.getLightPosition());
    }

    public void init() {

        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // enable a single light source
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
    }

}
