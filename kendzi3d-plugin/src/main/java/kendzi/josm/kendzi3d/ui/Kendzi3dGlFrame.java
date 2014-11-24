package kendzi.josm.kendzi3d.ui;

import javax.inject.Inject;
import javax.media.opengl.GLEventListener;

import kendzi.jogl.texture.TextureCacheService;
import kendzi.josm.kendzi3d.data.event.NewDataEvent;
import kendzi.josm.kendzi3d.data.producer.DataConsumersMonitor;
import kendzi.josm.kendzi3d.data.producer.DataEventListener;
import kendzi.kendzi3d.editor.ui.BaseEditorFrame;

public class Kendzi3dGlFrame extends BaseEditorFrame {

    @Inject
    private Kendzi3dGLEventListener listener;

    @Inject
    private DataConsumersMonitor dataConsumersMonitor;

    @Inject
    private TextureCacheService textureCacheService;
    @Inject
    private DataEventListener dataEventListener;

    @Override
    public GLEventListener getGlEventListener() {
        return listener;
    }

    @Override
    protected void onOpenWindow() {
        dataConsumersMonitor.addDataConsumer();

        /*
         * OpenGl context is stored per window, if we open new one we need to
         * clean up old textures.
         */
        textureCacheService.clear();

        /*
         * Data updates ware turn off after window closes. We need to rebuild
         * whole dataset.
         */
        dataEventListener.add(new NewDataEvent());
    }

    @Override
    protected void onCloseWindow() {
        dataConsumersMonitor.removeDataConsumer();
    }

}
