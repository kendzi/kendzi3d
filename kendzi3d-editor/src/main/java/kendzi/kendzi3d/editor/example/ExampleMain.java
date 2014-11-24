package kendzi.kendzi3d.editor.example;

import kendzi.kendzi3d.editor.example.ui.ExampleFrame;

import org.apache.log4j.BasicConfigurator;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ExampleMain {

    public static void main(String[] args) {

        // Set up a simple configuration that logs on the console.
        BasicConfigurator.configure();

        Injector injector = Guice.createInjector(new ExampleModule());

        // SimpleMoveAnimator simpleMoveAnimator =
        // injector.getInstance(SimpleMoveAnimator.class);
        // CameraMoveListener cameraMoveListener =
        // injector.getInstance(CameraMoveListener.class);
        // ExampleGLEventListener exampleGLEventListener =
        // injector.getInstance(ExampleGLEventListener.class);
        ExampleFrame frame = injector.getInstance(ExampleFrame.class);

        frame.initUi();
        frame.setVisible(true);
    }

}
