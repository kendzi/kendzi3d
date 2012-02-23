package kendzi.josm.kendzi3d.ui.pointModel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import kendzi.josm.kendzi3d.dto.xsd.PointModel;
import kendzi.josm.kendzi3d.service.impl.PointModelService;

public class PointModelListFrame extends JFrame {

    private PointModelService pointModelService;

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel dataModel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    PointModelListFrame frame = new PointModelListFrame();
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
        dataModel = new DefaultTableModel(new Object[][] { {"Id"}, {"Matcher"}}, new String[] { "Id", "Matcher" }) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            Class[] columnTypes = new Class[] {
                Long.class, String.class
            };
            @Override
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
            /* (non-Javadoc)
             * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }


        };

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



    public void loadTableData() {
        List<PointModel> all = this.pointModelService.findAll();

        dataModel.getDataVector().clear();
        for (PointModel pm : all) {
            Object [] row = new Object[7];
            row[0] = pm.getId();
            row[1] = pm.getMatcher();
            row[2] = pm.getModel();
            row[3] = pm.getScale();
            row[4] = pm.getTranslateX();
            row[5] = pm.getTranslateY();
            row[6] = pm.getTranslateZ();

            dataModel.addRow(row);
        }
//        table.c
    }

    public void addPointModel() {
        PointModelAddFrame frame = new PointModelAddFrame();
        frame.setPointModelService(pointModelService);
        frame.setModal(true);

        frame.setVisible(true);
       // frame.show();

        loadTableData();

//        frame.

    }

    protected void editPointModel() {
        PointModelAddFrame frame = new PointModelAddFrame();
        frame.setPointModelService(pointModelService);
        frame.setModal(true);
//
        Long id = getSelectedId();
        if (id == null) {
            return;
        }

        frame.load(id);
        frame.setVisible(true);


        loadTableData();

    }

    private Long getSelectedId() {
        int selectedRow = this.table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        if (selectedRow >= this.dataModel.getRowCount()) {
            return null;
        }

        return (Long) this.dataModel.getValueAt(selectedRow, 0);
    }



    protected void removePointModel() {
//        dataModel.removeRow(table.getSelectedRow());

        Long id = getSelectedId();
        if (id == null) {
            return;
        }
        pointModelService.remove(id);

        loadTableData();

    }





    /**
     * @param pointModelService the pointModelService to set
     */
    public void setPointModelService(PointModelService pointModelService) {
        this.pointModelService = pointModelService;
    }

}
