package kendzi.josm.kendzi3d.jogl.model.export.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class ExportOutput extends JFrame {

    private JPanel contentPane;
    private JTextArea textLog;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ExportOutput frame = new ExportOutput();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ExportOutput() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panelButtons = new JPanel();
        contentPane.add(panelButtons, BorderLayout.SOUTH);

        JButton btnOk = new JButton("Ok");
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panelButtons.add(btnOk);

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        textLog = new JTextArea();
        textLog.setEditable(false);
        scrollPane.setViewportView(textLog);
    }

    protected JTextArea getTextLog() {
        return textLog;
    }

    public void addLog(String str) {
        textLog.setText(textLog.getText() + "\n" + str);
    }
}
