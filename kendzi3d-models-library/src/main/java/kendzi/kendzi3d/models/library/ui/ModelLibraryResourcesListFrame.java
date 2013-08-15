package kendzi.kendzi3d.models.library.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import kendzi.kendzi3d.models.library.messages.Messages;

public class ModelLibraryResourcesListFrame extends JFrame {

    private JPanel contentPane;

    protected JTable table;

    protected ModelsFilesTableModel dataModel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ModelLibraryResourcesListFrame frame = new ModelLibraryResourcesListFrame();
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
    public ModelLibraryResourcesListFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 640, 480);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        dataModel = new ModelsFilesTableModel() {};

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        table = new JTable();
        scrollPane.setViewportView(table);
        table.setModel(dataModel);


        JPanel panel_top = new JPanel();
        contentPane.add(panel_top, BorderLayout.NORTH);
        panel_top.setLayout(new BorderLayout(0, 0));

        JLabel lblNodeModelList = new JLabel(Messages.getString("ModelLibraryResourcesListFrame.lblNodeModelList.text")); //$NON-NLS-1$
        panel_top.add(lblNodeModelList, BorderLayout.NORTH);
        lblNodeModelList.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNodeModelList.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel panel_buttons = new JPanel();
        contentPane.add(panel_buttons, BorderLayout.SOUTH);

        JButton btnEdit = new JButton(Messages.getString("ModelLibraryResourcesListFrame.btnEdit.text")); //$NON-NLS-1$
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
            }
        });

        JButton btnView = new JButton(Messages.getString("NodeModelListFrame.btnView.text")); //$NON-NLS-1$
        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewResourceDetails();
            }
        });

        JButton btnViewAll = new JButton(Messages.getString("ModelLibraryResourcesListFrame.btnViewAll.text")); //$NON-NLS-1$
        btnViewAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewFinalLibrary();
            }
        });
        panel_buttons.add(btnViewAll);
        panel_buttons.add(btnView);

        JButton btnAdd = new JButton(Messages.getString("ModelLibraryResourcesListFrame.btnAdd.text")); //$NON-NLS-1$
        btnAdd.setEnabled(false);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
            }
        });
        panel_buttons.add(btnAdd);
        panel_buttons.add(btnEdit);

        JButton btnDelete = new JButton(Messages.getString("ModelLibraryResourcesListFrame.btnDelete.text")); //$NON-NLS-1$
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
            }


        });
        panel_buttons.add(btnDelete);

        JPanel panel_empty = new JPanel();
        panel_buttons.add(panel_empty);

        JPanel panel_empty2 = new JPanel();
        panel_buttons.add(panel_empty2);

        JButton btnOk = new JButton("Ok");
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel_buttons.add(btnOk);
    }

    protected void viewFinalLibrary() {
        //
    }

    protected void viewResourceDetails() {
        //
    }

    public class ModelsFilesTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private List<String> data =
                Collections.unmodifiableList(new ArrayList<String>());

        public void setData(List<String> data) {
            this.data = Collections.unmodifiableList(new ArrayList<String>(data));
            fireTableStructureChanged();
        }

        public String get(int rowIndex) {
            return this.data.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return this.data.size();
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            String pm = this.data.get(rowIndex);

            switch (columnIndex) {
            case 0:
                return pm;

            default:
                return null;
            }
        }

        Class[] columnTypes = new Class[] { String.class };

        @Override
        public Class getColumnClass(int columnIndex) {
            return this.columnTypes[columnIndex];
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * {@inheritDoc}
         *
         * @see javax.swing.table.AbstractTableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
            case 0:
                return "Resource location";

            default:
                return null;
            }
        }
    }

}
