package kendzi.kendzi3d.models.library.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class NodeModelAddFrame extends JDialog {

    private JPanel contentPane;
    protected JTextField txtId;
    protected JTextField txtModel;
    protected JTextField txtFilter;
    protected JTextField txtScale;

    protected JTextField txtTranslatex;
    protected JTextField txtTranslatey;
    protected JTextField txtTranslatez;
    protected JTextField txtMatcher;
    private JButton btnSave;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    NodeModelAddFrame frame = new NodeModelAddFrame();
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
    public NodeModelAddFrame() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 337);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panelTitle = new JPanel();
        contentPane.add(panelTitle, BorderLayout.NORTH);

        JLabel lblModel_1 = new JLabel("Model configuration");
        lblModel_1.setFont(new Font("Tahoma", Font.PLAIN, 17));
        panelTitle.add(lblModel_1);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.CENTER);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 68, 133, 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        JLabel label = new JLabel("Id");
        label.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 5, 5);
        gbc_label.anchor = GridBagConstraints.WEST;
        gbc_label.gridx = 0;
        gbc_label.gridy = 0;
        panel.add(label, gbc_label);

        txtId = new JTextField();
        txtId.setEditable(false);
        GridBagConstraints gbc_txtId = new GridBagConstraints();
        gbc_txtId.gridwidth = 3;
        gbc_txtId.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtId.insets = new Insets(0, 0, 5, 0);
        gbc_txtId.gridx = 1;
        gbc_txtId.gridy = 0;
        panel.add(txtId, gbc_txtId);
        txtId.setColumns(10);

        JLabel lblMatcher = new JLabel("Matcher");
        lblMatcher.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblMatcher = new GridBagConstraints();
        gbc_lblMatcher.anchor = GridBagConstraints.WEST;
        gbc_lblMatcher.insets = new Insets(0, 0, 5, 5);
        gbc_lblMatcher.gridx = 0;
        gbc_lblMatcher.gridy = 1;
        panel.add(lblMatcher, gbc_lblMatcher);

        txtMatcher = new JTextField();
        txtMatcher.setText("(key=value)");
        GridBagConstraints gbc_txtMatcher = new GridBagConstraints();
        gbc_txtMatcher.gridwidth = 3;
        gbc_txtMatcher.insets = new Insets(0, 0, 5, 5);
        gbc_txtMatcher.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtMatcher.gridx = 1;
        gbc_txtMatcher.gridy = 1;
        panel.add(txtMatcher, gbc_txtMatcher);
        txtMatcher.setColumns(10);

        JLabel lblModel = new JLabel("Model");
        lblModel.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblModel = new GridBagConstraints();
        gbc_lblModel.anchor = GridBagConstraints.WEST;
        gbc_lblModel.insets = new Insets(0, 0, 5, 5);
        gbc_lblModel.gridx = 0;
        gbc_lblModel.gridy = 2;
        panel.add(lblModel, gbc_lblModel);

        txtModel = new JTextField();
        GridBagConstraints gbc_txtModel = new GridBagConstraints();
        gbc_txtModel.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtModel.insets = new Insets(0, 0, 5, 5);
        gbc_txtModel.gridx = 1;
        gbc_txtModel.gridy = 2;
        panel.add(txtModel, gbc_txtModel);
        txtModel.setText("/models/test.obj");
        txtModel.setColumns(10);

        JButton btnDict = new JButton("Dict");
        btnDict.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dictAction();
            }
        });
        GridBagConstraints gbc_btnDict = new GridBagConstraints();
        gbc_btnDict.insets = new Insets(0, 0, 5, 5);
        gbc_btnDict.gridx = 2;
        gbc_btnDict.gridy = 2;
        panel.add(btnDict, gbc_btnDict);

        JButton btnImport = new JButton("Import");
        btnImport.setEnabled(false);
        GridBagConstraints gbc_btnImport = new GridBagConstraints();
        gbc_btnImport.insets = new Insets(0, 0, 5, 0);
        gbc_btnImport.gridx = 3;
        gbc_btnImport.gridy = 2;
        panel.add(btnImport, gbc_btnImport);

        JLabel lblFilter = new JLabel("Filter");
        lblFilter.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblFilter = new GridBagConstraints();
        gbc_lblFilter.anchor = GridBagConstraints.WEST;
        gbc_lblFilter.insets = new Insets(0, 0, 5, 5);
        gbc_lblFilter.gridx = 0;
        gbc_lblFilter.gridy = 3;
        panel.add(lblFilter, gbc_lblFilter);

        txtFilter = new JTextField();
        txtFilter.setText("Filter");
        GridBagConstraints gbc_txtFilter = new GridBagConstraints();
        gbc_txtFilter.gridwidth = 3;
        gbc_txtFilter.insets = new Insets(0, 0, 5, 0);
        gbc_txtFilter.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtFilter.gridx = 1;
        gbc_txtFilter.gridy = 3;
        panel.add(txtFilter, gbc_txtFilter);
        txtFilter.setColumns(10);

        JLabel lblScale = new JLabel("Scale");
        lblScale.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblScale = new GridBagConstraints();
        gbc_lblScale.anchor = GridBagConstraints.WEST;
        gbc_lblScale.insets = new Insets(0, 0, 5, 5);
        gbc_lblScale.gridx = 0;
        gbc_lblScale.gridy = 4;
        panel.add(lblScale, gbc_lblScale);

        txtScale = new JTextField();
        txtScale.setText("normalHeight(1,1)");
        GridBagConstraints gbc_txtScale = new GridBagConstraints();
        gbc_txtScale.gridwidth = 3;
        gbc_txtScale.insets = new Insets(0, 0, 5, 0);
        gbc_txtScale.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtScale.gridx = 1;
        gbc_txtScale.gridy = 4;
        panel.add(txtScale, gbc_txtScale);
        txtScale.setColumns(10);

        JLabel lblTranslatex = new JLabel("TransformX");
        lblTranslatex.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblTranslatex = new GridBagConstraints();
        gbc_lblTranslatex.anchor = GridBagConstraints.WEST;
        gbc_lblTranslatex.insets = new Insets(0, 0, 5, 5);
        gbc_lblTranslatex.gridx = 0;
        gbc_lblTranslatex.gridy = 5;
        panel.add(lblTranslatex, gbc_lblTranslatex);

        txtTranslatex = new JTextField();
        txtTranslatex.setText("0");
        GridBagConstraints gbc_txtTranslatex = new GridBagConstraints();
        gbc_txtTranslatex.gridwidth = 3;
        gbc_txtTranslatex.insets = new Insets(0, 0, 5, 0);
        gbc_txtTranslatex.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtTranslatex.gridx = 1;
        gbc_txtTranslatex.gridy = 5;
        panel.add(txtTranslatex, gbc_txtTranslatex);
        txtTranslatex.setColumns(10);

        JLabel lblTranslatey = new JLabel("TransformY");
        lblTranslatey.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblTranslatey = new GridBagConstraints();
        gbc_lblTranslatey.anchor = GridBagConstraints.WEST;
        gbc_lblTranslatey.insets = new Insets(0, 0, 5, 5);
        gbc_lblTranslatey.gridx = 0;
        gbc_lblTranslatey.gridy = 6;
        panel.add(lblTranslatey, gbc_lblTranslatey);

        txtTranslatey = new JTextField();
        txtTranslatey.setText("0");
        GridBagConstraints gbc_txtTranslatey = new GridBagConstraints();
        gbc_txtTranslatey.gridwidth = 3;
        gbc_txtTranslatey.insets = new Insets(0, 0, 5, 0);
        gbc_txtTranslatey.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtTranslatey.gridx = 1;
        gbc_txtTranslatey.gridy = 6;
        panel.add(txtTranslatey, gbc_txtTranslatey);
        txtTranslatey.setColumns(10);

        JLabel lblTranslatez = new JLabel("TransformZ");
        lblTranslatez.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblTranslatez = new GridBagConstraints();
        gbc_lblTranslatez.anchor = GridBagConstraints.WEST;
        gbc_lblTranslatez.insets = new Insets(0, 0, 5, 5);
        gbc_lblTranslatez.gridx = 0;
        gbc_lblTranslatez.gridy = 7;
        panel.add(lblTranslatez, gbc_lblTranslatez);

        txtTranslatez = new JTextField();
        txtTranslatez.setText("0");
        GridBagConstraints gbc_txtTranslatez = new GridBagConstraints();
        gbc_txtTranslatez.insets = new Insets(0, 0, 5, 0);
        gbc_txtTranslatez.gridwidth = 3;
        gbc_txtTranslatez.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtTranslatez.gridx = 1;
        gbc_txtTranslatez.gridy = 7;
        panel.add(txtTranslatez, gbc_txtTranslatez);
        txtTranslatez.setColumns(10);

        JPanel panelButtons = new JPanel();
        contentPane.add(panelButtons, BorderLayout.SOUTH);

        btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAction();
            }
        });
        panelButtons.add(btnSave);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panelButtons.add(btnCancel);
    }

    protected void dictAction() {
        //
    }

    protected void saveAction() {
        //
    }

    public JButton getBtnSave() {
        return btnSave;
    }
}
