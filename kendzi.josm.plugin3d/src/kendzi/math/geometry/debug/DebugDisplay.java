package kendzi.math.geometry.debug;

import javax.swing.JDialog;

import kendzi.swing.ui.panel.equation.MapComponent;

public class DebugDisplay {

    private static DebugDisplay debugDisplay = null;

    public static DebugDisplay getDebugDisplay() {
        if (debugDisplay == null) {
            debugDisplay = new DebugDisplay();
        }
        return debugDisplay;
    }

    private JDialog frame;
    private DebugLayer debugLayer;
    private MapComponent mc;

    public DebugDisplay() {

        this.debugLayer = new DebugLayer();
        createDebugFrame(this.debugLayer);
    }

    public JDialog getFrame() {
        return this.frame;
    }

    public DebugLayer getDebugLayer() {
        return this.debugLayer;
    }

    public MapComponent getMapComponent() {
        return mc;
    }

    private void createDebugFrame(final DebugLayer debugLayer) {

        mc = new MapComponent();
        mc.addLayer(debugLayer);


//        java.awt.EventQueue.invokeLater(new Runnable() {


//        class PrimeThread extends Thread {
//
//            @Override
//            public void run() {


                DebugDisplay.this.frame = new JDialog();
                DebugDisplay.this.frame.add(mc);
                DebugDisplay.this.frame.pack();
                DebugDisplay.this.frame.setSize(600, 600);

                // ui.repaint();

                DebugDisplay.this.frame.setModal(false);
                DebugDisplay.this.frame.setVisible(true);

//            }
//        }
//
//        PrimeThread p = new PrimeThread();
//        p.setDaemon(true);
//        p.setPriority(Thread.MAX_PRIORITY );
//        p.start();

//        });

    }

    public void block() {
        this.getFrame().setVisible(false);
        this.getFrame().setModal(true);
        this.getFrame().setVisible(true);
    }

}
