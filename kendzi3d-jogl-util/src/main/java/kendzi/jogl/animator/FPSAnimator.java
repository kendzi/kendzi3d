package kendzi.jogl.animator;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import org.lwjgl.opengl.awt.AWTGLCanvas;

public class FPSAnimator extends TimerTask implements AnimatorBase {
    private final AWTGLCanvas canvas;
    private final int fps;
    private final Timer timer;
    private boolean isStarted;

    public FPSAnimator(AWTGLCanvas canvas, int fps) {
        this.canvas = canvas;
        this.fps = fps;
        this.timer = new Timer("FPSAnimator", false);
    }

    @Override
    public void start() {
        this.isStarted = true;
        this.timer.scheduleAtFixedRate(this, TimeUnit.SECONDS.toMillis(1), (long) (1000f / this.fps));
    }

    @Override
    public boolean isStarted() {
        return this.isStarted;
    }

    @Override
    public void stop() {
        this.cancel();
        this.isStarted = false;
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(this.canvas::render);
    }
}
