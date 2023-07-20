package kendzi.jogl.util;

public interface GLEventListener {
    void init();

    void dispose();

    void display();

    void reshape(int x, int y, int width, int height);
}
