package kendzi.math.geometry.skeleton;
import java.awt.BasicStroke;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class StrokeExample extends JPanel implements Runnable {

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        g.drawLine(0,0,w,h);    //default
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0,h,w,0);   //thick

    }

    @Override
    public void run() {

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new StrokeExample());
        f.setSize(500,400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new StrokeExample());
    }
}
