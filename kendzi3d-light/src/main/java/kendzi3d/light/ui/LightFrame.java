/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi3d.light.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LightFrame extends JFrame {

    private JPanel contentPane;
    private JTextField directionText;
    private JTextField angleText;
    private JLabel lblAmbientColor;
    private JLabel lblDiffuseColor;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LightFrame frame = new LightFrame();
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
    public LightFrame() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel buttonPanel = new JPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        JButton btnOk = new JButton("Save");
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOk();
            }

        });

        JButton btnDefault = new JButton("Default");
        btnDefault.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDefault();
            }
        });
        buttonPanel.add(btnDefault);
        buttonPanel.add(btnOk);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(btnCancel);

        JPanel imagePanel = new JPanel();
        contentPane.add(imagePanel, BorderLayout.CENTER);
        imagePanel.setLayout(null);

        JLabel lblLightImage = new JLabel("New label");
        lblLightImage.setIcon(new ImageIcon(LightFrame.class.getResource("/kendzi3d/light/light.png")));
        lblLightImage.setBounds(12, 12, 400, 300);
        imagePanel.add(lblLightImage);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.EAST);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 140, 0 };
        gbl_panel.rowHeights = new int[] { 15, 19, 33, 15, 19, 33, 25, 41, 25, 41, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        JButton btnAmbientColor = new JButton("Select ambient");
        btnAmbientColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSelectAmbientColor();
            }

        });

        JLabel lblDirection = new JLabel("Direction");
        GridBagConstraints gbc_lblDirection = new GridBagConstraints();
        gbc_lblDirection.anchor = GridBagConstraints.NORTH;
        gbc_lblDirection.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblDirection.insets = new Insets(0, 0, 5, 0);
        gbc_lblDirection.gridx = 0;
        gbc_lblDirection.gridy = 0;
        panel.add(lblDirection, gbc_lblDirection);

        directionText = new JTextField();
        GridBagConstraints gbc_directionText = new GridBagConstraints();
        gbc_directionText.anchor = GridBagConstraints.NORTH;
        gbc_directionText.fill = GridBagConstraints.HORIZONTAL;
        gbc_directionText.insets = new Insets(0, 0, 5, 0);
        gbc_directionText.gridx = 0;
        gbc_directionText.gridy = 1;
        panel.add(directionText, gbc_directionText);
        directionText.setText("0");
        directionText.setColumns(10);

        JLabel lblAngle = new JLabel("Angle");
        GridBagConstraints gbc_lblAngle = new GridBagConstraints();
        gbc_lblAngle.anchor = GridBagConstraints.NORTH;
        gbc_lblAngle.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblAngle.insets = new Insets(0, 0, 5, 0);
        gbc_lblAngle.gridx = 0;
        gbc_lblAngle.gridy = 3;
        panel.add(lblAngle, gbc_lblAngle);

        angleText = new JTextField();
        angleText.setText("45");
        GridBagConstraints gbc_angleText = new GridBagConstraints();
        gbc_angleText.anchor = GridBagConstraints.NORTH;
        gbc_angleText.fill = GridBagConstraints.HORIZONTAL;
        gbc_angleText.insets = new Insets(0, 0, 5, 0);
        gbc_angleText.gridx = 0;
        gbc_angleText.gridy = 4;
        panel.add(angleText, gbc_angleText);
        angleText.setColumns(10);
        GridBagConstraints gbc_btnAmbientColor = new GridBagConstraints();
        gbc_btnAmbientColor.anchor = GridBagConstraints.NORTHWEST;
        gbc_btnAmbientColor.insets = new Insets(0, 0, 5, 0);
        gbc_btnAmbientColor.gridx = 0;
        gbc_btnAmbientColor.gridy = 6;
        panel.add(btnAmbientColor, gbc_btnAmbientColor);

        lblAmbientColor = new JLabel("Ambient color");
        lblAmbientColor.setHorizontalAlignment(SwingConstants.CENTER);
        lblAmbientColor.setBorder(new LineBorder(new Color(0, 0, 0)));
        lblAmbientColor.setOpaque(true);
        GridBagConstraints gbc_lblAmbientColor = new GridBagConstraints();
        gbc_lblAmbientColor.fill = GridBagConstraints.BOTH;
        gbc_lblAmbientColor.insets = new Insets(0, 0, 5, 0);
        gbc_lblAmbientColor.gridx = 0;
        gbc_lblAmbientColor.gridy = 7;
        panel.add(lblAmbientColor, gbc_lblAmbientColor);

        JButton btnSelectDiffuseColor = new JButton("Select diffuse");
        btnSelectDiffuseColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSelectDiffuseColor();
            }
        });
        GridBagConstraints gbc_btnSelectDiffuseColor = new GridBagConstraints();
        gbc_btnSelectDiffuseColor.anchor = GridBagConstraints.NORTH;
        gbc_btnSelectDiffuseColor.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnSelectDiffuseColor.insets = new Insets(0, 0, 5, 0);
        gbc_btnSelectDiffuseColor.gridx = 0;
        gbc_btnSelectDiffuseColor.gridy = 8;
        panel.add(btnSelectDiffuseColor, gbc_btnSelectDiffuseColor);

        lblDiffuseColor = new JLabel("Diffuse color");
        lblDiffuseColor.setHorizontalAlignment(SwingConstants.CENTER);
        lblDiffuseColor.setBorder(new LineBorder(new Color(0, 0, 0)));
        lblDiffuseColor.setOpaque(true);
        GridBagConstraints gbc_lblDiffuseColor = new GridBagConstraints();
        gbc_lblDiffuseColor.fill = GridBagConstraints.BOTH;
        gbc_lblDiffuseColor.gridx = 0;
        gbc_lblDiffuseColor.gridy = 9;
        panel.add(lblDiffuseColor, gbc_lblDiffuseColor);
    }

    protected void onOk() {
        //
    }

    protected void onDefault() {
        //
    }

    protected void onSelectAmbientColor() {
        //
    }

    protected void onSelectDiffuseColor() {
        //
    }

    public JTextField getDirectionText() {
        return directionText;
    }

    public JTextField getAngleText() {
        return angleText;
    }

    public JLabel getLblAmbientColor() {
        return lblAmbientColor;
    }

    public JLabel getLblDiffuseColor() {
        return lblDiffuseColor;
    }
}
