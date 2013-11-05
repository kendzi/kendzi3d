package kendzi.josm.kendzi3d.jogl.model.roof.mk.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MainFrame {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setSize(50, 50);
        JButton button = new JButton("Click me");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOGLFrame frame = new JOGLFrame();

                frame.initUI();

                frame.setVisible(true);

            }
        });
        frame.add(button, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

}
