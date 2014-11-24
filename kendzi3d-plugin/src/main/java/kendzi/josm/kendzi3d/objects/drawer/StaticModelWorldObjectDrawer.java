package kendzi.josm.kendzi3d.objects.drawer;

import javax.inject.Inject;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.vecmath.Point3d;

import kendzi.jogl.Gl2Draw;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.kendzi3d.editor.drawer.HighlightDrawer;
import kendzi.kendzi3d.world.StaticModelWorldObject;

public class StaticModelWorldObjectDrawer {

    private final ColoredModelGl2Draw modelGl2Draw = new ColoredModelGl2Draw();

    @Inject
    private ModelRender modelRender;

    public void draw(GL2 gl, StaticModelWorldObject modelObject, boolean selected) {

        gl.glPushMatrix();

        // global position
        Point3d position = modelObject.getPosition();
        Model model = modelObject.getModel();

        // move to global space
        gl.glTranslated(position.x, position.y, position.z);

        if (selected) {
            modelGl2Draw.setModel(model);
            modelGl2Draw.setModelRender(modelRender);

            modelRender.resetMaterials();
            // gl.glColor4f(0.8f, 0.8f, 0.8f, 1);
            gl.glEnable(GLLightingFunc.GL_LIGHTING);
            gl.glEnable(GL.GL_TEXTURE_2D);

            HighlightDrawer.drawHighlight(modelGl2Draw, gl);

        } else {
            modelRender.render(gl, model);
        }

        gl.glPopMatrix();
    }

    private static class ColoredModelGl2Draw implements Gl2Draw {

        private ModelRender modelRender;

        private Model model;

        @Override
        public void draw(GL2 gl) {
            modelRender.renderRaw(gl, model);
        }

        /**
         * @return the modelRender
         */
        public ModelRender getModelRender() {
            return modelRender;
        }

        /**
         * @param modelRender
         *            the modelRender to set
         */
        public void setModelRender(ModelRender modelRender) {
            this.modelRender = modelRender;
        }

        /**
         * @return the model
         */
        public Model getModel() {
            return model;
        }

        /**
         * @param model
         *            the model to set
         */
        public void setModel(Model model) {
            this.model = model;
        }
    }

}
