package kendzi.kendzi3d.models.library.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import kendzi.josm.kendzi3d.service.UrlReciverService;

public class LocalModelsDict extends JDialog {

    private UrlReciverService urlReciverService;

    private final JPanel contentPanel = new JPanel();

    protected JList listModels;

//    private DefaultListModel listModel;
    protected AbstractListModel listModel;



    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        try {
            LocalModelsDict dialog = new LocalModelsDict();

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public LocalModelsDict() {
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));


        JPanel panel = new JPanel();
        contentPanel.add(panel, BorderLayout.NORTH);
        {
            JRadioButton rdbtnPluginDir = new JRadioButton("Plugin directory");
            rdbtnPluginDir.setSelected(true);
            panel.add(rdbtnPluginDir);
        }


        listModel = getListModel();

        listModels = new JList();
        listModels.setModel(listModel);


        JScrollPane scrollPane = new JScrollPane();
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        scrollPane.setViewportView(listModels);
//            scrollPane.setRowHeaderView(listModels);


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectValueAndClose()) {
                    dispose();
                }
            }

        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

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

    protected AbstractListModel getListModel() {
        return new DefaultListModel();
    }

    protected boolean selectValueAndClose() {
        return false;
    }
}
