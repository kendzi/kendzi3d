package kendzi.kendzi3d.editor.example;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.camera.CameraMoveListener;
import kendzi.jogl.camera.SimpleMoveAnimator;
import kendzi.jogl.camera.Viewport;
import kendzi.jogl.camera.ViewportPicker;
import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.texture.TextureCacheServiceImpl;
import kendzi.kendzi3d.editor.EditableObjectProvider;
import kendzi.kendzi3d.editor.EditorCore;
import kendzi.kendzi3d.editor.example.ui.ExampleEditorGLEventListener;
import kendzi.kendzi3d.editor.example.ui.ExampleFrame;
import kendzi.kendzi3d.editor.selection.ObjectSelectionManager;
import kendzi.kendzi3d.editor.selection.ViewportProvider;
import kendzi.kendzi3d.editor.ui.BaseEditorGLEventListener;
import kendzi.kendzi3d.resource.inter.LocalResourceReciver;
import kendzi.kendzi3d.resource.inter.ResourceService;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class ExampleModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SimpleMoveAnimator.class).in(Singleton.class);
        bind(Camera.class).to(SimpleMoveAnimator.class).in(Singleton.class);

        bind(ExampleFrame.class).in(Singleton.class);
        bind(ExampleEditorGLEventListener.class).in(Singleton.class);
        bind(ExampleCore.class).in(Singleton.class);
        bind(ViewportProvider.class).to(BaseEditorGLEventListener.class);
        bind(ObjectSelectionManager.class).in(Singleton.class);
        bind(EditableObjectProvider.class).to(ExampleCore.class).in(Singleton.class);
        bind(Viewport.class).in(Singleton.class);
        bind(ViewportPicker.class).to(Viewport.class).in(Singleton.class);

        bind(EditorCore.class).to(ExampleCore.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    CameraMoveListener provideCameraMoveListener(SimpleMoveAnimator simpleMoveAnimator) {
        return new CameraMoveListener(simpleMoveAnimator);
    }

    // @Provides
    // @Singleton
    // ObjectSelectionManager
    // provideObjectSelectionManager(ExampleGLEventListener
    // exampleGLEventListener, ExampleCore exampleCore) {
    //
    // return new ObjectSelectionManager(exampleGLEventListener, exampleCore);
    // }

    @Provides
    @Singleton
    TextureCacheService provideTextureCacheService() {

        ResourceService resourceService = new LocalResourceReciver();

        TextureCacheServiceImpl service = new TextureCacheServiceImpl();
        service.setFileUrlReciverService(resourceService);

        return service;
    }

}
