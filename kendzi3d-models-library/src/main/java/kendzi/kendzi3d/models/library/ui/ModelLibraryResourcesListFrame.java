package kendzi.kendzi3d.models.library.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
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
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import kendzi.kendzi3d.models.library.messages.Messages;

public class ModelLibraryResourcesListFrame extends JFrame {

    private final JPanel contentPane;

    protected JTable table;

    protected ModelsFilesTableModel dataModel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ModelLibraryResourcesListFrame frame = new ModelLibraryResourcesListFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public ModelLibraryResourcesListFrame() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 807, 532);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        dataModel = new ModelsFilesTableModel() {
        };

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

        JButton btnAddUrl = new JButton(Messages.getString("ModelLibraryResourcesListFrame.btnEdit.text"));
        btnAddUrl.addActionListener(e -> onAddResourceUrl());

        JButton btnView = new JButton(Messages.getString("NodeModelListFrame.btnView.text")); //$NON-NLS-1$
        btnView.addActionListener(e -> viewResourceDetails());

        JButton btnViewAll = new JButton(Messages.getString("ModelLibraryResourcesListFrame.btnViewAll.text")); //$NON-NLS-1$
        btnViewAll.addActionListener(e -> viewFinalLibrary());
        panel_buttons.add(btnViewAll);
        panel_buttons.add(btnView);

        JButton btnAdd = new JButton(Messages.getString("ModelLibraryResourcesListFrame.btnAdd.text"));
        btnAdd.addActionListener(e -> onAddResourceFile());
        panel_buttons.add(btnAdd);
        panel_buttons.add(btnAddUrl);

        JButton btnDelete = new JButton(Messages.getString("ModelLibraryResourcesListFrame.btnDelete.text"));
        btnDelete.addActionListener(e -> onRemoveResourceFile());
        panel_buttons.add(btnDelete);

        JButton btnDefault = new JButton(Messages.getString("ModelLibraryResourcesListFrame.btnDefault.text")); //$NON-NLS-1$
        btnDefault.addActionListener(e -> onDefaultResources());
        panel_buttons.add(btnDefault);

        JPanel panel_empty = new JPanel();
        panel_buttons.add(panel_empty);

        JPanel panel_empty2 = new JPanel();
        panel_buttons.add(panel_empty2);

        JButton btnOk = new JButton("Ok");
        btnOk.addActionListener(e -> dispose());
        panel_buttons.add(btnOk);
    }

    protected void viewFinalLibrary() {
        //
    }

    protected void viewResourceDetails() {
        //
    }

    protected void onAddResourceFile() {
        //
    }

    protected void onAddResourceUrl() {
        //
    }

    protected void onRemoveResourceFile() {
        //
    }

    protected void onDefaultResources() {
        //
    }

    public static class ModelsFilesTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private List<String> data = Collections.unmodifiableList(new ArrayList<>());

        public void setData(List<String> data) {
            this.data = Collections.unmodifiableList(new ArrayList<>(data));
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
