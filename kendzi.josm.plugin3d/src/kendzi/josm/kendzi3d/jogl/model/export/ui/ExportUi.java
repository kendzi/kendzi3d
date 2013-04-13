package kendzi.josm.kendzi3d.jogl.model.export.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ExportUi extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField txtFilePattern;
    private JTextField txtNumOfModels;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            ExportUi dialog = new ExportUi();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public ExportUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{131, 290, 30, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblExport = new JLabel("Export");
            GridBagConstraints gbc_lblExport = new GridBagConstraints();
            gbc_lblExport.anchor = GridBagConstraints.WEST;
            gbc_lblExport.insets = new Insets(0, 0, 5, 5);
            gbc_lblExport.gridx = 0;
            gbc_lblExport.gridy = 0;
            contentPanel.add(lblExport, gbc_lblExport);
        }
        {
            JLabel lblExportType = new JLabel("Type");
            GridBagConstraints gbc_lblExportType = new GridBagConstraints();
            gbc_lblExportType.anchor = GridBagConstraints.WEST;
            gbc_lblExportType.insets = new Insets(0, 0, 5, 5);
            gbc_lblExportType.gridx = 0;
            gbc_lblExportType.gridy = 1;
            contentPanel.add(lblExportType, gbc_lblExportType);
        }
        {
            JLabel lblCollada = new JLabel("Collada");
            GridBagConstraints gbc_lblCollada = new GridBagConstraints();
            gbc_lblCollada.anchor = GridBagConstraints.WEST;
            gbc_lblCollada.insets = new Insets(0, 0, 5, 5);
            gbc_lblCollada.gridx = 1;
            gbc_lblCollada.gridy = 1;
            contentPanel.add(lblCollada, gbc_lblCollada);
        }
        {
            JLabel lblOutputFile = new JLabel("Output file");
            GridBagConstraints gbc_lblOutputFile = new GridBagConstraints();
            gbc_lblOutputFile.anchor = GridBagConstraints.WEST;
            gbc_lblOutputFile.insets = new Insets(0, 0, 5, 5);
            gbc_lblOutputFile.gridx = 0;
            gbc_lblOutputFile.gridy = 2;
            contentPanel.add(lblOutputFile, gbc_lblOutputFile);
        }
        {
            txtFilePattern = new JTextField();
            txtFilePattern.setText("/out/export");
            GridBagConstraints gbc_txtFilePattern = new GridBagConstraints();
            gbc_txtFilePattern.insets = new Insets(0, 0, 5, 5);
            gbc_txtFilePattern.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtFilePattern.gridx = 1;
            gbc_txtFilePattern.gridy = 2;
            contentPanel.add(txtFilePattern, gbc_txtFilePattern);
            txtFilePattern.setColumns(10);
        }
        {
            Button button = new Button("F");
            GridBagConstraints gbc_button = new GridBagConstraints();
            gbc_button.insets = new Insets(0, 0, 5, 0);
            gbc_button.gridx = 2;
            gbc_button.gridy = 2;
            contentPanel.add(button, gbc_button);
        }
        {
            JLabel lblNumberOfModels = new JLabel("Number of models in file");
            GridBagConstraints gbc_lblNumberOfModels = new GridBagConstraints();
            gbc_lblNumberOfModels.anchor = GridBagConstraints.EAST;
            gbc_lblNumberOfModels.insets = new Insets(0, 0, 0, 5);
            gbc_lblNumberOfModels.gridx = 0;
            gbc_lblNumberOfModels.gridy = 3;
            contentPanel.add(lblNumberOfModels, gbc_lblNumberOfModels);
        }
        {
            txtNumOfModels = new JTextField();
            txtNumOfModels.setText("20");
            GridBagConstraints gbc_txtNumOfModels = new GridBagConstraints();
            gbc_txtNumOfModels.insets = new Insets(0, 0, 0, 5);
            gbc_txtNumOfModels.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtNumOfModels.gridx = 1;
            gbc_txtNumOfModels.gridy = 3;
            contentPanel.add(txtNumOfModels, gbc_txtNumOfModels);
            txtNumOfModels.setColumns(10);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("Export");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onExportAction();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    protected void onExportAction() {
        //
    }

    public JTextField getTxtNumOfModels() {
        return txtNumOfModels;
    }

    public JTextField getTxtFilePattern() {
        return txtFilePattern;
    }
}
