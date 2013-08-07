package kendzi.josm.kendzi3d.ui.pointModel;

import generated.PointModel;

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

import kendzi.josm.kendzi3d.ui.pointModel.action.PointModelListFrameAction;

public class PointModelListFrame extends JFrame {

    private JPanel contentPane;

    protected JTable table;

    protected ModelsTableModel dataModel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    PointModelListFrameAction frame = new PointModelListFrameAction();
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
    public PointModelListFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
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

        JLabel lblPointModelList = new JLabel("Point Model List");
        lblPointModelList.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblPointModelList.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblPointModelList, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.SOUTH);

        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPointModel();
            }
        });
        panel.add(btnAdd);

        JButton btnEdit = new JButton("Edit");
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editPointModel();
            }
        });
        panel.add(btnEdit);

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePointModel();
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



    protected void addPointModel() {
        //
    }

    protected void editPointModel() {
        //
    }

    protected void removePointModel() {
        //
    }

    public class ModelsTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private List<PointModel> data =
                Collections.unmodifiableList(new ArrayList<PointModel>());

        public void setData(List<PointModel> data) {
            this.data = Collections.unmodifiableList(new ArrayList<PointModel>(data));
            fireTableStructureChanged();
        }

        public Long getId(int rowIndex) {

            PointModel pm = this.data.get(rowIndex);

            return pm.getId();
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

            PointModel pm = this.data.get(rowIndex);

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
}
