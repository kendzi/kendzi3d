package kendzi.kendzi3d.models.library.ui;

import generated.NodeModel;

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

public class NodeModelListFrame extends JFrame {

    private JPanel contentPane;

    protected JTable table;

    protected ModelsTableModel dataModel;
    private JLabel lblFileKey;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    NodeModelListFrame frame = new NodeModelListFrame();
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
    public NodeModelListFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 640, 480);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        dataModel = new ModelsTableModel() {};

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        table = new JTable();
        scrollPane.setViewportView(table);
        table.setModel(dataModel);


        JPanel panel_1 = new JPanel();
        contentPane.add(panel_1, BorderLayout.NORTH);
        panel_1.setLayout(new BorderLayout(0, 0));

        JLabel lblNodeModelList = new JLabel(Messages.getString("NodeModelListFrame.lblNodeModelList.text")); //$NON-NLS-1$
        panel_1.add(lblNodeModelList, BorderLayout.NORTH);
        lblNodeModelList.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNodeModelList.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblLibrary = new JLabel(Messages.getString("NodeModelListFrame.lblNewLabel.text")); //$NON-NLS-1$
        panel_1.add(lblLibrary, BorderLayout.WEST);

        lblFileKey = new JLabel(Messages.getString("NodeModelListFrame.lblNewLabel.text_1")); //$NON-NLS-1$
        panel_1.add(lblFileKey);
        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.SOUTH);

        JButton btnEdit = new JButton("Edit");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editNodeModel();
            }
        });

        JButton btnView = new JButton(Messages.getString("NodeModelListFrame.btnView.text")); //$NON-NLS-1$
        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewNodeModel();
            }
        });
        panel.add(btnView);

        JButton btnAdd = new JButton("Add");
        btnAdd.setEnabled(false);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNodeModel();
            }
        });
        panel.add(btnAdd);
        panel.add(btnEdit);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeNodeModel();
            }


        });
        panel.add(btnDelete);

        JPanel panel_empty = new JPanel();
        panel.add(panel_empty);

        JPanel panel_empty2 = new JPanel();
        panel.add(panel_empty2);

        JButton btnOk = new JButton("Ok");
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel.add(btnOk);
    }



    protected void viewNodeModel() {
        //
    }

    protected void addNodeModel() {
        //
    }

    protected void editNodeModel() {
        //
    }

    protected void removeNodeModel() {
        //
    }

    public class ModelsTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private List<NodeModel> data =
                Collections.unmodifiableList(new ArrayList<NodeModel>());

        public void setData(List<NodeModel> data) {
            this.data = Collections.unmodifiableList(new ArrayList<NodeModel>(data));
            fireTableStructureChanged();
        }

        public NodeModel get(int rowIndex) {

            NodeModel pm = this.data.get(rowIndex);

            return pm;
        }

        @Override
        public int getRowCount() {
            return this.data.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            NodeModel pm = this.data.get(rowIndex);

            switch (columnIndex) {
            case 0:
                return pm.getMatcher();
            case 1:
                return pm.getModel();
            case 2:
                return pm.getScale();

            default:
                return null;
            }

            //            row[3] = pm.getTranslateX();
            //            row[4] = pm.getTranslateY();
            //            row[5] = pm.getTranslateZ();
        }

        Class[] columnTypes = new Class[] { String.class, String.class, String.class };

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
                return "Matcher";
            case 1:
                return "Model";
            case 2:
                return "Scale";

            default:
                return null;
            }
        }
    }


    public JLabel getLblFileKey() {
        return lblFileKey;
    }
}
