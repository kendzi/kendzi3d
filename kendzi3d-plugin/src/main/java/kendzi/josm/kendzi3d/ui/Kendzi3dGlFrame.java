package kendzi.josm.kendzi3d.ui;

import java.awt.event.ComponentEvent;

import javax.inject.Inject;

import com.jogamp.opengl.GLEventListener;
import kendzi.jogl.texture.TextureCacheService;
import kendzi.josm.kendzi3d.data.event.DataEvent;
import kendzi.josm.kendzi3d.data.event.NewDataEvent;
import kendzi.josm.kendzi3d.data.event.SelectionDataEvent;
import kendzi.josm.kendzi3d.data.producer.DataEventListener;
import kendzi.josm.kendzi3d.data.producer.EditorObjectsProducer;
import kendzi.josm.kendzi3d.data.producer.JosmDataEventSource;
import kendzi.josm.kendzi3d.data.selection.SelectionSynchronizeManager;
import kendzi.kendzi3d.editor.ui.BaseEditorFrame;

public class Kendzi3dGlFrame extends BaseEditorFrame implements DataEventListener {

    @Inject
    private Kendzi3dGLEventListener listener;

    @Inject
    private TextureCacheService textureCacheService;

    @Inject
    private EditorObjectsProducer editorObjectsProducer;

    @Inject
    private SelectionSynchronizeManager selectionSynchronizeManager;

    private JosmDataEventSource josmDataEventSource;

    @Override
    public GLEventListener getGlEventListener() {
        return listener;
    }

    @Override
    protected void onOpenWindow() {
        josmDataEventSource = new JosmDataEventSource(this);

        /*
         * OpenGl context is stored per window, if we open new one we need to
         * clean up old textures.
         */
        textureCacheService.clear();

        /*
         * Data updates ware turn off after window closes. We need to rebuild
         * whole dataset.
         */
        add(new NewDataEvent());
    }

    @Override
    protected void onCloseWindow() {
        josmDataEventSource.deregisterJosmEventSource();
    }

    @Override
    public void add(DataEvent dataEvent) {
        dataEvent.setResumable(() -> resumeAnimator());

        if (dataEvent instanceof SelectionDataEvent) {
            selectionSynchronizeManager.add(dataEvent);;
        } else {
            editorObjectsProducer.add(dataEvent);
        }
    }

    public void resumeAnimator() {
        if (this.isDisplayable()) {
            canvas.dispatchEvent(new ComponentEvent(canvas, ComponentEvent.COMPONENT_SHOWN));
        }
    }
}
