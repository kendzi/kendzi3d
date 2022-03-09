package kendzi.jogl;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import kendzi.jogl.util.GLEventListener;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapt GLEventListener from JOGL
 */
public class GLAutoDrawable extends AWTGLCanvas implements ComponentListener {
    private static final Logger log = LoggerFactory.getLogger(GLAutoDrawable.class);

    protected Collection<GLEventListener> listeners = new LinkedHashSet<>();

    public GLAutoDrawable() {
        super();
    }

    public GLAutoDrawable(GLData glData) {
        super(glData);
    }

    /**
     * Add a listener to be called on repaint
     *
     * @param listener
     *            The listener to be called
     * @return See {@link Collection#add(Object)}
     */
    public boolean addGLEventListener(GLEventListener listener) {
        if (this.initCalled) {
            listener.init();
        }
        return listeners.add(listener);
    }

    /**
     * Remove a listener to be called on repaint
     *
     * @param listener
     *            The listener to be removed
     * @return See {@link Collection#remove(Object)}
     */
    public boolean removeGLEventListener(GLEventListener listener) {
        listener.dispose();
        return listeners.remove(listener);
    }

    @Override
    public void initGL() {
        GL.createCapabilities(this.data.forwardCompatible);
        Color color = Color.LIGHT_GRAY;
        GL11C.glClearColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        this.runCommand(GLEventListener::init);
        // Ensure that the viewport is initialized (requires GL.createCapabilities to
        // have been called)
        this.addComponentListener(this);
        this.componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
    }

    @Override
    public void paintGL() {
        this.runCommand(GLEventListener::display);
        this.swapBuffers();
    }

    @Override
    public void disposeCanvas() {
        this.componentHidden(new ComponentEvent(this, ComponentEvent.COMPONENT_HIDDEN));
        this.removeComponentListener(this);
        this.runCommand(GLEventListener::dispose);
        super.disposeCanvas();
    }

    @Override
    public void componentResized(ComponentEvent event) {
        if (this.initCalled) {
            final int x = event.getComponent().getX();
            final int y = event.getComponent().getY();
            final int width = event.getComponent().getWidth();
            final int height = event.getComponent().getHeight();
            this.runInContext(() -> this.runCommand(listener -> listener.reshape(x, y, width, height)));
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        this.componentResized(e);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        this.componentResized(e);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        this.componentResized(e);
    }

    private void runCommand(Consumer<GLEventListener> callable) {
        for (GLEventListener listener : this.listeners) {
            try {
                callable.accept(listener);
            } catch (Exception exception) {
                log.error("", exception);
            }
        }
    }

    @Override
    public void repaint() {
        super.repaint();
        if (SwingUtilities.isEventDispatchThread()) {
            this.render();
        } else {
            SwingUtilities.invokeLater(this::render);
        }
    }
}
