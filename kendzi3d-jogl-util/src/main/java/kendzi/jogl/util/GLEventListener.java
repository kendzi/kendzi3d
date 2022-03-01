package kendzi.jogl.util;

import com.jogamp.opengl.GLAutoDrawable;

public interface GLEventListener extends com.jogamp.opengl.GLEventListener {
    @Override
    default void init(GLAutoDrawable drawable) {
        this.init();
    }

    void init();

    @Override
    default void dispose(GLAutoDrawable drawable) {
        this.dispose();
    }

    void dispose();

    @Override
    default void display(GLAutoDrawable drawable) {
        this.display();
    }

    void display();

    @Override
    default void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        this.reshape(x, y, width, height);
    }

    void reshape(int x, int y, int width, int height);
}
