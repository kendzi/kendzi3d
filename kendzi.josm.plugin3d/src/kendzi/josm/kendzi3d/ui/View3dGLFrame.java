package kendzi.josm.kendzi3d.ui;


import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.media.nativewindow.Capabilities;
import javax.media.nativewindow.GraphicsConfigurationFactory;
import javax.media.nativewindow.awt.AWTGraphicsConfiguration;
import javax.media.nativewindow.awt.AWTGraphicsDevice;
import javax.media.nativewindow.awt.AWTGraphicsScreen;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.jogl.photos.PhotoParmPanel;

import org.apache.log4j.Logger;

import com.jogamp.opengl.util.Animator;

// Based on TourGL.java by Andrew Davison

public class View3dGLFrame extends Frame implements WindowListener {

    /** Log. */
    private static final Logger log = Logger.getLogger(View3dGLFrame.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private static final int PWIDTH = 512; // size of panel
    private static final int PHEIGHT = 512;


    public Canvas canvas;
    private JTextField shapesLeftTF; // displays no. of shapes left
    private JTextField jtfTime; // displays time spent in game

    private PhotoParmPanel photoParmPanel;

    Kendzi3dGLEventListener canvasListener = new Kendzi3dGLEventListener() {

        @Override
        void displayStats(long pTime, int pFps) {
            setTimeSpent(pTime);
            setFps(pFps);
        }
    };

    private Animator animator;


    public View3dGLFrame() {
        super("Kendzi 3D");

//        Container c = getContentPane();
        Container c = this;
        c.setLayout(new BorderLayout());
        c.add(makeRenderPanel(), BorderLayout.CENTER);

        JPanel ctrls = new JPanel(); // a row of text fields
        ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.X_AXIS));

        this.shapesLeftTF = new JTextField("Test");
        this.shapesLeftTF.setEditable(false);
        ctrls.add(this.shapesLeftTF);

        this.jtfTime = new JTextField("Time Spent: 0 secs");
        this.jtfTime.setEditable(false);
        ctrls.add(this.jtfTime);

        c.add(ctrls, BorderLayout.SOUTH);

        addWindowListener(this);
//        getWindowListeners();

//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);



//        WindowsWGLGraphicsConfigurationFactory
        pack();
        setVisible(true);

        if (PhotoParmPanel.showPhotoPanel) {
            initPhotoFrame();
        }
    }

    private void initPhotoFrame() {
        JFrame photoFrame = new JFrame();
        this.photoParmPanel = new PhotoParmPanel();
        photoFrame.getContentPane().add(this.photoParmPanel);
        photoFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        photoFrame.pack();
        photoFrame.setVisible(true);
//        photoParmPanel.add
        this.photoParmPanel.addCameraChangeListener(this.canvasListener);
    }

    /** Make canvas panel.
     * @return
     */
    private JPanel makeRenderPanel() {
        JPanel renderPane = new JPanel();
        renderPane.setLayout(new BorderLayout());
        renderPane.setOpaque(false);
        renderPane.setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
        // XXX
        renderPane.setSize(640, 480);

        this.canvas = makeCanvas2(renderPane);
//        renderPane.add("Center", this.canvas);

        renderPane.add(this.canvas);
        renderPane.setVisible(true);

        renderPane.getComponentListeners();
        this.getWindowListeners();

        this.canvas.setFocusable(true);
        this.canvas.requestFocus(); // the canvas now has focus, so receives key
        // events

//        // detect window resizes, and reshape the canvas accordingly
//        renderPane.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent evt) {
//                Dimension d = evt.getComponent().getSize();
//                // log.info("New size: " + d);
//                View3dGLFrame.this.canvas.reshape(d.width, d.height);
//            } // end of componentResized()
//        });

        return renderPane;
    } // end of makeRenderPanel()

    private View3dCanvasGL makeCanvas(long period) {

        GLProfile profile = GLProfile.get(GLProfile.GL2);

        // get a configuration suitable for an AWT Canvas (for TourCanvasGL)
        GLCapabilities caps = new GLCapabilities(profile);

        // setup z-buffer
        caps.setDepthBits(16);

        // for anti-aliasing
        caps.setSampleBuffers(true);
        caps.setNumSamples(2);

        Capabilities cap = new Capabilities();
        //		cap.set


        AWTGraphicsScreen screen = (AWTGraphicsScreen) AWTGraphicsScreen.createDefault();
        //		AWTGraphicsConfiguration awtConfig = (AWTGraphicsConfiguration) GraphicsConfigurationFactory
        //				.getFactory(AWTGraphicsDevice.class).chooseGraphicsConfiguration(
        //						caps, null, screen);
        AWTGraphicsConfiguration awtConfig = (AWTGraphicsConfiguration) GraphicsConfigurationFactory
        .getFactory(AWTGraphicsDevice.class).chooseGraphicsConfiguration(
                caps, caps, null, screen);

        if (log.isDebugEnabled()) {
            log.debug("screean awtConfig: \n" + awtConfig);
            log.debug("screean caps: \n" + caps);
        }
        return new View3dCanvasGL(this, period, PWIDTH, PHEIGHT, awtConfig, caps);
    } // end of makeCanvas()

    private Canvas makeCanvas2(JPanel render) {

        log.info("is set debug for GraphicsConfiguration: " + com.jogamp.opengl.impl.Debug.debug("GraphicsConfiguration"));

        //create a profile, in this case OpenGL 2 or later
        GLProfile profile = GLProfile.get(GLProfile.GL2);

        //configure context
        GLCapabilities capabilities = new GLCapabilities(profile);

        String zbuffer = System.getProperty("kendzi3d.opengl.zbuffer");
        log.info("user zbuffer: " + zbuffer);

        // setup z-buffer
        if (zbuffer == null) {
            // Default use 16
            capabilities.setDepthBits(16);
        } else if (!"default".equals(zbuffer)) {
            capabilities.setDepthBits(Integer.parseInt(zbuffer));
        }

        String sampleBuffers = System.getProperty("kendzi3d.opengl.sampleBuffers");
        log.info("user sampleBuffers: " + sampleBuffers);

        if (sampleBuffers == null) {
            capabilities.setSampleBuffers(true);
        } else if ("true".equals(sampleBuffers)) {
            capabilities.setSampleBuffers(true);
        } else if ("false".equals(sampleBuffers)) {
            capabilities.setSampleBuffers(false);
        }

        String sampleBuffersNum = System.getProperty("kendzi3d.opengl.sampleBuffersNum");
        log.info("user sampleBuffersNum: " + sampleBuffersNum);

        if (sampleBuffersNum == null) {
            capabilities.setNumSamples(2);
        } else if (!"default".equals(sampleBuffersNum)) {
            capabilities.setNumSamples(Integer.parseInt(sampleBuffersNum));
        }

        log.info("GLCapabilities: " + capabilities);
        //initialize a GLDrawable of your choice
        GLCanvas canvas = new GLCanvas(capabilities);


        canvas.addGLEventListener(this.canvasListener);

//        WindowsWGLGraphicsConfigurationFactory

        this.animator = new Animator(canvas);
//        animator.set
//        this.addWindowListener(new WindowAdapter() {
//
//
//            @Override
//            public void windowClosing(WindowEvent e) {
//                // Run this on another thread than the AWT event queue to
//                // make sure the call to Animator.stop() completes before
//                // exiting
//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        View3dGLFrame.this.animator.stop();
//                        System.exit(0);
//                    }
//                }).start();
//            }
//        });

        this.canvasListener.addMoveListener(canvas);

        // Center frame
//        render.setLocationRelativeTo(null);

        this.animator.start();
        canvas.setFocusable(true);
        canvas.requestFocus();


        return canvas;
    }

    /** Display time spent.
     * @param pTime time
     */
    public void setTimeSpent(long pTime) {
        this.jtfTime.setText("Time Spent: " + pTime + " secs");
    }

    /** Display fps.
     * @param pFps fps
     */
    public void setFps(int pFps) {
        this.shapesLeftTF.setText("Fps: " + pFps);
    }

    // ----------------- window listener methods -------------

    @Override
    public void windowActivated(WindowEvent e) {
//        this.canvas.resumeGame();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
//        this.canvas.pauseGame();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
//        this.canvas.resumeGame();
    }

    @Override
    public void windowIconified(WindowEvent e) {
//        this.canvas.pauseGame();
    }

    @Override
    public void windowClosing(WindowEvent e) {

//        View3dGLFrame.this.animator.stop();
//        View3dGLFrame.this.setVisible(false);
//        this.dispose();

        new Thread(new Runnable() {

            @Override
            public void run() {
                View3dGLFrame.this.animator.stop();
                View3dGLFrame.this.setVisible(false);
                View3dGLFrame.this.dispose();
            }
        }).start();

        //e.consume();
//        e.getID()
//        e.
      //  this.setVisible(false);

//        this.canvas.stopGame();

       // this.animator.pause();

        // Run this on another thread than the AWT event queue to
        // make sure the call to Animator.stop() completes before
        // exiting
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                View3dGLFrame.this.setVisible(false);
//                View3dGLFrame.this.animator.stop();
//                if (View3dGLFrame.this.isDisplayable()) {
//                    View3dGLFrame.this.dispose();
//                }
//            }
//        }).start();
    }

    @Override
    public void windowClosed(WindowEvent e) {
//        // Run this on another thread than the AWT event queue to
//        // make sure the call to Animator.stop() completes before
//        // exiting
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
////                View3dGLFrame.this.animator.stop();
//                View3dGLFrame.this.setVisible(false);
//            }
//        }).start();
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    // -----------------------------------------

    public static void main(String[] args) {

        new View3dGLFrame();
    }

    public RenderJOSM getRenderJosm() {

        return this.canvasListener.getRenderJosm();
    }

    /**
     * @return the canvasListener
     */
    public Kendzi3dGLEventListener getCanvasListener() {
        return this.canvasListener;
    }

    public void resume() {
        //this.animator.resume();

//        canvas.setFocusable(true);
//        canvas.requestFocus();
        this.setVisible(true);
    }

}
